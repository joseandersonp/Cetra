package cetra.iso.record;

import java.lang.reflect.Array;
import java.util.Arrays;

import org.joda.time.DateTime;

import cetra.util.Data;

/**
 * <pre>
BP                     Field name                           Content
1                      Length of Directory Record (LEN-DR)  numerical value
2                      Extended Attribute Record Length     numerical value
3 to 10                Location of Extent                   numerical value
11 to 18               Data Length                          numerical value
19 to 25               Recording Date and Time              numerical values
26                     File Flags                           8 bits
27                     File Unit Size                       numerical value
28                     Interleave Gap Size                  numerical value
29 to 32               Volume Sequence Number               numerical value
33                     Length of File Identifier (LEN_FI)   numerical value
34 to (33+LEN_FI)      File Identifier                      d-characters, d1-characters, SEPARATOR 1, SEPARATOR 2, (00) or (01) byte
(34 + LEN_FI)          Padding Field                        (00) byte
(LEN_DR - LEN_SU + 1)  System Use                           LEN_SU byte	
to LEN_DR
 * </pre>
 */

public class DirectoryRecord {

	private int recordLength;
	private int extendedAttrLength;
	private long extentLocation;
	private long dataLength;
	private DateTime dateTime;
	private byte fileflags;
	private int fileUnitSize;
	private int interleaveGapSize;
	private int volumeSeqNumber;
	private int fileIdLength;
	private String fileId;
	private byte[] systemUse;
	
	private String fileName;	

	public DirectoryRecord(byte[] buffer) {
		setData(buffer, 0);
	}

	public DirectoryRecord() {
		super();
	}

	public int setData(byte[] buffer, int indexRecord) {

		recordLength = Array.getInt(buffer, indexRecord + 0);
		extendedAttrLength = Array.getByte(buffer, indexRecord + 1);
		extentLocation = Data.arrayToUnsignedInt(buffer, indexRecord + 6);
		dataLength = Data.arrayToUnsignedInt(buffer, indexRecord + 14);
		dateTime = Data.arrayTimeStampToDate(buffer, indexRecord + 18);
		fileflags = Array.getByte(buffer, indexRecord + 25);
		fileUnitSize = Array.getInt(buffer, indexRecord + 26);
		interleaveGapSize = Array.getInt(buffer, indexRecord + 27);
		volumeSeqNumber = Data.arrayToUnsignedShort(buffer, indexRecord + 30);
		fileIdLength = Array.getInt(buffer, indexRecord + 32);
		fileId = Data.arrayToString(buffer, indexRecord + 33, fileIdLength);

		int indexSystemUser = 33 + indexRecord + fileIdLength;
		if (fileIdLength % 2 == 0)
			indexSystemUser++;

		if (indexSystemUser < recordLength + indexRecord)
			systemUse = Arrays.copyOfRange(buffer, indexSystemUser, recordLength + indexRecord);
		
		fileName = fileId.replaceAll(";.*", "");
		

		return recordLength;
	}

	public int getRecordLength() {
		return recordLength;
	}

	public int getExtendedAttrLength() {
		return extendedAttrLength;
	}

	public long getExtentLocation() {
		return extentLocation;
	}

	public void setExtentLocation(long extentLocation) {
		this.extentLocation = extentLocation;
	}

	public long getDataLength() {
		return dataLength;
	}

	public void setDataLength(long dataLength) {
		this.dataLength = dataLength;
	}

	public DateTime getDateTime() {
		return dateTime;
	}

	public byte getFileflags() {
		return fileflags;
	}

	public int getFileUnitSize() {
		return fileUnitSize;
	}

	public int getInterleaveGapSize() {
		return interleaveGapSize;
	}

	public int getVolumeSeqNumber() {
		return volumeSeqNumber;
	}

	public int getFileIdLength() {
		return fileIdLength;
	}

	public String getFileId() {
		return fileId;
	}
	
	public String getFileName() {
		return fileName;
	}

	public byte[] getSystemUse() {
		return systemUse;
	}

	public byte[] getData() {

		byte[] data = new byte[recordLength];

		data[0] = (byte) recordLength;
		data[1] = (byte) extendedAttrLength;

		Data.littleEndianIntToArray(data, 2, (int) extentLocation);
		Data.intToArray(data, 6, (int) extentLocation);

		Data.littleEndianIntToArray(data, 10, (int) dataLength);
		Data.intToArray(data, 14, (int) dataLength);

		Data.dateTimeStampToArray(data, 18, dateTime);

		data[25] = fileflags;
		data[26] = (byte) fileUnitSize;
		data[27] = (byte) interleaveGapSize;

		Data.littleEndianShortToArray(data, 28, (short) volumeSeqNumber);
		Data.shortToArray(data, 30, (short) volumeSeqNumber);

		data[32] = (byte) fileIdLength;

		Data.stringToArray(data, 33, fileId);

		int indexSystemUser = 33 + fileIdLength;
		if (fileIdLength % 2 == 0)
			indexSystemUser++;

		System.arraycopy(systemUse, 0, data, indexSystemUser, systemUse.length);

		return data;
	}

	@Override
	public String toString() {

		StringBuffer sb = new StringBuffer();
		sb.append("\nFIELD NAME                        CONTENT");
		sb.append("\nLength of Directory Record      : ").append(recordLength);
		sb.append("\nExtended Attribute Record Length: ").append(extendedAttrLength);
		sb.append("\nLocation of Extent              : ").append(extentLocation);
		sb.append("\nData Length                     : ").append(dataLength);
		sb.append("\nRecording Date and Time         : ").append(dateTime);
		sb.append("\nFile Flags                      : ").append(fileflags);
		sb.append("\nFile Unit Size                  : ").append(fileUnitSize);
		sb.append("\nInterleave Gap Size             : ").append(interleaveGapSize);
		sb.append("\nVolume Sequence Number          : ").append(volumeSeqNumber);
		sb.append("\nLength of File Identifier       : ").append(fileIdLength);
		sb.append("\nFile Identifier                 : ").append(fileId);
		sb.append("\nSystem Use                      : ").append(systemUse);
		sb.append("\n");
		return sb.toString();

	}
}
