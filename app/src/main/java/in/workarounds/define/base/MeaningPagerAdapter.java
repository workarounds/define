package in.workarounds.define.base;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import in.workarounds.define.constants.DictionaryId;
import in.workarounds.define.urban.UrbanMeaningPage;
import in.workarounds.define.util.PrefUtils;
import in.workarounds.define.webviewDicts.livio.LivioMeaningPage;
import in.workarounds.define.wordnet.WordnetMeaningPage;

/**
 * Created by madki on 27/09/15.
 */
public class MeaningPagerAdapter extends PagerAdapter {

    private Context context;
    private int[] order;

    public MeaningPagerAdapter(Context context) {
        this.context = context;
        order = PrefUtils.getDictionaryOrder(context);
    }


    @Override
    public int getCount() {
        return order.length;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view;
        int dictId = order[position];
        switch (dictId) {
            case DictionaryId.WORDNET:
                view = new WordnetMeaningPage(context);
                break;
            case DictionaryId.URBAN:
                view = new UrbanMeaningPage(context);
                break;
            case DictionaryId.LIVIO_EN:
            case DictionaryId.LIVIO_ES:
            case DictionaryId.LIVIO_IT:
            case DictionaryId.LIVIO_DE:
            case DictionaryId.LIVIO_FR:
                view = new LivioMeaningPage(context, dictId);
                break;
            default:
                throw new IllegalStateException("Unknown DictionaryId provided in MeaningPagerAdapter");
        }

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
        return DictionaryId.dictNames[order[position]];
    }

}
