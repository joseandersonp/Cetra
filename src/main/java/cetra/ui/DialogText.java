package cetra.ui;

import java.util.ArrayList;

import cetra.field.tex.Font;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.geometry.Pos;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

public class DialogText {

	private Font font;
	private int tabX = 0;
	private int marginLeft=8;
	private int marginTop=4;
	private int buttonMarginTop=4;
	private int choiceMarginTop=2;
	
	public ArrayList<StackPane> createText(byte[] buffer, int firstLineChoice) {

		ArrayList<StackPane> panes = createText(buffer);

		if (panes != null && panes.size() > 0) {
			StackPane pane = panes.get(panes.size() - 1);
			ImageView imgChoice = genImageChar(0xE0, 7, -3, firstLineChoice * 16 + choiceMarginTop);
			pane.getChildren().add(imgChoice);
		}
		return panes;
		
	}

	public ArrayList<StackPane> createText(byte[] buffer) {

		ArrayList<StackPane> panes = new ArrayList<>();

		StackPane pane = new StackPane();
		pane.setAlignment(Pos.TOP_LEFT);
		pane.setTranslateX(marginLeft-4);
		pane.setTranslateY(marginTop-4);
		panes.add(pane);

		int x = 0;
		int y = 0;
		int paneWidth = 0;
		int paneHeight = 0;
		int indexColor = 7;
		int startColor = 0;
		font.setSpacedChars(false);

		ArrayList<ImageView> multicolorFrames = new ArrayList<>();
		ArrayList<ImageView> blinkFrames = new ArrayList<>();

		int startx = 0;
		int difWidthName = 0;
		int maxWidthName = font.getCharWidth(0x37) * 7; // 9W
		boolean enabledWidthDifName = false;

		for (int i = 0; i < buffer.length; i++) {

			int uByte = ((int) buffer[i]) & 0xFF;

			switch (uByte) {

			case 0xE0:
				x += font.getFullCharWidth(0xE0);
				break;

			case 0xE1:

				x += font.getFullCharWidth(0) * tabX;
				break;

			case 0xE8:

				pane = new StackPane();
				pane.setAlignment(Pos.TOP_LEFT);
				pane.setTranslateX(4);
				pane.setTranslateY(2);
				panes.add(pane);
				indexColor = 7;

				y += 16;

				x += difWidthName;
				if (paneHeight < y)
					paneHeight = y;

				if (paneWidth < x)
					paneWidth = x;

				x = 0;
				y = 0;
				difWidthName = 0;
				break;

			case 0xEA:
			case 0xEB:
			case 0xEC:
			case 0xED:
			case 0xEE:
			case 0xEF:
			case 0xF0:
			case 0xF1:
			case 0xF2:

				if (enabledWidthDifName) {
					difWidthName += maxWidthName - (x - startx);
					enabledWidthDifName = false;
				} else {
					enabledWidthDifName = true;
					startx = x;
				}
				break;

			case 0xE7:
			case 0xFF:

				y += 16;
				x += difWidthName;
				if (paneHeight < y)
					paneHeight = y;

				if (paneWidth < x)
					paneWidth = x;

				x = 0;
				difWidthName = 0;

				break;

			case 0xFE:

				uByte = ((int) buffer[++i]) & 0xFF;

				switch (uByte) {

				case 0xDC:
					break;

				case 0xDE:
				case 0xDF:

					pane.getChildren().add(genImageChar(0x10, indexColor, x, y));
					x += font.getCharWidth(0x10);
					break;

				case 0xE1:
					x += 8;
					pane.getChildren().add(genImageChar(0x10, indexColor, x, y));
					x += 7;
					break;

				case 0xE9:
					font.toggleSpacedChars();
					break;
					
				case 0xE2:
					
					i+=3;
					int lo = buffer[i++];
					int hi = buffer[i];
					int sSize = hi << 8 | lo; 					
					for (int j = 0; j < sSize; j++) {
						pane.getChildren().add(genImageChar(0x38, indexColor, x, y));
						x += font.getCharWidth(0x38);
					}
								
					
				break;

				default:
					indexColor = uByte - 0xD2;
				}

				break;

			case 0x00:
				x += font.getFullCharWidth(uByte);
				break;

			default:

				int charWidth = font.getCharWidth(uByte);
				x += Font.getLeftWidth(charWidth);

				if (indexColor == 8) {

					ImageView imageChar = genImageChar(uByte, 0, x, y);
					pane.getChildren().add(imageChar);
					blinkFrames.add(imageChar);

					imageChar = genImageChar(uByte, 7, x, y);
					pane.getChildren().add(imageChar);
					blinkFrames.add(imageChar);

				} else if (indexColor == 9) {

					int currentColor = startColor;

					for (int j = 0; j < 8; j++) {

						if (currentColor < 0)
							currentColor = 7;

						ImageView imageChar = genImageChar(uByte, currentColor, x, y);
						pane.getChildren().add(imageChar);
						multicolorFrames.add(imageChar);
						currentColor--;
					}
					startColor--;
					if (startColor < 0) {
						startColor = 7;
					}

				} else {
					pane.getChildren().add(genImageChar(uByte, indexColor, x, y));
				}

				x += Font.getRightWidth(charWidth);
				break;
			}
		}

		for (StackPane p : panes) {
			p.setPrefSize(paneWidth + 17, paneHeight + 9);
		}

		animateChars(multicolorFrames, 8, 500);
		animateChars(blinkFrames, 2, 500);
		return panes;
	}

	private void animateChars(ArrayList<ImageView> frames, int charFrames, int timeFrame) {

		Timeline timeline = new Timeline();
		int time = 0;

		for (int i = 0; i < frames.size(); i += charFrames) {
			time = timeFrame;
			for (int j = i + charFrames - 1; j >= i; j--) {
				ImageView imageView = frames.get(j);
				timeline.getKeyFrames().add(new KeyFrame(new Duration(time), new KeyValue(imageView.visibleProperty(), false)));
				time += timeFrame;
			}
		}
		timeline.setCycleCount(Timeline.INDEFINITE);
		timeline.play();
	}

	private ImageView genImageChar(int codeChar, int indexColor, int x, int y) {
		ImageView imgChar = font.getChar(codeChar, indexColor);
		imgChar.setTranslateX(x);
		if (codeChar >= 0xF6 && codeChar <= 0xF9)
			y = (y-4) + buttonMarginTop;
		imgChar.setTranslateY(y);
		return imgChar;
	}

	public Font getFont() {
		return font;
	}

	public void setFont(Font font) {
		this.font = font;
	}
	
	public void setTabX(int tabX) {
		this.tabX = tabX;
	}
}