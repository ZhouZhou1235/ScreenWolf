# 桌宠项目打包程序

import glob
import os
import shutil
import subprocess
import sys


workroot = os.getcwd().replace("\\", "/")
paths = {
    'jdk': f"{workroot}/bin/jdk",
    'mvn': f"{workroot}/bin/maven-bin/bin/mvn.cmd",
    'maven_repo': f"{workroot}/bin/maven-bin/repo",
    'game': f"{workroot}/bin/ScreenWolf.jar",
    'output_dir': f"{workroot}/output/",
}

# 执行命令
def java_exec(cmd:str):
    env = os.environ.copy()
    env["JAVA_HOME"] = os.path.normpath(paths['jdk'])
    java_bin_path = f"{paths['jdk']}/bin"
    if 'PATH' in env:
        env["PATH"] = f"{java_bin_path};{env['PATH']}"
    else:
        env["PATH"] = java_bin_path
    return subprocess.call(cmd, shell=True, env=env)==0

# 清理Maven本地仓库
def clean_mvn_repo():
    screenwolf_pattern = f"{paths['maven_repo']}/*"
    screenwolf_dirs = glob.glob(screenwolf_pattern)
    for dir_path in screenwolf_dirs:
        try: shutil.rmtree(dir_path)
        except Exception as e: print(f"{dir_path}: {e}")

# 安装游戏主程序jar包
def install_game():
    cmd = (
        f'"{paths["mvn"]}" install:install-file '
        f'-Dfile="{paths["game"]}" '
        '-DgroupId=com.pinkcandy '
        '-DartifactId=screenwolf '
        '-Dpackaging=jar '
        '-DgeneratePom=true '
        '-DcreateChecksum=true '
        f'-DlocalRepositoryPath={paths["maven_repo"]}'
    )
    return java_exec(cmd)

# 打包
def mvn_package(projectPath:str):
    projectPath = projectPath.replace("\\", "/")
    pom = f"{projectPath}/pom.xml"
    target_dir = f"{projectPath}/target/"
    clean_cmd = f'"{paths['mvn']}" -f "{pom}" clean'
    package_cmd = (
        f'"{paths['mvn']}" -f "{pom}" package '
        f'-Dmaven.compiler.fork=true '
        f'-Dmaven.compiler.executable="{os.path.normpath(paths['jdk'])}/bin/javac" '
        f'-Dfile.encoding=UTF-8 '
        f'-U -o'
    )
    java_exec(clean_cmd)
    ok = java_exec(package_cmd)
    if ok: copy_to_output(target_dir)
    return ok

# 复制到输出目录
def copy_to_output(target_dir:str):
    target_dir = target_dir.replace("\\", "/")
    for f in os.listdir(target_dir):
        if f.endswith(".jar"):
            src = f"{target_dir}/{f}"
            dst = f"{paths['output_dir']}/{f}"
            shutil.copy(src,dst)

# 打开文件夹
def open_directory(path:str):
    path = os.path.normpath(path)
    if os.path.exists(path):
        os.startfile(path)

# 运行
def main():
    install_game()
    print("===桌宠打包程序 PetPackager===")
    print("输入选项数字后回车执行 input option number then enter")
    while True:
        print("1 扫描宠物项目文件夹打包 scan pet project dir to build")
        print("2 清空maven本地仓库后重载 clean maven repo then reload")
        print("3 打开输出文件夹 open output directory")
        print("0 退出 exit")
        try:
            option = int(input('选项 option:'))
            if option==1:
                projectPath = input('宠物项目路径 pet project path:')
                if mvn_package(projectPath): open_directory(paths['output_dir'])
            elif option==2:
                clean_mvn_repo()
                install_game()
            elif option==3:
                open_directory(paths['output_dir'])
            elif option==0: sys.exit()
        except Exception as e: print(e)

if __name__=='__main__':
    main()
