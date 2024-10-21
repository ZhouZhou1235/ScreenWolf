package code;

import java.awt.Dimension;
import java.awt.Toolkit;

/**
 * GAreaClass
 * 游戏全局类
 */
public class GAreaClass {
    public GAreaClass(){initScreen();}
    public int SCREEN_WIDTH;
    public int SCREEN_HEIGHT;
    public int BODY_WIDTH;
    public int BODY_HEIGHT;
    // 动态计算屏幕大小并初始化
    void initScreen(){
        int scaleNum = 7;
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        SCREEN_WIDTH = (int)screenSize.getWidth();
        SCREEN_HEIGHT = (int)screenSize.getHeight();
        BODY_WIDTH = SCREEN_WIDTH/scaleNum;
        BODY_HEIGHT = SCREEN_WIDTH/scaleNum;
    }
}
