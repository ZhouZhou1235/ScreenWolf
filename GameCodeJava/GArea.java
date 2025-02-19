package pinkcandy.screenwolf;

/**
 * 全局常量
 * 全局工具见 GTools
 */
public interface GArea {
    // 基本
    String GAME_TITLE = "屏幕有狼"; // 游戏名
    String GAME_ICON = "assets/images/ui/icon.png"; // 小图标
    String GAME_LOGO = "assets/images/ui/logo.png"; // 标志LOGO
    String GAME_WOLVESFRAMES = "assets/json/wolvesFrames.json"; // 狼帧动画资源
    String GAME_FOODS = "assets/json/foods.json"; // 食物资源
    String GAME_KEYVALUES = "assets/json/staticKeyValues.json"; // 静态键值对配置
    String GAME_USER = "user/"; // 玩家配置地址
    int GAME_FREQUENY = 24; // 渲染频率
    int GAME_SAVE = 10000; // 数据定时同步
    int GAME_ANIMATION = 128; // 播放机帧频率 值不要低于渲染频率
    // 狼动作数
    int ACT_default = 1; // 默认
    int ACT_press = 2; // 被按下
    int ACT_move = 3; // 移动
    int ACT_focus = 4; // 被聚焦
    int ACT_touch = 5; // 被抚摸
    int ACT_rest = 6; // 休息
    int ACT_clean = 7; // 清洗
    int ACT_sad = 8; // 难过
    int ACT_dirty = 9; // 身体脏
    int ACT_hungry = 10; // 肚子饿
    int ACT_tired = 11; // 累了
    int ACT_low = 12; // 数值低时被聚焦
    int ACT_eat = 13; // 吃
    int ACT_focusToy = 14; // 聚焦玩具
    int ACT_playBubble = 15; //吹泡泡
    int ACT_gameSuccess = 16; // 游戏成功
    int ACT_gameFailed = 17; // 游戏失败
    int ACT_eatFull = 18; // 吃饱了
    int ACT_followMouse = 19; // 跟随鼠标的动作
    int ACT_holdMouse = 20; // 抱紧鼠标
    // 狼状态数
    int STATE_normal = 1; // 正常
    int STATE_rest = 2; // 休息
    int STATE_eating = 3; // 正在吃
    int STATE_cleaning = 4; // 正在清洗身体
    int STATE_playing = 5; // 玩耍
    // 狼默认值
    int WOLF_scaleNum = 8; // 相对屏幕缩放数
    int WOLF_speed = 5; // 速度
    int WOLF_reachTouch = 100; // 抚摸阈值
    int WOLF_duration = 3000; // 持续时间
    int WOLF_period = 3000; // 自动任务时间
    int WOLF_lifePeriod = 60000; // 身体运作
    int WOLF_basicFavor = 50; // 好感度水平值
    int WOLF_basicFood = 90; // 饱食度水平值
    int WOLF_addNum = 2; // 值增加默认数
    int WOLF_lowStateNum = 25; // 低状态
    int WOLF_maxStateNum = 100; // 数值最大值
    // 狼角色类型
    int WOLFROLE_ZhouZhou = 1; // 小蓝狗
    // 鼠标模式
    int MOUSE_normal = 1; // 默认
    int MOUSE_clean = 2; // 清理
    int MOUSE_food = 3; // 喂食
    int MOUSE_play = 4; // 玩耍
    // 静态贴图
    String MOUSETIP_clean = "assets/images/ui/mousetip/clean.png";
    String MOUSTIP_food = "assets/images/ui/mousetip/food.png";
    String MOUSETIP_play = "assets/images/ui/mousetip/play.png";
    String TOY_BONE = "assets/images/toys/bone.png";
    String TOY_BUBBLE = "assets/images/toys/bubble.png";
}
