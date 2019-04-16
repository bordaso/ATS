package businessModels;

import java.util.ArrayList;
import java.util.List;

public class GlobalSetup {

	private String globalOrderAt = "";
	private String globalCloseLongOrdersAt = "";
	
	private List<Ticker> tickerList = new ArrayList<>();

	public GlobalSetup() {
		super();
	}

	public GlobalSetup(String globalOrderAt, String globalCloseLongOrdersAt, List<Ticker> tickerList) {
		super();
		this.globalOrderAt = globalOrderAt;
		this.globalCloseLongOrdersAt = globalCloseLongOrdersAt;
		this.tickerList = tickerList;
	}

	public String getGlobalOrderAt() {
		return globalOrderAt;
	}

	public void setGlobalOrderAt(String globalOrderAt) {
		this.globalOrderAt = globalOrderAt;
	}

	public String getGlobalCloseLongOrdersAt() {
		return globalCloseLongOrdersAt;
	}

	public void setGlobalCloseLongOrdersAt(String globalCloseLongOrdersAt) {
		this.globalCloseLongOrdersAt = globalCloseLongOrdersAt;
	}

	public List<Ticker> getTickerList() {
		return tickerList;
	}

	public void setTickerList(List<Ticker> tickerList) {
		this.tickerList = tickerList;
	}	
	
	
}
