package cetra.field.script;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Tutorial {

	private int id;
	private StringBuffer script;
	private HashMap<Integer, eTOpcode> etOpcodes;
	private Table table;

	public Tutorial(int id, byte[] section) throws IOException {
		this(section);
		this.id = id;
	}

	public Tutorial(byte[] section) throws IOException {

		table = new Table(new File("table.txt"), true);
		script = new StringBuffer();
		etOpcodes = eTOpcode.mapValues();
		initialize(section);

	}

	private void initialize(byte[] section) throws IOException {

		ByteBuffer buffer = ByteBuffer.wrap(section);		
		buffer.order(ByteOrder.LITTLE_ENDIAN);
		buffer.position(0);

		while (buffer.position() < buffer.limit()) {

			int op = buffer.get() & 0xFF;
			eTOpcode opcode = null;

			if (etOpcodes.containsKey(op)) {
				
				try {
					opcode = etOpcodes.get(op);
					switch (opcode) {

					case MOVE:

						int x = buffer.getShort() & 0xFFFF;
						int y = buffer.getShort() & 0xFFFF;
						script.append(String.format(opcode.getName(), x, y));
						break;

					case PAUSE:

						int t = buffer.getShort() & 0xFFFF;
						script.append(String.format(opcode.getName(), t));
						break;

					case STRING:

						ArrayList<Byte> bytes = new ArrayList<>();
						byte b;

						do {
							b = buffer.get();
							bytes.add(b);

						} while (b != (byte) 0xFF);

						bytes.remove(bytes.size() - 1);
						script.append(table.format(bytes));
						break;

					case END:
						script.append(opcode.getName());
						return;

					case NOP:
						break;
					default:
						script.append(opcode.getName());
					}
			
				} catch(Exception e){
					script.append(String.format("{0x%02X}", op));
				}

			} else {
				script.append(String.format("{0x%02X}", op));
			}
			script.append("\n");
		}
	}

	@SuppressWarnings("unlikely-arg-type")
	public byte[] getbytes() {

		ArrayList<Byte> bytes = new ArrayList<>();
		Scanner scanner = new Scanner(script.toString());

		HashMap<String, Integer> opcodes = eTOpcode.mapIntegerValues();

		String moveRegex = "\\{MOVE (\\d+),\\s*(\\d+)\\}";
		Pattern movePattern = Pattern.compile(moveRegex);

		String pauseRegex = "\\{PAUSE \\s*(\\d+)\\}";
		Pattern pausePattern = Pattern.compile(pauseRegex);

		String hexRegex = "\\{0x(([1-9]|[A-F])+)\\}";
		Pattern hexPattern = Pattern.compile(hexRegex);

		Matcher matcher = null;

		while (scanner.hasNextLine()) {

			String line = scanner.nextLine();

			if (line.matches("\\{.+\\}")) {

				matcher = movePattern.matcher(line);
				if (matcher.matches()) {
					int x = Integer.parseInt(matcher.group(1));
					int y = Integer.parseInt(matcher.group(2));

					bytes.add((byte) eTOpcode.MOVE.getOp());
					bytes.add((byte) (x & 0xFF));
					bytes.add((byte) ((x >> 8) & 0xFF));

					bytes.add((byte) (y & 0xFF));
					bytes.add((byte) ((y >> 8) & 0xFF));
					continue;
				}

				matcher = pausePattern.matcher(line);
				if (matcher.matches()) {

					int t = Integer.parseInt(matcher.group(1));
					bytes.add((byte) eTOpcode.PAUSE.getOp());
					bytes.add((byte) (t & 0xFF));
					bytes.add((byte) ((t >> 8) & 0xFF));
					continue;
				}

				matcher = hexPattern.matcher(line);
				if (matcher.matches()) {
					bytes.add(Byte.parseByte(matcher.group(1)));
					continue;
				}

				if (opcodes.containsKey(line)) {
					bytes.add(opcodes.get(line).byteValue());
				} else {
					bytes.add(opcodes.get(0).byteValue());
				}

			} else {

				bytes.add((byte) eTOpcode.STRING.getOp());
				byte[] b = table.parseBytes(line);
				for (int i = 0; i < b.length; i++) {
					System.out.println(b[i]);
					bytes.add(b[i]);
				}

			}
		}

		scanner.close();

		byte[] arrayByte = new byte[bytes.size()];
		for (int i = 0; i < bytes.size(); i++) {
			arrayByte[i] = bytes.get(i);
		}
		return arrayByte;
	}

	public StringBuffer getScript() {
		return script;
	}

	@Override
	public String toString() {
		return String.format("Tutorial %02d", id);
	}

	public void setScript(StringBuffer script) {
		this.script = script;
	}

	public void setScript(String text) {
		script = new StringBuffer();
		script.append(text);
	}
}