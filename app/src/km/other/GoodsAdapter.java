package km.other;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import java.util.List;
import kameng.yimai.R;
import km.base.BaseActivity;
import km.model.BaseGood;
import km.model.Good;
import km.quick.QuickHolder;
import km.ui.ActWeb;
import km.view.KmGoldNoteView;
import km.view.PriceView;

/** Created by wuzhengu on 2018/11/1 0002 */
public class GoodsAdapter<Item extends BaseGood> extends BaseGoodAdapter<Item>
{
	public GoodsAdapter(int layout){
		super(layout);
	}
	
	public GoodsAdapter(int layout, List<Item> items){
		super(layout, items);
	}
	
	@Override
	protected void convert(QuickHolder h, Item item){
		super.convert(h, item);
		if(item instanceof Good){
			Good good=(Good)item;
			setPrice(h, R.id.item_good_price, good.priceNow, good.priceOld);
			h.setTextSafe(R.id.item_good_hits, String.valueOf(good.hits));
			h.setTextSafe(R.id.item_good_coupon, PriceView.format(2, false, good.coupon));
			KmGoldNoteView vGold=h.getView(R.id.item_good_gold_note);
			if(vGold!=null) vGold.setMoney(good.coupon);
		}
	}
	
	@Override
	public void onClick(View v, int p){
		clickGood(v.getContext(), getItem(p));
	}
	
	public static void setPrice(QuickHolder h, int vId, float priceNow, float priceOld){
		PriceView pv=h.getView(vId);
		if(pv!=null){
			pv.getNowParams().price=priceNow;
			pv.getOldParams().price=priceOld;
			pv.updateView();
		}
	}
	
	public static void clickGood(Context ctxt, BaseGood good){
		if(ctxt==null) return;
		if(good==null) return;
		String link=good.getLink();
		String name=good.getName();
		if(!TextUtils.isEmpty(name)) BaseActivity.toast(ctxt, name);
		if(TextUtils.isEmpty(link)) return;
		ctxt.startActivity(ActWeb.intent(link));
	}
}
