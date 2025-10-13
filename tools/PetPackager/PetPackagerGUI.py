# 桌宠项目打包程序

import os
import shutil
import subprocess
import tkinter as tk
from tkinter import filedialog, messagebox, ttk
import threading


jdk_exe = "./jdk/bin/javac.exe"
mvn_exe = "./maven/bin/mvn.cmd"
repo_dir = os.path.abspath("./repo")
output_dir = os.path.abspath("./output")

class PetPackagerGUI:
    def __init__(self, root):
        self.root = root
        self.root.title("ScreenWolf Pet Packager")
        self.root.geometry("600x400")
        self.setup_ui()
        
    def setup_ui(self):
        main_frame = ttk.Frame(self.root, padding="10")
        main_frame.grid(row=0, column=0, sticky=(tk.W, tk.E, tk.N, tk.S)) # type: ignore
        
        title_label = ttk.Label(main_frame, text="屏幕有狼桌宠项目打包程序", font=("SimHei",16,"bold"))
        title_label.grid(row=0, column=0, columnspan=3, pady=(0, 20))
        
        ttk.Label(main_frame, text="项目路径 project path:").grid(row=1, column=0, sticky=tk.W, pady=5)
        self.project_path_var = tk.StringVar()
        self.path_entry = ttk.Entry(main_frame, textvariable=self.project_path_var, width=50)
        self.path_entry.grid(row=1, column=1, sticky=(tk.W, tk.E), pady=5, padx=(5, 0)) # type: ignore
        
        browse_btn = ttk.Button(main_frame, text="浏览 view", command=self.browse_project)
        browse_btn.grid(row=1, column=2, pady=5, padx=(5, 0))
        
        self.progress = ttk.Progressbar(main_frame, mode='indeterminate')
        self.progress.grid(row=2, column=0, columnspan=3, sticky=(tk.W, tk.E), pady=10) # type: ignore
        
        ttk.Label(main_frame, text="打包日志 logs:").grid(row=3, column=0, sticky=tk.W, pady=(10, 0))
        self.log_text = tk.Text(main_frame, height=15, width=70)
        self.log_text.grid(row=4, column=0, columnspan=3, sticky=(tk.W, tk.E, tk.N, tk.S), pady=5) # type: ignore
        
        scrollbar = ttk.Scrollbar(main_frame, orient="vertical", command=self.log_text.yview)
        scrollbar.grid(row=4, column=3, sticky=(tk.N, tk.S), pady=5) # type: ignore
        self.log_text.configure(yscrollcommand=scrollbar.set)
        
        button_frame = ttk.Frame(main_frame)
        button_frame.grid(row=5, column=0, columnspan=3, pady=10)
        
        self.package_btn = ttk.Button(button_frame, text="开始打包 package", command=self.start_package)
        self.package_btn.pack(side=tk.LEFT, padx=5)
        
        self.open_output_btn = ttk.Button(button_frame, text="打开输出目录 open output", command=self.open_output_dir, state=tk.ACTIVE)
        self.open_output_btn.pack(side=tk.LEFT, padx=5)
        
        main_frame.columnconfigure(1, weight=1)
        main_frame.rowconfigure(4, weight=1)
        self.root.columnconfigure(0, weight=1)
        self.root.rowconfigure(0, weight=1)
    
    def browse_project(self):
        path = filedialog.askdirectory(title="选择桌宠项目目录 select pet project directory")
        if path:
            self.project_path_var.set(path)
    
    def log_message(self, message):
        self.log_text.insert(tk.END, message + "\n")
        self.log_text.see(tk.END)
        self.root.update_idletasks()
    
    def java_exec(self, cmd):
        env = os.environ.copy()
        java_home = os.path.dirname(os.path.dirname(os.path.abspath(jdk_exe)))
        env["JAVA_HOME"] = java_home
        java_bin_path = os.path.join(java_home, "bin")
        env["PATH"] = f"{java_bin_path};{env.get('PATH', '')}"
        return subprocess.call(cmd, shell=True, env=env) == 0
    
    def get_project_version(self, project_path):
        pom_file = os.path.join(project_path, "pom.xml")
        if not os.path.exists(pom_file):
            return "1.0.0"
        
        try:
            import xml.etree.ElementTree as ET
            tree = ET.parse(pom_file)
            root = tree.getroot()
            ns = {'maven': 'http://maven.apache.org/POM/4.0.0'}
            dependencies = root.find('.//maven:dependencies', ns)
            if dependencies is not None:
                for dependency in dependencies.findall('maven:dependency', ns):
                    group_id = dependency.find('maven:groupId', ns)
                    artifact_id = dependency.find('maven:artifactId', ns)
                    version = dependency.find('maven:version', ns)
                    if (group_id is not None and group_id.text == 'com.pinkcandy' and
                        artifact_id is not None and artifact_id.text == 'screenwolf' and
                        version is not None):
                        return version.text
        except Exception:
            pass
        return "1.0.0"
    
    def find_game_jar_by_version(self, version):
        lib_dir = "./lib"
        expected_name = f"screenwolf-{version}.jar"
        expected_path = os.path.join(lib_dir, expected_name)
        if os.path.exists(expected_path):
            return expected_path
        if os.path.exists(lib_dir):
            for file in os.listdir(lib_dir):
                if file.endswith(".jar"):
                    if f"-{version}.jar" in file:
                        return os.path.join(lib_dir, file)
                    if file.startswith("screenwolf-") and file.endswith(".jar"):
                        base_name = file[:-4]
                        parts = base_name.split("-")
                        if len(parts) >= 2 and parts[0] == "screenwolf":
                            file_version = parts[1]
                            if file_version == version:
                                return os.path.join(lib_dir, file)
        default_jar = os.path.join(lib_dir, "ScreenWolf.jar")
        if os.path.exists(default_jar):
            return default_jar
        if os.path.exists(lib_dir):
            for file in os.listdir(lib_dir):
                if file.endswith(".jar"):
                    return os.path.join(lib_dir, file)
        return None

    def install_game_lib_with_version(self, version):
        game_jar = self.find_game_jar_by_version(version)
        if game_jar is None or not os.path.exists(game_jar):
            self.log_message(f"错误：找不到版本 {version} 对应的游戏 JAR 文件")
            self.log_message(f"在 ./lib 目录中查找可用的 JAR 文件...")
            lib_dir = "./lib"
            if os.path.exists(lib_dir):
                jar_files = [f for f in os.listdir(lib_dir) if f.endswith(".jar")]
                if jar_files:
                    self.log_message(f"找到以下 JAR 文件: {', '.join(jar_files)}")
                else:
                    self.log_message("未找到任何 JAR 文件")
            return False
        self.log_message(f"使用游戏库: {os.path.basename(game_jar)}")
        if not os.path.exists(repo_dir):
            os.makedirs(repo_dir)
        install_cmd = (
            f'"{mvn_exe}" install:install-file '
            f'-Dfile="{game_jar}" '
            '-DgroupId=com.pinkcandy '
            '-DartifactId=screenwolf '
            f'-Dversion={version} '
            '-Dpackaging=jar '
            '-DgeneratePom=true '
            f'-DlocalRepositoryPath={repo_dir}'
        )
        return self.java_exec(install_cmd)
    
    def package_pet_project(self):
        project_path = self.project_path_var.get()
        project_path = os.path.abspath(project_path)
        pom_file = os.path.join(project_path, "pom.xml")
        
        if not os.path.exists(pom_file):
            self.log_message(f"错误：未找到 pom.xml 文件在 {project_path}")
            return False
        
        required_version = self.get_project_version(project_path)
        self.log_message(f"检测到项目需要的 screenwolf 版本: {required_version}")
        
        self.log_message("正在安装游戏库到本地仓库...")
        if not self.install_game_lib_with_version(required_version):
            self.log_message("游戏库安装失败，尝试使用默认版本...")
            if not self.install_game_lib_with_version("1.0.0"):
                return False
        
        self.log_message("正在执行 Maven 打包...")
        package_cmd = (
            f'"{mvn_exe}" -f "{pom_file}" clean package '
            f'-Dmaven.repo.local={repo_dir} '
            '-Dmaven.test.skip=true'
        )
        
        if self.java_exec(package_cmd):
            target_dir = os.path.join(project_path, "target")
            if not os.path.exists(output_dir):
                os.makedirs(output_dir)
                
            jar_count = 0
            for file in os.listdir(target_dir):
                if file.endswith(".jar") and not file.endswith("-sources.jar") and not file.endswith("-javadoc.jar"):
                    src_file = os.path.join(target_dir, file)
                    dst_file = os.path.join(output_dir, file)
                    shutil.copy2(src_file, dst_file)
                    jar_count += 1
            
            self.log_message(f"打包完成！共生成 {jar_count} 个 JAR 文件")
            return True
        else:
            self.log_message("项目打包失败")
            return False
    
    def start_package(self):
        if not self.project_path_var.get():
            messagebox.showerror("错误", "请选择项目路径")
            return
        
        if not os.path.exists(jdk_exe):
            messagebox.showerror("错误", f"JDK 不存在于 {jdk_exe}")
            return
        
        if not os.path.exists(mvn_exe):
            messagebox.showerror("错误", f"Maven 不存在于 {mvn_exe}")
            return
        
        self.package_btn.config(state=tk.DISABLED)
        self.open_output_btn.config(state=tk.DISABLED)
        self.progress.start()
        self.log_text.delete(1.0, tk.END)
        
        thread = threading.Thread(target=self.package_thread)
        thread.daemon = True
        thread.start()
    
    def package_thread(self):
        try:
            success = self.package_pet_project()
            self.root.after(0, self.package_complete, success)
        except Exception as e:
            self.root.after(0, self.package_error, str(e))
    
    def package_complete(self, success):
        self.progress.stop()
        self.package_btn.config(state=tk.NORMAL)
        if success:
            self.open_output_btn.config(state=tk.NORMAL)
            messagebox.showinfo("完成 SUCCESS", "项目打包成功")
        else:
            messagebox.showerror("错误 ERROR", "项目打包失败")
    
    def package_error(self, error_msg):
        self.progress.stop()
        self.package_btn.config(state=tk.NORMAL)
        self.log_message(f"打包过程中发生错误: {error_msg}")
        messagebox.showerror("错误", f"打包过程中发生错误:\n{error_msg}")
    
    def open_output_dir(self):
        if os.path.exists(output_dir):
            os.startfile(output_dir)
        else:
            messagebox.showwarning("警告", "输出目录不存在")

def main():
    root = tk.Tk()
    PetPackagerGUI(root)
    root.mainloop()


if __name__ == '__main__':
    main()
