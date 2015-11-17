package in.workarounds.define.base;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import in.workarounds.define.constants.DictionaryConstants;
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
            case DictionaryConstants.WORDNET:
                view = new WordnetMeaningPage(context);
                break;
            case DictionaryConstants.URBAN:
                view = new UrbanMeaningPage(context);
                break;
            case DictionaryConstants.LIVIO_EN:
            case DictionaryConstants.LIVIO_ES:
            case DictionaryConstants.LIVIO_IT:
            case DictionaryConstants.LIVIO_DE:
            case DictionaryConstants.LIVIO_FR:
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
        return DictionaryConstants.dictNames[order[position]];
    }

}
