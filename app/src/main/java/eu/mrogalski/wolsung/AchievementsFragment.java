package eu.mrogalski.wolsung;

import java.util.ArrayList;

import eu.mrogalski.wolsung.WolsungCharacter.Achievement;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;

public class AchievementsFragment extends AbstractCharacterFragment implements TextWatcher, OnCheckedChangeListener {

	private ArrayList<CheckBox> checkBoxes;
	private ArrayList<EditText> editTexts;

	@Override
	protected View createRootView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate( R.layout.achievements, container, false);
		checkBoxes = new ArrayList<CheckBox>();
		editTexts = new ArrayList<EditText>();
		browse((ViewGroup)v);
		CharacterSheetActivity.applyFonts(v, CharacterSheetActivity.typeface );
		v.setDrawingCacheEnabled(true);
		return v;
	}

	private void browse(ViewGroup v) {
		int length = v.getChildCount();
		for(int i = 0; i < length; ++i) {
			View child = v.getChildAt(i);
			if(child instanceof ViewGroup) {
				browse((ViewGroup) child);
			} else if(child instanceof CheckBox) {
				CheckBox cb = (CheckBox) child;
				cb.setOnCheckedChangeListener(this);
				checkBoxes.add(cb);
			} else if(child instanceof EditText) {
				EditText et = (EditText) child;
				et.addTextChangedListener(this);
				editTexts.add(et);
			}
		}
	}

	@Override
	public void refresh() {
		Achievement[] achievements = getCharacter().getAchievements();
		int i;
		for(i = 0; i < achievements.length; ++i) {
			checkBoxes.get(i).setChecked(achievements[i].used);
			editTexts.get(i).setText(achievements[i].name);
		}
		for(; i < checkBoxes.size(); ++i) {
			checkBoxes.get(i).setChecked(false);
			editTexts.get(i).setText("");
		}
	}

	private void saveAchievements() {
		Achievement[] achievements = new Achievement[checkBoxes.size()];
		for(int i = 0; i < achievements.length; ++i) {
			Achievement a = new Achievement();
			a.name = editTexts.get(i).getText().toString();
			a.used = checkBoxes.get(i).isChecked();
			achievements[i] = a;
		}
		getCharacter().setAchievements(achievements);
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		if(inhibitCharacterChanges) return;
		saveAchievements();
	}

	@Override
	public void afterTextChanged(Editable s) {
		if(inhibitCharacterChanges) return;
		saveAchievements();
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {}

}
