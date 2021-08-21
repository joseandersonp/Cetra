package cetra.field.tex;

import java.io.IOException;
import java.util.ArrayList;

import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Font {
		
	private Image imgFontext;
	private ArrayList<Integer> charWidths;

	private ArrayList<Rectangle2D> charTiles;
	private ArrayList<Image> fontImages;
	
	private boolean spacedChars;
	
	public void initialize() throws IOException{
		
		String urlFontext = getClass().getResource("/images/fontext.png").toString();
		
		imgFontext = new Image(urlFontext);
		charTiles = new ArrayList<>();

		/* Original version US rows=12;cols=21;tileSize=12 */
		int rows  = 16;
		int cols  = 16;		

		int tileSize = 16;

		int x = 0;
		int y = 0;

		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				charTiles.add(new Rectangle2D(x, y, tileSize, tileSize));
				x += tileSize;
			}
			x = 0;
			y += tileSize;
		}		

		tileSize = 16;
		x=0;

		for (int i = 0xF6; i < 0xFA; i++) {
			charTiles.set(i, new Rectangle2D(x, 0, tileSize, tileSize));
			charWidths.set(i, tileSize);
			x+=tileSize;
		}
		
		// Choice
		charTiles.set(0xE0, new Rectangle2D(64, 0, 24, tileSize));
		charWidths.set(0xE0, 22);
		
	}

	public ImageView getChar(int index, int indexColor) {

		ImageView imageV;

		if (index >= 0xF6 && index <=0xF9 || index == 0xE0)
			imageV = new ImageView(imgFontext);
		else
			imageV = new ImageView(fontImages.get(indexColor));

		imageV.setViewport(charTiles.get(index));
		return imageV;

	}

	public int getCharWidth(int index) {
		if (spacedChars){
			return 13;
		}
		return charWidths.get(index);
	}
	
	public int getFullCharWidth(int index) {
		int charWidth = getCharWidth(index);
		return getLeftWidth(charWidth) + getRightWidth(charWidth);
	}
	
	public static int getLeftWidth(int charWidth) {
		return (charWidth & 0b11100000) >> 5;
	}
	
	public static int getRightWidth(int charWidth) {
		return charWidth & 0b00011111;
	}
	
	public void toggleSpacedChars() {
		spacedChars = !spacedChars;
	}

	public void setSpacedChars(boolean b) {
		spacedChars = false;
	}
	
	public void setCharWidths(ArrayList<Integer> charWidths) {
		this.charWidths = charWidths;
	}
	
	public void setFontImages(ArrayList<Image> fontImages) {
		this.fontImages = fontImages;
	}
	
	public ArrayList<Image> getFontImages() {
		return fontImages;
	}
}

