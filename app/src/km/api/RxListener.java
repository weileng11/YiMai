package km.api;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Looper;
import android.widget.Toast;
import rx.Subscriber;

public abstract class RxListener<Model> extends Subscriber<Model>
{
	protected Context context(){
		return null;
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
		}
	}
	
	@Override
	public void onCompleted(){
		
	}
	
	@Override
	public void onError(Throwable ex){
		onNext(null, ex);
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
