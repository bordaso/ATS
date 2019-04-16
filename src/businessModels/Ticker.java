package businessModels;

import java.util.ArrayList;
import java.util.List;

public class Ticker {

	private String tickerName = "";
	private String tickerTOSFeed = "";
	
	private String tickerInfo = "";
	
	private String numberOfOpenedPositions ="";
	private String orderAt = "";
	private String closeLongOrdersAt = "";
	private Boolean includeGlobal = false;
	private Boolean setSelectedOrdersToQueue = false;
	private Boolean cancelSelectedOrdersFromQueue = false;	
	
	private List<OrderRow> orderListLong = new ArrayList<>();
	private List<OrderRow> orderListShort = new ArrayList<>();
	
	public Ticker() {
		super();
	}

	public Ticker(String tickerName, String tickerTOSFeed, String tickerInfo, String numberOfOpenedPositions,
			String orderAt, String closeLongOrdersAt, Boolean includeGlobal, Boolean setSelectedOrdersToQueue,
			Boolean cancelSelectedOrdersFromQueue, List<OrderRow> orderListLong, List<OrderRow> orderListShort) {
		super();
		this.tickerName = tickerName;
		this.tickerTOSFeed = tickerTOSFeed;
		this.tickerInfo = tickerInfo;
		this.numberOfOpenedPositions = numberOfOpenedPositions;
		this.orderAt = orderAt;
		this.closeLongOrdersAt = closeLongOrdersAt;
		this.includeGlobal = includeGlobal;
		this.setSelectedOrdersToQueue = setSelectedOrdersToQueue;
		this.cancelSelectedOrdersFromQueue = cancelSelectedOrdersFromQueue;
		this.orderListLong = orderListLong;
		this.orderListShort = orderListShort;
	}

	public String getTickerName() {
		return tickerName;
	}

	public void setTickerName(String tickerName) {
		this.tickerName = tickerName;
	}

	public String getTickerTOSFeed() {
		return tickerTOSFeed;
	}

	public void setTickerTOSFeed(String tickerTOSFeed) {
		this.tickerTOSFeed = tickerTOSFeed;
	}

	public String getTickerInfo() {
		return tickerInfo;
	}

	public void setTickerInfo(String tickerInfo) {
		this.tickerInfo = tickerInfo;
	}

	public String getNumberOfOpenedPositions() {
		return numberOfOpenedPositions;
	}

	public void setNumberOfOpenedPositions(String numberOfOpenedPositions) {
		this.numberOfOpenedPositions = numberOfOpenedPositions;
	}

	public String getOrderAt() {
		return orderAt;
	}

	public void setOrderAt(String orderAt) {
		this.orderAt = orderAt;
	}

	public String getCloseLongOrdersAt() {
		return closeLongOrdersAt;
	}

	public void setCloseLongOrdersAt(String closeLongOrdersAt) {
		this.closeLongOrdersAt = closeLongOrdersAt;
	}

	public Boolean getIncludeGlobal() {
		return includeGlobal;
	}

	public void setIncludeGlobal(Boolean includeGlobal) {
		this.includeGlobal = includeGlobal;
	}

	public Boolean getSetOrdersToQueue() {
		return setSelectedOrdersToQueue;
	}

	public void setSetOrdersToQueue(Boolean setOrdersToQueue) {
		this.setSelectedOrdersToQueue = setOrdersToQueue;
	}

	public Boolean getCancelOrdersFromQueue() {
		return cancelSelectedOrdersFromQueue;
	}

	public void setCancelOrdersFromQueue(Boolean cancelOrdersFromQueue) {
		this.cancelSelectedOrdersFromQueue = cancelOrdersFromQueue;
	}

	public List<OrderRow> getOrderListLong() {
		return orderListLong;
	}

	public void setOrderListLong(List<OrderRow> orderListLong) {
		this.orderListLong = orderListLong;
	}

	public List<OrderRow> getOrderListShort() {
		return orderListShort;
	}

	public void setOrderListShort(List<OrderRow> orderListShort) {
		this.orderListShort = orderListShort;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((tickerName == null) ? 0 : tickerName.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Ticker other = (Ticker) obj;
		if (tickerName == null) {
			if (other.tickerName != null)
				return false;
		} else if (!tickerName.equals(other.tickerName))
			return false;
		return true;
	}	
	
	
}
