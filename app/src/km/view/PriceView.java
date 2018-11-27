package km.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.widget.LinearLayout;
import android.widget.TextView;
import kameng.yimai.R;

/** Created by wuzhengu on 2018/10/31 0031 */
public class PriceView extends LinearLayout
{
	final TextView[] mViews=new TextView[2];
	final LayoutParams[] mParams=new LayoutParams[mViews.length];
	
	public PriceView(Context context){
		this(context, null);
	}
	
	public PriceView(Context context, @Nullable AttributeSet attrs){
		this(context, attrs, 0);
	}
	
	public PriceView(Context context, @Nullable AttributeSet attrs, int defStyleAttr){
		super(context, attrs, defStyleAttr);
		for(int i=0; i<mViews.length; i++){
			addView(mViews[i]=new TextView(getContext())
			{{
				setMaxLines(1);
			}}, mParams[i]=new LayoutParams());
		}
		mViews[1].getPaint().setFlags(Paint.ANTI_ALIAS_FLAG|Paint.STRIKE_THRU_TEXT_FLAG);
		if(attrs!=null) initAttrs(attrs);
		updateView();
	}
	
	void initAttrs(AttributeSet attrs){
		TypedArray ta=getContext().obtainStyledAttributes(attrs, R.styleable.PriceView);
		LayoutParams pNow=mParams[0];
		pNow.show=ta.getBoolean(R.styleable.PriceView_pvShowNow, pNow.show);
		pNow.price=ta.getFloat(R.styleable.PriceView_pvNowPrice, 0);
		pNow.currency=ta.getString(R.styleable.PriceView_pvNowCurrency);
		pNow.textSize=ta.getDimensionPixelSize(R.styleable.PriceView_pvNowTextSize, 0);
		pNow.textColor=ta.getColor(R.styleable.PriceView_pvNowTextColor, 0);
		pNow.decimal=ta.getInt(R.styleable.PriceView_pvNowDecimal, 2);
		pNow.forceDecimal=ta.getBoolean(R.styleable.PriceView_pvNowForceDecimal, false);
		LayoutParams pOld=mParams[1];
		pOld.show=ta.getBoolean(R.styleable.PriceView_pvShowOld, pOld.show);
		pOld.price=ta.getFloat(R.styleable.PriceView_pvOldPrice, 0);
		pOld.currency=ta.getString(R.styleable.PriceView_pvOldCurrency);
		pOld.textSize=ta.getDimensionPixelSize(R.styleable.PriceView_pvOldTextSize, 0);
		pOld.textColor=ta.getColor(R.styleable.PriceView_pvOldTextColor, 0);
		pOld.decimal=ta.getInt(R.styleable.PriceView_pvOldDecimal, 2);
		pOld.forceDecimal=ta.getBoolean(R.styleable.PriceView_pvOldForceDecimal, false);
		int space=ta.getDimensionPixelSize(R.styleable.PriceView_pvNowOldSpace, 0);
		if(getOrientation()==VERTICAL) pOld.topMargin=space;
		else pOld.leftMargin=space;
		ta.recycle();
	}
	
	public void updateView(){
		for(int i=0; i<mViews.length; i++){
			TextView v=mViews[i];
			LayoutParams p=mParams[i];
			v.setVisibility(p.show? VISIBLE: GONE);
			StringBuilder text=format(p.decimal, p.forceDecimal, p.price);
			if(p.currency!=null) text.insert(0, p.currency);
			v.setText(text);
			if(p.textColor!=0) v.setTextColor(p.textColor);
			if(p.textSize!=0) v.setTextSize(TypedValue.COMPLEX_UNIT_PX, p.textSize);
			v.setLayoutParams(p);
		}
	}
	
	public LayoutParams getNowParams(){
		return mParams[0];
	}
	
	public LayoutParams getOldParams(){
		return mParams[1];
	}
	
	public static class LayoutParams extends LinearLayout.LayoutParams
	{
		public boolean show=true;
		public float price;
		public String currency;
		public float textSize;
		public int textColor;
		public int decimal;
		public boolean forceDecimal;
		
		public LayoutParams(){
			super(-2, -2);
		}
	}
	
	public static StringBuilder format(int decimal, boolean force, float d){
		int n=(int)d;
		if(force? decimal==0: n==d) return new StringBuilder().append(n);
		if(decimal<0) decimal=0;
		StringBuilder text=new StringBuilder().append(d);
		int point=text.indexOf(".");
		if(!force){
			int end=point+decimal+1;
			if(end<text.length()) text.delete(end, text.length());
		}else{
			if(point<0){
				point=text.length();
				text.append(".");
			}
			int end=point+decimal+1;
			if(end<text.length()) text.delete(end, text.length());
			else while(text.length()<end) text.append("0");
		}
		return text;
	}
}
