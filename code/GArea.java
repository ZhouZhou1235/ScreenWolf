package code;

/**
 * GArea
 * 游戏固定全局变量
 */
public interface GArea {
    // 基本
    String GAME_TITLE = "屏幕有狼";
    String GAME_ICON = "assets/images/ui/icon.png";
    String GAME_LOGO = "assets/images/ui/logo.png";
    int GAME_FREQUENY = 32; // 游戏时钟常量
    // 宠物狼动作数
    int ACT_default = 1; // 默认
    int ACT_press = 2; // 被按下
    int ACT_move = 3; // 移动
    int ACT_focus = 4; // 被聚焦
    int ACT_touch = 5; // 被抚摸
    int ACT_rest = 6; // 休息
    // 宠物狼状态数
    int STATE_normal = 1; // 正常
    int STATE_rest = 2; // 休息
    int STATE_doingAction = 3; // 动作进行中
}
