package cetra.iso;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.zip.GZIPInputStream;

import com.jcraft.jzlib.Deflater;
import com.jcraft.jzlib.GZIPOutputStream;

import cetra.iso.descriptor.PrimaryVolumeDescriptor;
import cetra.iso.descriptor.TerminatorVolumeDescriptor;
import cetra.iso.descriptor.VolumeDescriptor;
import cetra.iso.record.DirectoryRecord;
import cetra.iso.record.PatchTableRecord;
import cetra.iso.sector.Format;
import cetra.iso.sector.Sector;
import cetra.util.Data;

public class ISOHandler {

	public static int SYSTEM_SECTORS = 16;

	private File fileIso;
	private PrimaryVolumeDescriptor primaryVolumeDescriptor;
	private ArrayList<PatchTableRecord> pathTable;
	private Format format;

	public ISOHandler(File isoFile) throws IOException{
		mount(isoFile);
	}

	public void mount(File file) throws IOException {

		this.fileIso = file;
		int sectorPointer = SYSTEM_SECTORS;
		format = Format.XA_MODE2_FORM1;

		RandomAccessFile fileImage = new RandomAccessFile(fileIso,"r");
		ArrayList<VolumeDescriptor> volumeDescriptors = new ArrayList<>();

		VolumeDescriptor volumeDescriptor;
		Sector sector;
		byte[] bufferSector;

		do {

			volumeDescriptor = null;
			fileImage.seek(format.size() * sectorPointer);
			bufferSector = new byte[format.size()];
			fileImage.read(bufferSector);

			sector = new Sector(bufferSector, format);

			switch(sector.getUserData()[0]){

			case 1:
				volumeDescriptor = new PrimaryVolumeDescriptor(sector.getUserData());	
				break;
			case -1:
				volumeDescriptor = new TerminatorVolumeDescriptor(sector.getUserData());
				break;
			}

			volumeDescriptors.add(volumeDescriptor);
			sectorPointer++;

		} while(!(volumeDescriptor instanceof TerminatorVolumeDescriptor));

		for (int i = 0; i < volumeDescriptors.size(); i++) {
			if (volumeDescriptors.get(i).getType() == 1){
				primaryVolumeDescriptor = (PrimaryVolumeDescriptor) volumeDescriptors.get(i);
				break;
			}
		}

		int sectorPathTable = (int) primaryVolumeDescriptor.getOccurrencePathTable();

		fileImage.seek(sectorPathTable * format.size());
		fileImage.read(bufferSector);
		sector = new Sector(bufferSector, format);

		int recordCount = 0;
		pathTable = new ArrayList<>();
		while (recordCount < primaryVolumeDescriptor.getPathTableSize()) {
			PatchTableRecord patchTableRecord = new PatchTableRecord();
			recordCount = patchTableRecord.setData(sector.getUserData(),recordCount);
			pathTable.add(patchTableRecord);
		}

		fileImage.close();
	}

	public ArrayList<PatchTableRecord> getPathTable() {
		return pathTable;
	}

	public PatchTableRecord getPathTableRecord(String nameDirectory) {
		for (PatchTableRecord patchTableRecord : pathTable) {
			if (patchTableRecord.getDirectoryId().equals(nameDirectory)){
				return patchTableRecord;
			}
		}	
		return null;
	}

	public ArrayList<DirectoryRecord> getDirectoryRecords(String nameDirectory) throws IOException{

		PatchTableRecord pathTableRecord = getPathTableRecord(nameDirectory);
		return getDirectoryRecords(pathTableRecord);

	}

	public ArrayList<DirectoryRecord> getDirectoryRecords(PatchTableRecord pathTableRecord) throws IOException{

		ArrayList<DirectoryRecord> directoryRecords = new ArrayList<DirectoryRecord>();

		if (pathTableRecord == null){
			return directoryRecords;
		}

		try (RandomAccessFile fileImage = new RandomAccessFile(fileIso,"r")) {

			byte[] bufferSector = new byte[format.size()];

			fileImage.seek(pathTableRecord.getExtentLocation() * format.size());

			fileImage.read(bufferSector);
			Sector sector = new Sector(bufferSector, format);

			DirectoryRecord fieldDirectoryRecord = new DirectoryRecord(sector.getUserData());

			directoryRecords.add(fieldDirectoryRecord);

			int countSector;
			int countRecord = fieldDirectoryRecord.getRecordLength();

			int sectorLength = (int)(fieldDirectoryRecord.getDataLength() / format.getUserDataLength());

			for(countSector = 0; countSector < sectorLength; countSector ++) {

				while (countRecord < format.getUserDataLength()) {

					if (sector.getUserData()[countRecord] == 0x00){
						countRecord++;
						continue;
					}

					DirectoryRecord directoryRecord = new DirectoryRecord();
					countRecord += directoryRecord.setData(sector.getUserData(), countRecord);
					directoryRecords.add(directoryRecord);

				}

				fileImage.read(bufferSector);
				sector = new Sector(bufferSector, format);
				countRecord = 0;

			}

		} catch (IOException e) {
			throw e;		
		}

		return directoryRecords;	
	}

	public long writeExtent(byte[] bufferFile, RandomAccessFile accessFileISO, long extentLocation) throws IOException {

		ByteArrayInputStream input = new ByteArrayInputStream(bufferFile);
		return writeExtent(input, accessFileISO, extentLocation);
	}

	public long writeExtent(InputStream input, RandomAccessFile accessFileISO, long extentLocation) throws IOException {

		byte[] bufferSector = new byte[format.size()];		
		accessFileISO.seek(extentLocation * format.size());

		while (input.available() > 0){

			accessFileISO.read(bufferSector);
			Sector sector = new Sector(bufferSector, format);

			int subHeader = (input.available() > format.getUserDataLength()) ? 2048 : 35072;
			sector.setSubHeader(subHeader);

			byte[] userData = new byte[format.getUserDataLength()];
			input.read(userData);
			sector.setUserData(userData);

			accessFileISO.seek(extentLocation * format.size());
			accessFileISO.write(sector.getData());
			extentLocation++;

		}
		input.close();
		return extentLocation;	
	}

	public void writeFilesInField(File fileDir) throws IOException{

		ArrayList<DirectoryRecord> directoryRecords = getDirectoryRecords("FIELD");

		RandomAccessFile fileImage = new RandomAccessFile(fileIso,"rw");

		byte[] bufferSector = new byte[format.size()];

		DirectoryRecord dr = null;

		ArrayList<DirectoryRecord> rootDirectoryRecords = new ArrayList<>();
		rootDirectoryRecords.add(directoryRecords.remove(0));
		rootDirectoryRecords.add(directoryRecords.remove(0));

		Collections.sort(directoryRecords, new Comparator<DirectoryRecord>() {
			@Override
			public int compare(DirectoryRecord dr1, DirectoryRecord dr2) {	
				return Long.compare(dr1.getExtentLocation(), dr2.getExtentLocation());
			}
		});

		DirectoryRecord directoryRecordField = directoryRecords.get(0);
		File fileField = new File(fileDir.getAbsolutePath() + File.separator + directoryRecordField.getFileName());

		int sizeFieldSector = (int) Math.ceil(fileField.length() / (double)format.getUserDataLength()); 

		long extentLocation = directoryRecordField.getExtentLocation() + sizeFieldSector;
		fileImage.seek(extentLocation * format.size());

		for (int i = 1; i < directoryRecords.size(); i++) {

			dr = directoryRecords.get(i);
			File file = new File(fileDir.getAbsolutePath() + File.separator + dr.getFileName());
			FileInputStream input = new FileInputStream(file);

			dr.setExtentLocation(extentLocation);
			dr.setDataLength(file.length());
			extentLocation = writeExtent(input, fileImage, extentLocation);

		}

		DataInputStream inField = new DataInputStream(new FileInputStream(fileField));

		int fileFieldSize = Integer.reverseBytes(inField.readInt());
		byte[] bufferField = new byte[fileFieldSize];

		int gzipDecompressCounter = Integer.reverseBytes(inField.readInt());
		GZIPInputStream inputGzip = new GZIPInputStream(inField);	

		byte[] buf = new byte[2048];
		int countRead = 0;
		int countWrite= 0;
		while((countRead = inputGzip.read(buf)) != -1){
			System.arraycopy(buf, 0, bufferField, countWrite, countRead);
			countWrite +=countRead;	
		}
		inField.close();

		int j = 1;
		for (int i = 0x3A5B8; i < 0x3EF80; i+=8) {
			Data.littleEndianIntToArray(bufferField, i, (int) directoryRecords.get(j).getExtentLocation());
			Data.littleEndianIntToArray(bufferField, i+4, (int) directoryRecords.get(j).getDataLength());
			j++;
		}

		for (int i = 0x3F08C; i < 0x3F0DC; i+=8) {
			Data.littleEndianIntToArray(bufferField, i, (int) directoryRecords.get(j).getExtentLocation());
			Data.littleEndianIntToArray(bufferField, i+4, (int) directoryRecords.get(j).getDataLength());
			j++;
		}	

		DirectoryRecord DirectoryRecordScus = findDirectoryRecord("SCUS_941(.+){3}");
		byte[] bufferScus = readExtent(DirectoryRecordScus);

		for (int i = 0x3954C; i < 0x3955C; i+=8) {
			Data.littleEndianIntToArray(bufferScus, i, (int) directoryRecords.get(j).getExtentLocation());
			Data.littleEndianIntToArray(bufferScus, i+4, (int) directoryRecords.get(j).getDataLength());
			j++;
		}

		/** TODO REMOVER NA PROXIMA VERS�O */
		bufferField[228428] = 30;


		directoryRecordField.setDataLength(bufferScus.length);
		extentLocation = DirectoryRecordScus.getExtentLocation();
		writeExtent(bufferScus, fileImage, extentLocation);

		ByteArrayOutputStream baOutputStream = new ByteArrayOutputStream();
		Deflater def = new Deflater(9, 31);
		GZIPOutputStream gzipOutputStream = new GZIPOutputStream(baOutputStream,def,1024,true);
		gzipOutputStream.write(bufferField);
		gzipOutputStream.close();

		byte[] bufferFieldComp = baOutputStream.toByteArray();

		byte[] bufferFieldCompH = new byte[bufferFieldComp.length + 8];
		Data.littleEndianIntToArray(bufferFieldCompH, 0, fileFieldSize);
		Data.littleEndianIntToArray(bufferFieldCompH, 4, gzipDecompressCounter);

		System.arraycopy(bufferFieldComp, 0, bufferFieldCompH, 8, bufferFieldComp.length);

		directoryRecordField.setDataLength(bufferFieldCompH.length);
		extentLocation = directoryRecordField.getExtentLocation();
		writeExtent(bufferFieldCompH, fileImage, extentLocation);


		Collections.sort(directoryRecords, new Comparator<DirectoryRecord>() {
			@Override
			public int compare(DirectoryRecord dr1, DirectoryRecord dr2) {	
				return dr1.getFileId().compareTo(dr2.getFileId());
			}
		});

		byte[] recordData = null;
		byte[] bufferUserData = new byte[format.getUserDataLength()];
		int indexUserData = 0;

		extentLocation = rootDirectoryRecords.get(0).getExtentLocation();
		fileImage.seek(extentLocation * format.size());

		for (int i = 0; i < 2; i++) {

			dr = rootDirectoryRecords.get(i);
			recordData = dr.getData();
			System.arraycopy(recordData, 0, bufferUserData, indexUserData, recordData.length);
			indexUserData += recordData.length;	

		} 

		for (int i = 0; i <= directoryRecords.size(); i++) {

			if(bufferUserData.length - indexUserData < dr.getRecordLength() || (i == directoryRecords.size())) {

				fileImage.read(bufferSector);

				Sector sector = new Sector(bufferSector, format);
				sector.setUserData(bufferUserData);

				fileImage.seek(extentLocation * format.size());
				fileImage.write(sector.getData());

				bufferUserData = new byte[format.getUserDataLength()];
				extentLocation++;
				indexUserData = 0;

				if (i == directoryRecords.size())
					break;
			}

			dr = directoryRecords.get(i);
			recordData = dr.getData();			
			System.arraycopy(recordData, 0, bufferUserData, indexUserData, recordData.length);
			indexUserData += recordData.length;

		}

		fileImage.close();
	}

	public byte[] readExtent(DirectoryRecord directoryRecord) throws IOException {

		if(directoryRecord == null)
			return null;

		int  dataLength     = (int) directoryRecord.getDataLength();
		int  userDataLength = format.getUserDataLength();
		int  countData = 0;

		Sector sector = null;
		byte[] bufferSector = new byte[format.size()];
		byte[] bufferFile =   new byte[dataLength];

		RandomAccessFile fileImage = new RandomAccessFile(fileIso,"r");
		fileImage.seek(directoryRecord.getExtentLocation() * format.size());

		while (countData < dataLength){

			if ((dataLength - countData) < userDataLength)
				userDataLength = (int)(dataLength - countData);

			fileImage.read(bufferSector);
			sector = new Sector(bufferSector, format);

			System.arraycopy(sector.getUserData(), 0, bufferFile, countData, userDataLength);

			countData += userDataLength;
		}		

		fileImage.close();
		return bufferFile;
	}

	public void readFiles(String pathDirFrom, String pathDirTo) throws IOException {

		ArrayList<DirectoryRecord> dirRecords = getDirectoryRecords(pathDirFrom);
		pathDirTo = (pathDirTo + File.separator).replaceAll("(\\\\\\\\|//)", File.separator);
		new File(pathDirTo).mkdirs();

		for (int j = 2; j < dirRecords.size() ; j++) {

			byte[] bytesFile = readExtent(dirRecords.get(j));
			FileOutputStream out = new FileOutputStream(pathDirTo + dirRecords.get(j).getFileId().replaceAll(";.*", ""));
			out.write(bytesFile);
			out.close();

		}
	}

	public DirectoryRecord findDirectoryRecord(String regexfileId) throws IOException{

		for (PatchTableRecord pathTableRecord : pathTable) {
			ArrayList<DirectoryRecord> directoryRecords = getDirectoryRecords(pathTableRecord);
			for (DirectoryRecord directoryRecord : directoryRecords) {
				if (directoryRecord.getFileId().matches(regexfileId)){
					return directoryRecord;
				}
			}
		}
		return null;
	}
	/*
	public static void main(String[] args) throws IOException {
		ISOHandler isoHandler = new ISOHandler(new File("C:/FFVII/Teste/ffvii.img"));
		//isoHandler.readFiles("FIELD","FIELD2");
		isoHandler.writeFilesInField(new File("C:/FFVII/Teste/FIELD"));
	}
	 */
}
