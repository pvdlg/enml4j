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
package com.syncthemall.enml4j.impl;

import static com.syncthemall.enml4j.util.Constants.SPAN;

import java.util.List;

import javax.xml.stream.events.Characters;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import com.evernote.edam.type.Note;
import com.syncthemall.enml4j.converter.BaseConverter;
import com.syncthemall.enml4j.converter.Converter;
import com.syncthemall.enml4j.util.Elements;

/**
 * Default {@code Converter} implementation to convert {@code <en-crypt>} ENML tags.
 * <p>
 * This {@link Converter} will replace an {@code <en-crypt>} tag with an {@code <span></span>} HTML tag containing the
 * text <i>[Encrypted in Evernote]</i>.
 * <p>
 * For example : {@code <en-crypt hint="My Cat's Name">NKLHX5yK1MlpzemJQijAN6C4545s2EODxQ8Bg1r==</en-crypt>} <br>
 * will be replaced by : <br>
 * {@code <span>[Encrypted in Evernote]</span>}
 * 
 * @see <a href="http://en.wikipedia.org/wiki/Data_URI_scheme">Data_URI_scheme</a>
 * @see <a href="http://dev.evernote.com/start/core/enml.php">Understanding the Evernote Markup Language</a>
 * @see <a href="http://docs.oracle.com/javaee/5/tutorial/doc/bnbdv.html">Streaming API for XML</a>
 * 
 * @author Pierre-Denis Vanduynslager <pierre.denis.vanduynslager@gmail.com>
 */
public class DefaultCryptTagConverter extends BaseConverter {

	/**
	 * Replace an {@code <en-crypt>} tag by an {@code <span></span>} tag.
	 */
	public final Elements convertElement(final StartElement start, final Note note) {
		return new Elements(getEventFactory().createStartElement("", "", SPAN), getEventFactory().createEndElement(
				"", "", SPAN));
	}

	/**
	 * This {@code Converter} does not add any tag after the {@code <span></span>} tag created.
	 */
	public final List<XMLEvent> insertAfter(final StartElement start, final Note note) {
		return null;
	}

	/**
	 * This {@code Converter} does not add any tag before the {@code <span></span>} tag created.
	 */
	public final List<XMLEvent> insertBefore(final StartElement start, final Note note) {
		return null;
	}

	/**
	 * This {@code Converter} does not insert any tag in the {@code <span></span>} tag created.
	 */
	public final List<XMLEvent> insertIn(final StartElement start, final Note note) {
		return null;
	}

	/**
	 * Replace the encrypted text in {@code <en-crypt></en-crypt>} by <i>[Encrypted in Evernote]</i>.
	 */
	public final Characters convertCharacter(final Characters characters, final StartElement start, final Note note) {
		return getEventFactory().createCharacters("[Encrypted in Evernote]");
	}

}
