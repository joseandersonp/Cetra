package cetra.field.script;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

public class EventScript {

	private static EOpcode[] eOpcodes = EOpcode.values();
	private static ESOpcode[] eSOpcodes = ESOpcode.values();
	private static HashMap<Integer, EKOpcode> eKOpcodes;
	private ArrayList<Opcode> script;

	static {

		eOpcodes = EOpcode.values();
		eSOpcodes = ESOpcode.values();

		eKOpcodes = new HashMap<>();
		for (EKOpcode kop : EKOpcode.values()) {
			eKOpcodes.put(kop.getOpcode(), kop);
		}
	}

	

	public EventScript(byte[] bufScritp) throws IOException {

		initialize(bufScritp);

	}

	public void initialize(byte[] buf) throws IOException {

		InputStream in = new ByteArrayInputStream(buf);
		script = new ArrayList<Opcode>();
		while (in.available() > 0) {

			Opcode op = new Opcode(eOpcodes[in.read()]);
			op.initialize(in);

			if (op.getOpcode() == EOpcode.SPECIAL) {
				op.setSOpcode(eSOpcodes[in.read() - 0xF5]);
				op.initializeSOpcode(in);
				// System.out.println("Opcode Special found:" + op);
			} else if (op.getOpcode() == EOpcode.KAWAI) {

				int value = in.read();
				// System.out.println(value);
				op.setKOpcode(eKOpcodes.get(value));
				op.initializeKOpcode(in);
				// System.out.println(op);
			}

			script.add(op);
		}
	}

	public ArrayList<Opcode> getScript() {
		return script;
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		for (Opcode upcode : script) {
			sb.append(upcode).append("\n");
		}
		return sb.toString();
	}

	public ArrayList<Byte> getBytes() {

		ArrayList<Byte> bytes = new ArrayList<>();
		for (Opcode opcode : script) {
			for (byte binCode : opcode.getBinCode()) {
				bytes.add(binCode);
			}
			;
		}
		return bytes;
	}
}