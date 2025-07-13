# 桌宠Maven项目打包程序

import os
import shutil
import subprocess

class MavenPetPackager:
    def __init__(self):
        self.root = os.getcwd().replace("\\","/")
        self.mvn = f"{self.root}/maven-bin/bin/mvn.cmd"
        self.lib = f"{self.root}/lib/ScreenWolf.jar"
        self.projects_dir = f"{self.root}/projects/"
        self.output_dir = f"{self.root}/build/"
    def _exec(self, cmd):
        env = os.environ.copy()
        java_home = os.path.normpath(f"{self.root}/jdk")
        env["JAVA_HOME"] = java_home
        env["PATH"] = f"{java_home}/bin;{env['PATH']}"
        return subprocess.call(cmd, shell=True, env=env)==0
    def _install_core(self):
        cmd = (
            f'"{self.mvn}" install:install-file '
            f'-Dfile="{self.lib}" '
            '-DgroupId=com.pinkcandy '
            '-DartifactId=screenwolf '
            '-Dversion=1.0.0 '
            '-Dpackaging=jar'
        )
        return self._exec(cmd)
    def _build(self, project):
        pom = f"{self.projects_dir}{project}/pom.xml"
        if not os.path.exists(pom): return False
        compile_cmd = (
            f'"{self.mvn}" -f "{pom}" clean package '
            f'-Dmaven.compiler.fork=true '
            f'-Dmaven.compiler.executable="{os.path.normpath(self.root)}/jdk/bin/javac"'
        )
        if not self._exec(compile_cmd): return False
        for f in os.listdir(f"{self.projects_dir}{project}/target/"):
            if f.endswith(".jar") and not f.endswith("-sources.jar"):
                src = f"{self.projects_dir}{project}/target/{f}"
                dst = f"{self.output_dir}{f}"
                shutil.move(src, dst)
                return True
        return False
    def run(self):
        if not os.path.exists(f"{self.root}/jdk/bin/javac.exe"): return False
        if not self._install_core(): return False
        os.makedirs(self.output_dir, exist_ok=True)
        success = True
        for p in os.listdir(self.projects_dir):
            project_path = f"{self.projects_dir}{p}"
            if os.path.isdir(project_path):
                if not self._build(p):
                    success = False
        return success

if __name__ == "__main__":
    if MavenPetPackager().run(): print("PINKCANDY: 成功")
    else: print("PINKCANDY: 失败")
    input("按回车关闭")
