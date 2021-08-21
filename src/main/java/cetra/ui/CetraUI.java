package cetra.ui;

import java.io.File;
import java.util.ArrayList;
import java.util.Optional;

import cetra.controller.CetraController;
import cetra.field.script.Tutorial;
import cetra.model.Field;
import cetra.model.Text;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.Scene;
import javafx.scene.control.Accordion;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TitledPane;
import javafx.scene.control.ToolBar;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

public class CetraUI extends Application {

	public static final String APPLICATION_NAME = "Cetra";
	// MenuBar & MenuBar Items

	private MenuBar menuBar;
	private Menu menuFile;
	private Menu menuAbout;
	private MenuItem menuItemOpenDir;

	// ToolBar & ToolBar Items

	private ToolBar toolBar;
	private Button buttonOpenISO;
	private Button buttonOpenDir;
	private Button buttonSave;
	private Button buttonSaveISO;
	private Button buttonExportText;
	private Button buttonImportText;

	// StatusPane & StatusPane Items

	private AnchorPane statusPane;
	private Label statuslabel;
	private ProgressBar progresBar;

	// MainPane & MainPane Items

	private SplitPane mainPane;
	private VBox leftPane;

	private TableView<Field> tableField;
	private TableView<Text> tableText;
	private ListView<Tutorial> tutorialList;
	private TreeView<String> treeField;

	private TabPane tabPaneEditor;
	private TabPane tabPaneField;
	private Tab tabEventScritps;
	private Tab tabTexts;
	private Tab tabTutorials;

	private VBox boxMain;
	private Stage primaryStage;

	private TextArea textAreaEdit;
	private TitledPane paneField;
	private VBox panePreview;
	private Accordion accordionField;

	private VBox vboxTextList;
	private VBox vboxTutorialList;
	private VBox vboxPreview;
	private DialogEditorPane dialogueEditor;

	private DirectoryChooser dirFieldChooser;
	private FileChooser fileChooserISO;
	private FileChooser fileChooserText;

	private TextArea tutorialAreaEdit;

	private VBox vboxTextEdit;
	private VBox vboxTutorialEdit;

	private Button buttonCleanScript;
	private Button buttonRearrangeScript;

	public void showAlertDialog(String title, String header, String content, AlertType alertType) {
		Alert alert = new Alert(alertType);
		alert.setTitle(title);
		alert.setHeaderText(header);
		alert.initOwner(this.getPrimaryStage());
		alert.setContentText(content);
		alert.showAndWait();
	}

	public boolean showConfirmationDialog(String title, String header, String content) {
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle(title);
		alert.setHeaderText(header);
		alert.setContentText(content);
		alert.initOwner(this.getPrimaryStage());
		alert.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO);
		Optional<ButtonType> option = alert.showAndWait();
		return option.get().getButtonData().isDefaultButton();
	}

	@Override
	public void start(Stage primaryStage) throws Exception {

		this.primaryStage = primaryStage;
		Scene scene = new Scene(getBoxMain(), 980, 600);
		primaryStage.setMinWidth(800);
		primaryStage.setMinHeight(600);
		primaryStage.setTitle(APPLICATION_NAME);
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	@Override
	public void init() throws Exception {
		new CetraListener(this, new CetraController(this));
		super.init();
	}

	// Getters Box Main

	public VBox getBoxMain() {
		if (boxMain == null) {
			boxMain = new VBox();
			boxMain.getChildren().addAll(getToolBar());
			boxMain.getChildren().addAll(getMainPane(), getStatusPane());
		}
		return boxMain;
	}

	//// Getters MenuBar & MenuBar Items

	public MenuBar getMenuBar() {

		if (menuBar == null) {
			menuBar = new MenuBar();
			menuBar.getMenus().addAll(getMenuFile(), getMenuAbout());
		}
		return menuBar;
	}

	public Menu getMenuFile() {
		if (menuFile == null) {
			menuFile = new Menu("File");
			menuFile.getItems().addAll(getMenuItemOpenDir());
		}
		return menuFile;
	}

	public Menu getMenuAbout() {
		if (menuAbout == null) {
			menuAbout = new Menu("About");
		}
		return menuAbout;
	}

	public MenuItem getMenuItemOpenDir() {
		if (menuItemOpenDir == null) {
			menuItemOpenDir = new MenuItem("Open Field Directory");
			menuItemOpenDir.setAccelerator(new KeyCodeCombination(KeyCode.F, KeyCombination.ALT_ANY));
			menuItemOpenDir.setMnemonicParsing(true);
		}
		return menuItemOpenDir;
	}

	//// Getters ToolBar & ToolBar Bar Items

	private ToolBar getToolBar() {
		if (toolBar == null) {
			toolBar = new ToolBar();
			toolBar.getItems().addAll(getButtonOpenISO(), getButtonSave(), getButtonSaveISO(), getButtonExportText(),
					getButtonImportText());
		}
		return toolBar;
	}

	public Button getButtonOpenDir() {
		if (buttonOpenDir == null) {
			buttonOpenDir = new Button("Open Field Dir");
		}
		return buttonOpenDir;
	}

	public Button getButtonOpenISO() {
		if (buttonOpenISO == null) {
			buttonOpenISO = new Button("Open ISO Image");
		}
		return buttonOpenISO;
	}

	public Button getButtonSaveISO() {
		if (buttonSaveISO == null) {
			buttonSaveISO = new Button("Save all in ISO");
		}
		return buttonSaveISO;
	}

	public Button getButtonSave() {
		if (buttonSave == null) {
			buttonSave = new Button("Save selected");
		}
		return buttonSave;
	}

	public Button getButtonExportText() {
		if (buttonExportText == null) {
			buttonExportText = new Button("Export Stript");
		}
		return buttonExportText;
	}

	public Button getButtonImportText() {
		if (buttonImportText == null) {
			buttonImportText = new Button("Import Stript");
		}
		return buttonImportText;
	}

	//// Getter MainPane & MainPane Items

	public SplitPane getMainPane() {

		if (mainPane == null) {

			mainPane = new SplitPane();
			mainPane.setOrientation(Orientation.HORIZONTAL);
			mainPane.getItems().addAll(getLeftPane(), getTabPaneEditor());
			mainPane.setDividerPositions(0.21);
			VBox.setVgrow(mainPane, Priority.ALWAYS);
			SplitPane.setResizableWithParent(getAccordionField(), false);

		}
		return mainPane;
	}

	public VBox getLeftPane() {

		if (leftPane == null) {

			leftPane = new VBox();
			leftPane.getChildren().addAll(getAccordionField(), getPanePreview());
			leftPane.setMaxWidth(200);

		}
		return leftPane;
	}

	////// Getters AccordionField & AccordionField Items

	public Accordion getAccordionField() {
		if (accordionField == null) {
			accordionField = new Accordion();
			accordionField.getPanes().addAll(getPaneField());
			accordionField.setExpandedPane(getPaneField());

		}
		return accordionField;
	}

	public TitledPane getPaneField() {
		if (paneField == null) {
			paneField = new TitledPane();
			paneField.setText("List");
			paneField.setCollapsible(false);
			paneField.setContent(getTabPaneField());

		}
		return paneField;
	}

	public VBox getPanePreview() {

		if (panePreview == null) {
			panePreview = new VBox();
			panePreview.setStyle("-fx-background-color:#000");
			panePreview.setMinWidth(200);
			panePreview.setMinHeight(200);
			paneField.setAlignment(Pos.CENTER);
		}
		return panePreview;
	}

	public TableView<Field> getTableField() {

		if (tableField == null) {

			tableField = new TableView<Field>();

			TableColumn<Field, Integer> colId = new TableColumn<Field, Integer>("#");
			colId.setCellValueFactory(new PropertyValueFactory<Field, Integer>("id"));
			colId.setPrefWidth(30);

			TableColumn<Field, String> colName = new TableColumn<Field, String>("Field");
			colName.setCellValueFactory(new PropertyValueFactory<Field, String>("name"));
			colName.setPrefWidth(70);

			tableField.getColumns().add(colId);
			tableField.getColumns().add(colName);
			tableField.setPrefWidth(114);

		}
		return tableField;
	}

	public TreeView<String> getTreeField() {
		if (treeField == null) {
			treeField = new TreeView<String>();
		}
		return treeField;
	}

	////// Getters TabPaneEditor & TabPaneEditor Items

	public TabPane getTabPaneEditor() {

		if (tabPaneEditor == null) {
			tabPaneEditor = new TabPane();
			tabPaneEditor.getTabs().addAll(getTabTexts(), getTabTutorials());
		}
		return tabPaneEditor;
	}

	public TabPane getTabPaneField() {

		if (tabPaneField == null) {
			tabPaneField = new TabPane();

			Tab tabPlaces = new Tab();
			tabPlaces.setText("Places");
			tabPlaces.setContent(getTreeField());
			tabPlaces.setClosable(false);

			Tab tabFiles = new Tab();
			tabFiles.setText("Files");
			tabFiles.setContent(getTableField());
			tabFiles.setClosable(false);

			tabPaneField.getTabs().addAll(tabPlaces, tabFiles);
			tabPaneField.setSide(Side.BOTTOM);
		}
		return tabPaneField;
	}

	public Tab getTabTexts() {

		if (tabTexts == null) {

			VBox vboxEditor = new VBox(10);
			vboxEditor.setFillWidth(true);
			vboxEditor.getChildren().addAll(getVboxTextEdit(), getVboxPreview());
			VBox.setVgrow(getVboxTextEdit(), Priority.ALWAYS);

			HBox hboxEditor = new HBox();
			hboxEditor.getChildren().addAll(getVboxTextList(), vboxEditor);
			hboxEditor.setFillHeight(true);
			HBox.setMargin(getVboxTextList(), new Insets(15));
			HBox.setMargin(vboxEditor, new Insets(15, 15, 15, 0));
			HBox.setHgrow(vboxEditor, Priority.ALWAYS);

			tabTexts = new Tab();
			tabTexts.setText("Texts & Dialogues");
			tabTexts.setContent(hboxEditor);
			tabTexts.setClosable(false);
		}
		return tabTexts;
	}

	public VBox getVboxTextEdit() {
		if (vboxTextEdit == null) {
			Label labelTextEdit = new Label("Editor");
			labelTextEdit.setFont(new Font("System", 16));

			vboxTextEdit = new VBox();
			vboxTextEdit.getChildren().addAll(labelTextEdit, getTextAreaEdit());
			VBox.setVgrow(getTextAreaEdit(), Priority.ALWAYS);
		}
		return vboxTextEdit;
	}

	public TextArea getTextAreaEdit() {
		if (textAreaEdit == null) {
			textAreaEdit = new TextArea();
			textAreaEdit.setFont(new Font("System", 14));
		}
		return textAreaEdit;
	}

	public VBox getVboxTextList() {
		if (vboxTextList == null) {

			Label labelTextList = new Label("List");
			labelTextList.setFont(new Font("System", 16));

			vboxTextList = new VBox();
			vboxTextList.setPrefWidth(200);

			GridPane gridPanel = new GridPane();
			gridPanel.add(getButtonCleanScript(), 1, 1);
			gridPanel.add(getButtonRearrangeScript(), 2, 1);
			gridPanel.setVgap(5);
			gridPanel.setHgap(3);
			vboxTextList.getChildren().addAll(labelTextList, getTableText(), gridPanel);

			VBox.setVgrow(getTableText(), Priority.ALWAYS);
		}
		return vboxTextList;
	}

	public TableView<Text> getTableText() {

		if (tableText == null) {

			tableText = new TableView<Text>();

			TableColumn<Text, Integer> colMsg = new TableColumn<Text, Integer>("Description");
			TableColumn<Text, String> colUsed = new TableColumn<Text, String>("[=]");
			TableColumn<Text, Integer> colNumMsg = new TableColumn<Text, Integer>("Msg.");

			colUsed.setCellValueFactory(new PropertyValueFactory<Text, String>("used"));
			colMsg.setCellValueFactory(new PropertyValueFactory<Text, Integer>("description"));
			colNumMsg.setCellValueFactory(new PropertyValueFactory<Text, Integer>("numMessages"));

			tableText.getColumns().add(colUsed);
			tableText.getColumns().add(colMsg);
			tableText.getColumns().add(colNumMsg);

			colUsed.setPrefWidth(30);
			colMsg.setPrefWidth(106);
			colNumMsg.setPrefWidth(50);

		}
		return tableText;
	}

	public Button getButtonCleanScript() {
		if (buttonCleanScript == null) {
			buttonCleanScript = new Button("Clean All Unused");
		}
		return buttonCleanScript;
	}

	public Button getButtonRearrangeScript() {
		if (buttonRearrangeScript == null)
			buttonRearrangeScript = new Button("Rearrange All");
		return buttonRearrangeScript;
	}

	public VBox getVboxPreview() {

		if (vboxPreview == null) {

			Label labelPreview = new Label("Preview");
			labelPreview.setFont(new Font("System", 16));

			vboxPreview = new VBox();
			vboxPreview.setMinHeight(280);
			vboxPreview.getChildren().addAll(labelPreview, getDialogueEditor());
			VBox.setVgrow(dialogueEditor, Priority.ALWAYS);
		}
		return vboxPreview;
	}

	public DialogEditorPane getDialogueEditor() {
		if (dialogueEditor == null) {
			dialogueEditor = new DialogEditorPane();
		}
		return dialogueEditor;
	}

	public Tab getTabEventScritps() {

		if (tabEventScritps == null) {
			tabEventScritps = new Tab();
			tabEventScritps.setText("Event Scritps");
			tabEventScritps.setClosable(false);
			tabEventScritps.setDisable(true);
		}
		return tabEventScritps;
	}

	public Tab getTabTutorials() {
		if (tabTutorials == null) {

			HBox hboxEditor = new HBox(10);
			hboxEditor.getChildren().addAll(getVboxTutorialList(), getVboxTutorialEdit());
			hboxEditor.setFillHeight(true);
			HBox.setMargin(getVboxTutorialList(), new Insets(15, 5, 15, 15));
			HBox.setMargin(getVboxTutorialEdit(), new Insets(15, 15, 15, 0));
			HBox.setHgrow(getVboxTutorialEdit(), Priority.ALWAYS);

			tabTutorials = new Tab();
			tabTutorials.setText("Tutorials");
			tabTutorials.setContent(hboxEditor);
			tabTutorials.setClosable(false);
		}
		return tabTutorials;
	}

	public TextArea getTutorialAreaEdit() {
		if (tutorialAreaEdit == null) {
			tutorialAreaEdit = new TextArea();
		}
		return tutorialAreaEdit;
	}

	public VBox getVboxTutorialList() {

		if (vboxTutorialList == null) {
			Label labelTutorialList = new Label("List");
			labelTutorialList.setFont(new Font("System", 16));

			vboxTutorialList = new VBox();
			vboxTutorialList.setPrefWidth(200);
			vboxTutorialList.getChildren().addAll(labelTutorialList, getTutorialList());
			VBox.setVgrow(getTutorialList(), Priority.ALWAYS);
		}
		return vboxTutorialList;
	}

	public ListView<Tutorial> getTutorialList() {
		if (tutorialList == null) {
			tutorialList = new ListView<>();
		}
		return tutorialList;
	}

	public VBox getVboxTutorialEdit() {
		if (vboxTutorialEdit == null) {
			Label labelTutorialEdit = new Label("Editor");
			labelTutorialEdit.setFont(new Font("System", 16));
			vboxTutorialEdit = new VBox();
			vboxTutorialEdit.getChildren().addAll(labelTutorialEdit, getTutorialAreaEdit());
			VBox.setVgrow(getTutorialAreaEdit(), Priority.ALWAYS);
		}
		return vboxTutorialEdit;
	}

	//// Getter StatusPane & StatusPane Items

	public AnchorPane getStatusPane() {
		if (statusPane == null) {
			statusPane = new AnchorPane();
			statusPane.getChildren().addAll(getStatuslabel(), getProgressBar());
			AnchorPane.setRightAnchor(getProgressBar(), .0);
		}
		return statusPane;
	}

	public Label getStatuslabel() {
		if (statuslabel == null)
			statuslabel = new Label();
		return statuslabel;
	}

	public ProgressBar getProgressBar() {
		if (progresBar == null) {
			progresBar = new ProgressBar(0);
			progresBar.setPrefHeight(20);
			progresBar.setVisible(false);
		}
		return progresBar;
	}

	public DirectoryChooser getDirFieldChooser() {
		if (dirFieldChooser == null) {
			dirFieldChooser = new DirectoryChooser();
			dirFieldChooser.setInitialDirectory(new File("/FFVII"));
		}
		return dirFieldChooser;
	}

	public FileChooser getFieldChooserISO() {

		if (fileChooserISO == null) {
			fileChooserISO = new FileChooser();

			ArrayList<String> extList = new ArrayList<>();
			extList.add("*.img");
			extList.add("*.bin");

			fileChooserISO.getExtensionFilters().add(new ExtensionFilter("ISO 9660 files", extList));

			File file = new File("/FFVII");

			if (file.exists())
				fileChooserISO.setInitialDirectory(file);
		}

		return fileChooserISO;
	}

	public FileChooser getFieldChooserText() {

		if (fileChooserText == null) {
			fileChooserText = new FileChooser();

			ArrayList<String> extList = new ArrayList<>();
			extList.add("*.xml");

			fileChooserText.getExtensionFilters().add(new ExtensionFilter("XML files", extList));

			File file = new File("/FFVII");

			if (file.exists())
				fileChooserText.setInitialDirectory(file);
		}

		return fileChooserText;
	}

	public Stage getPrimaryStage() {
		return primaryStage;
	}

	public static void main(String[] args) {
		launch(args);
	}

}
