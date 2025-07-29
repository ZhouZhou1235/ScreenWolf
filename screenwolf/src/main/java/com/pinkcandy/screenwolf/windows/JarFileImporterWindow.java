package com.pinkcandy.screenwolf.windows;

import javax.swing.*;

import com.pinkcandy.screenwolf.utils.FileImporter;
import com.pinkcandy.screenwolf.utils.GUtil;

import java.awt.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.function.Consumer;

// JAR文件导入器窗口
// TODO 考虑直接使用 JFileChooser 不用包装为类
public class JarFileImporterWindow extends FileImporter {
    private Path targetDirectory; // 目标目录路径
    public JarFileImporterWindow(Consumer<byte[]> callback, String targetDirectory){
        super(callback);
        this.targetDirectory = Paths.get(targetDirectory);
        customizeUI();
        GUtil.setWindowCenter(this);
    }
    private void customizeUI(){
        setTitle("JAR文件导入器");
        JLabel directoryLabel = new JLabel("目标目录: "+targetDirectory.toString());
        mainPanel.add(directoryLabel, BorderLayout.SOUTH);
    }
    @Override
    public void importFile(){
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("选择JAR文件");
        chooser.setFileFilter(new javax.swing.filechooser.FileFilter(){
            @Override
            public boolean accept(java.io.File f){
                return f.getName().toLowerCase().endsWith(".jar") || f.isDirectory();
            }
            @Override
            public String getDescription(){
                return "JAR文件 (*.jar)";
            }
        });
        if(chooser.showOpenDialog(this)==JFileChooser.APPROVE_OPTION){
            Path selectedFile = chooser.getSelectedFile().toPath();
            if(!selectedFile.toString().toLowerCase().endsWith(".jar")){
                JOptionPane.showMessageDialog(this, 
                    "PINKCANDY: 选择有效的JAR文件", 
                    "错误", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            try{
                byte[] fileData = Files.readAllBytes(selectedFile);
                Path targetPath = targetDirectory.resolve(selectedFile.getFileName());
                Files.copy(selectedFile, targetPath, StandardCopyOption.REPLACE_EXISTING);
                if(fileCallback!=null){fileCallback.accept(fileData);}
                JOptionPane.showMessageDialog(this, 
                    "已成功导入并复制到:"+targetPath, 
                    "成功", 
                    JOptionPane.INFORMATION_MESSAGE
                ); 
            }catch(IOException ex){
                JOptionPane.showMessageDialog(this, 
                    "处理JAR文件时出错: " + ex.getMessage(), 
                    "错误", 
                    JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }
    // 设置新目标目录
    public void setTargetDirectory(String targetDirectory){this.targetDirectory=Paths.get(targetDirectory);}
    // 获取当前目标目录
    public String getTargetDirectory(){return targetDirectory.toString();}
}
