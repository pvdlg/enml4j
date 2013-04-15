/**
 * The MIT License
 *
 * Copyright (c) 2013 Pierre-Denis Vanduynslager
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.syncthemall.enml4j.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import org.apache.commons.codec.binary.Base64;

/**
 * An utility class to encode/decode file in base64.
 */
public final class Utils {

	/** The base64 encoded {@code String} of an unknown icon (an icon with a question mark). **/
	private static final String UNKNOWN_ICON = "iVBORw0KGgoAAAANSUhEUgAAADAAAAAwCAYAAABXAvmHAAAEoUlEQVR42t2aSWscRxiG5QUSZTmY7IuTH5BTrtYl4NxiYoJxTOI4wTGOiXNTDvoJ+QE+5O6/okskBEIj0Ir2fd93iU49TX/DNx9V1d0ajT32wIuk6erq56n6uro0TEuSJC2vclpeKwH3uuLytss1l/eaLNcytiteAfd6o62t7ctKpfLv9vb20unpadJMgQk2GGGtEchG/uPOzs7nJycnyfHxcXJ0dNRUgQk2GGGVmRCBd1y+Xl5eXi3SUSOTd30YYYVZC1Bf34Y6aDR0GRnehxVmLfC+y3c0ODw8rMnLngEfD6wwa4EPXG7ZE17mLIQkMoFbMEcFYvDcTI1IEYmYwPc0ODg48M5CmdErAld0BjSLsMFat0DeaMr6Xe/olxG4bQV88Hkg6+vryezsbDI1NZWMjY2l4Xfe41gZESuhBG5bgQ9dfqDB/v5+2lAa55WQXHhzczOZmJhIQRcWFpK1tbX05/z8fDUzMzNpG/dUzS0tDS88wgYrzF4BnaICwDPKAkrpnJ2dpeGiWoLQlvfLCkhCAnfKCsgoMupzc3PV8Lcui8XFxZrjktgs5Ajc8QowKhIt4XugiRRtKQ2gJYywrndmSB8n09PT1T58feu6l/KR+ATYHN09jwA/AUJCB2gZTWreHid7e3vnFbgLsxX4kQ4lWiRvawEsIyoBTpfd0tJSzXFJDFzghUOzweoTuFdEICRBu5WVlVRGw/P75ORkWlY63BexLUOOwL26BELbDfu/hMzO+Ph4KqFD3yHw8wr8tLu7m0isREwkJsj9wdqvs7Ozk8rFwC28ZoO1kIBI2KW1aAAdHR1NZ0BmgT6KwIuAcOQJfOJy/yIFGH1uZtlOsO7nlUwJgfswW4FfGDGJlbAisQDC0jk8PJzOgMD7IH2xdQ+LZoO14QLuf9dkZGSkuqQWAa9H4FOXB1bA3sxFJYBl1JkBnsjngbc3rxF4ALMV+JVpl1gJKxKKgLBpGxoaSvvywcVi6x4WzQarFfjM5betra26BbSELp2i5+YJwAgrzFbgYZ5AmVkIPZCKwucIPPQK8NSkgYj47odYBIItxcDAQNLT05OuQvTnAwzF1r2AExgbJkA4r7e3N+nv769mcHCwMHw9Ar9vbGykDUREl5MW8UUuzrJZqVRq0tfXl65G9oEUigYXeOGCEVYr8LnLo4sQYOsMsA39XKDAI5itwGMrYEupqASjThlJmBVKqAy8r3SUwGOfwB9MMw1CM2FlfBERPpHgOUBfAp93rl3vLTiBEVYrcF0ErISeiVB8ErLf920FfLA2euQ1vBK4bgWe+ASKSuTNUtFzQ2VjBJ5YgS9c/uTDKCINYzKNSgiaCB+sMAcF8iQanRh8TOCpbhSS0LloYJ0QfCbw1CfwFwdXV1drEpNqROz1fDywekuoq6try57gk3hR8bHA6Cuhj1x+7ujo+C904osUiV0fRlhh1gLvutxobW39p729vb+7u3sndg/YGg4ts6El1K44vnvBlhVMsMEIK8xa4FL20cpNl79dnrk8b7I8y9huZqyX7FcNLmffSfgqM/ymyXIjY4PxcvDbKu511eVNl7eaLDBdtbz/A5FFGbor5DzMAAAAAElFTkSuQmCC";

	/**
	 * Encode an {@code byte[]} representing a file in base64.
	 * 
	 * @param bytes the source to encode
	 * @return a {@code String} representation of the {@code InputStream} in parameter, encoded in base64
	 */
	public static String encodeFileToBase64Binary(final byte[] bytes) {

		byte[] encoded = Base64.encodeBase64(bytes);
		String encodedString = new String(encoded);

		return encodedString;
	}

	/**
	 * Encode an {@code InputStream} representing a file in base64.
	 * 
	 * @param is the source to encode
	 * @return a {@code String} representation of the {@code InputStream} in parameter, encoded in base64
	 * @throws IOException if an I/O error occurs reading the {@code InputStream} in parameter
	 */
	public static String encodeToBase64Binary(final InputStream is) throws IOException {
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		int nRead;
		byte[] data = new byte[16384]; // 16K

		while ((nRead = is.read(data, 0, data.length)) != -1) {
			buffer.write(data, 0, nRead);
		}
		buffer.flush();

		return Base64.encodeBase64String(buffer.toByteArray());
		
	}

	/**
	 * Return a base64 encoded icon for a given mime type. If the mime type is unknown or the icon is unavailable,
	 * returns the base64 encoded {@code String} of an unknown icon (an icon with a question mark).
	 * 
	 * @param mimeType the mime type (ie application/rtf, image/bmp, ...)
	 * @return a base64 encoded {@code String} of the icon corresponding to the mime type in parameter
	 */
	public static String getEncodedIcon(final String mimeType) {
		InputStream icon = Utils.class.getResourceAsStream("/icons/" + mimeType.replaceFirst("/", "-") + ".png");
		try {
			return encodeToBase64Binary(icon);
		} catch (IOException e) {
			return UNKNOWN_ICON;
		}
	}

	/**
	 * Returns an hexadecimal representation of a {@code byte[]}.
	 * 
	 * @param bytes the source to encode
	 * @return a hexadecimal {@code String} representation of the data in parameters
	 */
	public static String bytesToHex(final byte[] bytes) {
		StringBuilder sb = new StringBuilder();
		for (byte hashByte : bytes) {
			int intVal = 0xff & hashByte;
			if (intVal < 0x10) {
				sb.append('0');
			}
			sb.append(Integer.toHexString(intVal));
		}
		return sb.toString();
	}

	/**
	 * Convert a millisecond duration to a string format.
	 * 
	 * @param millis A duration to convert to a string form
	 * @return A string of the form "X Days Y Hours Z Minutes A Seconds".
	 */
	public static String getDurationBreakdown(long millis) {
		if (millis < 0) {
			throw new IllegalArgumentException("Duration must be greater than zero!");
		}

		long minutes = TimeUnit.MILLISECONDS.toMinutes(millis);
		millis -= TimeUnit.MINUTES.toMillis(minutes);

		long seconds = TimeUnit.MILLISECONDS.toSeconds(millis);
		millis -= TimeUnit.SECONDS.toMillis(seconds);

		StringBuilder sb = new StringBuilder();
		sb.append(minutes);
		sb.append(" Minutes ");
		sb.append(seconds);
		sb.append(" Seconds ");
		sb.append(millis);
		sb.append(" Milliseconds");

		return (sb.toString());
	}

}
