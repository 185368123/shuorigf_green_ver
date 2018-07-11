package com.wcsmobile.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class CoordinateCurveView extends View{

	public CoordinateCurveView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	public CoordinateCurveView(Context context, AttributeSet attrs) {
		super(context, attrs);

	}
	public CoordinateCurveView(Context context, AttributeSet attrs,int defStyle) {
		super(context, attrs,defStyle);

	}	
	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
/*  		Paint paint = new Paint();  
        paint.setColor(Color.BLUE);  
      //���������С  
        paint.setTextSize(100);  
          
        //�û�����ͼ���ǿ��ĵ�  
        paint.setStyle(Paint.Style.STROKE);  
        //���û������ߵ� ��ϸ�̶�  
        paint.setStrokeWidth(5);  
        //����һ����  
        canvas.drawLine(0, 0, 200, 200, paint);  
          
        //������  
        canvas.drawRect(200, 500, 300, 300, paint);  
          
        //��Բ  
        canvas.drawCircle(200, 200, 100, paint);  
        //�����ַ��� drawText(String text, float x, float y, Paint paint)   
        // y �� ��׼�� ������ �ַ����� �ײ�  
        canvas.drawText("apple", 60, 60, paint);  
        canvas.drawLine(0, 60, 500, 60, paint);  

*/
		Paint paint = new Paint();  
        paint.setColor(Color.BLUE);  
        paint.setStrokeWidth(5);  
        
        canvas.drawLine(0, 0, 0, getHeight(), paint);  
        canvas.drawLine(0, getHeight(), getWidth(), getHeight(), paint);  
        
        paint.setColor(Color.RED);
        canvas.drawLine(0, getHeight(), 20,getHeight()-20 , paint);  
        canvas.drawLine(20,getHeight()-20, 30,getHeight()-10 , paint);  
     
        
		super.onDraw(canvas);
	}

}
