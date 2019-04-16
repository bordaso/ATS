package screens;

import java.io.IOException;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import businessModels.GlobalSetup;
import businessModels.Order;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.converter.LocalTimeStringConverter;
import util.APICaller;
import util.TimeCalculator;

public class GlobalSetupScreen extends VBox {

	private GlobalSetup globalSetupBusinessObject = new GlobalSetup();

	LocalTime now;
	LocalTime nowTimeSendOrder;
	LocalTime nowTimeCancelOrder;

	private ContentFinal contentFinal;

	List<TickerScreenLong> tickerScreenLongList = new ArrayList<>();
	List<TickerScreenShort> tickerScreenShortList = new ArrayList<>();

	List<OrderRowScreenShort> tickerScreenShortOrderList;
	List<OrderRowScreenLong> tickerScreenLongOrderList;
	private List<String> orderList = new ArrayList<>();

	private volatile boolean theThreadStopperGlobal = true;
	private volatile boolean theThreadStopperGlobalCancel = true;

	private Label title = new Label("Global Setup");
	private TextField globalOrderAt = new TextField();
	private TextField globalCloseLongOrdersAt = new TextField();

	private Button setSelectedOrdersToGlobalQueue = new Button("Set");
	private Button cancelSelectedOrdersFromGlobalQueue = new Button("Cancel");

	private HBox rowOne = new HBox();
	private HBox rowTwo = new HBox();
	private HBox rowThree = new HBox();

	private boolean isItAShortGlobalScreen;

	public GlobalSetupScreen(ContentFinal contentFinal, boolean isItAShortGlobalScreen) {
		super();

		this.contentFinal = contentFinal;
		this.isItAShortGlobalScreen = isItAShortGlobalScreen;

		globalSetupSETButtonSetup();
		globalCancelButtonSetup();

		globalOrderAt.setPromptText("Global order at");
		globalCloseLongOrdersAt.setPromptText("Global cancel at");
		globalCloseLongOrdersAt.setText("60");

		setupActionTextField();

		rowOne.getChildren().add(title);

		rowTwo.getChildren().add(globalOrderAt);
		rowTwo.getChildren().add(globalCloseLongOrdersAt);

		rowThree.getChildren().add(setSelectedOrdersToGlobalQueue);
		rowThree.getChildren().add(cancelSelectedOrdersFromGlobalQueue);

		rowOne.setSpacing(5);
		rowTwo.setSpacing(5);
		rowThree.setSpacing(5);

		getChildren().add(rowOne);
		getChildren().add(rowTwo);
		getChildren().add(rowThree);
		setSpacing(5);
	}

	private void setupActionTextField() {
		globalOrderAt.setOnMouseClicked(event -> {
			if (event.getClickCount() == 2) {
				LocalDateTime roundCeiling = LocalDateTime.now().plusMinutes(1).withSecond(0);
				// truncatedTo(ChronoUnit.MINUTES).
				globalOrderAt.setText(roundCeiling.toLocalTime().toString().substring(0, 8));
			}
		});

		globalOrderAt.setOnKeyPressed(event -> {
			if (event.getCode() == KeyCode.UP) {
				globalOrderAt.setText(TimeCalculator.increaseTime(globalOrderAt));
			} else if (event.getCode() == KeyCode.DOWN) {
				globalOrderAt.setText(TimeCalculator.decreaseTime(globalOrderAt));

			} else if (event.getCode() == KeyCode.RIGHT) {
				globalOrderAt.setText(TimeCalculator.increaseTime(globalOrderAt, true));

			} else if (event.getCode() == KeyCode.LEFT) {
				globalOrderAt.setText(TimeCalculator.decreaseTime(globalOrderAt, true));

			} else if (event.getCode() == KeyCode.ENTER) {
				setSelectedOrdersToGlobalQueue.fire();
			}
		});

	}

	private void globalCancelButtonSetup() {

		getCancelSelectedOrdersFromGlobalQueue().setOnAction(evt -> {
			setTheThreadStopperGlobal(false);
			getSetSelectedOrdersToGlobalQueue().setDisable(false);
			getGlobalOrderAt().setDisable(false);
			getGlobalOrderAt().clear();

			setTheThreadStopperGlobalCancel(false);
			getGlobalCloseLongOrdersAt().setDisable(false);
			getGlobalCloseLongOrdersAt().setText("60");
		});

	}

	// GlobalSetupScreen globalSetupLong, VBox contentFinal
	private void globalSetupSETButtonSetup() {

		getSetSelectedOrdersToGlobalQueue().setOnAction(evt -> {

			setTheThreadStopperGlobal(true);
			getSetSelectedOrdersToGlobalQueue().setDisable(true);
			getGlobalOrderAt().setDisable(true);

			ObservableList<Node> contentFinalsContent = contentFinal.getChildren();

			for (Node tickerScreenHolder : contentFinalsContent) {

				TickerScreenLong longScreen = ((TickerScreenHolder) tickerScreenHolder).getLongScreen();
				TickerScreenShort shortScreen = ((TickerScreenHolder) tickerScreenHolder).getShortScreen();

				if (isItAShortGlobalScreen && shortScreen.getTickerBusinessObject().getIncludeGlobal() == true) {

					tickerScreenShortList.add(shortScreen);
					tickerScreenShortOrderList = new ArrayList<>();

					for (OrderRowScreenShort row : shortScreen.getOrdersPanelContentList()) {
						if (row.getOrderSelected().isSelected()) {
							tickerScreenShortOrderList.add(row);
						}
					}

				}

				if (!isItAShortGlobalScreen && longScreen.getTickerBusinessObject().getIncludeGlobal() == true) {

					tickerScreenLongList.add(longScreen);
					tickerScreenLongOrderList = new ArrayList<>();

					for (OrderRowScreenLong row : longScreen.getOrdersPanelContentList()) {
						if (row.getOrderSelected().isSelected()) {
							tickerScreenLongOrderList.add(row);
						}
					}
				}

			}

			String pattern = "HH:mm:ss";
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);

			LocalTime futureInput = new LocalTimeStringConverter(formatter, null)
					.fromString(getGlobalOrderAt().getText());

			Thread globalLongSetThread = new Thread(() -> {

				Platform.runLater(new Runnable() {

					public void run() {

						if (tickerScreenShortOrderList == null) {

							for (TickerScreenLong screenLong : tickerScreenLongList) {
								screenLong.setFutureInputFromGlobal(futureInput);
								screenLong.setCancelStringFromGlobal(getGlobalCloseLongOrdersAt().getText());
								screenLong.getSetSelectedOrdersToQueue().fire();
							}

						} else {

							for (TickerScreenShort screenShort : tickerScreenShortList) {
								screenShort.setFutureInputFromGlobal(futureInput);
								screenShort.setCancelStringFromGlobal(getGlobalCloseLongOrdersAt().getText());
								screenShort.getSetSelectedOrdersToQueue().fire();
							}

						}

						getSetSelectedOrdersToGlobalQueue().setDisable(false);
						getGlobalOrderAt().setDisable(false);

					}
				});

			});

			globalLongSetThread.start();

			if (!getGlobalCloseLongOrdersAt().getText().isEmpty()) {
				
				getGlobalCloseLongOrdersAt().setDisable(true);
				setTheThreadStopperGlobalCancel(true);
			

				Thread globalLongCancelThread = new Thread(() -> {

					while (isTheThreadStopperGlobalCancel()) {

						try {
							now = LocalTime.now();
							Thread.sleep(1000);

						} catch (InterruptedException ex) {
							ex.printStackTrace();
						}

						if (futureInput.isBefore(now)) {

							Platform.runLater(new Runnable() {

								public void run() {

									Integer secondValue=null;
									if (!getGlobalCloseLongOrdersAt().getText().isEmpty()) {
										secondValue = Integer.valueOf(getGlobalCloseLongOrdersAt().getText());
										secondValue--;
										getGlobalCloseLongOrdersAt().setText(secondValue.toString());
									}

									if (secondValue != null && secondValue.toString().equals("0")) {
										setTheThreadStopperGlobalCancel(false);
										getGlobalCloseLongOrdersAt().setDisable(false);
									}

										
									

								}
							});
						}
					}
				});

				globalLongCancelThread.start();
			}

		});

	}

	public GlobalSetup getGlobalSetupBusinessObject() {
		return globalSetupBusinessObject;
	}

	public void setGlobalSetupBusinessObject(GlobalSetup globalSetupBusinessObject) {
		this.globalSetupBusinessObject = globalSetupBusinessObject;
	}

	public boolean isTheThreadStopperGlobal() {
		return theThreadStopperGlobal;
	}

	public void setTheThreadStopperGlobal(boolean theThreadStopperGlobal) {
		this.theThreadStopperGlobal = theThreadStopperGlobal;
	}

	public boolean isTheThreadStopperGlobalCancel() {
		return theThreadStopperGlobalCancel;
	}

	public void setTheThreadStopperGlobalCancel(boolean theThreadStopperGlobalCancel) {
		this.theThreadStopperGlobalCancel = theThreadStopperGlobalCancel;
	}

	public Label getTitle() {
		return title;
	}

	public void setTitle(Label title) {
		this.title = title;
	}

	public TextField getGlobalOrderAt() {
		return globalOrderAt;
	}

	public void setGlobalOrderAt(TextField globalOrderAt) {
		this.globalOrderAt = globalOrderAt;
	}

	public TextField getGlobalCloseLongOrdersAt() {
		return globalCloseLongOrdersAt;
	}

	public void setGlobalCloseLongOrdersAt(TextField globalCloseLongOrdersAt) {
		this.globalCloseLongOrdersAt = globalCloseLongOrdersAt;
	}

	public Button getSetSelectedOrdersToGlobalQueue() {
		return setSelectedOrdersToGlobalQueue;
	}

	public void setSetSelectedOrdersToGlobalQueue(Button setSelectedOrdersToGlobalQueue) {
		this.setSelectedOrdersToGlobalQueue = setSelectedOrdersToGlobalQueue;
	}

	public Button getCancelSelectedOrdersFromGlobalQueue() {
		return cancelSelectedOrdersFromGlobalQueue;
	}

	public void setCancelSelectedOrdersFromGlobalQueue(Button cancelSelectedOrdersFromGlobalQueue) {
		this.cancelSelectedOrdersFromGlobalQueue = cancelSelectedOrdersFromGlobalQueue;
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

}
