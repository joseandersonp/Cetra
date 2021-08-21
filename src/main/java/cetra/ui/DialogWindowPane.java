package cetra.ui;

import java.util.ArrayList;

import javafx.geometry.Pos;
import javafx.scene.layout.StackPane;

public class DialogWindowPane extends StackPane {
	
	private ArrayList<String> styleMode;
	private int mode;
	
	public DialogWindowPane() {
		initalize();
	}
	
	private void initalize() {
		
		styleMode = new ArrayList<>();
		styleMode.add("-fx-border-image-source: url('images/dialogueBorder.png'); -fx-border-image-slice: 4; -fx-border-image-width: 4px ;-fx-border-image-repeat: round ;-fx-background-radius: 5px; -fx-background-color: linear-gradient(to bottom right, #0000B0, #000020);");
		styleMode.add("-fx-background-fills: null; -fx-image-borders:null;");
		styleMode.add("-fx-border-image-source: url('images/dialogueBorder.png'); -fx-border-image-slice: 4; -fx-border-image-width: 4px ;-fx-border-image-repeat: round ;-fx-background-radius: 5px; -fx-background-color: linear-gradient(to bottom right, rgba(0,0,176,0.6), rgba(0,0,32,0.6));");
		
		this.setAlignment(Pos.TOP_LEFT);
	}

	public void setMode(int mode) {
		this.setStyle(styleMode.get(mode));
		this.mode = mode;
	}
	
	public int getMode() {
		return mode;
	}
}