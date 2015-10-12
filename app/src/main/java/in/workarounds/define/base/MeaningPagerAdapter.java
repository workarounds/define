package in.workarounds.define.base;

import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import in.workarounds.define.R;

/**
 * Created by madki on 27/09/15.
 */
public class MeaningPagerAdapter extends PagerAdapter {
    private static final String[] titles = new String[] {"Wordnet", "Urban"};
    private static final int[] layouts = new int[] {R.layout.layout_wordnet_page, R.layout.layout_urban_page};

    @Override
    public int getCount() {
        return titles.length;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = LayoutInflater.from(
                container.getContext()).cloneInContext(container.getContext())
                .inflate(layouts[position], container, false);
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
        return titles[position];
    }

}
