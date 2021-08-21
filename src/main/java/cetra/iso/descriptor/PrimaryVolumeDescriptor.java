package cetra.iso.descriptor;

import java.lang.reflect.Array;
import java.util.Arrays;

import org.joda.time.DateTime;

import cetra.iso.record.DirectoryRecord;
import cetra.util.Data;


/**
 <pre>
  	BP				Field Name												Content
	1				Volume Descriptor Type									numerical value
	2 to 6  	    Standard Identifier										CDOO01
	7				Volume Descriptor Version								numerical value
	8				Unused Field											(OO) byte
	9 to 40			System identifier										a-characters
	41 to 72		Volume Identifier										d-characters
	73 to 80 		Unused Field											(OO) bytes
	81 to 88 		Volume Space Size										numerical value
	89 to 120 		Unused Field											(OO) bytes
	121 to 124	 	Volume Set Size											numerical value
	125 to 128	 	Volume Sequence Number									numerical value
	129 to 132	 	Logical Block Size										numerical value
	133 to 140	 	Path Table Size											numerical value
	141 to 144	 	Location of Occurrence of Type L Path Table				numerical value
	145 to 148	 	Location of Optional Occurrence of Type L Path Table	numerical value
	149 to 152	 	Location of Occurrence of Type M Path Table				numerical value
	153 to 156	 	Location of Optional Occurrence of Type M Path Table	numerical value
	157 to 190	 	Directory Record for Root Directory						34 bytes
	191 to 318		Volume Set Identifier									d-characters 
	319 to 446		Publisher Identifier									a-characters
	447 to 574	 	Data Preparer Identifier								a-characters
	575 to 702		Application Identifier									a-characters
	703 to 739		Copyright File Identifier								d-characters, SEPARATOR 1, SEPARATOR 2
	740 to 776		Abstract File Identifier								d-characters, SEPARATOR 1, SEPARATOR 2
	777 to 813		Bibliographic File Identifier							d-characters, SEPARATOR 1, SEPARATOR 2
	814 to 830		Volume Creation Date and Time							Digit(s), numerical value
	831 to 847		Volume Modification Date and Time					 	Digit(s), numerical value
	848 to 864		Volume Expiration Date and Time							Digit(s), numerical value
	865 to 881		Volume Effective Date and Time							Digit(s), numerical value
	882				File Structure Version									numerical value
	883				(Reserved for future standardization)					(OO) byte
	884 to 1 395	Application Use											not specified
	1 396 to 2 048	(Reserved for future standardization)					(OO) byte
  </pre>
 */


public class PrimaryVolumeDescriptor extends VolumeDescriptor{
	
	private String systemId;
	private String volumeId;
	private long volumeSpaceSize;
	private int volumeSetSize;
	private int volumeSequenceNumber;
	private int logicalBlockSize;
	private long pathTableSize;
	private long occurrencePathTable;
	private long optOccurrencePathTable;
	private DirectoryRecord rootDirectoryRecord;
	private String volumeSetId;
	private String publisherId;
	private String preparerId;
	private String applicationId;
	private String copyrightFileId;
	private String abstractFileId;
	private String bibliographicFileId;
	private DateTime creationDate;
	private DateTime modificationDate; 
	private DateTime expirationDate;
	private DateTime effectiveDate;
	private int fileStructureVersion;
	private byte[] applicationUse;
	
	public PrimaryVolumeDescriptor(byte[] buffer) {
		
		super(buffer);
		
		systemId 				= Data.arrayToString(buffer, 8, 32).trim();
		volumeId				= Data.arrayToString(buffer, 40, 32).trim();
		volumeSpaceSize			= Data.arrayToUnsignedInt(buffer, 84);
		volumeSetSize			= Data.arrayToUnsignedShort(buffer, 122);
		volumeSequenceNumber	= Data.arrayToUnsignedShort(buffer, 126);
		logicalBlockSize		= Data.arrayToUnsignedShort(buffer, 130);
		pathTableSize			= Data.arrayToUnsignedInt(buffer, 136);
		occurrencePathTable		= Data.arrayToUnsignedInt(buffer, 148);
		optOccurrencePathTable	= Data.arrayToUnsignedInt(buffer, 152);
		rootDirectoryRecord		= new DirectoryRecord(Arrays.copyOfRange(buffer, 156, 190));
		volumeSetId				= Data.arrayToString(buffer, 190, 128).trim();
		publisherId				= Data.arrayToString(buffer, 318, 128).trim();
		preparerId				= Data.arrayToString(buffer, 446, 128).trim();
		applicationId			= Data.arrayToString(buffer, 574, 128).trim();
		copyrightFileId			= Data.arrayToString(buffer, 702, 37).trim();
		abstractFileId			= Data.arrayToString(buffer, 739, 37).trim();
		bibliographicFileId		= Data.arrayToString(buffer, 776, 37).trim();
		creationDate			= Data.arrayToDate(buffer, 813);
		modificationDate 		= Data.arrayToDate(buffer, 830); 
		expirationDate 			= Data.arrayToDate(buffer, 847);
		effectiveDate 			= Data.arrayToDate(buffer, 864);
		fileStructureVersion	= Array.getInt(buffer, 	881);
		applicationUse 			= Arrays.copyOfRange(buffer, 883, 1395);
	}

	public String getSystemId() {
		return systemId;
	}

	public void setSystemId(String systemId) {
		this.systemId = systemId;
	}

	public String getVolumeId() {
		return volumeId;
	}

	public void setVolumeId(String volumeId) {
		this.volumeId = volumeId;
	}

	public long getVolumeSpaceSize() {
		return volumeSpaceSize;
	}

	public void setVolumeSpaceSize(long volumeSpaceSize) {
		this.volumeSpaceSize = volumeSpaceSize;
	}

	public int getVolumeSetSize() {
		return volumeSetSize;
	}

	public void setVolumeSetSize(int volumeSetSize) {
		this.volumeSetSize = volumeSetSize;
	}

	public int getVolumeSequenceNumber() {
		return volumeSequenceNumber;
	}

	public void setVolumeSequenceNumber(int volumeSequenceNumber) {
		this.volumeSequenceNumber = volumeSequenceNumber;
	}

	public int getLogicalBlockSize() {
		return logicalBlockSize;
	}

	public void setLogicalBlockSize(int logicalBlockSize) {
		this.logicalBlockSize = logicalBlockSize;
	}

	public long getPathTableSize() {
		return pathTableSize;
	}

	public void setPathTableSize(long pathTableSize) {
		this.pathTableSize = pathTableSize;
	}

	public long getOccurrencePathTable() {
		return occurrencePathTable;
	}

	public void setOccurrencePathTable(long occurrencePathTable) {
		this.occurrencePathTable = occurrencePathTable;
	}

	public long getOptOccurrencePathTable() {
		return optOccurrencePathTable;
	}

	public void setOptOccurrencePathTable(long optOccurrencePathTable) {
		this.optOccurrencePathTable = optOccurrencePathTable;
	}

	public DirectoryRecord getRootDirectoryRecord() {
		return rootDirectoryRecord;
	}

	public void setRootDirectoryRecord(DirectoryRecord rootDirectoryRecord) {
		this.rootDirectoryRecord = rootDirectoryRecord;
	}

	public String getVolumeSetId() {
		return volumeSetId;
	}

	public void setVolumeSetId(String volumeSetId) {
		this.volumeSetId = volumeSetId;
	}

	public String getPublisherId() {
		return publisherId;
	}

	public void setPublisherId(String publisherId) {
		this.publisherId = publisherId;
	}

	public String getPreparerId() {
		return preparerId;
	}

	public void setPreparerId(String preparerId) {
		this.preparerId = preparerId;
	}

	public String getApplicationId() {
		return applicationId;
	}

	public void setApplicationId(String applicationId) {
		this.applicationId = applicationId;
	}

	public String getCopyrightFileId() {
		return copyrightFileId;
	}

	public void setCopyrightFileId(String copyrightFileId) {
		this.copyrightFileId = copyrightFileId;
	}

	public String getAbstractFileId() {
		return abstractFileId;
	}

	public void setAbstractFileId(String abstractFileId) {
		this.abstractFileId = abstractFileId;
	}

	public String getBibliographicFileId() {
		return bibliographicFileId;
	}

	public void setBibliographicFileId(String bibliographicFileId) {
		this.bibliographicFileId = bibliographicFileId;
	}

	public DateTime getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(DateTime creationDate) {
		this.creationDate = creationDate;
	}

	public DateTime getModificationDate() {
		return modificationDate;
	}

	public void setModificationDate(DateTime modificationDate) {
		this.modificationDate = modificationDate;
	}

	public DateTime getExpirationDate() {
		return expirationDate;
	}

	public void setExpirationDate(DateTime expirationDate) {
		this.expirationDate = expirationDate;
	}

	public DateTime getEffectiveDate() {
		return effectiveDate;
	}

	public void setEffectiveDate(DateTime effectiveDate) {
		this.effectiveDate = effectiveDate;
	}

	public int getFileStructureVersion() {
		return fileStructureVersion;
	}

	public void setFileStructureVersion(int fileStructureVersion) {
		this.fileStructureVersion = fileStructureVersion;
	}

	public byte[] getApplicationUse() {
		return applicationUse;
	}

	public void setApplicationUse(byte[] applicationUse) {
		this.applicationUse = applicationUse;
	}
	
}
