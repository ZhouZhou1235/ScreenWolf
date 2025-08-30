# 桌宠Maven项目打包程序

import os
import shutil
import subprocess
import sys
import json
import zipfile
import random
import xpinyin


xpinyin_obj = xpinyin.Pinyin()

class MavenPetPackager:
    def __init__(self):
        self.root = os.getcwd().replace("\\", "/")
        self.paths = {
            'mvn': f"{self.root}/bin/maven-bin/bin/mvn.cmd",
            'lib': f"{self.root}/bin/lib/ScreenWolf.jar",
            'projects_dir': f"{self.root}/pet-projects/",
            'output_dir': f"{self.root}/output/",
            'jdk': f"{self.root}/bin/jdk",
            'javac': f"{self.root}/bin/jdk/bin/javac.exe",
            'thepet_assets': f"{self.root}/thepet_assets/",
            'pet_demo_zip': f"{self.root}/bin/pet-demo.zip",
            'temp_dir': f"{self.root}/tmp/"
        }
    # 执行命令
    def exec(self, cmd):
        env = os.environ.copy()
        env["JAVA_HOME"] = os.path.normpath(self.paths['jdk'])
        env["PATH"] = f"{self.paths['jdk']}/bin;{env['PATH']}"
        return subprocess.call(cmd, shell=True, env=env) == 0
    # 游戏主程序安装
    def install_core(self):
        cmd = (
            f'"{self.paths["mvn"]}" install:install-file '
            f'-Dfile="{self.paths["lib"]}" '
            '-DgroupId=com.pinkcandy '
            '-DartifactId=screenwolf '
            '-Dpackaging=jar'
        )
        return self.exec(cmd)
    # maven编译打包
    def build(self, projectPath):
        pom = f"{projectPath}/pom.xml"
        if not os.path.exists(pom):
            return False
        compile_cmd = (
            f'"{self.paths["mvn"]}" -f "{pom}" clean package '
            f'-Dmaven.compiler.fork=true '
            f'-Dmaven.compiler.executable="{os.path.normpath(self.paths["jdk"])}/bin/javac"'
        )
        if not self.exec(compile_cmd):
            return False
        target_dir = f"{projectPath}/target/"
        for f in os.listdir(target_dir):
            if f.endswith(".jar") and not f.endswith("-sources.jar"):
                src = os.path.join(target_dir, f)
                dst = os.path.join(self.paths['output_dir'], f)
                shutil.move(src, dst)
                return True
        return False
    # 扫描打包pet-projects文件夹下所有项目
    def scan_run(self):
        if not os.path.exists(self.paths['javac']):
            return False
        if not self.install_core():
            return False
        os.makedirs(self.paths['output_dir'], exist_ok=True)
        success = True
        for project in os.listdir(self.paths['projects_dir']):
            project_path = os.path.join(self.paths['projects_dir'], project)
            if os.path.isdir(project_path):
                if not self.build(self.paths['projects_dir']+project):
                    success = False
        return success
    # 创建宠物项目并打包
    def create_and_build_pet(self,pet_name:str,pet_info:str):
        if os.path.exists(self.paths['temp_dir']):
            shutil.rmtree(self.paths['temp_dir'])
        os.makedirs(self.paths['temp_dir'], exist_ok=True)
        try:
            with zipfile.ZipFile(self.paths['pet_demo_zip'], 'r') as zip_ref:
                zip_ref.extractall(self.paths['temp_dir'])
        except Exception as e:
            print(e)
            return False
        extracted_dirs = [d for d in os.listdir(self.paths['temp_dir']) 
        if os.path.isdir(os.path.join(self.paths['temp_dir'], d))]
        if not extracted_dirs: return False
        project_dir = os.path.join(self.paths['temp_dir'], extracted_dirs[0])
        # 生成pet_data.json
        pet_data = {
            "id": xpinyin_obj.get_pinyin(pet_name).lower().replace(" ", "-")+str(random.randint(100000000,999999999)),
            "mainClass": f"com.pinkcandy.pet_demo.Pet",
            "name": pet_name,
            "info": pet_info,
            "specialMessages": [],
            "sadMessages": []
        }
        meta_inf_dir = os.path.join(project_dir, "src", "main", "resources", "META-INF")
        os.makedirs(meta_inf_dir, exist_ok=True)
        with open(os.path.join(meta_inf_dir, "pet_data.json"), "w", encoding="utf-8") as f:
            json.dump(pet_data, f, ensure_ascii=False, indent=2)
        # 生成pom.xml
        artifact_id = f"pet-{xpinyin_obj.get_pinyin(pet_name).lower().replace(' ','-')}"
        group_id = f"com.pinkcandy.{xpinyin_obj.get_pinyin(pet_name).lower().replace(' ','')}"
        pom_content = f'''<?xml version="1.0" encoding="UTF-8"?>
<project
    xmlns="http://maven.apache.org/POM/4.0.0"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd"
>

    <modelVersion>4.0.0</modelVersion>

    <groupId>{group_id}</groupId>
    <artifactId>{artifact_id}</artifactId>
    <version>1.0.0</version>
    <packaging>jar</packaging>
    <name>{xpinyin_obj.get_pinyin(pet_name)}</name>
    <description>{xpinyin_obj.get_pinyin(pet_info)}</description>

    <properties>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <maven.compiler.source>21</maven.compiler.source>
        <maven.compiler.target>21</maven.compiler.target>
    </properties>

    <dependencies>
        <dependency>
            <groupId>com.pinkcandy</groupId>
            <artifactId>screenwolf</artifactId>
            <version>1.0.0</version>
        </dependency>
    </dependencies>

    <build>
        <resources>
            <resource>
                <directory>src/main/resources</directory>
                <includes>
                    <include>**/*.json</include>
                    <include>**/*.png</include>
                    <include>**/*.jpg</include>
                </includes>
            </resource>
        </resources>
    </build>

</project>'''
        with open(os.path.join(project_dir, "pom.xml"), "w", encoding="utf-8") as f:
            f.write(pom_content)
        assets_src = self.paths['thepet_assets']
        assets_dest = os.path.join(project_dir, "src", "main", "resources", "assets")
        if os.path.exists(assets_src):
            if os.path.exists(assets_dest):
                shutil.rmtree(assets_dest)
            shutil.copytree(assets_src,assets_dest)
        return self.build(project_dir)

if __name__ == "__main__":
    print("===MavenPetPackager 桌宠打包程序===")
    print("输入选项数字后回车执行 input option number then enter")
    while True:
        print("1 扫描文件夹打包 scan dir to build")
        print("2 输入信息然后使用资源创建 input some information then use assets to build")
        print("0 退出 exit")
        try:
            option = int(input('选项 option:'))
            packager = MavenPetPackager()
            if option == 1:
                if packager.scan_run():
                    input("PINKCANDY: 全部打包完成 task finished.")
                else:
                    input("PINKCANDY: 打包失败 build failed!")
            elif option == 2:
                pet_name = input('宠物名 pet name:')
                pet_info = input('描述 pet info:')
                if packager.create_and_build_pet(pet_name,pet_info):
                    input(f"PINKCANDY: 打包完成 task finished.")
                else:
                    input("PINKCANDY: 打包失败 build failed!")
            elif option == 0:
                sys.exit()
            else:
                input('PINKCANDY: 未知选项 unknown option.')
        except Exception as e: print(e)
