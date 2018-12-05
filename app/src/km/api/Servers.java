package km.api;

import android.content.Context;
import android.text.TextUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import km.app.App;
import km.model.M;
import km.ui.ActLogin;
import km.util.LoginUtil;
import okhttp3.*;
import okhttp3.logging.HttpLoggingInterceptor;
import okio.Buffer;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;
import rx.Observable.Transformer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * @author: ${bruce}
 * @description:
 * @date: 2018/11/21 0021
 * @time: 下午 2:16
 */
public class Servers
{
	public static Retrofit getRetrofit(String baseUrl){
		OkHttpClient.Builder builder=new OkHttpClient.Builder();
		
		HttpLoggingInterceptor logging=new HttpLoggingInterceptor();
		logging.setLevel(HttpLoggingInterceptor.Level.BODY);
		builder.addInterceptor(logging);
		
		builder.addInterceptor(new Interceptor()
		{
			@Override
			public Response intercept(Chain chain) throws IOException{
				Request req=chain.request();
				if(req.url().toString().startsWith(Server.BASE_URL)){
					String token=Server.TEST_TOKEN;
					if(TextUtils.isEmpty(token)) token=LoginUtil.getToken();
					Request.Builder builder=req.newBuilder().method(req.method(), req.body());
					if(!TextUtils.isEmpty(token)) builder.addHeader("p-token", token);
					builder.addHeader("p-device-type", Server.DEVICE_TYPE);
					req=builder.build();
				}
				return chain.proceed(req);
			}
		});
		builder.cache(new Cache(new File(App.get().getExternalCacheDir(), "retrofit"), 1024*1024*50));
		builder.connectTimeout(10, TimeUnit.SECONDS);
		builder.readTimeout(10, TimeUnit.SECONDS);
		builder.writeTimeout(10, TimeUnit.SECONDS);
		builder.retryOnConnectionFailure(true);
		builder.protocols(Arrays.asList(Protocol.HTTP_1_1));
		OkHttpClient client=builder.build();
		Retrofit retrofit=new Retrofit.Builder().baseUrl(baseUrl)
				.addConverterFactory(createGsonConverter())
				.addCallAdapterFactory(RxJavaCallAdapterFactory.create())
				.client(client)
				.build();
		return retrofit;
	}
	
	static Converter.Factory createGsonConverter(){
		if(false) return GsonConverterFactory.create();
		return new Converter.Factory()
		{
			final String CHARSET="UTF-8";
			final MediaType MEDIA_TYPE=MediaType.parse("application/json; charset="+CHARSET);
			final boolean LENIENT=false;
			final Gson gson;
			{
				GsonBuilder builder=new GsonBuilder();
				if(LENIENT) builder.setLenient();
				gson=builder.create();
			}
			@Override
			public Converter<?, RequestBody> requestBodyConverter(final Type type,
					Annotation[] pAnnotations, Annotation[] mAnnotations, Retrofit retrofit){
				return new Converter<Object, RequestBody>()
				{
					@Override
					public RequestBody convert(Object in) throws IOException{
						Buffer buffer=new Buffer();
						Writer writer=new OutputStreamWriter(buffer.outputStream(), CHARSET);
						TypeAdapter adapter=gson.getAdapter(TypeToken.get(type));
						adapter.write(gson.newJsonWriter(writer), in);
						writer.close();
						return RequestBody.create(MEDIA_TYPE, buffer.readByteArray());
					}
				};
			}
			
			@Override
			public Converter<ResponseBody, ?> responseBodyConverter(final Type type,
					Annotation[] annotations, Retrofit retrofit){
				return new Converter<ResponseBody, Object>()
				{
					@Override
					public Object convert(ResponseBody in) throws IOException{
						String json=in.string();
						json=vaildateJson(json);
						Object out=null;
						try{
							TypeAdapter adapter=gson.getAdapter(TypeToken.get(type));
							out=adapter.read(gson.newJsonReader(new StringReader(json)));
						}catch(Exception e){
							e.printStackTrace();
						}
						in.close();
						if(out instanceof M){
							if("token过期".equals(((M)out).msg)){
								LoginUtil.logout();
								Context ctxt=App.get();
								if(ctxt!=null) ActLogin.start(ctxt);
							}
						}
						return out;
					}
				};
			}
		};
	}
	
	static String vaildateJson(String json){
		try{
			JSONObject jo=new JSONObject(json);
			boolean change=false;
			String key;
			Object value;
			key="data";
			value=jo.opt(key);
			if(value instanceof String){
				String str=(String)value;
				if(change=str.isEmpty()) jo.remove(key);
			}
			key="msg";
			value=jo.opt(key);
			if(value instanceof JSONArray){
				change=true;
				String str=value.toString();
				str=str.substring(1, str.length()-1);
				jo.put(key, str);
			}
			key="result";
			value=jo.opt(key);
			if(value instanceof JSONArray){
				if(((JSONArray)value).length()==0){
					change=true;
					jo.remove(key);
				}
			}
			if(change) json=jo.toString();
		}catch(Exception e){
			//e.printStackTrace();
		}
		return json;
	}
	
	public static <T> Observable<T> create(LifeCycles.Cycle cycle, Observable<T> o){
		return o.compose(LifeCycles.bindUntilEvent(cycle.getSubject(), LifeCycles.Event.DESTROY))
				.compose(new Transformer<Observable, Observable>()
				{
					@Override
					public Observable call(Observable o){
						return o.subscribeOn(Schedulers.io())
								.observeOn(AndroidSchedulers.mainThread());
					}
				});
	}
	
	public static <T> void start(LifeCycles.Cycle cycle, Observable<T> o, Subscriber<T> sub){
		create(cycle, o).subscribe(sub);
	}
	
	
}