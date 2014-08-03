package eu.mrogalski.android;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;

import org.apache.commons.io.IOUtils;

import eu.mrogalski.wolsung.R;

//import android.app.AlertDialog;
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;
import android.webkit.WebView;

public class ChangeLog {

	private static final String TAG = "ChangeLog";

	private static final String PREF_FILE_CHANGELOG = "changelog";

	private Context mContext;
	private SharedPreferences mPrefs;

	private int rawResId;

	private int iconResId;

	public ChangeLog(Context context, int rawResId, int iconResId) {
		mContext = context;
		this.rawResId = rawResId;
		this.iconResId = iconResId;
		mPrefs = mContext.getSharedPreferences(PREF_FILE_CHANGELOG,
				Context.MODE_PRIVATE);
	}

	public void showOnFirstRun() {
		String versionKey = "changelog_" + Utils.getAppVersionCode(mContext);

		if (!mPrefs.getBoolean(versionKey, false)) {
			showChangelogDialog();

			Editor editor = mPrefs.edit();
			editor.putBoolean(versionKey, true);
			editor.commit();
		}
	}

	public void showChangelogDialog() {

		String changelog = getChangelogFromResources();

		WebView webView = new WebView(mContext);
		webView.loadDataWithBaseURL(null, changelog, "text/html", "utf-8", null);

		AlertDialog changelogDialog = new AlertDialog.Builder(mContext)
				.setIcon(iconResId).setTitle(R.string.about)
				.setView(webView).setPositiveButton(R.string.close, null)
				.create();


		changelogDialog.show();
	}

	private String getChangelogFromResources() {
		InputStream is = null;
		try {
			is = mContext.getResources().openRawResource(rawResId);

			StringWriter writer = new StringWriter();
			IOUtils.copy(is, writer, "UTF-8");
			IOUtils.closeQuietly(is);

			return writer.toString();
		} catch (IOException e) {
			e.printStackTrace();
			Log.e(TAG, "Error when reading changelog from raw resources.", e);
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
					Log.e(TAG,
							"Error when reading changelog from raw resources.",
							e);
				}
			}
		}

		return "";
	}

}