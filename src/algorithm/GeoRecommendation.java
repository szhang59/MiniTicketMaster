package algorithm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import db.DBConnection;
import db.DBConnectionFactory;
import entity.Item;

public class GeoRecommendation {
	public List<Item> recommendItems(String userId, double lat, double lon) {
		List<Item> recommendedItems = new ArrayList<>();
		DBConnection conn = DBConnectionFactory.getDBConnection();
		
		// Step 1: Get all favorited items
		Set<String> favoriteItems = conn.getFavoriteItemIds(userId);
		
		// Step 2: Get all categories of favorited items, sort by count
		Map<String, Integer> allCategories = new HashMap<>();
		
		for (String item: favoriteItems) {
			Set<String> categories = conn.getCategories(item);
			for (String category: categories) {
				if (allCategories.containsKey(category)) {
					allCategories.put(category, allCategories.get(category) + 1);
				} else {
					allCategories.put(category, 1);
				}
			}
		}
		
		List<Entry<String, Integer>> categoryList = new ArrayList<>(allCategories.entrySet());
		Collections.sort(categoryList, new Comparator<Entry<String, Integer>>() {
			@Override
			public int compare(Entry<String, Integer> o1, Entry<String, Integer> o2) {
				return Integer.compare(o2.getValue(), o1.getValue());
			}
		});
		
		// Step 3: do search based on category, filter out favorited events, sort by distance
		Set<Item> visitedItems = new HashSet<>();
		for (Entry<String, Integer> category: categoryList) {
			List<Item> items = conn.searchItems(lat, lon, category.getKey());
			List<Item> filteredItems = new ArrayList<>();
			for (Item item: items) {
				if (!favoriteItems.contains(item) && !visitedItems.contains(item)) {
					filteredItems.add(item);
				}
			}
			Collections.sort(filteredItems, new Comparator<Item>() {
				@Override
				public int compare(Item i1, Item i2) {
					// increasing order or distance
					return Double.compare(i1.getDistance(), i2.getDistance());
				}
			});
			visitedItems.addAll(filteredItems);
			recommendedItems.addAll(filteredItems);			
		}
		return recommendedItems;
	}
	
}