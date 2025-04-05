<?php
require_once "config.php";
require_once "Database.php";
require_once "HtmlOut.php";
require_once "Tools.php";

function running(){
    $httpRequest = new HttpRequest();
    $action = $_POST["action"];
    if(!$action){$action = $_GET["action"];}
    switch($action){
        case "login":$httpRequest->login();break;
        case "logout":$httpRequest->logout();break;
        case "adopt":$httpRequest->adopt();break;
        case "changeWolfInfo":$httpRequest->changeWolfInfo();break;
        case "gameLogin":$httpRequest->gameLogin();break;
        case "gameClickLogin":$httpRequest->gameClickLogin();break;
        case "getUserAndWolfInfo":$httpRequest->getUserAndWolfInfo();break;
        case "saveDataToServer":$httpRequest->saveDataToServer();break;
    }
}

class HttpRequest {
    private $htmlOut;
    private $tools;
    private $database;
    public function __construct(){
        if(!empty($_POST["PINKCANDY"])){session_id($_POST["PINKCANDY"]);}
        session_start();
        $this->htmlOut = new HtmlOut();
        $this->tools = new Tools();
        $this->database = new Database();
    }
    // 登录粉糖
    public function login(){
        $username = $this->tools->inputFilter($_POST["username"])[0];
        $pendPassword = $this->tools->inputFilter($_POST["pendPassword"])[0];
        if($this->database->do_login($username,$pendPassword)){
            $_SESSION["username"] = $username;
            header("location:/index.php");
        }
        else{$this->htmlOut->failedInfo("粉糖账号或密码不正确");}
    }
    // 退出登录
    public function logout(){
        session_start();
        session_unset();
        session_destroy();
        header("location:/index.php");
    }
    // 领养狼
    public function adopt(){
        $role = $this->tools->inputFilter($_POST["role"])[0];
        $wolfname = $this->tools->inputFilter($_POST["wolfname"])[0];
        $info = $this->tools->inputFilter($_POST["info"])[0];
        if(!$_SESSION["username"]){return;}
        if(!is_numeric($role)){return;}
        if(!$this->database->get_wolf($_SESSION["username"],1)){
            if(empty($wolfname)){$this->htmlOut->failedInfo("狼名称不能为空");return;}
            $this->database->add_wolf(
                $_SESSION["username"],
                $wolfname,
                $info,
                $role
            );
            header("location:/index.php");
        }
    }
    // 修改狼信息
    public function changeWolfInfo(){
        if(empty($_SESSION["username"])){return;}
        $wolfname = $this->tools->inputFilter($_POST["wolfname"])[0];
        $info = $this->tools->inputFilter($_POST["info"])[0];
        if(empty($wolfname)){$this->htmlOut->failedInfo("狼名称不能为空");return;}
        $wolfID = $this->database->get_wolf($_SESSION["username"],1)["wolfid"];
        $this->database->update_wolfInfo($wolfID,$wolfname,$info);
        header("location:/index.php");
    }
    // 游戏客户端登录
    // return sessionid 失败返回failed
    public function gameLogin(){
        $username = $this->tools->inputFilter($_POST["username"])[0];
        $pendPassword = $this->tools->inputFilter($_POST["pendPassword"])[0];
        if($this->database->do_login($username,$pendPassword)){
            $_SESSION["username"] = $username;
            echo session_id();
        }
        else{echo "failed";}
    }
    // 游戏客户端检查是否登录
    public function gameClickLogin(){
        session_id();
        if(empty($_SESSION["username"])){echo "no";}
        else{echo "yes";}
    }
    // 获取玩家和宠物狼的信息
    public function getUserAndWolfInfo(){
        if(empty($_SESSION["username"])){echo "no log";return;}
        $username = $_SESSION["username"];
        $arr1 = $this->database->get_account($username);
        $arr2 = $this->database->get_wolf($username,1);
        if($arr2==null){echo "no wolf";return;}
        $outArr = [
            "username"=>$arr1["username"],
            "name"=>$arr1["name"],
            "species"=>$arr1["species"],
            "wolfID"=>$arr2["wolfid"],
            "wolfname"=>$arr2["wolfname"],
            "info"=>$arr2["info"],
            "role"=>$arr2["role"],
            "favor"=>$arr2["favor"],
            "clean"=>$arr2["clean"],
            "spirit"=>$arr2["spirit"],
            "level"=>$arr2["level"],
            "food"=>$arr2["food"],
            "viewtime"=>$arr2["viewtime"],
            "adoptedtime"=>$arr2["adoptedtime"],
        ];
        echo json_encode($outArr);
    }
    // 游戏客户端保存狼数据
    public function saveDataToServer(){
        if(empty($_SESSION["username"])){echo "abort";return;}
        $wolfID = $this->database->get_wolf($_SESSION["username"],1)["wolfid"];
        $favor = $this->tools->inputFilter($_POST["favor"])[0];
        $clean = $this->tools->inputFilter($_POST["clean"])[0];
        $food = $this->tools->inputFilter($_POST["food"])[0];
        $spirit = $this->tools->inputFilter($_POST["spirit"])[0];
        $level = $this->tools->inputFilter($_POST["level"])[0];
        echo $this->database->update_wolfData($wolfID,$favor,$clean,$food,$spirit,$level);
        echo "done";
    }
}

running();
