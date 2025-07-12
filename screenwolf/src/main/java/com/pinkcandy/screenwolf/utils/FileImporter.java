package com.pinkcandy.screenwolf.utils;

import javax.swing.*;

import com.pinkcandy.screenwolf.base.WindowBase;

import java.awt.Dimension;
import java.nio.file.Files;
import java.util.function.Consumer;

// 文件导入器
public class FileImporter extends WindowBase {
    private Consumer<byte[]> fileCallback; // 回调事件
    private JPanel mainPanel = new JPanel();
    public FileImporter(Consumer<byte[]> callback){
        super("文件导入器",new Dimension(
            (int)(GUtil.DEFAULT_windowSize.width/2),
            GUtil.DEFAULT_windowSize.height
        ));
        this.fileCallback = callback;
        initUI();
        this.setVisible(true);
    }
    private void initUI(){
        JButton importButton = new JButton("导入文件");
        importButton.addActionListener(e->importFile());
        mainPanel.add(importButton);
        this.add(mainPanel);
    }
    // 导入文件
    private void importFile(){
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("选择文件");
        if(chooser.showOpenDialog(this)==JFileChooser.APPROVE_OPTION){
            try {
                byte[] fileData = Files.readAllBytes(chooser.getSelectedFile().toPath());
                fileCallback.accept(fileData);
            }catch(Exception ex){
                JOptionPane.showMessageDialog(this, 
                    ex.getMessage(), 
                    "错误", 
                    JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }
}
