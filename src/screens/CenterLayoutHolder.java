package screens;

import javafx.geometry.Insets;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;

public class CenterLayoutHolder extends ScrollPane {

public CenterLayoutHolder(VBox contentFinal){
	
	setContent(contentFinal);
	setFitToWidth(true);
	setPadding(new Insets(10, 20, 10, 20));
	
}

}
