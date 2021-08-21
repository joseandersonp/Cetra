package cetra.field.tex;

public class Background {

	private int width = 1280;
	private int height = 512;

	private Palette paletteData = new Palette();
	private Texture[] textureDatas;
	private byte[] textureArea = new byte[width * height];

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public Palette getPaletteData() {
		return paletteData;
	}

	public void setPaletteData(Palette paletteData) {
		this.paletteData = paletteData;
	}

	public Texture[] getTextureData() {
		return textureDatas;
	}

	public void setTextureDatas(Texture[] textureDatas) {
		this.textureDatas = textureDatas;
	}

	public byte[] getTextureArea() {
		return textureArea;
	}

	public void setTextureArea(byte[] textureArea) {
		this.textureArea = textureArea;
	}

	public byte[] readRectArea(int x, int y, int w, int h) {

		byte[] buffer = new byte[w * h];

		int xy = x + (y * width);

		int ctd = 0;
		for (int i = 0; i < h; i++) {
			for (int j = 0; j < w; j++) {
				// try {
				buffer[ctd++] = textureArea[xy + j];
				// } catch (ArrayIndexOutOfBoundsException e ) {}
			}
			xy = xy + width;
		}

		return buffer;

	}

	public void writeRectArea(byte buffer[], int x, int y, int w, int h) {

		int xy = x + (y * width);

		int ctd = 0;
		for (int i = 0; i < h; i++) {
			for (int j = 0; j < w; j++) {
				// try {
				textureArea[xy + j] = buffer[ctd++];
				// } catch (ArrayIndexOutOfBoundsException e ) {}
			}
			xy = xy + width;
		}

	}

}
