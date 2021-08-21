package cetra.compression;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.jcraft.jzlib.Deflater;
import com.jcraft.jzlib.GZIPInputStream;
import com.jcraft.jzlib.GZIPOutputStream;

public class Gzip {

	public static int decode(InputStream in, OutputStream out) throws IOException {

		GZIPInputStream gin = new GZIPInputStream(in);

		byte buffer[] = new byte[1024];
		int cin = 0;
		int cout = 0;

		while ((cin = gin.read(buffer)) != -1) {
			out.write(buffer, 0, cin);
			cout += cin;
		}

		gin.close();

		return cout;

	}

	public static int encode(InputStream in, OutputStream out) throws IOException {

		Deflater def = new Deflater(9, 31);
		GZIPOutputStream gout = new GZIPOutputStream(out, def, 1024, true);

		byte buffer[] = new byte[2048];
		int cin = 0;
		int cout = 0;

		while ((cin = in.read(buffer)) != -1) {
			gout.write(buffer, 0, cin);
			cout += cin;
		}

		gout.close();

		return cout;

	}
	
	
	
	
}