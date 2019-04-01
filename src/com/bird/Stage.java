package com.bird;

import java.util.Random;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

/**
 * ��Ϸ�����ࣨȫ����ʾ��һ������
 * 
 * @author administrator
 * 
 */
public class Stage extends View {

	private final int CANVAS_WIDTH = 288; // ��������Ŀ��
	private final int CANVAS_HEIGHT = 480;// ��������ĸ߶�
	private final int EARTH_HEIGHT = 112; // ����ĸ߶�
	float width_rate = 0,height_rate = 0;// ������ȫ����ʾʱ���ű����Ŀ�ȱ�

	// С��Ĳ���
	private int birdX = 0; // С���x����
	private int birdY = 0; // С���y����
	private int birdIndex = 0;// С��ͼƬ������

	private final int BIRD_JUMP_HEIGHT = 40;// ��������ʼ��������������ߵ�
	private final int BIRD_JUMP_TIME = 1200;// ���������䵽ԭλ�������ĺ�����
	private int touchY = 0;// ���������꣨����ʱһ˲�����y���꣩
	private int highY = 0;// ������Ԥ����������ߵ�����
	private long nowTick, fromTick, toTick;// ��ǰʱ�䡢����ʱ�䡢Ԥ�Ƶ����ʱ��

	private final int BIRD_HEIGHT = 24;// С��ĸ߶�
	private final int BIRD_WIDTH = 34; // С��Ŀ��
	private boolean death; // С���Ƿ�������true������ false�����ţ�

	// �ܵ��Ĳ���
	private Point[] pipe = new Point[3]; // �ܵ�������
	private final int PIPE_WIDTH = 52; // �ܵ��Ŀ��
	private final int PIPE_HEIGHT = 320;// �ܵ��ĸ߶�
	private final int PIPE_INTERVAL_VERTICAL = 110; // ��ֱ���������ܵ��ľ���
	private final int PIPE_INTERVAL_HORIZONTAL = 100; // ˮƽ���������ܵ��ľ���
	private final int SPEED = 5; // �ܵ��ƶ����ٶ�

	// _gameover��ͼƬ����
	private final int GAMEOVER_WIDTH = 192;
	private final int GAMEOVER_HEIGHT = 42;

	// ���ֵĲ���
	private final int SCORE_WIDTH = 24;
	private final int SCORE_HEIGHT = 36;
	private int score; // �÷�
	private int scoreLine ; //�÷��ߣ���¼�ܵ����ұߴ�ֱ���������ģ�

	// �����
	Random random;

	// ���췽��
	public Stage(Context context, AttributeSet attrs) {
		super(context, attrs);
		// ��ȡ�ֻ���Ļ����ʾ��Ϣ
		WindowManager windowManger = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		Display display = windowManger.getDefaultDisplay();
		int windowWidth = display.getWidth();
		int windowHeight = display.getHeight();
		width_rate = windowWidth * 1.0f / CANVAS_WIDTH; // �����ͼ����ȫ����ʾ�Ŀ�ȵ����ű�
		height_rate = windowHeight * 1.0f / CANVAS_HEIGHT;
		// ��ʼ����Ϸ�ĸ��ֲ���
		InitGame();
	}

	// ��ʼ����Ϸ�Ĳ���
	private void InitGame() {
		// ��������
		score = 0;
		// ����С�������
		birdX = 100;
		birdY = (CANVAS_HEIGHT - EARTH_HEIGHT) / 2;
		death = false; // ����С��û��

		// ��ʼ��С������˲���һЩֵ
		fromTick = System.currentTimeMillis();
		toTick = fromTick + BIRD_JUMP_TIME;
		touchY = birdY;
		highY = touchY - BIRD_JUMP_HEIGHT;

		// ���ùܵ�������
		random = new Random();
		int tempX = CANVAS_WIDTH + random.nextInt(100);
		for (int i = 0; i < pipe.length; i++) {
			pipe[i] = new Point();
			pipe[i].x = tempX + (PIPE_WIDTH + PIPE_INTERVAL_HORIZONTAL) * i;
			pipe[i].y = random.nextInt(CANVAS_HEIGHT - EARTH_HEIGHT
					- PIPE_INTERVAL_VERTICAL)
					- PIPE_HEIGHT;
		}
		//��ʼ���÷��ߣ��ڵ�һ���ܵ����Ҳ�ߣ�
		scoreLine = pipe[0].x+PIPE_WIDTH;
	}

	// ���Ƹó����ķ���
	@Override
	protected void onDraw(Canvas canvas) {
		// ���û��������ű���
		canvas.scale(width_rate, height_rate);

		// �ж�С���Ƿ��Ѿ�����
		if (death) {
			// ������Ϸ�����Ļ���
			canvas.drawBitmap(GameResources.gameover,
					(CANVAS_WIDTH - GAMEOVER_WIDTH) / 2,
					(CANVAS_HEIGHT - GAMEOVER_HEIGHT) / 2, null);
			return;
		}

		// ���ƹܵ�
		for (int i = 0; i < pipe.length; i++) {
			canvas
					.drawBitmap(GameResources.pipeTop, pipe[i].x, pipe[i].y,
							null);
			canvas.drawBitmap(GameResources.pipeBottom, pipe[i].x, pipe[i].y
					+ PIPE_HEIGHT + PIPE_INTERVAL_VERTICAL, null);
			pipe[i].x -= SPEED;
		}

		// �����һ���ܵ����Ҳ��Ե��������Ļ����ࣨ��һ���ܵ���������Ļ��
		if (pipe[0].x + PIPE_WIDTH <= 0) {
			// ���¼�����һ���ܵ���x����
			pipe[0].x = pipe[pipe.length - 1].x + PIPE_WIDTH
					+ PIPE_INTERVAL_HORIZONTAL;
			pipe[0].y = random.nextInt(CANVAS_HEIGHT - EARTH_HEIGHT
					- PIPE_INTERVAL_VERTICAL)
					- PIPE_HEIGHT;

			// �������Ĺܵ��ƶ����������������
			for (int i = 0; i < pipe.length - 1; i++) {
				Point temp = pipe[i];
				pipe[i] = pipe[i + 1];
				pipe[i + 1] = temp;
			}
		}

		// ���Ƶ���
		canvas.drawBitmap(GameResources.earth, 0, CANVAS_HEIGHT - EARTH_HEIGHT,
				null);

		// ����С�������
		nowTick = System.currentTimeMillis();// ��¼��ǰ��ʱ��
		birdY = getJumpPos(nowTick, fromTick, toTick, touchY, highY);

		// ����С��
		canvas.drawBitmap(GameResources.bird[birdIndex], birdX, birdY, null);
		birdIndex++;
		if (birdIndex == 4) {
			birdIndex = 0;
		}
		//�ۼӻ���
		scoreLine -= SPEED; //�÷���ҵҪ���Źܵ�һ���ƶ�
		if (birdX >= scoreLine) {
			score++;//�ӷ�
			scoreLine = pipe[1].x+PIPE_WIDTH;
		}
		// ���ƻ���
		String str = score + ""; 
		char[] charScores = str.toCharArray(); 
		for (int i = 0; i < charScores.length; i++) {
			int tempNum = Integer.parseInt(charScores[i]+"");
			//�Ӵ�ͼ�н�ȡ��Ӧ������ͼƬ
			Bitmap tempBitmap = Bitmap.createBitmap(GameResources.number, 0,
					tempNum * SCORE_HEIGHT, SCORE_WIDTH, SCORE_HEIGHT);
			//�����������
			canvas.drawBitmap(tempBitmap,SCORE_WIDTH*i, 0, null);
		}

		// �ж�С���Ƿ�����
		death = isDead();

		// ÿ��ִ��30��
		try {
			Thread.sleep(1000 / 30);
		} catch (Exception e) {

		}

		this.invalidate();// ���»��ƻ���
	}

	// �����¼�
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// ��������Ļ֮�󣬳�ʼ��С�����Ծ˲���һЩ����
		fromTick = System.currentTimeMillis(); // ����ʱ��
		toTick = fromTick + BIRD_JUMP_TIME; // Ԥ�����ʱ��
		touchY = birdY;// ������y����
		highY = touchY - BIRD_JUMP_HEIGHT;// Ԥ����������ߵ�

		// С���Ѿ�����ʱ�򣬴�������Ļ����ʼ����Ϸ
		if (death) {
			// ��ʼ��һЩ����
			InitGame();
			// �����ػ�
			this.invalidate();
		}

		return super.onTouchEvent(event);
	}

	/**
	 * @param nowTick
	 *            ���룬���ڵ�ʱ��
	 * @param fromTick
	 *            ���룬����ʱ��
	 * @param toTick
	 *            ���룬 Ԥ�����ʱ��
	 * @param fromY
	 *            λ�ã����������أ�������ʱ y λ��
	 * @param toY
	 *            λ�� �����������أ��������� y ��ߵ�
	 * @return ����nowTickʱ�̵�y���� ��������ߵ�toYʱ��ʱ����fromTick��toTick���м�ֵ��
	 */
	private int getJumpPos(long nowTick, long fromTick, long toTick, int fromY,
			int toY) {
		if (nowTick <= fromTick)
			return fromY;
		float m = (float) (nowTick - fromTick);
		float y = (float) (toY - fromY);
		float n = (float) (toTick - fromTick);
		float speed = y / (n / 2) - (1 + n / 2) * -8 * y / n / n / 2;
		float g = -8 * y / n / n;
		float pos = speed * m + (1 + m) * m * g / 2;
		return (int) (pos + fromY);
	}

	// �ж�С���Ƿ������ķ���
	private boolean isDead() {
		// С���Ƿ������˹ܵ�
		// С��ľ�������
		Rect birdRect = new Rect(birdX, birdY, birdX + BIRD_WIDTH, birdY
				+ BIRD_HEIGHT);
		// ѭ��3��ܵ�����ȡÿ��ܵ��е��ϡ��������ܵ��ľ���
		for (int i = 0; i < pipe.length; i++) {
			Rect rect1 = new Rect(pipe[i].x, pipe[i].y, pipe[i].x + PIPE_WIDTH,
					pipe[i].y + PIPE_HEIGHT);
			Rect rect2 = new Rect(pipe[i].x, pipe[i].y + PIPE_HEIGHT
					+ PIPE_INTERVAL_VERTICAL, pipe[i].x + PIPE_WIDTH, pipe[i].y
					+ PIPE_HEIGHT * 2 + PIPE_INTERVAL_VERTICAL);
			// �жϾ����Ƿ����˽���
			if (birdRect.intersect(rect1) || birdRect.intersect(rect2)) {
				return true;
			}
		}
		// С����������Ļ����
		if (birdY <= 0) {
			return true;
		}
		// С�������˵���
		if (birdY + BIRD_HEIGHT >= CANVAS_HEIGHT - EARTH_HEIGHT) {
			return true;
		}
		return false;
	}
}
