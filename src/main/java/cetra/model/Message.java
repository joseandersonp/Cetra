package cetra.model;

import java.util.ArrayList;

import cetra.field.script.EOpcode;
import cetra.field.script.Opcode;

public class Message {
		
	private Text text;
	private Opcode opcode;
	private int indexWindowId = 1;
	private int indexTextId   = 2;
	
	private java.util.List<Window> windows = new ArrayList<>(0);	

	@SuppressWarnings("incomplete-switch")
	public Message(Opcode opcode) {
		
		switch (opcode.getOpcode()) {
			
			case ASK: 
			
				indexWindowId++;
				indexTextId++;
				break;
				
			case MPNAM:
				
				indexWindowId=0;
				indexTextId=1;
				
				Opcode wop = new Opcode(EOpcode.WINDOW);
				wop.initialize();
				Window window = new Window(wop, new WindowMode(indexWindowId, 0));
				window.setX(184);
				window.setY(208);
				window.setWidth(180);
				window.setHeight(24);
				
				windows.add(window);
				break;
		}
		this.opcode = opcode;
	}	

	public Text getText() {
		return text;
	}

	public void setText(Text text) {
		this.text = text;
		if (opcode.getOpcode() != EOpcode.MPNAM)
			this.opcode.getBinCode()[indexTextId] = (byte) text.getId();
	}
	
	public int getTextId() {
		return ((int)this.opcode.getBinCode()[indexTextId]) & 0xFF;
	}
	
	public void setTextId(int id) {
		this.opcode.getBinCode()[indexTextId] = (byte)id;
	}
	
	public int getWindowId() {
		return ((int)this.opcode.getBinCode()[indexWindowId]) & 0xFF;
	}
	
	public int getFirstLineChoice(){
		if (opcode.getOpcode() == EOpcode.ASK){
			return ((int)this.opcode.getBinCode()[4]) & 0xFF;
		}
		return -1;
	}

	public Opcode getOpcode() {
		return opcode;
	}

	public void setUpcodeMessage(Opcode opcode) {
		this.opcode = opcode;
	}
	
	public java.util.List<Window> getWindows() {
		return windows;
	}
	
	public void setWindows(java.util.List<Window> windows) {
		this.windows = windows;
	}
	
	public void addWindow(Window window) {
		windows.add(window);		
	}
	
	public Window getWindow(int index) {
		return windows.get(index);
	}
	
}
