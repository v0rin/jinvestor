package org.jinvestor.model;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Test;


public class BarTestUtil {

	public static Map<String, String> getStandardCsvColumnsMappings() {
		Map<String, String> inputToOutputColumnMappings = new HashMap<>();
		inputToOutputColumnMappings.put("Symbol", "symbol");
		inputToOutputColumnMappings.put("Date", "timestamp");
		inputToOutputColumnMappings.put("Open", "open");
		inputToOutputColumnMappings.put("High", "high");
		inputToOutputColumnMappings.put("Low", "low");
		inputToOutputColumnMappings.put("Close", "close");
		inputToOutputColumnMappings.put("Volume", "volume");

		return inputToOutputColumnMappings;
	}

	@Test
	public void test() {
		List<Integer> list1 = Arrays.asList(1, 2, 3);
		List<Integer> list2 = Arrays.asList(2, 1, 3);

		System.out.println(list1.containsAll(list2) && list2.containsAll(list1));
		System.out.println(new HashSet<>(list1).equals(new HashSet<>(list2)));

		Set<Integer> set1 = new HashSet<>(list1);
		Set<Integer> set2 = new HashSet<>(list2);
		System.out.println( set1.equals(set2) );

	}
}
