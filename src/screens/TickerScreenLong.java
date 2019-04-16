package screens;

import java.io.IOException;
import java.net.DatagramSocket;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import businessModels.Ticker;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.util.converter.LocalTimeStringConverter;
import util.APICaller;
import util.Side;
import util.TimeCalculator;

public class TickerScreenLong extends BorderPane {
	
	private	LocalTime nowTimeSendOrder;
	private LocalTime nowTimeCancelOrder;
	private LocalTime futureInputFromGlobal = null;
	private String cancelStringFromGlobal = null;

	private Ticker tickerBusinessObject = new Ticker();

	//LocalTime now;

	private DatagramSocket screensServerSocket;

	private volatile boolean theThreadStopperLong = true;
	private volatile boolean theThreadStopperLongCancelled = true;

	private Thread longSetThread;

	private VBox topView;
	private HBox rowOne;
	private HBox rowTwo;
	private HBox rowThree;
	private HBox rowFour;
	private HBox rowFive;
	private Button addStockRowButton = new Button("Add new order row");
	private Button deleteStockRowButton = new Button("Delete order row");

	private TextField tickerName = new TextField();
	private TextField tickerTOSFeed = new TextField();
	private TextField tickerTOSTime = new TextField();
	
	private TextField orderAt = new TextField();
	private TextField closeLongOrdersAt = new TextField();

	private CheckBox includeGlobal = new CheckBox();
	private Label excludeFromGlobalCheckboxLabel = new Label("Include into Global Setup Long:");
	private Button setSelectedOrdersToQueue = new Button("Set");
	private Button cancelSelectedOrdersFromQueue = new Button("Cancel");
	private Button getTickerData = new Button("Get ticker data");

	private OrderRowScreenLong orderRowScreenLong;
	private VBox ordersPanelContent;
	private List<OrderRowScreenLong> ordersPanelContentList;
	// VBox ordersPanelContent = new VBox(5);
	private ScrollPane ordersPanel = new ScrollPane(ordersPanelContent);
	
	private List<OrderRowScreenLong> selectedLongOrdersData;

	public TickerScreenLong() {

		Color separatorColor = Color.color(0.78, 0.78, 0.78, 1);
		// Color.BLACK;
		orderRowScreenLong = new OrderRowScreenLong(tickerName);
		ordersPanelContentList = new ArrayList<>();
		ordersPanelContentList.add(orderRowScreenLong);
		ordersPanelContent = new VBox(orderRowScreenLong);
		ordersPanelContent.setSpacing(5);
		// ordersPanelContent.setBorder(new Border(new
		// BorderStroke(separatorColor,
		// BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));

		// ordersPanelContent.setBorder(getBorder());

		String cssLayout = "-fx-border-color: black;\n" + "-fx-border-insets: 5;\n" + "-fx-border-width: 3;\n";
		// +
		// "-fx-border-style: dashed;\n";

		// VBox yourBox = new VBox();
		// ordersPanelContent.setStyle(cssLayout);

		ordersPanel.setFitToWidth(true);
		ordersPanel.setPrefHeight(150);
		ordersPanel.setMaxHeight(150);

		tickerName.setPromptText("Ticker name");
		tickerTOSFeed.setPromptText("Ticker TOS feed");
		orderAt.setPromptText("Order at");
		closeLongOrdersAt.setPromptText("Cancel Long orders at");
		closeLongOrdersAt.setText("60");

		setupActionButtons();
		setupActionCheckbox();
		setupActionTextField();
		// longSETButtonSetup();

		rowOne = new HBox(tickerName, tickerTOSFeed, excludeFromGlobalCheckboxLabel, includeGlobal);
		rowTwo = new HBox(getTickerData);
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
		topView.setPadding(new Insets(0, 0, 10, 0));

		this.setTop(topView);
		// this.setCenter(ordersPanel);
		this.setCenter(ordersPanelContent);
	}

	private TickerScreenLong getThis() {
		return this;
	}

	private void setupActionButtons() {

		getTickerData.setOnAction(evt -> {
			// RandomString gen = new RandomString(8,
			// ThreadLocalRandom.current());
			//
			// tickerName.setText(gen.nextString());
			// tickerTOSFeed.setText(String.valueOf(ThreadLocalRandom.current().nextInt(1,
			// 100 + 1)));

			try {
				APICaller.callGetTickerData(this);
			} catch (IOException e) {
				tickerName.setDisable(false);
				tickerName.clear();
				tickerTOSFeed.setDisable(false);
				tickerName.clear();
				getTickerData.setDisable(false);
				
				
				triggerError("ERROR NO DATA RECEIVED FOR 5 SEC, TIMEOUT");

				return;
			}

			tickerName.setDisable(true);
			tickerTOSFeed.setDisable(true);
			getTickerData.setDisable(true);
			triggerInfo("Succesfuly connected");

		});

		addStockRowButton.setOnAction(evt -> {
			OrderRowScreenLong orderRowLong = new OrderRowScreenLong(tickerName);
			ordersPanelContent.getChildren().add(orderRowLong);
			ordersPanelContentList.add(orderRowLong);
			System.out.println("row added to screen id: " + this + " row id: " + orderRowLong);
		});

		deleteStockRowButton.setOnAction(evt -> {
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
		// ObservableList<OrderRowScreenLong> selectedOrders =
		// FXCollections.observableArrayList();
		// selectedOrders.clear();
		//
		// for (Node order : allOrders) {
		// if (((OrderRowScreenLong)
		// order).getOrderBusinessObject().getOrderSelected())
		// {
		// DecimalFormat df = new DecimalFormat("#");
		//
		// OrderRowScreenLong longOrder=(OrderRowScreenLong) order;
		//
		// SharedObject.getSharedStringList().clear();
		//
		// SharedObject.getSharedStringList().add(
		//
		// "Order of: "+this.getTickerName().getText()+
		// " Number of shares: "+longOrder.getQuantitiyInNumber().getText()+
		// " at the price of:
		// "+(Double.valueOf(this.getTickerTOSFeed().getText())+Double.valueOf(longOrder.getAmountToGetTheDesiredPrice().getText()))+
		// " at the time of: "+this.getOrderAt().getText()
		//
		// );
		//
		// selectedOrders.add(longOrder);
		// }
		// }
		// System.out.println(selectedOrders);
		//
		// });
		
		longCancelButtonSetup(this);
		longSETButtonSetup(this); 

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

	}

	public void triggerInfo(String text) {

		Alert alertInfo = new Alert(AlertType.INFORMATION, text);
		alertInfo.show();

	}

	public void triggerError(String text) {

		Alert alertError = new Alert(AlertType.ERROR, text);
		alertError.show();

	}

	private void longCancelButtonSetup(TickerScreenLong buyScreen) {

		buyScreen.getCancelSelectedOrdersFromQueue().setOnAction(evt -> {
			buyScreen.setTheThreadStopperLong(false);
			buyScreen.getSetSelectedOrdersToQueue().setDisable(false);
			buyScreen.getOrderAt().setDisable(false);
			buyScreen.getOrderAt().clear();

			buyScreen.setTheThreadStopperLongCancelled(false);
			buyScreen.getCloseLongOrdersAt().setDisable(false);
			buyScreen.getCloseLongOrdersAt().clear();
			buyScreen.getCloseLongOrdersAt().setText("60");
		});

	}

	private void longSETButtonSetup(TickerScreenLong buyScreen) {

		selectedLongOrdersData = new ArrayList<>();

		buyScreen.getSetSelectedOrdersToQueue().setOnAction(evt -> {

			buyScreen.setTheThreadStopperLong(true);
			buyScreen.getSetSelectedOrdersToQueue().setDisable(true);
			buyScreen.getOrderAt().setDisable(true);
			
			selectedLongOrdersData.clear();
			
			for(OrderRowScreenLong row : getOrdersPanelContentList()){
				if(row.getOrderSelected().isSelected()){
					selectedLongOrdersData.add(row);
				}
				
				
			}

			String pattern = "HH:mm:ss";
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);

			LocalTime futureInput = futureInputFromGlobal==null?new LocalTimeStringConverter(formatter, null)
					.fromString(buyScreen.getOrderAt().getText()):futureInputFromGlobal;
			
			

			Thread longSetThread = new Thread(() -> {

				while (buyScreen.isTheThreadStopperLong()) {
					try {
						
						nowTimeSendOrder = new LocalTimeStringConverter(formatter, null)
								.fromString(buyScreen.getTickerTOSTime().getText());
						Thread.sleep(1);

					} catch (InterruptedException ex) {
						ex.printStackTrace();
					}
					
					
					if ((  futureInput.equals(nowTimeSendOrder.withSecond(00)) || futureInput.isBefore(nowTimeSendOrder) ) && buyScreen.isTheThreadStopperLong()) {

						Platform.runLater(new Runnable() {

							public void run() {

								if ((  futureInput.equals(nowTimeSendOrder.withSecond(00)) || futureInput.isBefore(nowTimeSendOrder) ) && buyScreen.isTheThreadStopperLong()) {
									
									try {
										System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
										System.out.println("++++++++++++++++++++++++++++++BUY ORDER++++++++++++++++++++++++++++++");
										System.out.println("futureInput TIME "+futureInput.toString()+"  IS BEFORE ");
										System.out.println(" MARKET TIME "+nowTimeSendOrder.toString());
										System.out.println(" ((  futureInput.equals(nowTime.withSecond(00)) "+ futureInput.equals(nowTimeSendOrder.withSecond(00)));
										System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
										
										APICaller.callSendOrder(selectedLongOrdersData, tickerTOSFeed.getText(), Side.BUY);
									} catch (IOException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}									
									
									buyScreen.setTheThreadStopperLong(false);
									buyScreen.getSetSelectedOrdersToQueue().setDisable(false);
									buyScreen.getOrderAt().setDisable(false);
									buyScreen.getOrderAt().clear();
								}
								
							}
						});
						System.gc();
					}
				}
			});

			longSetThread.start();

			if (!buyScreen.getCloseLongOrdersAt().getText().isEmpty()) {

				String cancelString = buyScreen.getCloseLongOrdersAt().getText();
				boolean isNumeric = cancelString.chars().allMatch(Character::isDigit);

				if (!isNumeric) {
					return;
				}

				buyScreen.getCloseLongOrdersAt().setDisable(true);
				buyScreen.setTheThreadStopperLongCancelled(true);

				LocalTime cancelTime = cancelStringFromGlobal==null?futureInput.plusSeconds(Long.valueOf(cancelString)):futureInput.plusSeconds(Long.valueOf(cancelStringFromGlobal));

				Thread longCancelThread = new Thread(() -> {

					while (buyScreen.isTheThreadStopperLongCancelled()) {

						try {
							
							nowTimeCancelOrder  = LocalTime.now();
							Thread.sleep(1000);

						} catch (InterruptedException ex) {
							ex.printStackTrace();
						}

						if (futureInput.isBefore(nowTimeCancelOrder)) {

							Platform.runLater(new Runnable() {

								public void run() {

									if (!buyScreen.getCloseLongOrdersAt().getText().isEmpty()) {
										Integer secondValue = Integer
												.valueOf(buyScreen.getCloseLongOrdersAt().getText());
										secondValue--;
										buyScreen.getCloseLongOrdersAt().setText(secondValue.toString());
									}

									if (cancelTime.isBefore(nowTimeCancelOrder) && buyScreen.isTheThreadStopperLongCancelled()) {

										try {
											APICaller.callCancelOrder(selectedLongOrdersData, Side.BUY);
											setFutureInputFromGlobal(null);
											setCancelStringFromGlobal(null);
										} catch (IOException e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										}finally {
											buyScreen.getCloseLongOrdersAt().clear();
											buyScreen.getCloseLongOrdersAt().setText("61");
											}

										buyScreen.setTheThreadStopperLongCancelled(false);
										buyScreen.getCloseLongOrdersAt().setDisable(false);
										//buyScreen.getCloseLongOrdersAt().clear();
									}

									// }
									
								}
								
							});
							System.gc();
						}
					}
					
				});

				longCancelThread.start();
			}

		});

	}

	public Ticker getTickerBusinessObject() {
		return tickerBusinessObject;
	}

	public DatagramSocket getScreensServerSocket() {
		return screensServerSocket;
	}

	public void setScreensServerSocket(DatagramSocket screensServerSocket) {
		this.screensServerSocket = screensServerSocket;
	}

	public void setTickerBusinessObject(Ticker tickerBusinessObject) {
		this.tickerBusinessObject = tickerBusinessObject;
	}

	public boolean isTheThreadStopperLong() {
		return theThreadStopperLong;
	}

	public void setTheThreadStopperLong(boolean theThreadStopperLong) {
		this.theThreadStopperLong = theThreadStopperLong;
	}

	public boolean isTheThreadStopperLongCancelled() {
		return theThreadStopperLongCancelled;
	}

	public void setTheThreadStopperLongCancelled(boolean theThreadStopperLongCancelled) {
		this.theThreadStopperLongCancelled = theThreadStopperLongCancelled;
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

	public Button getGetTickerData() {
		return getTickerData;
	}

	public void setGetTickerData(Button getTickerData) {
		this.getTickerData = getTickerData;
	}
	
	public OrderRowScreenLong getOrderRowScreenLong() {
		return orderRowScreenLong;
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
	
	public TextField getTickerTOSTime() {
		return tickerTOSTime;
	}

	public void setTickerTOSTime(TextField tickerTOSTime) {
		this.tickerTOSTime = tickerTOSTime;
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

	public VBox getOrdersPanelContent() {
		return ordersPanelContent;
	}

	public void setOrdersPanelContent(VBox ordersPanelContent) {
		this.ordersPanelContent = ordersPanelContent;
	}
	
	public List<OrderRowScreenLong> getOrdersPanelContentList() {
		return ordersPanelContentList;
	}

	public void setOrdersPanelContentList(List<OrderRowScreenLong> ordersPanelContentList) {
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
