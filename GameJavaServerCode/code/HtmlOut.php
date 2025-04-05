<?php
require_once "config.php";
require_once "Database.php";
require_once "NetConnect.php";
require_once "Tools.php";

class HtmlOut {
    private $haveLog;
    private $haveWolf;
    private $userData;
    private $wolfData;
    private $database;
    private $my_wolves;
    public function __construct(){
        global $g_wolves;
        $this->my_wolves = $g_wolves;
        $this->database = new Database();
        $this->haveLog = false;
        $this->haveWolf = false;
        session_start();
        if(!empty($_SESSION["username"])){
            $this->haveLog = true;
            $arr1 = $this->database->get_account($_SESSION["username"]);
            $this->userData = [
                "username"=>$arr1["username"],
                "name"=>$arr1["name"],
                "species"=>$arr1["species"],
            ];
            if($arr2=$this->database->get_wolf($_SESSION["username"],1)){
                $this->haveWolf = true;
                $this->wolfData = [
                    "username"=>$arr2["username"],
                    "wolfID"=>$arr2["wolfid"],
                    "wolfname"=>$arr2["wolfname"],
                    "info"=>$arr2["info"],
                    "role"=>$arr2["role"],
                    "favor"=>$arr2["favor"],
                    "clean"=>$arr2["clean"],
                    "food"=>$arr2["food"],
                    "spirit"=>$arr2["spirit"],
                    "level"=>$arr2["level"],
                    "viewtime"=>$arr2["viewtime"],
                    "adoptedtime"=>$arr2["adoptedtime"],
                ];
            }
        }
    }
    // html头部
    public function setHead(){
        $out = <<<EOF
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <title>粉糖粒子 - 屏幕有狼</title>
            <link rel="stylesheet" href="resource/css/bootstrap.min.css">
            <script src="resource/js/bootstrap.bundle.min.js"></script>
            <link rel="stylesheet" href="/resource/css/pinkcandy.css">
            <link rel="icon" href="/resource/images/webLogoICO.ico">
            <meta name="keywords" content="粉糖粒子 非盈利 中文 屏幕有狼 桌宠 游戏">
            <meta name="description" content="屏幕有狼 周周编写的桌面宠物游戏 cute pet desktop game by zhou in pinkcandy">
        EOF;
        echo $out;
    }
    // 首页
    public function show_homePage(){
        $out = "";
        $name = $this->userData["name"];
        $species = $this->userData["species"];
        if($this->haveLog && !$this->haveWolf){ // 登录了没宠物
            $out = <<<EOF
                <div class="container p-1 text-center">
                    <img src="/resource/images/screenwolf.png" alt="logo" width="50%">
                </div>
                <div class="row">
                    <div class="col">
                        <p>$name $species 似乎还没有宠物狼呢......</p>
                        <h2>现在就领养一只吧！</h2>
                    </div>
                </div>
                <div class="row">
                    <div class="col-sm-2">
                        <a href="./wolf_adopt.php?role=1">
                            <h1>小蓝狗</h1>
                        </a>
                    </div>
                </div>
                <a href="/code/NetConnect.php?action=logout">离开</a>
            EOF;
        }
        else if($this->haveLog && $this->haveWolf){ // 登录了有宠物
            $wolfname = $this->wolfData["wolfname"];
            $role = $this->wolfData["role"];
            $info = $this->wolfData["info"];
            $favor = $this->wolfData["favor"];
            $clean = $this->wolfData["clean"];
            $food = $this->wolfData["food"];
            $spirit = $this->wolfData["spirit"];
            $level = $this->wolfData["level"];
            $viewtime = $this->wolfData["viewtime"];
            $adoptedtime = $this->wolfData["adoptedtime"];
            $theWolf = $this->my_wolves[$role];
            $wolfRole = $theWolf["roleName"];
            $url = $theWolf["url"];
            $out = <<<EOF
                <div class="container p-3 text-center">
                    <img src="/resource/images/screenwolf.png" alt="logo" width="25%">
                </div>
                <div class="row">
                    <div class="col-sm-6">
                        <img src="$url" alt="wolf" width="100%">
                    </div>
                    <div class="col-sm-6">
                        <h2>$wolfname</h2>
                        <h3>角色类型 $wolfRole</h3>
                        <h3>等级 $level</h3>
                        <p>$info</p>
                        <small>好感度</small>
                        <div class="progress" role="progressbar" aria-label="favor" aria-valuenow="100" aria-valuemin="0" aria-valuemax="100">
                            <div class="progress-bar progress-bar-striped bg-danger" style="width: $favor%"></div>
                        </div>
                        <small>清洁度</small>
                        <div class="progress" role="progressbar" aria-label="clean" aria-valuenow="50" aria-valuemin="0" aria-valuemax="100">
                            <div class="progress-bar progress-bar-striped bg-info" style="width: $clean%"></div>
                        </div>
                        <small>饱食度</small>
                        <div class="progress" role="progressbar" aria-label="food" aria-valuenow="50" aria-valuemin="0" aria-valuemax="100">
                            <div class="progress-bar progress-bar-striped bg-warning" style="width: $food%"></div>
                        </div>
                        <small>精神</small>
                        <div class="progress" role="progressbar" aria-label="food" aria-valuenow="50" aria-valuemin="0" aria-valuemax="100">
                            <div class="progress-bar progress-bar-striped bg-primary" style="width: $spirit%"></div>
                        </div>
                        <small>陪伴时间 $viewtime</small>
                        <small>领养时间 $adoptedtime</small>
                        <a href="/wolf_control.php" class="d-grid">
                            <button class="btn btn-sm">编辑狼</button>
                        </a>
                        <a href="/code/NetConnect.php?action=logout">离开</a>
                    </div>
                </div>
            EOF;
        }
        else{ // 没登录
            $out = <<<EOF
                <div class="container p-3 text-center">
                    <img src="/resource/images/screenwolf.png" alt="logo" width="75%">
                </div>
                <div class="row">
                    <div class="col-sm-6">
                        <h1>嗷呜~屏幕有狼！</h1>
                        <h2>周周编写的开源免费桌宠游戏</h2>
                        <div class="row">
                            <div class="col-sm-6">
                                <a href="/resource/game/ScreenWolf_1.0.0_windows64.zip">
                                    <div class="d-grid">
                                        <button class="btn btn-info">立即下载</button>
                                    </div>
                                </a>
                            </div>
                            <div class="col-sm-6">
                                <a href="https://github.com/ZhouZhou1235/ScreenWolf">
                                    <div class="d-grid">
                                        <button class="btn btn-dark">github下载</button>
                                    </div>
                                </a>
                            </div>
                        </div>
                        <p style="white-space: pre-line;font-size: 1.2em;">
                            在电脑屏幕上养一只宠物狼吧！
                            宠物狼与粉糖账号绑定。
                            领养后可以在这里或者画廊小兽空间看到宠物状态哦~
                            如果没有粉糖账号请先到<a href="http://gallery.pinkcandy.top">幻想动物画廊</a>注册
                        </p>
                        <a href="http://pinkcandy.top">粉糖粒子</a>
                        <a href="https://github.com/ZhouZhou1235">github</a>
                    </div>
                    <div class="col-sm-6">
                        <form action="./code/NetConnect.php" method="post">
                            <div class="mb-3">
                                <label for="" class="form-label">粉糖账号</label>
                                <input
                                    type="text"
                                    class="form-control"
                                    name="username"
                                    id=""
                                    placeholder=""
                                />
                            </div>
                            <div class="mb-3">
                                <label for="" class="form-label">密码</label>
                                <input
                                    type="password"
                                    class="form-control"
                                    name="pendPassword"
                                    id=""
                                    placeholder=""
                                />
                            </div>
                            <input type="hidden" name="action" value="login">
                            <div class="d-grid">
                                <button class="btn btn-secondary">登录粉糖</button>
                            </div>
                        </form>        
                    </div>
                </div>
            EOF;
        }
        echo $out;
    }
    // 新领养页
    public function show_adoptWolf(){
        $out = "";
        if($this->haveLog && !$this->haveWolf){
            $role = $_GET["role"];
            $theWolf = $this->my_wolves[$role];
            $wolfRole = $theWolf["roleName"];
            $url = $theWolf["url"];
            $out = <<<EOF
                <h1>选择领养 角色类型 $wolfRole</h1>
                <div class="row">
                    <div class="col">
                        <h2>确定领养这只宠物狼吗？领养后要负责，不能更换！</h2>
                        <div class="row p-3">
                            <div class="col-sm-6">
                                <img src="$url" alt="wolf" width="100%">
                            </div>
                            <div class="col-sm-6">
                                <form action="/code/NetConnect.php" method="post" class="d-grid">
                                    <input type="hidden" name="role" value="$role">
                                    <input type="hidden" name="action" value="adopt">
                                    <div class="input-group">
                                        <span class="input-group-text">狼名称</span>
                                        <input type="text" name="wolfname" class="form-control">
                                    </div>
                                    <div class="input-group">
                                        <span class="input-group-text">狼描述</span>
                                        <input type="text" name="info" class="form-control">
                                    </div>
                                    <button class="btn btn-secondary">确定</button>
                                </form>
                            </div>
                        </div>
                    </div>
                </div>
            EOF;
        }
        echo $out;
    }
    // 宠物管理页
    public function show_controlWolf(){
        $out = "";
        if($this->haveLog && $this->haveWolf){
            $wolfname = $this->wolfData["wolfname"];
            $role = $this->wolfData["role"];
            $info = $this->wolfData["info"];
            $favor = $this->wolfData["favor"];
            $clean = $this->wolfData["clean"];
            $food = $this->wolfData["food"];
            $spirit = $this->wolfData["spirit"];
            $level = $this->wolfData["level"];
            $viewtime = $this->wolfData["viewtime"];
            $adoptedtime = $this->wolfData["adoptedtime"];
            $theWolf = $this->my_wolves[$role];
            $wolfRole = $theWolf["roleName"];
            $url = $theWolf["url"];
            $out = <<<EOF
                <div class="row">
                    <div class="col-sm-6">
                        <img src="$url" alt="wolf" width="100%">
                    </div>
                    <div class="col-sm-6">
                        <h2>$wolfname</h2>
                        <h3>角色类型 $wolfRole</h3>
                        <h3>等级 $level</h3>
                        <p>$info</p>
                        <small>好感度</small>
                        <div class="progress" role="progressbar" aria-label="favor" aria-valuenow="100" aria-valuemin="0" aria-valuemax="100">
                            <div class="progress-bar progress-bar-striped bg-danger" style="width: $favor%"></div>
                        </div>
                        <small>清洁度</small>
                        <div class="progress" role="progressbar" aria-label="clean" aria-valuenow="50" aria-valuemin="0" aria-valuemax="100">
                            <div class="progress-bar progress-bar-striped bg-info" style="width: $clean%"></div>
                        </div>
                        <small>饱食度</small>
                        <div class="progress" role="progressbar" aria-label="food" aria-valuenow="50" aria-valuemin="0" aria-valuemax="100">
                            <div class="progress-bar progress-bar-striped bg-warning" style="width: $food%"></div>
                        </div>
                        <small>精神</small>
                        <div class="progress" role="progressbar" aria-label="food" aria-valuenow="50" aria-valuemin="0" aria-valuemax="100">
                            <div class="progress-bar progress-bar-striped bg-primary" style="width: $spirit%"></div>
                        </div>
                        <small>陪伴时间 $viewtime</small>
                        <small>领养时间 $adoptedtime</small>
                        <form action="/code/NetConnect.php" method="post">
                            <input type="hidden" name="action" value="changeWolfInfo">
                            <div class="input-group">
                                <span class="input-group-text">狼名称</span>
                                <input type="text" name="wolfname" class="form-control" value="$wolfname">
                            </div>
                            <div class="input-group">
                                <span class="input-group-text">狼描述</span>
                                <input type="text" name="info" class="form-control" value="$info">
                            </div>
                            <div class="d-grid">
                                <button class="btn btn-secondary">修改</button>
                            </div>
                        </form>
                    </div>
                </div>
            EOF;
        }
        echo $out;
    }
    // 失败提示
    public function failedInfo($info){
        $out = <<<EOF
            <!DOCTYPE html>
            <html lang="zh">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>error</title>
                <link rel="stylesheet" href="resource/css/bootstrap.min.css">
                <script src="resource/js/bootstrap.bundle.min.js"></script>
            </head>
            <body>
                <div class="container text-center bg-warning">
                    <h1>失败提示</h1>
                    <h2> $info </h2>
                </div>
            </body>
            </html>
        EOF;
        echo $out;
    }
}
