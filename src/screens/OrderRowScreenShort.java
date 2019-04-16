package screens;

import java.text.DecimalFormat;

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

public class OrderRowScreenShort extends HBox implements Order {

	private OrderRow orderBusinessObject = new OrderRow();
	
	private TextField tickerName = new TextField();

	private TextField quantitiyInPercent = new TextField();
	private TextField quantitiyInPercentActualAmount = new TextField();
	private TextField quantitiyDisplayed = new TextField();
	private TextField amountToGetTheDesiredPrice = new TextField();
	private TextField orderInputInfo = new TextField();
	
	private TextField numberOfOpenedPositions;

	private CheckBox orderSelected = new CheckBox();

	private Color separatorColor = Color.color(0.78, 0.78, 0.78, 1);
	private Background selectedFiller = new Background(
			new BackgroundFill(Color.YELLOW, CornerRadii.EMPTY, Insets.EMPTY));
	private Background selectedDefault = new Background(
			new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY));
	private Border selectedDefaultBorder = new Border(
			new BorderStroke(separatorColor, BorderStrokeStyle.SOLID, new CornerRadii(2.5), BorderWidths.DEFAULT));

	public OrderRowScreenShort(TextField numberOfOpenedPositions, TextField tickerName) {
		super();
		
		this.numberOfOpenedPositions=numberOfOpenedPositions;
		this.tickerName = tickerName;

		quantitiyInPercent.setPromptText("Qty%");
		quantitiyInPercentActualAmount.setPromptText("Qty");
		quantitiyDisplayed.setPromptText("QtyPub");
		amountToGetTheDesiredPrice.setPromptText(" +/- ");
		orderInputInfo.setPromptText("Order info");

		quantitiyInPercentActualAmount.setDisable(true);

		quantitiyInPercent.setMaxWidth(50);
		quantitiyInPercentActualAmount.setMaxWidth(50);
		quantitiyDisplayed.setMaxWidth(56);
		amountToGetTheDesiredPrice.setMaxWidth(50);
		orderInputInfo.setPrefWidth(294);

		quantitiyInPercent.setBackground(selectedDefault);
		quantitiyInPercent.setBorder(selectedDefaultBorder);
		quantitiyInPercentActualAmount.setBackground(selectedDefault);
		quantitiyInPercentActualAmount.setBorder(selectedDefaultBorder);
		quantitiyDisplayed.setBackground(selectedDefault);
		quantitiyDisplayed.setBorder(selectedDefaultBorder);
		amountToGetTheDesiredPrice.setBackground(selectedDefault);
		amountToGetTheDesiredPrice.setBorder(selectedDefaultBorder);
		orderInputInfo.setBackground(selectedDefault);
		orderInputInfo.setBorder(selectedDefaultBorder);

		setupActionCheckbox();
		setupQuantities();
		setupQuantitiyInPercentActualAmount();

		getChildren().add(quantitiyInPercent);
		getChildren().add(quantitiyInPercentActualAmount);
		getChildren().add(quantitiyDisplayed);
		getChildren().add(amountToGetTheDesiredPrice);
		getChildren().add(orderInputInfo);
		getChildren().add(orderSelected);
		setSpacing(5);
	}

	private String callme() {
		return this.toString();
	}

	private OrderRowScreenShort getThis() {

		return this;
	}

	private void setupActionCheckbox() {
		orderSelected.selectedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				System.out.println(
						"##OLD ROW VALUE " + orderBusinessObject.getOrderSelected() + " FOR ROW ID: " + callme());
				orderBusinessObject.setOrderSelected(newValue);
				System.out.println(
						"NEW ROW VALUE " + orderBusinessObject.getOrderSelected() + " FOR ROW ID: " + callme());

				orderBusinessObject.setOrderSelected(newValue);
				OrderRowScreenShort mainObj = getThis();
				if (mainObj.getBackground() != null) {
					mainObj.setBackground(null);
					mainObj.getQuantitiyInPercent().setBackground(selectedDefault);
					mainObj.getQuantitiyInPercent().setBorder(selectedDefaultBorder);
					mainObj.getQuantitiyInPercentActualAmount().setBackground(selectedDefault);
					mainObj.getQuantitiyInPercentActualAmount().setBorder(selectedDefaultBorder);
					mainObj.getQuantitiyDisplayed().setBackground(selectedDefault);
					mainObj.getQuantitiyDisplayed().setBorder(selectedDefaultBorder);
					mainObj.getAmountToGetTheDesiredPrice().setBackground(selectedDefault);
					mainObj.getAmountToGetTheDesiredPrice().setBorder(selectedDefaultBorder);
					mainObj.getOrderInputInfo().setBackground(selectedDefault);
					mainObj.getOrderInputInfo().setBorder(selectedDefaultBorder);
					// mainObj.getOrderSelected().setBackground(null);
				} else {
					mainObj.setBackground(selectedFiller);
					mainObj.getQuantitiyInPercent().setBackground(selectedFiller);
					mainObj.getQuantitiyInPercentActualAmount().setBackground(selectedFiller);
					mainObj.getQuantitiyDisplayed().setBackground(selectedFiller);
					mainObj.getAmountToGetTheDesiredPrice().setBackground(selectedFiller);
					mainObj.getOrderInputInfo().setBackground(selectedFiller);
					// mainObj.getOrderSelected().setBackground(selectedFiller);
				}

			}
		});

	}

	private void setupQuantities() {
		
		//ALREADY SET IN THE API CALLER callSendOrder METHOD

//		quantitiyDisplayed.textProperty().addListener((observable, oldValue, newValue) -> {
//			
//			if (!quantitiyDisplayed.getText().matches("\\d*")) {
//				quantitiyDisplayed.setText(newValue.replaceAll("[^\\d]", ""));
//				return;
//			}		
//
//			if (Integer.valueOf(newValue) > Integer.valueOf(quantitiyInPercentActualAmount.getText())) {
//				quantitiyDisplayed.setText("");
//				return;
//			}
//
//		});

		

	}

	private void setupQuantitiyInPercentActualAmount() {
		DecimalFormat df = new DecimalFormat("#");
		
		numberOfOpenedPositions.textProperty().addListener((observable, oldValue, newValue) -> {
			System.out.println("xxxxxxxxx "+numberOfOpenedPositions.getText()+" xxxxxxxxx");
			if (numberOfOpenedPositions.getText()==null) {
//				numberOfOpenedPositions.setText("0");
//				quantitiyInPercentActualAmount.setText("");
//				quantitiyInPercent.setText("");
				System.out.println("xxxxxxxxx "+numberOfOpenedPositions+" xxxxxxxxx");
				quantitiyInPercentActualAmount.clear();
				quantitiyInPercent.clear();
				return;
			}

			Double amountOfSharesSold = Double.valueOf(
					df.format(Double.valueOf(numberOfOpenedPositions.getText()) * (Double.valueOf(getQuantitiyInPercent().getText()) / 100)));
			Double amountOfSharesSoldDisplayed = amountOfSharesSold < 1 ? 1 : amountOfSharesSold;

			int indexOfDot = amountOfSharesSoldDisplayed.toString().indexOf(".");

			quantitiyInPercentActualAmount.setText(amountOfSharesSoldDisplayed.toString().substring(0, indexOfDot));
		});	

		quantitiyInPercent.textProperty().addListener((observable, oldValue, newValue) -> {

			if (!getQuantitiyInPercent().getText().matches("\\d*")) {
				getQuantitiyInPercent().setText(newValue.replaceAll("[^\\d]", ""));
			}

			if (getQuantitiyInPercent().getText().isEmpty()) {
				quantitiyInPercentActualAmount.setText("");
				return;
			}

			Double amountOfSharesSold = Double.valueOf(
					df.format(Double.valueOf(numberOfOpenedPositions.getText()) * (Double.valueOf(getQuantitiyInPercent().getText()) / 100)));
			Double amountOfSharesSoldDisplayed = amountOfSharesSold < 1 ? 1 : amountOfSharesSold;

			int indexOfDot = amountOfSharesSoldDisplayed.toString().indexOf(".");

			quantitiyInPercentActualAmount.setText(amountOfSharesSoldDisplayed.toString().substring(0, indexOfDot));
		});
	}

	@Override
	public TextField[] fieldList() {
		return new TextField[]{tickerName, quantitiyInPercentActualAmount, quantitiyDisplayed, amountToGetTheDesiredPrice, orderInputInfo};
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

	public TextField getQuantitiyInPercent() {
		return quantitiyInPercent;
	}

	public void setQuantitiyInPercent(TextField quantitiyInPercent) {
		this.quantitiyInPercent = quantitiyInPercent;
	}

	public TextField getQuantitiyInPercentActualAmount() {
		return quantitiyInPercentActualAmount;
	}

	public void setQuantitiyInPercentActualAmount(TextField quantitiyInPercentActualAmount) {
		this.quantitiyInPercentActualAmount = quantitiyInPercentActualAmount;
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
