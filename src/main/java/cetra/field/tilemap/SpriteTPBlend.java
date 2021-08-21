package cetra.field.tilemap;

public class SpriteTPBlend {

	int uInt16;
	int zz; // 7 MSB
	int deph; // deph:2;
	int blendM;// blending_mode:2;
	int pageY; // page_y:1;
	int pageX; // page_x:4; // 4 LSB

	public SpriteTPBlend(int uInt16) {

		this.uInt16 = uInt16;

		zz =     (uInt16 & 0b1111111000000000) >> 9;
		deph =   (uInt16 & 0b0000000110000000) >> 7;
		blendM = (uInt16 & 0b0000000001100000) >> 5;
		pageY =  (uInt16 & 0b0000000000010000) >> 4;
		pageX =   uInt16 & 0b0000000000001111;		
		
	}

	public int getuInt16() {
		return uInt16;
	}

	public void setuInt16(int uInt16) {
		this.uInt16 = uInt16;
	}

	public int getZz() {
		return zz;
	}

	public void setZz(int zz) {
		this.zz = zz;
	}

	public int getDeph() {
		return deph;
	}

	public void setDeph(int deph) {
		this.deph = deph;
	}

	public int getBlendingMode() {
		return blendM;
	}

	public void setBlendingMode(int blendingMode) {
		this.blendM = blendingMode;
	}

	public int getPageY() {
		return pageY;
	}

	public void setPageY(int pageY) {
		this.pageY = pageY;
	}

	public int getPageX() {
		return pageX;
	}

	public void setPageX(int pageX) {
		this.pageX = pageX;
	}

}
