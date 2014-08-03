package eu.mrogalski;

import java.util.Arrays;
import java.util.Comparator;

public abstract class MArray {

	public static <T> String join(final T[] array, final String separator) {
		StringBuilder builder = new StringBuilder();
		
		for(int i = 0; i < array.length; ++i) {
			if(i > 0) {
				builder.append(separator);
			}
			builder.append(array[i]);
		}
		
		return builder.toString();
	}
	
	public static <T> int[] indexSort(final T[] array, final Comparator<? super T> cmp) {
		final Integer[] indicies = new Integer[array.length];
		for(int i = 0; i < indicies.length; ++i) {
			indicies[i] = Integer.valueOf(i);
		}
		
		
		Arrays.sort(indicies, new Comparator<Integer>() {

			@Override
			public int compare(final Integer arg0, final Integer arg1) {
				return cmp.compare(array[arg0.intValue()], array[arg1.intValue()]);
			}
			
		});
		
		
		final int[] plain_indicies = new int[array.length];
		for(int i = 0; i < indicies.length; ++i) {
			plain_indicies[i] = indicies[i].intValue();
		}
		return plain_indicies;
	}
}

