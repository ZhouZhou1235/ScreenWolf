package com.pinkcandy.screenwolf.utils;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.plaf.FontUIResource;

// 全局通用工具
public class GUtil {
    // 最大帧长度
    static public int GAME_maxFrameLength = 1024;
    // 渲染时钟间隔
    static public int GAME_renderTime = 32;
    // 高速循环间隔
    static public int GAME_updateTime = 16;
    // 低速循环间隔
    static public int GAME_slowUpdateTime = 1024;
    // 程序工作地址
    static public String GAME_workPath = System.getProperty("user.dir").replace("\\", "/");
    // 游戏数据位置
    static public String GAME_dataPath = GUtil.GAME_workPath+"/data/";
    // 桌宠数据保存位置
    static public String GAME_savePath = GUtil.GAME_workPath+"/data/save/";
    // 桌宠资源保存位置
    static public String GAME_petsPath = GUtil.GAME_workPath+"/data/pets/";
    // 屏幕大小
    static public Dimension SCREEN_dimension = Toolkit.getDefaultToolkit().getScreenSize();
    // 默认动画播放间隔
    static public int DEFAULT_animationPlaySpeed = 96;
    // 默认窗口大小
    static public Dimension DEFAULT_windowSize = new Dimension(SCREEN_dimension.width/2,SCREEN_dimension.height/2);
    // 默认字体大小
    static public int DEFAULT_textSize = SCREEN_dimension.width/90;
    // 默认字体
    static public Font DEFAULT_font = new Font(Font.DIALOG,Font.PLAIN,DEFAULT_textSize);
    // 默认宠物大小
    static public Dimension DEFAULT_bodySize = new Dimension((int)(SCREEN_dimension.getWidth()/10),(int)(SCREEN_dimension.getWidth()/10));
    // 扫描文件夹内容
    static public String[] scanDir(String path){return (new File(path)).list();}
    // swing 等比例缩放图片标签
    static public ImageIcon scaleImageIcon(ImageIcon icon,int l){
        int w = icon.getIconWidth();
        int h = icon.getIconHeight();
        Image image = icon.getImage();
        Image newImage = image.getScaledInstance(l,(int)l*h/w,Image.SCALE_SMOOTH);
        return new ImageIcon(newImage);
    }
    // swing 清除计时器的所有事件
    static public void clearTimerListeners(Timer timer){
        ActionListener[] listeners = timer.getActionListeners();
        for(ActionListener listener:listeners){
            timer.removeActionListener(listener);
        }
    }
    // swing 使窗体居中
    static public void setWindowCenter(JFrame jFrame){
        Dimension size = jFrame.getSize();
        int x = (SCREEN_dimension.width/2)-(size.width/2);
        int y = (SCREEN_dimension.height/2)-(size.height/2);
        jFrame.setLocation(x,y);
    }
    // 创建文件
    static public int createFile(String path){
        File file = new File(path);
        if(!file.exists()){
        try{
            file.createNewFile();
            return 1;
        }catch(IOException e){e.printStackTrace();}}
        return 0;
    }
    // 读取文件
    static public String readFile(String path){
        try {
            return new String(Files.readAllBytes(Paths.get(path)),"UTF-8");
        }catch(IOException e){
            e.printStackTrace();
            return "";
        }
    }
    // 写入文件
    static public void saveToFile(String path,String content){
        File file = new File(path);
        try {
            if(!file.exists()){file.createNewFile();}
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write(content);
            fileWriter.flush();
            fileWriter.close();
        }catch(IOException e){
            e.printStackTrace();
        }
    }
    // 获取鼠标在屏幕位置
    static public Point getMousePoint(){return MouseInfo.getPointerInfo().getLocation();}
    // 获取两点之间距离
    static public double getDistanse2Point(Point point1,Point point2){
        double x = Math.abs(point1.x-point2.x);
        double y = Math.abs(point1.y-point2.y);
        double distanse = Math.pow(Math.pow(x,2)+Math.pow(y,2),0.5);
        return distanse;
    }
    // 在jar包中指定类 返回一个实例
    static public Object loadObjFromJarByClass(String jarPath,String jarClass,Object... constructorArgs){
        try{
            File jarFile = new File(jarPath);
            URLClassLoader classLoader = new URLClassLoader(new URL[]{jarFile.toURI().toURL()});
            Class<?> loadedClass = classLoader.loadClass(jarClass);
            Class<?>[] paramTypes = new Class<?>[constructorArgs.length];
            for(int i=0;i<constructorArgs.length;i++){
                paramTypes[i] = constructorArgs[i].getClass();
            }
            Constructor<?> constructor = loadedClass.getDeclaredConstructor(paramTypes);
            Object instance = constructor.newInstance(constructorArgs);
            classLoader.close();
            return instance;
        }catch(Exception e){System.out.println(e);return null;}
    }
    // 构建游戏文件结构
    static public void initFileDirs(){
        File dataDir = new File(GAME_dataPath);
        File saveDir = new File(GAME_savePath);
        File petsDir = new File(GAME_petsPath);
        File[] fileList = {dataDir,saveDir,petsDir};
        for(File file:fileList){if(!file.exists()){file.mkdir();}}
    }
    // awt 翻转图像
    static public Image flipImage(Image image){
        BufferedImage bufferedImage = new BufferedImage(
            image.getWidth(null), 
            image.getHeight(null), 
            BufferedImage.TYPE_INT_ARGB
        );
        Graphics2D g2d = bufferedImage.createGraphics();
        g2d.drawImage(image,0, 0,null);
        g2d.dispose();
        AffineTransform tx = AffineTransform.getScaleInstance(-1,1);
        tx.translate(-bufferedImage.getWidth(null), 0);
        AffineTransformOp op = new AffineTransformOp(tx,AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
        return op.filter(bufferedImage, null);
    }
    // 全局字体
	static public void initGlobalFont(Font font){
		FontUIResource fontRes = new FontUIResource(font);
		for(Enumeration<Object> keys = UIManager.getDefaults().keys();keys.hasMoreElements();){
			Object key = keys.nextElement();
			Object value = UIManager.get(key);
			if(value instanceof FontUIResource){
                UIManager.put(key,fontRes);
            }
		}
	}
    // 随机在屏幕上取点
    static public Point getRandomPointOnScreen(){
        int width = SCREEN_dimension.width;
        int height = SCREEN_dimension.height;
        int w = (int)(width*Math.random());
        int h = (int)(height*Math.random());
        return new Point(w,h);
    }
    // 文件夹扫描指定类型文件
    static public String[] scanDir(String path,String extension){
        File dir = new File(path);
        if(!dir.exists()||!dir.isDirectory()){return new String[0];}
        return Arrays.stream(dir.listFiles())
            .filter(file -> file.getName().toLowerCase().endsWith(extension))
            .map(File::getName)
            .toArray(String[]::new);
    }
    // 选择文件复制事件
    public static int copyFilesWithDialog(
        Component parent,String dialogTitle, 
        String fileTypeDesc,
        String fileExtension, 
        String destDir
    ){
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle(dialogTitle);
        fileChooser.setMultiSelectionEnabled(true);
        fileChooser.setFileFilter(new FileNameExtensionFilter(
            String.format("%s (*.%s)",fileTypeDesc,fileExtension), 
            fileExtension
        ));
        int returnValue = fileChooser.showOpenDialog(parent);
        if (returnValue != JFileChooser.APPROVE_OPTION){
            return 0;
        }
        File[] selectedFiles = fileChooser.getSelectedFiles();
        int copiedCount = 0;
        for (File file : selectedFiles) {
            try {
                File destFile = new File(destDir + file.getName());
                if (destFile.exists()) {
                    int overwrite = JOptionPane.showConfirmDialog(
                        parent,
                        String.format("文件 %s 已存在，是否覆盖？", file.getName()),
                        "确认覆盖",
                        JOptionPane.YES_NO_OPTION
                    );
                    if (overwrite != JOptionPane.YES_OPTION) {
                        continue;
                    }
                }
                java.nio.file.Files.copy(
                    file.toPath(), 
                    destFile.toPath(), 
                    java.nio.file.StandardCopyOption.REPLACE_EXISTING
                );
                copiedCount++;
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(
                    parent,
                    String.format("复制文件 %s 失败: %s", file.getName(), ex.getMessage()),
                    "错误",
                    JOptionPane.ERROR_MESSAGE
                );
            }
        }
        if (copiedCount > 0) {
            JOptionPane.showMessageDialog(
                parent,
                String.format("成功添加 %d 个文件", copiedCount),
                "操作完成",
                JOptionPane.INFORMATION_MESSAGE
            );
        }
        return copiedCount;
    }
    // 创建图标按钮
    public static JButton createIconButton(String path,String tooltip,int size){
        ImageIcon icon = ResourceReader.getResourceAsImageIcon(path);
        icon = GUtil.scaleImageIcon(icon,size);
        JButton button = new JButton(icon);
        button.setToolTipText(tooltip);
        button.setContentAreaFilled(false);
        button.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }
    // 创建图标按钮
    public static JButton createIconButton(ImageIcon icon,String tooltip,int size){
        icon = GUtil.scaleImageIcon(icon,size);
        JButton button = new JButton(icon);
        button.setToolTipText(tooltip);
        button.setContentAreaFilled(false);
        button.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }
    // 长文本分割算法
    public static String[] splitTextIntoMessages(String text){
        String[] sentences = text.split("(?<=[.!?。！？])");
        List<String> messages = new ArrayList<>();
        StringBuilder currentMsg = new StringBuilder();
        for(String sentence:sentences){
            if(currentMsg.length() + sentence.length()>50){
                messages.add(currentMsg.toString().trim());
                currentMsg = new StringBuilder();
            }
            currentMsg.append(sentence);
        }
        if(currentMsg.length()>0){
            messages.add(currentMsg.toString().trim());
        }
        return messages.toArray(new String[0]);
    }
    // 用字节流创建一张ImageIcon图片
    public static ImageIcon createImageIconFromBytes(byte[] imageData){
        if(imageData == null||imageData.length==0){return new ImageIcon();}
        try{
            return new ImageIcon(ImageIO.read(new ByteArrayInputStream(imageData)));
        }catch(IOException e){
            e.printStackTrace();
            return new ImageIcon();
        }
    }
}
