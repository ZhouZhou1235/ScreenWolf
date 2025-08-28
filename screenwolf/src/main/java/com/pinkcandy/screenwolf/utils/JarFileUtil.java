package com.pinkcandy.screenwolf.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;


/**
 * jar包文件工具类
 * 可以读取jar包的内容
 */
public class JarFileUtil {
    // 读取jar指定目录下的文件夹名
    static public ArrayList<String> listJarDirNamesByPath(String jarPath,String dirPath){
        try(JarFile jarFile = new JarFile(jarPath)){
            Enumeration<JarEntry> entries = jarFile.entries();
            ArrayList<String> nameList = new ArrayList<>();
            while(entries.hasMoreElements()){
                JarEntry entry = entries.nextElement();
                String path = entry.getName();
                if(entry.isDirectory()&&path.startsWith(dirPath)){
                    String name = path.substring(dirPath.length()).replace("/","");
                    if(name!=""){nameList.add(name);}
                }
            }
            return nameList;
        }catch(IOException e){e.printStackTrace();return null;}
    }
    // 读取jar指定目录下的文件 输出哈希映射
    static public HashMap<String,byte[]> listJarFileMapByPath(String jarPath,String dirPath){
        try(JarFile jarFile = new JarFile(jarPath)){
            Enumeration<JarEntry> entries = jarFile.entries();
            HashMap<String,byte[]> hashMap = new HashMap<>();
            while(entries.hasMoreElements()){
                JarEntry entry = entries.nextElement();
                String name = entry.getName();
                if(!entry.isDirectory()&&name.startsWith(dirPath)){
                    InputStream inputStream = jarFile.getInputStream(entry);
                    byte[] byteData = inputStream.readAllBytes();
                    hashMap.put(name,byteData);
                }
            }
            return hashMap;
        }catch(IOException e){e.printStackTrace();return null;}
    }
    // 读取jar指定文件的字节流
    static public byte[] readByteInJarFile(String jarPath,String filePath) throws IOException{
        try(JarFile jarFile = new JarFile(jarPath)){
            JarEntry entry = jarFile.getJarEntry(filePath);
            if(entry!=null){
                try(InputStream is = jarFile.getInputStream(entry)){
                    return is.readAllBytes();
                }
            }
        }
        return null;
    }
}
