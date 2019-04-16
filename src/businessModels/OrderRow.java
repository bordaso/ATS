package businessModels;

public class OrderRow {

	private Ticker relatedTicker = new Ticker();

	private String quantitiyInNumber = "";
	private String quantitiyInPercent = "";
	private String amountToGetTheDesiredPrice = "";
	private String orderInputInfo = "";

	private Boolean orderSelected = false;

	public OrderRow() {
		super();
	}

	public OrderRow(Ticker relatedTicker, String quantitiyInNumber, String quantitiyInPercent,
			String amountToGetTheDesiredPrice, String orderInputInfo, Boolean orderSelected) {
		super();
		this.relatedTicker = relatedTicker;
		this.quantitiyInNumber = quantitiyInNumber;
		this.quantitiyInPercent = quantitiyInPercent;
		this.amountToGetTheDesiredPrice = amountToGetTheDesiredPrice;
		this.orderInputInfo = orderInputInfo;
		this.orderSelected = orderSelected;
	}

	public Ticker getRelatedTicker() {
		return relatedTicker;
	}

	public void setRelatedTicker(Ticker relatedTicker) {
		this.relatedTicker = relatedTicker;
	}

	public String getQuantitiyInNumber() {
		return quantitiyInNumber;
	}

	public void setQuantitiyInNumber(String quantitiyInNumber) {
		this.quantitiyInNumber = quantitiyInNumber;
	}

	public String getQuantitiyInPercent() {
		return quantitiyInPercent;
	}

	public void setQuantitiyInPercent(String quantitiyInPercent) {
		this.quantitiyInPercent = quantitiyInPercent;
	}

	public String getAmountToGetTheDesiredPrice() {
		return amountToGetTheDesiredPrice;
	}

	public void setAmountToGetTheDesiredPrice(String amountToGetTheDesiredPrice) {
		this.amountToGetTheDesiredPrice = amountToGetTheDesiredPrice;
	}

	public String getOrderInputInfo() {
		return orderInputInfo;
	}

	public void setOrderInputInfo(String orderInputInfo) {
		this.orderInputInfo = orderInputInfo;
	}

	public Boolean getOrderSelected() {
		return orderSelected;
	}

	public void setOrderSelected(Boolean orderSelected) {
		this.orderSelected = orderSelected;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((relatedTicker == null) ? 0 : relatedTicker.hashCode());
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
		OrderRow other = (OrderRow) obj;
		if (relatedTicker == null) {
			if (other.relatedTicker != null)
				return false;
		} else if (!relatedTicker.equals(other.relatedTicker))
			return false;
		return true;
	}

}
