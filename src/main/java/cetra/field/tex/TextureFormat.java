package cetra.field.tex;

public class TextureFormat {

	public static int bClut4bppTo32Bgra(byte half, byte outBuffer[], int idxOB, int[] palette15Bgra) {

		int byteTex = half & 0xF;
		int color = palette15Bgra[byteTex];

		int b = ((color & 0b0111110000000000) >> 10);
		int g = ((color & 0b0000001111100000) >> 5);
		int r = (color & 0b0000000000011111);
		int stp = (color & 0b1000000000000000) >> 15;

		int alpha = 255;

		if (b + g + r == 0 && (stp == 0))
			alpha = (byte) 0;

		outBuffer[idxOB++] = (byte) (b * 8);
		outBuffer[idxOB++] = (byte) (g * 8);
		outBuffer[idxOB++] = (byte) (r * 8);
		outBuffer[idxOB++] = (byte) (alpha);

		return idxOB;
	}

	public static byte[] buffer4bppTo32Bgra(byte bufferTex[], int[] palette15Bgra) {

		int length = (bufferTex.length * 4) * 2;
		byte[] buffer32 = new byte[length];

		int z = 0;
		for (int i = 0; i < bufferTex.length; i++) {

			int byteTex = bufferTex[i] & 0xFF;
			byte halfLeft = (byte) ((byteTex & 0xF0) >> 4);
			byte halfRight = (byte) ((byteTex & 0x0F));

			z = bClut4bppTo32Bgra(halfRight, buffer32, z, palette15Bgra);
			z = bClut4bppTo32Bgra(halfLeft, buffer32, z, palette15Bgra);
		}
		return buffer32;
	}

	public static byte[] buffer8bppTo32Bgra(byte bufferTex[], int[] palette15Bgra) {

		int length = bufferTex.length * 4;
		byte[] buffer32 = new byte[length];

		int z = 0;
		for (int i = 0; i < bufferTex.length; i++) {

			int byteTex = bufferTex[i] & 0xFF;
			int color = palette15Bgra[byteTex];

			int b = ((color & 0b0111110000000000) >> 10);
			int g = ((color & 0b0000001111100000) >> 5);
			int r = (color & 0b0000000000011111);
			int stp = (color & 0b1000000000000000) >> 15;

			int alpha = 255;

			if (b + g + r == 0 && (stp == 0))
				alpha = (byte) 0;

			buffer32[z++] = (byte) (b * 8);
			buffer32[z++] = (byte) (g * 8);
			buffer32[z++] = (byte) (r * 8);
			buffer32[z++] = (byte) (alpha);

		}
		return buffer32;

	}

	public static byte[] buffer15bppTo32Bgra(byte bufferTex[]) {

		int length = (bufferTex.length * 4);
		byte[] buffer32 = new byte[length];

		int z = 0;
		for (int i = 0; i < bufferTex.length; i += 2) {
			int word = ((bufferTex[i + 1] & 0xFF) << 8) + (bufferTex[i] & 0xFF);
			z = s15bppTo32Bgra(word, buffer32, z);
		}
		return buffer32;

	}

	public static int s15bppTo32Bgra(int word, byte outBuffer[], int idxOB) {

		int color = word & 0xFFFF;

		int b = ((color & 0b0111110000000000) >> 10);
		int g = ((color & 0b0000001111100000) >> 5);
		int r = (color & 0b0000000000011111);
		int stp = (color & 0b1000000000000000) >> 15;

		int alpha = 255;

		if (b + g + r == 0 && (stp == 0))
			alpha = (byte) 0;

		outBuffer[idxOB++] = (byte) (b * 8);
		outBuffer[idxOB++] = (byte) (g * 8);
		outBuffer[idxOB++] = (byte) (r * 8);
		outBuffer[idxOB++] = (byte) (alpha);

		return idxOB;
	}

}
