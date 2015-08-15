package woj;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;
import java.util.Random;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
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
	private int itemsRemaining;
	private BoardItem currentItem;
	private GameTimer timer;
	private WOJGameViz gameViz;
	private int dailyDoubleCount;
	private int dailyDoubleAmount;
	private boolean dailyDouble;
	
	public Game(String[] names, Parent root) {
		gameViz = new JavaFXWOJGameViz(root);
		
		players = new Player[3];
		
		//Create the three players using the names entered
		players[0] = new Player(names[0], gameViz.getPlayerContext(0));
		players[1] = new Player(names[1], gameViz.getPlayerContext(1));
		players[2] = new Player(names[2], gameViz.getPlayerContext(2));
		
		//Choose a random player to have the first turn
		whoseTurn = (int) (Math.random() * 3);
		
		round = 1;
		spinsRemaining = 50;
		itemsRemaining = 30;
		dailyDoubleCount = 2;
		dailyDoubleAmount = 0;
		dailyDouble = false;
		timer = new GameTimer(30, gameViz.getTimerContext(), new AnswerHandler());
		
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
		
		spinsRemaining = 50;
		itemsRemaining = 30;
		gameViz.updateSpinsRemaining(spinsRemaining);
		dailyDoubleCount = 2;
		dailyDoubleAmount = 0;
		dailyDouble = false;
		
		//Clear everybody's round points
		for (Player player : players) {
			player.clearRoundPoints();
		}
		
		displayMessage("Round " + round, "Let's begin!");
		
		//Initiate the first turn
		beginPlayerTurn();
	
	}
	
	/*
	 * Begin a player turn. A player may have several consecutive turns either by answering a question correctly or free turns.
	 */
	private void beginPlayerTurn() {		
		players[whoseTurn].showPlayerActive();
		
		displayMessage(players[whoseTurn].getName() + ", spin the wheel!");
		
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
		decreaseSpinsByOne();
		
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
				
				//dailyDouble check per Round
				if(dailyDoubleCount > 0 && itemsRemaining <= 2){
					dailyDouble = true;
					System.out.println("should be daily double");
					dailyDouble();
					System.out.println(dailyDoubleAmount);
					dailyDoubleCount--;
				}
				
				else{
					if(dailyDoubleCount > 0 && itemsRemaining > 2){
						Random rn = new Random();
						int chance = rn.nextInt(10 - 1 + 1) + 1;
						
						if(chance == 2){
							dailyDouble = true;
							System.out.println("should be daily double");
							dailyDouble();
							System.out.println(dailyDoubleAmount);
							dailyDoubleCount--;
						}
					}
				}
				
				//Store the current Board Item so it can be used when processing the answer
				currentItem = board.getItem(category);
				
				//Show the next question available in the category
				board.showQuestion(category);
				
				//Ask the user for an answer
				gameViz.askForAnswer();
				
				//Start the timer
				timer.countDown();
				
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
					
					players[whoseTurn].goBankrupt();	
					
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
		if (roundIsOver()) {
			endRound();
		}
		else {
			//Check if the current player has a free turn to use
			if (players[whoseTurn].getFreeTurns() > 0) {
				//Ask if the current player wants to use a free turn
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
		timer.cancel();
		itemsRemaining--;
		playerAnswer = playerAnswer.trim().toUpperCase();
		
		boolean correct = false;
		
		if (currentItem.getAnswer().equals(playerAnswer)) {
			//Tell the player that the answer is correct
			if (dailyDouble){
				displayMessage("Correct!", "You earned " + dailyDoubleAmount + " points!");
				players[whoseTurn].updatePoints(dailyDoubleAmount);
				dailyDouble = false;
			}
			else{
				displayMessage("Correct!", "You earned " + currentItem.getPointValue() + " points!");
				players[whoseTurn].updatePoints(currentItem.getPointValue());
			}
			
			correct = true;
		}
		else if (playerAnswer.length() == 0) {
			//Tell the player the correct answer
			displayMessage("The correct answer was \"" + currentItem.getAnswer() + "\"");
		}
		else { 
			//Tell the player that the answer is incorrect and announce the correct answer.
			//Check if the answer was close enough to count as correct.
			
			boolean closeEnough = gameViz.closeEnough(currentItem.getAnswer());
			
			if (closeEnough) {
				if(dailyDouble){
					players[whoseTurn].updatePoints(dailyDoubleAmount);
					dailyDouble = false;
				}
				else{
					players[whoseTurn].updatePoints(currentItem.getPointValue());
				}
				correct = true;
			}
			else {
				if(dailyDouble){
					players[whoseTurn].updatePoints(0-dailyDoubleAmount);
					dailyDouble = false;
				}
				else{
					players[whoseTurn].updatePoints(0-currentItem.getPointValue());
				}
			}
			
		}
		
		//Clear the input box for the answer
		gameViz.clearAnswer();
		
		//Mark the question used
		currentItem.markUsed();
		
		board.showJeopardyBoard();
		
		//Reset the timer
		timer.reset();
		
		if (correct && roundIsOver() == false) {
			beginPlayerTurn();
		}
		else {
			checkForFreeTurn();
		}
		
		dailyDoubleAmount = 0;
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
		players[whoseTurn].removePlayerActive();
		whoseTurn = (whoseTurn + 1) % 3;
		if (roundIsOver() == false) {
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
		
		String winnerMessage = "Final Scores:\n";
		
		for (Player player : players) {
			winnerMessage += player.getName() + ": " + player.getTotalPoints() + "\n";
		}
		
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
				winnerMessage += "You all tied!";
			} else if (players[0].getTotalPoints() == players[1].getTotalPoints()) {
				winnerMessage += "The winners are " + players[0].getName() + " and " + players[1].getName() + "!";
			} else if (players[0].getTotalPoints() == players[2].getTotalPoints()) {
				winnerMessage += "The winners are " + players[0].getName() + " and " + players[2].getName() + "!";
			} else {
				winnerMessage += "The winners are " + players[1].getName() + " and " + players[2].getName() + "!";
			}

		} else {
			//Only one winner
			winnerMessage += "The winner is " + players[winner].getName() + "!";
		}
		
		//Need to display the winner message
		return winnerMessage;
		
	}
	public int dailyDouble(){
		TextInputDialog dialog = new TextInputDialog("5");
		dialog.setTitle("Daily Double!");
		
		if (players[whoseTurn].getTotalPoints() < currentItem.getPointValue()){
			dialog.setHeaderText("How much would you like to wager? It must be between 5 and " + currentItem.getPointValue());
		}
		else{
			dialog.setHeaderText("How much would you like to wager? It must be between 5 and " + players[whoseTurn].getTotalPoints());
		}
		dialog.setContentText("Enter your wager here:" );
		Optional<String> result = dialog.showAndWait();
		if (result.isPresent()){
		    dailyDoubleAmount = Integer.parseInt(result.get());
		    if (players[whoseTurn].getTotalPoints() < currentItem.getPointValue()){
			    if (dailyDoubleAmount > currentItem.getPointValue()){
			    	displayMessage("that is too high! Try again");
			    	dailyDouble();
			    }
		    }
		    else if (dailyDoubleAmount > players[whoseTurn].getTotalPoints() && players[whoseTurn].getTotalPoints() > 5){
		    	displayMessage("that is too high! Try again");
		    	dailyDouble();
		    }
		}
		return dailyDoubleAmount;
	}
	/*
	 * Display a message for the players in the GUI
	 * 
	 * This version of this method does not use a secondary message
	 */
	private void displayMessage(String message) {
		gameViz.displayMessage(message, "");
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
		Object getTimerContext();
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
			answerField.setDisable(true);
			
			spinsRemaining = (Text) root.getScene().lookup("#spinscount");
			
		}
		
		public Object getWheelVizContext() {
			return root.getScene().lookup("#wheelpane");	
		}

		public Object getJeopardyBoardContext() {
			return root.getScene().lookup("#jeopardyboard");
		}
		
		public Object getPlayerContext(int index) {
			int indexForString = index + 1;
			return root.getScene().lookup("#player" + indexForString);
		}
		
		public Object getTimerContext() {
			return root.getScene().lookup("#timer");
		}
		
		public void updateSpinsRemaining(int spins) {
			spinsRemaining.setText(""+spins);
		}
		
		public void displayMessage(String mainMessage, String secondaryMessage) {
			
			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setTitle("Wheel of Jeopardy");
			alert.setHeaderText(mainMessage);
			alert.setContentText(secondaryMessage);			
			alert.getDialogPane().setStyle("-fx-font-size: 22px; -fx-font-weight: bold");
			alert.showAndWait();
			
			//This used the TextField before we added dialogs
			//messageField.setText(message);
		}
		
		/*
		 * Clear out the answer box to prepare for the next turn
		 */
		public void clearAnswer() {
			answerField.clear();
			answerField.setDisable(true);
		}
		
		/*
		 * Put the focus on the TextField where the user will type the answer.
		 */
		public void askForAnswer() {
			answerField.setDisable(false);
			answerField.requestFocus();
		}
		
		/*
		 * Get the answer from the TextField
		 */
		public String getAnswer() {
			return answerField.getText();
		}

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
			catChoiceBox.getDialogPane().setStyle("-fx-font-size: 16px; -fx-font-weight: bold");
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
			
			return chosen;
		}
		
		/*
		 * Return true if the current player wants to use a free turn. Otherwise return false.
		 * Pre-condition: The current player has at least one free turn.
		 */
		public boolean playerWantsFreeTurn() {
			Alert freeTurnBox = new Alert(AlertType.CONFIRMATION);
			freeTurnBox.setTitle("Wheel of Jeopardy");
			freeTurnBox.getDialogPane().setStyle("-fx-font-size: 22px; -fx-font-weight: bold");
			
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

		/*
		 * Return true if the players's agree that the answer was close enough to being correct to count.
		 * Otherwise return false.
		 */
		public boolean closeEnough(String answer) {
			Alert closeEnoughBox = new Alert(AlertType.CONFIRMATION);
			closeEnoughBox.setTitle("Wheel of Jeopardy");
			closeEnoughBox.setHeaderText("Sorry that is incorrect. The correct answer was \"" + answer + "\"");
			closeEnoughBox.setContentText("Was that answer close enough to be counted as correct?");
			closeEnoughBox.getDialogPane().setStyle("-fx-font-size: 22px; -fx-font-weight: bold");
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

	}
}
