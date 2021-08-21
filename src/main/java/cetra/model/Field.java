package cetra.model;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import cetra.field.script.Tutorial;
import cetra.io.DatReader;
import javafx.scene.image.WritableImage;

@XmlRootElement(name = "script")
@XmlAccessorType (XmlAccessType.NONE)
public class Field {

	private static int nextId;
	private Integer id;	
	private File file;
	private WritableImage bkgImage;
	
	private boolean initialized;
	
	private Script script;    	// Contains conversations, save point interaction etc. 
	private byte[] walkmesh; 	// Contains walkmesh triangles and access info. 
	private byte[] tileMap;		// Contains the information for the background, animation, and static scene objects.
	private byte[] cameraMatrix;// Contains camera info.
	private byte[] triggers; 	// Contains triggers, singles, gateways and so on.
	private byte[] encounter; 	// Battle Encounter information for location.
	private byte[] models; 		// Some info about field models. 
		
	private List<Text> texts;
	
	private List<Tutorial> tutorials;
	
	{ id = ++nextId; }
	
	public static void resetId() {
		nextId = 0;
	}
	
	public Field() {}
	
	public Field(File file) {
		this.file = file;
	}
	
	public void initialize(DatReader datReader) throws Exception {
		script 			= datReader.getScript();
		walkmesh		= datReader.getData(1, 2);
		tileMap			= datReader.getData(2, 3);
		cameraMatrix	= datReader.getData(3, 4);
		triggers		= datReader.getData(4, 5);
		encounter		= datReader.getData(5, 6);
		
		byte[] models	= datReader.getData(6, 7);
		
		int modelsLength = ((int)models[0]) & 0xFF;
		modelsLength += (((int)models[1]) & 0xFF) << 8;
		
		this.models = Arrays.copyOf(models, modelsLength);
		
		initialized = true;
	}

	public int getId() {
		return id;
	}

	
	public String getName(){		
		return file.getName().replaceAll("\\.(.*)", "");
	}

	
	public void setName(String name){
		file = new File(name);		
	}
	
	public File getFile() {
		return file;
	}
	
	public boolean isInitialized() {
		return initialized;
	}
	
	public List<Text> getTexts() {
		return texts;
	}

	@XmlElement (name= "dialog")
	public void setTexts(List<Text> texts){
		this.texts = texts;
	}

	public void setTutorials(List<Tutorial> tutorials) {
		this.tutorials = tutorials;
	}
	
	public byte[] getCameraMatrix() {
		return cameraMatrix;
	}

	public void setCameraMatrix(byte[] cameraMatrix) {
		this.cameraMatrix = cameraMatrix;
	}

	public byte[] getEncounter() {
		return encounter;
	}

	public void setEncounter(byte[] encounter) {
		this.encounter = encounter;
	}

	public byte[] getModels() {
		return models;
	}

	public void setModels(byte[] models) {
		this.models = models;
	}
	
	public Script getScript() {
		return script;
	}

	public byte[] getWalkmesh() {
		return walkmesh;
	}

	public byte[] getTileMap() {
		return tileMap;
	}

	public byte[] getTriggers() {
		return triggers;
	}
	
	public List<Tutorial> getTutorials() {
		return tutorials;
	}

	public WritableImage getBkgImage() {
		return bkgImage;
	}

	public void setBkgImage(WritableImage bkgImage) {
		this.bkgImage = bkgImage;
	}
	
	
	
}
