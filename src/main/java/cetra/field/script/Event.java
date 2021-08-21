package cetra.field.script;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class Event {

	private EventScript[][] tableEventScripts;

	public Event(byte[] buf, int[][] tableScriptsOffsets, int offsetEOS) throws IOException {
		initialize(buf, tableScriptsOffsets, offsetEOS);
	}

	public void initialize(byte[] buf, int[][] tableScriptsOffsets, int offsetEOS) throws IOException {

		tableEventScripts = new EventScript[tableScriptsOffsets.length][32];

		for (int i = 0; i < tableScriptsOffsets.length; i++) {

			int jAux = 0;
			for (int j = 0; j < tableScriptsOffsets[i].length; j++) {

				int from = tableScriptsOffsets[i][j];
				int to;

				if (j + 1 < tableScriptsOffsets[i].length)
					to = tableScriptsOffsets[i][j + 1];
				else if (i + 1 < tableScriptsOffsets.length)
					to = tableScriptsOffsets[i + 1][0];
				else
					to = offsetEOS;

				//System.out.printf("Script id: %d [de: %02X at� %02X]\n", i, from, to);
				if (from == to) {
					jAux++;
				} else {
					EventScript eventScritp = new EventScript(Arrays.copyOfRange(buf, from, to));
					//System.out.println(eventScritp);
					tableEventScripts[i][j - jAux] = eventScritp;
					jAux = 0;
				}
			}
		}
	}

	public EventScript[][] getTableEventScripts() {
		return tableEventScripts;
	}

	public ArrayList<Byte> getBytes(){

		ArrayList<Byte> bytes = new ArrayList<>();

		for (EventScript[] eventScripts : tableEventScripts){

			if (eventScripts != null)
				for (EventScript eventScript : eventScripts) {
					
					if (eventScript != null)
						for (byte binCode : eventScript.getBytes()){
							bytes.add(binCode);
						}
				}
		}

		return bytes;
	}
}