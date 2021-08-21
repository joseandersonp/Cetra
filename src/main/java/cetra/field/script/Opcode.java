package cetra.field.script;

import java.io.IOException;
import java.io.InputStream;

public class Opcode {

	EOpcode  opcode = EOpcode.RET;
	ESOpcode sOpcode;
	EKOpcode kOpcode;
	private byte[] data;
	private byte[] sData;
	private byte[] kData;

	public Opcode(EOpcode upcode) {
		this.opcode = upcode;
	}

	public void initialize() {
		data = new byte[opcode.getLength()];
		data[0] = (byte) opcode.getUpcode();
	}

	void initialize(InputStream in) throws IOException {
		initialize();
		if (opcode.getLength() > 1)
			in.read(data, 1, opcode.getLength() - 1);
	}
	
	public void initializeSOpcode(InputStream in) throws IOException {
		if (sOpcode.getLength() > 1){
			sData = new byte[sOpcode.getLength()];
			sData[0] = (byte) sOpcode.getSupop();
			in.read(sData, 1 , sOpcode.getLength() - 1);	
		}
	}
	
	public void initializeKOpcode(InputStream in) throws IOException {
			kData = new byte[data[1]-2];
			kData[0] = (byte) kOpcode.getOpcode();
			in.read(kData, 1 , data[1]- 2 - 1);
	}

	public byte[] getBinCode() {
		
		if(opcode == EOpcode.KAWAI){
			byte[] data = new byte[this.data.length + kData.length];
			System.arraycopy(this.data, 0, data, 0, this.data.length);
			System.arraycopy(kData, 0, data, this.data.length, kData.length);
			return data;
		}
		
		if(opcode == EOpcode.SPECIAL){
			byte[] data = new byte[this.data.length + sData.length];
			System.arraycopy(this.data, 0, data, 0, this.data.length);
			System.arraycopy(sData, 0, data, this.data.length, sData.length);
			return data;
		}
		return data;
	}
	
	public String getAssemblyCode() {
		return null;
	}

	public String getDiassemblyCode() {
		return null;
	}

	public String getHexCode() {
		return null;
	}

	public EOpcode getOpcode() {
		return opcode;
	}
	
	public void setSOpcode(ESOpcode sOpcode) {
		this.sOpcode = sOpcode;
	}	
	
	public void setKOpcode(EKOpcode kOpcode) {
		this.kOpcode = kOpcode;
	}	

	@Override
	public String toString() {

		String ret = opcode.getShortName() + " ";
		
		if (data != null) {
			for (int i = 1; i < data.length; i++)
				ret += String.format("%02X ", data[i]);
		}
		
		if (sData != null){
			ret += sOpcode.getShortName() + " ";
			for (int i = 1; i < sData.length; i++)
				ret += String.format("%02X ", sData[i]);
		}
		
		if (kData != null){
			ret += kOpcode.getShortName() + " ";
			for (int i = 1; i < kData.length; i++)
				ret += String.format("%02X ", kData[i]);
		}
		
		return ret;
	}
}