package cetra.field.script;

public enum EKOpcode {
	
	EYETX(0x00, "EYETX"," "),
	TRNSP(0x01, "TRNSP"," "),
	AMBNT(0x02, "AMBNT"," "),
	MGF03(0x03, "MGF03"," "),  //???
	MGF04(0x04, "MGF04"," "),  //???
	MGF05(0x05, "MGF05"," "),  //???
	LIGHT(0x06, "LIGHT"," "),
	MGF07(0x07, "MGF07"," "),  //???
	MGF08(0x08, "MGF08"," "),  //???
	MGF09(0x09, "MGF09"," "),  //???
	SBOBJ(0x0A, "SBOBJ"," "),
	MGF0B(0x0B, "MGF0B"," "),  //???
	MGF0C(0x0C, "MGF0C"," "),  //???
	SHINE(0x0D, "SHINE"," "),
	RESET(0xFF, "RESET"," ");
	
	private int opcode;
	private String shortName;
	private String longName;

	EKOpcode(int opcode, String shortName, String longName) {
		this.opcode = opcode;
		this.shortName = shortName;
		this.longName = longName;
	}

	public int getOpcode() {
		return opcode;
	}

	public String getShortName() {
		return shortName;
	}

	public String getLongName() {
		return longName;
	}
	
}
