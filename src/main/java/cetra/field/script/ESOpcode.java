package cetra.field.script;

public enum ESOpcode {
	
	ARROW(0xF5, 2, "ARROW"," "),
	PNAME(0xF6, 2, "PNAME"," "), 
	GMSPD(0xF7, 2, "GMSPD"," "),
	SMSPD(0xF8, 3, "SMSPD"," "),
	FLMAT(0xF9, 1, "FLMAT"," "),
	FLITM(0xFA, 1, "FLITM"," "),
	BTLCK(0xFB, 2, "BTLCK"," "),
	MVLCK(0xFC, 2, "MVLCK"," "),
	SPCNM(0xFD, 3, "SPCNM"," "),
	RSGLB(0xFE, 1,"RSGLB"," "),
	CLITM(0xFF, 1,"CLITM","");
	
	private int subop;
	private int length;
	private String shortName;
	private String longName;

	ESOpcode(int subop, int length, String shortName, String longName) {
		this.subop = subop;
		this.length = length;
		this.shortName = shortName;
		this.longName = longName;
	}

	public int getSupop() {
		return subop;
	}

	public int getLength() {
		return length;
	}

	public String getShortName() {
		return shortName;
	}

	public String getLongName() {
		return longName;
	}
	
}
