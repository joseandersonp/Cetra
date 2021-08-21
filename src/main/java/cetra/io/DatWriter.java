package cetra.io;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import cetra.compression.Lzss;
import cetra.model.Field;

public class DatWriter {

	private int indexSection;
	private byte sections[][];

	public DatWriter(int numSections) {
		sections = new byte[numSections][];
	}

	public void writeFile(Field field) throws IOException {

		ByteBuffer buffer = ByteBuffer.allocate(0xC800);
		buffer.order(ByteOrder.LITTLE_ENDIAN);
		buffer.position(0);

		int[] header = new int[sections.length];

		int headerCount = 0x80115000;
		buffer.putInt(headerCount);

		for (int i = 0; i < header.length - 1; i++) {
			headerCount += sections[i].length;
			buffer.putInt(headerCount);
		}

		for (int i = 0; i < sections.length; i++) {
			buffer.put(sections[i]);
		}

		byte[] inBuffer = new byte[buffer.position()];
		buffer.position(0);
		buffer.get(inBuffer);
		byte[] outBuffer = new byte[0xC800];

		int encodeSize = Lzss.encodeBuffers(inBuffer, outBuffer);

		try (FileOutputStream out = new FileOutputStream(field.getFile());
		// FileOutputStream outDEC = new FileOutputStream("C:\\FFVII\\Teste\\" +
		// field.getName() + ".bin")
		) {

			// outDEC.write(inBuffer, 0, inBuffer.length);
			out.write((byte) (encodeSize & 0xFF));
			out.write((byte) ((encodeSize >> 8) & 0xFF));
			out.write((byte) ((encodeSize >> 16) & 0xFF));
			out.write((byte) ((encodeSize >> 24) & 0xFF));
			out.write(outBuffer, 0, encodeSize);

		}

	}

	public void addSection(byte[] section) {
		sections[indexSection++] = section;
	}

	public void setSection(int indexSection, byte[] section) {
		sections[indexSection] = section;
	}

	public void setSection(byte[][] sections) {
		this.sections = sections;
	}

	/*
	 * public void writeFile1(Field field) throws IOException {
	 * 
	 * ByteArrayOutputStream baOut = new ByteArrayOutputStream();
	 * LittleEndianDataOutputStream leOut = new LittleEndianDataOutputStream(baOut);
	 * 
	 * int[] header = new int[sections.length];
	 * 
	 * int headerCount = 0x80115000; leOut.writeInt(headerCount);
	 * 
	 * for (int i = 0; i < header.length-1; i++) { headerCount +=
	 * sections[i].length; leOut.writeInt(headerCount); }
	 * 
	 * for (int i = 0; i < sections.length; i++) { leOut.write(sections[i]); }
	 * 
	 * byte[] buf = baOut.toByteArray(); baOut.close(); leOut.close();
	 * 
	 * ByteArrayInputStream baIn = new ByteArrayInputStream(buf);
	 * 
	 * baOut = new ByteArrayOutputStream(); leOut = new
	 * LittleEndianDataOutputStream(baOut); long encodeSize = Lzss.encode(baIn,
	 * leOut);
	 * 
	 * buf = baOut.toByteArray(); baOut.close(); leOut.close();
	 * 
	 * leOut = new LittleEndianDataOutputStream(new
	 * FileOutputStream(field.getFile())); leOut.writeInt((int)encodeSize);
	 * leOut.write(buf);
	 * 
	 * baOut.close(); leOut.close();
	 * 
	 * }
	 */
}