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
 * Base class to extends to creates custom converters for {@code <en-media>} ENML tags.
 * 
 * @author Pierre-Denis Vanduynslager <pierre.denis.vanduynslager@gmail.com>
 */
public abstract class MediaConverter implements Converter {

	private XMLEventFactory eventFactory;

	@Override
	public abstract Elements convertElement(StartElement start, Note note, Map<String, String> mapHashURL);

	@Override
	public abstract List<XMLEvent> insertBefore(StartElement start, Note note, Map<String, String> mapHashURL);

	@Override
	public abstract List<XMLEvent> insertAfter(StartElement start, Note note, Map<String, String> mapHashURL);

	@Override
	public abstract List<XMLEvent> insertIn(StartElement start, Note note, Map<String, String> mapHashURL);

	@Override
	public abstract Characters convertCharacter(Characters characters, StartElement start, Note note,
			Map<String, String> mapHashURL);

	@Override
	public final XMLEventFactory getEventFactory() {
		return eventFactory;
	}

	@Override
	public final MediaConverter setEventFactory(final XMLEventFactory eventFactory) {
		this.eventFactory = eventFactory;
		return this;
	}

}
