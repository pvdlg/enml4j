package com.syncthemall.enml4j.impl;

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
 * This {@link Converter} will replace an {@code <en-crypt>} tag with an {@code <span></span>} HTML tag containing the text
 * <i>[Encrypted in Evernote]</i>.
 * <p>
 * For example : {@code <en-crypt hint="My Cat's Name">NKLHX5yK1MlpzemJQijAN6C4545s2EODxQ8Bg1r==</en-crypt>} <br>
 * will be replaced by : <br>
 * {@code <span>[Encrypted in Evernote]</span>}
 * 
 * @see <a href="http://en.wikipedia.org/wiki/Data_URI_scheme">Data_URI_scheme</a>
 * @see <a href="http://dev.evernote.com/start/core/enml.php">Understanding the Evernote Markup Language</a>
 * @see <a href="http://docs.oracle.com/javaee/5/tutorial/doc/bnbdv.html">Streaming API for XML</a>
 */
public class DefaultCryptTagConverter extends BaseConverter {

	public final Elements convertElement(final StartElement start, final Note note) {

		/**
		 * Replace an {@code <en-crypt>} tag by an {@code <span></span>} tag.
		 */
		return new Elements(getEventFactory().createStartElement("", "", "span"), getEventFactory().createEndElement(
				"", "", "span"));
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
