package ucas.dataMining.util;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class MapUtil {
	/**
	 * 根据value值对Map排序
	 */
	public static Map<String, Double> sortByMapValue(Map<String, Double> unsortedMap) {
		List<Map.Entry<String, Double>> list = new LinkedList<Map.Entry<String, Double>>(unsortedMap.entrySet());
		 
		Collections.sort(list, new Comparator<Map.Entry<String, Double>>() {
			public int compare(Map.Entry<String, Double> o1, Map.Entry<String, Double> o2) {
				return (o2.getValue()).compareTo(o1.getValue());
			}
		});
		
		Map<String, Double> sortedMap = new LinkedHashMap<String, Double>();
		for (Iterator<Map.Entry<String, Double>> it = list.iterator(); it.hasNext();) {
			Map.Entry<String, Double> entry = it.next();
			sortedMap.put(entry.getKey(), entry.getValue());
		}
		return sortedMap;
	}
	
	/**
	 * 打印Map
	 * @return
	 */
	public static String MapPrint(Map<String, Double> map) {
		StringBuilder mapBuilder = new StringBuilder();
		mapBuilder.append("{");
		for (Map.Entry<String, Double> entry : map.entrySet()) {
			mapBuilder.append("\"" + entry.getKey() + "\":" + entry.getValue() + ",");
		}
		mapBuilder.deleteCharAt(mapBuilder.length() - 1);
		mapBuilder.append("}");
		
		return mapBuilder.toString();
	}
}
