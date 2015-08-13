package woj;

import javafx.scene.control.Button;

public class BoardItem {
	private final String question;
	private final String answer;
	private final int pointValue;
	private boolean used;
	private BoardItemViz itemViz;
	
	public BoardItem(String quest, String ans, int pv) {
		question = quest;
		answer = ans;
		pointValue = pv;
		used = false;
		
	}
	
	public String getQuestion() {
		return question;
	}
	
	public String getAnswer() {
		return answer;
	}
	
	public int getPointValue() {
		return pointValue;
	}
	
	public boolean getUsed() {
		return used;
	}
	
	public void markUsed() {
		used = true;
		itemViz.showItemUsed();
	}
	
	public void setViz(Object context) {
		itemViz = new JavaFXBoardItemViz(context);
	}
	
	interface BoardItemViz {
		void showItemUsed();
	}
	
	private class JavaFXBoardItemViz implements BoardItemViz {
		private Button itemButton;
		
		public JavaFXBoardItemViz(Object context) {
			itemButton = (Button) context;
		}
		
		public void showItemUsed() {
			itemButton.setText("");
		}
		
	}
}

