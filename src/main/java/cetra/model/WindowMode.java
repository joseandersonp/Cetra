package cetra.model;

public class WindowMode{
	int id;
	int mode;
	
	public WindowMode(int id, int mode){
		this.id = id;
		this.mode = mode;
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getMode() {
		return mode;
	}
	public void setMode(int mode) {
		this.mode = mode;
	}
}
