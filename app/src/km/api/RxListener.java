package km.api;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Looper;
import android.widget.Toast;
import km.app.App;
import rx.Subscriber;

/**
 * @author: ${bruce}
 * @description:
 * @date: 2018/11/21 0021
 * @time: 下午 2:16
 */
public abstract class RxListener<Model> extends Subscriber<Model>
{
	protected Context context(){
		return App.get();
	}
	
	@Override
	public void onStart(){
		Context ctxt=context();
		if(ctxt==null) return;
		ConnectivityManager manager=
				(ConnectivityManager)ctxt.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info=manager.getActiveNetworkInfo();
		boolean netOk=info!=null && info.isAvailable();
		if(!netOk){
			if(Looper.myLooper()==null) Looper.prepare();
			Toast.makeText(ctxt, "无网络！", Toast.LENGTH_SHORT).show();
			return;
		}
	}
	
	@Override
	public void onCompleted(){
		
	}
	//public abstract void onError(ExceptionHandle.ResponeThrowable responeThrowable);
	
	@Override
	public void onError(Throwable e){
		//if(e instanceof Exception){
		//	//访问获得对应的Exception
		//	onError(ExceptionHandle.handleException(e));
		//}else {
		//	//将Throwable 和 未知错误的status code返回
		//	onError(new ExceptionHandle.ResponeThrowable(e,ExceptionHandle.ERROR.UNKNOWN));
		//}
		onNext(null, e);
	}
	
	@Override
	public void onNext(Model model){
		onNext(model, check(model));
	}
	
	public void onNext(Model model, Throwable ex){
		String msg=null;
		if(ex!=null) msg=ex.getMessage();
		onNext(model, msg);
	}
	
	public void onNext(Model model, String msg){
		
	}
	
	protected Throwable check(Model model){
		return null;
	}
}
