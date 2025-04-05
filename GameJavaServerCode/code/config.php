<?php

// 数据库
$g_dbInfo = [
    // 连接
    "hostname"=>"localhost",
    "dbuser"=>"pinkcandyzhou",
    "password"=>"20231001",
    "dbname"=>"pinkcandy3",
];
$g_connect = new mysqli(
    $g_dbInfo["hostname"],
    $g_dbInfo["dbuser"],
    $g_dbInfo["password"],
    $g_dbInfo["dbname"]
);

// 宠物狼的介绍
$g_wolves = [
    "1"=>[
        "roleName"=>"小蓝狗",
        "url"=>"/resource/images/wolves/ZhouZhou.png",
    ]
];
