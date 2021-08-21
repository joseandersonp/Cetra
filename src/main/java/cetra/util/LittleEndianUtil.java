package cetra.util;

public class LittleEndianUtil {
	
	public static int readUnsignedShort(byte[] buffer,int index){	
		int byte1 = buffer[index] & 0xFF;
		int byte2 = buffer[++index] & 0xFF;
		int uShort = byte1 + (byte2 << 8);
		return uShort;
	}
	
	public static int readShort(byte[] buffer,int index){
		return (short)readUnsignedShort(buffer, index);	
	}
	
	public static void writeUnsignedShort(byte[] buffer, int index, int value) {
		buffer[index] = (byte)(value & 0xFF);
		buffer[++index] = (byte)((value >> 8) & 0xFF);
	}
	
	public static void writeShort(byte[] buffer, int index, int value) {
		writeUnsignedShort(buffer, index, value);
	}
	
}
