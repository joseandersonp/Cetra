package cetra.field.tilemap;

import java.util.ArrayList;
import java.util.List;

public class TileMap {

	private int minDestinationX;
	private int maxDestinationX;

	private int minDestinationY;
	private int maxDestinationY;

	
	private List<LayerChange> layerChanges = new ArrayList<>();
	private List<Tile> tiles = new ArrayList<>();
	private List<SpriteTPBlend> spriteTpBlends = new ArrayList<>();
	

	public void calculateMaxMinDestination(int destX, int destY) {

		if (destX < 0) {
			if (destX < minDestinationX)
				minDestinationX = destX;
		} else {
			if (destX > maxDestinationX)
				maxDestinationX = destX;
		}

		if (destY < 0) {
			if (destY < minDestinationY)
				minDestinationY = destY;
		} else {
			if (destY > maxDestinationY)
				maxDestinationY = destY;
		}

	}

	public int getMinDestinationX() {
		return minDestinationX;
	}

	public void setMinDestinationX(int minDestinationX) {
		this.minDestinationX = minDestinationX;
	}

	public int getMaxDestinationX() {
		return maxDestinationX;
	}

	public void setMaxDestinationX(int maxDestinationX) {
		this.maxDestinationX = maxDestinationX;
	}

	public int getMinDestinationY() {
		return minDestinationY;
	}

	public void setMinDestinationY(int minDestinationY) {
		this.minDestinationY = minDestinationY;
	}

	public int getMaxDestinationY() {
		return maxDestinationY;
	}

	public void setMaxDestinationY(int maxDestinationY) {
		this.maxDestinationY = maxDestinationY;
	}

//	public List<List<LayerChange>> getLayers() {
//		return layers;
//	}
//
//	public void setLayers(List<List<LayerChange>> layers) {
//		this.layers = layers;
//	}

	public List<LayerChange> getLayerChanges() {
		return layerChanges;
	}

	public void setLayerChanges(List<LayerChange> layerChanges) {
		this.layerChanges = layerChanges;
	}

	public List<Tile> getTiles() {
		return tiles;
	}

	public void setTiles(List<Tile> tiles) {
		this.tiles = tiles;
	}

	public List<SpriteTPBlend> getSpriteTpBlends() {
		return spriteTpBlends;
	}

	public void setSpriteTpBlends(List<SpriteTPBlend> spriteTpBlends) {
		this.spriteTpBlends = spriteTpBlends;
	}
	
	@Override
	public String toString() {
		
		
		StringBuilder sb = new StringBuilder();
		sb.append("Dest X: " + minDestinationX + "min, " + maxDestinationX +"max");
		sb.append("\nDest Y: " + minDestinationY + "min, " + maxDestinationY +"max");
		sb.append("\nLayers: " + layerChanges.size());
		sb.append("\nTPBlends: " + spriteTpBlends.size());
		sb.append("\nTiles: " + tiles.size());
		return sb.toString();
	
	}

}
