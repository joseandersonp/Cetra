package cetra.field.tilemap;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class TileMapBuilding {

	public static TileMap generate(byte[] data) {

		TileMap tileMap = new TileMap();

		ByteBuffer lin = ByteBuffer.wrap(data);
		lin.position(0);
		lin.order(ByteOrder.LITTLE_ENDIAN);

		// Load Header tile Map
		int endAllSections = lin.limit();
		int firstLayerInf = lin.getInt();
		int texturePageData = lin.getInt();
		int spriteLayerInf = lin.getInt();
		int thirdLayerInf = lin.getInt();

		// Load Section 1

		int offset = 0x10;
		do {

			LayerChange layerChange = new LayerChange();
			tileMap.getLayerChanges().add(layerChange);

			layerChange.setType(lin.getShort() & 0xFFFF);
			offset += 2;

			if (layerChange.getType() != 0x7FFF) {
				layerChange.setTilePos(lin.getShort() & 0xFFFF);
				layerChange.setTileCount(lin.getShort() & 0xFFFF);
				offset += 4;
			}

		} while (offset < firstLayerInf);

		// Load Section 2
		while (offset < texturePageData) {

			Tile tile = new Tile();
			tile.setDestinationX(lin.getShort());
			tile.setDestinationY(lin.getShort());
			tile.setTexPageSourceX(lin.get() & 0xFF);
			tile.setTexPageSourceY(lin.get() & 0xFF);
			tile.setTileClutData(new TileClutData(lin.getShort() & 0xFFFF));

			tileMap.getTiles().add(tile);

			offset += 8;

			tileMap.calculateMaxMinDestination(tile.getDestinationX(), tile.getDestinationY());

		}

		// Load Section 3
		while (offset < spriteLayerInf) {
			tileMap.getSpriteTpBlends().add(new SpriteTPBlend(lin.getShort() & 0xFFFF));
			offset += 2;
		}

		// Load Section 4
		while (offset < thirdLayerInf) {

			Tile tile = new Tile();

			tile.setDestinationX(lin.getShort());
			tile.setDestinationY(lin.getShort());
			tile.setTexPageSourceX(lin.get() & 0xFF & 0xFF);
			tile.setTexPageSourceY(lin.get() & 0xFF & 0xFF);
			tile.setTileClutData(new TileClutData(lin.getShort() & 0xFFFF));
			tile.setSpriteTPBlend(new SpriteTPBlend(lin.getShort() & 0xFFFF));
			tile.setGroup(lin.getShort() & 0xFFFF);
			tile.setParameter(new Parameter(lin.get() & 0xFF));
			tile.setState(lin.get() & 0xFF);

			tileMap.getTiles().add(tile);

			offset += 14;

			tileMap.calculateMaxMinDestination(tile.getDestinationX(), tile.getDestinationY());
		}

		// Load Section 5
		if (offset < endAllSections) {

			int tiles = (endAllSections - offset) / 10;

			for (int i = 0; i < tiles; i++) {

				Tile tile = new Tile();

				tile.setDestinationX(lin.getShort());
				tile.setDestinationY(lin.getShort());
				tile.setTexPageSourceX(lin.get() & 0xFF);
				tile.setTexPageSourceY(lin.get() & 0xFF);
				tile.setTileClutData(new TileClutData(lin.getShort() & 0xFFFF));
				tile.setParameter(new Parameter(lin.get() & 0xFF));
				tile.setState(lin.get() & 0xFF);

				tileMap.getTiles().add(tile);

				offset += 10;

				tileMap.calculateMaxMinDestination(tile.getDestinationX(), tile.getDestinationY());
			}
		}
		
		return tileMap;
	}

}
