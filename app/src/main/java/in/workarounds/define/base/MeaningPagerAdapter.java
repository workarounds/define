package in.workarounds.define.base;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.HashMap;

import in.workarounds.define.R;
import in.workarounds.define.constants.DictionaryId;
import in.workarounds.define.util.PrefUtils;

/**
 * Created by madki on 27/09/15.
 */
public class MeaningPagerAdapter extends PagerAdapter {
    private HashMap<Integer, String> titleMap;
    private HashMap<Integer, Integer> layoutMap;

    private Context context;
    private int[] order;

    public MeaningPagerAdapter(Context context) {
        initMaps();
        this.context = context;
        order = PrefUtils.getDictionaryOrder(context);
    }

    private void initMaps() {
        titleMap = new HashMap<>();
        titleMap.put(DictionaryId.WORDNET, "Wordnet");
        titleMap.put(DictionaryId.LIVIO, "Livio");
        titleMap.put(DictionaryId.URBAN, "Urban");

        layoutMap = new HashMap<>();
        layoutMap.put(DictionaryId.WORDNET, R.layout.layout_wordnet_page);
        layoutMap.put(DictionaryId.LIVIO, R.layout.layout_livio_page);
        layoutMap.put(DictionaryId.URBAN, R.layout.layout_urban_page);
    }

    @Override
    public int getCount() {
        return order.length;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = LayoutInflater.from(
                container.getContext()).cloneInContext(container.getContext())
                .inflate(layoutMap.get(order[position]), container, false);
        container.addView(view);
        return view;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titleMap.get(order[position]);
    }

}
