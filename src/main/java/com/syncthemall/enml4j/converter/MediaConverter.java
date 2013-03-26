package com.syncthemall.enml4j.converter;

import java.net.URL;
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
 */
public abstract class MediaConverter implements Converter {

	private XMLEventFactory eventFactory;

	//TODO ajouter log : x converti vers y
	public abstract Elements convertElement(StartElement start, Note note, Map<String, URL> mapHashURL);

	public abstract List<XMLEvent> insertBefore(StartElement start, Note note, Map<String, URL> mapHashURL);

	public abstract List<XMLEvent> insertAfter(StartElement start, Note note, Map<String, URL> mapHashURL);

	public abstract List<XMLEvent> insertIn(StartElement start, Note note, Map<String, URL> mapHashURL);

	public abstract Characters convertCharacter(Characters characters, StartElement start, Note note,
			Map<String, URL> mapHashURL);

	public final XMLEventFactory getEventFactory() {
		return eventFactory;
	}

	public final MediaConverter setEventFactory(final XMLEventFactory eventFactory) {
		this.eventFactory = eventFactory;
		return this;
	}

}
