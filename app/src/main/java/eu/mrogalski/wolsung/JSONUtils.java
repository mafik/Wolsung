package eu.mrogalski.wolsung;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JSONUtils {
	public static JSONArray removeIndex(JSONArray jsonArray, int index) {
		ArrayList<JSONObject> list = new ArrayList<JSONObject>();
		int len = jsonArray.length();
		if (jsonArray != null) {
			for (int i = 0; i < len; i++) {
				if(i == index) continue;
				try {
					list.add(jsonArray.getJSONObject(i));
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}
		// Recreate JSON Array
		JSONArray jsArray = new JSONArray(list);
		return jsArray;
	}
}
