package cetra.field.tex;

public class Texture {

	private long size; // UINT32 <=> 12 + Width*2*Height

	private int x; // UINT16 location of blocks
	private int y; // on 1024x512 display area

	private int width;  // UINT16 Width is the # of Word units
	private int height; // (UINT16) the blocks are wide

	private byte blockData[];

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

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public byte[] getBlockData() {
		return blockData;
	}

	public void setBlockData(byte[] blockData) {
		this.blockData = blockData;
	}

}
