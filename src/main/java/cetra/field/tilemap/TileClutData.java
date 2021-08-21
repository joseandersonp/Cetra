package cetra.field.tilemap;

public class TileClutData {
	
	int uInt16;

	private int zz1;    // ZZ1:6
	private int clutNumber; // ClutNumber
	private int zz2; // ZZ2:6

	public TileClutData(int uInt16) {
		this.uInt16 = uInt16;
		zz1 = (0b1111110000000000 & uInt16) >> 10;
		clutNumber = (0b0000001111000000 & uInt16) >> 6;
		zz2 = 0b0000000000111111 & uInt16;
	}

	public int getZz1() {
		return zz1;
	}
	
	public int getClutNumber() {
		return clutNumber;
	}
	
	public int getZz2() {
		return zz2;
	}
	
	public int getuInt16() {
		return uInt16;
	}
}