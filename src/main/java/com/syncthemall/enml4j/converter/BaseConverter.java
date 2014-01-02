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
package com.syncthemall.enml4j.converter;

import java.util.List;
import java.util.Map;

import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import com.evernote.edam.type.Note;
import com.syncthemall.enml4j.util.Elements;

/**
 * Base class to extends to creates custom converters for {@code <en-note>}, {@code <en-todo>} or {@code <en-crypt>}
 * ENML tags.
 * 
 * @author Pierre-Denis Vanduynslager <pierre.denis.vanduynslager@gmail.com>
 */
public abstract class BaseConverter implements Converter {

	private XMLEventFactory eventFactory;

	@Override
	public final Elements convertElement(final StartElement start, final Note note, final Map<String, String> mapHashURL) {
		return convertElement(start, note);
	}

	@Override
	public final List<XMLEvent> insertBefore(final StartElement start, final Note note,
			final Map<String, String> mapHashURL) {
		return insertBefore(start, note);
	}

	@Override
	public final List<XMLEvent> insertAfter(final StartElement start, final Note note,
			final Map<String, String> mapHashURL) {
		return insertAfter(start, note);
	}

	@Override
	public final List<XMLEvent> insertIn(final StartElement start, final Note note, final Map<String, String> mapHashURL) {
		return insertIn(start, note);
	}

	@Override
	public final Characters convertCharacter(final Characters characters, final StartElement start, final Note note,
			final Map<String, String> mapHashURL) {
		return convertCharacter(characters, start, note);
	}

	/**
	 * Convert an ENML tag.
	 * <p>
	 * This methods convert the currently processed ENML tag based on:
	 * <ul>
	 * <li>the {@code StartElement} corresponding to the ENML tag currently processed</li>
	 * <li>the {@code Note} currently processed</li>
	 * </ul>
	 * 
	 * The methods has to return an {@code Elements<StartElement, EndElement} with:
	 * <ul>
	 * <li>a non null {@code StartElement} corresponding to the opening converted ENML tag currently processed</li>
	 * <li>a non null {@code EndElement} corresponding to the closing converted ENML tag currently processed</li>
	 * </ul>
	 * Returning a {@code StartElement} with a non corresponding {@code EndElement} will result in malformed HTML.
	 * <p>
	 * For example a {@code Converter} designed to convert an {@code <en-todo>} tag to an HTML 'input' tag would
	 * implement this method. The implemented methods would have to return an {@code Elements} with an 'input'
	 * {@code StartElement} and an 'input {@code EndElement} .
	 * 
	 * @param start the {@code StartElement} of the corresponding ENML tag
	 * @param note the {@code Note} currently converted
	 * @return an {@code Elements<StartElement, EndElement>} the converted start and end tags
	 */
	public abstract Elements convertElement(StartElement start, Note note);

	/**
	 * Add a {@code List<XMLEvent>} before a converted tag.
	 * <p>
	 * This methods add {@code XMLEvent} tags before the converted tag of the currently processed ENML tag based on:
	 * <ul>
	 * <li>the {@code StartElement} corresponding to the ENML tag currently processed</li>
	 * <li>the {@code Note} currently processed</li>
	 * </ul>
	 * 
	 * The methods has to return a {@code List<XMLEvent>} with all the tags to be added. The {@code List<XMLEvent>} will
	 * be processed in order. It has to contain {@code StartElement}s, {@code EndElement}s and {@code Characters}s in a
	 * valid order. That means a {@code Characters} has to be between a {@code StartElement} and an {@code EndElement}
	 * and for every {@code StartElement} an {@code EndElement} has to exist.<br>
	 * A non valid {@code List<XMLEvent>} will result in malformed HTML.
	 * <p>
	 * For example a {@code Converter} designed to convert an {@code <en-note>} tag to a 'body' tag preceded by a head
	 * tag would implements this methods to add the 'head'.
	 * 
	 * @param start the {@code StartElement} of the corresponding ENML tag
	 * @param note the {@code Note} currently converted
	 * @return a {@code List<XMLEvent>} to insert before a currently converted ENML tag
	 */
	public abstract List<XMLEvent> insertBefore(StartElement start, Note note);

	/**
	 * Add a {@code List<XMLEvent>} after a converted tag.
	 * <p>
	 * This methods add {@code XMLEvent} tags after the converted tag of the currently processed ENML tag based on:
	 * <ul>
	 * <li>the {@code StartElement} corresponding to the ENML tag currently processed</li>
	 * <li>the {@code Note} currently processed</li>
	 * </ul>
	 * 
	 * The methods has to return a {@code List<XMLEvent>} with all the tags to be added. The {@code List<XMLEvent>} will
	 * be processed in order. It has to contain {@code StartElement}s, {@code EndElement}s and {@code Characters}s in a
	 * valid order. That means a {@code Characters} has to be between a {@code StartElement} and an {@code EndElement}
	 * and for every {@code StartElement} an {@code EndElement} has to exist.<br>
	 * A non valid {@code List<XMLEvent>} will result in malformed HTML.
	 * 
	 * @param start the {@code StartElement} of the corresponding ENML tag
	 * @param note the {@code Note} currently converted
	 * @return a {@code List<XMLEvent>} to insert before a currently converted ENML tag
	 */
	public abstract List<XMLEvent> insertAfter(StartElement start, Note note);

	/**
	 * Add a {@code List<XMLEvent>} in a converted tag.
	 * <p>
	 * This methods add {@code XMLEvent} tags in a the converted tag of the currently processed ENML tag based on:
	 * <ul>
	 * <li>the {@code StartElement} corresponding to the ENML tag currently processed</li>
	 * <li>the {@code Note} currently processed</li>
	 * </ul>
	 * 
	 * The methods has to return a {@code List<XMLEvent>} with all the tags to be added. The {@code List<XMLEvent>} will
	 * be processed in order. It has to contain {@code StartElement}s, {@code EndElement}s and {@code Characters}s in a
	 * valid order. That means a {@code Characters} has to be between a {@code StartElement} and an {@code EndElement}
	 * and for every {@code StartElement} an {@code EndElement} has to exist.<br>
	 * A non valid {@code List<XMLEvent>} will result in malformed HTML.
	 * <p>
	 * 
	 * @param start the {@code StartElement} of the corresponding ENML tag
	 * @param note the {@code Note} currently converted
	 * @return a {@code List<XMLEvent>} to insert before a currently converted ENML tag
	 */
	public abstract List<XMLEvent> insertIn(StartElement start, Note note);

	/**
	 * Transform text in a in a converted tag.
	 * <p>
	 * This methods creates text in the form of a {@code Characters} to replace the text included in the currently
	 * processed ENML tag:
	 * <ul>
	 * <li>the {@code Characters} text contained in the ENML tag currently processed</li>
	 * <li>the {@code StartElement} corresponding to the ENML tag currently processed</li>
	 * <li>the {@code Note} currently processed</li>
	 * </ul>
	 * 
	 * The methods has to return a {@code Characters} with the text to replace with. If the implemented methods return
	 * null, the text in the currently processed ENML tag will remain untouched.
	 * <p>
	 * For example a {@code Converter} designed to convert an {@code <en-crypt>} tag to an HTML an HTML 'span' tag with
	 * with the text contents "[Encrypted in Evernote]" would implements this methods. The methods
	 * {@code Converter#convertElement(StartElement, Note, Map)} would handle the conversion of the {@code <en-crypt>}
	 * tag to the 'span' tag. This methods would handle the replacement of the encrypted text by the generic String
	 * "[Encrypted in Evernote]".
	 * 
	 * @param characters {@code Characters} containing the text included in the currently processed ENML tag
	 * @param start the {@code StartElement} of the corresponding ENML tag
	 * @param note the {@code Note} currently converted
	 * @return a new {@code Characters} containing the replacement text
	 */
	public abstract Characters convertCharacter(Characters characters, StartElement start, Note note);

	@Override
	public final XMLEventFactory getEventFactory() {
		return eventFactory;
	}

	@Override
	public final BaseConverter setEventFactory(final XMLEventFactory eventFactory) {
		this.eventFactory = eventFactory;
		return this;
	}

}
