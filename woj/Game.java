package woj;

import java.io.IOException;

import javafx.scene.Parent;

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
	
	private WOJGameViz gameViz;
	
	public Game(String[] names, Parent root) {
		gameViz = new JavaFXWOJGameViz(root);
		
		players = new Player[3];
		
		//Create the three players using the names entered
		System.out.println(gameViz.getPlayerContext(0));
		players[0] = new Player(names[0], gameViz.getPlayerContext(0));
		players[1] = new Player(names[1], gameViz.getPlayerContext(1));
		players[2] = new Player(names[2], gameViz.getPlayerContext(2));
		
		whoseTurn = (int) (Math.random() * 3);
		round = 1;
		
	}
	
	/*
	 * Play one full game.
	 */
	public void playGame() {
		
		//Temporarily just using one round for testing
		playRound();
		
		//Play two rounds
		/*
		while (round < 3) {
			playRound();
		}
		*/
		//The game is over, so announce the winner(s)
		announceWinner();
		
		
	}
	
	/*
	 * Play one round of the game
	 */
	private void playRound() {
		
		//Make a new Jeopardy board for this round
		//This needs to be fixed to use different files for each round / game
		try {
			board = new JeopardyBoard(round, "src/rounds/Round1.txt", gameViz.getJeopardyBoardContext());
		} catch (IOException e) {
			e.printStackTrace();
		}	
		
		wheel = new Wheel(board.getCategoryNames(), gameViz.getWheelVizContext());
		
		spinsRemaining = 50;
		/*
		//Keep taking turns while there are spins remaining and board items remaining
		while (spinsRemaining > 0 && board.isEmpty() == false) {
			playerTurn();
		}
		
		round++;		
		*/	
	}
	
	/*
	 * Complete one player turn. A player may have several consecutive turns either by answering a question correctly or free turns.
	 */
	private void playerTurn() {
		//This needs to be implemented. The code here is just temporary.
		System.out.println("Starting player turn for " + players[whoseTurn].getName());
		System.out.println("Spins Remaining: " + spinsRemaining);
		
		players[whoseTurn].showPlayerActive();
		
		int spinResult = -1;
		
		displayMessage("Press [Space Bar] to spin the wheel!");
		
		//Must wait for key press to spin the wheel
		
		//Spin until you get something to act on
		do {			
			//This will need to be modified to wait for the user to click the Spin button
			spinResult = wheel.spin();
			spinsRemaining--;
			
			if (spinResult == 10 || spinResult == 11 || (spinResult < 6 && board.isCategoryUsedUp(spinResult))) {
				//SPIN AGAIN
				
				if (spinResult == 11) { //FREE TURN			
					//Display a "FREE TURN" message
					displayMessage("You earned a free turn that you can use later!");
					
					players[whoseTurn].updateFreeTurns(1);
				}
				
				//Display a "SPIN AGAIN" message
				displayMessage("Press [Space Bar] to spin again!");
				
			}
		} while (spinsRemaining > 0 && (spinResult == 10 || spinResult == 11 || (spinResult < 6 && board.isCategoryUsedUp(spinResult))));
		
		//If landed on a category with an item left or user's or player's choice
		if (spinResult <= 7) {
			int category = -1;
			if (spinResult == 6) {
				category = players[whoseTurn].getCategoryChoice();
			} else if (spinResult == 7) {
				//Figure out next player so they can be asked to choose a category
				int nextPlayer = (whoseTurn + 1) % 3;
				
				category = players[nextPlayer].getCategoryChoice();
			} else {
				category = spinResult;
			}
			
			boolean correct = questionAndAnswer(category);
			
			if (correct) {
				//recursively call this method to start the turn over
				playerTurn();
			} else {				
				
				//Update that it is now the next player's turn
				moveTurnToNextPlayer();
			}
			
		} else { //deal with other wheel sectors besides categories and category choices
			if (spinResult == 8) {
				//BANKRUPT
				//Display a "BANKRUPT" message
				System.out.println("Bankrupt");
				
				players[whoseTurn].clearRoundPoints();	
			}
			else {
				//LOSE TURN
				//Display a "LOSE TURN" message
				System.out.println("Lost turn");
			}
			
			//Update that it is now the next player's turn
			moveTurnToNextPlayer();
			
		}
	}
	
	private void moveTurnToNextPlayer() {
		whoseTurn = (whoseTurn + 1) % 3;
		players[whoseTurn].removePlayerActive();
	}
	
	private boolean checkForFreeTurn() {
		return false;
	}
	
	/*
	 * Ask the current player the next question from the category. Update the points based on the answer.
	 * Return true if answered correctly. Otherwise return false.
	 */
	private boolean questionAndAnswer(int categoryNumber) {
		
		BoardItem currentItem = board.getItem(categoryNumber);
		
		board.showQuestion(categoryNumber);
		
		//Start the timer - not implemented yet
		
		//Get user's answer - not implemented yet
		String playerAnswer = "What is cheese?";
		
		playerAnswer = playerAnswer.trim();
		
		if (currentItem.getAnswer().equals(playerAnswer)) {
			//Tell the player that the answer is correct
			displayMessage("Correct!");
			players[whoseTurn].updatePoints(currentItem.getPointValue());
			
		}
		else if (currentItem.getAnswer().equals("")) {
			//Tell the player the correct answer
			displayMessage("The correct answer was \"" + currentItem.getAnswer() + "\"");
			
		}
		else { 
			//Tell the player that the answer is incorrect and announce the correct answer.
			displayMessage("Sorry that is incorrect. The correct answer was \"" + currentItem.getAnswer() + "\"");
			
			players[whoseTurn].updatePoints(0-currentItem.getPointValue());
		}
		
		currentItem.markUsed();
		
		//Reset the timer - not implemented yet
		
		return false;
	}
	
	private void announceWinner() {
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
		displayMessage(winnerMessage);
		
	}
	
	private void displayMessage(String message) {
		gameViz.displayMessage(message);
	}

}
