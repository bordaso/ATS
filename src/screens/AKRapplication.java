package screens;

import java.io.IOException;
import java.net.DatagramSocket;
import java.time.LocalTime;

import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import util.APICaller;

public class AKRapplication extends BorderPane {
	
	LocalTime now;
	
	
	private Color defaultColor = Color.color(0.78, 0.78, 0.78, 1);
	private Border defaultBorder = new Border(
			new BorderStroke(defaultColor, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT));


	public AKRapplication() {	

		ContentFinal contentFinal = new ContentFinal();

		Button addButton = addTickerScreenButtonSetup(contentFinal);
		Button deleteTickerScreenButton = deleteTickerScreenButtonSetup(contentFinal);

		HBox menuPanel = new HBox(addButton, deleteTickerScreenButton);
		menuPanel.setSpacing(20);
      
		  setTop(new TopLayoutHolder(contentFinal));
		  setCenter(new CenterLayoutHolder(contentFinal));
		  setBottom(menuPanel);
        
		setBorder(defaultBorder);
		
		startThread();		
	}

	public void startThread() {

		Thread timerThread = new Thread(() -> {

			while (true) {

				try {

					Thread.sleep(1);
					now = LocalTime.now();

				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				Platform.runLater(new Runnable() {

					public void run() {
						((TopLayoutHolder) getTop()).getTimeText().setText(now.toString());
					}
				});
			}
		});

		timerThread.setDaemon(true);
		timerThread.start();

	}


	private Button addTickerScreenButtonSetup(VBox contentFinal) {		
		
		Button addButton = new Button("Add a ticker screen");
		addButton.setOnAction(e -> {

			contentFinal.getChildren().add(new TickerScreenHolder());
		});

		return addButton;
	}
	
	
	private Button deleteTickerScreenButtonSetup(VBox contentFinal) {
		Button deleteTickerScreenButton = new Button("Delete a ticker screen");

		deleteTickerScreenButton.setOnAction((evt -> {
			int idx = contentFinal.getChildren().size();

			if (idx == 0) {
				return;
			}

			TickerScreenLong buyScreen =((TickerScreenLong)(((HBox)(((VBox) (contentFinal.getChildren().get(idx - 1))).getChildren().get(0))).getChildren().get(0)));
			DatagramSocket openSocket = buyScreen.getScreensServerSocket();
			
			if(!buyScreen.getTickerName().getText().isEmpty()){
			try {
				APICaller.callStopGetTickerData(buyScreen);
			} catch (IOException e) {
				e.printStackTrace();
			}
			}
			
			
			if(openSocket != null && !openSocket.isClosed()){
				openSocket.close();
			}
			
			contentFinal.getChildren().remove(idx - 1);
		}));

		return deleteTickerScreenButton;
	}
	

	

	
	
}
