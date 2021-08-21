package cetra.model;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "dialog")
@XmlAccessorType(XmlAccessType.NONE)
public class Text {

	private int id;
	private String dialog;
	private List<Byte> data;
	private List<Message> messages;

	public Text() {
	}

	public Text(int id, String dialog, List<Byte> data) {
		this(id, dialog);
		this.data = data;
	}

	public Text(int id, String dialog) {
		this.messages = new ArrayList<Message>();
		this.id = id;
		this.dialog = dialog;
	}

	public void addMessage(Message message) {
		messages.add(message);
	}

	public List<Message> getMessages() {
		return messages;
	}

	public String getText() {
		return dialog;
	}

	public void setText(String txt) {
		dialog = txt;
	}

	public String getDescription() {
		return String.format("Dialogue #%03d", id);
	}

	public String isUsed() {
		if (messages != null && messages.size() > 0) {
			return "[=]";
		}
		return "";
	}

	public int getNumMessages() {
		if (messages != null) {
			return messages.size();
		}
		return 0;
	}

	@XmlElement
	public void setId(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}

	@Override
	public String toString() {
		return dialog;
	}

	public String getDialog() {
		return dialog;
	}

	@XmlElement(name = "text")
	public void setDialog(String dialog) {
		this.dialog = dialog;
	}

	public List<Byte> getData() {
		return data;
	}
	
	public void setData(List<Byte> data) {
		this.data = data;
	}
}
