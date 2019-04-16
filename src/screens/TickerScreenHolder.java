package screens;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Separator;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public class TickerScreenHolder extends VBox {
	
	private String cssLayout = "-fx-border-color: grey;\n" + "-fx-border-insets: 5;\n" + "-fx-border-width: 3;\n";
	private Color separatorColor = Color.color(0.78, 0.78, 0.78, 1);
	private Border tickerBorder = new Border(
			new BorderStroke(separatorColor, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT));
	
	private TickerScreenLong longScreen;
	private TickerScreenShort shortScreen;
	
	
	public TickerScreenHolder(){
	
		longScreen = new TickerScreenLong();
		shortScreen = new TickerScreenShort(longScreen);
		
		HBox tickerScreens = new HBox();
		tickerScreens.setPadding(new Insets(2.5, 2.5, 2.5, 2.5));
		tickerScreens.getChildren().add(longScreen);
		tickerScreens.setSpacing(200);
		tickerScreens.getChildren().add(shortScreen);
		tickerScreens.setBorder(tickerBorder);
		
		Separator screenSeparator = new Separator();
		screenSeparator.setStyle(cssLayout);
		
		getChildren().addAll(new Node[]{tickerScreens, screenSeparator});
	}

	public TickerScreenLong getLongScreen() {
		return longScreen;
	}


	public TickerScreenShort getShortScreen() {
		return shortScreen;
	}	
	

}
