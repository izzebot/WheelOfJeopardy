package woj;

import java.io.IOException;
import java.util.ArrayList;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class WOJTestCode extends Application {
	
	private Parent root;
	private Stage stage;

	public static void main(String[] args) {
		Application.launch(WOJTestCode.class, args);
	}
	
	@Override
	public void start(Stage st) throws Exception {
		
		//Start with Title Screen where the players enter their names.
		
		root = FXMLLoader.load(getClass().getResource("enterplayers.fxml"));
		stage = st;
        
		// Fix button styling so it doesn't glitch when you hover or click
        Button playButton = (Button) root.lookup("#play");
        
        playButton.setOnAction(new PlayButtonHandler());
        playButton.setStyle("-fx-text-fill: gold; -fx-font-size: 26px; -fx-font-weight: bold; -fx-background-color: #0000FF;");
        
        stage.setTitle("Wheel of Jeopardy");
        stage.setScene(new Scene(root, 1200, 800));
        stage.show();
		
	}
	
	private class PlayButtonHandler implements EventHandler<ActionEvent> {
		public void handle(ActionEvent event) {
			TextField player1Box = (TextField) root.lookup("#player1Box");
			TextField player2Box = (TextField) root.lookup("#player2Box");
			TextField player3Box = (TextField) root.lookup("#player3Box");
			
			ArrayList<TextField> nameFields = new ArrayList<TextField>();
			nameFields.add(player1Box);
			nameFields.add(player2Box);
			nameFields.add(player3Box);
			
			processPlayButtonSubmission(nameFields);
		}
	}
	
	/*
	 * Reads in the FXML for the main game scene, uses the names from the enter players scene, sets up the scene, and starts the game.
	 */
	private void processPlayButtonSubmission(ArrayList<TextField> nameFields) {
		if (namesCorrectlyEntered(nameFields)) {			
			
			//go to playGameScene
			try {
				root = FXMLLoader.load(getClass().getResource("gamescene.fxml"));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			Text title = (Text) root.lookup("#titletext");
			
			title.setStyle("-fx-font-size: 50px; -fx-fill: #818181; -fx-effect: innershadow( three-pass-box , rgba(0,0,0,0.7) , 6, 0.0 , 0 , 2 )");
			
			Scene mainScene = new Scene(root, 1200, 800);
			stage.setScene(mainScene);
			
			String[] names = new String[3];
			
			for (int index = 0; index < 3; index++) {
				names[index] = nameFields.get(index).getText().trim();
			}
			Game game = new Game(names, root);
			
			stage.show();
			
			game.playGame();
		}
		else {
			//Error message to remind players to fill in all three names
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Wheel of Jeopardy");
			alert.setHeaderText("You are missing some information!");
			alert.setContentText("Please enter a name for each of the three players.");

			alert.showAndWait();
		}
	}
    
  //For now, return false if name is left blank, otherwise return true
  	private boolean namesCorrectlyEntered(ArrayList<TextField> nameFields) {
  		for (TextField field : nameFields) {
  			if (field.getText().equals("")) {
  				return false;
  			}
  		}
  		return true;		
  	}
}
	