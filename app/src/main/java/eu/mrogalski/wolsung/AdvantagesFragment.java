package eu.mrogalski.wolsung;

import eu.mrogalski.wolsung.WolsungCharacter.Edge;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;

public class AdvantagesFragment extends AbstractCharacterFragment {

	public class EdgeSaver implements TextWatcher, OnCheckedChangeListener {

		@Override
		public void afterTextChanged(Editable s) {
			if(inhibitCharacterChanges) return;
			saveEdges();
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
		}

		@Override
		public void onCheckedChanged(CompoundButton buttonView,
				boolean isChecked) {
			if(inhibitCharacterChanges) return;
			saveEdges();
		}

	}

	public class AddNewRowWatcher implements TextWatcher {

		@Override
		public void afterTextChanged(Editable s) {
			if(s.toString().trim().length() > 0) {
				addLastRow();
				
				Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.edge_enter);
				animation.setStartOffset(0);
				lastRow.startAnimation(animation);
			}
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
		}

	}

	private EditText experiencePointsEditText;
	private EditText wealthEditText;
	private EditText currentWealthEditText;
	private TableLayout edgeTable;
	private TableRow lastRow;
	private TextWatcher lastRowWatcher;
	private EditText lastRowEditText;
	private EdgeSaver edgeSaver;

	@Override
	protected View createRootView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate( R.layout.advantages, container, false);
		experiencePointsEditText = (EditText) v.findViewById(R.id.experience_points);
		wealthEditText = (EditText) v.findViewById(R.id.wealth);
		currentWealthEditText = (EditText) v.findViewById(R.id.current_wealth);
		
		edgeTable = (TableLayout) v.findViewById(R.id.edge_table);
		lastRowWatcher = new AddNewRowWatcher();
		edgeSaver = new EdgeSaver();
		addLastRow();
		
		experiencePointsEditText.addTextChangedListener(new ManagedTextWatcher() {

			@Override
			protected void onChange(String text) {
				getCharacter().setExperience(tryInt(text));
			}
			
		});
		
		wealthEditText.addTextChangedListener(new ManagedTextWatcher() {

			@Override
			protected void onChange(String text) {
				getCharacter().setWealth(tryInt(text));
			}
			
		});
		
		currentWealthEditText.addTextChangedListener(new ManagedTextWatcher() {

			@Override
			protected void onChange(String text) {
				getCharacter().setCurrentWealth(tryInt(text));
			}
			
		});
		CharacterSheetActivity.applyFonts(v, CharacterSheetActivity.typeface );
		v.setDrawingCacheEnabled(true);
		return v;
	}

	private void addLastRow() {
		if(lastRowEditText != null) {
			lastRowEditText.removeTextChangedListener(lastRowWatcher);
		}
		LayoutInflater inflater = getActivity().getLayoutInflater();
		lastRow = (TableRow) inflater.inflate(R.layout.edge_row, edgeTable, false);
		lastRowEditText = (EditText) lastRow.findViewById(R.id.edge_name);
		lastRowEditText.setTypeface(CharacterSheetActivity.typeface);
		lastRowEditText.addTextChangedListener(edgeSaver);
		lastRowEditText.addTextChangedListener(lastRowWatcher);

		CheckBox cb = (CheckBox) lastRow.findViewById(R.id.edge_used);
		cb.setOnCheckedChangeListener(edgeSaver);
		edgeTable.addView(lastRow);
	}

	@Override
	public void refresh() {
		WolsungCharacter c = getCharacter();
		
		experiencePointsEditText.setText("" + c.getExperience());
		wealthEditText.setText("" + c.getWealth());
		currentWealthEditText.setText("" + c.getCurrentWealth());

		int edgeTableRowCount = edgeTable.getChildCount();
		while(edgeTableRowCount > 1) {			
			edgeTable.removeViewAt(1);
			edgeTableRowCount = edgeTable.getChildCount();
		}
		lastRow = null;
		lastRowEditText = null;

		LayoutInflater inflater = getActivity().getLayoutInflater();
		
		Edge[] edges = getCharacter().getEdges();
		for(Edge e : edges) {
			View row = inflater.inflate(R.layout.edge_row, edgeTable, false);
			EditText et = (EditText) row.findViewById(R.id.edge_name);
			et.setTypeface(CharacterSheetActivity.typeface);
			et.setText(e.name);
			et.addTextChangedListener(edgeSaver);
			CheckBox cb = (CheckBox) row.findViewById(R.id.edge_used);
			cb.setChecked(e.used);
			edgeTable.addView(row);
		}
		
		addLastRow();
		
	}

	public void saveEdges() {
		int count = edgeTable.getChildCount();
		Edge[] edges = new Edge[count - 2];
		for(int i = 1; i < count - 1; ++i) {
			Edge e = new Edge();
			e.used = ((CheckBox)edgeTable.getChildAt(i).findViewById(R.id.edge_used)).isChecked();
			e.name = ((EditText)edgeTable.getChildAt(i).findViewById(R.id.edge_name)).getText().toString();
			edges[i-1] = e;
		}
		getCharacter().setEdges(edges);
	}

}
