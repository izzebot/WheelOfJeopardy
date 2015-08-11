package woj;

import javafx.scene.Parent;
import javafx.scene.control.TextField;

/*
 * This class uses JavaFX to create a visualization for the Wheel of JeopardyGame
 */
public class JavaFXWOJGameViz implements WOJGameViz {
		
	private Parent root;
	private TextField messageField;
	
	public JavaFXWOJGameViz(Parent vizRoot) {
		root = vizRoot;
		messageField = (TextField) root.getScene().lookup("#messagebox");
	}
	
	public Object getWheelVizContext() {
		System.out.println("getting wheel viz");
		return root.getScene().lookup("#wheelpane");	
	}

	public Object getJeopardyBoardContext() {
		return root.getScene().lookup("#jeopardyboard");
	}
	
	public Object getPlayerContext(int index) {
		int indexForString = index + 1;
		return root.getScene().lookup("#player" + indexForString);
	}
	
	public void displayMessage(String message) {
		messageField.setText(message);
	}
	
	public void clearMessage() {
		messageField.clear();
	}
	
}