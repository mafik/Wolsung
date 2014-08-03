package eu.mrogalski.wolsung;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.Spanned;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

public class BasicInfoFragment extends AbstractCharacterFragment {
	
	@SuppressWarnings("unused")
	private static final String TAG = "BasicInfoFragment";
	private EditText mNameEditText;
	private CheckBox raceAbility1;
	private CheckBox raceAbility2;
	private CheckBox raceAbility3;
	private CheckBox raceAbility4;
	private TextView raceWeaknessTextView;
	private Spinner mNationalitySpinner;
	private Spinner mRaceSpinner;
	private Spinner mArchetypeSpinner;
	private Spinner mProfessionSpinner;

	@Override
	protected View createRootView(LayoutInflater inflater,
            ViewGroup container, final Bundle savedInstanceState) {

        rootView = (ViewGroup) inflater.inflate(
                R.layout.base_description, container, false);
        
        
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(rootView.findViewById(R.id.nameEditText).getWindowToken(), 0);
		
		rootView.setFocusableInTouchMode(true);
		rootView.requestFocus();
		
		mNameEditText = (EditText) rootView.findViewById(R.id.nameEditText);

		mNationalitySpinner = (Spinner) rootView.findViewById(R.id.nationalitySpinner);
		mRaceSpinner = (Spinner) rootView.findViewById(R.id.raceSpinner);
		mArchetypeSpinner = (Spinner) rootView.findViewById(R.id.archetypeSpinner);
		mProfessionSpinner = (Spinner) rootView.findViewById(R.id.professionSpinner);

		bindSuggestions(R.id.nationalitySpinner, R.array.nationalities);
		bindSuggestions(R.id.raceSpinner, R.array.races);
		bindSuggestions(R.id.archetypeSpinner, R.array.archetypes);
		bindSuggestions(R.id.professionSpinner, R.array.professions);

		raceWeaknessTextView = (TextView) rootView.findViewById(R.id.raceWeaknessTextView);
		
		raceAbility1 = (CheckBox) rootView.findViewById(R.id.raceAbility1);
		raceAbility2 = (CheckBox) rootView.findViewById(R.id.raceAbility2);
		raceAbility3 = (CheckBox) rootView.findViewById(R.id.raceAbility3);
		raceAbility4 = (CheckBox) rootView.findViewById(R.id.raceAbility4);
		
		raceAbility1.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                buttonView.setSingleLine(!isChecked);
				if(inhibitCharacterChanges) return;
				getCharacter().putBoolean("racialAbility1", isChecked);
			}
		});
		
		raceAbility2.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                buttonView.setSingleLine(!isChecked);
				if(inhibitCharacterChanges) return;
				getCharacter().putBoolean("racialAbility2", isChecked);
			}
		});
		
		raceAbility3.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                buttonView.setSingleLine(!isChecked);
				if(inhibitCharacterChanges) return;
				getCharacter().putBoolean("racialAbility3", isChecked);
			}
		});
		
		raceAbility4.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                buttonView.setSingleLine(!isChecked);
				if(inhibitCharacterChanges) return;
				getCharacter().putBoolean("racialAbility4", isChecked);
			}
		});
		
		mNationalitySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				if(inhibitCharacterChanges) return;
				getCharacter().setNationality(position);
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) { }
		});

		bindListener(R.id.raceSpinner, new AdapterView.OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				if(inhibitCharacterChanges) return;
				Resources resources = getResources();
				String[] weaknesses = resources.getStringArray(
						R.array.race_weaknesses);
				String[] perks = resources.getStringArray(
						R.array.race_perks);
				String[] current_perks = perks[position].split("\\|");
				raceWeaknessTextView.setText(Html.fromHtml("<b>" + resources.getString(R.string.weakness) + ":</b> "
						+ weaknesses[position]));

                raceAbility1.setText(Html.fromHtml(current_perks[0].trim()));
				raceAbility2.setText(Html.fromHtml(current_perks[1].trim()));
				raceAbility3.setText(Html.fromHtml(current_perks[2].trim()));
				raceAbility4.setText(Html.fromHtml(current_perks[3].trim()));
				
				raceWeaknessTextView.post(new Runnable() {
					@Override
					public void run() {
						raceWeaknessTextView.requestLayout();			
					}
				});
				
				//raceWeaknessTextView.requestLayout();
				//int pixels = raceWeaknessTextView.getLineCount() * raceWeaknessTextView.getLineHeight(); //approx height text
				//raceWeaknessTextView.setHeight(pixels);
				
				getCharacter().setRace(position);
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				if(inhibitCharacterChanges) return;
				raceWeaknessTextView.setText(Html
						.fromHtml("<b>" + getActivity().getString(R.string.weakness) + ":</b> brak"));

				raceWeaknessTextView.post(new Runnable() {
					@Override
					public void run() {
						raceWeaknessTextView.requestLayout();			
					}
				});
			}

		});

		final TextView professionAbilityTextView = (TextView) rootView.findViewById(R.id.professionAbilityTextView);
		final TextView professionPrestigeTextView = (TextView) rootView.findViewById(R.id.professionPrestigeTextView);

		bindListener(R.id.professionSpinner, new AdapterView.OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				if(inhibitCharacterChanges) return;
				String[] abilities = getResources().getStringArray(
						R.array.profession_abilities);
				String[] prestige = getResources().getStringArray(
						R.array.professions_prestige);
				professionAbilityTextView.setText(Html
						.fromHtml("<b>" + getActivity().getString(R.string.ability) + ":</b> " + abilities[position]));
				professionPrestigeTextView.setText(Html
						.fromHtml("<b>" + getActivity().getString(R.string.prestige) + ":</b> " + prestige[position]));
				
				rootView.post(new Runnable() {
					@Override
					public void run() {
						professionAbilityTextView.requestLayout();
						professionPrestigeTextView.requestLayout();			
					}
				});
				
				getCharacter().setProfession(position);
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				if(inhibitCharacterChanges) return;
				professionAbilityTextView.setText(Html
						.fromHtml("<b>" + getActivity().getString(R.string.ability) + ":</b> brak"));
				professionPrestigeTextView.setText(Html
						.fromHtml("<b>" + getActivity().getString(R.string.prestige) + ":</b> brak"));
				
				rootView.post(new Runnable() {
					@Override
					public void run() {
						professionAbilityTextView.requestLayout();
						professionPrestigeTextView.requestLayout();			
					}
				});
			}
		});

		final TextView archetypeCardsTextView = (TextView) rootView.findViewById(R.id.archetypeCardsTextView);

		bindListener(R.id.archetypeSpinner, new AdapterView.OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				if(inhibitCharacterChanges) return;
				String[] cards = getResources().getStringArray(
						R.array.archetype_cards);
				archetypeCardsTextView.setText(Html.fromHtml(cards[position]));

				archetypeCardsTextView.post(new Runnable() {
					@Override
					public void run() {
						archetypeCardsTextView.requestLayout();			
					}
				});
				
				getCharacter().setArchetype(position);
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				if(inhibitCharacterChanges) return;
				archetypeCardsTextView.setText(Html.fromHtml("<b>Błąd!</b>"));

				archetypeCardsTextView.post(new Runnable() {
					@Override
					public void run() {
						archetypeCardsTextView.requestLayout();			
					}
				});
			}
		});
		
		mNameEditText.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {}
			
			@Override
			public void afterTextChanged(Editable s) {
				if(inhibitCharacterChanges) return;
				getCharacter().setName(s.toString());
				activity.refreshSpinner(true);
			}
		});
        

		CharacterSheetActivity.applyFonts(rootView, CharacterSheetActivity.typeface );

		rootView.setDrawingCacheEnabled(true);
        return rootView;	
	}

	@Override
	public void refresh() {
		final WolsungCharacter c = getCharacter();
		mNameEditText.setText(c.getName());
		mRaceSpinner.setSelection(c.getRace());
		mArchetypeSpinner.setSelection(c.getArchetype());
		mProfessionSpinner.setSelection(c.getProfession());
		mNationalitySpinner.setSelection(c.getNationality());
		raceAbility1.setChecked(c.getBoolean("racialAbility1", false));
		raceAbility2.setChecked(c.getBoolean("racialAbility2", false));
		raceAbility3.setChecked(c.getBoolean("racialAbility3", false));
		raceAbility4.setChecked(c.getBoolean("racialAbility4", false));
	}
}
