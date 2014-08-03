package eu.mrogalski.wolsung;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;

import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Typeface;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

public class CharacterListSpinnerAdapter implements SpinnerAdapter {

	private JSONArray mCharacterList;
	private ArrayList<DataSetObserver> mObserverList;
	private ActionBarActivity mActivity;

	public CharacterListSpinnerAdapter(ActionBarActivity activity, JSONArray character_list) {
		this.mActivity = activity;
		this.mCharacterList = character_list;
		this.mObserverList = new ArrayList<DataSetObserver>();
	}

	@Override
	public int getCount() {
		return mCharacterList.length();
	}

	@Override
	public Object getItem(int position) {
		try {
			return mCharacterList.get(position);
		} catch (JSONException e) {
			return null;
		}
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public int getItemViewType(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		return prepareTextView(position, R.layout.spinner_item_ab);
	}

	private TextView prepareTextView(int position, int layoutResId) {
		Context ctx = mActivity.getSupportActionBar().getThemedContext();

		TextView a = (TextView) mActivity.getLayoutInflater().inflate(layoutResId, null);
		a.setTypeface(/*font*/ CharacterSheetActivity.typeface);
        //a.setTextColor(0xffcccccc);
		// a.setTextSize(a.getTextSize() * 2 );
		a.setShadowLayer(0.0001f, 0, 2, 0xff000000);
		String name = "???";
		try {
			name = mCharacterList.getJSONObject(position).getString("name");
		} catch (JSONException e) {}
		a.setText(name);
		return a;
	}

	@Override
	public int getViewTypeCount() {
		return 1;
	}

	@Override
	public boolean hasStableIds() {
		return false;
	}

	@Override
	public boolean isEmpty() {
		return mCharacterList.length() == 0;
	}

	@Override
	public void registerDataSetObserver(DataSetObserver observer) {
		mObserverList.add(observer);
	}

	@Override
	public void unregisterDataSetObserver(DataSetObserver observer) {
		mObserverList.remove(observer);
	}

	@Override
	public View getDropDownView(int position, View convertView, ViewGroup parent) {
		return prepareTextView(position, R.layout.spinner_dropdown_item_ab);
	}
	
	public void refresh() {
		for(DataSetObserver dso : mObserverList) {
			dso.onChanged();
		}
	}

}
