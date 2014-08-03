package eu.mrogalski.wolsung;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;

public class WolsungPagerAdapter extends FragmentStatePagerAdapter {
    private final Resources resources;

    // Five identical classes for god's sake...

	public static class ScrolledBasicInfoFragment extends BasicInfoFragment {
		@Override
		protected View createRootView(LayoutInflater inflater,
				ViewGroup container, Bundle savedInstanceState) {
			ScrollView scrollView = (ScrollView) inflater.inflate(R.layout.scroll_container, container, false);
			ViewGroup embed_here = (ViewGroup) scrollView.findViewById(R.id.wrapper_frame);
			View original = super.createRootView(inflater, embed_here, savedInstanceState);
			embed_here.addView(original);
			return scrollView;
		}
	}

	public static class ScrolledAttributesFragment extends AttributesFragment {
		@Override
		protected View createRootView(LayoutInflater inflater,
				ViewGroup container, Bundle savedInstanceState) {
			View wrapper = inflater.inflate(R.layout.scroll_container, container, false);
			ViewGroup embed_here = (ViewGroup) wrapper.findViewById(R.id.wrapper_frame);
			View original = super.createRootView(inflater, embed_here, savedInstanceState);
			embed_here.addView(original);
			return wrapper;
		}
	}

	public static class ScrolledAdvantagesFragment extends AdvantagesFragment {
		@Override
		protected View createRootView(LayoutInflater inflater,
				ViewGroup container, Bundle savedInstanceState) {
			View wrapper = inflater.inflate(R.layout.scroll_container, container, false);
			ViewGroup embed_here = (ViewGroup) wrapper.findViewById(R.id.wrapper_frame);
			View original = super.createRootView(inflater, embed_here, savedInstanceState);
			embed_here.addView(original);
			return wrapper;
		}
	}

	public static class ScrolledAchievementsFragment extends AchievementsFragment {
		@Override
		protected View createRootView(LayoutInflater inflater,
				ViewGroup container, Bundle savedInstanceState) {
			View wrapper = inflater.inflate(R.layout.scroll_container, container, false);
			ViewGroup embed_here = (ViewGroup) wrapper.findViewById(R.id.wrapper_frame);
			View original = super.createRootView(inflater, embed_here, savedInstanceState);
			embed_here.addView(original);
			return wrapper;
		}
	}

	public static class ScrolledDescriptionFragment extends DescriptionFragment {
		@Override
		protected View createRootView(LayoutInflater inflater,
				ViewGroup container, Bundle savedInstanceState) {
			View wrapper = inflater.inflate(R.layout.scroll_container, container, false);
			ViewGroup embed_here = (ViewGroup) wrapper.findViewById(R.id.wrapper_frame);
			View original = super.createRootView(inflater, embed_here, savedInstanceState);
			embed_here.addView(original);
			return wrapper;
		}
	}

	public WolsungPagerAdapter(FragmentManager supportFragmentManager, Resources resources) {
		super(supportFragmentManager);
        this.resources = resources;
    }
	

	@Override
	public Fragment getItem(int arg0) { 
		if(arg0 == 0) {
			return new ScrolledBasicInfoFragment(); 
		} else if(arg0 == 1) {
			return new ScrolledAttributesFragment();
		} else if(arg0 == 2) {
			return new ScrolledAdvantagesFragment();
		} else if(arg0 == 3) {
			return new ScrolledAchievementsFragment();
		} else if(arg0 == 4) {
			return new ScrolledDescriptionFragment();
		}
		return new ScrolledBasicInfoFragment();
	}
	
	@Override
	public CharSequence getPageTitle(int position) {
        return resources.getStringArray(R.array.tabs)[position];
	}

	@Override
	public int getCount() {
		return 5;
	}

}