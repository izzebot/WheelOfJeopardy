package woj;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.text.Text;

/*
 * Game class
 * 
 * This is the main class that controls the game play.
 */
public class Game {
	private Player[] players;
	private JeopardyBoard board;
	private Wheel wheel;
	private int whoseTurn;
	private int round;
	private int spinsRemaining;
	private BoardItem currentItem;
	
	private WOJGameViz gameViz;
	
	public Game(String[] names, Parent root) {
		gameViz = new JavaFXWOJGameViz(root);
		
		players = new Player[3];
		
		//Create the three players using the names entered
		System.out.println(gameViz.getPlayerContext(0));
		players[0] = new Player(names[0], gameViz.getPlayerContext(0));
		players[1] = new Player(names[1], gameViz.getPlayerContext(1));
		players[2] = new Player(names[2], gameViz.getPlayerContext(2));
		
		//Choose a random player to have the first turn
		whoseTurn = (int) (Math.random() * 3);
		
		round = 1;
		spinsRemaining = 50;
		gameViz.updateSpinsRemaining(spinsRemaining);
		
	}
	
	/*
	 * Play one full game.
	 */
	public void playGame() {
		
		//Initiate the game with the first round
		playRound();
		
	}
	
	/*
	 * Play one round of the game
	 */
	private void playRound() {
		
		//Make a new Jeopardy board for this round
		//This needs to be fixed to use different files for each round / game
		try {
			if (round == 1) {
				board = new JeopardyBoard(round, "src/rounds/Round3.txt", gameViz.getJeopardyBoardContext());
			} else {
				board = new JeopardyBoard(round, "src/rounds/Round1.txt", gameViz.getJeopardyBoardContext());
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}	
		
		wheel = new Wheel(board.getCategoryNames(), gameViz.getWheelVizContext(), new SpinHandler());
		
		//Temporarily set spinsRemaining to 5 for testing purposes. This would normally be set to 50. 
		spinsRemaining = 5;
<<<<<<< HEAD
		
		displayMessage("Round " + round, "Let's begin!");
		
=======
		
		displayMessage("Round " + round, "Let's begin!");
		
>>>>>>> origin/master
		//Initiate the first turn
		beginPlayerTurn();
	
	}
	
	/*
	 * Begin a player turn. A player may have several consecutive turns either by answering a question correctly or free turns.
	 */
	private void beginPlayerTurn() {		
		players[whoseTurn].showPlayerActive();
		
		displayMessage(players[whoseTurn].getName() + ", spin the wheel!");
		
		decreaseSpinsByOne();
		wheel.spin();
	}
	
	/*
	 * SpinHandler class - used when the wheel animation has finished
	 */
	private class SpinHandler implements EventHandler<ActionEvent> {
		@Override
		public void handle(ActionEvent event) {
			processSpin();			
		}	
	}
	
	/*
	 * Process a spin after the wheel has been spun.
	 */
	private void processSpin() {
		int spinResult = wheel.getCurrentSector();
		
		if ((spinResult == Wheel.SPIN_AGAIN || spinResult == Wheel.FREE_TURN || (spinResult < 6 && board.isCategoryUsedUp(spinResult)))) {
			if (spinResult == Wheel.FREE_TURN) { 			
				//Display a "FREE TURN" message
				displayMessage("You earned a free turn that you can use later!");
				
				players[whoseTurn].updateFreeTurns(1);
			}
			
			if (roundIsOver() == false) {
				//Display a "SPIN AGAIN" message
				displayMessage(players[whoseTurn].getName() + ", spin again!");
				
				decreaseSpinsByOne();
				wheel.spin();
			}
			else {
				//end the round
				endRound();
			}
		} 
		else { 
			//Check if landed on a category with an item left or user's or player's choice
			//If landed on a category with an item left or user's or player's choice
			if (spinResult <= 7) {
				int category = -1;
				if (spinResult == Wheel.PLAYER_CHOICE) {
					category = getCategoryChoice(players[whoseTurn].getName(), players[whoseTurn].getName());
				} else if (spinResult == Wheel.OPPONENT_CHOICE) {
					//Figure out next player so they can be asked to choose a category
					int nextPlayer = (whoseTurn + 1) % 3;
					
					category = getCategoryChoice(players[nextPlayer].getName(), players[whoseTurn].getName());
				} else {
					category = spinResult;
				}
				
				//Highlight the category in the JeopardyBoard? (Animation or Effect?)
				//To be implemented if time allows
				
				//Store the current Board Item so it can be used when processing the answer
				currentItem = board.getItem(category);
				
				//Show the next question available in the category
				board.showQuestion(category);
				
				//Ask the user for an answer
				gameViz.askForAnswer();
				
				//Start the timer
				//to be implemented
				
			} else { //deal with other wheel sectors besides categories and category choices
				if (spinResult == Wheel.BANKRUPT) {
					if (players[whoseTurn].getRoundPoints() > 0) {
						displayMessage("Sorry, you went bankrupt.");
					} 
					else if (players[whoseTurn].getRoundPoints() < 0) {
						displayMessage("You went bankrupt.", "Looks like you got lucky this time!");
					} 
					else {
						displayMessage("You went bankrupt.", "No harm done this time.");
					}
					
					players[whoseTurn].clearRoundPoints();	
					
					//Update that it is now the next player's turn
					moveTurnToNextPlayer();
				}
				else {
					//LOSE TURN
					//Display a "LOSE TURN" message
					displayMessage("Sorry, you lost your turn.");
					
					checkForFreeTurn();
					
				}
			}
		}
	}
	
	private void checkForFreeTurn() {
		//Check if the current player has a free turn to use
		if (players[whoseTurn].getFreeTurns() > 0) {
			//Ask if the current player wants to use a free turn
			System.out.println("Asking user if going to use a free turn");
			if (gameViz.playerWantsFreeTurn()) {
				players[whoseTurn].updateFreeTurns(-1);
				beginPlayerTurn();
			}
			else {
				//Update that it is now the next player's turn
				moveTurnToNextPlayer();
			}
		}
		else {
			//Update that it is now the next player's turn
			moveTurnToNextPlayer();
		}
		
	}
	
	private class AnswerHandler implements EventHandler<ActionEvent> {
		@Override
		public void handle(ActionEvent event) {
			processAnswer(gameViz.getAnswer());		
		}	
	}
	
	/*
	 * Ask the current player the next question from the category. Update the points based on the answer.
	 * Return true if answered correctly. Otherwise return false.
	 */
	private void processAnswer(String playerAnswer) {
		
		playerAnswer = playerAnswer.trim().toUpperCase();
		
		boolean correct = false;
		
		if (currentItem.getAnswer().equals(playerAnswer)) {
			//Tell the player that the answer is correct
			displayMessage("Correct!", "You earned " + currentItem.getPointValue() + " points!");
			players[whoseTurn].updatePoints(currentItem.getPointValue());
			correct = true;
		}
		else if (currentItem.getAnswer().equals("")) {
			//Tell the player the correct answer
			displayMessage("The correct answer was \"" + currentItem.getAnswer() + "\"");
		}
		else { 
			//Tell the player that the answer is incorrect and announce the correct answer.
<<<<<<< HEAD
			//Check if the answer was close enough to count as correct.
			
			boolean closeEnough = gameViz.closeEnough(currentItem.getAnswer());
			
			if (closeEnough) {
				players[whoseTurn].updatePoints(currentItem.getPointValue());
				correct = true;
			}
			else {
				players[whoseTurn].updatePoints(0-currentItem.getPointValue());
			}
=======
			displayMessage("Sorry that is incorrect", "The correct answer was \"" + currentItem.getAnswer() + "\"");
>>>>>>> origin/master
			
		}
		
		//Clear the input box for the answer
		gameViz.clearAnswer();
		
		//Mark the question used
		currentItem.markUsed();
		
		board.showJeopardyBoard();
		
		//Reset the timer - not implemented yet
		
		if (correct && roundIsOver() == false) {
			beginPlayerTurn();
		}
		else {
			checkForFreeTurn();
		}
		
	}
	
	/*
	 * Return the index of the category that was chosen for a player choice or opponent's choice
	 */
	private int getCategoryChoice(String playerToAsk, String playerFor) {
		//get the user to choose a category 
		String chosenCategoryName = gameViz.getCategoryChoice(playerToAsk, playerFor, board.getActiveCategories());
		
		//lookup the correct ID (or index) for this category
		String[] names = board.getCategoryNames();
		
		int index = 0;
		
		//Find the category that was chosen and return the correct index
		while (index < names.length) {
			if (names[index].equals(chosenCategoryName)) {
				return index;
			}
			index++;
		}
		
		//This should never happen!
		return -1;
	}
	
	/*
	 * Return true if the current round has ended
	 */
	private boolean roundIsOver() {
		return (spinsRemaining == 0 || board.isEmpty());
	}
	
	/*
	 * Check if the round is over. If it is, end the current round.
	 * If it is not, set the player turn to the next player.
	 */
	private void moveTurnToNextPlayer() {
		if (roundIsOver() == false) {
			players[whoseTurn].removePlayerActive();
			whoseTurn = (whoseTurn + 1) % 3;
			beginPlayerTurn();
		} else {
			//end the current round
			endRound();
			
		}
	}
	
	/*
	 * End the current round. If this is round 1, initiate round 2.
	 */
	private void endRound() {
		displayMessage("Round " + round + " is over.");
	
		if (round == 1) {
			round++;
			playRound();
		}
		else {
			endGame();
		}
	}
	
	/*
	 * End the game by announcing the winner(s)
	 */
	private void endGame() {
		displayMessage("That's the end of the game.", getWinnerMessage());
	}
	
	/*
	 * Decrease the spin count and update it in the GUI
	 */
	private void decreaseSpinsByOne() {
		spinsRemaining--;
		gameViz.updateSpinsRemaining(spinsRemaining);
	}
	
	/*
	 * Return a message for the winner(s).
	 */
	private String getWinnerMessage() {
		int winner = -1;
		
		String winnerMessage;
		
		//Determine who has the highest score
		if (players[0].getTotalPoints() > players[1].getTotalPoints()) {
			if (players[0].getTotalPoints() > players[2].getTotalPoints()) {
				winner = 0;
			} else if (players[2].getTotalPoints() > players[0].getTotalPoints()) {
				winner = 2;
			}
		}
		else {
			if (players[1].getTotalPoints() > players[2].getTotalPoints()) {
				winner = 1;
			}
			else if (players[2].getTotalPoints() > players[1].getTotalPoints()) {
				winner = 2;
			}
		}
		
		//Deal with a tie
		if (winner == -1) {
			if (players[0].getTotalPoints() == players[1].getTotalPoints() && players[0].getTotalPoints() == players[2].getTotalPoints()) {
				winnerMessage = "You all tied!";
			} else if (players[0].getTotalPoints() == players[1].getTotalPoints()) {
				winnerMessage = "The winners are " + players[0].getName() + " and " + players[1].getName() + "!";
			} else if (players[0].getTotalPoints() == players[2].getTotalPoints()) {
				winnerMessage = "The winners are " + players[0].getName() + " and " + players[2].getName() + "!";
			} else {
				winnerMessage = "The winners are " + players[1].getName() + " and " + players[2].getName() + "!";
			}

		} else {
			//Only one winner
			winnerMessage = "The winner is " + players[winner].getName() + "!";
		}
		
		//Need to display the winner message
		return winnerMessage;
		
	}
	
	/*
	 * Display a message for the players in the GUI
	 * 
	 * This version of this method does not use a secondary message
	 */
	private void displayMessage(String message) {
		gameViz.displayMessage(message, "");
<<<<<<< HEAD
	}
	
	/*
	 * Display a message for the players in the GUI
	 * 
	 * This version of this method includes a main message and a secondary message. Both should be shown.
	 */
	private void displayMessage(String mainMessage, String secondaryMessage) {
		gameViz.displayMessage(mainMessage, secondaryMessage);
=======
>>>>>>> origin/master
	}
	
	/*
	 * Display a message for the players in the GUI
	 * 
	 * This version of this method includes a main message and a secondary message. Both should be shown.
	 */
	private void displayMessage(String mainMessage, String secondaryMessage) {
		gameViz.displayMessage(mainMessage, secondaryMessage);
	}

	/*
	 * An interface that must be implemented by a visualization of the WOJ Game
	 */
	public interface WOJGameViz {
		Object getWheelVizContext();
		Object getJeopardyBoardContext();
		Object getPlayerContext(int index);
		void updateSpinsRemaining(int spins);
		void displayMessage(String mainMessage, String secondaryMessage);
		void clearAnswer();
		void askForAnswer();
		String getAnswer();
		String getCategoryChoice(String playerChoosing, String playerFor, ArrayList<String> activeCategories);
		boolean playerWantsFreeTurn();
	}
	
	/*
	 * This class uses JavaFX to create a visualization for the Wheel of JeopardyGame
	 */
	private class JavaFXWOJGameViz implements WOJGameViz {
			
		private Parent root;
		private TextField answerField;
		private Text spinsRemaining;
		
		/*
		 * Constructor
		 */
		public JavaFXWOJGameViz(Parent vizRoot) {
			root = vizRoot;
			answerField = (TextField) root.getScene().lookup("#messagebox");
			answerField.setOnAction(new AnswerHandler());
			
			spinsRemaining = (Text) root.getScene().lookup("#spinscount");
			
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
		
		public void updateSpinsRemaining(int spins) {
			spinsRemaining.setText(""+spins);
		}
		
		public void displayMessage(String mainMessage, String secondaryMessage) {
			
			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setTitle("Wheel of Jeopardy");
			alert.setHeaderText(mainMessage);
			alert.setContentText(secondaryMessage);

			alert.showAndWait();
			
			//This used the TextField before we added dialogs
			//messageField.setText(message);
		}
		
		/*
		 * Clear out the answer box to prepare for the next turn
		 */
		public void clearAnswer() {
			answerField.clear();
		}
		
		/*
		 * Put the focus on the TextField where the user will type the answer.
		 */
		public void askForAnswer() {
			answerField.requestFocus();
		}
		
		/*
		 * Get the answer from the TextField
		 */
		public String getAnswer() {
			return answerField.getText();
		}

<<<<<<< HEAD
	/*
	 * An interface that must be implemented by a visualization of the WOJ Game
	 */
	public interface WOJGameViz {
		Object getWheelVizContext();
		Object getJeopardyBoardContext();
		Object getPlayerContext(int index);
		void updateSpinsRemaining(int spins);
		void displayMessage(String mainMessage, String secondaryMessage);
		void clearAnswer();
		void askForAnswer();
		String getAnswer();
		String getCategoryChoice(String playerChoosing, String playerFor, ArrayList<String> activeCategories);
		boolean playerWantsFreeTurn();
		boolean closeEnough(String answer);
	}
	
	/*
	 * This class uses JavaFX to create a visualization for the Wheel of JeopardyGame
	 */
	private class JavaFXWOJGameViz implements WOJGameViz {
			
		private Parent root;
		private TextField answerField;
		private Text spinsRemaining;
		
		/*
		 * Constructor
		 */
		public JavaFXWOJGameViz(Parent vizRoot) {
			root = vizRoot;
			answerField = (TextField) root.getScene().lookup("#messagebox");
			answerField.setOnAction(new AnswerHandler());
			
			spinsRemaining = (Text) root.getScene().lookup("#spinscount");
			
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
		
		public void updateSpinsRemaining(int spins) {
			spinsRemaining.setText(""+spins);
		}
		
		public void displayMessage(String mainMessage, String secondaryMessage) {
			
			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setTitle("Wheel of Jeopardy");
			alert.setHeaderText(mainMessage);
			alert.setContentText(secondaryMessage);

			alert.showAndWait();
			
			//This used the TextField before we added dialogs
			//messageField.setText(message);
		}
		
		/*
		 * Clear out the answer box to prepare for the next turn
		 */
		public void clearAnswer() {
			answerField.clear();
		}
		
		/*
		 * Put the focus on the TextField where the user will type the answer.
		 */
		public void askForAnswer() {
			answerField.requestFocus();
		}
		
		/*
		 * Get the answer from the TextField
		 */
		public String getAnswer() {
			return answerField.getText();
		}

=======
>>>>>>> origin/master
		/*
		 * Ask a player for a category from the ones with items remaining
		 * 
		 * param: 	playerChoosing - the player being asked to choose
		 * 			playerFor - the player who will be answering the question
		 * 
		 * Note: playerChoosing will be the same as playerFor in the case of "Player's Choice"
		 * 
		 * Pre-condition: activeCategories has at least one category name in it
		 * 
		 */
		public String getCategoryChoice(String playerChoosing, String playerFor, ArrayList<String> activeCategories) {
			//Create a dialog box with a button for each of the active categories
			Alert catChoiceBox = new Alert(AlertType.CONFIRMATION);
			catChoiceBox.setTitle("Wheel of Jeopardy");
			if (playerChoosing.equals(playerFor)) {
				catChoiceBox.setHeaderText(playerChoosing + ", please choose a category.");
			}
			else {
				catChoiceBox.setHeaderText(playerChoosing + ", please choose a category for " + playerFor + ".");
			}
			
			catChoiceBox.setContentText("Choose one option.");
			
			ArrayList<ButtonType> buttonList = new ArrayList<ButtonType>();
			
			for (String catName : activeCategories) {
				buttonList.add(new ButtonType(catName));
			}
			
			catChoiceBox.getButtonTypes().setAll(buttonList);
			
			Optional<ButtonType> result = catChoiceBox.showAndWait();
			
			String chosen = activeCategories.get(buttonList.indexOf(result.get()));
			
			System.out.println(chosen + " category was chosen");
			
			return chosen;
		}
		
		/*
		 * Return true if the current player wants to use a free turn. Otherwise return false.
		 * Pre-condition: The current player has at least one free turn.
		 */
		public boolean playerWantsFreeTurn() {
			Alert freeTurnBox = new Alert(AlertType.CONFIRMATION);
			freeTurnBox.setTitle("Wheel of Jeopardy");
			
			String headerText = "";
			String contentText = "";
			if (players[whoseTurn].getFreeTurns() == 1) {
				headerText = players[whoseTurn].getName() + ", you have a free turn available.";
				contentText = "Do you want to use it?";
			}
			else {
				headerText = players[whoseTurn].getName() + ", you have " + players[whoseTurn].getFreeTurns() + " free turns available.";
				contentText = "Do you want to use a free turn?";
			}
			freeTurnBox.setHeaderText(headerText);
			freeTurnBox.setContentText(contentText);
			
			ButtonType yesButton = new ButtonType("Yes");
			ButtonType noButton = new ButtonType("No");
			
			freeTurnBox.getButtonTypes().setAll(yesButton, noButton);
			
			Optional<ButtonType> response = freeTurnBox.showAndWait();
			
			if (response.get().getText().equals("Yes")) {
				return true;
			}
			else {
				return false;
			}
			
		}
<<<<<<< HEAD

		/*
		 * Return true if the players's agree that the answer was close enough to being correct to count.
		 * Otherwise return false.
		 */
		public boolean closeEnough(String answer) {
			Alert closeEnoughBox = new Alert(AlertType.CONFIRMATION);
			closeEnoughBox.setTitle("Wheel of Jeopardy");
			closeEnoughBox.setHeaderText("Sorry that is incorrect. The correct answer was \"" + answer + "\"");
			closeEnoughBox.setContentText("Was that answer close enough to be counted as correct?");
			ButtonType noButton = new ButtonType("No");
			ButtonType yesButton = new ButtonType("Yes");
			
			closeEnoughBox.getButtonTypes().setAll(noButton, yesButton);
			
			Optional<ButtonType> response = closeEnoughBox.showAndWait();
			
			if (response.get().getText().equals("Yes")) {
				return true;
			}
			else {
				return false;
			}
		}
=======
>>>>>>> origin/master
		
	}
}
