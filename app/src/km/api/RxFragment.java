package km.api;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;
import rx.subjects.BehaviorSubject;

public class RxFragment extends Fragment implements LifeCycles.Cycle
{
	protected final BehaviorSubject<LifeCycles.Event> mSubject=BehaviorSubject.create();
	
	public BehaviorSubject getSubject(){
		return mSubject;
	}
	
	@Override
	public void onAttach(Context context){
		super.onAttach(context);
		mSubject.onNext(LifeCycles.Event.ATTACH);
	}
	
	@Override
	public void onCreate(@Nullable Bundle bundle){
		super.onCreate(bundle);
		mSubject.onNext(LifeCycles.Event.CREATE);
	}
	
	@Override
	public void onViewCreated(View view, @Nullable Bundle bundle){
		super.onViewCreated(view, bundle);
		mSubject.onNext(LifeCycles.Event.CREATE_VIEW);
	}
	
	@Override
	public void onStart(){
		super.onStart();
		mSubject.onNext(LifeCycles.Event.START);
	}
	
	@Override
	public void onResume(){
		super.onResume();
		mSubject.onNext(LifeCycles.Event.RESUME);
	}
	
	@Override
	public void onPause(){
		super.onPause();
		mSubject.onNext(LifeCycles.Event.PAUSE);
	}
	
	@Override
	public void onStop(){
		super.onStop();
		mSubject.onNext(LifeCycles.Event.STOP);
	}
	
	@Override
	public void onDestroy(){
		super.onDestroy();
		mSubject.onNext(LifeCycles.Event.DESTROY);
	}
	
	@Override
	public void onDestroyView(){
		super.onDestroyView();
		mSubject.onNext(LifeCycles.Event.DESTROY_VIEW);
	}
	
	@Override
	public void onDetach(){
		super.onDetach();
		mSubject.onNext(LifeCycles.Event.DETACH);
	}
}