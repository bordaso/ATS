package application;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import screens.AKRapplication;

public class Main extends Application {

	Stage primaryStage;

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) {
		
		this.primaryStage=primaryStage;

		Scene scene = new Scene(new AKRapplication(), 1400, 900);
		
		// stage.getIcons().add(new
		// Image(<yourclassname>.class.getResourceAsStream("icon.png")));
		// stage.getIcons().add(new Image("file:icon.png"));
		// /AKRFazisI/src/misc/AKRlogo.PNG

		// primaryStage.getIcons().add(new Image("misc/AKRlogo3.png"));

		// primaryStage.getIcons().add(new
		// Image(Main.class.getResourceAsStream("/misc/AKRlogo3.png")));

//		 primaryStage.getIcons().add(new
//		 Image(Main.class.getResourceAsStream("AKRlogo3.png")));

		primaryStage.setOnCloseRequest(evt -> {
		    Alert alert = new Alert(AlertType.CONFIRMATION);
		    alert.setTitle("Confirm Close");
		    alert.setHeaderText("Close program?");
		    alert.showAndWait().filter(r -> r != ButtonType.OK).ifPresent(r->evt.consume());
		});
		
		
		primaryStage.setScene(scene);
		primaryStage.setTitle("Frontpoint AKR");
		primaryStage.show();
	}

	

}
