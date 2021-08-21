package cetra.ui;

import cetra.controller.CetraController;
import cetra.field.script.Tutorial;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.TreeItem;

public class CetraListener {

	private CetraUI cetraUI;
	private CetraController cetraController;

	public CetraListener(CetraUI cetraUI, CetraController cetraController) {
		this.cetraUI = cetraUI;
		this.cetraController = cetraController;
		initToolbarListeners();
		initTableListeners();
		initListListeners();
		initTextAreaListeners();
	}

	private void initToolbarListeners() {

		cetraUI.getButtonOpenISO().setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				cetraController.openISOImage();
			}
		});

		cetraUI.getButtonSave().setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				cetraController.saveField();
			}
		});

		cetraUI.getButtonSaveISO().setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				cetraController.saveAllFieldsInISO();
			}
		});

		cetraUI.getButtonImportText().setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				cetraController.importFieldText();
			}
		});

		cetraUI.getButtonExportText().setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				cetraController.exportFieldScript();
			}
		});

		cetraUI.getButtonCleanScript().setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				cetraController.cleanScript();
			}

		});

		cetraUI.getButtonRearrangeScript().setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				cetraController.rearrangeScript();
			}

		});

	}

	private void initTableListeners() {

		cetraUI.getTableField().getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				if (newValue.intValue() > -1)
					cetraController.openField();
			}
		});

		cetraUI.getTableText().getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {

			@Override
			public void changed(ObservableValue<? extends Number> arg0, Number arg1, Number arg2) {
				cetraController.openText();
			}
		});

		cetraUI.getTreeField().getSelectionModel().selectedItemProperty()
				.addListener(new ChangeListener<TreeItem<String>>() {

					@Override
					public void changed(ObservableValue<? extends TreeItem<String>> observable,
							TreeItem<String> oldValue, TreeItem<String> newValue) {
						cetraController.selectFieldFree();
					}
				});
	}

	private void initListListeners() {

		cetraUI.getTutorialList().getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Tutorial>() {

			@Override
			public void changed(ObservableValue<? extends Tutorial> observable, Tutorial oldValue, Tutorial newValue) {
				if (newValue != null)
					cetraUI.getTutorialAreaEdit().setText(newValue.getScript().toString().trim());
			}
		});
	}

	private void initTextAreaListeners() {
		cetraUI.getTextAreaEdit().textProperty().addListener(new ChangeListener<String>() {

			@Override
			public void changed(ObservableValue<? extends String> arg0, String arg1, String arg2) {
				cetraController.updateText();
			}
		});

		cetraUI.getTextAreaEdit().caretPositionProperty().addListener(new ChangeListener<Number>() {

			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				cetraController.selectDialogWindowPage();
			}
		});

		cetraUI.getTutorialAreaEdit().focusedProperty().addListener(new ChangeListener<Boolean>() {

			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				cetraController.updateTutorial();
			}
		});
	}	

}