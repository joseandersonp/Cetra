package cetra.iso.descriptor;

import java.lang.reflect.Array;

import cetra.util.Data;

public abstract class VolumeDescriptor{

	private int    type;
	private String id;
	private int    version;

	public VolumeDescriptor(byte[] buffer){
		this.type	= Array.getInt(buffer, 0);
		this.id		= Data.arrayToString(buffer, 1, 5);
		this.version= Array.getInt(buffer, 6);
	}
	
	public int getType() {
		return type;
	}
	public String getId() {
		return id;
	}
	public int getVersion() {
		return version;
	}
}

