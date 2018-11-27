package km.util.pop;

import android.app.Activity;
import android.graphics.drawable.BitmapDrawable;
import android.view.*;
import android.widget.*;
import com.bumptech.glide.GlideApp;
import kameng.yimai.R;
import km.model.CardDetail;

/**
 * @author: ${bruce}
 * @project: YiMai
 * @package: km.util.pop
 * @description:
 * @date: 2018/11/6 0006  
 * @time: 下午 7:40
 */
public class BusCodePop
{
	private PopupWindow popupWindow;
	private Activity mActivity;
	private CardDetail.CardInfo mCardInfo;
	private ImageView mIvHead;
	private TextView mTvName;
	private TextView mTvPhone;
	private ImageView mIvCode;
	public BusCodePop(Activity activity,CardDetail.CardInfo cardInfo) {
		mActivity = activity;
		this.mCardInfo=cardInfo;
		initView();
	}
	
	private void initView() {
		//防止重复按按钮
		if (popupWindow != null && popupWindow.isShowing()) return;
		//设置PopupWindow的View
		View view = LayoutInflater.from(mActivity).inflate(R.layout.pop_bus_code, null);
		popupWindow = new PopupWindow(view, RelativeLayout.LayoutParams.MATCH_PARENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);
		//设置背景,这个没什么效果，不添加会报错
		popupWindow.setBackgroundDrawable(new BitmapDrawable());
		//设置点击弹窗外隐藏自身
		popupWindow.setFocusable(true);
		popupWindow.setOutsideTouchable(true);
		//设置动画
		//        popupWindow.setAnimationStyle(R.style.PopupWindow);
		//设置位置
		popupWindow.showAtLocation(mActivity.getWindow().getDecorView(), Gravity.CENTER, 0, 0);
		//                                                  Gravity.BOTTOM底部  NO_GRAVITY左上  LEFT高的中间
		//设置消失监听
		popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
			@Override
			public void onDismiss() {
				setBackgroundAlpha(1); //消失后设置回透明度
			}
		});
		//设置PopupWindow的View点击事件
		setOnPopupViewClick(view);
		//设置背景色
		setBackgroundAlpha(0.5f);
	}
	
	public void setBackgroundAlpha(float alpha) {
		Activity context = (Activity) this.mActivity;
		WindowManager.LayoutParams lp = context.getWindow().getAttributes();
		lp.alpha = alpha;
		context.getWindow().setAttributes(lp);
	}
	
	private void setOnPopupViewClick(View view){
		mIvHead=(ImageView)view.findViewById(R.id.iv_head);
		mTvName=(TextView)view.findViewById(R.id.tv_name);
		mTvPhone=(TextView)view.findViewById(R.id.tv_phone);
		mIvCode=(ImageView)view.findViewById(R.id.iv_code);
		//头像
		GlideApp.with(mActivity).load(mCardInfo.headPortrait).into(mIvHead);
		mTvName.setText(mCardInfo.realName);
		mTvPhone.setText(mCardInfo.mobile);
		GlideApp.with(mActivity).load(mCardInfo.qrcode).into(mIvCode);
	}
	
}
