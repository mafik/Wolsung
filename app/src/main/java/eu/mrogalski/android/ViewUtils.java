package eu.mrogalski.android;

import android.view.View;

public class ViewUtils {
	public static int getRelativeLeft(View myView) {
	    if (myView.getParent() == myView.getRootView())
	        return myView.getLeft();
	    else
	        return myView.getLeft() + getRelativeLeft((View) myView.getParent());
	}

	public static int getRelativeTop(View myView) {
	    if (myView.getParent() == myView.getRootView())
	        return myView.getTop();
	    else
	        return myView.getTop() + getRelativeTop((View) myView.getParent());
	}
}
