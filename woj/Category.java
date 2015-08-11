package woj;

import java.util.ArrayList;

public class Category {
	private final String name;
	private ArrayList<BoardItem> items; 
	
	public Category(String catName, ArrayList<BoardItem> catItems) {
		name = catName;
		items = catItems;
	}
	
	public String getName() {
		return name;
	}
	
	/*
	 * Return the next unused item in this category. 
	 * Precondition: There is at least one item left in the category.
	 */
	public BoardItem getNextItem() {
		int index = 0;
		while (items.get(index).getUsed()) {
			index++;
		}
		return items.get(index);
	}
	
	/*
	 * Return true if all of the items in this category have been used. Otherwise return false.
	 */
	public boolean isUsedUp() {
		for (int index = 0; index < items.size(); index++) {
			if (items.get(index).getUsed() == false) {
				return false;
			}
		}
		return true;
	}
	
	public BoardItem getBoardItem(int index) {
		return items.get(index);
	}
	
	
}
