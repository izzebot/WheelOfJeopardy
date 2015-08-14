package woj;

import javafx.scene.control.Button;
import javafx.scene.layout.VBox;

/*
 * Player class
 */
public class Player {
	private int totalPoints; //total points in the game
	private int roundPoints; //points in the current round
	private int freeTurns; //number of free turns currently accumulated
	private String name; //the player's name
	private PlayerViz playerViz; //used to show the player's name and current round score in the GUI
	
	/*
	 * Constructor
	 */
	public Player(String playerName, Object context) {
		name = playerName;
		playerViz = new JavaFXPlayerViz(context);
	}
	
	/*
	 * Return the user's answer to the current question
	 */
	public String getAnswer() {
		return "";
	}
	
	/*
	 * Return the user's category choice
	 */
	public int getCategoryChoice() {
		return 0;
	}
	
	/*
	 * Update the total points and round points. The pointsToAdd param could be positive or negative
	 * This also updates the round points in the GUI
	 */
	public void updatePoints(int pointsToAdd) {
		totalPoints += pointsToAdd;
		roundPoints += pointsToAdd;
		playerViz.updateScore(roundPoints);
	}
	
	/*
	 * Set the round points to 0
	 * This also updates the round points to 0 in the GUI
	 */
	public void clearRoundPoints() {
		roundPoints = 0;
		playerViz.updateScore(0);
	}
	
	public void goBankrupt() {
		totalPoints -= roundPoints;
		clearRoundPoints();
	}
	
	/*
	 * Update the number of free turns. The numberToAdd param could be +1 or -1
	 */
	public void updateFreeTurns(int numberToAdd) {
		freeTurns += numberToAdd;
	}
	
	public String getName() {
		return name;
	}
	
	public int getRoundPoints() {
		return roundPoints;
	}
	
	public int getTotalPoints() {
		return totalPoints;
	}
	
	public int getFreeTurns() {
		return freeTurns;
	}
	
	/*
	 * This is used to show whose turn it is in the GUI
	 */
	public void showPlayerActive() {
		playerViz.showPlayerActive();
	}
	
	/*
	 * This is used to remove the effects that showed that it was this player's turn
	 */
	public void removePlayerActive() {
		playerViz.removePlayerActive();
	}
	
	/*
	 * This interface must be implemented by the class that is used to visualize the player's name and score in the game GUI
	 */
	public interface PlayerViz {
		void showPlayerActive();
		void removePlayerActive();
		void updateScore(int roundPoints);
	}
	
	/*
	 * PlayerViz class
	 * 
	 * A visualization of the player's name and score in a JavaFX scene
	 */
	private class JavaFXPlayerViz implements PlayerViz {
		private VBox playerBox;
		private Button playerNameText;
		private Button playerScore;
		
		/* 
		 * Constructor
		 */
		public JavaFXPlayerViz(Object context) {
			playerBox = (VBox) context;
			playerNameText = (Button) playerBox.getChildren().get(0);
			playerNameText.setStyle("-fx-text-fill: white; -fx-font-size: 25px; -fx-font-weight: bold; -fx-background-color: #0000FF;");
			playerNameText.setPrefWidth(180);
			playerNameText.setText(name);
			
			playerScore = (Button) playerBox.getChildren().get(1);
			playerScore.setStyle("-fx-text-fill: white; -fx-font-size: 35px; -fx-font-weight: bold; -fx-background-color: #000088;");
			playerScore.setPrefWidth(180);
			playerScore.setText("0");
			
		}
		
		@Override
		public void updateScore(int roundPoints) {
			playerScore.setText(""+roundPoints);
		}
		
		@Override
		public void showPlayerActive() {
			playerNameText.setStyle("-fx-text-fill: white; -fx-font-size: 25px; -fx-font-weight: bold; -fx-background-color: #8a2be2;");
		}

		@Override
		public void removePlayerActive() {
			// TODO Auto-generated method stub
			playerNameText.setStyle("-fx-text-fill: white; -fx-font-size: 25px; -fx-font-weight: bold; -fx-background-color: #0000FF;");
			
		}

	}
	
}
