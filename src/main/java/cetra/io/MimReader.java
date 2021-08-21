package cetra.io;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cetra.compression.Lzss;
import cetra.field.tex.Background;
import cetra.field.tex.BackgroundBulding;
import cetra.field.tex.Palette;
import cetra.field.tex.TextureFormat;
import cetra.field.tilemap.LayerChange;
import cetra.field.tilemap.Parameter;
import cetra.field.tilemap.SpriteTPBlend;
import cetra.field.tilemap.Tile;
import cetra.field.tilemap.TileImage;
import cetra.field.tilemap.TileMap;
import cetra.field.tilemap.TileMapBuilding;
import cetra.model.Field;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.BlendMode;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

public class MimReader {

	private int cw = 1280, ch = 600;

	private Background backgroundD;
	private TileMap tileM;
	private Map<Integer, ArrayList<TileImage>> mapZorder;
	private Canvas canvas;

	public MimReader(Field field) throws IOException {
		init(field);
	}

	private void init(Field field) throws IOException {

		canvas = new Canvas();
		mapZorder = new HashMap<>();

		String fileMIM = field.getFile().getParentFile() + "\\" + field.getName() + ".MIM";

		// long firstTime = System.currentTimeMillis();
		FileInputStream in = new FileInputStream(fileMIM);
		in.skip(4);

		byte[] inBuffer = new byte[in.available()];
		in.read(inBuffer);
		in.close();

		byte[] outBuffer = new byte[0x200000]; // buffer 2Mb
		Lzss.decodeBuffers(inBuffer, outBuffer);

		// System.out.println("Time:" + (lastTime - System.currentTimeMillis()) / 1000d
		// +" sec");

		backgroundD = BackgroundBulding.generate(outBuffer);

		/*
		 * FileOutputStream out = new FileOutputStream("C:/FFVII/Teste/bg_" +
		 * field.getName()); out.write(backgroundD.getTextureArea()); out.close();
		 */

		tileM = TileMapBuilding.generate(field.getTileMap());

		cw = tileM.getMaxDestinationX() + 16 - tileM.getMinDestinationX();
		ch = tileM.getMaxDestinationY() + 16 - tileM.getMinDestinationY();

		canvas.setWidth(cw);
		canvas.setHeight(ch);

	}

	private WritableImage toImageBgra(byte[] pixelTex, int w, int h) {

		WritableImage writableImage = new WritableImage(w, h);
		PixelWriter pixelWriter = writableImage.getPixelWriter();
		pixelWriter.setPixels(0, 0, w, h, PixelFormat.getByteBgraInstance(), pixelTex, 0, w * 4);
		return writableImage;

	}

	public void loadLayers() throws Exception {

		List<LayerChange> layerCs = tileM.getLayerChanges();
		List<Tile> tiles = tileM.getTiles();
		Palette paletteD = backgroundD.getPaletteData();
		List<SpriteTPBlend> spriteTPBs = tileM.getSpriteTpBlends();

		SpriteTPBlend spriteTPB;

		if (spriteTPBs.size() == 0)
			spriteTPB = new SpriteTPBlend(0);
		else
			spriteTPB = spriteTPBs.get(0);

		int indexPX = backgroundD.getTextureData()[0].getX() / 64;

		int countLC = 0;
		int countSTPB = 0;
		int currentLC = 0;
		int tileS = 16;

		while (countLC < layerCs.size()) {

			LayerChange layerLC = layerCs.get(countLC);
			int tileP = layerLC.getTilePos();
			int tileC = layerLC.getTileCount();

			if (layerLC.getType() == 0x7FFE) {
				++countSTPB;
				if (countSTPB < spriteTPBs.size()) {
					spriteTPB = spriteTPBs.get(countSTPB);
				}
			}

			if (layerLC.getType() == 0x7FFF) {
				currentLC++;
				if (currentLC == 2) {
					tileS = 32;
				}				
			}

			while (tileC > 0) {

				Tile tile = tiles.get(tileP);
				int clutN = tile.getTileClutData().getClutNumber();
				int deph = spriteTPB.getDeph();

				tileP++;
				tileC--;
				
				if (tile.getParameter() != null) {
					/*
					 * System.out.println("Layer: " + currentLC + ", lchange: " + countLC +
					 * ", tile: " + tileC + ", param.: "+ tile.getParameter().getId() + ", state " +
					 * tile.getState() + ", group " + tile.getGroup());
					 */
					if (tile.getState() > 1)
						continue;
				}

				int pageX = spriteTPB.getPageX() - indexPX;
				int pageY = spriteTPB.getPageY();

				if (currentLC == 1) {
					pageX = tile.getSpriteTPBlend().getPageX() - indexPX;
					pageY = tile.getSpriteTPBlend().getPageY();
					deph = tile.getSpriteTPBlend().getDeph();
				}

				int pageSX = tile.getTexPageSourceX();
				int pageSY = tile.getTexPageSourceY();

				pageSY += pageY * 256;

				byte[] tileData;
				byte[] pixels32Bgra = null;
				WritableImage imageBgra = null;

				switch (deph) {
				case 0:
					pageSX = pageX * 128 + pageSX / 2;
					tileData = backgroundD.readRectArea(pageSX, pageSY, tileS / 2, tileS);
					pixels32Bgra = TextureFormat.buffer4bppTo32Bgra(tileData, paletteD.getPaletes()[clutN]);
					break;
				case 1:
					pageSX = pageX * 128 + pageSX;
					tileData = backgroundD.readRectArea(pageSX, pageSY, tileS, tileS);
					pixels32Bgra = TextureFormat.buffer8bppTo32Bgra(tileData, paletteD.getPaletes()[clutN]);
					break;
				default:
					pageSX = pageX * 128 + pageSX * 2;
					tileData = backgroundD.readRectArea(pageSX, pageSY, tileS * 2, tileS);
					pixels32Bgra = TextureFormat.buffer15bppTo32Bgra(tileData);
				}

				imageBgra = toImageBgra(pixels32Bgra, tileS, tileS);

				TileImage tileI = new TileImage(tile, imageBgra);

				int zOrder = tile.getGroup();

				if (currentLC == 0)
					zOrder = 4094;
				else if (currentLC == 2)
					zOrder = 4096;
				else if (currentLC == 3)
					zOrder = 4095;

				if (!mapZorder.containsKey(zOrder))
					mapZorder.put(zOrder, new ArrayList<TileImage>());

				mapZorder.get(zOrder).add(tileI);
			}
			countLC++;
		}
	}

	public void paintCanvas() {

		GraphicsContext gc = canvas.getGraphicsContext2D();
		gc.setFill(Color.BLACK);
		gc.fillRect(0, 0, cw, ch);

		ArrayList<Integer> keys = new ArrayList<Integer>(mapZorder.keySet());
		Collections.sort(keys);
		for (int i = keys.size() - 1; i >= 0; i--) {

			ArrayList<TileImage> tileIs = mapZorder.get(keys.get(i));
			for (TileImage tileI : tileIs) {

				Tile tile = tileI.getTile();
				SpriteTPBlend spriteTPB = tile.getSpriteTPBlend();
				Parameter param = tile.getParameter();

				if ((spriteTPB != null && spriteTPB.getBlendingMode() == 1)
						|| (param != null && param.getBlendMode() == 1)) {
					gc.setGlobalBlendMode(BlendMode.ADD);
					gc.drawImage(tileI.getImage(), cw / 2 + tile.getDestinationX(), ch / 2 + tile.getDestinationY());
					gc.setGlobalBlendMode(BlendMode.SRC_OVER);
				} else {
					gc.drawImage(tileI.getImage(), cw / 2 + tile.getDestinationX(), ch / 2 + tile.getDestinationY());
				}
			}
		}
	}

	public WritableImage getImage() {
		return canvas.snapshot(null, null);
	}
}