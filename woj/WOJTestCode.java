package woj;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class WOJTestCode extends Application {

	public static void main(String[] args) {
		Application.launch(WOJTestCode.class, args);
	}
	
	@Override
	public void start(Stage stage) throws Exception {
		
		Parent root = FXMLLoader.load(getClass().getResource("gamescene.fxml"));
        		
		stage.setTitle("Wheel of Jeopardy");
		
		//StackPane wheelPane = new StackPane();
		
		Scene mainScene = new Scene(root, 1200, 800);
		stage.setScene(mainScene);
		
		String[] names = {"Liz", "Danielle", "Nik"};
		Game game = new Game(names, root);
		
		stage.show();
		
		game.playGame();
		
	}

}
