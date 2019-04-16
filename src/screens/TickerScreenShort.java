package screens;

import java.io.IOException;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import businessModels.SharedObject;
import businessModels.Ticker;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.util.converter.LocalTimeStringConverter;
import util.APICaller;
import util.Side;
import util.TimeCalculator;

public class TickerScreenShort extends BorderPane {
	
	private LocalTime nowTimeSendOrder;
	private LocalTime nowTimeCancelOrder;
	private LocalTime futureInputFromGlobal = null;
	private String cancelStringFromGlobal = null;

	private Ticker tickerBusinessObject = new Ticker();
	
	private TickerScreenLong buyScreenReference;

	private volatile boolean theThreadStopperShort = true;

	private volatile boolean theThreadStopperShortCancelled = true;

	private VBox topView;
	private HBox rowOne;
	private HBox rowTwo;
	private HBox rowThree;
	private HBox rowFour;
	private HBox rowFive;
	private Button addStockRowButton = new Button("Add new order row");;
	private Button deleteStockRowButton = new Button("Delete order row");;

	private TextField tickerName = new TextField();
	private TextField tickerTOSFeed = new TextField();
	private TextField tickerInfo = new TextField();
	private Button getNumberOfOpenedPositionsButton = new Button("Get positions");
	private TextField numberOfOpenedPositions = new TextField();
	private TextField orderAt = new TextField();
	private TextField closeLongOrdersAt = new TextField();

	private CheckBox includeGlobal = new CheckBox();
	private Label excludeFromGlobalCheckboxLabel = new Label("Include into Global Setup Closing:");
	private Button setSelectedOrdersToQueue = new Button("Set");
	private Button cancelSelectedOrdersFromQueue = new Button("Cancel");

	private OrderRowScreenShort orderRowScreenShort;
	private VBox ordersPanelContent;
	private List<OrderRowScreenShort> ordersPanelContentList;
	// VBox ordersPanelContent = new VBox(5);
	private ScrollPane ordersPanel = new ScrollPane(ordersPanelContent);

	private Color separatorColor = Color.color(0.78, 0.78, 0.78, 1);
	private Background selectedFiller = new Background(
			new BackgroundFill(Color.YELLOW, CornerRadii.EMPTY, Insets.EMPTY));
	private Background selectedDefault = new Background(
			new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY));
	private Border selectedDefaultBorder = new Border(
			new BorderStroke(separatorColor, BorderStrokeStyle.SOLID, new CornerRadii(2.5), BorderWidths.DEFAULT));
	
	private List<OrderRowScreenShort> selectedShortOrdersData;

	public TickerScreenShort(TickerScreenLong buyScreenReference) {
		
		
		this.buyScreenReference = buyScreenReference;

		Color separatorColor = Color.color(0.78, 0.78, 0.78, 1);
		// Color.BLACK;
		orderRowScreenShort=new OrderRowScreenShort(numberOfOpenedPositions, tickerName);
		ordersPanelContentList = new ArrayList<>();
		ordersPanelContentList.add(orderRowScreenShort);
		ordersPanelContent = new VBox(orderRowScreenShort);
		ordersPanelContent.setSpacing(5);
		// ordersPanelContent.setBorder(new Border(new
		// BorderStroke(separatorColor,
		// BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));

		ordersPanel.setFitToWidth(true);
		ordersPanel.setPrefHeight(150);
		ordersPanel.setPrefWidth(600);
		ordersPanel.setMaxHeight(150);

		tickerName.setPromptText("Ticker name");
		tickerTOSFeed.setPromptText("Ticker TOS feed");
		tickerInfo.setPromptText("Ticker info");
		numberOfOpenedPositions.setPromptText("Number of opened positions");
		orderAt.setPromptText("Order at");
		closeLongOrdersAt.setPromptText("Cancel Closing orders at");
		closeLongOrdersAt.setText("60");

		numberOfOpenedPositions.setBackground(selectedDefault);
		numberOfOpenedPositions.setBorder(selectedDefaultBorder);
		numberOfOpenedPositions.setDisable(true);

		setupActionButtons();
		setupActionCheckbox();
		setupActionTextField();

		// rowOne = new HBox(tickerName, tickerTOSFeed, tickerInfo,
		// excludeFromGlobalCheckboxLabel, excludeFromGlobal);
		rowOne = new HBox(numberOfOpenedPositions, excludeFromGlobalCheckboxLabel, includeGlobal);
		// rowTwo = new HBox(getNumberOfOpenedPositionsButton);
		rowTwo = new HBox(new Label(""));
		rowThree = new HBox(orderAt, closeLongOrdersAt);
		rowFour = new HBox(setSelectedOrdersToQueue, cancelSelectedOrdersFromQueue);
		rowFive = new HBox(addStockRowButton, deleteStockRowButton);

		rowOne.setSpacing(5);
		rowTwo.setSpacing(5);
		rowThree.setSpacing(5);
		rowFour.setSpacing(5);
		rowFive.setSpacing(5);

		topView = new VBox(rowOne, rowTwo, rowThree, rowFour, rowFive);
		topView.setSpacing(5);
		topView.setPadding(new Insets(0, 0, 18, 0));

		this.setTop(topView);
		// this.setCenter(ordersPanel);
		this.setCenter(ordersPanelContent);
	}

	private void setupActionButtons() {


		getAddStockRowButton().setOnAction(evt -> {
			OrderRowScreenShort orderRowShort = new OrderRowScreenShort(numberOfOpenedPositions, tickerName);
			ordersPanelContent.getChildren().add(orderRowShort);
			ordersPanelContentList.add(orderRowShort);
			System.out.println("row added to screen id: " + this + " row id: " + orderRowShort);
		});

		getDeleteStockRowButton().setOnAction(evt -> {
			int idx = ordersPanelContent.getChildren().size();

			if (idx == 0) {
				return;
			}

			ordersPanelContent.getChildren().remove(idx - 1);
			ordersPanelContentList.remove(idx - 1);
		});

		// setSelectedOrdersToQueue.setOnAction(evt -> {
		// ObservableList<Node> allOrders =
		// getOrdersPanelContent().getChildren();
		// ObservableList<OrderRowScreenShort> selectedOrders =
		// FXCollections.observableArrayList();
		// selectedOrders.clear();
		//
		// for (Node order : allOrders) {
		// if (((OrderRowScreenShort)
		// order).getOrderBusinessObject().getOrderSelected()) {
		// DecimalFormat df = new DecimalFormat("#");
		// OrderRowScreenShort shortOrder = (OrderRowScreenShort) order;
		// Double
		// amountOfSharesSold=Double.valueOf(df.format(Double.valueOf(this.getNumberOfOpenedPositions().getText())*(Double.valueOf(shortOrder.getQuantitiyInPercent().getText())/100)));
		// Double amountOfSharesSoldDisplayed=
		// amountOfSharesSold<1?1:amountOfSharesSold;
		//
		// SharedObject.getSharedStringList().clear();
		//
		// SharedObject.getSharedStringList().add(
		//
		// "Sell Order of: "+SharedObject.getTicker(this)+
		// " Number of open positions:
		// "+this.getNumberOfOpenedPositions().getText()+
		// " Percent of shares to sell at this order:
		// "+shortOrder.getQuantitiyInPercent().getText()+"%"+
		// " Actual amount of shares sold "+amountOfSharesSoldDisplayed+
		// " at the price of:
		// "+(Double.valueOf(SharedObject.getTOSFeed(this))-Double.valueOf(shortOrder.getAmountToGetTheDesiredPrice().getText()))+
		// " at the time of: "+this.getOrderAt()
		//
		// );
		// selectedOrders.add(shortOrder);
		// }
		// }
		// System.out.println(selectedOrders);
		//
		// });

		shortCancelButtonSetup(this);
		shortSETButtonSetup(buyScreenReference, this); 
		
	}

	private void setupActionCheckbox() {
		includeGlobal.selectedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				tickerBusinessObject.setIncludeGlobal(newValue);
				setSelectedOrdersToQueue.setDisable(newValue);
				cancelSelectedOrdersFromQueue.setDisable(newValue);
				orderAt.setDisable(newValue);
				closeLongOrdersAt.setDisable(newValue);

			}
		});
	}

	private void setupActionTextField() {
		orderAt.setOnMouseClicked(event -> {
			if (event.getClickCount() == 2) {
				LocalDateTime roundCeiling =  LocalDateTime.now().plusMinutes(1).withSecond(0);
				//truncatedTo(ChronoUnit.MINUTES).				
				orderAt.setText(roundCeiling.toLocalTime().toString().substring(0, 8));
			}
		});

		orderAt.setOnKeyPressed(event -> {
			if (event.getCode() == KeyCode.UP) {
				orderAt.setText(TimeCalculator.increaseTime(orderAt));
			} else if (event.getCode() == KeyCode.DOWN) {
				orderAt.setText(TimeCalculator.decreaseTime(orderAt));
			} else if (event.getCode() == KeyCode.RIGHT) {
				orderAt.setText(TimeCalculator.increaseTime(orderAt, true));

			} else if (event.getCode() == KeyCode.LEFT) {
				orderAt.setText(TimeCalculator.decreaseTime(orderAt, true));

			} else if (event.getCode() == KeyCode.ENTER) {
				setSelectedOrdersToQueue.fire();
			}
		});

		numberOfOpenedPositions.textProperty().addListener((observable, oldValue, newValue) -> {

			if (!newValue.isEmpty()) {
				numberOfOpenedPositions.setBackground(selectedFiller);
			} else {
				numberOfOpenedPositions.setBackground(selectedDefault);
				numberOfOpenedPositions.setBorder(selectedDefaultBorder);
			}
		});

	}
	
	
	public void triggerInfo(String text) {

		Alert alertInfo = new Alert(AlertType.INFORMATION, text);
		alertInfo.show();

	}

	public void triggerError(String text) {

		Alert alertError = new Alert(AlertType.ERROR, text);
		alertError.show();

	}
	
	
	private void shortCancelButtonSetup(TickerScreenShort sellScreen) {

		sellScreen.getCancelSelectedOrdersFromQueue().setOnAction(evt -> {
			sellScreen.setTheThreadStopperShort(false);
			sellScreen.getSetSelectedOrdersToQueue().setDisable(false);
			sellScreen.getOrderAt().setDisable(false);
			sellScreen.getOrderAt().clear();
			
			sellScreen.setTheThreadStopperShortCancelled(false);
			sellScreen.getCloseLongOrdersAt().setDisable(false);
			sellScreen.getCloseLongOrdersAt().clear();
			sellScreen.getCloseLongOrdersAt().setText("60");
		});

	}

	private void shortSETButtonSetup(TickerScreenLong buyScreen, TickerScreenShort sellScreen) {

		selectedShortOrdersData = new ArrayList<>();

		sellScreen.getSetSelectedOrdersToQueue().setOnAction(evt -> {

			sellScreen.setTheThreadStopperShort(true);
			sellScreen.getSetSelectedOrdersToQueue().setDisable(true);
			sellScreen.getOrderAt().setDisable(true);

			selectedShortOrdersData.clear();

			
			for(OrderRowScreenShort row : getOrdersPanelContentList()){
				if(row.getOrderSelected().isSelected()){
					row.setTickerName(buyScreenReference.getTickerName());
					selectedShortOrdersData.add(row);
				}
				
				
			}


			String pattern = "HH:mm:ss";
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);

			LocalTime futureInput = futureInputFromGlobal==null?new LocalTimeStringConverter(formatter, null)
					.fromString(sellScreen.getOrderAt().getText()):futureInputFromGlobal;

			Thread shortSetThread = new Thread(() -> {

				while (sellScreen.isTheThreadStopperShort()) {
					try {

						//now = LocalTime.now();
						nowTimeSendOrder = new LocalTimeStringConverter(formatter, null)
								.fromString(buyScreen.getTickerTOSTime().getText());
						Thread.sleep(1);

					} catch (InterruptedException ex) {
						ex.printStackTrace();
					}

					if ((  futureInput.equals(nowTimeSendOrder.withSecond(00)) || futureInput.isBefore(nowTimeSendOrder) ) && sellScreen.isTheThreadStopperShort()) {

						Platform.runLater(new Runnable() {

							public void run() {

								if ((  futureInput.equals(nowTimeSendOrder.withSecond(00)) || futureInput.isBefore(nowTimeSendOrder) ) && sellScreen.isTheThreadStopperShort()) {
									
									try {
										
										System.out.println("--------------------------------------------------------------------");
										System.out.println("-----------------------------SELL ORDER-----------------------------");
										System.out.println("futureInput TIME "+futureInput.toString()+"  IS BEFORE ");
										System.out.println(" MARKET TIME "+nowTimeSendOrder.toString());
										System.out.println(" ((  futureInput.equals(nowTime.withSecond(00)) "+ futureInput.equals(nowTimeSendOrder.withSecond(00)));
										System.out.println("--------------------------------------------------------------------");
										
										
										APICaller.callSendOrder(selectedShortOrdersData, buyScreen.getTickerTOSFeed().getText(), Side.SELL);
									} catch (IOException e) {
										e.printStackTrace();
									}									
									
									sellScreen.setTheThreadStopperShort(false);
									sellScreen.getSetSelectedOrdersToQueue().setDisable(false);
									sellScreen.getOrderAt().setDisable(false);
									sellScreen.getOrderAt().clear();
								}
							}
						});
						System.gc();
					}
				}
			});

			shortSetThread.start();
			
			if (!sellScreen.getCloseLongOrdersAt().getText().isEmpty()) {

				String cancelString = sellScreen.getCloseLongOrdersAt().getText();
				boolean isNumeric = cancelString.chars().allMatch(Character::isDigit);

				if (!isNumeric) {
					return;
				}

				sellScreen.getCloseLongOrdersAt().setDisable(true);
				sellScreen.setTheThreadStopperShortCancelled(true);

				LocalTime cancelTime = cancelStringFromGlobal==null?futureInput.plusSeconds(Long.valueOf(cancelString)):futureInput.plusSeconds(Long.valueOf(cancelStringFromGlobal));

				Thread shortCancelThread = new Thread(() -> {

					while (sellScreen.isTheThreadStopperShortCancelled()) {

						try {

							
							nowTimeCancelOrder = LocalTime.now();
							Thread.sleep(1000);

						} catch (InterruptedException ex) {
							ex.printStackTrace();
						}

						if (futureInput.isBefore(nowTimeCancelOrder)) {

							Platform.runLater(new Runnable() {

								public void run() {

									if (!sellScreen.getCloseLongOrdersAt().getText().isEmpty()) {
										Integer secondValue = Integer
												.valueOf(sellScreen.getCloseLongOrdersAt().getText());
										secondValue--;
										sellScreen.getCloseLongOrdersAt().setText(secondValue.toString());
									}

									if (cancelTime.isBefore(nowTimeCancelOrder) && sellScreen.isTheThreadStopperShortCancelled()) {
										
										try {
											APICaller.callCancelOrder(selectedShortOrdersData, Side.SELL);
											setFutureInputFromGlobal(null);
											setCancelStringFromGlobal(null);
										} catch (IOException e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										}finally {
											sellScreen.getCloseLongOrdersAt().clear();
											sellScreen.getCloseLongOrdersAt().setText("61");
											}

										sellScreen.setTheThreadStopperShortCancelled(false);
										sellScreen.getCloseLongOrdersAt().setDisable(false);
										//sellScreen.getCloseLongOrdersAt().clear();
									}

									// }
									

								}
							});
							System.gc();
						}
					}
					
				});

				shortCancelThread.start();
			}

		});

	}
	

	
	
	
	public TickerScreenLong getBuyScreenReference() {
		return buyScreenReference;
	}

	public Ticker getTickerBusinessObject() {
		return tickerBusinessObject;
	}

	public void setTickerBusinessObject(Ticker tickerBusinessObject) {
		this.tickerBusinessObject = tickerBusinessObject;
	}

	public boolean isTheThreadStopperShort() {
		return theThreadStopperShort;
	}

	public void setTheThreadStopperShort(boolean theThreadStopperShort) {
		this.theThreadStopperShort = theThreadStopperShort;
	}

	public boolean isTheThreadStopperShortCancelled() {
		return theThreadStopperShortCancelled;
	}

	public void setTheThreadStopperShortCancelled(boolean theThreadStopperShortCancelled) {
		this.theThreadStopperShortCancelled = theThreadStopperShortCancelled;
	}

	public VBox getTopView() {
		return topView;
	}

	public void setTopView(VBox topView) {
		this.topView = topView;
	}

	public HBox getRowOne() {
		return rowOne;
	}

	public void setRowOne(HBox rowOne) {
		this.rowOne = rowOne;
	}

	public HBox getRowTwo() {
		return rowTwo;
	}

	public void setRowTwo(HBox rowTwo) {
		this.rowTwo = rowTwo;
	}

	public HBox getRowThree() {
		return rowThree;
	}

	public void setRowThree(HBox rowThree) {
		this.rowThree = rowThree;
	}

	public HBox getRowFour() {
		return rowFour;
	}

	public void setRowFour(HBox rowFour) {
		this.rowFour = rowFour;
	}

	public HBox getRowFive() {
		return rowFive;
	}

	public void setRowFive(HBox rowFive) {
		this.rowFive = rowFive;
	}

	public Button getAddStockRowButton() {
		return addStockRowButton;
	}

	public void setAddStockRowButton(Button addStockRowButton) {
		this.addStockRowButton = addStockRowButton;
	}

	public Button getDeleteStockRowButton() {
		return deleteStockRowButton;
	}

	public void setDeleteStockRowButton(Button deleteStockRowButton) {
		this.deleteStockRowButton = deleteStockRowButton;
	}

	public TextField getTickerName() {
		return tickerName;
	}

	public void setTickerName(TextField tickerName) {
		this.tickerName = tickerName;
	}

	public TextField getTickerTOSFeed() {
		return tickerTOSFeed;
	}

	public void setTickerTOSFeed(TextField tickerTOSFeed) {
		this.tickerTOSFeed = tickerTOSFeed;
	}

	public TextField getTickerInfo() {
		return tickerInfo;
	}

	public void setTickerInfo(TextField tickerInfo) {
		this.tickerInfo = tickerInfo;
	}

	public Button getGetNumberOfOpenedPositionsButton() {
		return getNumberOfOpenedPositionsButton;
	}

	public void setGetNumberOfOpenedPositionsButton(Button getNumberOfOpenedPositionsButton) {
		this.getNumberOfOpenedPositionsButton = getNumberOfOpenedPositionsButton;
	}

	public TextField getNumberOfOpenedPositions() {
		return numberOfOpenedPositions;
	}

	public void setNumberOfOpenedPositions(TextField numberOfOpenedPositions) {
		this.numberOfOpenedPositions = numberOfOpenedPositions;
	}

	public TextField getOrderAt() {
		return orderAt;
	}

	public void setOrderAt(TextField orderAt) {
		this.orderAt = orderAt;
	}

	public TextField getCloseLongOrdersAt() {
		return closeLongOrdersAt;
	}

	public void setCloseLongOrdersAt(TextField closeLongOrdersAt) {
		this.closeLongOrdersAt = closeLongOrdersAt;
	}

	public CheckBox getIncludeGlobal() {
		return includeGlobal;
	}

	public void setIncludeGlobal(CheckBox includeGlobal) {
		this.includeGlobal = includeGlobal;
	}

	public Label getExcludeFromGlobalCheckboxLabel() {
		return excludeFromGlobalCheckboxLabel;
	}

	public void setExcludeFromGlobalCheckboxLabel(Label excludeFromGlobalCheckboxLabel) {
		this.excludeFromGlobalCheckboxLabel = excludeFromGlobalCheckboxLabel;
	}

	public Button getSetSelectedOrdersToQueue() {
		return setSelectedOrdersToQueue;
	}

	public void setSetSelectedOrdersToQueue(Button setSelectedOrdersToQueue) {
		this.setSelectedOrdersToQueue = setSelectedOrdersToQueue;
	}

	public Button getCancelSelectedOrdersFromQueue() {
		return cancelSelectedOrdersFromQueue;
	}

	public void setCancelSelectedOrdersFromQueue(Button cancelSelectedOrdersFromQueue) {
		this.cancelSelectedOrdersFromQueue = cancelSelectedOrdersFromQueue;
	}

	public OrderRowScreenShort getOrderRowScreenShort() {
		return orderRowScreenShort;
	}

	public VBox getOrdersPanelContent() {
		return ordersPanelContent;
	}

	public void setOrdersPanelContent(VBox ordersPanelContent) {
		this.ordersPanelContent = ordersPanelContent;
	}
	
	public List<OrderRowScreenShort> getOrdersPanelContentList() {
		return ordersPanelContentList;
	}

	public void setOrdersPanelContentList(List<OrderRowScreenShort> ordersPanelContentList) {
		this.ordersPanelContentList = ordersPanelContentList;
	}

	public ScrollPane getOrdersPanel() {
		return ordersPanel;
	}

	public void setOrdersPanel(ScrollPane ordersPanel) {
		this.ordersPanel = ordersPanel;
	}

	public LocalTime getFutureInputFromGlobal() {
		return futureInputFromGlobal;
	}

	public void setFutureInputFromGlobal(LocalTime futureInputFromGlobal) {
		this.futureInputFromGlobal = futureInputFromGlobal;
	}
	
	public String getCancelStringFromGlobal() {
		return cancelStringFromGlobal;
	}

	public void setCancelStringFromGlobal(String cancelStringFromGlobal) {
		this.cancelStringFromGlobal = cancelStringFromGlobal;
	}
	

}
