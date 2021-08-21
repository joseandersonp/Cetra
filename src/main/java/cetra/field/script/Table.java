package cetra.field.script;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Hashtable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings("serial")
public class Table extends Hashtable<Long, String> {

	private File table;
	private int level;
	private boolean enableDTE;

	public Table(File table) throws NumberFormatException, IOException {
		this.table = table;
		initialize();
	}

	public Table(File table, boolean enableDTE) throws NumberFormatException, IOException {
		this.table = table;
		this.enableDTE = enableDTE;
		initialize();
	}

	public void initialize() throws NumberFormatException, IOException {

		FileReader in = new FileReader(table);		
		BufferedReader bin = new BufferedReader(in);

		String line = null;

		while ((line = bin.readLine()) != null) {

			// int level = 0;
			String parts[] = line.split("=", 2);

			long key = Long.parseLong(parts[0], 16);
			String value = parts[1];
			// level = parts[0].length() / 2;
			this.put(key, value);
			
			//System.out.println(key +": "+ value);

			this.level = 2;

		}
		bin.close();

		this.put(0XE0L, "{TAB}");
		this.put(0XE1L, "\t");

		if (!enableDTE) {
			this.put(0xE2L, ", ");
			this.put(0xE3L, ".\"");
			this.put(0xE4L, "...\"");
		}

		this.put(231L, "\n");
		this.put(232L, "\n{NEXT PAGE}\n");
		this.put(234L, "{Cloud}");
		this.put(235L, "{Barret}");
		this.put(236L, "{Tifa}");
		this.put(237L, "{Aerith}");
		this.put(238L, "{Red 13}");
		this.put(239L, "{Yuffie}");
		this.put(240L, "{Cait Sith}");
		this.put(241L, "{Vincent}");
		this.put(242L, "{Cid}");
		this.put(243L, "{Person 1}");
		this.put(244L, "{Person 2}");
		this.put(245L, "{Person 3}");

		this.put(246L, "{O}");
		this.put(247L, "{T}");
		this.put(248L, "{S}");
		this.put(249L, "{X}");
		this.put(255L, "{END}");

		this.put(65243L, "{MULTI}");
		this.put(65242L, "{BLINK}");
		this.put(65241L, "{WHITE}");
		this.put(65240L, "{YELLOW}");
		this.put(65239L, "{CYAN}");
		this.put(65238L, "{GREEN}");
		this.put(65237L, "{PURPLE}");
		this.put(65236L, "{RED}");
		this.put(65235L, "{BLUE}");
		this.put(0xFED2L, "{GRAY}");

		this.put(0xFEDCL, "{PAUSE}");
		this.put(0xFEDDL, "{%03d}");
		this.put(0xFEDEL, "{VAR1}");
		this.put(0xFEDFL, "{VAR2}");

		this.put(0xFEE1L, "{VAR3}");
		this.put(0xFEE9L, "{SPACED}");
		this.put(0xFEE2L, "{STR %04X:%04X}");
	}

	public String format(Collection<Byte> listBytes) {
		Byte[] bytes = new Byte[listBytes.size()];
		listBytes.toArray(bytes);
		return this.format(bytes);
	}
	
	public String format(Byte[] buf) {
		return format(buf,0,buf.length);
	}
	
	public String format(byte[] buf) {
		return format(buf,0,buf.length);
	}
	
	public String format(byte[] buf ,int i,int length) {		
		 Byte[] oBuf = new Byte[buf.length];
		 Arrays.setAll(oBuf, n -> buf[n]);
		 return format(oBuf ,i,length);
	}

	public String format(Byte[] buf ,int i,int length) {

		StringBuilder strBuilder = new StringBuilder();

		for (; i < length; i++) {
			
			long key = 0;

			for (int j = i; j < this.level + i; j++) {

				key += (long) (buf[j] & 0xFF);

				if (this.containsKey(key)) {

					String chars = this.get(key);
					if (get(0xFEDDL).equals(chars)) {
						chars = String.format(chars, buf[++j]);
					} else  if (0xFEE2L == key) {
						
						int lo = buf[++j];
						int hi = buf[++j];
						int offset = (hi << 8) | lo;
						
						lo = buf[++j];
						hi = buf[++j];
						int size = (hi << 8) | lo;
						
						chars = String.format(chars, offset, size);
					}
					
					strBuilder.append(chars);
					i = j;
					break;

				} else if (j == length - 1 || j == this.level + i - 1) {
					strBuilder.append(String.format("{0x%02X}", buf[i]));
					break;

				} else {
					key = key << 8;
				}
			}
		}
		return strBuilder.toString();
	}

	public ArrayList<Byte> parseList(String text, boolean forceDTE) {

		String subText = null;
		Pattern especialByte = Pattern.compile("\\{.+?\\}");
		ArrayList<Byte> bytes = new ArrayList<Byte>();

		int i = 0;
		while (i < text.length()) {

			byte[] bufBytes = null;

			if (text.charAt(i) == '{') {
				Matcher matcher = especialByte.matcher(text);
				if (matcher.find(i) && i == matcher.start(0)) {

					String specialValue = matcher.group(0);

					if (specialValue.matches("\\{0x([0-9]|[A-F]){2}\\}"))
						bufBytes = new byte[] { (byte) Integer.parseInt(specialValue.substring(3, 5), 16) };
					else if (specialValue.matches("\\{(\\d){3}\\}"))
						bufBytes = new byte[] { (byte) 0xFE, (byte) 0xDD, Byte.parseByte(specialValue.substring(1, 4)) };
					else if (specialValue.matches("\\{STR\\s[\\dA-Fa-f]{4}:[\\dA-Fa-f]{4}\\}")) {
						
						String sOffset = specialValue.substring(5,9);
						String sSize = specialValue.substring(10,14);
						
						int offset = Integer.parseInt(sOffset, 16);
						int size = Integer.parseInt(sSize, 16);
						
						bufBytes = new byte[6];
						bufBytes[0] = (byte) 0xFE;
						bufBytes[1] = (byte) 0xE2;
						bufBytes[2] = (byte) (offset & 0xFF);
						bufBytes[3] = (byte) (offset >>> 8);
						bufBytes[4] = (byte) (size & 0xFF);
						bufBytes[5] = (byte) (size >>> 8);
											
					} else
						bufBytes = getKeyBytes(specialValue);

					if (bufBytes != null)
						i += specialValue.length() - 1;

				}
			}

			if (bufBytes == null && i + 2 < text.length() && text.charAt(i) == '.') {
				subText = text.substring(i, i + 3);
				bufBytes = getKeyBytes(subText);
				if (bufBytes != null) {
					i += 2;
				}
			}

			if (enableDTE || forceDTE) {

				if (bufBytes == null && i + 1 < text.length()) {
					subText = text.substring(i, i + 2);
					bufBytes = getKeyBytes(subText);
					if (bufBytes != null) {
						i++;
					}
				}

			} else {

				if (bufBytes == null && i + 1 < text.length() && text.charAt(i) == ',') {
					subText = text.substring(i, i + 2);
					bufBytes = getKeyBytes(subText);
					if (bufBytes != null) {
						i++;
					}
				}

			}

			if (bufBytes == null) {
				bufBytes = getKeyBytes(Character.toString(text.charAt(i)));
			}

			if (bufBytes != null) {
				for (byte b : bufBytes) {
					bytes.add(b);
				}
			} else {
				bytes.add((byte) 0);
			}
			i++;
		}
		return bytes;
	}
	
	public byte[] parseBytes(String str) {
		return parseBytes(str, false);
	}	

	public byte[] parseBytes(String text, boolean forceDTE) {

		if (text == null || text.isEmpty()) {
			return new byte[] { (byte) 0xff };
		}

		text = text.replaceAll("\n\\{NEXT PAGE\\}\n", "{0xE8}");

		ArrayList<Byte> bytes = parseList(text, forceDTE);
		byte[] buf = new byte[bytes.size() + 1];

		for (int i = 0; i < bytes.size(); i++) {
			buf[i] = bytes.get(i);
		}

		buf[bytes.size()] = (byte) 0xFF;

		return buf;
	}

	private Long getKey(String value) {
		for (Long key : this.keySet()) {
			if (this.get(key).equals(value))
				return key;
		}
		return null;
	}

	private byte[] getKeyBytes(String value) {

		try {
			long key = getKey(value);

			byte[] buf;
			long range = 0;
			int levelCount = 0;

			while (levelCount < level) {
				levelCount++;
				range = (range << 8) + 0xFF;
				if (key <= range) {
					break;
				}
			}

			buf = new byte[levelCount];

			for (int i = 0; i < buf.length; i++) {
				buf[i] = (byte) ((key >> (8 * (--levelCount))) & 0xFF);
			}
			return buf;

		} catch (Exception e) {
			return null;
		}
	}
}
