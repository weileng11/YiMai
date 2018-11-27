package km.api;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import rx.subjects.BehaviorSubject;

public class RxActivity extends AppCompatActivity implements LifeCycles.Cycle
{
	protected final BehaviorSubject<LifeCycles.Event> mSubject=BehaviorSubject.create();
	
	public BehaviorSubject getSubject(){
		return mSubject;
	}
	
	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		mSubject.onNext(LifeCycles.Event.CREATE);
	}
	
	@Override
	protected void onResume(){
		super.onResume();
		mSubject.onNext(LifeCycles.Event.RESUME);
	}
	
	@Override
	protected void onStart(){
		super.onStart();
		mSubject.onNext(LifeCycles.Event.START);
	}
	
	@Override
	protected void onPause(){
		super.onPause();
		mSubject.onNext(LifeCycles.Event.PAUSE);
	}
	
	@Override
	protected void onStop(){
		super.onStop();
		mSubject.onNext(LifeCycles.Event.STOP);
	}
	
	@Override
	protected void onDestroy(){
		super.onDestroy();
		mSubject.onNext(LifeCycles.Event.DESTROY);
	}
}
