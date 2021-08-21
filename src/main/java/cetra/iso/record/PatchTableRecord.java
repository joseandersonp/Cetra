package cetra.iso.record;

import java.lang.reflect.Array;

import cetra.util.Data;

/**
 * <pre>
BP					Field name								Content
1					Length of Directory Identifier (LEN_DI)	numerical value
2					Extended Attribute Record Length		numerical value
3 to 6				Location of Extent						numerical value
7 to 8				Parent Directory Number					numerical value
9 to (8 + LEN_DI)	Directory Identifier					d-characters, d1-characters, (00) byte
(9 + LEN_DI)		Padding Field							(00) byte
 * </pre>
 */
public class PatchTableRecord {

	private int directoryIdLength;
	private int extendedAttrLength;
	private long extentLocation;
	private int parentDirectoryNumber;
	private String directoryId;

	public PatchTableRecord(byte[] buffer, int indexRecord) {
		setData(buffer, indexRecord);
	}

	public PatchTableRecord() {
		super();
	}

	public int setData(byte[] buffer, int recordCount) {

		directoryIdLength = Array.getInt(buffer, recordCount++);
		extendedAttrLength = Array.getInt(buffer, recordCount++);
		extentLocation = Data.arrayToUnsignedInt(buffer, recordCount);
		recordCount += 4;
		parentDirectoryNumber = Data.arrayToUnsignedShort(buffer, recordCount);
		recordCount += 2;
		directoryId = Data.arrayToString(buffer, recordCount, directoryIdLength);
		recordCount += directoryIdLength;

		if (directoryIdLength % 2 == 1)
			recordCount++;

		return recordCount;
	}

	public int getDirectoryIdLength() {
		return directoryIdLength;
	}

	public int getExtendedAttrLength() {
		return extendedAttrLength;
	}

	public long getExtentLocation() {
		return extentLocation;
	}

	public int getParentDirectoryNumber() {
		return parentDirectoryNumber;
	}

	public String getDirectoryId() {
		return directoryId;
	}

	@Override
	public String toString() {

		StringBuffer sb = new StringBuffer();
		sb.append("\nFIELD NAME                        CONTENT");														
		sb.append("\nLength of Directory Identifier:   ").append(directoryIdLength);
		sb.append("\nExtended Attribute Record Length: ").append(extendedAttrLength);
		sb.append("\nLocation of Extent:               ").append(extentLocation);
		sb.append("\nParent Directory Number:          ").append(parentDirectoryNumber);
		sb.append("\nDirectory Identifier:             ").append(directoryId);
		sb.append("\n");

		return sb.toString();
	}

}
