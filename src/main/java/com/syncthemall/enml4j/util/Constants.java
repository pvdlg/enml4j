/**
 * The MIT License
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
/**
 * 
 */
package com.syncthemall.enml4j.util;

/**
 * Constant class.
 * 
 * @author Pierre-Denis Vanduynslager <pierre.denis.vanduynslager@gmail.com>
 */
public final class Constants {

	/** Version of ENML4j. Written in the header of the generated HTML. */
	public static final String VERSION = "ENML4J 1.0.0";

	/**
	 * The tag {@code <en-note>}. See <a href="http://dev.evernote.com/start/core/enml.php#added">Understanding the
	 * Evernote Markup Language</a>
	 */
	public static final String NOTE = "en-note";

	/**
	 * The tag {@code <en-media>}. See <a href="http://dev.evernote.com/start/core/enml.php#added">Understanding the
	 * Evernote Markup Language</a>
	 */
	public static final String MEDIA = "en-media";

	/**
	 * The tag {@code <en-todo>}. See <a href="http://dev.evernote.com/start/core/enml.php#added">Understanding the
	 * Evernote Markup Language</a>
	 */
	public static final String TODO = "en-todo";

	/**
	 * The tag {@code <en-crypt>}. See <a href="http://dev.evernote.com/start/core/enml.php#added">Understanding the
	 * Evernote Markup Language</a>
	 */
	public static final String CRYPT = "en-crypt";

	/**
	 * The attribute {@code hash}. See <a href="http://dev.evernote.com/start/core/enml.php#added">Understanding the
	 * Evernote Markup Language</a>
	 */
	public static final String HASH = "hash";

	/** The attribute {@code type}. */
	public static final String TYPE = "type";

	/** The attribute {@code name}. */
	public static final String NAME = "name";

	/** The attribute {@code content}. */
	public static final String CONTENT = "content";

	/** The attribute {@code meta}. */
	public static final String META = "meta";

	/** The attribute {@code title}. */
	public static final String TITLE = "title";

	/** The attribute {@code style}. */
	public static final String STYLE = "style";

	/** The attribute {@code image}. */
	public static final String IMAGE = "image";

	/** The attribute {@code img}. */
	public static final String IMG = "img";

	/** The attribute {@code src}. */
	public static final String SRC = "src";

	/** The attribute {@code span}. */
	public static final String SPAN = "span";

	/** The attribute {@code head}. */
	public static final String HEAD = "head";

	/** The attribute {@code a}. */
	public static final String A = "a";

	/** The attribute {@code href}. */
	public static final String HREF = "href";

	/** The attribute {@code alt}. */
	public static final String ALT = "alt";

	/** The attribute {@code input}. */
	public static final String INPUT = "input";
	
	/** The attribute {@code html}. */
	public static final String HTML = "html";
	
	/** The attribute {@code xmlns}. */
	public static final String XMLNS = "xmlns";

	/** The attribute {@code checked}. */
	public static final String CHECKED = "checked";

	/** The attribute {@code data:}. */
	public static final String DATA = "data:";

	/** The attribute {@code ;base64, }. */
	public static final String BASE64 = ";base64, ";

	/** The attribute {@code 0.000000}. */
	public static final String POSITION_ZERO = "0.000000";

	/** Default charset : UTF-8. */
	public static final String CHARSET = "UTF-8";

	/** Buffer size to convert image stream in base64. Defined to 16 KB. */
	public static final int BUFFER_SIZE = 16384;

	private Constants() {
		super();
	}

}
