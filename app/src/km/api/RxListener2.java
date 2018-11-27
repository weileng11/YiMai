package km.api;

import km.model.M;

public abstract class RxListener2<Data> extends RxListener<M<Data>>
{
	
	@Override
	protected final Throwable check(M<Data> model){
		if(model==null) return new NullPointerException("Null model");
		if(model.result==null) return new Error(model.msg);
		return null;
	}
	
	@Override
	public void onNext(M<Data> model, String msg){
		onNext2(model==null? null: model.result, msg);
	}
	
	public void onNext2(Data data, String msg){
		
	}
	
}
