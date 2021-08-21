package cetra.field.tex;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class BackgroundBulding {

	public static Background generate(byte[] data)  {

		Background backgroundData = new Background();
		Palette palData = backgroundData.getPaletteData();

		ByteBuffer buffer = ByteBuffer.wrap(data);
		buffer.order(ByteOrder.LITTLE_ENDIAN);
		buffer.position(0);
		
		palData.setSize(buffer.getInt());
		palData.setX(buffer.getShort() & 0xFFFF);
		palData.setY(buffer.getShort() & 0xFFFF);

		palData.setWidth(buffer.getShort() & 0xFFFF);
		palData.setHeight(buffer.getShort() & 0xFFFF);
			

		int[][] palette = new int[palData.getHeight()][palData.getWidth()];

		for (int i = 0; i < palData.getHeight(); i++) {
			for (int j = 0; j < palData.getWidth(); j++) {
				palette[i][j] = buffer.getShort() & 0xFFFF;
			}
		}
		
		palData.setPaletes(palette);
		backgroundData.setPaletteData(palData);
		
		//int offsetTex = totalSize - lin.available() + 14;
		//System.out.println("Offset Tex:" + offsetTex);
		
		Texture[] textureDatas = new Texture[2];
		for (int i = 0; i < textureDatas.length; i++) {
			
			Texture texData = new Texture();
			textureDatas[i] = texData;

			texData.setSize(buffer.getInt());
			
			if (texData.getSize() == 0)
				break;
			
			texData.setX(buffer.getShort());
			texData.setY(buffer.getShort());
			texData.setWidth(buffer.getShort());
			texData.setHeight(buffer.getShort());

			int lenghtTex = texData.getWidth() * 2 * texData.getHeight();

			byte blockData[] = new byte[lenghtTex];
			buffer.get(blockData);
			
			backgroundData.writeRectArea(blockData, 0, texData.getY(), texData.getWidth() * 2, texData.getHeight());
							
		}
		
		backgroundData.setTextureDatas(textureDatas);

		return backgroundData;
	}
}
