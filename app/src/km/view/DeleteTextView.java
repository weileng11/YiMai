package km.view;

import android.content.Context;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.TextView;

/** Created by wuzhengu on 2018/10/31 0031 */
public class DeleteTextView extends TextView
{
	public DeleteTextView(Context context){
		this(context, null);
	}
	
	public DeleteTextView(Context context, @Nullable AttributeSet attrs){
		this(context, attrs, 0);
	}
	
	public DeleteTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr){
		super(context, attrs, defStyleAttr);
		getPaint().setFlags(Paint.ANTI_ALIAS_FLAG|Paint.STRIKE_THRU_TEXT_FLAG);
	}
}
