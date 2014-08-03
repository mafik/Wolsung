package eu.mrogalski.android;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.TextView;

import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Spinner;

import java.lang.reflect.Field;
import java.util.ArrayList;

public class Synchronizer {
	
	protected static ArrayList<FieldBinder> fieldBinders = new ArrayList<FieldBinder>();
	protected static boolean inhibitObjectChanges = false;
	
	public static abstract class FieldBinder {
		public Object target;
		public String fieldName;
		public Runnable runnable;
		
		public FieldBinder(Object target, String fieldName) {
			this.target = target;
			this.fieldName = fieldName;
			this.runnable = null;
		}
		
		protected void viewChanged() {
			if(inhibitObjectChanges) return;
			viewToObject();
			if(runnable != null) {
				runnable.run();
			}
		}
		
		public void and(Runnable runnable) {
			this.runnable = runnable;
		}
		
		public abstract void objectToView();
		public abstract void viewToObject();

		protected Field getField() {
			try {
				return target.getClass().getField(fieldName);
			} catch (NoSuchFieldException e) {
				e.printStackTrace();
				return null;
			}
		}
	}
	
	private static class SpinnerBinder extends FieldBinder implements OnItemSelectedListener {
		public Spinner spinner;
		
		public SpinnerBinder(Spinner spinner, Object target, String fieldName ) {
			super(target, fieldName);
			this.spinner = spinner;
			spinner.setOnItemSelectedListener(this);
		}

		@Override
		public void onItemSelected(AdapterView<?> parent, View view,
				int position, long id) {
			viewChanged();
		}

		@Override
		public void onNothingSelected(AdapterView<?> parent) {}

		@Override
		public void objectToView() {
			try {
				spinner.setSelection(getField().getInt(target));
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}

		@Override
		public void viewToObject() {
			try {
				getField().setInt(target, spinner.getSelectedItemPosition());
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}			
		}
		
	}
	
	private static class TextBinder extends FieldBinder implements TextWatcher {
		
		public TextView textView;

		public TextBinder(TextView textView, Object target, String fieldName) {
			super(target, fieldName);
			this.textView = textView;
			textView.addTextChangedListener(this);
		}

		@Override
		public void objectToView() {
			String text = "error";
			try {
				text = (String) getField().get(target);
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
			// TODO: if view does not exist, mark this binder as obsolete and remove it after *Updated loop
			textView.setText(text);
		}

		@Override
		public void viewToObject() {
			String text = textView.getText().toString();
			try {
				getField().set(target, text);
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}

		@Override
		public void afterTextChanged(Editable s) {
			viewChanged();
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {}

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {}
		
	}

	public static FieldBinder bindText(TextView textView, Object target, String fieldName) {
		FieldBinder textBinder = new TextBinder(textView, target, fieldName);
		fieldBinders.add(textBinder);
		return textBinder;
	}

	public static FieldBinder bindSpinner(Spinner textView, Object target, String fieldName) {
		SpinnerBinder spinnerBinder = new SpinnerBinder(textView, target, fieldName);
		fieldBinders.add(spinnerBinder);
		return spinnerBinder;
	}
	
	public static void objectUpdated(Object object) {
		inhibitObjectChanges = true;
		for(FieldBinder b : fieldBinders) {
			if(b.target.equals(object)) {
				b.objectToView();
			}
		}
		inhibitObjectChanges = false;
	}
	
	public static void fieldUpdated(Object object, String fieldName) {
		inhibitObjectChanges = true;
		for(FieldBinder b : fieldBinders) {
			if(b.target.equals(object) && b.fieldName == fieldName) {
				b.objectToView();
			}
		}
		inhibitObjectChanges = false;
	}
}
