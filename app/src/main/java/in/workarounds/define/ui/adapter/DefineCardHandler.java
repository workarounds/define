package in.workarounds.define.ui.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Locale;

import in.workarounds.define.R;
import in.workarounds.define.model.DictResult;
import in.workarounds.define.urban.Meaning;
import in.workarounds.define.urban.UrbanResult;
import in.workarounds.define.ui.view.FlowLayout;
import in.workarounds.define.util.LogUtils;
import in.workarounds.define.util.StringUtils;

public class DefineCardHandler implements OnTouchListener{
    private static final String TAG = LogUtils.makeLogTag(DefineCardHandler.class);
    private Context mContext;
    private View mRoot;
    private ViewGroup mCard;
    private  ViewGroup mMeanings;
    private SelectedTextChangedListener mSelectedTextChangedListener;

    public DefineCardHandler(@NonNull Context context, @NonNull View root, @NonNull SelectedTextChangedListener listener) {
        mContext = context;
        mRoot = root;
        mSelectedTextChangedListener = listener;
        initialize();
    }

    private void initialize() {
        mCard = (ViewGroup) mRoot.findViewById(R.id.bubble_card);
        mCard.setVisibility(View.GONE);
        mCard.setOnTouchListener(this);

    }

    /**
     * Adds a TextView for each word in words[] array to the FlowLayout with
     * spacing dimension mentioned in the resources. Removes all the views in
     * the FlowLayout to avoid concatenation of ClipData
     *
     * @param words array containing each word in the text of the ClipData
     */
    public void showBubbles(String[] words) {
        FlowLayout card = (FlowLayout) mCard;
        card.setVisibility(View.VISIBLE);
        card.removeAllViews();

        for (String word : words) {
            if (!word.contains("\n")) {
                addTextView(word, false);
            } else {
                String[] brokenWords = word.split("\n");
                if (brokenWords.length == 0)
                    continue;
                word = brokenWords[0];
                addTextView(word, false);
                boolean addEnterAtEnd = false;
                for (int i = 1; i < brokenWords.length; i++) {
                    if (!brokenWords[i].isEmpty()) {
                        addEnterAtEnd = false;
                        addTextView(brokenWords[i], true);
                    } else {
                        addEnterAtEnd = true;
                    }
                }
                if (addEnterAtEnd) {
                    addTextView("", true);
                }
            }
        }
        setBubbleCardScrollHeight();
    }

    /**
     * Adds a textview for the word(text) to the card. If newLine is true, the
     * textview is added in the nextLine of the FlowLayout
     *
     * @param text
     *            text in the textview to be added
     * @param newLine
     *            true if the textview should be added in a newLine of the
     *            FlowLayout, false otherwise
     */
    private void addTextView(String text, boolean newLine) {
        FlowLayout card = (FlowLayout) mCard;

        Resources res = mContext.getResources();
//		int horizontal_spacing = res
//				.getDimensionPixelSize(R.dimen.bubble_horizontal_spacing);
        int vertical_spacing = res
                .getDimensionPixelSize(R.dimen.bubble_vertical_spacing);
        int horizontal_spacing = 0;
        FlowLayout.LayoutParams params = new FlowLayout.LayoutParams(
                horizontal_spacing, vertical_spacing, false);

        FlowLayout.LayoutParams paramsNewLine = new FlowLayout.LayoutParams(
                horizontal_spacing, vertical_spacing, true);

        LayoutInflater inflater = LayoutInflater.from(mContext);

        View v = inflater.inflate(R.layout.text_bubble, card, false);
        TextView tv = (TextView) v.findViewById(R.id.text_bubble);

        tv.setText(text);
        if (newLine)
            card.addView(tv, paramsNewLine);
        else
            card.addView(tv, params);
    }

    public void showMeanings(String wordForm, ArrayList<DictResult> results){
        ViewGroup meaningCard = (ViewGroup) getMeaningCard();
        meaningCard.setVisibility(View.VISIBLE);

        mMeanings = (ViewGroup) meaningCard
                .findViewById(R.id.meanings);

        TextView word = (TextView) meaningCard.findViewById(R.id.word);
        String wordText = capitalizeFirstLetter(StringUtils.preProcessWord(wordForm));
        word.setText(wordText);

        addMeaningsToScrollView(results);
    }

    /**
     * This function does basically does tha work of an adapter. It populates
     * the meanings ScrollView with all the meanings from the DictResults[]
     *
     */
    private void addMeaningsToScrollView(ArrayList<DictResult> results) {
        mMeanings.removeAllViews();
        for (DictResult dr : results) {
            View meaningRow = LayoutInflater.from(mContext).inflate(
                    R.layout.meaning_row, mMeanings, false);
            TextView def = (TextView) meaningRow.findViewById(R.id.definition);
            def.setText(dr.getMeaning());
            TextView type = (TextView) meaningRow
                    .findViewById(R.id.meaning_type);
            type.setText(dr.getType());
            TextView synonyms = (TextView) meaningRow
                    .findViewById(R.id.synonyms);
            synonyms.setText(TextUtils.join(", ", dr.getSynonyms()));
            TextView usage = (TextView) meaningRow.findViewById(R.id.usage);
            usage.setText(TextUtils.join(", ", dr.getUsage()));
            /*Making usage text view disappear if the length of `uses` is 0*/
            if(dr.getUsage().size() == 0){
                usage.setVisibility(View.GONE);
            }else{
                usage.setVisibility(View.VISIBLE);
            }
            mMeanings.addView(meaningRow);
        }
    }

    public void addUrbanDictMeaningsToScrollView(UrbanResult results) {

        TextView urbanHeadingTextView = new TextView(mContext);
        urbanHeadingTextView.setTextSize(15);
        urbanHeadingTextView.setTypeface(urbanHeadingTextView.getTypeface(), Typeface.BOLD_ITALIC);
        urbanHeadingTextView.setTextColor(mContext.getResources().getColor(R.color.black));
        urbanHeadingTextView.setText("Urban Dictionary Meanings");
        urbanHeadingTextView.setPadding(25,25,25,25);
        //TODO: Remove magic numbers, can't rely on them
        urbanHeadingTextView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));

        if (results.getMeanings().size() != 0) { mMeanings.addView(urbanHeadingTextView);}

        for (Meaning dr : results.getMeanings()) {
            View meaningRow = LayoutInflater.from(mContext).inflate(
                    R.layout.urban_meaning_row, mMeanings, false);

            TextView def = (TextView) meaningRow.findViewById(R.id.definition);
            def.setText(dr.getDefinition());
            TextView synonyms = (TextView) meaningRow
                    .findViewById(R.id.synonyms);
            synonyms.setText(TextUtils.join(", ", results.getTags()));
            TextView usage = (TextView) meaningRow.findViewById(R.id.usage);
            usage.setText(dr.getExample());
            TextView author = (TextView) meaningRow.findViewById(R.id.author);
            author.setText("-"+ dr.getAuthor());
            /*Making usage text view disappear if the length of `uses` is 0*/
            if(dr.getExample().isEmpty()){
                usage.setVisibility(View.GONE);
            }else{
                usage.setVisibility(View.VISIBLE);
            }
            mMeanings.addView(meaningRow);
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                clearSelected();
            case MotionEvent.ACTION_MOVE:
                View touchedView = this.getTouchedChildView(v, event.getRawX(),
                        event.getRawY());
                if (touchedView instanceof TextView) {
                    TextView selectedTV = (TextView) touchedView;
                    toggleSelected(selectedTV);
                }
                return true;
            case MotionEvent.ACTION_UP:
                v.performClick();
                mSelectedTextChangedListener
                        .onSelectedTextChanged(extractTextFromCard());
                clearTags();
                return true;
        }
        return false;
    }

    private void toggleSelected(TextView selectedTV) {
        if (!isViewAlreadyTouched(selectedTV)) {
            selectedTV.setSelected(!selectedTV.isSelected());
            selectedTV.setTag(true);
        }
    }

    private boolean isViewAlreadyTouched(TextView selectedTV) {
        boolean touched = false;
        Object temp = selectedTV.getTag();
        if (temp != null)
            touched = (Boolean) temp;
        return touched;
    }

    private void clearTags() {
        ViewGroup card = mCard;
        int n = card.getChildCount();
        for (int i = 0; i < n; i++) {
            View v = card.getChildAt(i);
            v.setTag(false);
        }
    }

    private void clearSelected() {
        ViewGroup card = mCard;
        int n = card.getChildCount();
        for (int i = 0; i < n; i++) {
            View v = card.getChildAt(i);
            v.setSelected(false);
        }
    }

    /**
     * Capitalizes the first letter of the word given
     *
     * @param word
     * @return Capitalized string
     */
    public static String capitalizeFirstLetter(String word) {
        if (word.length() > 1)
            word = word.substring(0, 1).toUpperCase(Locale.ENGLISH)
                    + word.substring(1).toLowerCase(Locale.ENGLISH);
        else if (word.length() == 1)
            word = word.toUpperCase(Locale.ENGLISH);
        return word;
    }

    /**
     * Determines if given points are inside view
     *
     * @param x
     *            - x coordinate of point
     * @param y
     *            - y coordinate of point
     * @param view
     *            - view object to compare
     * @return true if the points are within view bounds, false otherwise
     */
    public static boolean isPointInsideView(float x, float y, View view) {
        int location[] = new int[2];
        view.getLocationOnScreen(location);
        int viewX = location[0];
        int viewY = location[1];

        // point is inside view bounds
        if ((x > viewX && x < (viewX + view.getWidth()))
                && (y > viewY && y < (viewY + view.getHeight()))) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * returns the first childview under the given co ordinates
     *
     * @param parentView
     * @param x
     * @param y
     * @return
     */
    public View getTouchedChildView(View parentView, float x, float y) {
        int n = mCard.getChildCount();
        for (int i = 0; i < n; i++) {
            View child = mCard.getChildAt(i);
            boolean isInside = isPointInsideView(x, y, child);
            if (isInside) {
                return child;
            }
        }
        return null;
    }

    public String extractTextFromCard() {
        ViewGroup card = mCard;
        int n = card.getChildCount();
        String extractedText = "";
        for (int i = 0; i < n; i++) {
            TextView tv = (TextView) card.getChildAt(i);
            if (tv.isSelected())
                extractedText += tv.getText() + " ";
        }
        if (!extractedText.isEmpty())
            extractedText = extractedText.substring(0,
                    extractedText.length() - 1);
        return extractedText;
    }

    /**
     * Sets the height of the ScrollView that contains the bubble card
     * dynamically. This basically emulates MAX-HEIGHT functionality for the
     * scroll view. The max-height is accessed from the dimension resources
     */
    public void setBubbleCardScrollHeight() {
        View scroll = getBubbleCardParentView();
        View card = mCard;
        int maxHeight = mContext.getResources().getDimensionPixelSize(
                R.dimen.bubble_card_height);
        int width = mContext.getResources().getDisplayMetrics().widthPixels;
        int widthSpec = MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY);
        card.measure(widthSpec, MeasureSpec.UNSPECIFIED);
        if (card.getMeasuredHeight() > maxHeight) {
            ViewGroup.LayoutParams params = scroll.getLayoutParams();
            params.height = maxHeight;
            scroll.setLayoutParams(params);
        }
    }

    private View getBubbleCardParentView(){
        return mRoot.findViewById(R.id.bubble_card_parent);
    }

    private View getMeaningCard(){
        return mRoot.findViewById(R.id.meaning_card);
    }

    public interface SelectedTextChangedListener{
        void onSelectedTextChanged(String selectedText);
    }
}

