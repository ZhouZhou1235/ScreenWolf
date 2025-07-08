package com.pinkcandy.screenwolf.utils;

import java.io.IOException;
import java.io.InputStream;

import javax.swing.ImageIcon;

// 项目资源读取器
public class ResourceReader {
    // 读取资源的输入流
    public static InputStream getResourceAsStream(String filePath){
        InputStream inputStream = ResourceReader.class.getClassLoader().getResourceAsStream(filePath);
        return inputStream;
    }
    // 读取资源为字符串
    public static String readResourceAsString(String filePath){
        InputStream inputStream = ResourceReader.class.getClassLoader().getResourceAsStream(filePath);
        String str = "";
        try{
            str = new String(inputStream.readAllBytes(),"UTF-8");
        }
        catch(IOException e){e.printStackTrace();}
        return str;
    }
    // 读取资源为swing图像
    public static ImageIcon getResourceAsImageIcon(String filePath){
        InputStream inputStream = ResourceReader.class.getClassLoader().getResourceAsStream(filePath);
        ImageIcon imageIcon;
        try {
            imageIcon = new ImageIcon(inputStream.readAllBytes());
        }catch(IOException e){e.printStackTrace();return null;}
        return imageIcon;
    }
}
