package cetra.field.tilemap;

public class Tile {

	private int destinationX;     // INT16
	private int destinationY;     // INT16
	private int texPageSourceX;   // UINT8
	private int texPageSourceY;   // UINT8
	private TileClutData tileClutData;
	private SpriteTPBlend spriteTPBlend;// UINT16
	private int group; // 12 lsb bits only UINT16
	private Parameter parameter;// UINT8
	private int state;// UINT8

	public int getDestinationX() {
		return destinationX;
	}

	public void setDestinationX(int destinationX) {
		this.destinationX = destinationX;
	}

	public int getDestinationY() {
		return destinationY;
	}

	public void setDestinationY(int destinationY) {
		this.destinationY = destinationY;
	}

	public int getTexPageSourceX() {
		return texPageSourceX;
	}

	public void setTexPageSourceX(int texPageSourceX) {
		this.texPageSourceX = texPageSourceX;
	}

	public int getTexPageSourceY() {
		return texPageSourceY;
	}

	public void setTexPageSourceY(int texPageSourceY) {
		this.texPageSourceY = texPageSourceY;
	}

	public TileClutData getTileClutData() {
		return tileClutData;
	}

	public void setTileClutData(TileClutData tileClutData) {
		this.tileClutData = tileClutData;
	}

	public SpriteTPBlend getSpriteTPBlend() {
		return spriteTPBlend;
	}

	public void setSpriteTPBlend(SpriteTPBlend spriteTPBlend) {
		this.spriteTPBlend = spriteTPBlend;
	}

	public int getGroup() {
		return group;
	}

	public void setGroup(int group) {
		this.group = group;
	}

	public Parameter getParameter() {
		return parameter;
	}

	public void setParameter(Parameter parameter) {
		this.parameter = parameter;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

}