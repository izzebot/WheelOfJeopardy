package woj;

import javafx.scene.paint.Color;

/*
 * This class holds the data for a Sector of the wheel
 */
public class Sector {
	private String label;
	private Color color;
	private int id; //the value that is returned when the wheel spin lands on this sector
	
	public Sector(String sectorLabel, Color sectorColor, int sectorID) {
		label = sectorLabel;
		color = sectorColor;
		id = sectorID;
	}

	public String getLabel() {
		return label;
	}
	
	public Color getColor() {
		return color;
	}
	
	public int getID() {
		return id;
	}	
	
}
