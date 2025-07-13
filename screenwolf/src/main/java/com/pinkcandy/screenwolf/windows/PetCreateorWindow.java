package com.pinkcandy.screenwolf.windows;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import com.pinkcandy.screenwolf.base.WindowBase;
import com.pinkcandy.screenwolf.utils.GsonUtil;
import com.pinkcandy.screenwolf.utils.GUtil;

public class PetCreateorWindow extends WindowBase{
    private JTextField nameField;
    private JTextField infoField;
    private JTextField jarNameField;
    private JTextField mainClassField;
    private JTextArea messageBubblesArea;
    private JTextField animationFolderField;
    private JButton createButton;
    private JButton selectFolderButton;
    public static class PetConfig {
        public String name;
        public String info;
        public String jarName;
        public String mainClass;
        public String[] messageBubbleList;
        public PetConfig(){
            this.name="";
            this.info="";
            this.jarName="xxx-1.0.0.jar";
            this.mainClass="com.pinkcandy.xxx.XXX";
            this.messageBubbleList=new String[]{};
        }
    }
    private PetConfig currentConfig=new PetConfig();
    private String animationFolderPath;
    public PetCreateorWindow(){
        super("PetCreateor",GUtil.DEFAULT_windowSize);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        initUI();
        loadConfigToForm();
        GUtil.setWindowCenter(this);
    }
    private void initUI(){
        JPanel mainPanel=new JPanel(new BorderLayout(10,10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15,15,15,15));
        JPanel formPanel=new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel,BoxLayout.Y_AXIS));
        nameField=createFormField("桌宠名称:",formPanel);
        infoField=createFormField("桌宠描述:",formPanel);
        jarNameField=createFormField("JAR文件名:",formPanel);
        mainClassField=createFormField("主类名:",formPanel);
        JPanel bubblesPanel=new JPanel(new BorderLayout());
        bubblesPanel.add(new JLabel("消息气泡列表(每行一个):"),BorderLayout.NORTH);
        messageBubblesArea=new JTextArea(5,20);
        bubblesPanel.add(new JScrollPane(messageBubblesArea),BorderLayout.CENTER);
        formPanel.add(bubblesPanel);
        JPanel folderPanel=new JPanel(new BorderLayout());
        folderPanel.add(new JLabel("帧动画文件夹:"),BorderLayout.NORTH);
        JPanel folderInputPanel=new JPanel(new BorderLayout());
        animationFolderField=new JTextField();
        folderInputPanel.add(animationFolderField,BorderLayout.CENTER);
        selectFolderButton=new JButton("选择文件夹");
        selectFolderButton.addActionListener(this::selectAnimationFolder);
        folderInputPanel.add(selectFolderButton,BorderLayout.EAST);
        folderPanel.add(folderInputPanel,BorderLayout.CENTER);
        formPanel.add(folderPanel);
        JPanel buttonPanel=new JPanel(new FlowLayout(FlowLayout.RIGHT));
        createButton=new JButton("创建桌宠");
        createButton.addActionListener(this::createPet);
        buttonPanel.add(createButton);
        mainPanel.add(formPanel,BorderLayout.CENTER);
        mainPanel.add(buttonPanel,BorderLayout.SOUTH);
        
        this.add(mainPanel);
    }
    private JTextField createFormField(String label,JPanel parent){
        JPanel panel=new JPanel(new BorderLayout(5,5));
        panel.add(new JLabel(label),BorderLayout.WEST);
        JTextField field=new JTextField();
        panel.add(field,BorderLayout.CENTER);
        parent.add(panel);
        return field;
    }
    private void loadConfigToForm(){
        nameField.setText(currentConfig.name);
        infoField.setText(currentConfig.info);
        jarNameField.setText(currentConfig.jarName);
        mainClassField.setText(currentConfig.mainClass);
        messageBubblesArea.setText(String.join("\n",currentConfig.messageBubbleList));
    }
    private void saveFormToConfig(){
        currentConfig.name=nameField.getText();
        currentConfig.info=infoField.getText();
        currentConfig.jarName=jarNameField.getText();
        currentConfig.mainClass=mainClassField.getText();
        currentConfig.messageBubbleList=messageBubblesArea.getText().split("\\r?\\n");
        animationFolderPath=animationFolderField.getText();
    }
    private void selectAnimationFolder(ActionEvent e){
        JFileChooser chooser=new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setDialogTitle("选择帧动画文件夹");
        if(chooser.showOpenDialog(this)==JFileChooser.APPROVE_OPTION){
            animationFolderField.setText(chooser.getSelectedFile().getAbsolutePath());
        }
    }
    private void createPet(ActionEvent e){
        try{
            saveFormToConfig();
            if(animationFolderPath==null||animationFolderPath.isEmpty()){
                throw new Exception("请选择帧动画文件夹");
            }
            String jsonConfig=GsonUtil.bean2Json(currentConfig);
            JOptionPane.showMessageDialog(this,
                "桌宠配置创建成功!\nJSON配置:"+jsonConfig+
                "\n动画文件夹:"+animationFolderPath,
                "成功",JOptionPane.INFORMATION_MESSAGE);
            
            // TODO:添加回调处理逻辑
            // Launcher.getInstance().onPetCreated(jsonConfig,animationFolderPath);
            
        }catch(Exception ex){
            JOptionPane.showMessageDialog(this,
                "创建配置时出错:"+ex.getMessage(),
                "错误",JOptionPane.ERROR_MESSAGE);
        }
    }
    public String getJsonConfig(){return GsonUtil.bean2Json(currentConfig);}
    public String getAnimationFolderPath(){return animationFolderPath;}
    public PetConfig getPetConfig(){return currentConfig;}
}
