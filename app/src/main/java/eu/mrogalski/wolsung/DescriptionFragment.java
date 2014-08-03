package eu.mrogalski.wolsung;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

public class DescriptionFragment extends AbstractCharacterFragment implements TextWatcher {

	@SuppressWarnings("unused")
	private static final String TAG = "DescriptionFragment";
	private EditText mDescription;
	private EditText mScars;
	private EditText mTeam;

	@Override
	protected View createRootView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate( R.layout.description, container, false);

		mDescription = (EditText) v.findViewById(R.id.description_extended);
		mScars = (EditText) v.findViewById(R.id.description_scars);
		mTeam = (EditText) v.findViewById(R.id.description_team);
		
		mDescription.addTextChangedListener(this);
		mScars.addTextChangedListener(this);
		mTeam.addTextChangedListener(this);
		CharacterSheetActivity.applyFonts(v, CharacterSheetActivity.typeface );

		v.setDrawingCacheEnabled(true);
		return v;
	}

	@Override
	public void refresh() {
		WolsungCharacter character = getCharacter();
		String description = character.getDescription();
		String scars = character.getScars();
		String fellowship = character.getFellowship();
		inhibitCharacterChanges = true;
		mDescription.setText(description);
		mScars.setText(scars);
		mTeam.setText(fellowship);
		inhibitCharacterChanges = false;
		//Log.d(TAG, "Results for currently selected character: " + description + ", " + scars + ", " + fellowship);
	}

	@Override
	public void afterTextChanged(Editable s) {}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		if(inhibitCharacterChanges) return;
		WolsungCharacter character = getCharacter();
		character.setDescription(mDescription.getText().toString());
		character.setScars(mScars.getText().toString());
		character.setFellowship(mTeam.getText().toString());
	}

}
