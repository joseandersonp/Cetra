package cetra.field.tilemap;

import javafx.scene.image.WritableImage;

public class TileImage {

	
	private Tile tile;
	private WritableImage image;
	
	public TileImage(Tile tile, WritableImage image) {
		super();
		this.tile = tile;
		this.image = image;
	}

	public Tile getTile() {
		return tile;
	}

	public void setTile(Tile tile) {
		this.tile = tile;
	}

	public WritableImage getImage() {
		return image;
	}

	public void setImage(WritableImage image) {
		this.image = image;
	}
	
}