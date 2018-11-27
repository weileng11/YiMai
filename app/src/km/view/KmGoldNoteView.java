package km.view;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.GlideApp;
import kameng.yimai.R;

/** Created by wuzhengu on 2018/11/9 */
public class KmGoldNoteView extends ImageView implements View.OnClickListener
{
	public KmGoldNoteView(Context context){
		this(context, null);
	}
	
	public KmGoldNoteView(Context context, @Nullable AttributeSet attrs){
		this(context, attrs, 0);
	}
	
	public KmGoldNoteView(Context context, @Nullable AttributeSet attrs, int defStyleAttr){
		super(context, attrs, defStyleAttr);
		int resId=R.mipmap.gold_note_button;
		if(isInEditMode()){
			super.setImageResource(resId);
			return;
		}
		GlideApp.with(getContext()).load(resId).into(this);
		setOnClickListener(this);
	}
	
	float mMoney;
	
	public KmGoldNoteView setMoney(float money){
		mMoney=money;
		return this;
	}
	
	@Override
	public void onClick(View v){
		new Dialog(getContext())
		{
			@Override
			protected void onCreate(Bundle bundle){
				super.onCreate(bundle);
				getWindow().setBackgroundDrawable(new ColorDrawable(0));
				setContentView(R.layout.dialog_gold_note);
				TextView[] views={
						findViewById(R.id.gold_note_money1),
						findViewById(R.id.gold_note_money2),
				};
				for(int i=0; i<views.length; i++){
					float m=mMoney;
					int n=(int)m;
					if(n==m) views[i].setText(String.valueOf(n));
					else views[i].setText(String.valueOf(m));
				}
			}
		}.show();
	}
}
