package km.view;

import android.content.Context;
import android.graphics.*;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.ImageView;

/**Created by wuzhengu on 2018/7/14 0014 */
public class RoundImageView extends ImageView
{
	Paint mPaint;
	RectF mRectF;
	Xfermode mXfermode;
	
	public RoundImageView(Context context){
		super(context);
	}
	
	public RoundImageView(Context context, @Nullable AttributeSet attrs){
		super(context, attrs);
	}
	
	void preDraw(Canvas canvas, boolean padding){
		if(mPaint==null){
			mPaint=new Paint(Paint.ANTI_ALIAS_FLAG);
			mRectF=new RectF();
			mXfermode=new PorterDuffXfermode(PorterDuff.Mode.SRC_IN);
		}
		mRectF.set(0, 0, getWidth(), getHeight());
		if(padding){
			mRectF.left+=getPaddingLeft();
			mRectF.top+=getPaddingTop();
			mRectF.right-=getPaddingRight();
			mRectF.bottom-=getPaddingBottom();
		}
		canvas.saveLayer(mRectF.left, mRectF.top, mRectF.right, mRectF.bottom, null, Canvas.ALL_SAVE_FLAG);
		mPaint.setColor(Color.GRAY);
		mPaint.setXfermode(null);
		canvas.drawOval(mRectF, mPaint);
		mPaint.setXfermode(mXfermode);
		canvas.saveLayer(mRectF.left, mRectF.top, mRectF.right, mRectF.bottom, mPaint, Canvas.ALL_SAVE_FLAG);
	}
	
	@Override
	public void draw(Canvas canvas){
		if(getBackground()!=null || getDrawable()!=null) preDraw(canvas, false);
		super.draw(canvas);
	}
	
	@Override
	protected void onDraw(Canvas canvas){
		if(getDrawable()!=null) preDraw(canvas, true);
		super.onDraw(canvas);
	}
}
