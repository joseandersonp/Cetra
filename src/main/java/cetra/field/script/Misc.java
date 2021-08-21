package cetra.field.script;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Misc {

	private Map<Integer, Object> musicTuts;

	public Misc(byte[] data, long[] offsetsMisc, int indexEnd) throws IOException {	
		initialize(data, offsetsMisc, indexEnd);
	}

	private void initialize(byte[] buf,long[] offsetsMisc, int end) throws IOException {

		musicTuts = new HashMap<>();

		int lengthSections = offsetsMisc.length;

		int countTuto = 0;
		for (int i = 0; i < lengthSections; i++) {

			int from = (int) offsetsMisc[i];
			int to =(int)((i+1 < lengthSections) ? offsetsMisc[i+1] : end); 

			byte[] section = Arrays.copyOfRange(buf, from, to);
			String header  = new String(Arrays.copyOf(section, 4));

			if (header.equals("AKAO"))
				musicTuts.put(i, section);
			else 
				musicTuts.put(i, new Tutorial(countTuto++, section));
		}	
	}

	public byte[][] getBytes() {

		byte[][] sections = new byte[musicTuts.size()][];

		for (int i = 0; i < musicTuts.size(); i++) {
			if (musicTuts.get(i) instanceof Tutorial)
				sections[i] = ((Tutorial) musicTuts.get(i)).getbytes();
			else
				sections[i] = (byte[]) musicTuts.get(i);
		}

		return sections;
	}
	
	public List<Tutorial> getTutorials() {
		
		ArrayList<Tutorial> tutorials = new ArrayList<>();
		for (int i = 0; i < musicTuts.size(); i++) {
			if (musicTuts.get(i) instanceof Tutorial){
				tutorials.add((Tutorial)musicTuts.get(i));
			}
		}
		return tutorials;
	}
}

