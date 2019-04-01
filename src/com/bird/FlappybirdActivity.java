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
        //�������е�˲�䣬���ظ���ͼƬ��Դ
        initResource();
    }
    
    /**
     * ��ʼ����Դ������ͼƬ�ȣ�
     */
    private void initResource(){
    	//��ȡ�ֻ�����ʾ��ص�һЩ����
    	Resources res = this.getResources();
    	//λͼ�����е�ѡ�����
    	BitmapFactory.Options opts = new BitmapFactory.Options();
    	//�������������ֻ��ķֱ���
    	opts.inDensity = res.getDisplayMetrics().densityDpi;
    	opts.inTargetDensity = res.getDisplayMetrics().densityDpi;
    	
    	//ͨ��λͼ��������ͼƬ
    	//���ص���ͼƬ
    	GameResources.earth = BitmapFactory.decodeResource(res, R.drawable.earth,opts);
    	//����С���ͼƬ
    	GameResources.bird[0] = BitmapFactory.decodeResource(res, R.drawable.bird01,opts);
    	GameResources.bird[1] = BitmapFactory.decodeResource(res, R.drawable.bird02,opts);
    	GameResources.bird[2] = BitmapFactory.decodeResource(res, R.drawable.bird03,opts);
    	GameResources.bird[3] = BitmapFactory.decodeResource(res, R.drawable.bird02,opts);
    	//���عܵ���ͼƬ
    	GameResources.pipeTop = BitmapFactory.decodeResource(res, R.drawable.p1,opts);
    	GameResources.pipeBottom = BitmapFactory.decodeResource(res, R.drawable.p2,opts);
    	//_gameover
    	GameResources.gameover= BitmapFactory.decodeResource(res, R.drawable.gameover,opts);
    	//����
    	GameResources.number= BitmapFactory.decodeResource(res, R.drawable.num,opts);    	
    }
}

