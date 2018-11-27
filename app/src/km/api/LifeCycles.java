package km.api;

import rx.Observable;
import rx.Observable.Transformer;
import rx.functions.Func1;
import rx.subjects.BehaviorSubject;

public class LifeCycles
{
	public static <T> Transformer<T, T> bindUntilEvent(
			BehaviorSubject<Event> subject, final Event event){
		final Observable<Event> takeFirst=subject.takeFirst(new Func1<Event, Boolean>()
		{
			@Override
			public Boolean call(Event event2){
				return event2==event;
			}
		});
		Transformer<T, T> transformer=new Transformer<T, T>()
		{
			@Override
			public Observable<T> call(Observable<T> src){
				return src.takeUntil(takeFirst);
			}
		};
		return transformer;
	}
	
	public interface Cycle
	{
		BehaviorSubject getSubject();
	}
	
	public enum Event
	{
		ATTACH,
		CREATE,
		CREATE_VIEW,
		START,
		RESUME,
		PAUSE,
		STOP,
		DESTROY_VIEW,
		DESTROY,
		DETACH,
		;
	}
}
