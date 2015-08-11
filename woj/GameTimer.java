package woj;

/*
 * GameTimer class
 * 
 * This class is used to keep track of the time each player has to answer a question.
 */
public class GameTimer {
	private int timeLimit; //The time limit for each question
	private int currentTime; //The time currently left to answer the current question
	
	public GameTimer(int limit) {
		timeLimit = limit;
		currentTime = timeLimit;
	}
	
	public void countDown() {
		//Needs to be implemented
	}
	
	public void resetTimer() {
		currentTime = timeLimit;
	}
	
	public boolean isElapsed() {
		return currentTime == 0;
	}
	
	public int getCurrentTime() {
		return currentTime;
	}
}
