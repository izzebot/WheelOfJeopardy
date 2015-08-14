package woj;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import javafx.scene.control.Button;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
import javafx.scene.text.TextAlignment;

/*
 * JeopardyBoard class
 * 
 * This is the JeopardyBoard that contains the categories and all the associated items for each category
 */
public class JeopardyBoard {
	private ArrayList<Category> categories;
	private JeopardyBoardViz boardViz;
	
	/*
	 * Initialize the JeopardyBoard for the upcoming round using the given text file
	 * 
	 * params: roundNumber - the round number is used to determine what point values should be used
	 * 		   filename - the path of the file that contains the question data for this round
	 */
	public JeopardyBoard(int roundNumber, String filename, Object context) throws IOException {
		categories = new ArrayList<Category>(6);
		
		int roundMultiplier = 1;
		if (roundNumber == 2) {
			roundMultiplier = 2;
		}
		
		Scanner inFile = new Scanner(new File(filename));
		
		//Repeat six times, once for each category
		for (int catNumber = 1; catNumber <= 6; catNumber++) {
			String catName = inFile.nextLine().trim().toUpperCase();
			
			ArrayList<BoardItem> items = new ArrayList<BoardItem>(5);
			int pointValueBase = 100;
			
			//Repeat five times, once for each board item
			for (int itemNumber = 1; itemNumber <= 5; itemNumber++) {
				//One line contains an answer and question
				String line = inFile.nextLine();
				
				//The answer and question can be split at the "?"
				String[] fields = line.split("\\?");
				
				
				String answer = fields[0].trim().toUpperCase() + "?";
				
				String question = fields[1].trim().toUpperCase();
				
				//Point values double for the second round
				int pointValue = pointValueBase * itemNumber * roundMultiplier;
				
				items.add(new BoardItem(question, answer, pointValue));
			}
			
			categories.add(new Category(catName, items));
			
			//read in the blank line
			if (inFile.hasNextLine()) {
				String blankLine = inFile.nextLine();
			}
		}
		
		inFile.close();
		
		boardViz = new JavaFXJeopardyBoardViz(context);
	}
	
	/*
	 * Return true if all the items on the board have been used
	 */
	public boolean isEmpty() {
		for (Category category : categories) {
			if (category.isUsedUp() == false) {
				return false;
			}
		}
		return true;
	}
	
	/*
	 * Return the next unused item from a given category
	 * Precondition: The category has at least one item remaining.
	 * param: The category to fetch the next item from
	 */
	public BoardItem getItem(int categoryNumber) {
		return categories.get(categoryNumber).getNextItem();
	}
	
	/*
	 * Show the next question in the given category
	 * Precondition: The category has at least one item remaining.
	 */
	public void showQuestion(int categoryNumber) {
		boardViz.showQuestion(getItem(categoryNumber).getQuestion());
	}
	
	/* 
	 * Show the whole board
	 */
	public void showJeopardyBoard() {
		boardViz.showGrid();
	}
	
	/*
	 * Return true if there are not items left in the category specied by categoryNumber
	 * Otherwise return false.
	 */
	public boolean isCategoryUsedUp(int categoryNumber) {
		return categories.get(categoryNumber).isUsedUp();
	}
	
	/*
	 * Return an array with the six category names. 
	 */
	public String[] getCategoryNames() {
		String[] names = new String[6];
		
		for (int index = 0; index < 6; index++) {
			names[index] = categories.get(index).getName();
		}
		
		return names;
	}
	
	/*
	 * Return a list with the names of the categories that still have items available on the board
	 */
	public ArrayList<String> getActiveCategories() {
		//Figure out how many categories are still active
		ArrayList<String> catList = new ArrayList<String>();
		
		for (Category cat : categories) {
			if (cat.isUsedUp() == false) {
				catList.add(cat.getName());
			}
		}
		
		return catList;
	}
	
	/*
	 * This interface must be implemented by the class that is used to visualize the JeopardyBoard
	 */
	interface JeopardyBoardViz {
		public void showQuestion(String question);
		public void showGrid();	
	}
	
	/*
	 * This class uses JavaFX to create a visualization of the Jeopardy Board
	 */
	private class JavaFXJeopardyBoardViz implements JeopardyBoardViz {
		
		private StackPane mainBoardPane;
		private GridPane gridPane;
		private Button questionText;
		private Button[] categoryButtons;
		private Button[][] itemButtons;

		public JavaFXJeopardyBoardViz(Object pane) {		
			
			mainBoardPane = (StackPane) pane;
			mainBoardPane.getChildren().clear();
			
			//Create and add children of mainBoardPane
			gridPane = new GridPane();
			gridPane.setHgap(10);
			gridPane.setVgap(10);
			questionText = new Button("");
			mainBoardPane.getChildren().addAll(gridPane, questionText);
			
			gridPane.toString();
			
			questionText.setMaxWidth(Double.MAX_VALUE);
			questionText.setMaxHeight(Double.MAX_VALUE);
			questionText.setWrapText(true);
			questionText.setTextAlignment(TextAlignment.CENTER);
			questionText.setStyle("-fx-text-fill: white; -fx-font-size: 36px; -fx-font-weight: bold; -fx-background-color: #0000FF;");
			questionText.setVisible(false);
			
			//Set the columns to be equal sizes in the grid 
			for (int col = 1; col <= 6; col++) {
				ColumnConstraints colConstraint = new ColumnConstraints();
				colConstraint.setPercentWidth(100 / 6);
				gridPane.getColumnConstraints().add(colConstraint);
			}
			
			//Set the rows to be equal sizes in the grid 
			RowConstraints rowConstraint = new RowConstraints();
			rowConstraint.setPercentHeight(12);
			gridPane.getRowConstraints().add(rowConstraint);
			for (int row = 1; row <= 5; row++) {
				rowConstraint = new RowConstraints();
				rowConstraint.setPercentHeight(88 / 5);
				gridPane.getRowConstraints().add(rowConstraint);
			}			
			
			categoryButtons = new Button[6];
			
			itemButtons = new Button[5][6];
			
			//Add buttons for category titles
			for (int categoryIndex = 0; categoryIndex <= 5; categoryIndex++) {
				categoryButtons[categoryIndex] = new Button(categories.get(categoryIndex).getName());
				categoryButtons[categoryIndex].setMaxWidth(Double.MAX_VALUE);
				categoryButtons[categoryIndex].setMaxHeight(Double.MAX_VALUE);
				categoryButtons[categoryIndex].setWrapText(true);
				categoryButtons[categoryIndex].setTextAlignment(TextAlignment.CENTER);
				categoryButtons[categoryIndex].setStyle("-fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold; -fx-background-color: #0000FF;");
				//categoryButtons[categoryIndex].setFont(new Font("Arial Narrow", 14px));
				gridPane.add(categoryButtons[categoryIndex], categoryIndex, 0);
				
				//Add buttons for BoardItems
				for (int itemIndex = 0; itemIndex <= 4; itemIndex++) {
					itemButtons[itemIndex][categoryIndex] = new JeopardyItemButton(categories.get(categoryIndex).getBoardItem(itemIndex));
					itemButtons[itemIndex][categoryIndex].setMaxWidth(Double.MAX_VALUE);
					itemButtons[itemIndex][categoryIndex].setMaxHeight(Double.MAX_VALUE);
					itemButtons[itemIndex][categoryIndex].setStyle("-fx-text-fill: gold; -fx-font-size: 24px; -fx-font-weight: bold; -fx-background-color: #0000FF;");
					gridPane.add(itemButtons[itemIndex][categoryIndex], categoryIndex, itemIndex + 1);
					categories.get(categoryIndex).getBoardItem(itemIndex).setViz(itemButtons[itemIndex][categoryIndex]);
				}
				
			}
			
		}
		
		@Override
		public void showQuestion(String question) {
			
			//Hide the grid 
			gridPane.setVisible(false);
			
			//Load the text from the current question and then show it
			questionText.setText(question);		
			questionText.setVisible(true);
		}

		@Override
		public void showGrid() {
			//Hide the previously shown question and show the grid
			questionText.setVisible(false);
			gridPane.setVisible(true);
			
			
		}
		
	}
	
	//I had a reason for adding this class but I'm not actually sure if it's going to be necessary
	private class JeopardyItemButton extends Button {
		
		public JeopardyItemButton(BoardItem item) {
			super(""+item.getPointValue());
		}
		
	}
}
