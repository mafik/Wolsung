package eu.mrogalski.wolsung;

import java.util.Arrays;
import java.util.Comparator;

import org.json.JSONArray;
import org.json.JSONException;

import android.content.res.Resources;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TableLayout;
import android.widget.TextView;

import eu.mrogalski.MArray;

public class AttributesFragment extends AbstractCharacterFragment implements
		OnCheckedChangeListener, TextWatcher,
		CompoundButton.OnCheckedChangeListener {

	private static final String TAG = "AttributesFragment";

	private static final int[] ATTRIBUTES = { R.id.brawnRadioGroup,
			R.id.agilityRadioGroup, R.id.witsRadioGroup,
			R.id.composureRadioGroup, R.id.charismaRadioGroup };

	private static final int[] ATTR_VALUES = { R.id.choiceFirst,
			R.id.choiceSecond, R.id.choiceThird };

	private static final int[] DAMAGE = { R.id.brawnDamage, R.id.agilityDamage,
			R.id.witsDamage, R.id.composureDamage, R.id.charismaDamage };

	/*
	private static final int[] SECONDARY_BASE = { R.id.constitution_base,
			R.id.reputation_base, R.id.defense_base, R.id.endurance_base,
			R.id.confidence_base };
			*/

	private static final int[] SECONDARY_MOD = { R.id.constitution_mod,
			R.id.reputation_mod, R.id.defense_mod, R.id.endurance_mod,
			R.id.confidence_mod };

	private static final int[] SECONDARY_DMG = { R.id.constitution_dmg,
			R.id.reputation_dmg, R.id.defense_dmg, R.id.endurance_dmg,
			R.id.confidence_dmg };

	private CheckBox[] mSkillTrainedCheckBoxes;
	private EditText[] mSkillSpecialitiesEditTexts;

	@Override
	protected View createRootView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.attributes, container, false);

		for (int id : ATTRIBUTES) {
			((RadioGroup) v.findViewById(id)).setOnCheckedChangeListener(this);
		}

		for (int id : DAMAGE) {
			EditText e = (EditText) v.findViewById(id);
			e.addTextChangedListener(this);
		}

		TextWatcher secondaryDmgTextWatcher = new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				if (inhibitCharacterChanges)
					return;

				View v = getView();

				JSONArray array = new JSONArray();
				for (int id : SECONDARY_DMG) {
					EditText e = (EditText) v.findViewById(id);
					int val = 0;
					try {
						val = Integer.parseInt(e.getText().toString()); 
					} catch(NumberFormatException ex) {}
					array.put(val);
				}
				getCharacter().setSecondaryDamage(array);

			}
		};

		for (int id : SECONDARY_DMG) {
			EditText e = (EditText) v.findViewById(id);
			e.addTextChangedListener(secondaryDmgTextWatcher);
		}

		TextWatcher secondaryModTextWatcher = new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				if (inhibitCharacterChanges)
					return;

				View v = getView();

				JSONArray array = new JSONArray();
				for (int id : SECONDARY_MOD) {
					EditText e = (EditText) v.findViewById(id);
					int val = 0;
					try {
						val = Integer.parseInt(e.getText().toString()); 
					} catch(NumberFormatException ex) {}
					array.put(val);
				}
				getCharacter().setSecondaryAttributesMods(array);

			}
		};

		for (int id : SECONDARY_MOD) {
			EditText e = (EditText) v.findViewById(id);
			e.addTextChangedListener(secondaryModTextWatcher);
		}

		TableLayout t = (TableLayout) v.findViewById(R.id.skillTable);

		Resources r = getResources();

		String[] skill_names = r.getStringArray(R.array.skill_names);
		String[] attribute_short_names = r
				.getStringArray(R.array.attribute_names_short);
		int[] skill_attributes = r.getIntArray(R.array.skill_attributes);

		mSkillTrainedCheckBoxes = new CheckBox[skill_names.length];
		mSkillSpecialitiesEditTexts = new EditText[skill_names.length];

		int[] indicies = MArray.indexSort(skill_names,
				new Comparator<String>() {

					@Override
					public int compare(String lhs, String rhs) {
						return lhs.compareTo(rhs);
					}

				});

		for (int _i = 0; _i < skill_names.length; ++_i) {
			int i = indicies[_i];
			View row = inflater.inflate(R.layout.skill_row, t, false);
			String skill_name = skill_names[i];
			int skill_attribute = skill_attributes[i];
			String attribute_short_name = attribute_short_names[skill_attribute];
			final int skillNumber = i;

			TextView skillNameTextView = (TextView) row
					.findViewById(R.id.skill_name);
			TextView skillBaseTextView = (TextView) row
					.findViewById(R.id.skill_base);
			CheckBox skillTrainedCheckBox = (CheckBox) row
					.findViewById(R.id.skill_trained);
			EditText skillSpecialitiesEditText = (EditText) row
					.findViewById(R.id.skill_specialities);

			skillNameTextView.setText(skill_name);
			skillBaseTextView.setText(attribute_short_name);
			skillTrainedCheckBox.setTag(Integer.valueOf(i));
			skillTrainedCheckBox.setOnCheckedChangeListener(this);
			skillSpecialitiesEditText.addTextChangedListener(new TextWatcher() {

				@Override
				public void onTextChanged(CharSequence s, int start,
						int before, int count) {
				}

				@Override
				public void beforeTextChanged(CharSequence s, int start,
						int count, int after) {
				}

				@Override
				public void afterTextChanged(Editable s) {
					if (inhibitCharacterChanges)
						return;
					getCharacter().setSkillSpecialities(skillNumber,
							s.toString());

				}
			});

			mSkillTrainedCheckBoxes[i] = skillTrainedCheckBox;
			mSkillSpecialitiesEditTexts[i] = skillSpecialitiesEditText;

			t.addView(row);
		}
		
		CharacterSheetActivity.applyFonts(v, CharacterSheetActivity.typeface );

		v.setDrawingCacheEnabled(true);
		return v;
	}

	@Override
	public void refresh() {
		View v = getView();

		WolsungCharacter character = getCharacter();
		JSONArray array = character.getAttributes();
		for (int i = 0; i < ATTRIBUTES.length; ++i) {
			int id = ATTRIBUTES[i];
			int val = 0;
			try {
				val = array.getInt(i);
			} catch (JSONException e) {
			}
			int val_id = ATTR_VALUES[val];
			RadioGroup g = (RadioGroup) v.findViewById(id);
			RadioButton b = (RadioButton) g.findViewById(val_id);
			b.setChecked(true);
		}

		array = character.getDamage();
		for (int i = 0; i < DAMAGE.length; ++i) {
			int id = DAMAGE[i];
			int val = 0;
			try {
				val = array.getInt(i);
			} catch (JSONException e) {
			}
			EditText e = (EditText) v.findViewById(id);
			e.setText(Integer.toString(val));
		}

		fillTextViews(SECONDARY_DMG, character.getSecondaryDamage());
		fillTextViews(SECONDARY_MOD, character.getSecondaryAttributesMods());

		recalcSecondary();

		for (int i = 0; i < mSkillTrainedCheckBoxes.length; ++i) {
			boolean skillTrained = character.isSkillTrained(i);
			String skillSpecialities = character.getSkillSpecialities(i);
			mSkillTrainedCheckBoxes[i].setChecked(skillTrained);
			mSkillSpecialitiesEditTexts[i].setText(skillSpecialities);
		}

	}

	private void fillTextViews(int[] idArr, JSONArray array) {
		View v = getView();
		for (int i = 0; i < idArr.length; ++i) {
			int id = idArr[i];
			int val = 0;
			try {
				val = array.getInt(i);
			} catch (JSONException e) {
			}
			EditText e = (EditText) v.findViewById(id);
			e.setText(Integer.toString(val));
		}
	}

	private void recalcSecondary() {
		JSONArray attributes = getCharacter().getAttributes();
		JSONArray damage = getCharacter().getDamage();
		int[] values = new int[5];
		for (int i = 0; i < 5; ++i) {
			try {
				values[i] = attributes.getInt(i) - damage.getInt(i) + 1;
			} catch (JSONException e) {
				e.printStackTrace();
				Log.e(TAG, "Couldn't retrieve attribute/damage number " + i);
				values[i] = 0;
			}
		}
		int constitution = Math.max(values[0], values[1]);
		int reputation = Math.max(values[3], values[4]);
		fillText(R.id.constitution_base, "" + constitution);
		fillText(R.id.reputation_base, "" + reputation);
		fillText(R.id.defense_base, "" + (10 + 2 * constitution));
		fillText(R.id.endurance_base, "" + (10 + 2 * constitution));
		fillText(R.id.confidence_base, "" + (10 + 2 * reputation));
	}

	private void fillText(int viewId, String text) {
		((TextView) getView().findViewById(viewId)).setText(text);
	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		if (inhibitCharacterChanges)
			return;

		int array_position = Arrays.binarySearch(ATTRIBUTES, group.getId());
		int array_value = Arrays.binarySearch(ATTR_VALUES, checkedId);

		JSONArray array = getCharacter().getAttributes();
		try {
			array.put(array_position, array_value);
		} catch (JSONException e) {
			Log.e(TAG, "Couldn't set attribute value", e);
		}
		getCharacter().setAttributes(array);

		recalcSecondary();
	}

	@Override
	public void afterTextChanged(Editable s) {
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		if (inhibitCharacterChanges)
			return;

		View v = getView();

		JSONArray array = new JSONArray();
		for (int id : DAMAGE) {
			EditText e = (EditText) v.findViewById(id);
			try {
				array.put(Integer.parseInt(e.getText().toString()));
			} catch (NumberFormatException ex) {
				array.put(0);
			}
		}
		getCharacter().setDamage(array);

		recalcSecondary();
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		if (inhibitCharacterChanges)
			return;
		Integer skillInteger = (Integer) buttonView.getTag();
		getCharacter().setSkillTrained(skillInteger.intValue(), isChecked);
	}

}
