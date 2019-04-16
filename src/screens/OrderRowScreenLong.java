package screens;

import businessModels.Order;
import businessModels.OrderRow;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;

public class OrderRowScreenLong extends HBox implements Order {

	private OrderRow orderBusinessObject = new OrderRow();

	private TextField tickerName = new TextField();
	
	private TextField quantitiyInNumber = new TextField();
	private TextField quantitiyDisplayed = new TextField();
	private TextField amountToGetTheDesiredPrice = new TextField();
	private TextField orderInputInfo = new TextField();

	private CheckBox orderSelected = new CheckBox();

	private Color separatorColor = Color.color(0.78, 0.78, 0.78, 1);
	private Background selectedFiller = new Background(
			new BackgroundFill(Color.YELLOW, CornerRadii.EMPTY, Insets.EMPTY));
	private Background selectedDefault = new Background(
			new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY));
	private Border selectedDefaultBorder = new Border(
			new BorderStroke(separatorColor, BorderStrokeStyle.SOLID, new CornerRadii(2.5), BorderWidths.DEFAULT));
	// CornerRadii.EMPTY

	public OrderRowScreenLong(TextField tickerName) {
		super();

		this.tickerName=tickerName;
		
		quantitiyInNumber.setPromptText("Qty");
		quantitiyDisplayed.setPromptText("QtyPub");
		amountToGetTheDesiredPrice.setPromptText(" +/- ");
		orderInputInfo.setPromptText("Order info");

		quantitiyInNumber.setMaxWidth(50);
		quantitiyDisplayed.setMaxWidth(56);
		amountToGetTheDesiredPrice.setMaxWidth(50);
		orderInputInfo.setPrefWidth(294);

		quantitiyInNumber.setBackground(selectedDefault);
		quantitiyInNumber.setBorder(selectedDefaultBorder);
		quantitiyDisplayed.setBackground(selectedDefault);
		quantitiyDisplayed.setBorder(selectedDefaultBorder);
		amountToGetTheDesiredPrice.setBackground(selectedDefault);
		amountToGetTheDesiredPrice.setBorder(selectedDefaultBorder);
		orderInputInfo.setBackground(selectedDefault);
		orderInputInfo.setBorder(selectedDefaultBorder);

		setupActionCheckbox();
		setupQuantities();

		getChildren().add(quantitiyInNumber);
		getChildren().add(quantitiyDisplayed);
		getChildren().add(amountToGetTheDesiredPrice);
		getChildren().add(orderInputInfo);
		getChildren().add(orderSelected);
		setSpacing(5);

	}

	private OrderRowScreenLong getThis() {

		return this;
	}

	private void setupActionCheckbox() {
		orderSelected.selectedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				orderBusinessObject.setOrderSelected(newValue);
				OrderRowScreenLong mainObj = getThis();
				if (mainObj.getBackground() != null) {
					mainObj.setBackground(null);
					mainObj.getQuantitiyInNumber().setBackground(selectedDefault);
					mainObj.getQuantitiyInNumber().setBorder(selectedDefaultBorder);
					mainObj.getQuantitiyDisplayed().setBackground(selectedDefault);
					mainObj.getQuantitiyDisplayed().setBorder(selectedDefaultBorder);
					mainObj.getAmountToGetTheDesiredPrice().setBackground(selectedDefault);
					mainObj.getAmountToGetTheDesiredPrice().setBorder(selectedDefaultBorder);
					mainObj.getOrderInputInfo().setBackground(selectedDefault);
					mainObj.getOrderInputInfo().setBorder(selectedDefaultBorder);
					// mainObj.getOrderSelected().setBackground(null);
				} else {
					mainObj.setBackground(selectedFiller);
					mainObj.getQuantitiyInNumber().setBackground(selectedFiller);
					mainObj.getQuantitiyDisplayed().setBackground(selectedFiller);
					mainObj.getAmountToGetTheDesiredPrice().setBackground(selectedFiller);
					mainObj.getOrderInputInfo().setBackground(selectedFiller);
					// mainObj.getOrderSelected().setBackground(selectedFiller);
				}
			}
		});
	}

	private void setupQuantities() {

		quantitiyInNumber.textProperty().addListener((observable, oldValue, newValue) -> {

			if (!quantitiyInNumber.getText().matches("\\d*")) {
				quantitiyInNumber.setText(newValue.replaceAll("[^\\d]", ""));
			}

			if (quantitiyInNumber.getText().isEmpty()) {
				quantitiyInNumber.setText("");
				return;
			}

			if (quantitiyDisplayed.getText().isEmpty()) {
				quantitiyDisplayed.setText("");
				return;
			}

			if (Integer.valueOf(newValue) < Integer.valueOf(quantitiyDisplayed.getText())) {
				quantitiyDisplayed.setText("");
			}

		});

		quantitiyDisplayed.textProperty().addListener((observable, oldValue, newValue) -> {

			if (!quantitiyDisplayed.getText().matches("\\d*")) {
				quantitiyDisplayed.setText(newValue.replaceAll("[^\\d]", ""));
			}

			if (quantitiyInNumber.getText().isEmpty()) {
				quantitiyInNumber.setText("");
				return;
			}

			if (quantitiyDisplayed.getText().isEmpty()) {
				quantitiyDisplayed.setText("");
				return;
			}

			if (Integer.valueOf(newValue) > Integer.valueOf(quantitiyInNumber.getText())) {
				quantitiyDisplayed.setText("");
			}

		});

	}
	
	@Override
	public TextField[] fieldList() {
		return new TextField[]{tickerName, quantitiyInNumber, quantitiyDisplayed, amountToGetTheDesiredPrice, orderInputInfo};
	}
	
	

	public TextField getTickerName() {
		return tickerName;
	}

	public void setTickerName(TextField tickerName) {
		this.tickerName = tickerName;
	}

	public OrderRow getOrderBusinessObject() {
		return orderBusinessObject;
	}

	public void setOrderBusinessObject(OrderRow orderBusinessObject) {
		this.orderBusinessObject = orderBusinessObject;
	}

	public TextField getQuantitiyInNumber() {
		return quantitiyInNumber;
	}

	public void setQuantitiyInNumber(TextField quantitiyInNumber) {
		this.quantitiyInNumber = quantitiyInNumber;
	}

	public TextField getQuantitiyDisplayed() {
		return quantitiyDisplayed;
	}

	public void setQuantitiyDisplayed(TextField quantitiyDisplayed) {
		this.quantitiyDisplayed = quantitiyDisplayed;
	}

	public TextField getAmountToGetTheDesiredPrice() {
		return amountToGetTheDesiredPrice;
	}

	public void setAmountToGetTheDesiredPrice(TextField amountToGetTheDesiredPrice) {
		this.amountToGetTheDesiredPrice = amountToGetTheDesiredPrice;
	}

	public TextField getOrderInputInfo() {
		return orderInputInfo;
	}

	public void setOrderInputInfo(TextField orderInputInfo) {
		this.orderInputInfo = orderInputInfo;
	}

	public CheckBox getOrderSelected() {
		return orderSelected;
	}

	public void setOrderSelected(CheckBox orderSelected) {
		this.orderSelected = orderSelected;
	}

}
