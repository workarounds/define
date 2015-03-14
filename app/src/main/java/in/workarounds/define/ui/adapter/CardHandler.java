package in.workarounds.define.ui.adapter;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Locale;

import in.workarounds.define.R;
import in.workarounds.define.model.DictResult;
import in.workarounds.define.model.Dictionary;
import in.workarounds.define.model.WordnetDictionary;
import in.workarounds.define.service.PopupManager;
import in.workarounds.define.ui.view.FlowLayout;
import in.workarounds.define.util.LogUtils;
import in.workarounds.define.util.StringUtils;

public class CardHandler implements OnTouchListener, OnClickListener {
    private static final String TAG = LogUtils.makeLogTag(CardHandler.class);
    private PopupManager mPopupManager;
	private ViewGroup mCard;

	/**
	 * instance of dictionary class
	 */
	private Dictionary mDictionary;

	/**
	 * result list
	 */
	private DictResult[] mResultList;

	public CardHandler(PopupManager popupManager) {
		mPopupManager = popupManager;
		mCard = mPopupManager.getBubbleCardView();
		mDictionary = new WordnetDictionary(mPopupManager);
		initialize();
	}

	private void initialize() {
		View root = mPopupManager.getRootView();
		ImageButton copyButton = (ImageButton) root
				.findViewById((R.id.action_copy));
		copyButton.setOnClickListener(this);
	}

	/**
	 * Adds a TextView for each word in words[] array to the FlowLayout with
	 * spacing dimension mentioned in the resources. Removes all the views in
	 * the FlowLayout to avoid concatenation of ClipData
	 * 
	 * @param words array containing each word in the text of the ClipData
	 */
	public void showBubbles(String[] words) {
		FlowLayout card = (FlowLayout) mPopupManager.getBubbleCardView();
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
		mPopupManager.setBubbleCardScrollHeight();
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
		FlowLayout card = (FlowLayout) mPopupManager.getBubbleCardView();

		Resources res = mPopupManager.getResources();
		int horizontal_spacing = res
				.getDimensionPixelSize(R.dimen.bubble_horizontal_spacing);
		int vertical_spacing = res
				.getDimensionPixelSize(R.dimen.bubble_vertical_spacing);
		FlowLayout.LayoutParams params = new FlowLayout.LayoutParams(
				horizontal_spacing, vertical_spacing, false);

		FlowLayout.LayoutParams paramsNewLine = new FlowLayout.LayoutParams(
				horizontal_spacing, vertical_spacing, true);

		LayoutInflater inflater = mPopupManager.getInflater();

		View v = inflater.inflate(R.layout.text_bubble, card, false);
		TextView tv = (TextView) v.findViewById(R.id.text_bubble);

		tv.setText(text);
		if (newLine)
			card.addView(tv, paramsNewLine);
		else
			card.addView(tv, params);
	}

	/**
	 * Searches for the meaning and displays the results in meanings card
	 * 
	 * @param wordForm
	 *            the word of whose meaning the user is looking for
	 */
	public void showMeanings(String wordForm) {
		mResultList = new DictResult[] {};

		ViewGroup meaningCard = mPopupManager.getMeaningCard();
		meaningCard.setVisibility(View.VISIBLE);

		ViewGroup meanings = (ViewGroup) meaningCard
				.findViewById(R.id.meanings);

		TextView word = (TextView) meaningCard.findViewById(R.id.word);
		String wordText = capitalizeFirstLetter(StringUtils.preProcessWord(wordForm));
		word.setText(wordText);

		updateMeanings(wordForm);
		addMeaningsToScrollView(meanings);
	}

	/**
	 * This function does basically does tha work of an adapter. It populates
	 * the meanings ScrollView with all the meanings from the DictResults[]
	 * 
	 * @param meanings
	 *            The ViewGroup(ScrollView) to which the meanings should be
	 *            added and displayed
	 */
	private void addMeaningsToScrollView(ViewGroup meanings) {
		meanings.removeAllViews();
		for (DictResult dr : mResultList) {
			View meaningRow = mPopupManager.getInflater().inflate(
					R.layout.meaning_row, meanings, false);
			TextView def = (TextView) meaningRow.findViewById(R.id.definition);
			def.setText(dr.getMeaning());
			TextView type = (TextView) meaningRow
					.findViewById(R.id.meaning_type);
			type.setText(dr.getType());
			TextView synonyms = (TextView) meaningRow
					.findViewById(R.id.synonyms);
			String syns = "";
			for (String syn : dr.getSynonyms()) {
				syns += syn + ", ";
			}
			synonyms.setText(syns);
			TextView usage = (TextView) meaningRow.findViewById(R.id.usage);
			String uses = "";
			for (String use : dr.getUsage()) {
				uses += use + ", ";
			}
			usage.setText(uses);

			def.setText(dr.getMeaning());
			meanings.addView(meaningRow);
		}
	}

	/**
	 * updates the Meanings in the result list
	 * 
	 * @param wordForm
	 */
	private void updateMeanings(String wordForm) {
		ArrayList<DictResult> results = mDictionary.getMeanings(wordForm);
		mResultList = new DictResult[results.size()];
		mResultList = results.toArray(mResultList);
		for (DictResult dr : mResultList) {
			Log.d("CardHandler", "results[] = " + dr);
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
			showMeanings(extractTextFromCard());
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
		ViewGroup card = mPopupManager.getBubbleCardView();
		int n = card.getChildCount();
		for (int i = 0; i < n; i++) {
			View v = card.getChildAt(i);
			v.setTag(false);
		}
	}

	private void clearSelected() {
		ViewGroup card = mPopupManager.getBubbleCardView();
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

	private String extractTextFromCard() {
		ViewGroup card = mPopupManager.getBubbleCardView();
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

	@Override
	public void onClick(View v) {
		String extractedText = extractTextFromCard();
		if (!extractedText.isEmpty()) {
			clipText(extractedText);
		}
	}

	private void clipText(String text) {
		ClipboardManager clipboardManager = (ClipboardManager) mPopupManager
				.getSystemService(Context.CLIPBOARD_SERVICE);
		ClipData clip = ClipData.newPlainText("RECOPY", text);
		clipboardManager.setPrimaryClip(clip);
		// Give some feedback
	}

}
