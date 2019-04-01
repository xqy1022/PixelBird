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
 * 游戏场景类（全屏显示的一块区域）
 * 
 * @author administrator
 * 
 */
public class Stage extends View {

	private final int CANVAS_WIDTH = 288; // 绘制区域的宽度
	private final int CANVAS_HEIGHT = 480;// 绘制区域的高度
	private final int EARTH_HEIGHT = 112; // 地面的高度
	float width_rate = 0,height_rate = 0;// 绘制区全屏显示时缩放比例的宽度比

	// 小鸟的参数
	private int birdX = 0; // 小鸟的x坐标
	private int birdY = 0; // 小鸟的y坐标
	private int birdIndex = 0;// 小鸟图片的索引

	private final int BIRD_JUMP_HEIGHT = 40;// 从起跳开始，可以跳到的最高点
	private final int BIRD_JUMP_TIME = 1200;// 从起跳至落到原位所经过的毫秒数
	private int touchY = 0;// 起跳的坐标（触屏时一瞬间鸟的y坐标）
	private int highY = 0;// 起跳后预计跳到的最高点坐标
	private long nowTick, fromTick, toTick;// 当前时间、起跳时间、预计的落地时间

	private final int BIRD_HEIGHT = 24;// 小鸟的高度
	private final int BIRD_WIDTH = 34; // 小鸟的宽度
	private boolean death; // 小鸟是否死亡（true：死亡 false：活着）

	// 管道的参数
	private Point[] pipe = new Point[3]; // 管道的坐标
	private final int PIPE_WIDTH = 52; // 管道的宽度
	private final int PIPE_HEIGHT = 320;// 管道的高度
	private final int PIPE_INTERVAL_VERTICAL = 110; // 垂直方向两个管道的距离
	private final int PIPE_INTERVAL_HORIZONTAL = 100; // 水平方向两个管道的距离
	private final int SPEED = 5; // 管道移动的速度

	// _gameover的图片参数
	private final int GAMEOVER_WIDTH = 192;
	private final int GAMEOVER_HEIGHT = 42;

	// 积分的参数
	private final int SCORE_WIDTH = 24;
	private final int SCORE_HEIGHT = 36;
	private int score; // 得分
	private int scoreLine ; //得分线（记录管道的右边垂直方向的坐标的）

	// 随机数
	Random random;

	// 构造方法
	public Stage(Context context, AttributeSet attrs) {
		super(context, attrs);
		// 获取手机屏幕的显示信息
		WindowManager windowManger = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		Display display = windowManger.getDefaultDisplay();
		int windowWidth = display.getWidth();
		int windowHeight = display.getHeight();
		width_rate = windowWidth * 1.0f / CANVAS_WIDTH; // 计算绘图区域全屏显示的宽度的缩放比
		height_rate = windowHeight * 1.0f / CANVAS_HEIGHT;
		// 初始化游戏的各种参数
		InitGame();
	}

	// 初始化游戏的参数
	private void InitGame() {
		// 积分清零
		score = 0;
		// 设置小鸟的坐标
		birdX = 100;
		birdY = (CANVAS_HEIGHT - EARTH_HEIGHT) / 2;
		death = false; // 假设小鸟没死

		// 初始化小鸟起跳瞬间的一些值
		fromTick = System.currentTimeMillis();
		toTick = fromTick + BIRD_JUMP_TIME;
		touchY = birdY;
		highY = touchY - BIRD_JUMP_HEIGHT;

		// 设置管道的坐标
		random = new Random();
		int tempX = CANVAS_WIDTH + random.nextInt(100);
		for (int i = 0; i < pipe.length; i++) {
			pipe[i] = new Point();
			pipe[i].x = tempX + (PIPE_WIDTH + PIPE_INTERVAL_HORIZONTAL) * i;
			pipe[i].y = random.nextInt(CANVAS_HEIGHT - EARTH_HEIGHT
					- PIPE_INTERVAL_VERTICAL)
					- PIPE_HEIGHT;
		}
		//初始化得分线（在第一个管道的右侧边）
		scoreLine = pipe[0].x+PIPE_WIDTH;
	}

	// 绘制该场景的方法
	@Override
	protected void onDraw(Canvas canvas) {
		// 设置画布的缩放比例
		canvas.scale(width_rate, height_rate);

		// 判断小鸟是否已经死亡
		if (death) {
			// 绘制游戏结束的画面
			canvas.drawBitmap(GameResources.gameover,
					(CANVAS_WIDTH - GAMEOVER_WIDTH) / 2,
					(CANVAS_HEIGHT - GAMEOVER_HEIGHT) / 2, null);
			return;
		}

		// 绘制管道
		for (int i = 0; i < pipe.length; i++) {
			canvas
					.drawBitmap(GameResources.pipeTop, pipe[i].x, pipe[i].y,
							null);
			canvas.drawBitmap(GameResources.pipeBottom, pipe[i].x, pipe[i].y
					+ PIPE_HEIGHT + PIPE_INTERVAL_VERTICAL, null);
			pipe[i].x -= SPEED;
		}

		// 如果第一个管道的右侧边缘贴到了屏幕的左侧（第一个管道进入了屏幕）
		if (pipe[0].x + PIPE_WIDTH <= 0) {
			// 重新计算下一个管道的x坐标
			pipe[0].x = pipe[pipe.length - 1].x + PIPE_WIDTH
					+ PIPE_INTERVAL_HORIZONTAL;
			pipe[0].y = random.nextInt(CANVAS_HEIGHT - EARTH_HEIGHT
					- PIPE_INTERVAL_VERTICAL)
					- PIPE_HEIGHT;

			// 将最左侧的管道移动到最后（两两交换）
			for (int i = 0; i < pipe.length - 1; i++) {
				Point temp = pipe[i];
				pipe[i] = pipe[i + 1];
				pipe[i + 1] = temp;
			}
		}

		// 绘制地面
		canvas.drawBitmap(GameResources.earth, 0, CANVAS_HEIGHT - EARTH_HEIGHT,
				null);

		// 计算小鸟的坐标
		nowTick = System.currentTimeMillis();// 记录当前的时间
		birdY = getJumpPos(nowTick, fromTick, toTick, touchY, highY);

		// 绘制小鸟
		canvas.drawBitmap(GameResources.bird[birdIndex], birdX, birdY, null);
		birdIndex++;
		if (birdIndex == 4) {
			birdIndex = 0;
		}
		//累加积分
		scoreLine -= SPEED; //得分线业要随着管道一起移动
		if (birdX >= scoreLine) {
			score++;//加分
			scoreLine = pipe[1].x+PIPE_WIDTH;
		}
		// 绘制积分
		String str = score + ""; 
		char[] charScores = str.toCharArray(); 
		for (int i = 0; i < charScores.length; i++) {
			int tempNum = Integer.parseInt(charScores[i]+"");
			//从大图中截取对应的数字图片
			Bitmap tempBitmap = Bitmap.createBitmap(GameResources.number, 0,
					tempNum * SCORE_HEIGHT, SCORE_WIDTH, SCORE_HEIGHT);
			//绘制这个数字
			canvas.drawBitmap(tempBitmap,SCORE_WIDTH*i, 0, null);
		}

		// 判断小鸟是否死亡
		death = isDead();

		// 每秒执行30次
		try {
			Thread.sleep(1000 / 30);
		} catch (Exception e) {

		}

		this.invalidate();// 重新绘制画布
	}

	// 触屏事件
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// 触摸到屏幕之后，初始化小鸟的跳跃瞬间的一些参数
		fromTick = System.currentTimeMillis(); // 起跳时间
		toTick = fromTick + BIRD_JUMP_TIME; // 预计落地时间
		touchY = birdY;// 起跳的y坐标
		highY = touchY - BIRD_JUMP_HEIGHT;// 预计跳到的最高点

		// 小鸟已经死的时候，触摸了屏幕，则开始新游戏
		if (death) {
			// 初始化一些参数
			InitGame();
			// 画面重绘
			this.invalidate();
		}

		return super.onTouchEvent(event);
	}

	/**
	 * @param nowTick
	 *            毫秒，现在的时间
	 * @param fromTick
	 *            毫秒，起跳时间
	 * @param toTick
	 *            毫秒， 预计落地时间
	 * @param fromY
	 *            位置（可是以象素），起跳时 y 位置
	 * @param toY
	 *            位置 （可是以象素），跳到的 y 最高点
	 * @return 返回nowTick时刻的y坐标 （跳到最高点toY时的时刻是fromTick，toTick的中间值）
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

	// 判断小鸟是否死亡的方法
	private boolean isDead() {
		// 小鸟是否碰到了管道
		// 小鸟的矩形区域
		Rect birdRect = new Rect(birdX, birdY, birdX + BIRD_WIDTH, birdY
				+ BIRD_HEIGHT);
		// 循环3组管道，获取每组管道中的上、下两个管道的矩形
		for (int i = 0; i < pipe.length; i++) {
			Rect rect1 = new Rect(pipe[i].x, pipe[i].y, pipe[i].x + PIPE_WIDTH,
					pipe[i].y + PIPE_HEIGHT);
			Rect rect2 = new Rect(pipe[i].x, pipe[i].y + PIPE_HEIGHT
					+ PIPE_INTERVAL_VERTICAL, pipe[i].x + PIPE_WIDTH, pipe[i].y
					+ PIPE_HEIGHT * 2 + PIPE_INTERVAL_VERTICAL);
			// 判断矩形是否发生了交错
			if (birdRect.intersect(rect1) || birdRect.intersect(rect2)) {
				return true;
			}
		}
		// 小鸟碰到的屏幕顶端
		if (birdY <= 0) {
			return true;
		}
		// 小鸟碰到了地面
		if (birdY + BIRD_HEIGHT >= CANVAS_HEIGHT - EARTH_HEIGHT) {
			return true;
		}
		return false;
	}
}
