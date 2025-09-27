# 桌宠项目打包程序 命令行

import os
import shutil
import subprocess
import sys


# 配置路径
jdk_exe = "./jdk/bin/javac.exe"
mvn_exe = "./maven/bin/mvn.cmd"
repo_dir = "./repo"
output_dir = "./output"
game_jar = "./lib/ScreenWolf.jar"

def log_message(message):
    """打印日志信息"""
    print(message)

def java_exec(cmd):
    """使用指定的 JDK 环境执行命令"""
    env = os.environ.copy()
    java_home = os.path.dirname(os.path.dirname(os.path.abspath(jdk_exe)))
    env["JAVA_HOME"] = java_home
    java_bin_path = os.path.join(java_home, "bin")
    env["PATH"] = f"{java_bin_path};{env.get('PATH', '')}"
    return subprocess.call(cmd, shell=True, env=env) == 0

def install_game_lib():
    """将游戏主 JAR 安装到 Maven 本地仓库"""
    if not os.path.exists(game_jar):
        log_message(f"错误：游戏主 JAR 不存在 {game_jar}")
        return False
    
    if not os.path.exists(repo_dir):
        os.makedirs(repo_dir)
    
    install_cmd = (
        f'"{mvn_exe}" install:install-file '
        f'-Dfile="{game_jar}" '
        '-DgroupId=com.pinkcandy '
        '-DartifactId=screenwolf '
        '-Dversion=1.0.0 '
        '-Dpackaging=jar '
        '-DgeneratePom=true '
        f'-DlocalRepositoryPath={repo_dir}'
    )
    return java_exec(install_cmd)

def get_project_version(project_path):
    """从项目的 pom.xml 中读取 screenwolf 依赖的版本号"""
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

def install_game_lib_with_version(version):
    """使用指定版本号将游戏主 JAR 安装到 Maven 本地仓库"""
    if not os.path.exists(game_jar):
        log_message(f"错误：游戏主 JAR 不存在 {game_jar}")
        return False
    
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
    return java_exec(install_cmd)

def package_pet_project(project_path):
    """打包指定的桌宠项目"""
    project_path = os.path.abspath(project_path)
    pom_file = os.path.join(project_path, "pom.xml")
    
    if not os.path.exists(pom_file):
        log_message(f"错误：未找到 pom.xml 文件在 {project_path}")
        return False
    
    required_version = get_project_version(project_path)
    
    if not install_game_lib_with_version(required_version):
        if not install_game_lib_with_version("1.0.0"):
            return False
    
    package_cmd = (
        f'"{mvn_exe}" -f "{pom_file}" clean package '
        f'-Dmaven.repo.local={repo_dir} '
        '-Dmaven.test.skip=true'
    )
    
    if java_exec(package_cmd):
        target_dir = os.path.join(project_path, "target")
        if not os.path.exists(output_dir):
            os.makedirs(output_dir)
            
        for file in os.listdir(target_dir):
            if file.endswith(".jar") and not file.endswith("-sources.jar") and not file.endswith("-javadoc.jar"):
                src_file = os.path.join(target_dir, file)
                dst_file = os.path.join(output_dir, file)
                shutil.copy2(src_file, dst_file)
        
        return True
    else:
        return False

def main():
    """主函数"""
    print("屏幕有狼桌宠项目打包程序 ScreenWolf Pet Packager")
    if not os.path.exists(jdk_exe):
        log_message(f"错误：JDK 不存在于 {jdk_exe}")
        return
    
    if not os.path.exists(mvn_exe):
        log_message(f"错误：Maven 不存在于 {mvn_exe}")
        return
    
    if len(sys.argv) > 1:
        project_path = sys.argv[1]
    else:
        project_path = input("桌宠项目路径 pet project path: ").strip()
    
    if not project_path:
        log_message("未提供项目路径")
        return
    
    if not os.path.exists(project_path):
        log_message(f"错误：项目路径不存在 {project_path}")
        return
    
    package_pet_project(project_path)


if __name__ == '__main__':
    main()
