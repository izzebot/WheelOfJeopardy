package woj;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.TimerTask;

import javafx.application.Platform;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.application.Platform;

import java.util.Timer;

/*
 * GameTimer class
 * 
 * This class is used to keep track of the time each player has to answer a question.
 */
public class GameTimer {
	private int timeLimit; //The time limit for each question
	private int currentTime; //The time currently left to answer the current question
	Timer timer;
	GameTimerViz timerViz;
	EventHandler timeOutHandler;
	
	boolean running;
	
	public GameTimer(int limit, Object context, EventHandler toHandler) {
		timeLimit = limit;
		currentTime = timeLimit;

		timer = new Timer(true);
		timerViz = new JavaFXGameTimerViz(context);
		running = false;
		timeOutHandler = toHandler;
	}
	
	public void countDown() {
		//timer.start();
		running = true;
		timer.schedule(new GameTimerTask(), 0);
		
	}
	
	public void reset() {
		currentTime = timeLimit;
		running = false;
		timerViz.updateTime(currentTime);
	}
	
	public boolean isElapsed() {
		return currentTime == 0;
	}
	
	public int getCurrentTime() {
		return currentTime;
	}
	
	public void cancel() {
		running = false;
	}
	
	private class GameTimerTask extends TimerTask {

		@Override
		public void run() {
			
			if (running) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
		            e.printStackTrace();
		        }
				
				if (isElapsed() == false) {
					currentTime -= 1;
					timerViz.updateTime(currentTime);
					run();
				}
				else {
					Platform.runLater(new Runnable() {
						public void run() {
							timeOutHandler.handle(null);
						}
					});
				}
			}
		}
		
	}
	

	
	public interface GameTimerViz {

		void updateTime(int timeLeft);
	}
	
	private class JavaFXGameTimerViz implements GameTimerViz {
		private VBox timerBox;
		private Text timerLabel;
		private Text timerCount;
		
		public JavaFXGameTimerViz(Object context) {
			timerBox = (VBox) context;
			timerLabel = (Text) timerBox.getChildren().get(0);
			timerCount = (Text) timerBox.getChildren().get(1);
		}
		
		public void updateTime(int timeLeft) {
			timerCount.setText("" + timeLeft);
		}
		

	}
}
