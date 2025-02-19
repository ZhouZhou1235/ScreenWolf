package pinkcandy.screenwolf.core;

import pinkcandy.screenwolf.GArea;
import pinkcandy.screenwolf.GTools;
import pinkcandy.screenwolf.core.wolves.ZhouZhou;

import javax.swing.*;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

// 启动器
public class Launcher extends JFrame {
    boolean haveLog; // 是否登录
    JPanel rootPanel; // 窗体基面板
    Map<String,String> userData; // 玩家用户数据
    public Launcher(){
        new GTools();
        haveLog = false;
        rootPanel = new JPanel();
        // GTools.setGlobalFont(new Font("SansSerif",Font.BOLD,24));
        GTools.setGlobalFont(new Font("SimHei",Font.BOLD,GTools.GAME_FONTSIZE));
        this.setContentPane(rootPanel);
        this.setSize(GTools.SCREEN_WIDTH/2,GTools.SCREEN_HEIGHT/2);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setTitle(GArea.GAME_TITLE);
        // this.setResizable(false);
        this.setVisible(true);
        checkLogin();
        loadInfo();
    }
    // 加载开始界面
    private void loadInfo(){
        rootPanel.removeAll();
        rootPanel.add(new JLabel(GTools.scaleImageIcon(new ImageIcon(GArea.GAME_LOGO),this.getWidth())));
        if(haveLog){
            getToLoadUserDataInfo();
            String str = userData.get("name")+"的宠物狼："+userData.get("wolfname");
            JLabel label = new JLabel(str);
            JButton beginButton = new JButton("开始");
            JButton logoutButton = new JButton("退出登录");
            beginButton.addActionListener(new ActionListener(){
                @Override public void actionPerformed(ActionEvent e){begin();}
            });
            logoutButton.addActionListener(new ActionListener(){
                @Override public void actionPerformed(ActionEvent e){logout();}
            });
            rootPanel.add(label);
            rootPanel.add(beginButton);
            rootPanel.add(logoutButton);
        }
        else{
            JLabel label1 = new JLabel("粉糖账号");
            JLabel label2 = new JLabel("密码");
            JTextField username = new JTextField(10);
            JPasswordField password = new JPasswordField(10);
            password.setEchoChar('*');
            JButton loginButton = new JButton("登录");
            loginButton.addActionListener(new ActionListener(){
                @Override public void actionPerformed(ActionEvent e){
                    String pendPassword = new String(password.getPassword());
                    login(username.getText(),pendPassword);
                }
            });
            rootPanel.add(label1);
            rootPanel.add(username);
            rootPanel.add(label2);
            rootPanel.add(password);
            rootPanel.add(loginButton);
        }
        this.repaint();rootPanel.updateUI();
    }
    // 开始运行
    private void begin(){
        getToLoadUserDataInfo();
        if(haveLog && userData!=null){
            Wolf theWolf = loadWolf();
            Window window = new Window();
            window.loadWolf(theWolf);
            window.listenWolf(window.myWolf);
            window.add(window.myWolf,-1);
            new Tray(window);
            this.setVisible(false);    
        }
    }
    // 退出登录
    private void logout(){
        GTools.saveContentToFile(GArea.GAME_USER+"PINKCANDY","");
        GTools.saveContentToFile(GArea.GAME_USER+"userdata","");
        checkLogin();
        loadInfo();
    }
    // 登录
    private void login(String username,String pendPassword){
        Map<String,String> map = new HashMap<String,String>();
        map.put("action","gameLogin");
        map.put("username",username);
        map.put("pendPassword",pendPassword);
        String sessionID = GTools.postSender(GTools.GAME_NET,null,map);
        if(sessionID.equals("failed")){
            JOptionPane.showMessageDialog(
                null,
                "粉糖账号或密码不正确",
                "登录失败",
                JOptionPane.ERROR_MESSAGE
            );
        }
        else{
            GTools.saveContentToFile(GArea.GAME_USER+"PINKCANDY",sessionID);
            checkLogin();
            loadInfo();
        }
    }
    // 检查是否登录
    private boolean checkLogin(){
        Map<String,String> headerMap = new HashMap<String,String>();
        Map<String,String> bodyMap = new HashMap<String,String>();
        String sessionID = GTools.getContentFromFile(GArea.GAME_USER+"PINKCANDY");
        bodyMap.put("action","gameClickLogin");
        bodyMap.put("PINKCANDY",sessionID);
        String res = GTools.postSender(GTools.GAME_NET,headerMap,bodyMap);
        if(res.equals("yes")){haveLog=true;return true;}
        else{haveLog=false;return false;}
    }
    // 获取并载入玩家和狼的信息
    private void getToLoadUserDataInfo(){
        Map<String,String> bodyMap = new HashMap<String,String>();
        String sessionID = GTools.getContentFromFile(GArea.GAME_USER+"PINKCANDY");
        bodyMap.put("action","getUserAndWolfInfo");
        bodyMap.put("PINKCANDY",sessionID);
        String jsonStr = GTools.postSender(GTools.GAME_NET, null,bodyMap);
        if(jsonStr.equals("no wolf")){
            JOptionPane.showMessageDialog(
                null,
                "似乎没有宠物狼呢......快去领养一只！",
                "没有数据",
                JOptionPane.WARNING_MESSAGE
            );
            return;
        }
        else{
            GTools.saveContentToFile(GArea.GAME_USER+"userdata",jsonStr);
            JSONObject jsonObj = JSON.parseObject(jsonStr);
            userData = JSONObject.parseObject(jsonObj.toString(),new TypeReference<Map<String,String>>(){});
        }
    }
    // 载入宠物狼
    private Wolf loadWolf(){
        Wolf wolf;
        int role = Integer.parseInt(userData.get("role"));
        GTools.setWolfAssets(userData,role);
        // 数字匹配 在此处添加角色类型
        switch(role){
            case GArea.WOLFROLE_ZhouZhou:wolf=new ZhouZhou(userData);return wolf;
            default:break;
        }
        return null;
    }
}
