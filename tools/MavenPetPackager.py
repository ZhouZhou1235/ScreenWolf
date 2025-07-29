# 桌宠Maven项目打包程序

import os
import shutil
import subprocess

class MavenPetPackager:
    def __init__(self):
        self.root = os.getcwd().replace("\\", "/")
        self.paths = {
            'mvn': f"{self.root}/bin/maven-bin/bin/mvn.cmd",
            'lib': f"{self.root}/bin/lib/ScreenWolf.jar",
            'projects_dir': f"{self.root}/pet-projects/",
            'output_dir': f"{self.root}/data/pets/",
            'jdk': f"{self.root}/bin/jdk",
            'javac': f"{self.root}/bin/jdk/bin/javac.exe"
        }
        
    def _exec(self, cmd):
        env = os.environ.copy()
        env["JAVA_HOME"] = os.path.normpath(self.paths['jdk'])
        env["PATH"] = f"{self.paths['jdk']}/bin;{env['PATH']}"
        return subprocess.call(cmd, shell=True, env=env) == 0
    def _install_core(self):
        cmd = (
            f'"{self.paths["mvn"]}" install:install-file '
            f'-Dfile="{self.paths["lib"]}" '
            '-DgroupId=com.pinkcandy '
            '-DartifactId=screenwolf '
            '-Dversion=1.0.0 '
            '-Dpackaging=jar'
        )
        return self._exec(cmd)
    def _build(self, project):
        pom = f"{self.paths['projects_dir']}{project}/pom.xml"
        if not os.path.exists(pom):
            return False 
        compile_cmd = (
            f'"{self.paths["mvn"]}" -f "{pom}" clean package '
            f'-Dmaven.compiler.fork=true '
            f'-Dmaven.compiler.executable="{os.path.normpath(self.paths["jdk"])}/bin/javac"'
        )
        if not self._exec(compile_cmd):
            return False
        target_dir = f"{self.paths['projects_dir']}{project}/target/"
        for f in os.listdir(target_dir):
            if f.endswith(".jar") and not f.endswith("-sources.jar"):
                src = os.path.join(target_dir, f)
                dst = os.path.join(self.paths['output_dir'], f)
                shutil.move(src, dst)
                return True
        return False
    def run(self):
        if not os.path.exists(self.paths['javac']):
            return False
            
        if not self._install_core():
            return False
            
        os.makedirs(self.paths['output_dir'], exist_ok=True)
        success = True
        
        for project in os.listdir(self.paths['projects_dir']):
            project_path = os.path.join(self.paths['projects_dir'], project)
            if os.path.isdir(project_path):
                if not self._build(project):
                    success = False
        return success

if __name__ == "__main__":
    if MavenPetPackager().run():
        print("PINKCANDY: 成功")
    else:
        print("PINKCANDY: 失败")
    input("按回车关闭")
