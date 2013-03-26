package com.syncthemall.enml4j.impl;

import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;


import com.evernote.edam.type.Note;
import com.syncthemall.enml4j.converter.BaseConverter;
import com.syncthemall.enml4j.converter.Converter;
import com.syncthemall.enml4j.util.Elements;

/**
 * Default {@code Converter} implementation to convert {@code <en-todo>} ENML tags.
 * <p>
 * This {@link Converter} will replace an {@code <en-todo>} tag with an {@code <input type="checkbox"></input>} HTML tag. <br>
 * The {@code <input type="checkbox">} will be checked if the {@code <en-todo>} tag is.
 * <p>
 * 
 * For example : {@code <en-todo checked="true"></en-todo>}<br>
 * will be replaced by :<br>
 * {@code <input type="checkbox" checked=""></input>}
 * 
 * @see <a href="http://en.wikipedia.org/wiki/Data_URI_scheme">Data_URI_scheme</a>
 * @see <a href="http://dev.evernote.com/start/core/enml.php">Understanding the Evernote Markup Language</a>
 * @see <a href="http://docs.oracle.com/javaee/5/tutorial/doc/bnbdv.html">Streaming API for XML</a>
 */
public class DefaultTodoTagConverter extends BaseConverter {

	/**
	 * Replace an {@code <en-todo>} tag by an {@code <input type="checkbox"></input>} tag.
	 */
	public final Elements convertElement(final StartElement start, final Note note) {

		List<Attribute> attrs = new ArrayList<Attribute>();
		attrs.add(getEventFactory().createAttribute("type", "checkbox"));
		if (start.getAttributeByName(new QName("checked")).getValue().equalsIgnoreCase("true")) {
			attrs.add(getEventFactory().createAttribute("checked", ""));
		}

		return new Elements(getEventFactory().createStartElement(start.getName().getPrefix(),
				start.getName().getNamespaceURI(), "input", attrs.iterator(), start.getNamespaces()), getEventFactory()
				.createEndElement("", "", "type"));
	}

	/**
	 * This {@code Converter} does not add any tag after the {@code <input></input>} tag created.
	 */
	public final List<XMLEvent> insertAfter(final StartElement start, final Note note) {
		return null;
	}

	/**
	 * This {@code Converter} does not add any tag before the {@code <input></input>} tag created.
	 */
	public final List<XMLEvent> insertBefore(final StartElement start, final Note note) {
		return null;
	}

	/**
	 * This {@code Converter} does not insert any tag in the {@code <input></input>} tag created.
	 */
	public final List<XMLEvent> insertIn(final StartElement start, final Note note) {
		return null;
	}

	/**
	 * This {@code Converter} does not replace text in the {@code <input></input>} tag created.
	 */
	public final Characters convertCharacter(final Characters characters, final StartElement start, final Note note) {
		return characters;
	}

}
