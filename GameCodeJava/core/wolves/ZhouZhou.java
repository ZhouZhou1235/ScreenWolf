package pinkcandy.screenwolf.core.wolves;

import java.awt.Point;
import java.util.Map;
import java.util.TimerTask;

import pinkcandy.screenwolf.GArea;
import pinkcandy.screenwolf.GTools;
import pinkcandy.screenwolf.core.Wolf;

// 小蓝狗
public class ZhouZhou extends Wolf {
    public ZhouZhou(Map<String,String> theData){
        super(theData);
    }
    // 小蓝狗的活动事件
    @Override public void startActiveEvent(){
        super.startActiveEvent();
        TimerTask dragMouse = new TimerTask(){
            @Override public void run(){
                if(GTools.randomTodo(10)){dragMouse();}
            }
        };
        actionTimer.scheduleAtFixedRate(dragMouse,GArea.WOLF_maxStateNum,GArea.WOLF_period);
    }
    // 小蓝狗拖拽鼠标
    private void dragMouse(){
        if(!preDoAction()){return;}
        Point pos = GTools.getRandomPoint();
        wolfMove(pos);
        TimerTask task = new TimerTask(){
            @Override public void run(){
                if(!moving){stopActiveEvent();startActiveEvent();}
                robot.mouseMove(getWolfLocation().x+bodyWidth/2,getWolfLocation().y);
            }
        };
        actionTimer.scheduleAtFixedRate(task,0,GArea.GAME_FREQUENY);
    }
}
