package cetra.io;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;

import cetra.compression.Gzip;
import cetra.field.tex.Font;
import cetra.field.tex.Palette;
import cetra.field.tex.Texture;
import cetra.field.tex.TextureFormat;
import javafx.scene.image.Image;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;

public class WindowReader {

	private Font font = new Font();

	public WindowReader(byte[] data) throws IOException {
		init(data);
	}

	public void init(byte[] rawData) throws IOException {

		ByteBuffer buffer = ByteBuffer.wrap(rawData);
		buffer.order(ByteOrder.LITTLE_ENDIAN);
		buffer.position(0);

		byte[][] sectionData = new byte[3][];
		
		for (int i = 0; i < sectionData.length; i++) {

			int dataLength = buffer.getShort() & 0xFFFF;
			buffer.getInt();

			byte dataComp[] = new byte[dataLength];
			buffer.get(dataComp);

			ByteArrayInputStream bin = new ByteArrayInputStream(dataComp);
			ByteArrayOutputStream bout = new ByteArrayOutputStream();

			Gzip.decode(bin, bout);

			sectionData[i] = bout.toByteArray();

		}

		Palette pal = new Palette();

		// Wraps section 1 in buffer;
		
		buffer = ByteBuffer.wrap(sectionData[1]);
		buffer.order(ByteOrder.LITTLE_ENDIAN);
		buffer.position(12);

		pal.setX(buffer.getShort() & 0xFFFF);
		pal.setY(buffer.getShort() & 0xFFFF);		
		pal.setWidth(buffer.getShort() & 0xFFFF);
		pal.setHeight(buffer.getShort() & 0xFFFF);
		
		
		int colors[][] = new int[pal.getHeight()][pal.getWidth()];
		for (int i = 0; i < pal.getHeight(); i++) 
			for (int j = 0; j < pal.getWidth(); j++) 
				colors[i][j] = buffer.getShort() & 0xFFFF;				
				
		pal.setPaletes(colors);

		Texture textF = new Texture();
		textF.setSize(buffer.getInt() & 0xFFFFFFFFL);		
		textF.setX(buffer.getShort() & 0xFFFF);		
		textF.setY(buffer.getShort() & 0xFFFF);		
		textF.setWidth(buffer.getShort() & 0xFFFF);		
		textF.setHeight(buffer.getShort() & 0xFFFF);		

		int texLenght = buffer.limit() - buffer.position();
		byte[] dataTex = new byte[texLenght];
		buffer.get(dataTex);		

		textF.setBlockData(dataTex);

		ArrayList<Image> fontImages = new ArrayList<>();

		int w = textF.getWidth() * 4;
		int h = textF.getHeight();
		for (int j = 0; j < colors.length; j++) {
			byte[] pixelTex = TextureFormat.buffer4bppTo32Bgra(dataTex, colors[j]);
			WritableImage writableImage = new WritableImage(w, h);
			PixelWriter pixelWriter = writableImage.getPixelWriter();
			pixelWriter.setPixels(0, 0, w, h, PixelFormat.getByteBgraInstance(), pixelTex, 0, w * 4);
			fontImages.add(writableImage);
		}
		
		font.setFontImages(fontImages);

		ArrayList<Integer> charWidths = new ArrayList<Integer>();
		for (int j = 0; j < sectionData[2].length; j++) 
			charWidths.add(sectionData[2][j] & 0xFF);
		
		font.setCharWidths(charWidths);
	}

	public Font getFont() {
		return font;
	}

}
