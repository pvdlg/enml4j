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
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import com.evernote.edam.type.Note;
import com.syncthemall.enml4j.util.Elements;

/**
 * Interface for every Converter.
 * <p>
 * A Converter is used by ENML4j to convert a specific ENML tag to HTML. ENML4j provide default Converters to serves
 * generic purpose. If more specifics conversion are required a {@code Converter} class has to be implemented. <br>
 * A custom {@code Converter} has to extends :
 * <ul>
 * <li> {@link BaseConverter} for conversion of tags {@code <en-note>}, {@code <en-todo>} or {@code <en-crypt>}</li>
 * <li> {@link MediaConverter} for conversion of tags {@code <en-media>}</li>
 * </ul>
 * 
 * @see <a href="http://dev.evernote.com/start/core/enml.php">Understanding the Evernote Markup Language</a>
 * @see <a href="http://docs.oracle.com/javaee/5/tutorial/doc/bnbdv.html">Streaming API for XML</a>
 * 
 * @author Pierre-Denis Vanduynslager <pierre.denis.vanduynslager@gmail.com>
 */
public interface Converter {

	/**
	 * Convert an ENML tag.
	 * <p>
	 * This methods convert the currently processed ENML tag based on:
	 * <ul>
	 * <li>the {@code StartElement} corresponding to the ENML tag currently processed</li>
	 * <li>the {@code Note} currently processed</li>
	 * <li>the mapping of {@code Resource}s hash with their corresponding physical files {@code URL} if the tag is
	 * {@code <en-media>}</li>
	 * </ul>
	 * 
	 * The methods has to return an {@code Elements<StartElement, EndElement} with:
	 * <ul>
	 * <li>a non null {@code StartElement} corresponding to the opening converted ENML tag currently processed</li>
	 * <li>a non null {@code EndElement} corresponding to the closing converted ENML tag currently processed</li>
	 * </ul>
	 * Returning a {@code StartElement} with a non corresponding {@code EndElement} will result in malformed HTML.
	 * <p>
	 * For example a {@code Converter} designed to convert an {@code <en-media>} tag to an HTML {@code <img>} tag or an
	 * HTML 'a' (link) tag depending on the attribute of the {@code <en-media>} tag would implement this method. The
	 * implemented methods would have to determines first if the converted tag has to be {@code <img>} or 'a'. Then
	 * return an {@code Elements} with either an {@code <img>} {@code StartElement} and an {@code <img>}
	 * {@code EndElement} or 'a' {@code StartElement} and an 'a {@code EndElement}.
	 * 
	 * @param start the {@code StartElement} of the corresponding ENML tag
	 * @param note the {@code Note} currently converted
	 * @param mapHashURL the mapping of {@code Resource}s hash with their corresponding physical files path
	 * @return an {@code Elements<StartElement, EndElement>} the converted start and end tags
	 */
	Elements convertElement(StartElement start, Note note, Map<String, String> mapHashURL);

	/**
	 * Add a {@code List<XMLEvent>} before a converted tag.
	 * <p>
	 * This methods add {@code XMLEvent} tags before the converted tag of the currently processed ENML tag based on:
	 * <ul>
	 * <li>the {@code StartElement} corresponding to the ENML tag currently processed</li>
	 * <li>the {@code Note} currently processed</li>
	 * <li>the mapping of {@code Resource}s hash with their corresponding physical files {@code URL} if the tag is
	 * {@code <en-media>}</li>
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
	 * @param mapHashURL the mapping of {@code Resource}s hash with their corresponding physical files path
	 * @return a {@code List<XMLEvent>} to insert before a currently converted ENML tag
	 */
	List<XMLEvent> insertBefore(StartElement start, Note note, Map<String, String> mapHashURL);

	/**
	 * Add a {@code List<XMLEvent>} after a converted tag.
	 * <p>
	 * This methods add {@code XMLEvent} tags after the converted tag of the currently processed ENML tag based on:
	 * <ul>
	 * <li>the {@code StartElement} corresponding to the ENML tag currently processed</li>
	 * <li>the {@code Note} currently processed</li>
	 * <li>the mapping of {@code Resource}s hash with their corresponding physical files {@code URL} if the tag is
	 * {@code <en-media>}</li>
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
	 * @param mapHashURL the mapping of {@code Resource}s hash with their corresponding physical files path
	 * @return a {@code List<XMLEvent>} to insert before a currently converted ENML tag
	 */
	List<XMLEvent> insertAfter(StartElement start, Note note, Map<String, String> mapHashURL);

	/**
	 * Add a {@code List<XMLEvent>} in a converted tag.
	 * <p>
	 * This methods add {@code XMLEvent} tags in a the converted tag of the currently processed ENML tag based on:
	 * <ul>
	 * <li>the {@code StartElement} corresponding to the ENML tag currently processed</li>
	 * <li>the {@code Note} currently processed</li>
	 * <li>the mapping of {@code Resource}s hash with their corresponding physical files {@code URL} if the tag is
	 * {@code <en-media>}</li>
	 * </ul>
	 * 
	 * The methods has to return a {@code List<XMLEvent>} with all the tags to be added. The {@code List<XMLEvent>} will
	 * be processed in order. It has to contain {@code StartElement}s, {@code EndElement}s and {@code Characters}s in a
	 * valid order. That means a {@code Characters} has to be between a {@code StartElement} and an {@code EndElement}
	 * and for every {@code StartElement} an {@code EndElement} has to exist.<br>
	 * A non valid {@code List<XMLEvent>} will result in malformed HTML.
	 * <p>
	 * For example a {@code Converter} designed to convert an {@code <en-media>} tag to an HTML an HTML 'a' (link) tag
	 * with an {@code <img>} tag in it, creates an http link with an image would implements this method. The convertion
	 * of the {@code <en-media>} tag to an 'a' tag will be handled by
	 * {@link Converter#convertElement(StartElement, Note, Map)}. The creation of the included {@code <img>} tag will be
	 * handled by this method. The implemented methods would have to creates an {@code <img>} {@code StartElement}, fill
	 * it with {@link Attribute}, eventually creates an {@code Characters} to add some text and finally an {@code <img>}
	 * {@code EndElement}.
	 * 
	 * @param start the {@code StartElement} of the corresponding ENML tag
	 * @param note the {@code Note} currently converted
	 * @param mapHashURL the mapping of {@code Resource}s hash with their corresponding physical files path
	 * @return a {@code List<XMLEvent>} to insert before a currently converted ENML tag
	 */
	List<XMLEvent> insertIn(StartElement start, Note note, Map<String, String> mapHashURL);

	/**
	 * Transform text in a in a converted tag.
	 * <p>
	 * This methods creates text in the form of a {@code Characters} to replace the text included in the currently
	 * processed ENML tag:
	 * <ul>
	 * <li>the {@code Characters} text contained in the ENML tag currently processed</li>
	 * <li>the {@code StartElement} corresponding to the ENML tag currently processed</li>
	 * <li>the {@code Note} currently processed</li>
	 * <li>the mapping of {@code Resource}s hash with their corresponding physical files {@code URL} if the tag is
	 * {@code <en-media>}</li>
	 * </ul>
	 * 
	 * The methods has to return a {@code Characters} with the text to replace with. If the implemented methods return
	 * null, the text in the currently processed ENML tag will remain untouched.
	 * <p>
	 * 
	 * @param characters {@code Characters} containing the text included in the currently processed ENML tag
	 * @param start the {@code StartElement} of the corresponding ENML tag
	 * @param note the {@code Note} currently converted
	 * @param mapHashURL the mapping of {@code Resource}s hash with their corresponding physical files path
	 * @return a new {@code Characters} containing the replacement text
	 */
	Characters convertCharacter(Characters characters, StartElement start, Note note, Map<String, String> mapHashURL);

	/**
	 * @return the {@code XMLEventFactory} used to creates new {@code XMLEvent}.
	 */
	XMLEventFactory getEventFactory();

	/**
	 * @param eventFactory the {@code XMLEventFactory} used to creates new {@code XMLEvent}
	 * @return the invoked {@code Converter}
	 */
	Converter setEventFactory(XMLEventFactory eventFactory);

}
