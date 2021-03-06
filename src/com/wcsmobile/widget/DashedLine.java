package com.wcsmobile.widget;

import com.wcsmobile.R;
import com.wcsmobile.data.BitmapHelper;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.util.AttributeSet;
import android.view.View;

public class DashedLine extends View {
	private Paint paint = null;
	private Path path = null;
	private PathEffect pe = null;

	public DashedLine(Context paramContext) {
		this(paramContext, null);
	}

	public DashedLine(Context paramContext, AttributeSet paramAttributeSet) {
		super(paramContext, paramAttributeSet);

		TypedArray a = paramContext.obtainStyledAttributes(paramAttributeSet, R.styleable.dashedline);

		int lineColor = a.getColor(R.styleable.dashedline_lineColor, 0XFF000000);
		a.recycle();
		this.paint = new Paint();
		this.path = new Path();
		this.paint.setStyle(Paint.Style. STROKE);
		this.paint.setColor(lineColor);
		this.paint.setAntiAlias( true);
		this.paint.setStrokeWidth(BitmapHelper.dip2px(getContext(), 2.0F));
		float[] arrayOfFloat = new float[4];
		arrayOfFloat[0] = BitmapHelper.dip2px(getContext(), 4.0F);
		arrayOfFloat[1] = BitmapHelper.dip2px(getContext(), 2.0F);
		arrayOfFloat[2] = BitmapHelper.dip2px(getContext(), 4.0F);
		arrayOfFloat[3] = BitmapHelper.dip2px(getContext(), 2.0F);
		this.pe = new DashPathEffect(arrayOfFloat, BitmapHelper.dip2px(getContext(), 1.0F));
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		this.path.moveTo(0.0F, 0.0F);
		this.path.lineTo(getMeasuredWidth(), 0.0F);
		this.paint.setPathEffect( this.pe);
		canvas.drawPath( this.path, this.paint);
	}

}
