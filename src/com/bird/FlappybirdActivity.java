package com.bird;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.os.Bundle;

public class FlappybirdActivity extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        //程序运行的瞬间，加载各种图片资源
        initResource();
    }
    
    /**
     * 初始化资源（加载图片等）
     */
    private void initResource(){
    	//获取手机的显示相关的一些参数
    	Resources res = this.getResources();
    	//位图工厂中的选项参数
    	BitmapFactory.Options opts = new BitmapFactory.Options();
    	//给参数中设置手机的分辨率
    	opts.inDensity = res.getDisplayMetrics().densityDpi;
    	opts.inTargetDensity = res.getDisplayMetrics().densityDpi;
    	
    	//通过位图工厂加载图片
    	//加载地面图片
    	GameResources.earth = BitmapFactory.decodeResource(res, R.drawable.earth,opts);
    	//加载小鸟的图片
    	GameResources.bird[0] = BitmapFactory.decodeResource(res, R.drawable.bird01,opts);
    	GameResources.bird[1] = BitmapFactory.decodeResource(res, R.drawable.bird02,opts);
    	GameResources.bird[2] = BitmapFactory.decodeResource(res, R.drawable.bird03,opts);
    	GameResources.bird[3] = BitmapFactory.decodeResource(res, R.drawable.bird02,opts);
    	//加载管道的图片
    	GameResources.pipeTop = BitmapFactory.decodeResource(res, R.drawable.p1,opts);
    	GameResources.pipeBottom = BitmapFactory.decodeResource(res, R.drawable.p2,opts);
    	//_gameover
    	GameResources.gameover= BitmapFactory.decodeResource(res, R.drawable.gameover,opts);
    	//数字
    	GameResources.number= BitmapFactory.decodeResource(res, R.drawable.num,opts);    	
    }
}

