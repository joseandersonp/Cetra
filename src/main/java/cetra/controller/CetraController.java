package cetra.controller;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import cetra.compression.Gzip;
import cetra.field.script.EOpcode;
import cetra.field.script.EventScript;
import cetra.field.script.Opcode;
import cetra.field.script.Tutorial;
import cetra.field.tex.Font;
import cetra.io.DatReader;
import cetra.io.DatWriter;
import cetra.io.MimReader;
import cetra.io.WindowReader;
import cetra.iso.ISOHandler;
import cetra.iso.record.DirectoryRecord;
import cetra.model.Field;
import cetra.model.Message;
import cetra.model.Text;
import cetra.model.Window;
import cetra.model.WindowMode;
import cetra.ui.CetraUI;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;

public class CetraController {

	private CetraUI cetraUI;
	private String rootPath;

	private ISOHandler isoHandler;

	public CetraController(CetraUI cetraUI) {
		this.cetraUI = cetraUI;
	}

	public ISOHandler getIsoHandler() {
		return isoHandler;
	}

	public void openText() {
		
		if (cetraUI.getTableText().getSelectionModel().getSelectedItem() != null) {

			Text text = cetraUI.getTableText().getSelectionModel().getSelectedItem();
			cetraUI.getDialogueEditor().setMessages(text.getMessages());
			cetraUI.getDialogueEditor().initialize();
			cetraUI.getTextAreaEdit().setText(text.toString());

		}
		
	}

	public void updateText() {

		if (cetraUI.getTableText().getSelectionModel().getSelectedItem() != null) {

			Text text = cetraUI.getTableText().getSelectionModel().getSelectedItem();
			text.setText(cetraUI.getTextAreaEdit().getText());
			cetraUI.getDialogueEditor().refresh();

		}
	}

	public void updateTutorial() {

		Tutorial tutorial = cetraUI.getTutorialList().getSelectionModel().getSelectedItem();
		tutorial.setScript(cetraUI.getTutorialAreaEdit().getText());
	}

	public void exportFieldScript() {

		Field field = cetraUI.getTableField().getSelectionModel().getSelectedItem();

		cetraUI.getFieldChooserText().setInitialFileName(field.getName());
		File fileText = cetraUI.getFieldChooserText().showSaveDialog((cetraUI.getPrimaryStage()));

		if (fileText != null) {
			try {
				JAXBContext jaxbContext = JAXBContext.newInstance(Field.class);
				Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
	
				jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
	
				jaxbMarshaller.marshal(field, fileText);
	
			} catch (JAXBException e) {
				cetraUI.showAlertDialog("Error!", "Field Script", "Failed to export Field Script File\nError:" + e.getMessage(), AlertType.ERROR);
			}
		}

	}

	public void importFieldText() {

		Field field = cetraUI.getTableField().getSelectionModel().getSelectedItem();
		cetraUI.getFieldChooserText().setInitialFileName(field.getName());
		File fileText = cetraUI.getFieldChooserText().showOpenDialog((cetraUI.getPrimaryStage()));

		try {

			JAXBContext context = JAXBContext.newInstance(Field.class);
			Unmarshaller unmarshaller = context.createUnmarshaller();
			Field fieldS = (Field) unmarshaller.unmarshal(fileText);

			ObservableList<Text> selectedItems = cetraUI.getTableText().getItems();
			for (Text text : selectedItems) {
				for (Text textS : fieldS.getTexts()) {
					if (textS.getId() == text.getId()) {
						// System.out.println(text.getId() +" "+ text.getDialog());
						// System.out.println(textS.getId() +" "+ textS.getDialog());
						text.setDialog(textS.getDialog());
					}
				}
			}
		} catch (JAXBException ex) {
			ex.printStackTrace();
		}

		openText();
	}

	public void loadXMLDatPlaces() {

		File file = new File("dat_files.xml");
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		try {

			TreeView<String> treeView = cetraUI.getTreeField();

			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(file);

			doc.getDocumentElement().normalize();

			Element root = doc.getDocumentElement();
			NodeList childsRoot = root.getElementsByTagName("place");
			TreeItem<String> tRoot = new TreeItem<String>();
			for (int x = 0; x < childsRoot.getLength(); x++) {
				Element eRoot = (Element) childsRoot.item(x);
				TreeItem<String> tPlace = new TreeItem<String>(eRoot.getAttribute("name"));
				NodeList childsPlace = eRoot.getElementsByTagName("file");
				for (int y = 0; y < childsPlace.getLength(); y++) {
					Element eField = (Element) childsPlace.item(y);
					TreeItem<String> tField = new TreeItem<String>(eField.getAttribute("name").toUpperCase());
					tPlace.getChildren().add(tField);
				}

				tRoot.getChildren().add(tPlace);
			}

			treeView.setRoot(tRoot);
			treeView.setShowRoot(false);

		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void selectFieldFree() {

		TreeItem<String> selectedItem = cetraUI.getTreeField().getSelectionModel().getSelectedItem();
		String fieldName = selectedItem.getValue();

		ObservableList<Field> items = cetraUI.getTableField().getItems();
		for (Field field : items) {
			if (field.getName().equalsIgnoreCase(fieldName)) {
				cetraUI.getTableField().getSelectionModel().select(field);
				break;
			}
		}

	}

	public void openField() {

		Field field = cetraUI.getTableField().getSelectionModel().getSelectedItem();
		cetraUI.getTabPaneEditor().getSelectionModel().select(0);
		try {

			if (field.isInitialized()) {

				cetraUI.getTabTutorials().setDisable(field.getTutorials() == null);
				cetraUI.getTutorialList().setItems((ObservableList<Tutorial>) field.getTutorials());
				cetraUI.getTutorialList().getSelectionModel().selectFirst();
				cetraUI.getTableText().setItems((ObservableList<Text>) field.getTexts());
				cetraUI.getTableText().getSelectionModel().selectFirst();

				cetraUI.getPanePreview().getChildren().clear();
				cetraUI.getPanePreview().setAlignment(Pos.CENTER);
				cetraUI.getPanePreview().getChildren().add(new ImageView(field.getBkgImage()));

				return;
			}

			DatReader reader = new DatReader(field.getFile());
			field.initialize(reader);

			if (field.getScript().getDialog() == null) {
				cetraUI.getTableText().setItems(null);
			} else {

				EventScript[][] tableEventScripts = field.getScript().getEvent().getTableEventScripts();
				ObservableList<Text> dataText = FXCollections.observableArrayList();

				HashMap<Integer, Text> texts = field.getScript().getDialog().getTexts();

				for (int i = 0; i < texts.size(); i++) {
					dataText.add(texts.get(i));
				}

				HashMap<Integer, Window> windows = new HashMap<>();
				HashMap<Integer, WindowMode> wModes = new HashMap<>();

				for (int i = 0; i <= 3; i++) {

					Window win = createDefaultWindow();
					WindowMode wMode = new WindowMode(i, 0);

					win.setId(i);
					win.setWindowMode(wMode);

					windows.put(i, win);
					wModes.put(i, wMode);
				}
				windows.get(1).setY(8);

				int idWindow = 0;

				List<Window> windowsCache = new ArrayList<>();

				for (int i = 0; i < tableEventScripts.length; i++) {

					for (int j = 0; j < tableEventScripts[i].length; j++) {

						if (tableEventScripts[i][j] != null) {

							ArrayList<Opcode> script = tableEventScripts[i][j].getScript();

							for (Opcode opcode : script) {

								switch (opcode.getOpcode()) {

								case WREST:

									idWindow = opcode.getBinCode()[1];
									wModes.put(idWindow, new WindowMode(idWindow, 0));
									break;

								case WMODE:

									idWindow = opcode.getBinCode()[1];
									int mode = opcode.getBinCode()[2];
									wModes.put(idWindow, new WindowMode(idWindow, mode));

									break;

								case MPNAM:

									Message msgf = new Message(opcode);
									Text textf = dataText.get(msgf.getTextId());
									msgf.setText(textf);
									textf.addMessage(msgf);

									if (textf.getData().size() >= 24) {
										String warningMessage = String.format(
												"WARNING! Location Name exceeded: [%s], size: %d bytes (limit:23 bytes)",
												textf.getDialog(), textf.getData().size());
										cetraUI.getStatuslabel().setText(warningMessage);
										cetraUI.showAlertDialog(
												"Warning!", "Long Location Name Size", "The location name \""
														+ textf.getDialog() + "\" exceeded the 23-byte size limit.",
												AlertType.WARNING);
									}
									break;

								case WINDOW:
								case WSIZW:

									idWindow = opcode.getBinCode()[1];
									Window window = new Window(opcode, wModes.get(idWindow));
									windows.put(idWindow, window);
									windowsCache.add(window);
									break;

								case ASK:
								case MESSAGE:

									Message msg = new Message(opcode);

									for (Window win1 : windowsCache)
										if (msg.getWindowId() == win1.getId())
											msg.addWindow(win1);
									for (Window win2 : msg.getWindows())
										windowsCache.remove(win2);

									if (msg.getWindows().isEmpty()) {
										msg.addWindow(windows.get(msg.getWindowId()));
									}

									// System.out.println(msg.getTextId());
									Text text = dataText.get(msg.getTextId());
									msg.setText(text);
									text.addMessage(msg);

									wModes.put(msg.getWindowId(), new WindowMode(msg.getWindowId(), 0));
									break;

								default:
									break;
								}

							}
						}
					}
				}

				cetraUI.getTableText().setItems(dataText);
				cetraUI.getTableText().getSelectionModel().selectFirst();
				field.setTexts(dataText);

				cetraUI.getTabTutorials().setDisable(true);

				if (field.getScript().getMisc() != null && field.getScript().getMisc().getTutorials().size() > 0) {

					cetraUI.getTabTutorials().setDisable(false);

					ObservableList<Tutorial> tutorials = FXCollections.observableArrayList();
					for (Tutorial tutorial : field.getScript().getMisc().getTutorials()) {
						tutorials.add(tutorial);
					}
					cetraUI.getTutorialList().setItems(tutorials);
					cetraUI.getTutorialList().getSelectionModel().selectFirst();
					field.setTutorials(tutorials);
				}
			}

			MimReader mimReader = new MimReader(field);
			mimReader.loadLayers();
			mimReader.paintCanvas();

			WritableImage image = mimReader.getImage();
			ImageView imageV = new ImageView(image);
			imageV.setPreserveRatio(true);

			if (image.getWidth() > image.getHeight()) {
				imageV.fitWidthProperty().bind(cetraUI.getPanePreview().widthProperty());
			} else {
				imageV.fitHeightProperty().bind(cetraUI.getPanePreview().heightProperty());
			}

			cetraUI.getPanePreview().getChildren().clear();
			cetraUI.getPanePreview().setAlignment(Pos.CENTER);
			cetraUI.getPanePreview().getChildren().add(imageV);
			field.setBkgImage(imageV.snapshot(null, null));

		} catch (Exception e) {
			cetraUI.showAlertDialog("Error!", "Load Dat File",
					"Failed to open file \"" + field.getName() + "\"\nError: " + e.getMessage(), AlertType.ERROR);
		}
	}

	public void saveAllFieldsInISO() {
		try {
			isoHandler.writeFilesInField(new File(rootPath + "\\FIELD"));
			cetraUI.showAlertDialog("Information", "Save All Data", "The ISO was saved successfully!",
					AlertType.INFORMATION);
		} catch (IOException e) {
			cetraUI.showAlertDialog("Error!", "Save All Data",
					"\"An error occurred while saving the changes in ISO File\nError: " + e.getMessage(),
					AlertType.ERROR);
		}
	}

	public void saveField() {

		Field field = cetraUI.getTableField().getSelectionModel().getSelectedItem();

		try {

			byte[] sectionScript = field.getScript().getBytes();

			DatWriter datWriter = new DatWriter(7);

			datWriter.addSection(sectionScript);
			datWriter.addSection(field.getWalkmesh());
			datWriter.addSection(field.getTileMap());
			datWriter.addSection(field.getCameraMatrix());
			datWriter.addSection(field.getTriggers());
			datWriter.addSection(field.getEncounter());
			datWriter.addSection(field.getModels());

			datWriter.writeFile(field);

			cetraUI.showAlertDialog("Information", "Save Dat File",
					"The file '" + field.getFile().getName() + "' was saved successfully!", AlertType.INFORMATION);
		} catch (Exception e) {
			cetraUI.showAlertDialog("Error!", "Save Dat File", "\"An error occurred while saving the changes in file '"
					+ field.getFile().getName() + "'\nError: " + e.getMessage(), AlertType.ERROR);
		}
	}

	public void openISOImage() {

		File isoFile = cetraUI.getFieldChooserISO().showOpenDialog((cetraUI.getPrimaryStage()));

		if (isoFile != null) {

			cetraUI.getPrimaryStage().setTitle(CetraUI.APPLICATION_NAME + " - " + isoFile.getAbsolutePath());

			rootPath = isoFile.getAbsolutePath().replaceAll("\\.(.*)", "");

			try {

				isoHandler = new ISOHandler(isoFile);
				File fieldDir = new File(rootPath + "\\FIELD");
				boolean loadFieldCache = true;

				if (fieldDir.exists()) {
					if (fieldDir.isDirectory()) {
						if (fieldDir.listFiles().length == 2374)
							loadFieldCache = !cetraUI.showConfirmationDialog("Confirmation", "Load Cache Field Data",
									"Do you want to use existing data?");
					} else {
						fieldDir.delete();
						fieldDir.mkdir();
					}
				}

				if (loadFieldCache) {
					isoHandler.readFiles("FIELD", fieldDir.getAbsolutePath());
				}

				openFieldDir(fieldDir);
				loadXMLDatPlaces();
				openWindowData();

			} catch (IOException e) {
				cetraUI.showAlertDialog("Error!", "Load ISO Data",
						"Error to open ISO Image.\n Error: " + e.getMessage(), AlertType.ERROR);
			}

		}

	}

	private void openWindowData() throws IOException {

		DirectoryRecord directoryRecord = isoHandler.findDirectoryRecord("WINDOW(.+){3}");
		byte[] buffer = isoHandler.readExtent(directoryRecord);

		WindowReader windowReader = new WindowReader(buffer);

		directoryRecord = isoHandler.findDirectoryRecord("FIELD(.+){3}");
		buffer = isoHandler.readExtent(directoryRecord);

		ByteArrayInputStream bin = new ByteArrayInputStream(buffer);
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		bin.skip(8);

		try {
			Gzip.decode(bin, bout);
		} catch (EOFException e) {
			e.printStackTrace();
		}

		cetraUI.getDialogueEditor().getDialogText().setTabX(bout.toByteArray()[0x360E4]);

		Font font = windowReader.getFont();
		font.initialize();
		cetraUI.getDialogueEditor().getDialogText().setFont(font);

	}

	public void openFieldDir(File dirFieldDats) {

		try {

			if (dirFieldDats != null) {

				final File[] filesDat = dirFieldDats.listFiles(new FileFilter() {
					@Override
					public boolean accept(File pathname) {
						return pathname.getName().matches(".*\\.DAT");
					}
				});

				if (filesDat == null || filesDat.length == 0) {
					cetraUI.showAlertDialog("Warning!", "Load DAT Files",
							"DAT Files not found in " + dirFieldDats.getName() + "dir.", AlertType.WARNING);
					return;
				}

				Field.resetId();
				cetraUI.getProgressBar().setProgress(0);
				cetraUI.getProgressBar().setVisible(true);

				ObservableList<Field> dataField = FXCollections.observableArrayList();
				
				ByteBuffer buf4 = ByteBuffer.allocate(4);
				buf4.order(ByteOrder.LITTLE_ENDIAN);
				
				for (int i = 0; i < filesDat.length; i++) {
					
					try {

						FileInputStream in = new FileInputStream(filesDat[i]);
						in.getChannel().read(buf4);
						int fileLength = buf4.getInt(0) + 4;
						int countEOF = fileLength;
						buf4.clear();
						

						in.skip(fileLength - 4);
						while (in.available() > 0) {
							countEOF++;
							if (in.read() != 0)
								break;
						}
						
						in.close();
						
						fileLength = countEOF;

						// System.out.println(fileLength +" "+filesDat[i].length());
						// System.out.print("Process " + filesDat[i].getName() + "...");
						if (fileLength == filesDat[i].length()) {
							dataField.addAll(new Field(filesDat[i]));
							// System.out.println("\t[OK]");
						}  //else {
						//	 System.out.println(filesDat[i].getName() + "\t[UNCOMPRESS]");
						// }

					} catch (Exception e) {
						// System.out.println("\t[ERROR]");
						e.printStackTrace();
					}
					cetraUI.getProgressBar().setProgress((i + 1f) / filesDat.length);
				}
				cetraUI.getProgressBar().setVisible(false);
				cetraUI.getTableField().setItems(dataField);
				cetraUI.getTableField().getSelectionModel().selectFirst();

			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private Window createDefaultWindow() {
		Opcode op = new Opcode(EOpcode.WINDOW);
		op.initialize();
		Window win = new Window(op);
		win.setX(8);
		win.setY(149);
		win.setWidth(304);
		win.setHeight(73);
		return win;
	}

	public void selectDialogWindowPage() {

		Pattern p = Pattern.compile("(\\n\\{NEXT PAGE\\}\\n)");
		Matcher m = p.matcher(cetraUI.getTextAreaEdit().getText());
		m.region(0, cetraUI.getTextAreaEdit().getCaretPosition());

		int indexPage = 0;
		while (m.find()) {
			indexPage++;
		}

		cetraUI.getDialogueEditor().getLspnPage().setIndex(indexPage);

	}

	public void cleanScript() {
		ObservableList<Text> texts = cetraUI.getTableText().getItems();
		for (Text text : texts) {
			if (text.isUsed().equals("")) {
				text.setDialog("");
			}
		}

		openText();
	}

	public void rearrangeScript() {

		ObservableList<Text> texts = cetraUI.getTableText().getItems();

		texts.sort(new Comparator<Text>() {

			@Override
			public int compare(Text o1, Text o2) {

				if (o1.getNumMessages() == 0 && o2.getNumMessages() > 0) {
					return 1;
				}
				return 0;

			}

		});

		int count = 0;
		for (Text text : texts) {
			text.setId(count);
			if (text.getNumMessages() > 0) {
				for (Message message : text.getMessages()) {
					message.setTextId(count);
				}
			}
			count++;
		}
	}

	public String getRootPath() {
		return rootPath;
	}

	public void setRootPath(String rootPath) {
		this.rootPath = rootPath;
	}
}
