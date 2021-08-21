package cetra.field.script;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.HashMap;

import cetra.model.Text;

public class Dialog {

	private int textLength;
	private HashMap<Integer, Text> texts;

	public Dialog(byte[] buf,int bufLimit, Header header) throws IOException {
		texts = new HashMap<Integer, Text>();
		initialize(buf,bufLimit, header);
	}

	private void initialize(byte[] data, int dataLimit, Header header) throws IOException {

		if (header.getNumberAkaoBlocks() > 0 &&
			header.getOffsetStrings() == header.getOffsetsAkaoBlocks()[0]){
			return;
		}
		
		ByteBuffer buffer = ByteBuffer.wrap(data);
		buffer.order(ByteOrder.LITTLE_ENDIAN);		
		buffer.position(header.getOffsetStrings());
		
		textLength = buffer.getShort() & 0xFFFF;
		int firstPointer = buffer.getShort() & 0xFFFF;
		
		if (buffer.position() > dataLimit)
			return;

		int textLengthCheck = 0;
		if (firstPointer > 0)
			textLengthCheck = (firstPointer >> 1) - 1;

		if (textLength != textLengthCheck)
			textLength = textLengthCheck;
		
		if (textLength == 0)
			return;

		int[] pointers = new int[textLength];
		pointers[0] = firstPointer;

		for (int i = 1; i < pointers.length; i++) {
			pointers[i] = buffer.getShort() & 0xFFFF;
		}
		
		Table table = new Table(new File("table.txt"));
		for (int i = 0; i < pointers.length; i++) {

			int pointer = pointers[i];
			ArrayList<Byte> dataText = new ArrayList<Byte>();
			pointer += header.getOffsetStrings();
			while (data[pointer] != -1)
				dataText.add(data[pointer++]);

			String strText = table.format(dataText);
			texts.put(i, new Text(i, strText, dataText));
		}
	}

	public HashMap<Integer, Text> getTexts() {
		return texts;
	}

	public void setTexts(HashMap<Integer, Text> texts) {
		this.texts = texts;
	}

	public ArrayList<Byte> getBytes() throws NumberFormatException, IOException {

		ArrayList<Byte> bytes = new ArrayList<Byte>();
		ArrayList<Byte> pointers = new ArrayList<Byte>();

		int numTexts = texts.size();
		pointers.add((byte) (numTexts & 0xFF));
		pointers.add((byte) ((numTexts >> 8) & 0xFF));

		int pointer = (numTexts + 1) * 2;
		pointers.add((byte) (pointer & 0xFF));
		pointers.add((byte) ((pointer >> 8) & 0xFF));		

			Table table = new Table(new File("table.txt"));
			for (int i = 0; i < numTexts; i++) {
				Text text = texts.get(i);

				boolean forceDTE = text.getMessages().size() > 0
						? (text.getMessages().get(0).getOpcode().getOpcode() == EOpcode.MPNAM)
						: false;
				byte[] bText = table.parseBytes(text.getText(), forceDTE);
				if (forceDTE && bText.length > 24) {
					String message = "The location name '" + text.getText() + "' exceeded the 23-byte limit";
					throw new IOException(message);
				}

				for (byte b : bText) {
					bytes.add(b);
				}
				pointer += bText.length;
				pointers.add((byte) (pointer & 0xFF));
				pointers.add((byte) ((pointer >> 8) & 0xFF));
			}


		ArrayList<Byte> combBytes = new ArrayList<Byte>();

		for (int i = 0; i < pointers.size() - 2; i++) {
			combBytes.add(pointers.get(i));
		}

		for (Byte b : bytes) {
			combBytes.add(b);
		}

		return combBytes;
	}
}