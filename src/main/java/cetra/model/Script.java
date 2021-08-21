package cetra.model;

import java.io.IOException;
import java.util.ArrayList;

import cetra.field.script.Dialog;
import cetra.field.script.Event;
import cetra.field.script.Header;
import cetra.field.script.Misc;

public class Script {

	private Header	header;
	private Event 	event;
	private Dialog  dialog;
	private Misc    misc;

	private int indexEnd;

	public Script(byte[] data, int indexEnd) throws IOException {
		this.indexEnd = indexEnd;
		init(data);
	}

	public void init(byte[] data) throws IOException {
		
		header = new Header(data);
		event =  new Event(data, header.getTableScriptsOffsets(), header.getOffsetStrings());

		dialog = new Dialog(data, indexEnd, header);
		
		if (header.getNumberAkaoBlocks() > 0)
			misc = new Misc(data, header.getOffsetsAkaoBlocks(),indexEnd);
	}

	public Header getHeader() {
		return header;
	}

	public Dialog getDialog() {
		return dialog;
	}

	public Event getEvent() {
		return event;
	}

	public Misc getMisc() {
		return misc;
	}

	public byte[] getBytes() throws NumberFormatException, IOException{

		ArrayList<Byte> bytes = getListBytes();
		byte[] buf = new byte[bytes.size()];
		for (int i = 0; i < bytes.size(); i++) {
			buf[i] = bytes.get(i);	
		}
		return buf;
	}

	public ArrayList<Byte> getListBytes() throws NumberFormatException, IOException{

		ArrayList<Byte> bufferScript = new ArrayList<>();
		ArrayList<Byte> bufferHeader = null;
		ArrayList<Byte> bufferEvent = event.getBytes();
		ArrayList<Byte> bufferDialog = new ArrayList<>();
		byte[][] bufferMisc = null;

		if (dialog != null){

			bufferDialog = dialog.getBytes();

			int length = bufferDialog.size() + bufferEvent.size();
			int fill = ((int) Math.ceil(length/4f)) * 4 - length;
			while (fill > 0){
				bufferDialog.add((byte)0xFF);
				fill--;
			}
		}

		long pointer = bufferEvent.size() + header.getBytes().size();
		header.setOffsetStrings((int)pointer);

		if (misc != null) {
			
			pointer += bufferDialog.size();
			bufferMisc = misc.getBytes();
			long[] offsetsMisc = new long[bufferMisc.length];

			offsetsMisc[0] = pointer;
			pointer += bufferMisc[0].length;

			for (int i = 1; i < bufferMisc.length; i++) {
				offsetsMisc[i] = pointer;
				pointer += bufferMisc[i].length;
			}
			
			header.setOffsetsAkaoBlocks(offsetsMisc);

		}

		bufferHeader = header.getBytes();

		for (byte b : bufferHeader) {
			bufferScript.add(b);
		}

		for (byte b : bufferEvent) {
			bufferScript.add(b);
		}

		for (byte b : bufferDialog) {
			bufferScript.add(b);
		}

		if (misc != null) {
			for (byte[] ba : bufferMisc) {
				for (byte b : ba) {
					bufferScript.add(b);
				}
			}
			
			int length = bufferScript.size();
			int fill = ((int) Math.ceil(length/4f)) * 4 - length;
			while (fill > 0){
				bufferScript.add((byte)0xFF);
				fill--;
			}
		}
		return bufferScript;
	}
}