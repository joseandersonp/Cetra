package cetra.field.tilemap;

public class LayerChange {

	private int type;	  // UINT16
	private int tilePos;  // UINT16
	private int tileCount;// UINT16
	// Type 0x7FFE is a sprite use Section 3 to find it's texture page.
	// Type 0x7FFF is the End of layer information Data Record.	
	
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public int getTilePos() {
		return tilePos;
	}
	public void setTilePos(int tilePos) {
		this.tilePos = tilePos;
	}
	public int getTileCount() {
		return tileCount;
	}
	public void setTileCount(int tileCount) {
		this.tileCount = tileCount;
	}	
}