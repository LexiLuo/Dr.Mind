package ui;

import android.graphics.Canvas;
import android.graphics.Paint;

/*
 * @auther:Liu 
 * @date:2016.7.8
 * @description:�������ӽڵ�����ߣ�sinΪ���壬���һ��ֱ��
 */

public class SinGraph {

	private Paint paint;
	private Canvas canvas;

	public SinGraph(Canvas c) {
		paint = new Paint();
		this.canvas = c;
	}

	public void drawSinGraph() {
		for (int i = 0; i < 200; i++) {
			canvas.drawPoint((float) i, (float) Math.sin(i*180/Math.PI), paint);
		}
	}

}
