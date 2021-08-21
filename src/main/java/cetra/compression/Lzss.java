package cetra.compression;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class Lzss {

	private static int N = 4096; /* tamanho do buffer circular */
	private static int F = 18; /* limite m�ximo para match_length */	
	private static int THRESHOLD = 2; /*
										 * codifica��o da string num par (offset, tamanho) se match_length for maior que
										 * THRESHOLD
										 */
	private static int NIL = N; /* indice para a raiz de uma binary search trees */

	private static byte[] text_buf; /*
									 * buffer circular de tamanho N, com F-1 bytes extra para facilitar a compara��o
									 * de strings
									 */
	private static int match_position, match_length; /*
														 * da correspondencia mais longa. Este s�o definidos pelo
														 * procedimento InsertNode().
														 */

	/*
	 * filho esquerdo e direito & pai -- constituem as binary search trees.
	 */
	private static int[] lson;
	private static int[] rson;
	private static int[] dad;

	private static int EOF = -1;

	private static void initalizeBuffers() {

		text_buf = new byte[N + F - 1];

		lson = new int[N + 1];
		rson = new int[N + 257];
		dad = new int[N + 1];

		match_position = 0;
		match_length = 0;

		int i;

		/*
		 * Para i = 0 at� N - 1, rson[i] and lson[i] v�o ser o filho direito e esquerdo
		 * do nodo i. Este nodos n�o precisam de ser inicializados. Tamb�m, dad[i] � o
		 * pai do nodo i. Estes s�o inicializados a NIL (= N), que significa 'n�o
		 * usado'. Para i = 0 at� 255, rson[N + i + 1] � a raiz da �rvore para strings
		 * que come�am com o caracter i. Estes s�o inicializados a NIL.
		 */

		for (i = N + 1; i <= N + 256; i++)
			rson[i] = NIL;
		for (i = 0; i < N; i++)
			dad[i] = NIL;
	}

	private static void destroyBuffers() {

		text_buf = null;
		lson = null;
		rson = null;
		dad = null;

	}

	private static void insertNode(int r)
	/*
	 * Insere uma string de tamanho F, text_buf[r..r+F-1], numa das �rvores
	 * (text_buf[r] �rvore) e retorna a maior correspondencia atraves das variaveis
	 * globais match_position and match_length. Se match_length = F, ent�o remove o
	 * antigo nodo e insere o novo, porque o antigo ser� eliminado anteriormente.
	 */
	{
		int i, p, cmp;

		cmp = 1;
		p = N + 1 + (text_buf[r] & 0xFF);
		rson[r] = lson[r] = NIL;
		match_length = 0;
		for (;;) {

			if (cmp >= 0) {
				if (rson[p] != NIL)
					p = rson[p];
				else {
					rson[p] = r;
					dad[r] = p;

					return;
				}
			} else {
				if (lson[p] != NIL)
					p = lson[p];
				else {
					lson[p] = r;
					dad[r] = p;
					return;
				}
			}
			for (i = 1; i < F; i++) {

				int ri = text_buf[r + i] & 0xFF;
				int pi = text_buf[p + i] & 0xFF;

				if ((cmp = ri - pi) != 0)
					break;
			}
			if (i > match_length) {

				match_position = p;
				if ((match_length = i) >= F)
					break;

			}
		}
		dad[r] = dad[p];
		lson[r] = lson[p];
		rson[r] = rson[p];
		dad[lson[p]] = r;
		dad[rson[p]] = r;
		if (rson[dad[p]] == p)
			rson[dad[p]] = r;
		else
			lson[dad[p]] = r;
		dad[p] = NIL; /* remover p */

	}

	private static void deleteNode(int p) /* elimina o nodo p da �rvore */
	{

		int q;

		if (dad[p] == NIL) {
			return; /* n�o est� na �rvore */
		}

		if (rson[p] == NIL)
			q = lson[p];
		else if (lson[p] == NIL)
			q = rson[p];
		else {
			q = lson[p];
			if (rson[q] != NIL) {
				do {
					q = rson[q];
				} while (rson[q] != NIL);
				rson[dad[q]] = lson[q];
				dad[lson[q]] = dad[q];
				lson[q] = lson[p];
				dad[lson[p]] = q;
			}
			rson[q] = rson[p];
			dad[rson[p]] = q;
		}
		dad[q] = dad[p];
		if (rson[dad[p]] == p)
			rson[dad[p]] = q;
		else
			lson[dad[p]] = q;
		dad[p] = NIL;

	}

	public static int encode(InputStream in, OutputStream out) throws IOException {

		int textsize = 0; /* contador para o tamanho do texto */
		int codesize = 0; /* contador do tamanho do c�digo */
		int printcount = 0; /* contador para verificar o progresso a cada 1Kbyte */

		int i, c, len, r, s, last_match_length, code_buf_ptr;
		byte[] code_buf = new byte[17];
		byte mask;

		initalizeBuffers(); /* inicializar as �rvores */
		code_buf[0] = 0; /*
							 * code_buf[1..16] salva 8 unidades de c�digo, e code_buf[0] funciona como 8
							 * flags, "1" respresenta que a unidade � uma letra n�o codificada (1 byte), "0"
							 * representa um par(offset, tamanho) (2 bytes). Assim sendo, 8 unidades de
							 * c�digos ncessitam no m�ximo 16bytes de c�digo.
							 */
		code_buf_ptr = mask = 1;
		s = 0;
		r = N - F;
		for (i = s; i < r; i++)
			text_buf[i] = 0x0; /*
								 * Clear the buffer with any character that will appear often.
								 */
		for (len = 0; len < F && (c = in.read()) != EOF; len++)
			text_buf[r + len] = (byte) c; /*
											 * Read F bytes into the last F bytes of the buffer
											 */
		if ((textsize = len) == 0)
			return 0; /* text of size zero */
		for (i = 1; i <= F; i++)
			insertNode(r - i); /*
								 * Insert the F strings, each of which begins with one or more 'space'
								 * characters. Note the order in which these strings are inserted. This way,
								 * degenerate trees will be less likely to occur.
								 */
		insertNode(r); /*
						 * Finally, insert the whole string just read. The global variables match_length
						 * and match_position are set.
						 */
		do {
			if (match_length > len)
				match_length = len; /*
									 * match_length pode muito grande a medida que se aproxima o final do texto.
									 */
			if (match_length <= THRESHOLD) {
				match_length = 1; /* Correspondencia n�o � grande o suficiente. Enviar 1 byte. */
				code_buf[0] |= mask; /* 'Enviar 1 byte' flag */
				code_buf[code_buf_ptr++] = text_buf[r]; /* Enviar sem codifica��o. */
			} else {
				code_buf[code_buf_ptr++] = (byte) match_position;
				code_buf[code_buf_ptr++] = (byte) (((match_position >> 4) & 0xf0)
						| (match_length - (THRESHOLD + 1))); /* Enviar o par (offset, tamanho). */
			}
			if ((mask <<= 1) == 0) { /* Deslocar a m�scara 1bit. */
				for (i = 0; i < code_buf_ptr; i++) /* Enviar no m�ximo 8 unidades de c�digo */
					out.write(code_buf[i]);
				codesize += code_buf_ptr;
				code_buf[0] = 0;
				code_buf_ptr = mask = 1;
			}
			last_match_length = match_length;

			try {

				for (i = 0; i < last_match_length && (c = in.read()) != EOF; i++) {
					deleteNode(s); /* Eliminar a string antiga e */
					text_buf[s] = (byte) c; /* ler os novos bytes */
					if (s < F - 1)
						text_buf[s + N] = (byte) c; /*
													 * Se a posi��o se encontra perto do final do buffer, extender o
													 * buffer para facilitar a compara��o da string.
													 */
					s = (s + 1) & (N - 1);
					r = (r + 1) & (N - 1);
					/* Sendo um buffer circular, incrementar a posi��o. */
					insertNode(r); /* Inserir a string presente em text_buf[r..r+F-1] na �rvore */
				}

			} catch (EOFException e) {
			}

			if ((textsize += i) > printcount) {
				// System.out.printf("%12d\r", textsize);
				printcount += 1024;
				/*
				 * Devolve o estado cada vez que o tamanho do texto exceda multiplos de 1024.
				 */
			}
			while (i++ < last_match_length) { /* Depois de todo o texto ser processado, */
				deleteNode(s); /* n�o h� necessidade de ler, mas */
				s = (s + 1) & (N - 1);
				r = (r + 1) & (N - 1);
				if (--len > 0)
					insertNode(r); /* o buffer pode n�o estar vazio. */
			}
		} while (len > 0); /* Enquanto o tamanho da string a processar ser 0 */

		if (code_buf_ptr > 1) { /* Enviar o restante c�digo. */
			for (i = 0; i < code_buf_ptr; i++)
				out.write(code_buf[i]);
			codesize += code_buf_ptr;
		}
		// System.out.printf("In : %d bytes\n", textsize); /* Conclu�da a codifica��o */
		// System.out.printf("Out: %d bytes\n", codesize);
		// System.out.printf("Out/In: %.3f\n", (double) codesize / textsize);

		destroyBuffers();

		return codesize;
	}

	/* Processo inverso da função Encode(). */
	public static void decode(InputStream in, OutputStream out) throws IOException {

		initalizeBuffers();

		int i, j, k, r, c;
		long flags;

		r = N - F;
		flags = 0;
		for (;;) {
			if (((flags >>= 1) & 256) == 0) {
				if ((c = in.read()) == EOF)
					break;
				flags = c | 0xff00; /* contagem das flags até 8 */
			}
			if ((flags & 1) > 0) {
				if ((c = in.read()) == EOF)
					break;
				out.write(c);
				text_buf[r++] = (byte) c;
				r &= (N - 1);
			} else {
				if ((i = in.read()) == EOF)
					break;
				if ((j = in.read()) == EOF)
					break;
				i |= ((j & 0xf0) << 4);
				j = (j & 0x0f) + THRESHOLD;

				for (k = 0; k <= j; k++) {
					c = (text_buf[(i + k) & (N - 1)]) & 0xFF;
					out.write(c);
					text_buf[r++] = (byte) c;
					r &= (N - 1);
				}

			}
		}
		destroyBuffers();
	}

	public static int decodeBuffers(byte[] inBuffer, byte[] outBuffer) throws IOException {

		byte[] textBuf = new byte[N + F - 1];

		int i, j, k, r, bi = 0, bo = 0;
		long flags;

		r = N - F;
		flags = 0;

		while (true) {

			if (((flags >>= 1) & 256) == 0) {
				if (bi == inBuffer.length)
					break;
				flags = (inBuffer[bi++] & 0xFF) | 0xff00; /* contagem das flags até 8 */
			}
			if ((flags & 1) > 0) {
				if (bi == inBuffer.length)
					break;
				outBuffer[bo] = inBuffer[bi++];
				textBuf[r++] = outBuffer[bo++];
				r &= (N - 1);

			} else {

				if (bi == inBuffer.length)
					break;
				i = inBuffer[bi++] & 0xFF;

				if (bi == inBuffer.length)
					break;
				j = inBuffer[bi++] & 0xFF;

				i |= ((j & 0xf0) << 4);
				j = (j & 0x0f) + THRESHOLD;

				for (k = 0; k <= j; k++) {
					byte b = (textBuf[(i + k) & (N - 1)]);
					outBuffer[bo++] = b;
					textBuf[r++] = b;
					r &= (N - 1);
				}
			}
		}

		return bo;
	}
	/*
	 * public static void main(String[] args) throws IOException {
	 * 
	 * InputStream in = new FileInputStream("C:\\FFVII\\Teste\\MD1STIN.DEC");
	 * OutputStream out = new FileOutputStream("C:\\FFVII\\Teste\\MD1STIN.MIN8");
	 * 
	 * out.write(0); out.write(0); out.write(0); out.write(0);
	 * 
	 * encode(in, out);
	 * 
	 * in.close(); out.close();
	 * 
	 * // InputStream in = new FileInputStream("C:\\FFVII\\Teste\\MD1STIN.MIN8"); //
	 * OutputStream out = new FileOutputStream("C:\\FFVII\\Teste\\MD1STIN.DEC8"); //
	 * // in.skip(4); // // decode(in,out); // // in.close(); // out.close(); }
	 */

	public static int encodeBuffers(byte[] inBuffer, byte[] outBuffer) {

		int textsize = 0; /* contador para o tamanho do texto */
		int codesize = 0; /* contador do tamanho do c�digo */
		int printcount = 0; /* contador para verificar o progresso a cada 1Kbyte */

		int i, len, r, s, last_match_length, code_buf_ptr;
		int bi = 0, bo = 0;
		byte[] code_buf = new byte[17];
		byte mask;

		initalizeBuffers(); /* inicializar as �rvores */
		code_buf[0] = 0; /*
							 * code_buf[1..16] salva 8 unidades de c�digo, e code_buf[0] funciona como 8
							 * flags, "1" respresenta que a unidade � uma letra n�o codificada (1 byte), "0"
							 * representa um par(offset, tamanho) (2 bytes). Assim sendo, 8 unidades de
							 * c�digos ncessitam no m�ximo 16bytes de c�digo.
							 */
		code_buf_ptr = mask = 1;
		s = 0;
		r = N - F;
		for (i = s; i < r; i++)
			text_buf[i] = 0x0; /*
								 * Clear the buffer with any character that will appear often.
								 */
		for (len = 0; len < F && bi < inBuffer.length; len++)
			text_buf[r + len] = inBuffer[bi++]; /*
												 * Read F bytes into the last F bytes of the buffer
												 */
		if ((textsize = len) == 0)
			return 0; /* text of size zero */
		for (i = 1; i <= F; i++)
			insertNode(r - i); /*
								 * Insert the F strings, each of which begins with one or more 'space'
								 * characters. Note the order in which these strings are inserted. This way,
								 * degenerate trees will be less likely to occur.
								 */
		insertNode(r); /*
						 * Finally, insert the whole string just read. The global variables match_length
						 * and match_position are set.
						 */
		do {
			if (match_length > len)
				match_length = len; /*
									 * match_length pode muito grande a medida que se aproxima o final do texto.
									 */
			if (match_length <= THRESHOLD) {
				match_length = 1; /* Correspondencia n�o � grande o suficiente. Enviar 1 byte. */
				code_buf[0] |= mask; /* 'Enviar 1 byte' flag */
				code_buf[code_buf_ptr++] = text_buf[r]; /* Enviar sem codifica��o. */
			} else {
				code_buf[code_buf_ptr++] = (byte) match_position;
				code_buf[code_buf_ptr++] = (byte) (((match_position >> 4) & 0xf0)
						| (match_length - (THRESHOLD + 1))); /* Enviar o par (offset, tamanho). */
			}
			if ((mask <<= 1) == 0) { /* Deslocar a m�scara 1bit. */
				for (i = 0; i < code_buf_ptr; i++) /* Enviar no m�ximo 8 unidades de c�digo */
					outBuffer[bo++] = code_buf[i];
				codesize += code_buf_ptr;
				code_buf[0] = 0;
				code_buf_ptr = mask = 1;
			}
			last_match_length = match_length;

			for (i = 0; i < last_match_length && bi < inBuffer.length; i++) {
				byte b = inBuffer[bi++];
				deleteNode(s); /* Eliminar a string antiga e */
				text_buf[s] = b; /* ler os novos bytes */
				if (s < F - 1)
					text_buf[s + N] = b; /*
											 * Se a posi��o se encontra perto do final do buffer, extender o buffer para
											 * facilitar a compara��o da string.
											 */
				s = (s + 1) & (N - 1);
				r = (r + 1) & (N - 1);
				/* Sendo um buffer circular, incrementar a posi��o. */
				insertNode(r); /* Inserir a string presente em text_buf[r..r+F-1] na �rvore */
			}

			if ((textsize += i) > printcount) {
				// System.out.printf("%12d\r", textsize);
				printcount += 1024;
				/*
				 * Devolve o estado cada vez que o tamanho do texto exceda multiplos de 1024.
				 */
			}
			while (i++ < last_match_length) { /* Depois de todo o texto ser processado, */
				deleteNode(s); /* n�o h� necessidade de ler, mas */
				s = (s + 1) & (N - 1);
				r = (r + 1) & (N - 1);
				if (--len > 0)
					insertNode(r); /* o buffer pode n�o estar vazio. */
			}
		} while (len > 0); /* Enquanto o tamanho da string a processar ser 0 */

		if (code_buf_ptr > 1) { /* Enviar o restante c�digo. */
			for (i = 0; i < code_buf_ptr; i++)
				outBuffer[bo++] = code_buf[i];
			codesize += code_buf_ptr;
		}
		// System.out.printf("In : %d bytes\n", textsize); /* Conclu�da a codifica��o */
		// System.out.printf("Out: %d bytes\n", codesize);
		// System.out.printf("Out/In: %.3f\n", (double) codesize / textsize);

		destroyBuffers();

		return codesize;
	}

}
