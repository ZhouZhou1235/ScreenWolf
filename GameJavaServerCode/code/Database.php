<?php
require_once "config.php";
require_once "HtmlOut.php";
require_once "NetConnect.php";
require_once "Tools.php";

class Database {
    private $my_connect;
    public function __construct(){
        global $g_connect;
        $this->my_connect = $g_connect; 
    }
    // 登录
    public function do_login($username,$pendPassword){
        $sql = "select * from pc3_account where username='$username'";
        if($result=$this->my_connect->query($sql)){
            if(password_verify($pendPassword,$result->fetch_array()["password"])){
                return true;
            }
        }
        return false;
    }
    // 得到小兽用户数据
    public function get_account($username){
        $sql = "select * from pc3_account where username='$username'";
        $result = $this->my_connect->query($sql);
        return $result->fetch_array();
    }
    // 得到狼数据
    public function get_wolf($number,$mode){
        $sql = "";
        if($mode==1){$sql="select * from pc3game_wolves where username='$number'";}
        if($mode==2){$sql="select * from pc3game_wolves where wolfid='$number'";}
        $result = $this->my_connect->query($sql);
        return $result->fetch_array();
    }
    // 领养狼
    public function add_wolf($username,$wolfname,$info,$role){
        do{$wolfID = random_int(10000,99999);}
        while($this->get_wolf($wolfID,2)!=null);
        $sql = "
            insert into pc3game_wolves(username,wolfid,wolfname,info,role,favor,clean,food,spirit,level)
            values('$username','$wolfID','$wolfname','$info','$role',50,75,75,75,1)
        ";
        $this->my_connect->query($sql);
    }
    // 修改狼信息
    public function update_wolfInfo($wolfID,$wolfname,$info){
        $sql = "
            update pc3game_wolves
            set wolfname='$wolfname',info='$info'
            where wolfid='$wolfID'
        ";
        $this->my_connect->query($sql);
    }
    // 更新狼数据
    public function update_wolfData($wolfID,$favor,$clean,$food,$spirit,$level){
        $sql = "
            update pc3game_wolves
            set favor='$favor',clean='$clean',food='$food',spirit='$spirit',level='$level',viewtime=CURRENT_TIMESTAMP
            where wolfid='$wolfID'
        ";
        $this->my_connect->query($sql);
    }
}
