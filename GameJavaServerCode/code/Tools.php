<?php
class Tools {
    public function __construct(){}
    // 非法字符过滤器
    function inputFilter($inputStr){
        $danger = false;
        $dangerList = [
            '`','@','#','$','%','^','&','*','(',')',
            '{','}','[',']','|','\\','<','>','/','_',
            '\'','"','/*','//','<>',
        ];
        for($i=0;$inputStr[$i];$i++){
            if(in_array($inputStr[$i],$dangerList)){
                $danger = true;
                $inputStr = htmlspecialchars($inputStr);
                break;
            };
        }    
        return [0=>$inputStr,1=>$danger];
    }
}
