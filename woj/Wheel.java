package woj;

import java.util.ArrayList;

import javafx.animation.PauseTransition;
import javafx.animation.RotateTransition;
import javafx.animation.SequentialTransition;
import javafx.geometry.VPos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;

/*
 * Wheel class
 */
public class Wheel {
	private int angle;
	private Sector[] sectors;
	private WheelViz wheelViz;
	
	//Sector IDs
	public static final int PLAYER_CHOICE = 6;
	public static final int OPPONENT_CHOICE = 7;
	public static final int BANKRUPT = 8;
	public static final int LOSE_TURN = 9;
	public static final int SPIN_AGAIN = 10;
	public static final int FREE_TURN = 11;
	
	/*
	 * Create a new Wheel using the category names provided
	 * param: catNames - The category names for this round
	 */
	public Wheel(String[] catNames, Object context, EventHandler<ActionEvent> afterSpinEvent) {
		//The angle starts at 15 because of the way the wheel is drawn
		angle = 15;		
		sectors = new Sector[12];
		
		//create an ArrayList to get sector locations from
		ArrayList<Integer> sectorLocations = new ArrayList<Integer>(12);
		
		for (int num = 0; num <= 11; num++) {
			sectorLocations.add(num);
		}
		
		//create color array to use for Sectors
		String[] sectorColors = {"#66FFFF", "#FF9933", "#FF66FF", "#CC6633", "#3366CC", "#669900"};
		
		//Add sectors for the categories in random sector locations
		int sectorNum;
		for (int index = 0; index <= 5; index++) {
			sectorNum = sectorLocations.remove((int) (Math.random() * sectorLocations.size()));
			
			sectors[sectorNum] = new Sector(catNames[index], Color.web(sectorColors[index]), index);
		}
		
		//Add sectors for other results in random locations
		sectorNum = sectorLocations.remove((int) (Math.random() * sectorLocations.size()));
		sectors[sectorNum] = new Sector("PLAYER'S CHOICE", Color.web("#99CCCC"), 6);
		
		sectorNum = sectorLocations.remove((int) (Math.random() * sectorLocations.size()));
		sectors[sectorNum] = new Sector("OPPONENT'S CHOICE", Color.web("#CC9900"), 7);
		
		sectorNum = sectorLocations.remove((int) (Math.random() * sectorLocations.size()));
		sectors[sectorNum] = new Sector("BANKRUPT", Color.web("#CCCCCC"), 8);
		
		sectorNum = sectorLocations.remove((int) (Math.random() * sectorLocations.size()));
		sectors[sectorNum] = new Sector("LOSE TURN", Color.web("#FF0000"), 9);
		
		sectorNum = sectorLocations.remove((int) (Math.random() * sectorLocations.size()));
		sectors[sectorNum] = new Sector("SPIN AGAIN", Color.web("#FFFF66"), 10);
		
		sectorNum = sectorLocations.remove((int) (Math.random() * sectorLocations.size()));
		sectors[sectorNum] = new Sector("FREE TURN", Color.web("#00FF33"), 11);
		
		
		wheelViz = new JavaFXWheelViz(context, afterSpinEvent);		
	}
	
	/*
	 * Spin the wheel and return a int representing the sector that was landed on
	 */
	public void spin() {
		
		int spinAngle = (int) (Math.random() * 360 + 360);
		angle = (angle + spinAngle) % 360;
		
		//start animation
		wheelViz.spinWheel(spinAngle);
	}
	
	public int getCurrentSector() {
		
		int sectorNum = angle / 30;
		
		return sectors[sectorNum].getID();
	}
	
	/*
	 * This interface must be implemented by the class that is used to visualize the wheel in the game GUI
	 */
	interface WheelViz {
		void drawWheel(int radius);
		void spinWheel(int spinAngle);
	}
	
	/*
	 * JavaFXWheelViz: An inner class to create a visualization of the Wheel for the GUI
	 * 
	 * This class implements the WheelViz interface using a JavaFX StackPane
	 */
	private class JavaFXWheelViz implements WheelViz{
		private StackPane wheelPane;
		private Canvas wheelCanvas;
		private Canvas pointerCanvas;
		private EventHandler<ActionEvent> afterSpin;
		/*
		 * Construct a wheel in a given StackPane
		 */
		public JavaFXWheelViz(Object pane, EventHandler<ActionEvent> afterSpinEvent) {
			wheelPane = (StackPane) pane;			
			afterSpin = afterSpinEvent;
			
			int smallerDimension = 400; //should be based on pane size, but didn't work for some reason
			wheelCanvas = new Canvas(smallerDimension, smallerDimension); 
			wheelCanvas.getGraphicsContext2D().translate(smallerDimension / 2, smallerDimension / 2);
			wheelPane.getChildren().add(wheelCanvas);
			
			int pointerCanvasDimension = (int) (smallerDimension * 1.1);
			
			pointerCanvas = new Canvas(pointerCanvasDimension, pointerCanvasDimension); 
			pointerCanvas.getGraphicsContext2D().translate(pointerCanvasDimension, pointerCanvasDimension / 2);
			wheelPane.getChildren().add(pointerCanvas);
			
			drawWheel((int) (smallerDimension / 2 * 0.95));
			
			//Set up event handler for clicking on the Wheel (not currently being used)
			/*
			wheelPane.addEventHandler(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
				public void handle(MouseEvent event) {
	                spin();
	            }
			});
			*/
		}
		
		/*
		 * Draw the wheel rotated to the current angle.
		 * 
		 */
		public void drawWheel(int radius) {
			GraphicsContext gc = wheelCanvas.getGraphicsContext2D();
			
			double startAngle = -15;
			double endAngle = startAngle + 30;
			double innerRadius = radius * 0.3;
			
			gc.setTextAlign(TextAlignment.CENTER);
			gc.setTextBaseline(VPos.CENTER);
			gc.setFont(new Font("Calibri Lite", 35));

			//draw the twelve sectors that make up the wheel
			for (int index = 0; index < 12; index++) {				
				gc.setLineWidth(radius * 0.02);
				gc.setStroke(sectors[index].getColor());
				gc.setFill(sectors[index].getColor());
			
				gc.beginPath();
				
				//go to the correct starting point for this sector
				double startX = radius * Math.cos(Math.toRadians(startAngle));
		        double startY = radius * Math.sin(Math.toRadians(startAngle));
		        gc.moveTo(startX, startY);
		        
		        //draw the outline of the sector and fill it in
		        double x = innerRadius * Math.cos(Math.toRadians(startAngle));
		        double y = innerRadius * Math.sin(Math.toRadians(startAngle));
		        gc.lineTo(x, y);
		        
		        gc.arc(0.0, 0.0, innerRadius, innerRadius, 15, -30);
		        
		        x = radius * Math.cos(Math.toRadians(endAngle));
		        y = radius * Math.sin(Math.toRadians(endAngle));
		        gc.lineTo(x, y);
		        
		        gc.arc(0.0, 0.0, radius, radius, -15, 30);        
		        
		        gc.stroke();
				gc.fill();
				
				//add the label to the sector
				gc.setStroke(Color.BLACK);
				x = radius * 0.65;
				y = 0;
				gc.strokeText(sectors[index].getLabel(), x, y - radius * 0.01, radius * 0.6);
				
				//turn 30 degrees before drawing the next sector
				gc.rotate(-30);
			}		
			
			//draw the pointer
			GraphicsContext pointerGC = pointerCanvas.getGraphicsContext2D();
			pointerGC.setLineWidth(radius * 0.02);
			pointerGC.setStroke(Color.BLACK);
			pointerGC.setFill(Color.BLACK);
			
			pointerGC.beginPath();
			pointerGC.lineTo(0, -radius * 0.04);
			pointerGC.lineTo(-radius * 0.15, 0);
			pointerGC.lineTo(0, radius * 0.04);
			
			pointerGC.stroke();
			pointerGC.fill();
			
		}
		
		/*
		 * Animate the wheel spinning the amount specified by the parameter spinAngle
		 */
		public void spinWheel(int spinAngle) {
			
			RotateTransition rt = new RotateTransition(Duration.millis(spinAngle * 2), wheelCanvas);
			rt.setByAngle(spinAngle);
			
			
			SequentialTransition rtWithPause = new SequentialTransition (
					new PauseTransition(Duration.millis(400)), rt, new PauseTransition(Duration.millis(750)) // wait a second
			     );
			
			//Delay the spin by .4 seconds
			//rt.setDelay(Duration.millis(400));
			rtWithPause.setOnFinished(afterSpin);
			
			rtWithPause.play();
		}
	}
	
}
