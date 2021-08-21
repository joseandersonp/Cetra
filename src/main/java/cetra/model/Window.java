package cetra.model;

import cetra.field.script.Opcode;
import cetra.util.LittleEndianUtil;

public class Window {

	private Opcode opcode;
	private WindowMode windowMode;

	public Window(Opcode opcode, WindowMode windowMode) {
		this(opcode);
		this.windowMode = windowMode;
	}

	public Window(Opcode opcode) {
		this.opcode = opcode;
	}

	public int getId() {
		return opcode.getBinCode()[1];
	}

	public void setId(int id) {
		opcode.getBinCode()[1] = (byte) id;
	}

	public int getX() {
		return LittleEndianUtil.readShort(opcode.getBinCode(), 2);
	}

	public void setX(int x) {
		LittleEndianUtil.writeShort(opcode.getBinCode(), 2, x);
	}

	public int getY() {
		return LittleEndianUtil.readShort(opcode.getBinCode(), 4);
	}

	public void setY(int y) {
		LittleEndianUtil.writeShort(opcode.getBinCode(), 4, y);
	}

	public int getWidth() {
		return LittleEndianUtil.readShort(opcode.getBinCode(), 6);
	}

	public void setWidth(int width) {
		LittleEndianUtil.writeShort(opcode.getBinCode(), 6, width);
	}

	public int getHeight() {
		return LittleEndianUtil.readShort(opcode.getBinCode(), 8);
	}

	public void setHeight(int height) {
		LittleEndianUtil.writeShort(opcode.getBinCode(), 8, height);
	}

	public Opcode getOpcode() {
		return opcode;
	}

	public void setOpcode(Opcode opcode) {
		this.opcode = opcode;
	}

	public WindowMode getWindowMode() {
		return windowMode;
	}

	public void setWindowMode(WindowMode windowMode) {
		this.windowMode = windowMode;
	}
}