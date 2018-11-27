package km.base;

import android.app.*;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import static android.app.Activity.RESULT_OK;

public class ActivityProxy
{
	Activity mActivity;
	Fragment mFragment;
	List<Starter> mCalls;
	AlertDialog mLoadDialog;
	
	public ActivityProxy set(Activity activity, Fragment fragment){
		mActivity=activity;
		mFragment=fragment;
		return this;
	}
	
	public Fragment getFragment(){
		return mFragment;
	}
	
	public Activity getActivity(){
		if(mActivity==null && mFragment!=null) mActivity=mFragment.getActivity();
		return mActivity;
	}
	
	public Context getContext(){ return mActivity; }
	
	public void dismissLoading(){
		if(mLoadDialog!=null) mLoadDialog.dismiss();
	}
	
	public void showLoading(CharSequence msg){
		if(mLoadDialog==null) mLoadDialog=new ProgressDialog(getActivity());
		mLoadDialog.setMessage(!TextUtils.isEmpty(msg)? msg: "加载中...");
		mLoadDialog.show();
	}
	
	public void alert(DialogBuilder builder){
		builder.show(getContext());
	}
	
	public void alert(final String msg){
		alert(new DialogBuilder()
		{
			@Override
			public boolean isCancelable(){
				return false;
			}
			@Override
			public CharSequence getMessage(){
				return msg;
			}
			@Override
			public CharSequence getButton1(){
				return "知道了";
			}
		});
	}
	
	public boolean onActivityResult(int req, int res, Intent data){
		Starter call=null;
		if(mCalls!=null && mCalls.size()>0){
			Iterator<Starter> it=mCalls.iterator();
			while(it.hasNext()){
				Starter temp=it.next();
				if(temp.id==req){
					call=temp;
					it.remove();
					break;
				}
			}
		}
		return call!=null && call.onResult(res, data);
	}
	
	public void startActivity(Class<? extends Activity> cls){
		getActivity().startActivity(new Intent(getActivity(), cls));
	}
	
	public boolean startActivity(Intent intent, Starter call){
		if(mCalls==null) mCalls=new LinkedList<>();
		else mCalls.remove(call);
		mCalls.add(0, call);
		Fragment fragment=getFragment();
		try{
			if(fragment!=null) fragment.startActivityForResult(intent, call.id);
			else getActivity().startActivityForResult(intent, call.id);
			return true;
		}catch(Exception e){ e.printStackTrace(); }
		return false;
	}
	
	public boolean startActivity(Starter call){
		return startActivity(call.getIntent(), call);
	}
	
	public abstract static class Starter
	{
		/**
		 @see android.support.v4.app.FragmentActivity#checkForValidRequestCode(int) requestCode
		 */
		public final int id=hashCode()&0xFFFF;
		
		public Starter(){}
		
		public Intent getIntent(){ return null; }
		
			protected boolean onResult(int res, Intent data){
			onResult(res==RESULT_OK, data);
			return true;
		}
		
		protected abstract void onResult(boolean ok, Intent data);
	}
	
	public abstract static class DialogBuilder implements View.OnClickListener
	{
		private String[] buttonText;
		private AlertDialog mDialog;
		
		public void show(Context ctxt){
			AlertDialog dialog=mDialog;
			if(isForProgress()){
				if(!(dialog instanceof ProgressDialog)) dialog=new ProgressDialog(ctxt);
			}else{
				if(dialog==null || dialog instanceof ProgressDialog){
					dialog=new AlertDialog.Builder(ctxt).create();
				}
			}
			dialog.setCancelable(isCancelable());
			dialog.setTitle(getTitle());
			dialog.setMessage(getMessage());
			int[] buttons={
					DialogInterface.BUTTON_POSITIVE,
					DialogInterface.BUTTON_NEUTRAL,
					DialogInterface.BUTTON_NEGATIVE,
			};
			for(int which: buttons){
				dialog.setButton(which, "-", (DialogInterface.OnClickListener)null);
			}
			dialog.show();
			for(int i=0; i<buttons.length; i++){
				int which=buttons[i];
				CharSequence text=null;
				switch(i){
				case 0:
					text=getButton1();
					break;
				case 1:
					text=getButton2();
					break;
				case 2:
					text=getButton3();
					break;
				}
				Button button=dialog.getButton(which);
				if(text!=null && text.length()>0){
					button.setText(text);
					button.setOnClickListener(this);
					button.setVisibility(View.VISIBLE);
				}else button.setVisibility(View.INVISIBLE);
			}
			mDialog=dialog;
		}
		
		public boolean isForProgress(){
			return false;
		}
		
		public boolean isCancelable(){
			return true;
		}
		
		public CharSequence getTitle(){
			return null;
		}
		
		public abstract CharSequence getMessage();
		
		public DialogBuilder setButtonText(String... buttonText){
			this.buttonText=buttonText;
			return this;
		}
		
		public CharSequence getButton1(){
			return buttonText!=null && buttonText.length>0? buttonText[0]: "";
		}
		
		public CharSequence getButton2(){
			return buttonText!=null && buttonText.length>1? buttonText[1]: "";
		}
		
		public CharSequence getButton3(){
			return buttonText!=null && buttonText.length>2? buttonText[2]: "";
		}
		
		@Override
		public void onClick(View v){
			if(mDialog!=null){
				boolean keep=false;
				if(v==null) ;
				else if(v==mDialog.getButton(DialogInterface.BUTTON_POSITIVE)) keep=onClick1(mDialog);
				else if(v==mDialog.getButton(DialogInterface.BUTTON_NEUTRAL)) keep=onClick2(mDialog);
				else if(v==mDialog.getButton(DialogInterface.BUTTON_NEGATIVE)) keep=onClick3(mDialog);
				if(!keep) mDialog.dismiss();
			}
		}
		
		public boolean onClick1(DialogInterface dialog){
			return false;
		}
		
		public boolean onClick2(DialogInterface dialog){
			return false;
		}
		
		public boolean onClick3(DialogInterface dialog){
			return false;
		}
	}
}
