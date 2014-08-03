package eu.mrogalski.wolsung;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class WolsungCharacter {
	private static final String TAG = "Character";
	public static final String[] SKILL_NAMES = { "academics", "athletics",
			"bluff", "brawl", "courage", "drive", "empathy", "expression",
			"firearms", "intimidation", "larceny", "occult", "persuassion",
			"research", "spot", "stealth", "streetwise", "survival", "technics" };
	private JSONObject json;
	public int index;

	private WolsungCharacter() {
		json = new JSONObject();
	}

	public WolsungCharacter(JSONArray arr, int index) {
		this.index = index;
		try {
			this.json = arr.getJSONObject(index);
		} catch (JSONException e) {
			Log.e(TAG, "Couldn't get character at index " + index);
			this.json = new JSONObject();
		}
	}

	public static WolsungCharacter makeDefaultAppendingTo(
			JSONArray character_list) {
		WolsungCharacter c = makeDefault();
		c.index = character_list.length();
		character_list.put(c.json);
		return c;
	}

	private static WolsungCharacter makeDefault() {
		WolsungCharacter c = new WolsungCharacter();
		c.setName("Johnny Walker");
		return c;
	}

	public JSONObject getJSONObject() {
		return json;
	}

	// Field access:

	public void setName(String name) {
		putString("name", name);
	}

	public void setRace(int race) {
		putInt("race", race);
	}

	public void setProfession(int profession) {
		putInt("profession", profession);
	}

	public void setNationality(int nationality) {
		putInt("nationality", nationality);
	}

	public void setArchetype(int archetype) {
		putInt("archetype", archetype);
	}

	public String getName() {
		return getString("name", "");
	}

	public int getRace() {
		return getInt("race", 0);
	}

	public int getNationality() {
		return getInt("nationality", 0);
	}

	public int getProfession() {
		return getInt("profession", 0);
	}

	public int getArchetype() {
		return getInt("archetype", 0);
	}

	// Low-level field access

	public void putString(String field, String value) {
		try {
			json.put(field, value);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public String getString(String field, String fallback) {
		try {
			return json.getString(field);
		} catch (JSONException e) {
			return fallback;
		}
	}

	public void putInt(String field, int value) {
		try {
			json.put(field, value);
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

	public int getInt(String field, int fallback) {
		try {
			return json.getInt(field);
		} catch (JSONException e) {
			return fallback;
		}
	}

	public void putBoolean(String field, boolean value) {
		try {
			json.put(field, value);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public boolean getBoolean(String field, boolean fallback) {
		try {
			return json.getBoolean(field);
		} catch (JSONException e) {
			return fallback;
		}
	}

	public JSONArray getJSONArray(String field, String defaultValue)
			throws JSONException {
		try {
			return json.getJSONArray(field);
		} catch (JSONException e) {
			return new JSONArray(defaultValue);
		}
	}

	public JSONObject getJSONObject(String field) throws JSONException {
		try {
			return json.getJSONObject(field);
		} catch (JSONException e) {
			throw e;
		}
	}

	public void setJSONObject(String field, JSONObject object)
			throws JSONException {
		try {
			json.put(field, object);
		} catch (JSONException e) {
			Log.e(TAG, "Couldn't set field '" + field + "' to object '"
					+ object.toString() + "'.");
			throw e;
		}
	}

	public void setJSONArray(String field, JSONArray array) {
		try {
			json.put(field, array);
		} catch (JSONException e) {
			Log.e(TAG,
					"Couldn't set field '" + field + "' to array '"
							+ array.toString() + "'.");
		}
	}

	public void setAttributes(JSONArray array) {
		setJSONArray("attributes", array);
	}

	public void setDamage(JSONArray array) {
		setJSONArray("damage", array);
	}

	public JSONArray getAttributes() {
		try {
			return getJSONArray("attributes", "[0,0,0,0,0]");
		} catch (JSONException e) {
			Log.e(TAG, "Couldn't get or create array for attributes.");
			return new JSONArray();
		}
	}

	public JSONArray getDamage() {
		try {
			return getJSONArray("damage", "[0,0,0,0,0]");
		} catch (JSONException e) {
			Log.e(TAG, "Couldn't get or create array for damage.");
			return new JSONArray();
		}
	}
	
	// Begin secondary attribute hell

	public void setSecondaryAttributesMods(JSONArray array) {
		setJSONArray("secondary_attributes", array);
	}

	public JSONArray getSecondaryAttributesMods() {
		try {
			return getJSONArray("secondary_attributes", "[0,0,0,0,0]");
		} catch (JSONException e) {
			Log.e(TAG, "Couldn't get or create array for secondary attributes.");
			return new JSONArray();
		}
	}

	public void setSecondaryDamage(JSONArray array) {
		setJSONArray("secondary_damage", array);
	}

	public JSONArray getSecondaryDamage() {
		try {
			return getJSONArray("secondary_damage", "[0,0,0,0,0]");
		} catch (JSONException e) {
			Log.e(TAG, "Couldn't get or create array for secondary damage.");
			return new JSONArray();
		}
	}
	
	// End secondary attribute hell
	
	// Begin skill hell

	private JSONObject makeSkill() {
		JSONObject skill = new JSONObject();
		try {
			skill.put("trained", false);
			skill.put("specialities", "");
		} catch (JSONException e1) {
			Log.e(TAG,
					"Disturbing error during default JSON skill contruction",
					e1);
		}
		return skill;
	}
	
	public boolean isSkillTrained(int number) {
		try {
			return getSkill(number).getBoolean("trained");
		} catch (JSONException e) {
			Log.e(TAG, "Malformed skill object!", e);
			return false;
		}
	}
	
	public void setSkillTrained(int number, boolean trained) {
		try {
			JSONObject skill = getSkill(number);
			skill.put("trained", trained);
			setSkill(number, skill);
		} catch (JSONException e1) {
			Log.e(TAG,
					"Disturbing error during setting trained skill",
					e1);
		}
	}
	
	public String getSkillSpecialities(int number) {
		try {
			return getSkill(number).getString("specialities");
		} catch (JSONException e) {
			// TODO: fix whole class
			return "";
		}
	}
	
	public void setSkillSpecialities(int number, String specialities) {
		try {
			getSkill(number).put("specialities", specialities);
		} catch (JSONException e) {
			// TODO Fix whole class
		}
	}

	public JSONObject getSkill(int number) {
		try {
			return getSkills().getJSONObject(SKILL_NAMES[number]);
		} catch (JSONException e) {
			Log.e(TAG, "Malformed skill object!", e);
			return makeSkill();
		}
	}

	public void setSkill(int number, JSONObject skill) {
		try {
			JSONObject skills = getSkills();
			skills.put(SKILL_NAMES[number], skill);
			setSkills(skills);
		} catch (JSONException e1) {
			Log.e(TAG, "Disturbing error during putting skill \"" + number
					+ "\" in json object.", e1);
		}
	}

	/**
	 * Get skill object. If such object doesn't yet exist, create it and assign
	 * default values.
	 * 
	 * @return Retrieved or created skill object.
	 */
	public JSONObject getSkills() {
		try {
			return getJSONObject("skills");
		} catch (JSONException e) {
			setSkills(new JSONObject());
			for (int i = 0; i < SKILL_NAMES.length; ++i) {
				setSkill(i, makeSkill());
			}
			return getSkills();
		}
	}

	public void setSkills(JSONObject skills) {
		try {
			setJSONObject("skills", skills);
		} catch (JSONException e1) {
			Log.e(TAG, "Disturbing error during putting skill object in character.", e1);
		}
	}
	
	// End skill hell
	
	// Begin bean hell

	public String getDescription() {
		return getString("description", "");
	}

	public void setDescription(String desc) {
		putString("description", desc);
	}

	public String getScars() {
		return getString("scars", "");
	}

	public void setScars(String scars) {
		putString("scars", scars);
	}

	public String getFellowship() {
		return getString("fellowship", "");
	}

	public void setFellowship(String fellowship) {
		putString("fellowship", fellowship);
	}
	
	public int getExperience() {
		return getInt("experience", 0);
	}
	
	public void setExperience(int experience) {
		putInt("experience", experience);
	}
	
	public int getWealth() {
		return getInt("wealth", 3);
	}
	
	public void setWealth(int wealth) {
		putInt("wealth", wealth);
	}
	
	public int getCurrentWealth() {
		return getInt("current_wealth", 3);
	}
	
	public void setCurrentWealth(int current_wealth) {
		putInt("current_wealth", current_wealth);
	}
	
	// End bean hell
	
	public static class Edge {
		String name;
		boolean used;
		
		public static Edge fromJSON(JSONObject obj) throws JSONException {
			Edge e = new Edge();
			e.name = obj.getString("name");
			e.used = obj.getBoolean("used");
			return e;
		}
		
		public JSONObject toJSON() {
			JSONObject obj = new JSONObject();
			try {
				obj.put("name", name);
				obj.put("used", used);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return obj;
		}
	}

	public void setEdges(Edge[] edges) {
		JSONArray arr = new JSONArray();
		for(int i = 0; i < edges.length; ++i) {
			arr.put(edges[i].toJSON());
		}
		try {
			json.put("edges", arr);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public Edge[] getEdges() {
		try {
			JSONArray arr = json.getJSONArray("edges");
			int length = arr.length();
			Edge[] edges = new Edge[length];
			for(int i = 0; i < length; ++i) {
				JSONObject obj = arr.getJSONObject(i);
				edges[i] = Edge.fromJSON(obj);
			}
			return edges;
		} catch (JSONException e) {
			return new Edge[0];
		}
	}
	
	// Achievements
	
	public static class Achievement {
		String name;
		boolean used;
		
		public static Achievement fromJSON(JSONObject obj) throws JSONException {
			Achievement a = new Achievement ();
			a.name = obj.getString("name");
			a.used = obj.getBoolean("used");
			return a;
		}
		
		public JSONObject toJSON() {
			JSONObject obj = new JSONObject();
			try {
				obj.put("name", name);
				obj.put("used", used);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return obj;
		}
	}

	public void setAchievements (Achievement [] achievements) {
		JSONArray arr = new JSONArray();
		for(int i = 0; i < achievements.length; ++i) {
			arr.put(achievements[i].toJSON());
		}
		try {
			json.put("achievements", arr);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public Achievement[] getAchievements() {
		try {
			JSONArray arr = json.getJSONArray("achievements");
			int length = arr.length();
			Achievement[] achievements = new Achievement[length];
			for(int i = 0; i < length; ++i) {
				JSONObject obj = arr.getJSONObject(i);
				achievements[i] = Achievement.fromJSON(obj);
			}
			return achievements;
		} catch (JSONException e) {
			return new Achievement[0];
		}
	}

	public void renewStats() {
		setDamage(lowerDamage(getDamage()));
		setSecondaryDamage(lowerDamage(getSecondaryDamage()));
		setCurrentWealth(oneTowards(getCurrentWealth(), getWealth()));
		Achievement[] achievements = getAchievements();
		for(Achievement a : achievements) {
			a.used = false;
		}
		setAchievements(achievements);
		Edge[] edges = getEdges();
		for(Edge e : edges) {
			e.used = false;
		}
		setEdges(edges);
	}
	
	private int oneTowards(int from, int to) {
		return from + Math.min(Math.max(to - from, -1), 1);
	}
	
	private JSONArray lowerDamage(JSONArray dmg) {
		for(int i = 0; i < dmg.length(); ++i) {
			try {
				int d = dmg.getInt(i);
				d = Math.max(d-1, 0);
				dmg.put(i, d);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return dmg;
	}
}
