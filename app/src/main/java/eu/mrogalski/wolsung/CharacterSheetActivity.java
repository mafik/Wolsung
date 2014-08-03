package eu.mrogalski.wolsung;

import android.app.AlertDialog;
import android.content.ClipData;
import android.text.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.AssetManager;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import com.google.analytics.tracking.android.EasyTracker;
import com.viewpagerindicator.PageIndicator;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.LinkedList;

import eu.mrogalski.android.ChangeLog;


public class CharacterSheetActivity extends ActionBarActivity implements ActionBar.OnNavigationListener {

    static final String TAG = CharacterSheetActivity.class.getSimpleName();

    static final String FILENAME = "character_list";

    static Typeface typeface;
    static Typeface typefaceBold;
    static Typeface typefaceItalic;
    static Typeface typefaceBoldItalic;
    WolsungCharacter active_character;
    JSONArray character_list;
    LinkedList<AbstractCharacterFragment> active_fragments;
    CharacterListSpinnerAdapter mSpinnerAdapter;
    WolsungPagerAdapter mWolsungPagerAdapter;
    ViewPager mViewPager;
    ChangeLog changeLog;
    boolean inhibitNavigation = false;
    static ActionBar mActionBar;
    static float actionBarHeight = 72;

    private void read_character_list() {

        String json = "[]";

        try {
            FileInputStream fis = openFileInput(FILENAME);
            StringWriter writer = new StringWriter();
            IOUtils.copy(fis, writer, "UTF-8");
            IOUtils.closeQuietly(fis);
            json = writer.toString();
            Log.d(TAG, "Read serialized JSON: " + json);
        } catch (FileNotFoundException ex) {
            // ignore
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "UWAGA: Błąd we-wy przy odczycie listy postaci!", Toast.LENGTH_LONG).show();
        }

        try {
            character_list = new JSONArray(json);
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(this, "UWAGA: Błąd przy odczycie danych postaci! Usuwam resztki zapisanych kart!", Toast.LENGTH_LONG).show();
            deleteFile(FILENAME);
            character_list = new JSONArray();
        }

        if (character_list.length() == 0) {
            active_character = WolsungCharacter.makeDefaultAppendingTo(character_list);
        }

        active_character = new WolsungCharacter(character_list, 0);

    }

    private void save_character_list() {
        try {
            FileOutputStream fos = openFileOutput(FILENAME, Context.MODE_PRIVATE);
            String list_repr = character_list.toString();
            Log.d(TAG, "Writing serialized JSON: " + list_repr);

            OutputStreamWriter osw = new OutputStreamWriter(fos);
            osw.write(list_repr);

            // IOUtils.write(list_repr, fos); // bug: NoSuchMethod: String.getBytes

            osw.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            Toast.makeText(this, "UWAGA: Nie znaleziono pliku do zapisania postaci!", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "UWAGA: Błąd we-wy przy zapisie listy postaci!", Toast.LENGTH_LONG).show();
        }
    }

    static void setFinalStatic(Class c, String fieldName, Object newValue) {
        try {
            Field field = c.getDeclaredField(fieldName);
            field.setAccessible(true);

            /*Field modifiersField = Field.class.getDeclaredField("modifiers");
            modifiersField.setAccessible(true);
            modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);*/

            field.set(null, newValue);
        } catch(Exception e) {
            //throw new RuntimeException(e);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        active_fragments = new LinkedList<AbstractCharacterFragment>();


        final AssetManager assets = getAssets();

        typeface = Typeface.createFromAsset(assets, "fonts/PlayfairDisplay-Regular.ttf");
        typefaceBold = Typeface.createFromAsset(assets, "fonts/PlayfairDisplay-Bold.ttf");
        typefaceItalic = Typeface.createFromAsset(assets, "fonts/PlayfairDisplay-Italic.ttf");
        typefaceBoldItalic = Typeface.createFromAsset(assets, "fonts/PlayfairDisplay-BoldItalic.ttf");
        Log.d(TAG, "Bold: " + typefaceBoldItalic.isBold());
        Log.d(TAG, "Italic: " + typefaceBoldItalic.isItalic());

        setFinalStatic(Typeface.class, "DEFAULT", typeface);
        setFinalStatic(Typeface.class, "DEFAULT_BOLD", typefaceBold);
        setFinalStatic(Typeface.class, "sDefaults", new Typeface[]{typeface, typefaceBold, typefaceItalic, typefaceBoldItalic});

        try {
            final Field arrField = Typeface.class.getDeclaredField("sTypefaceCache");
            arrField.setAccessible(true);
            SparseArray<SparseArray<Typeface>> arr = (SparseArray<SparseArray<Typeface>>) arrField.get(null);

            final Field niField = Typeface.class.getDeclaredField("native_instance");
            niField.setAccessible(true);
            int ni = niField.getInt(typeface);

            final SparseArray<Typeface> typefaceArray = new SparseArray<Typeface>(4); // arr.get(ni);
            typefaceArray.put(Typeface.NORMAL, typeface);
            typefaceArray.put(Typeface.BOLD, typefaceBold);
            typefaceArray.put(Typeface.ITALIC, typefaceItalic);
            typefaceArray.put(Typeface.BOLD_ITALIC, typefaceBoldItalic);

            arr.put(ni, typefaceArray);

        } catch(Exception e) {
            //throw new RuntimeException(e);
        }

        read_character_list();

        setContentView(R.layout.activity_character_sheet);

        mActionBar = getSupportActionBar();
        mActionBar.setDisplayShowTitleEnabled(false);
        mActionBar.setDisplayShowHomeEnabled(false);
        mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);

        final TypedArray styledAttributes = getTheme().obtainStyledAttributes(
                new int[] { android.R.attr.actionBarSize });
        actionBarHeight = styledAttributes.getDimension(0, 0);
        styledAttributes.recycle();


        mSpinnerAdapter = new CharacterListSpinnerAdapter(this, character_list);
        mActionBar.setListNavigationCallbacks(mSpinnerAdapter, this);

        mWolsungPagerAdapter = new WolsungPagerAdapter(getSupportFragmentManager(), getResources());

        applyFonts(findViewById(R.id.root_view), typeface);

        mViewPager = (ViewPager) findViewById(R.id.pager);
        if (mViewPager != null) {
            mViewPager.setOffscreenPageLimit(5);
            mViewPager.setAdapter(mWolsungPagerAdapter);

            final PageIndicator indicator = (PageIndicator) findViewById(R.id.titles);
            indicator.setViewPager(mViewPager);
            applyFonts(findViewById(R.id.titles), typeface);
        }

        changeLog = new ChangeLog(this, R.raw.changelog, R.drawable.ic_action_about_gray);
        changeLog.showOnFirstRun();
    }

	public static void applyFonts(final View v, Typeface fontToSet)
	{
	    try {
	        if (v instanceof ViewGroup) {
	            ViewGroup vg = (ViewGroup) v;
	            for (int i = 0; i < vg.getChildCount(); i++) {
	                View child = vg.getChildAt(i);
	                applyFonts(child, fontToSet);
	            }
	        } else if (v instanceof android.widget.TextView) {
                final TextView tv = (TextView) v;
                tv.setTypeface(fontToSet, Typeface.NORMAL);
            }
	    } catch (Exception e) {
	        e.printStackTrace();
	        // ignore
	    }
	}


    @Override
    protected void onStart() {
        super.onStart();
        EasyTracker.getInstance().activityStart(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        save_character_list();
        EasyTracker.getInstance().activityStop(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.menu_renew_used:
                Animation pulse_animation = AnimationUtils.loadAnimation(this, R.anim.pager_pulse);
                pulse_animation.setStartOffset(0);
                View animatedView = mViewPager != null ? mViewPager : findViewById(R.id.root_view);
                animatedView.startAnimation(pulse_animation);

                active_character.renewStats();
                refreshFragments();
                toast(R.string.renewed_toast);
                return true;
            case R.id.menu_new_character:
                active_character = WolsungCharacter.makeDefaultAppendingTo(character_list);
                refreshFragments();
                refreshSpinner(true);
                toast(R.string.new_character_toast);
                return true;
            case R.id.menu_discard_character:
                AlertDialog dialog = new AlertDialog.Builder(this).create();
                dialog.setTitle(getString(R.string.discard_character_question));
                dialog.setIcon(R.drawable.ic_discard_dark);
                //dialog.setMessage(getString(R.string.discard_character_question));
                dialog.setCancelable(true);
                dialog.setButton((int)AlertDialog.BUTTON_POSITIVE, getString(android.R.string.yes), new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        character_list = JSONUtils.removeIndex(character_list, active_character.index);
                        loadCharacter(0);
                        refreshSpinner(false);
                        refreshFragments();
                        toast(R.string.discard_character_toast);
                    }

                });
                dialog.setButton((int)AlertDialog.BUTTON_NEGATIVE, getString(android.R.string.no), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) { }
                });

                dialog.show();
                return true;
            case R.id.menu_about:
                changeLog.showChangelogDialog();
                return true;
            case R.id.menu_export_to_clipboard:
                try {
                    final String json = active_character.getJSONObject().toString(4);
                    ((ClipboardManager)getSystemService(Context.CLIPBOARD_SERVICE)).setText(json);
                } catch (JSONException e) {
                    toast(R.string.export_failed_toast);
                }
                return true;
            case R.id.menu_import_from_clipboard:
                save_character_list();

                String str = ((ClipboardManager)getSystemService(Context.CLIPBOARD_SERVICE)).getText().toString();
                JSONObject character;
                try {
                    character = new JSONObject(str);
                    final int index = character_list.length();
                    character_list.put(index, character);
                    loadCharacter(index);
                } catch (JSONException e) {
                    e.printStackTrace();
                    toast(R.string.import_failed_toast);
                    read_character_list();
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(int itemPosition, long itemId) {
        if(inhibitNavigation) return true;
        if(mViewPager != null) {
            View focused = mViewPager.findFocus();
            if(focused != null) {
                focused.clearFocus();
            }
        }

        int last = active_character.index;
        active_character = new WolsungCharacter(character_list, (int)itemId);
        int current = active_character.index;

        if(last != current) {
            final View animatedView = mViewPager != null ? mViewPager : findViewById(R.id.root_view);
            Animation out_animation = AnimationUtils.loadAnimation(this, R.anim.pager_out);
            out_animation.setStartOffset(0);
            out_animation.setAnimationListener(new Animation.AnimationListener() {

                @Override
                public void onAnimationStart(Animation animation) {}

                @Override
                public void onAnimationRepeat(Animation animation) {}

                @Override
                public void onAnimationEnd(Animation animation) {
                    refreshFragments();
                    Animation in_animation = AnimationUtils.loadAnimation(CharacterSheetActivity.this, R.anim.pager_in);
                    in_animation.setStartOffset(0);
                    animatedView.startAnimation(in_animation);
                }
            });
            animatedView.startAnimation(out_animation);
        } else {
            refreshFragments();
        }


        return true;
    }

    private void toast(int resId) {
        Toast.makeText(this, getString(resId), Toast.LENGTH_LONG).show();
    }

    private void loadCharacter(int index) {
        if(index < 0) {
            index = 0;
        }
        if(character_list.length() > index) {
            active_character = new WolsungCharacter(character_list, index);
        } else {
            active_character = WolsungCharacter.makeDefaultAppendingTo(character_list);
        }
    }

    void refreshSpinner(boolean simpleAdd) {
        if(simpleAdd) {
            mSpinnerAdapter.refresh();
            inhibitNavigation  = true;
            mActionBar.setSelectedNavigationItem(active_character.index);
            inhibitNavigation = false;
            return;
        }
        mSpinnerAdapter = new CharacterListSpinnerAdapter(this, character_list);
        inhibitNavigation  = true;
        mActionBar.setListNavigationCallbacks(mSpinnerAdapter, this);
        mActionBar.setSelectedNavigationItem(active_character.index);
        inhibitNavigation = false;

    }

    void refreshFragments() {
        for(AbstractCharacterFragment f : this.active_fragments) {
            f.refresh();
        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.character_sheet, menu);
        return true;
    }

}
