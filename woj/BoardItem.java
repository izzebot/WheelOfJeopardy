package woj;

public class BoardItem {
	private final String question;
	private final String answer;
	private final int pointValue;
	private boolean used;
	
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
	}
	
}
