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
package com.syncthemall.enml4j.impl;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.xml.stream.events.Characters;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import com.evernote.edam.type.Note;
import com.syncthemall.enml4j.ENMLProcessor;
import com.syncthemall.enml4j.converter.BaseConverter;
import com.syncthemall.enml4j.converter.Converter;
import com.syncthemall.enml4j.util.Elements;

/**
 * Default {@code Converter} implementation to convert {@code <en-note>} ENML tags.
 * <p>
 * This {@link Converter} will replace an {@code <en-note>} tag with an {@code <body></body>} HTML tag preceded by a full HTML
 * header. The header will contain {@code <meta>} and {@code <title>} tags filled with the {@code Note} attributes. The
 * valid HTML attributes of the {@code <en-note>} tag will be preserved in the {@code <body>} tag.
 * <p>
 * For example :
 * {@code <en-note xmlns="http://xml.evernote.com/pub/enml2.dtd" style="background: #e6e6e6;font-size: 14px;">} will be
 * replaced by :<br>
 * {@code <head>}<br>
 * {@code <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>}<br>
 * {@code <meta name="exporter-version" content="ENML4J 1.0"/>}<br>
 * {@code <meta name="altitude" content="34.284"/>}<br>
 * {@code <meta name="author" content="Anthony E. Stark"/>}<br>
 * {@code <meta name="created" content="Sat Mar 16 23:33:56 PDT 2013"/>}<br>
 * {@code <meta name="latitude" content="34.013183"/>}<br>
 * {@code <meta name="longitude" content="-118.816495"/>}<br>
 * {@code <meta name="updated" content="Sat Mar 16 23:50:09 PDT 2013"/>}<br>
 * {@code <title>Mark XLVII Schematic</title>}<br>
 * {@code </head>}<br>
 * {@code <body style="background: #e6e6e6;font-size: 14px;"> ... </body>}<br>
 * 
 * @see <a href="http://en.wikipedia.org/wiki/Data_URI_scheme">Data_URI_scheme</a>
 * @see <a href="http://dev.evernote.com/start/core/enml.php">Understanding the Evernote Markup Language</a>
 * @see <a href="http://docs.oracle.com/javaee/5/tutorial/doc/bnbdv.html">Streaming API for XML</a>
 */
public class DefaultNoteTagConverter extends BaseConverter {

	/**
	 * Replace an {@code <en-note>} tag by an {@code <body></body>} tag.
	 */
	public final Elements convertElement(final StartElement start, final Note note) {

		return new Elements(getEventFactory().createStartElement("", "", "body", start.getAttributes(), null),
				getEventFactory().createEndElement("", "", "body"));

	}

	/**
	 * Creates an HTML header tag to be inserted before the {@code <body></body>} tag.
	 */
	public final List<XMLEvent> insertBefore(final StartElement start, final Note note) {

		List<XMLEvent> result = new ArrayList<XMLEvent>();

		result.add(getEventFactory().createStartElement("", "", "head", null, null));
		result.add(getEventFactory().createStartElement(
				"",
				"",
				"meta",
				Arrays.asList(getEventFactory().createAttribute("http-equiv", "Content-Type"),
						getEventFactory().createAttribute("content", "text/html; charset=UTF-8")).iterator(), null));
		result.add(getEventFactory().createEndElement("", "", "meta"));

		result.add(getEventFactory().createStartElement(
				"",
				"",
				"meta",
				Arrays.asList(getEventFactory().createAttribute("name", "exporter-version"),
						getEventFactory().createAttribute("content", ENMLProcessor.VERSION)).iterator(), null));
		result.add(getEventFactory().createEndElement("", "", "meta"));

		result.add(getEventFactory().createStartElement(
				"",
				"",
				"meta",
				Arrays.asList(
						getEventFactory().createAttribute("name", "altitude"),
						getEventFactory().createAttribute("content",
								new DecimalFormat("0.000000").format(note.getAttributes().getAltitude()))).iterator(),
				null));
		result.add(getEventFactory().createEndElement("", "", "meta"));

		result.add(getEventFactory().createStartElement(
				"",
				"",
				"meta",
				Arrays.asList(
						getEventFactory().createAttribute("name", "author"),
						getEventFactory().createAttribute("content",
								note.getAttributes().getAuthor() != null ? note.getAttributes().getAuthor() : ""))
						.iterator(), null));
		result.add(getEventFactory().createEndElement("", "", "meta"));

		result.add(getEventFactory().createStartElement(
				"",
				"",
				"meta",
				Arrays.asList(getEventFactory().createAttribute("name", "created"),
						getEventFactory().createAttribute("content", new Date(note.getCreated()).toString()))
						.iterator(), null));
		result.add(getEventFactory().createEndElement("", "", "meta"));

		result.add(getEventFactory().createStartElement(
				"",
				"",
				"meta",
				Arrays.asList(
						getEventFactory().createAttribute("name", "latitude"),
						getEventFactory().createAttribute("content",
								new DecimalFormat("0.000000").format(note.getAttributes().getLatitude()))).iterator(),
				null));
		result.add(getEventFactory().createEndElement("", "", "meta"));

		result.add(getEventFactory().createStartElement(
				"",
				"",
				"meta",
				Arrays.asList(
						getEventFactory().createAttribute("name", "longitude"),
						getEventFactory().createAttribute("content",
								new DecimalFormat("0.000000").format(note.getAttributes().getLongitude()))).iterator(),
				null));
		result.add(getEventFactory().createEndElement("", "", "meta"));

		result.add(getEventFactory().createStartElement(
				"",
				"",
				"meta",
				Arrays.asList(getEventFactory().createAttribute("name", "updated"),
						getEventFactory().createAttribute("content", new Date(note.getUpdated()).toString()))
						.iterator(), null));
		result.add(getEventFactory().createEndElement("", "", "meta"));

		result.add(getEventFactory().createStartElement("", "", "title"));
		result.add(getEventFactory().createCharacters(note.getTitle() != null ? note.getTitle() : ""));
		result.add(getEventFactory().createEndElement("", "", "title"));

		result.add(getEventFactory().createEndElement("", "", "head"));

		return result;
	}

	/**
	 * This {@code Converter} does not insert any tag in the {@code <body></body>} tag created.
	 */
	public final List<XMLEvent> insertIn(final StartElement start, final Note note) {
		return null;
	}

	/**
	 * This {@code Converter} does not insert any tag after {@code <body></body>} tag created.
	 */
	public final List<XMLEvent> insertAfter(final StartElement start, final Note note) {
		return null;
	}

	/**
	 * This {@code Converter} does not replace any text in the {@code <body></body>} tag created.
	 */
	public final Characters convertCharacter(final Characters characters, final StartElement start, final Note note) {
		return characters;
	}

}
