package cetra.iso.sector;

import java.io.IOException;
import java.util.Arrays;

import cetra.util.Data;

public class Sector{
	
	private Format format;
	private byte[] data;

	public Sector(byte[] data, Format format) throws IOException {
		this.data = data;
		this.format = format;
	}

	public Format getFormat() {
		return format;
	}

	public void setTrackType(Format layout) {
		this.format = layout;
	}

	public byte[] getSync() {
		return Arrays.copyOfRange(data, 0, format.getSynchLength());
	}
	
	public void setSubHeader(int subHeader){
		
		int from = format.getSynchLength() 
				 + format.getHeaderLength();
		
		Data.intToArray(this.data, from, subHeader);
		Data.intToArray(this.data, from+4, subHeader);
	}
	
	public byte[] getUserData() {
		
		int from = format.getSynchLength() 
				 + format.getHeaderLength()
				 + format.getSubHeaderLength();
		
		int to = from + format.getUserDataLength();
		return Arrays.copyOfRange(data, from , to);
	}
	
	public void setUserData(byte[] data) {
		
		int from = format.getSynchLength() + format.getHeaderLength() + format.getSubHeaderLength();
		System.arraycopy(data, 0, this.data, from, format.getUserDataLength());
		
	}
	
	public byte[] getData() {
		return data;
	}

	@Override
	public String toString() {

		if (data != null && data.length != format.size())
			return "NO DATA FORMAT";

		StringBuilder sbData = new StringBuilder();
		for(int i = 0; i < format.size(); i+=16){
			StringBuilder sbHexa = new StringBuilder();
			StringBuilder sbAsc = new StringBuilder();
			for(int j = i; j < (i + 16);j++){
				sbHexa.append(String.format("%02X ",data[j]));
				sbAsc.append((char)data[j]);
			}
			sbData.append(sbHexa.toString());
			sbData.append(sbAsc.toString());
			sbData.append("\n");
		}
		return sbData.toString();
	}
}