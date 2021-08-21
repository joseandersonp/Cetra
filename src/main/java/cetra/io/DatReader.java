package cetra.io;

import java.io.File;
import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

import cetra.compression.Lzss;
import cetra.model.Script;

public class DatReader {
	
	private byte[] data;
	private	int [] pointers;
	private int    startMemory;
	
	public DatReader(File datFile) throws Exception{
		init(datFile);
	}

	public void init(File datFile) throws Exception {
		
		FileInputStream in = new FileInputStream(datFile);
		byte[] inBuffer = new byte[in.available()];
		byte[] outBuffer = new byte[0x100000];
		
		in.skip(4);
		in.read(inBuffer);
		int outBufferLen = Lzss.decodeBuffers(inBuffer, outBuffer);
		
		ByteBuffer buffer =  ByteBuffer.wrap(outBuffer);
		buffer.order(ByteOrder.LITTLE_ENDIAN);
		
		in.close();

		pointers = new int[7];
		startMemory = buffer.getInt();
		
		pointers[0] = 0;
		for (int i = 1; i < pointers.length; i++) 
			pointers[i] = buffer.getInt() - startMemory;
		
		int dataLen = outBufferLen - buffer.position();
		data = new byte[dataLen];
		buffer.get(data);
		
	}

	public Script getScript() throws Exception {
		return new Script(data, pointers[1]);
	}
	
	public byte[] getData(int indexFrom,int indexTo) throws Exception {
		
		int offsetFrom = 0;
		int offsetTo = data.length;
		
		if (indexTo < pointers.length)
			offsetTo = pointers[indexTo];
		
		if (indexFrom > 0)
			offsetFrom = pointers[indexFrom];
		
		return Arrays.copyOfRange(data, offsetFrom, offsetTo);
	}
}