package woj;

public interface WOJGameViz {
	Object getWheelVizContext();
	Object getJeopardyBoardContext();
	Object getPlayerContext(int index);
	void displayMessage(String message);
	void clearMessage();
}