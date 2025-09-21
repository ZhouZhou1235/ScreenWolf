# 桌宠项目打包程序
import glob
import os
import shutil
import subprocess
import sys
import tkinter as tk
from tkinter import filedialog,messagebox,scrolledtext


class PetPackagerGUI:
    def __init__(self,root):
        self.root=root
        self.root.title("桌宠打包程序 PetPackager")
        self.root.geometry("800x600")
        self.workroot=os.getcwd().replace("\\","/")
        self.paths={
            'jdk':f"{self.workroot}/bin/jdk",
            'mvn':f"{self.workroot}/bin/maven-bin/bin/mvn.cmd",
            'maven_repo':f"{self.workroot}/bin/maven-bin/repo",
            'game':f"{self.workroot}/bin/ScreenWolf.jar",
            'output_dir':f"{self.workroot}/output/",
        }
        self.create_widgets()
        self.install_game()
    # 窗口
    def create_widgets(self):
        main_frame=tk.Frame(self.root)
        main_frame.pack(fill=tk.BOTH,expand=True)
        title_label=tk.Label(main_frame,text="桌宠打包程序 PetPackager",font=("SimHei",16,"bold"))
        title_label.pack(pady=10)
        path_frame=tk.Frame(main_frame)
        path_frame.pack(pady=5)
        tk.Label(path_frame,text="宠物项目路径 pet project path:").pack(side=tk.LEFT)
        self.project_path_var=tk.StringVar()
        tk.Entry(path_frame,textvariable=self.project_path_var,width=50).pack(side=tk.LEFT,padx=5)
        tk.Button(path_frame,text="选择 select",command=self.browse_project_path).pack(side=tk.LEFT)
        button_frame=tk.Frame(main_frame)
        button_frame.pack(pady=10)
        tk.Button(button_frame,text="打包项目 build project",command=self.package_project,width=25).pack(side=tk.LEFT,padx=5)
        tk.Button(button_frame,text="重载仓库 reload maven repo",command=self.clean_maven_repo,width=25).pack(side=tk.LEFT,padx=5)
        tk.Button(button_frame,text="打开输出 open output",command=self.open_output_dir,width=25).pack(side=tk.LEFT,padx=5)
        tk.Label(main_frame,text="日志 log:").pack(pady=(10,5))
        self.log_area=scrolledtext.ScrolledText(main_frame,height=20)
        self.log_area.pack(fill=tk.BOTH,expand=True)
        self.redirect_stdout()
    # 重定向控制台输出
    def redirect_stdout(self):
        class StdoutRedirector:
            def __init__(self,text_widget):
                self.text_widget=text_widget
            def write(self,string):
                self.text_widget.insert(tk.END,string)
                self.text_widget.see(tk.END)
            def flush(self):
                pass
        sys.stdout=StdoutRedirector(self.log_area)
    # 文件夹浏览选择
    def browse_project_path(self):
        path=filedialog.askdirectory(title="选择宠物项目文件夹 select pet project directory")
        if path:
            self.project_path_var.set(path)
    # 打印日志
    def log_message(self,message):
        print(message)
        self.root.update()
    # 执行命令
    def java_exec(self,cmd:str):
        env=os.environ.copy()
        env["JAVA_HOME"]=os.path.normpath(self.paths['jdk'])
        java_bin_path=f"{self.paths['jdk']}/bin"
        if 'PATH' in env:
            env["PATH"]=f"{java_bin_path};{env['PATH']}"
        else:
            env["PATH"]=java_bin_path
        return subprocess.call(cmd,shell=True,env=env)==0
    # 清理Maven本地仓库
    def clean_maven_repo(self):
        self.log_message("清理本地仓库 clean maven local repo")
        screenwolf_pattern=f"{self.paths['maven_repo']}/*"
        screenwolf_dirs=glob.glob(screenwolf_pattern)
        for dir_path in screenwolf_dirs:
            try:
                shutil.rmtree(dir_path)
                self.log_message(f"已删除 have deleted: {dir_path}")
            except Exception as e:
                self.log_message(f"删除失败 delete failed {dir_path}: {e}")
        self.log_message("安装游戏库 install game jar")
        self.install_game()
        messagebox.showinfo("完成 done","仓库已清理并重新安装游戏 task finish")
    # 安装游戏主程序jar包
    def install_game(self):
        install_cmd=(
            f'"{self.paths["mvn"]}" install:install-file '
            f'-Dmaven.compiler.fork=true '
            f'-Dmaven.compiler.executable="{os.path.normpath(self.paths['jdk'])}/bin/javac" '
            f'-Dfile="{self.paths["game"]}" '
            '-DgroupId=com.pinkcandy '
            '-DartifactId=screenwolf '
            '-Dpackaging=jar '
            '-DgeneratePom=true '
            '-DcreateChecksum=true '
            f'-DlocalRepositoryPath={self.paths["maven_repo"]}'
        )
        success=self.java_exec(install_cmd)
        return success
    # 打包
    def mvn_package(self,projectPath:str):
        projectPath=projectPath.replace("\\","/")
        pom=f"{projectPath}/pom.xml"
        target_dir=f"{projectPath}/target/"
        if not os.path.exists(pom):
            messagebox.showerror("错误 error",f"未找到pom.xml文件 pom.xml not found: {projectPath}")
            return False
        self.log_message(f"开始打包 start package: {projectPath}")
        clean_cmd=f'"{self.paths['mvn']}" -f "{pom}" clean'
        package_cmd=(
            f'"{self.paths['mvn']}" -f "{pom}" package '
            f'-Dmaven.compiler.fork=true '
            f'-Dmaven.compiler.executable="{os.path.normpath(self.paths['jdk'])}/bin/javac" '
            f'-Dfile.encoding=UTF-8 '
            f'-Dmaven.repo.local={self.paths["maven_repo"]} '
        )
        self.java_exec(clean_cmd)
        ok=self.java_exec(package_cmd)
        if ok:
            self.copy_to_output(target_dir)
            self.log_message("打包成功 build success")
        else:
            self.log_message("打包失败 build failed")
        return ok
    # 复制到输出目录
    def copy_to_output(self,target_dir:str):
        target_dir=target_dir.replace("\\","/")
        if not os.path.exists(self.paths['output_dir']):
            os.makedirs(self.paths['output_dir'])
        for f in os.listdir(target_dir):
            if f.endswith(".jar"):
                src=f"{target_dir}/{f}"
                dst=f"{self.paths['output_dir']}/{f}"
                shutil.copy(src,dst)
                self.log_message(f"复制 copy: {f}")
    # 打开文件夹
    def open_directory(self,path:str):
        path=os.path.normpath(path)
        if os.path.exists(path):
            os.startfile(path)
        else:
            messagebox.showwarning("警告 warning",f"路径不存在 path not found: {path}")
    def package_project(self):
        project_path=self.project_path_var.get().strip()
        if not project_path:
            messagebox.showwarning("警告 warning","先选择项目路径 need select project path")
            return
        if self.mvn_package(project_path):
            messagebox.showinfo("成功 success","项目打包完成 task finish")
    def open_output_dir(self):
        self.open_directory(self.paths['output_dir'])

if __name__=='__main__':
    root=tk.Tk()
    app=PetPackagerGUI(root)
    root.mainloop()
