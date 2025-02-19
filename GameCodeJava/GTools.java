package pinkcandy.screenwolf;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.ImageIcon;
import javax.swing.UIManager;
import javax.swing.plaf.FontUIResource;

import org.apache.http.HttpEntity;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;

/**
 * 全局工具
 */
public class GTools {
    public static int SCREEN_WIDTH; // 屏幕宽
    public static int SCREEN_HEIGHT; // 屏幕高
    public static int WOLF_scaleNum; // 宠物狼相对屏幕缩放倍数
    public static int BODY_WIDTH; // 宠物狼宽
    public static int BODY_HEIGHT; // 宠物狼高
    public static int GAME_FONTSIZE; // 全局字体默认大小
    public static String GAME_NET; // 服务器地址
    public GTools(){
        String jsonStr = getContentFromFile(GArea.GAME_KEYVALUES);
        Map<String,String> staticKeyValuesMap = decodeJsonObject(jsonStr);
        WOLF_scaleNum = Integer.parseInt(staticKeyValuesMap.get("WOLF_scaleNum"));
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        SCREEN_WIDTH = (int)screenSize.getWidth();
        SCREEN_HEIGHT = (int)screenSize.getHeight();
        BODY_WIDTH = SCREEN_WIDTH/WOLF_scaleNum;
        BODY_HEIGHT = SCREEN_WIDTH/WOLF_scaleNum;
        GAME_NET = staticKeyValuesMap.get("GAME_NET");
        GAME_FONTSIZE = Integer.parseInt(staticKeyValuesMap.get("GAME_FONTSIZE"));
    }
    // 概率触发
    public static boolean randomTodo(int num){
        int x = (int)(Math.random()*num)+1;
        return x==1?true:false;
    }
    // 获取一个屏幕随机点
    public static Point getRandomPoint(){
        int x = (int)(Math.random()*(SCREEN_WIDTH-BODY_WIDTH));
        int y = (int)(Math.random()*(SCREEN_HEIGHT-BODY_HEIGHT));
        Point pos = new Point(x,y);
        return pos;
    }
    // 获取超出屏幕顶端的随机点
    public static Point getRandomPointOverScreenUp(int height){
        int x = (int)(Math.random()*(SCREEN_WIDTH-BODY_WIDTH));
        int y = 0-height*2;
        Point pos = new Point(x,y);
        return pos;
    }
    // 等比例缩放图片标签
    public static ImageIcon scaleImageIcon(ImageIcon icon,int l){
        int w = icon.getIconWidth();
        int h = icon.getIconHeight();
        Image image = icon.getImage();
        Image newImage = image.getScaledInstance(l,(int)l*h/w,Image.SCALE_DEFAULT);
        return new ImageIcon(newImage);
    }
    // 设置全局字体
    public static void setGlobalFont(Font font){
		FontUIResource fontRes = new FontUIResource(font);
		for(Enumeration<Object> keys = UIManager.getDefaults().keys();keys.hasMoreElements();){
			Object key = keys.nextElement();
			Object value = UIManager.get(key);
			if(value instanceof FontUIResource){
                UIManager.put(key,fontRes);
            }
		}
	}
    // post发送方法
    public static String postSender(String url,Map<String,String> headerMap,Map<String,String> bodyMap){
        HttpPost httpPost = new HttpPost(url);
        try {
            List<BasicNameValuePair> list = new ArrayList<BasicNameValuePair>();
            if(bodyMap!=null){for(String str:bodyMap.keySet()){list.add(new BasicNameValuePair(str,bodyMap.get(str)));}}
            httpPost.setEntity(new UrlEncodedFormEntity(list,"UTF-8"));
            if(headerMap!=null){for(String str:headerMap.keySet()){httpPost.addHeader(str,headerMap.get(str));}}
            CloseableHttpClient client = HttpClients.createDefault();
            CloseableHttpResponse response = client.execute(httpPost);
            HttpEntity entity = response.getEntity();
            String result = EntityUtils.toString(entity, "UTF-8");
            client.close();
            return result;
        }catch(Exception e){e.printStackTrace();return "error";}
    }
    // 保存信息到文件
    public static void saveContentToFile(String fileName,String content){
        BufferedWriter writer = null;
        File file = new File(fileName);
        if(!file.exists()){
            try{file.createNewFile();}
            catch(IOException e){e.printStackTrace();}
        }
        try{
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file,false), "UTF-8"));
            writer.write(content);
        }
        catch(IOException e){e.printStackTrace();}
        finally{
            try{writer.close();}
            catch(IOException e){e.printStackTrace();}
        }
    }
    // 从文件读取内容
    public static String getContentFromFile(String fileName){
		BufferedReader reader = null;
		String laststr = "";
		try{
			FileInputStream fileInputStream = new FileInputStream(fileName);
			InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream,"UTF-8");
			reader = new BufferedReader(inputStreamReader);
			String tempString = null;
			while((tempString=reader.readLine())!=null){laststr+=tempString;}
			reader.close();
		}
        catch(IOException e){e.printStackTrace();}
        finally{
			if(reader!=null){
				try{reader.close();}
                catch(IOException e){e.printStackTrace();}
			}
		}
		return laststr;
    }
    // 设置狼贴图资源
    public static Map<String,String> setWolfAssets(Map<String,String> userData,int role){
        String key = String.valueOf(role);
        String jsonStr = GTools.getContentFromFile(GArea.GAME_WOLVESFRAMES);        
        Map<String,String> map = decodeJsonObject(jsonStr);
        JSONArray actions = JSON.parseArray(map.get(key));
        for(int i=0;i<actions.size();i++){
            String theKey = "";
            String theValue = "";
            JSONObject act = actions.getJSONObject(i);
            Set<String> keys = act.keySet();
            for(String k:keys){theKey=k;theValue=(String)act.get(k);}
            userData.put(theKey,theValue);
        }
        return userData;
    }
    // 解析简单json对象为map
    public static Map<String,String> decodeJsonObject(String jsonStr){
        JSONObject jsonObj = JSON.parseObject(jsonStr);
        Map<String,String> map = JSONObject.parseObject(
            jsonObj.toString(),
            new TypeReference<Map<String,String>>(){}
        );
        return map;
    }
    // 计算两点距离
    public static double get2PosLength(Point pos1,Point pos2){
        double a = Math.pow(pos1.x-pos2.x,2);
        double b = Math.pow(pos1.y-pos2.y,2);
        double c = Math.sqrt(a+b);
        return c;
    }
}
