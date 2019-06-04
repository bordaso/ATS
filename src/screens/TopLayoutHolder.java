package screens;

import java.io.IOException;
import java.util.Map;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import util.APICaller;
import util.InputXMLFormatter;

public class TopLayoutHolder extends VBox {
	
	private TextField timeText = new TextField();
	private Button getGlobalNumberOfOpenedPositionsButton = new Button("Get  positions ");
	private ContentFinal contentFinal;
	
	
	public TopLayoutHolder(ContentFinal contentFinal){
		
		this.contentFinal=contentFinal;
		
		setupActionButtons();
		
		GlobalSetupScreen globalSetupLong = new GlobalSetupScreen(contentFinal, false);
		globalSetupLong.getTitle().setText("Global Setup Long");
		GlobalSetupScreen globalSetupShort = new GlobalSetupScreen(contentFinal, true);
		globalSetupShort.getTitle().setText("Global Setup Closing");		
		
		HBox topRow = new HBox(globalSetupLong, globalSetupShort);
		topRow.setSpacing(398);
		
		HBox middleRow = new HBox(timeText, getGlobalNumberOfOpenedPositionsButton);
		middleRow.setSpacing(550);
		middleRow.setPadding(new Insets(5, 0, 20, 0));
		
		HBox screenLabels = new HBox(new Label("LONG"), new Label("CLOSE"));
		screenLabels.setSpacing(670);
		
		setPadding(new Insets(10, 0, 0, 20));
		
		getChildren().addAll(new Node[]{topRow, middleRow, screenLabels});
	}

	public TextField getTimeText() {
		return timeText;
	}
	
	private void setupActionButtons() {

		getGlobalNumberOfOpenedPositionsButton.setOnAction(evt -> {
			getGlobalNumberOfOpenedPositionsButton.setDisable(true);
			startNumberOfOpenedPositionsQueryThread();
		});

	}
	
	public void startNumberOfOpenedPositionsQueryThread() {

		Thread timerThread = new Thread(() -> {

			while (true) {

				try {

					Thread.sleep(5000);

				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				Platform.runLater(new Runnable() {

					public void run() {
						
						String xmlResponse="";

						try {
							 xmlResponse = APICaller.callGetPositions("TAMAADAM");
						} catch (IOException e) {
							e.printStackTrace();
						}	
						
						Map<String, String> positions = InputXMLFormatter.getPositionValues(xmlResponse);
						
						for (Node tickerScreenHolder : contentFinal.getChildren()) {

							TickerScreenShort shortScreen = ((TickerScreenHolder) tickerScreenHolder).getShortScreen();
							shortScreen.getNumberOfOpenedPositions().setText(positions.get(shortScreen.getBuyScreenReference().getTickerName().getText()));							
					

						}
																	
					}
				});
			}
		});

		timerThread.setDaemon(true);
		timerThread.start();

	}
	
	
}
