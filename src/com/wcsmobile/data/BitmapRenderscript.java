package com.wcsmobile.data;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.renderscript.Allocation;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;

public class BitmapRenderscript {

	public static Drawable OpacityCcaleZoom(Bitmap bitmap)
	{
		Bitmap BitmapOrg = bitmap;    
		int width = BitmapOrg.getWidth();    
		int height = BitmapOrg.getHeight();    
		int newWidth = 50;    
		int newHeight = 50;    

		float scaleWidth = ((float) newWidth) / width;    
		float scaleHeight = ((float) newHeight) / height;    

		Matrix matrix = new Matrix();    
		matrix.postScale(scaleWidth, scaleHeight);    
		// if you want to rotate the Bitmap     
		// matrix.postRotate(45);     
		Bitmap resizedBitmap = Bitmap.createBitmap(BitmapOrg, 0, 0, width,    
				height, matrix, true);    
		return new BitmapDrawable(resizedBitmap);  
	}
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
	public static Drawable Blur(Bitmap bitmap,Context context)
	{
		float radius = 20;

		Bitmap overlay = Bitmap.createBitmap((int) (bitmap.getWidth()-50),
				(int) (bitmap.getHeight()-50), Bitmap.Config.ARGB_8888);

		Canvas canvas = new Canvas(overlay);

		canvas.translate(0, 0);
		// canvas.drawBitmap(bitmap, 0, 0, null);
		canvas.drawBitmap(bitmap, new Rect(25,25,overlay.getWidth(),overlay.getWidth()),new Rect(0,0,overlay.getWidth(),overlay.getWidth()), null);
		RenderScript rs = RenderScript.create(context);

		Allocation overlayAlloc = Allocation.createFromBitmap(
				rs, overlay);

		ScriptIntrinsicBlur blur = ScriptIntrinsicBlur.create(
				rs, overlayAlloc.getElement());

		blur.setInput(overlayAlloc);

		blur.setRadius(radius);

		blur.forEach(overlayAlloc);

		overlayAlloc.copyTo(overlay);


		rs.destroy();
		return new BitmapDrawable(overlay);  
	}

}
