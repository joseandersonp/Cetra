package cetra.field.script;

import java.util.HashMap;

public enum eTOpcode {

	PAUSE  (0x00, "{PAUSE %d}"),
	UNKNOWN(0x01, "{UNKNOWN}"),
	UP     (0x02, "{UP}"),
	DOWN   (0x03, "{DOWN}"),
	LEFT   (0x04, "{LEFT}"),
	RIGHT  (0x05, "{RIGHT}"),
	MENU   (0x06, "{MENU}"),
	CANCEL (0x07, "{CANCEL}"),
	CHANGE (0x08, "{CHANGE}"),
	OK     (0x09, "{OK}"),
	R1     (0x0A, "{R1}"),
	R2     (0x0B, "{R2}"),
	L1     (0x0C, "{L1}"),	
	L2     (0x0D, "{L2}"),
	START  (0x0E, "{START}"),
	SELECT (0x0F, "{SELECT}"),
	STRING (0x10, "{STRING}"),
	END    (0x11, "{END}"),
	MOVE   (0x12, "{MOVE %d, %d}"),
	NOP    (0xFF, "{NOP}");

	private int op;
	private String name;

	eTOpcode(int op,  String name){
		this.op = op;
		this.name = name;
	}

	public int getOp() {
		return op;
	}
	public String getName() {
		return name;
	}
	
	public static HashMap<Integer, eTOpcode> mapValues(){
		
		HashMap<Integer, eTOpcode> eTOpcodes = new HashMap<>();
		for (eTOpcode eop : eTOpcode.values()) {
			eTOpcodes.put(eop.getOp(), eop);
		}
		return eTOpcodes;
	}
	
	public static HashMap<String, Integer> mapIntegerValues(){
		
		HashMap<String, Integer> values = new HashMap<>();
		for (eTOpcode eop : eTOpcode.values()) {
			values.put(eop.getName(), eop.getOp());
		}
		return values;
	}
}
