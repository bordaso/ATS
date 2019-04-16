package businessModels;

import java.util.ArrayList;
import java.util.List;

import javafx.scene.Node;
import screens.TickerScreenShort;

public class SharedObject {

	private static SharedObject INSTANCE = new SharedObject();
	private static List<String> sharedStringList = new ArrayList<>();
	private static List<Node> sharedNodeList = new ArrayList<>();

	private SharedObject() {
	}
	
	
	public static SharedObject getINSTANCE() {
		return INSTANCE;
	}


	public static synchronized List<String> getSharedStringList() {
		return sharedStringList;
	}


	public static synchronized List<Node> getSharedNodeList() {
		return sharedNodeList;
	}
	
	public static synchronized String getTicker(TickerScreenShort inputObj) {
		return null;
	}
	
	public static synchronized String getTOSFeed(TickerScreenShort inputObj) {
		return null;
	}

}
