package cetra.field.tilemap;

public class Parameter {

	private int id;
	private int blendMode;

	private int uInt16;

	public Parameter(int uInt16) {
		this.uInt16 = uInt16;
		blendMode = (0b10000000 & uInt16) >> 7;
		id = 0b01111111 & uInt16;
	}

	public int getuInt16() {
		return uInt16;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getBlendMode() {
		return blendMode;
	}

	public void setBlendMode(int blendMode) {
		this.blendMode = blendMode;
	}

}
