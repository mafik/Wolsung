package eu.mrogalski.wolsung;

import com.google.analytics.tracking.android.EasyTracker;

import android.database.DataSetObserver;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

public abstract class AbstractCharacterFragment extends Fragment {

	protected abstract class ManagedTextWatcher implements TextWatcher {

		@Override
		public void afterTextChanged(Editable s) {
			if (inhibitCharacterChanges)
				return;
			onChange(s.toString());
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
		}

		protected abstract void onChange(String text);
		
		protected int tryInt(String text, int def) {
			try {
				return Integer.parseInt(text);
			} catch(NumberFormatException ex) {
				return def;
			}
		}
		
		protected int tryInt(String text) {
			return tryInt(text, 0);
		}

	}

	static final String TAG = "AbstractCharacterFragment";
	protected ViewGroup rootView;
	protected CharacterSheetActivity activity;
	protected boolean inhibitCharacterChanges = true;

	public AbstractCharacterFragment() {
		super();
	}
	
	@Override
	public void onStart() {
		super.onStart();
		EasyTracker.getInstance().setContext(activity);
		EasyTracker.getTracker().sendView(this.getClass().getSimpleName());
	}

	@Override
	public void onResume() {
		super.onResume();
		refresh();
		inhibitCharacterChanges = false;
	}

	@Override
	public void onPause() {
		inhibitCharacterChanges = true;
		super.onPause();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt("scroll_position", rootView.getScrollY());
	}

	long createdTime;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			final Bundle savedInstanceState) {
		
		createdTime = System.currentTimeMillis();

		activity = (CharacterSheetActivity) inflater.getContext();
		activity.active_fragments.add(this);

		// Log.d(TAG, "Creating new view for " +
		// activity.active_character.getName());

		rootView = (ViewGroup) createRootView(inflater, container,
				savedInstanceState);

		rootView.getChildAt(0).setPadding(8, 4, 8, 8);

		if (savedInstanceState != null) {
			rootView.post(new Runnable() {
				@Override
				public void run() {
					rootView.scrollTo(0,
							savedInstanceState.getInt("scroll_position"));
				}
			});
		}

		return rootView;
	}

	@Override
	public void onDestroyView() {
		
		long now = System.currentTimeMillis();
		EasyTracker.getTracker().sendTiming("fragment activity", now - createdTime, this.getClass().getSimpleName(), null);
		
		super.onDestroyView();
		activity.active_fragments.remove(this);
	}

	protected abstract View createRootView(LayoutInflater inflater,
			ViewGroup container, final Bundle savedInstanceState);

	public WolsungCharacter getCharacter() {
		return activity.active_character;
	}

	public abstract void refresh();

	protected void bindListener(int viewId, AdapterView.OnItemSelectedListener listener) {
		Spinner spinner = (Spinner) rootView.findViewById(viewId);
		spinner.setOnItemSelectedListener(listener);
	}

	protected void bindSuggestions(int viewId, int arrayId) {
		final ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
				activity, arrayId, R.layout.spinner_item_light); // android.R.layout.simple_spinner_item
		
		SpinnerAdapter sa = new SpinnerAdapter() {
			
			@Override
			public void unregisterDataSetObserver(DataSetObserver observer) {
				adapter.unregisterDataSetObserver(observer);
			}
			
			@Override
			public void registerDataSetObserver(DataSetObserver observer) {
				adapter.registerDataSetObserver(observer);
			}
			
			@Override
			public boolean isEmpty() {
				return adapter.isEmpty();
			}
			
			@Override
			public boolean hasStableIds() {
				return adapter.hasStableIds();
			}
			
			@Override
			public int getViewTypeCount() {
				return adapter.getViewTypeCount();
			}
			
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				View view = adapter.getView(position, convertView, parent);
				CharacterSheetActivity.applyFonts(view, CharacterSheetActivity.typeface);
				return view;
			}
			
			@Override
			public int getItemViewType(int position) {
				return adapter.getItemViewType(position);
			}
			
			@Override
			public long getItemId(int position) {
				return adapter.getItemId(position);
			}
			
			@Override
			public Object getItem(int position) {
				return adapter.getItem(position);
			}
			
			@Override
			public int getCount() {
				return adapter.getCount();
			}
			
			@Override
			public View getDropDownView(int position, View convertView, ViewGroup parent) {
				View dropDownView = adapter.getDropDownView(position, convertView, parent);
				CharacterSheetActivity.applyFonts(dropDownView, CharacterSheetActivity.typeface);
				return dropDownView;
			}
		};
		adapter.setDropDownViewResource(R.layout.spinner_dropdown_item_light);
		Spinner spinner = (Spinner) rootView.findViewById(viewId);
		spinner.setAdapter(sa);
	}

}