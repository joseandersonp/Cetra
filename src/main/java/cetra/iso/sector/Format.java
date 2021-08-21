package cetra.iso.sector;

public enum Format {

	XA_MODE2_FORM1(12, 4, 8, 2048, 4, 276);
	
	private int synchLength;
	private int headerLength;
	private int subHeaderLength;
	private int userDataLength;
	private int edcLength;
	private int eccLenght;

	private Format(int synchLength, int headerLength, int subHeaderLength, int userDataLength, int edcLength, int eccLength) {
		this.synchLength = synchLength;
		this.headerLength = headerLength;
		this.subHeaderLength = subHeaderLength;
		this.userDataLength = userDataLength;
		this.edcLength = edcLength;
		this.eccLenght = eccLength;
	}
	public int getSynchLength() {
		return synchLength;
	}
	public int getHeaderLength() {
		return headerLength;
	}
	public int getSubHeaderLength() {
		return subHeaderLength;
	}
	public int getUserDataLength() {
		return userDataLength;
	}
	public int getEdcLength() {
		return edcLength;
	}
	public int getEccLenght() {
		return eccLenght;
	}
	public int size() {
		return synchLength + headerLength + subHeaderLength + userDataLength + edcLength + eccLenght;
	}
}

