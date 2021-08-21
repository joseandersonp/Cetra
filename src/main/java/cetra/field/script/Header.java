package cetra.field.script;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;

public class Header {

	private int      unknown; 			// u16: Always 0x0502
	private int      numberEntities; 	// char Number of entities
	private int      numberModels; 		// char Number of models
	private int      offsetStrings; 	// u16 Offset to strings
	private int      numberAkaoBlocks; 	// u16 Specifies the number of Akao/tuto blocks/offsets
	private int      scale; 			// u16 Scale of field. For move and talk calculation // (9bit fixed point).
	private int[]    blank; 			// u16[3]
	private String   creatorName;		// char[8] Field creator (never shown)
	private String   fieldName; 		// char[8] Field name (never shown)
	private String[] entitiesNames; 	// char[numberEntities] Field entity names
	private long[]   offsetsAkaoBlocks;	// u32[numberAkaoBlocks] Akao/Tuto block offsets
	private int[][]  tableScriptsOffsets; // u16[numberEntities][32]; Entity script entry points, or more explicitly, subroutine offsets

	public Header(byte[] buf) {
		initialize(buf);
	}

	public Header() {
	}

	private int initialize(byte[] buf) {

		ByteBuffer ledIn = ByteBuffer.wrap(buf);
		ledIn.order(ByteOrder.LITTLE_ENDIAN);
		
		unknown = ledIn.getShort() & 0xFFFF;
		numberEntities = ledIn.get() & 0xFF;
		numberModels = ledIn.get() & 0xFF;
		offsetStrings = ledIn.getShort() & 0xFFFF;
		numberAkaoBlocks = ledIn.getShort() & 0xFFFF;
		scale = ledIn.getShort() & 0xFFFF;
		blank = new int[]{
			ledIn.getShort(), 
			ledIn.getShort(), 
			ledIn.getShort() 
		};

		byte[] subBuf = new byte[8];

		ledIn.get(subBuf);
		creatorName = new String(subBuf).trim();

		ledIn.get(subBuf);
		fieldName = new String(subBuf).trim();

		entitiesNames = new String[numberEntities];

		for (int i = 0; i < entitiesNames.length; i++) {
			ledIn.get(subBuf);
			entitiesNames[i] = new String(subBuf).trim();
		}

		offsetsAkaoBlocks = new long[numberAkaoBlocks];
		for (int i = 0; i < offsetsAkaoBlocks.length; i++) {
			offsetsAkaoBlocks[i] = ledIn.getInt();
		}

		tableScriptsOffsets = new int[numberEntities][32];

		for (int i = 0; i < tableScriptsOffsets.length; i++) {
			for (int j = 0; j < 32; j++) {
				tableScriptsOffsets[i][j] = ledIn.getShort() & 0xFFFF;
			}
		}
		
		return ledIn.position();
		
	}

	public int getUnknown() {
		return unknown;
	}

	public void setUnknown(int unknown) {
		this.unknown = unknown;
	}

	public int getNumberEntities() {
		return numberEntities;
	}

	public void setNumberEntities(int numberEntities) {
		this.numberEntities = numberEntities;
	}

	public int getNumberModels() {
		return numberModels;
	}

	public void setNumberModels(int numberModels) {
		this.numberModels = numberModels;
	}

	public int getOffsetStrings() {
		return offsetStrings;
	}

	public void setOffsetStrings(int offsetStrings) {
		this.offsetStrings = offsetStrings;
	}

	public int getNumberAkaoBlocks() {
		return numberAkaoBlocks;
	}

	public void setNumberAkaoBlocks(int numberAkaoBlocks) {
		this.numberAkaoBlocks = numberAkaoBlocks;
	}

	public int getScale() {
		return scale;
	}

	public void setScale(int scale) {
		this.scale = scale;
	}

	public int[] getBlank() {
		return blank;
	}

	public void setBlank(int[] blank) {
		this.blank = blank;
	}

	public String getCreatorName() {
		return creatorName;
	}

	public void setCreatorName(String creatorName) {
		this.creatorName = creatorName;
	}

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public String[] getEntitiesNames() {
		return entitiesNames;
	}

	public void setEntitiesName(String[] entitiesName) {
		this.entitiesNames = entitiesName;
	}

	public long[] getOffsetsAkaoBlocks() {
		return offsetsAkaoBlocks;
	}

	public void setOffsetsAkaoBlocks(long[] offsetsAkaoBlocks) {
		this.offsetsAkaoBlocks = offsetsAkaoBlocks;
	}

	public int[][] getTableScriptsOffsets() {
		return tableScriptsOffsets;
	}

	public void setTableScriptsOffsets(int[][] tableScriptsOffsets) {
		this.tableScriptsOffsets = tableScriptsOffsets;
	}

	public ArrayList<Byte> getBytes(){

		ArrayList<Byte> bytes = new ArrayList<>();

		// unknown u16: Always 0x0502
		bytes.add((byte) (unknown & 0xFF));
		bytes.add((byte) ((unknown >> 8) & 0xFF));

		//numberEntities; char Number of entities
		bytes.add((byte) (numberEntities & 0xFF));

		//numberModels; char Number of models
		bytes.add((byte) (numberModels & 0xFF));

		//offsetStrings; u16 Offset to strings
		bytes.add((byte) (offsetStrings & 0xFF));
		bytes.add((byte) ((offsetStrings >> 8) & 0xFF));

		//numberAkaoBlocks; u16 Specifies the number of Akao/tuto blocks/offsets
		bytes.add((byte) (numberAkaoBlocks & 0xFF));
		bytes.add((byte) ((numberAkaoBlocks >> 8) & 0xFF));

		//scale; u16 Scale of field. For move and talk calculation // (9bit fixed point).
		bytes.add((byte) (scale & 0xFF));
		bytes.add((byte) ((scale >> 8) & 0xFF));

		// blank; // u16[3]
		bytes.add((byte)0); bytes.add((byte)0);
		bytes.add((byte)0); bytes.add((byte)0);
		bytes.add((byte)0);	bytes.add((byte)0);

		//creatorName; char[8] Field creator (never shown)
		byte[] bCreatorName = creatorName.getBytes();
		for (int i = 0; i < 8; i++) {
			try {
				bytes.add(bCreatorName[i]);	
			} catch (IndexOutOfBoundsException e) {
				bytes.add((byte)0);
			}
		}

		//fieldName; char[8] Field creator (never shown)
		byte[] bFieldName = fieldName.getBytes();
		for (int i = 0; i < 8; i++) {
			try {
				bytes.add(bFieldName[i]);	
			} catch (IndexOutOfBoundsException e) {
				bytes.add((byte)0);
			}
		}

		//entitiesNames; char[numberEntities] Field entity names

		for(String entitieName : entitiesNames){
			byte[] bEntitieName = entitieName.getBytes();
			for (int i = 0; i < 8; i++) {
				try {
					bytes.add(bEntitieName[i]);	
				} catch (IndexOutOfBoundsException e) {
					bytes.add((byte)0);
				}
			}
		}
		
		//offsetsAkaoBlocks; u32[numberAkaoBlocks] Akao/Tuto block offsets
		for(long offsetAkaoBlock : offsetsAkaoBlocks){
			bytes.add((byte) (offsetAkaoBlock & 0xFF));
			bytes.add((byte) ((offsetAkaoBlock >> 8) & 0xFF));
			bytes.add((byte) ((offsetAkaoBlock >> 16) & 0xFF));
			bytes.add((byte) ((offsetAkaoBlock >> 32) & 0xFF));
		}
		
		//tableScriptsOffsets; u16[numberEntities][32]; Entity script entry points, or more explicitly, subroutine offsets		
		for (int[] scriptOffsets : tableScriptsOffsets) {
			for (int offset : scriptOffsets) {
				bytes.add((byte) (offset & 0xFF));
				bytes.add((byte) ((offset >> 8) & 0xFF));
			}
		}
		return bytes;
	}
}