package cetra.ui;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cetra.field.script.EOpcode;
import cetra.field.script.Table;
import cetra.model.Message;
import cetra.model.Window;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import jfxtras.scene.control.ListSpinner;

public class DialogEditorPane extends ScrollPane {

	public static final int FIELD_WIDTH = 320;
	public static final int FIELD_HEIGHT = 224;

	public static final int FIELD_MARGIN_LEFT = 8;
	public static final int FIELD_MARGIN_TOP = 8;
	public static final int FIELD_MARGIN_RIGHT = 8;
	public static final int FIELD_MARGIN_BOTTON = 0;

	public static final int FIELD_AREA_WIDTH = FIELD_WIDTH - FIELD_MARGIN_RIGHT;
	public static final int FIELD_AREA_HEIGHT = FIELD_HEIGHT - FIELD_MARGIN_BOTTON;

	private StackPane fieldAreaPane;
	private GridPane winPropertiesPane;
	private GridPane msgPropertiesPane;
	private DialogWindowPane dialoqueWindow;
	private DialogText dialogText;
	private Table table;
	private Window selectedWindow;
	private Message selectedMessage;

	private ListSpinner<Integer> lspnMessage;
	private ListSpinner<Integer> lspnPage;

	private ListSpinner<Integer> lspnWindow;
	private ListSpinner<Integer> lspnX;
	private ListSpinner<Integer> lspnY;
	private ListSpinner<Integer> lspnWidth;
	private ListSpinner<Integer> lspnHeight;

	private Label amountWindow;
	private Label amountPage;
	private Label amountMessage;

	private Button btnResizeWidth;
	private Button btnResizeHeight;

	private List<Message> messages;

	public DialogEditorPane() {

		fieldAreaPane = new StackPane();

		winPropertiesPane = new GridPane();
		msgPropertiesPane = new GridPane();

		dialoqueWindow = new DialogWindowPane();

		dialoqueWindow.setTranslateX(FIELD_MARGIN_LEFT);
		dialoqueWindow.setTranslateY(FIELD_MARGIN_TOP);

		dialogText = new DialogText();

		try {
			table = new Table(new File("table.txt"));
		} catch (Exception e) {
			e.printStackTrace();
		}

		lspnMessage = new ListSpinner<Integer>(1, 1);
		lspnPage = new ListSpinner<Integer>(1, 1);
		lspnWindow = new ListSpinner<Integer>(1, 1);

		amountMessage = new Label();
		amountPage = new Label();
		amountWindow = new Label();

		lspnX = new ListSpinner<Integer>(Short.MIN_VALUE, Short.MAX_VALUE);
		lspnY = new ListSpinner<Integer>(Short.MIN_VALUE, Short.MAX_VALUE);
		lspnWidth = new ListSpinner<Integer>(Short.MIN_VALUE, Short.MAX_VALUE);
		lspnHeight = new ListSpinner<Integer>(Short.MIN_VALUE, Short.MAX_VALUE);

		lspnX.setValue(FIELD_MARGIN_LEFT);
		lspnY.setValue(FIELD_MARGIN_TOP);
		lspnWidth.setValue(FIELD_AREA_WIDTH - FIELD_MARGIN_RIGHT);
		lspnHeight.setValue(FIELD_AREA_HEIGHT - FIELD_MARGIN_TOP);

		btnResizeWidth = new Button("Resize");
		btnResizeHeight = new Button("Resize");

		btnResizeWidth.setFont(new Font("System", 13));
		btnResizeHeight.setFont(new Font("System", 13));

		msgPropertiesPane.add(new Label("Message"), 0, 0);
		msgPropertiesPane.add(lspnMessage, 1, 0);
		msgPropertiesPane.add(amountMessage, 2, 0);

		msgPropertiesPane.add(new Label("Page"), 0, 1);
		msgPropertiesPane.add(lspnPage, 1, 1);
		msgPropertiesPane.add(amountPage, 2, 1);

		msgPropertiesPane.setTranslateX(340);
		msgPropertiesPane.setTranslateY(5);
		msgPropertiesPane.setVgap(5);

		winPropertiesPane.add(new Label("Window"), 0, 0);
		winPropertiesPane.add(lspnWindow, 1, 0);
		winPropertiesPane.add(amountWindow, 2, 0);

		winPropertiesPane.add(new Label("X"), 0, 1);
		winPropertiesPane.add(lspnX, 1, 1);

		winPropertiesPane.add(new Label("Y"), 0, 2);
		winPropertiesPane.add(lspnY, 1, 2);

		winPropertiesPane.add(new Label("Width"), 0, 3);
		winPropertiesPane.add(lspnWidth, 1, 3);
		winPropertiesPane.add(btnResizeWidth, 2, 3);

		winPropertiesPane.add(new Label("Height"), 0, 4);
		winPropertiesPane.add(lspnHeight, 1, 4);
		winPropertiesPane.add(btnResizeHeight, 2, 4);

		winPropertiesPane.setTranslateX(340);
		winPropertiesPane.setTranslateY(90);
		winPropertiesPane.setVgap(2);
		winPropertiesPane.setHgap(3);

		winPropertiesPane.getColumnConstraints().add(new ColumnConstraints(50));
		winPropertiesPane.getColumnConstraints().add(new ColumnConstraints(80));

		msgPropertiesPane.getColumnConstraints().add(new ColumnConstraints(50));
		msgPropertiesPane.getColumnConstraints().add(new ColumnConstraints(60));

		fieldAreaPane.setAlignment(Pos.TOP_LEFT);

		Line line = new Line(0, 0, 0, FIELD_AREA_HEIGHT);
		line.setTranslateX(FIELD_MARGIN_LEFT);
		line.setStroke(Color.RED);
		fieldAreaPane.getChildren().add(line);

		line = new Line(0, 0, 0, FIELD_HEIGHT);
		line.setTranslateX(FIELD_AREA_WIDTH - 1);
		line.setStroke(Color.RED);
		fieldAreaPane.getChildren().add(line);

		line = new Line(0, 0, 0, FIELD_AREA_HEIGHT);
		line.setTranslateX(FIELD_WIDTH / 4);
		line.setStroke(Color.GRAY);
		line.getStrokeDashArray().add(2d);
		fieldAreaPane.getChildren().add(line);

		line = new Line(0, 0, 0, FIELD_AREA_HEIGHT);
		line.setTranslateX((FIELD_WIDTH / 2) + (FIELD_WIDTH / 4));
		line.setStroke(Color.GRAY);
		line.getStrokeDashArray().add(2d);
		fieldAreaPane.getChildren().add(line);

		line = new Line(0, 0, 0, FIELD_HEIGHT);
		line.setTranslateX(FIELD_WIDTH / 2);
		line.setStroke(Color.GREENYELLOW);
		fieldAreaPane.getChildren().add(line);

		line = new Line(0, 0, FIELD_WIDTH, 0);
		line.setTranslateY(FIELD_MARGIN_TOP);
		line.setStroke(Color.RED);
		fieldAreaPane.getChildren().add(line);

		line = new Line(0, 0, FIELD_WIDTH, 0);
		line.setTranslateY(FIELD_HEIGHT - FIELD_MARGIN_TOP - 2);
		line.setStroke(Color.RED);
		fieldAreaPane.getChildren().add(line);

		line = new Line(0, 0, FIELD_WIDTH, 0);
		line.setTranslateY(FIELD_HEIGHT / 4);
		line.setStroke(Color.GRAY);
		line.getStrokeDashArray().add(2d);
		fieldAreaPane.getChildren().add(line);

		line = new Line(0, 0, FIELD_WIDTH, 0);
		line.setTranslateY((FIELD_HEIGHT / 4) + (FIELD_HEIGHT / 2));
		line.setStroke(Color.GRAY);
		line.getStrokeDashArray().add(2d);
		fieldAreaPane.getChildren().add(line);

		line = new Line(0, 0, FIELD_WIDTH, 0);
		line.setTranslateY(FIELD_HEIGHT / 2);
		line.setStroke(Color.GREENYELLOW);

		fieldAreaPane.getChildren().add(line);

		fieldAreaPane.getChildren().add(dialoqueWindow);
		fieldAreaPane.setStyle("-fx-background-color:#000000;");
		fieldAreaPane.setPrefSize(640, 480);

		ScrollPane fieldScrollPane = new ScrollPane();
		fieldScrollPane.setContent(fieldAreaPane);
		fieldScrollPane.setHbarPolicy(ScrollBarPolicy.NEVER);
		fieldScrollPane.setVbarPolicy(ScrollBarPolicy.NEVER);
		

		fieldScrollPane.setTranslateX(5);
		fieldScrollPane.setTranslateY(5);
		// fieldScrollPane.setFitToWidth(true);
		// fieldScrollPane.setFitToHeight(true);

		fieldScrollPane.setPrefSize(FIELD_WIDTH, FIELD_HEIGHT);

		StackPane stackPaneAreaEditor = new StackPane();
		stackPaneAreaEditor.setAlignment(Pos.TOP_LEFT);
		stackPaneAreaEditor.getChildren().addAll(fieldScrollPane, msgPropertiesPane, winPropertiesPane);

		this.setContent(stackPaneAreaEditor);

		initListeners();
		disableAllComponents(true);
	}

	public void initialize() {
		
		if (messages != null && messages.size() > 0) {

			disableAllComponents(false);
			ObservableList<Integer> indexMessages = FXCollections.observableArrayList();
			for (int i = 1; i <= messages.size(); i++) {
				indexMessages.add(i);
			}
			
			lspnMessage.setItems(indexMessages);
			lspnMessage.setIndex(0);
			lspnPage.setIndex(0);
			lspnMessage.setDisable(indexMessages.size() < 2);
			amountMessage.setText(" of " + indexMessages.size());

			selectMessage();
			loadWindow();

		} else {
			disableAllComponents(true);
		}
	}

	public void refresh() {

		if (messages != null && messages.size() > 0) {
			loadMessage();
			selectPage(lspnPage.getIndex());
		}
	}

	public void disableAllComponents(boolean b) {

		dialoqueWindow.setVisible(!b);
		lspnX.setDisable(b);
		lspnY.setDisable(b);
		lspnWidth.setDisable(b);
		lspnHeight.setDisable(b);
		lspnMessage.setDisable(b);
		lspnPage.setDisable(b);
		btnResizeWidth.setDisable(b);
		btnResizeHeight.setDisable(b);
		lspnWindow.setDisable(b);

		if (b) {
			amountMessage.setText("");
			amountPage.setText("");
			amountWindow.setText("");
		}

	}

	private void selectMessage() {

		selectedMessage = messages.get(lspnMessage.getIndex());

		ObservableList<Integer> numberWindows = FXCollections.observableArrayList();
		for (int i = 1; i <= selectedMessage.getWindows().size(); i++) {
			numberWindows.add(i);
		}
		lspnWindow.setItems(numberWindows);
		lspnWindow.setIndex(0);
		lspnWindow.setDisable(numberWindows.size() < 2);
		amountWindow.setText("of " + numberWindows.size());

		loadMessage();
		selectWindow();

	}

	private void selectWindow() {
		selectedWindow = selectedMessage.getWindow(lspnWindow.getIndex());
		loadWindow();
	}

	private void loadWindow() {

		dialoqueWindow.setMode(selectedWindow.getWindowMode().getMode());

		lspnWidth.setValue(selectedWindow.getWidth());
		lspnHeight.setValue(selectedWindow.getHeight());
		lspnX.setValue(selectedWindow.getX());
		lspnY.setValue(selectedWindow.getY());

		adjustWindowX(selectedWindow.getX());
		adjustWindowY(selectedWindow.getY());
	}

	private void loadMessage() {

		try {

			String str = selectedMessage.getText().toString().replaceAll("\\{Cloud\\}|\\{0xEA\\}", "{0xEA}Cloud{0xEA}")
					.replaceAll("\\{Barret\\}|\\{0xEB\\}", "{0xEB}Barret{0xEB}").replaceAll("\\{Tifa\\}|\\{0xEC\\}", "{0xEC}Tifa{0xEC}")
					.replaceAll("\\{Aerith\\}|\\{0xED\\}", "{0xED}Aerith{0xED}").replaceAll("\\{Red 13\\}|\\{0xEE\\}", "{0xEE}Red 13{0xEE}")
					.replaceAll("\\{Yuffie\\}|\\{0xEF\\}", "{0xEF}Yuffie{0xEF}").replaceAll("\\{Cait Sith\\}|\\{0xEF0\\}", "{0xF0}Cait Sith{0xF0}")
					.replaceAll("\\{Vincent\\}|\\{0xF1\\}", "{0xF1}Vincent{0xF1}").replaceAll("\\{Cid\\}|\\{0xF2\\}", "{0xF2}Cid{0xF2}")
					.replaceAll("\\{(\\d){3}\\}", "").replaceAll(", ", "{0x0C}{0x00}");

			byte[] bytes = table.parseBytes(str);

			ArrayList<StackPane> paneTexts = null;
			if (selectedMessage.getFirstLineChoice() > -1)
				paneTexts = dialogText.createText(bytes, selectedMessage.getFirstLineChoice());
			else
				paneTexts = dialogText.createText(bytes);

			dialoqueWindow.getChildren().clear();
			dialoqueWindow.getChildren().addAll(paneTexts);

			ObservableList<Integer> numbersPages = FXCollections.observableArrayList();
			for (int i = 1; i <= paneTexts.size(); i++) {
				numbersPages.add(i);
			}
			lspnPage.setItems(numbersPages);
			lspnPage.setDisable(numbersPages.size() < 2);
			amountPage.setText(" of " + numbersPages.size());

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void adjustWindowY(int y) {

		if (y < FIELD_MARGIN_TOP)
			y = FIELD_MARGIN_TOP;

		if (y + dialoqueWindow.getMaxHeight() > (FIELD_AREA_HEIGHT))
			y = FIELD_AREA_HEIGHT - (int) dialoqueWindow.getMaxHeight();

		dialoqueWindow.setTranslateY(y);
	}

	private void adjustWindowX(int x) {

		if (x < FIELD_MARGIN_LEFT)
			x = FIELD_MARGIN_LEFT;

		if (x + dialoqueWindow.getMaxWidth() > (FIELD_AREA_WIDTH))
			x = FIELD_AREA_WIDTH - (int) dialoqueWindow.getMaxWidth();

		dialoqueWindow.setTranslateX(x);
	}

	private void selectPage(int indexPage) {
		for (Node node : dialoqueWindow.getChildren()) {
			node.setVisible(false);
		}
		dialoqueWindow.getChildren().get(indexPage).setVisible(true);
	}

	private void initListeners() {

		lspnHeight.valueProperty().addListener(new ChangeListener<Integer>() {
			@Override
			public void changed(ObservableValue<? extends Integer> observable, Integer oldValue, Integer newValue) {
				dialoqueWindow.setMaxHeight(newValue);
				adjustWindowY(lspnY.getValue());
				selectedWindow.setHeight(newValue);
			}
		});

		lspnWidth.valueProperty().addListener(new ChangeListener<Integer>() {
			@Override
			public void changed(ObservableValue<? extends Integer> observable, Integer oldValue, Integer newValue) {
				adjustWindowX(lspnX.getValue());
				dialoqueWindow.setMaxWidth(newValue);
				selectedWindow.setWidth(newValue);
			}
		});

		lspnX.valueProperty().addListener(new ChangeListener<Integer>() {
			@Override
			public void changed(ObservableValue<? extends Integer> observable, Integer oldValue, Integer newValue) {
				adjustWindowX(newValue);
				selectedWindow.setX(newValue);
			}
		});

		lspnY.valueProperty().addListener(new ChangeListener<Integer>() {
			@Override
			public void changed(ObservableValue<? extends Integer> observable, Integer oldValue, Integer newValue) {
				adjustWindowY(newValue);
				selectedWindow.setY(newValue);
			}
		});

		lspnPage.indexProperty().addListener(new ChangeListener<Integer>() {
			@Override
			public void changed(ObservableValue<? extends Integer> observable, Integer oldValue, Integer newValue) {
				selectPage(newValue);
			}
		});

		lspnMessage.indexProperty().addListener(new ChangeListener<Integer>() {
			@Override
			public void changed(ObservableValue<? extends Integer> observable, Integer oldValue, Integer newValue) {
				selectMessage();
				selectPage(lspnPage.getIndex());
			}
		});

		lspnWindow.indexProperty().addListener(new ChangeListener<Integer>() {
			@Override
			public void changed(ObservableValue<? extends Integer> observable, Integer oldValue, Integer newValue) {
				selectWindow();
			}
		});

		btnResizeWidth.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				lspnWidth.setValue((int) ((StackPane) dialoqueWindow.getChildren().get(0)).getPrefWidth());
			}
		});

		btnResizeHeight.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				lspnHeight.setValue((int) ((StackPane) dialoqueWindow.getChildren().get(0)).getPrefHeight());
			}
		});

		class Delta {
			double x;
			double y;
		}
		final Delta delta = new Delta();

		dialoqueWindow.setOnMousePressed(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				
					delta.x = event.getSceneX() - dialoqueWindow.getTranslateX();
					delta.y = event.getSceneY() - dialoqueWindow.getTranslateY();					
					/// System.out.printf("Window positon: X=%d,
					/// Y=%d\n",(int)dialoqueWindow.getTranslateX(),(int)dialoqueWindow.getTranslateY());
				
			}
		});

		dialoqueWindow.setOnMouseDragged(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {

				if (selectedMessage.getOpcode().getOpcode() != EOpcode.MPNAM) {
					int x = (int) (event.getSceneX() - delta.x);
					int y = (int) (event.getSceneY() - delta.y);

					lspnX.setValue(x);
					lspnY.setValue(y);

					adjustWindowX(x);
					adjustWindowY(y);
				}

				dialoqueWindow.setCursor(Cursor.CLOSED_HAND);
			}
		});

		dialoqueWindow.setOnMouseReleased(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				dialoqueWindow.setCursor(Cursor.DEFAULT);
			}
		});
	}

	public void setMessages(List<Message> messages) {
		this.messages = messages;
	}

	public ListSpinner<Integer> getLspnPage() {
		return lspnPage;
	}

	public DialogText getDialogText() {
		return dialogText;
	}
}
