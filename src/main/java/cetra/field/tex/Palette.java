package cetra.field.tex;

public class Palette {

	private long size;     // UINT32 <=> 12 + PalWidth*2*PalHeight
	private int x, y;           // UINT16
	private int width, height;  // UINT16
	
	private int [][] paletes;

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int wdth) {
		this.width = wdth;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public int[][] getPaletes() {
		return paletes;
	}

	public void setPaletes(int[][] paletes) {
		this.paletes = paletes;
	}



	

}