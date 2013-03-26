package com.syncthemall.enml4j;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.logging.Logger;

import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;


import com.evernote.edam.type.Note;
import com.evernote.edam.type.Resource;
import com.syncthemall.enml4j.converter.BaseConverter;
import com.syncthemall.enml4j.converter.Converter;
import com.syncthemall.enml4j.converter.MediaConverter;
import com.syncthemall.enml4j.impl.DefaultCryptTagConverter;
import com.syncthemall.enml4j.impl.DefaultInlineMediaTagConverter;
import com.syncthemall.enml4j.impl.DefaultMediaTagConverter;
import com.syncthemall.enml4j.impl.DefaultNoteTagConverter;
import com.syncthemall.enml4j.impl.DefaultTodoTagConverter;
import com.syncthemall.enml4j.util.Elements;
import com.syncthemall.enml4j.util.Utils;

/**
 * The entry point of ENML4j.
 * 
 * This class should be instantiated and kept in reference (as a static for example) for better performances. When
 * converting a {@code Note} to HTML the Evernote DTD has to be parsed the first time, then stays in memory. Parsing the
 * DTD the first time is time-consuming.
 * <p>
 * This class rely on stAX to convert ENML to HTML. ENML4j will uses the default stAX implementation on the platform.
 * But implementation can be easily chosen : <a
 * href="http://docs.oracle.com/javaee/5/tutorial/doc/bnbem.html#bnbeo">StAX Factory Classes</a>
 * <p>
 * This class is thread-safe as long as the stAX implementation of {@link XMLInputFactory}, {@link XMLOutputFactory},
 * {@link XMLEventFactory} are thread-safe. Almost all implementation of this classes are thread-safe.
 * <p>
 * ENML4j rely on {@link Converter}s classes to convert specifics ENML tags to an HTML equivalent. Default
 * {@code Converter} are provided and instantiated by default.
 * <ul>
 * <li>{@link DefaultNoteTagConverter}</li>
 * <li> {@link DefaultInlineMediaTagConverter}</li>
 * <li> {@link DefaultTodoTagConverter}</li>
 * <li> {@link DefaultCryptTagConverter}</li>
 * <li> {@link DefaultInlineMediaTagConverter}</li>
 * </ul>
 * <p>
 * For specifics needs {@link BaseConverter} and {@link MediaConverter} can be implemented and set with
 * {@link ENMLProcessor#setConverters(BaseConverter, MediaConverter, BaseConverter, BaseConverter)} and
 * {@link ENMLProcessor#setInlineConverters(BaseConverter, MediaConverter, BaseConverter, BaseConverter)}.
 * 
 * @see <a href="http://dev.evernote.com/start/core/enml.php">Understanding the Evernote Markup Language</a>
 * @see <a href="http://docs.oracle.com/javaee/5/tutorial/doc/bnbdv.html">Streaming API for XML</a>
 */
public class ENMLProcessor {

	private Logger log = Logger.getLogger("com.syncthemall.ENMLProcessor");

	/**
	 * The Attribute {@code <en-note>}. See <a href="http://dev.evernote.com/start/core/enml.php#added">Understanding
	 * the Evernote Markup Language</a>
	 */
	public static final String NOTE = "en-note";
	/**
	 * The Attribute {@code <en-media>}. See <a href="http://dev.evernote.com/start/core/enml.php#added">Understanding
	 * the Evernote Markup Language</a>
	 */
	public static final String MEDIA = "en-media";
	/**
	 * The Attribute {@code <en-todo>}. See <a href="http://dev.evernote.com/start/core/enml.php#added">Understanding
	 * the Evernote Markup Language</a>
	 */
	public static final String TODO = "en-todo";
	/**
	 * The Attribute {@code <en-crypt>}. See <a href="http://dev.evernote.com/start/core/enml.php#added">Understanding
	 * the Evernote Markup Language</a>
	 */
	public static final String CRYPT = "en-crypt";

	/** Version of ENML4j. Written in the header of the generated HTML. */
	public static final String VERSION = "ENML4J 0.1.0";

	private static final Map<String, Converter> CONVERTERS = new HashMap<String, Converter>();
	private static final Map<String, Converter> INLINE_CONVERTERS = new HashMap<String, Converter>();

	/** An instance of {@code XMLEventFactory} used to creates new {@link XMLEvent}s. */
	private static final XMLEventFactory eventFactory = XMLEventFactory.newInstance();
	private static final XMLInputFactory inputFactory = XMLInputFactory.newInstance();
	private static final XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();

	/**
	 * Construct an {@code ENMLProcessor} with default {@code Converter}s.
	 * <p>
	 * For in-line HTML conversion the default {@code Converter} are :
	 * <ul>
	 * <li> {@link DefaultNoteTagConverter}</li>
	 * <li> {@link DefaultMediaTagConverter}</li>
	 * <li> {@link DefaultTodoTagConverter}</li>
	 * <li> {@link DefaultCryptTagConverter}</li>
	 * </ul>
	 * <p>
	 * For HTML conversion with resource reference, the default {@code Converter} are :
	 * <ul>
	 * <li> {@link DefaultNoteTagConverter}</li>
	 * <li> {@link DefaultInlineMediaTagConverter}</li>
	 * <li> {@link DefaultTodoTagConverter}</li>
	 * <li> {@link DefaultCryptTagConverter}</li>
	 * </ul>
	 */
	public ENMLProcessor() {
		CONVERTERS.put(NOTE, new DefaultNoteTagConverter().setEventFactory(eventFactory));
		CONVERTERS.put(MEDIA, new DefaultMediaTagConverter().setEventFactory(eventFactory));
		CONVERTERS.put(TODO, new DefaultTodoTagConverter().setEventFactory(eventFactory));
		CONVERTERS.put(CRYPT, new DefaultCryptTagConverter().setEventFactory(eventFactory));

		INLINE_CONVERTERS.put(NOTE, new DefaultNoteTagConverter().setEventFactory(eventFactory));
		INLINE_CONVERTERS.put(MEDIA, new DefaultInlineMediaTagConverter().setEventFactory(eventFactory));
		INLINE_CONVERTERS.put(TODO, new DefaultTodoTagConverter().setEventFactory(eventFactory));
		INLINE_CONVERTERS.put(CRYPT, new DefaultCryptTagConverter().setEventFactory(eventFactory));
	}

	/**
	 * Set the {@code Converter}s for HTML conversion with resource reference. If one the parameter is null the default
	 * {@code Converter} will be used.
	 * <p>
	 * For HTML conversion with resource reference, the default {@code Converter} are :
	 * <ul>
	 * <li> {@link DefaultNoteTagConverter}</li>
	 * <li> {@link DefaultMediaTagConverter}</li>
	 * <li> {@link DefaultTodoTagConverter}</li>
	 * <li> {@link DefaultCryptTagConverter}</li>
	 * </ul>
	 * 
	 * @param noteConverter the {@code Converter} used to convert the ENML tag {@code <en-note>}
	 * @param mediaConverter the {@code Converter} used to convert the ENML tag {@code <en-media>}
	 * @param todoConverter the {@code Converter} used to convert the ENML tag {@code <en-todo>}
	 * @param cryptConverter the {@code Converter} used to convert the ENML tag {@code <en-crypt>}
	 */
	public final void setConverters(final BaseConverter noteConverter, final MediaConverter mediaConverter,
			final BaseConverter todoConverter, final BaseConverter cryptConverter) {
		if (noteConverter != null) {
			CONVERTERS.put(NOTE, noteConverter.setEventFactory(eventFactory));
		} else {
			CONVERTERS.put(NOTE, new DefaultNoteTagConverter().setEventFactory(eventFactory));
		}
		if (mediaConverter != null) {
			CONVERTERS.put(MEDIA, mediaConverter.setEventFactory(eventFactory));
		} else {
			CONVERTERS.put(MEDIA, new DefaultMediaTagConverter().setEventFactory(eventFactory));
		}
		if (todoConverter != null) {
			CONVERTERS.put(TODO, todoConverter.setEventFactory(eventFactory));
		} else {
			CONVERTERS.put(TODO, new DefaultTodoTagConverter().setEventFactory(eventFactory));
		}
		if (cryptConverter != null) {
			CONVERTERS.put(CRYPT, cryptConverter.setEventFactory(eventFactory));
		} else {
			CONVERTERS.put(CRYPT, new DefaultCryptTagConverter().setEventFactory(eventFactory));
		}
	}

	/**
	 * Set the {@code Converter}s for in-line HTML conversion. If one the parameter is null the default
	 * {@code Converter} will be used.
	 * <p>
	 * For in-line HTML conversion the default {@code Converter} are :
	 * <ul>
	 * <li> {@link DefaultNoteTagConverter}</li>
	 * <li> {@link DefaultMediaTagConverter}</li>
	 * <li> {@link DefaultTodoTagConverter}</li>
	 * <li> {@link DefaultCryptTagConverter}</li>
	 * </ul>
	 * 
	 * @param noteConverter the {@code Converter} used to convert the ENML tag {@code <en-note>}
	 * @param mediaConverter the {@code Converter} used to convert the ENML tag {@code <en-media>}
	 * @param todoConverter the {@code Converter} used to convert the ENML tag {@code <en-todo>}
	 * @param cryptConverter the {@code Converter} used to convert the ENML tag {@code <en-crypt>}
	 */
	public final void setInlineConverters(final BaseConverter noteConverter, final MediaConverter mediaConverter,
			final BaseConverter todoConverter, final BaseConverter cryptConverter) {

		if (noteConverter != null) {
			INLINE_CONVERTERS.put(NOTE, noteConverter.setEventFactory(eventFactory));
		} else {
			INLINE_CONVERTERS.put(NOTE, new DefaultNoteTagConverter().setEventFactory(eventFactory));
		}
		if (mediaConverter != null) {
			INLINE_CONVERTERS.put(MEDIA, mediaConverter.setEventFactory(eventFactory));
		} else {
			INLINE_CONVERTERS.put(MEDIA, new DefaultInlineMediaTagConverter().setEventFactory(eventFactory));
		}
		if (todoConverter != null) {
			INLINE_CONVERTERS.put(TODO, todoConverter);
		} else {
			INLINE_CONVERTERS.put(TODO, new DefaultTodoTagConverter().setEventFactory(eventFactory));
		}
		if (cryptConverter != null) {
			INLINE_CONVERTERS.put(CRYPT, cryptConverter.setEventFactory(eventFactory));
		} else {
			INLINE_CONVERTERS.put(CRYPT, new DefaultCryptTagConverter().setEventFactory(eventFactory));
		}
	}

	/**
	 * Creates an HTML version of the ENML content of a {@code Note}.
	 * <p>
	 * The HTML is generated based on the {@link Converter}s defined by
	 * {@code ENMLProcessor#setConverters(Converter, Converter, Converter, Converter)}. <br>
	 * The methods assumes the {@code Note} contains a valid ENML content and that it's {@code Resource}s objects
	 * contains their date. <br>
	 * The {@code Resource}s of the {@code Note} will be generated directly in the generated HTML using Data URI scheme.
	 * See <a href="http://en.wikipedia.org/wiki/Data_URI_scheme">Data_URI_scheme</a> <br>
	 * The generated HTML page will be viewable in a browser without requiring an access to the physical file associated
	 * to the {@code Resource}
	 * 
	 * @param note the Note to creates the HTML from. It has to contain its list of {@code Resource}s with data and an
	 *            ENML content
	 * @return a {@code String} containing the resulting HTML file
	 * @throws XMLStreamException if there is an unexpected processing error, like a malformed ENML content in the Note
	 */
	public final String noteToInlineHTMLString(final Note note) throws XMLStreamException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		noteToInlineHTML(note, baos);
		return new String(baos.toByteArray(), Charset.forName("UTF-8"));
	}

	/**
	 * Creates an HTML version of the ENML content of a {@code Note}.
	 * <p>
	 * The HTML is generated based on the {@link Converter}s defined by
	 * {@code ENMLProcessor#setConverters(Converter, Converter, Converter, Converter)}. <br>
	 * The methods assumes the {@code Note} contains a valid ENML content and that it's {@code Resource}s objects
	 * contains their date. <br>
	 * The {@code Resource}s of the {@code Note} will be generated directly in the generated HTML using Data URI scheme.
	 * See <a href="http://en.wikipedia.org/wiki/Data_URI_scheme">Data_URI_scheme</a> <br>
	 * The generated HTML page will be viewable in a browser without requiring an access to the physical file associated
	 * to the {@code Resource}
	 * 
	 * @param note the Note to creates the HTML from. It has to contain its list of {@code Resource}s with data and an
	 *            ENML content
	 * @return an {@code InputStream} containing the resulting HTML file
	 * @throws XMLStreamException if there is an unexpected processing error, like a malformed ENML content in the Note
	 * @throws IOException if an I/O error occurs during the {@code InputStream} creation
	 */
	public final InputStream noteToInlineHTMLInputStream(final Note note) throws XMLStreamException, IOException {
		PipedInputStream pis = new PipedInputStream();
		PipedOutputStream pos = new PipedOutputStream(pis);
		noteToInlineHTML(note, pos);
		return pis;
	}

	/**
	 * Creates an HTML version of the ENML content of a {@code Note}.
	 * <p>
	 * The HTML is generated based on the {@link Converter}s defined by
	 * {@code ENMLProcessor#setConverters(Converter, Converter, Converter, Converter)}. <br>
	 * The methods assumes the {@code Note} contains a valid ENML content and that it's {@code Resource}s objects
	 * contains their date. <br>
	 * The {@code Resource}s of the {@code Note} will be generated directly in the generated HTML using Data URI scheme.
	 * See <a href="http://en.wikipedia.org/wiki/Data_URI_scheme">Data_URI_scheme</a> <br>
	 * The generated HTML page will be viewable in a browser without requiring an access to the physical file associated
	 * to the {@code Resource}
	 * 
	 * @param note the Note to creates the HTML from. It has to contain its list of {@code Resource}s with data and an
	 *            ENML content
	 * @param out an {@code OutputStream} in which to write the resulting HTML file
	 * @throws XMLStreamException if there is an unexpected processing error, like a malformed ENML content in the Note
	 */
	public final void noteToInlineHTML(final Note note, final OutputStream out) throws XMLStreamException {
		noteToHTML(note, null, out, true);
	}

	/**
	 * Creates an HTML version of the ENML content of a {@code Note}.
	 * <p>
	 * The HTML is generated based on the {@link Converter}s defined by
	 * {@code ENMLProcessor#setConverters(Converter, Converter, Converter, Converter)}. <br>
	 * The methods assumes the {@code Note} contains a valid ENML content and that it's {@code Resource}s objects
	 * contains their date. <br>
	 * The {@code Resource}s of the {@code Note} will be referenced in the generated HTML according to the {@code Map}
	 * in parameter. This {@code Map} has to contain for every {@code Resource} in the {@code Note} an entry with :
	 * <ul>
	 * <li>the GUID of the {@code Resource}</li>
	 * <li>The {@code URL} of the actual resource to reference in the generated HTML. This {@code URL} will typically be
	 * used as the value of the 'href' attribute for file resources and 'src' attribute for image resources.</li>
	 * </ul>
	 * <br>
	 * In order to view the HTML page generated in a browser all the resources (files, images, ...) has to be accessible
	 * by the browser at the {@code URL} given in the {@code Map}. The resources (the actual files and images) doesn't
	 * need to be accessible by this methods though.
	 * 
	 * @param note the Note to creates the HTML from. It has to contain its list of {@code Resource}s with data and an
	 *            ENML content
	 * @param mapGUIDURL the mapping of {@code Resource}s GUID with their corresponding physical files {@code URL}
	 * @return a {@code String} containing the resulting HTML file
	 * @throws XMLStreamException if there is an unexpected processing error, like a malformed ENML content in the Note
	 */
	public final String noteToHTMLString(final Note note, final Map<String, URL> mapGUIDURL) throws XMLStreamException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		Map<String, URL> hashURLMap = new HashMap<String, URL>();
		if (mapGUIDURL != null) {
			for (String guid : mapGUIDURL.keySet()) {
				for (Resource resource : note.getResources()) {
					if (resource.getGuid().equals(guid)) {
						hashURLMap.put(Utils.bytesToHex(resource.getData().getBodyHash()), mapGUIDURL.get(guid));
					}
				}
			}
		}
		noteToHTML(note, hashURLMap, baos, false);
		return new String(baos.toByteArray(), Charset.forName("UTF-8"));
	}

	/**
	 * Creates an HTML version of the ENML content of a {@code Note}.
	 * <p>
	 * The HTML is generated based on the {@link Converter}s defined by
	 * {@code ENMLProcessor#setConverters(Converter, Converter, Converter, Converter)}. <br>
	 * The methods assumes the {@code Note} contains a valid ENML content and that it's {@code Resource}s objects
	 * contains their date. <br>
	 * The {@code Resource}s of the {@code Note} will be referenced in the generated HTML according to the {@code Map}
	 * in parameter. This {@code Map} has to contain for every {@code Resource} in the {@code Note} an entry with :
	 * <ul>
	 * <li>the GUID of the {@code Resource}</li>
	 * <li>The {@code URL} of the actual resource to reference in the generated HTML. This {@code URL} will typically be
	 * used as the value of the 'href' attribute for file resources and 'src' attribute for image resources.</li>
	 * </ul>
	 * <br>
	 * In order to view the HTML page generated in a browser all the resources (files, images, ...) has to be accessible
	 * by the browser at the {@code URL} given in the {@code Map}. The resources (the actual files and images) doesn't
	 * need to be accessible by this methods though.
	 * 
	 * @param note the Note to creates the HTML from. It has to contain its list of {@code Resource}s with data and an
	 *            ENML content
	 * @param mapGUIDURL the mapping of {@code Resource}s GUID with their corresponding physical files {@code URL}
	 * @return an {@code InputStream} containing the resulting HTML file
	 * @throws XMLStreamException if there is an unexpected processing error, like a malformed ENML content in the Note
	 * @throws IOException if an I/O error occurs during the {@code InputStream} creation
	 */
	public final InputStream noteToHTMLInputStream(final Note note, final Map<String, URL> mapGUIDURL)
			throws XMLStreamException, IOException {

		Map<String, URL> hashURLMap = new HashMap<String, URL>();

		if (mapGUIDURL != null) {
			for (String guid : mapGUIDURL.keySet()) {
				for (Resource resource : note.getResources()) {
					if (resource.getGuid().equals(guid)) {
						hashURLMap.put(Utils.bytesToHex(resource.getData().getBodyHash()), mapGUIDURL.get(guid));
					}
				}
			}
		}
		PipedInputStream pis = new PipedInputStream();
		PipedOutputStream pos = new PipedOutputStream(pis);
		noteToHTML(note, hashURLMap, pos, false);
		return pis;
	}

	/**
	 * Creates an HTML version of the ENML content of a {@code Note}.
	 * <p>
	 * The HTML is generated based on the {@link Converter}s defined by
	 * {@code ENMLProcessor#setConverters(Converter, Converter, Converter, Converter)}. <br>
	 * The methods assumes the {@code Note} contains a valid ENML content and that it's {@code Resource}s objects
	 * contains their date. <br>
	 * The {@code Resource}s of the {@code Note} will be referenced in the generated HTML according to the {@code Map}
	 * in parameter. This {@code Map} has to contain for every {@code Resource} in the {@code Note} an entry with :
	 * <ul>
	 * <li>the GUID of the {@code Resource}</li>
	 * <li>The {@code URL} of the actual resource to reference in the generated HTML. This {@code URL} will typically be
	 * used as the value of the 'href' attribute for file resources and 'src' attribute for image resources.</li>
	 * </ul>
	 * <br>
	 * In order to view the HTML page generated in a browser all the resources (files, images, ...) has to be accessible
	 * by the browser at the {@code URL} given in the {@code Map}. The resources (the actual files and images) doesn't
	 * need to be accessible by this methods though.
	 * 
	 * @param note the Note to creates the HTML from. It has to contain its list of {@code Resource}s with data and an
	 *            ENML content
	 * @param mapGUIDURL the mapping of {@code Resource}s GUID with their corresponding physical files {@code URL}
	 * @param out an {@code OutputStream} in which to write the resulting HTML file
	 * @throws XMLStreamException if there is an unexpected processing error, like a malformed ENML content in the Note
	 */
	public final void noteToHTML(final Note note, final Map<String, URL> mapGUIDURL, final OutputStream out)
			throws XMLStreamException {

		Map<String, URL> hashURLMap = new HashMap<String, URL>();

		if (mapGUIDURL != null) {
			for (String guid : mapGUIDURL.keySet()) {
				for (Resource resource : note.getResources()) {
					if (resource.getGuid().equals(guid)) {
						hashURLMap.put(Utils.bytesToHex(resource.getData().getBodyHash()), mapGUIDURL.get(guid));
					}
				}
			}
		}
		noteToHTML(note, hashURLMap, out, false);
	}

	private void noteToHTML(final Note note, final Map<String, URL> mapHashURL, final OutputStream out,
			final boolean inline) throws XMLStreamException {

		long start = System.currentTimeMillis();
		log.finer("Converting Note " + note.getGuid() + " to HTML");

		Map<String, Converter> currentConverter;
		if (inline) {
			currentConverter = INLINE_CONVERTERS;
		} else {
			currentConverter = CONVERTERS;
		}

		ArrayDeque<EndElement> stack = new ArrayDeque<EndElement>();
		Map<EndElement, List<XMLEvent>> toInsertAfter = new HashMap<EndElement, List<XMLEvent>>();

		XMLEventReader reader = inputFactory.createXMLEventReader(new ByteArrayInputStream(note.getContent().getBytes(
				Charset.forName("UTF-8"))));

		XMLEventWriter writer = outputFactory.createXMLEventWriter(out);

		XMLEvent lastEvent = null;

		while (reader.hasNext()) {
			XMLEvent event = (XMLEvent) reader.next();
			if (event.getEventType() == XMLEvent.DTD) {
				writer.add(eventFactory
						.createDTD("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">"));
				StartElement newElement = eventFactory.createStartElement("", "http://www.w3.org/1999/xhtml", "html");
				writer.add(newElement);
			} else if (event.getEventType() == XMLEvent.START_ELEMENT) {

				StartElement startElement = event.asStartElement();
				if (currentConverter.containsKey(startElement.getName().getLocalPart())) {

					Converter converter = currentConverter.get(startElement.getName().getLocalPart());

					List<XMLEvent> elementsToInsert = converter.insertBefore(startElement, note, mapHashURL);
					if (elementsToInsert != null) {
						for (XMLEvent element : elementsToInsert) {
							writer.add(element);
						}
					}
					Elements convertedElements = converter.convertElement(startElement, note, mapHashURL);
					writer.add(convertedElements.getStartElement());
					stack.push(convertedElements.getEndElement());

					elementsToInsert = converter.insertAfter(startElement, note, mapHashURL);
					toInsertAfter.put(convertedElements.getEndElement(), elementsToInsert);

					elementsToInsert = converter.insertIn(startElement, note, mapHashURL);
					if (elementsToInsert != null) {
						for (XMLEvent element : elementsToInsert) {
							writer.add(element);
						}
					}
				} else {
					writer.add(event);
				}
			} else if (event.getEventType() == XMLEvent.CHARACTERS) {
				Characters characters = event.asCharacters();
				if (lastEvent != null && lastEvent.isStartElement()) {
					StartElement lastStartElement = lastEvent.asStartElement();
					if (currentConverter.containsKey(lastStartElement.asStartElement().getName().getLocalPart())) {
						Converter converter = currentConverter.get(lastStartElement.getName().getLocalPart());
						Characters convertedCharacter = converter.convertCharacter(characters, lastStartElement, note,
								mapHashURL);
						if (convertedCharacter != null) {
							writer.add(convertedCharacter);
						} else {
							writer.add(characters);
						}
					} else {
						writer.add(event);
					}
				} else {
					writer.add(event);
				}
			} else if (event.getEventType() == XMLEvent.END_ELEMENT) {
				if (currentConverter.containsKey(event.asEndElement().getName().getLocalPart())) {

					EndElement endElement = stack.pop();
					writer.add(endElement);
					
					if (toInsertAfter.containsKey(endElement)) {
						if (toInsertAfter.get(endElement) != null) {
							for (XMLEvent element : toInsertAfter.get(endElement)) {
								writer.add(element);
							}
						}
					}
				} else {
					writer.add(event);
				}
			} else {
				writer.add(event);
			}
			lastEvent = event;
		}
		writer.flush();

		log.fine("Note " + note.getGuid() + " has been converted in "
				+ Utils.getDurationBreakdown(System.currentTimeMillis() - start));
		return;
	}

	/**
	 * Updates the {@code Note} content with the information from it's {@code Resource}s.
	 * <p>
	 * Using this methods supposes that {@link Note} in parameter contain an updated list of {@link Resource}s and it's
	 * ENML content contain the corresponding {@code <en-media>} tags. The methods parse the ENML content of the
	 * {@code Note} and fill the hash attribute of the {@code <en-media>} by a new hash created from the {@code Note}'s
	 * list of {@code Resource} . <br>
	 * The {@code Resource}s and the {@code <en-media>} tag are processed in order. So the first {@code <en-media>} hash
	 * attribute will be updated with the hash of the first {@code Resource} in the list.
	 * 
	 * @param note the Note to update. It has to contain the list of {@code Resource}s to update and a ENML content
	 * @return the number of {@code <en-media>} hash attribute left to update. This number should be 0. If not that
	 *         means there is more {@code Resource}s objects returns by {@code Note#getResourcesSize()} than
	 *         {@code <en-media>} tags in the {@code Note} ENML content.
	 * @throws XMLStreamException if there is an unexpected processing error, like a malformed ENML content in the Note
	 * @throws NoSuchElementException if there is more {@code <en-media>} tags in the {@code Note} ENML content than
	 *             number {@code Resource}s objects returns by {@code Note#getResourcesSize()}
	 */
	public final int updateNoteContentWithRessources(final Note note) throws XMLStreamException {
		long start = System.currentTimeMillis();
		log.finer("Update ENML content of Note " + note.getGuid());

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		XMLEventReader reader = inputFactory.createXMLEventReader(new ByteArrayInputStream(note.getContent().getBytes(
				Charset.forName("UTF-8"))));

		XMLEventWriter writer = outputFactory.createXMLEventWriter(baos);

		Iterator<Resource> resourceIterator = note.getResources().iterator();

		int toUpdateCount = note.getResourcesSize();
		while (reader.hasNext()) {
			XMLEvent event = (XMLEvent) reader.next();
			if (event.getEventType() == XMLEvent.START_DOCUMENT) {
				StartElement startElement = event.asStartElement();
				if (startElement.getName().getLocalPart().equals(MEDIA)) {
					for (@SuppressWarnings("unchecked")
					Iterator<Attribute> iterator = startElement.getAttributes(); iterator.hasNext();) {
						Attribute attr = (Attribute) iterator.next();
						if (attr.getName().getLocalPart().equals("hash")) {
							writer.add(eventFactory.createAttribute("hash",
									Utils.bytesToHex(resourceIterator.next().getData().getBodyHash())));
							toUpdateCount--;
						} else {
							writer.add(attr);
						}
					}
				} else {
					writer.add(event);
				}
			} else {
				writer.add(event);
			}
		}
		note.setContent(new String(baos.toByteArray(), Charset.forName("UTF-8")));
		log.fine("Note ENML content " + note.getGuid() + " has been updated in "
				+ Utils.getDurationBreakdown(System.currentTimeMillis() - start));
		return toUpdateCount;
	}

	/**
	 * Updates the {@code Note} content with the information of new {@code Resource}s.
	 * <p>
	 * The update consist in replacing the 'hash' attributes of tags {@code <en-media>} in the ENML content of the
	 * {@code Note}. The attributes are updated based on the mapping represented by {@code Map<Resource, Resource>} in
	 * parameter. <br>
	 * For every {@code Resource} that has to be updated this {@code Map} has to contain an entry with:
	 * <ul>
	 * <li>
	 * the {@code Resource} to update (the old Resource)</li>
	 * <li>the {@code Resource} to update with (the new Resource)</li>
	 * </ul>
	 * <br>
	 * The methods assumes the {@code Note} has an ENML content and the {@code Resource} in the map has data (to be able
	 * to compute the hash). <br>
	 * If a {@code Resource} is present in the {@code Note} but not in the {@code Map} it will stays untouched both in
	 * the ENML content and in the {@code Resource} list of the {@code Note}. <br>
	 * If a {@code Resource} is present in the {@code Map} as a new one, but is not referenced in the {@code Note} ENML
	 * content no update will be performed for the {@code Map} entry. <br>
	 * The methods will take care of removing the old {@code Resource} objects in the {@code Note} list after they have
	 * been updated.
	 * 
	 * @param note the Note to update. It has to contain an ENML content.
	 * @param oldNewResourcesMap the mapping of old and new {@code Resource}s
	 * @throws XMLStreamException if there is an unexpected processing error, like a malformed ENML content in the Note
	 */
	public final void updateNoteResources(final Note note, final Map<Resource, Resource> oldNewResourcesMap)
			throws XMLStreamException {

		Map<String, Resource> hashResourceMap = new HashMap<String, Resource>();

		for (Resource oldResource : oldNewResourcesMap.keySet()) {
			hashResourceMap.put(Utils.bytesToHex(oldResource.getData().getBodyHash()),
					oldNewResourcesMap.get(oldResource));
		}
		updateNoteResourcesByHash(note, hashResourceMap);
	}

	/**
	 * Updates the {@code Note} content with the information of new {@code Resource}s.
	 * <p>
	 * The update consist in replacing the 'hash' attributes of tags {@code <en-media>} in the ENML content of the
	 * {@code Note}. The attributes are updated based on the mapping represented by {@code Map<String, String>} in
	 * parameter. <br>
	 * The methods assumes the {@code Note} has an ENML content and it's {@code Resource}s has data (to be able to
	 * compute the hash). <br>
	 * For every {@code Resource} that has to be updated this {@code Map} has to contain an entry with:
	 * <ul>
	 * <li>the GUID of the {@code Resource} to update (the old Resource)</li>
	 * <li>the GUID of the {@code Resource} to update with (the new Resource)</li>
	 * </ul>
	 * <br>
	 * If a {@code Resource}s referenced by its GUID in the Map is not in the {@code Resource} list of the {@code Note}
	 * no update will be performed for the {@code Map} entry. <br>
	 * If a {@code Resource} is present in the {@code Note} but not in the {@code Map} it will stays untouched both in
	 * the ENML content and in the {@code Resource} list of the {@code Note}. <br>
	 * The methods will take care of removing the old {@code Resource} objects in the {@code Note} list after they have
	 * been updated.
	 * 
	 * @param note the Note to update. It has to contain an ENML content.
	 * @param oldNewResourcesMap the mapping of old and new {@code Resource}s
	 * @throws XMLStreamException if there is an unexpected processing error, like a malformed ENML content in the Note
	 */
	public final void updateNoteResourcesByGUID(final Note note, final Map<String, String> oldNewResourcesMap)
			throws XMLStreamException {

		Map<String, Resource> hashResourceMap = new HashMap<String, Resource>();

		for (String oldGuid : oldNewResourcesMap.keySet()) {
			Resource oldResource = null;
			Resource newResource = null;
			for (Resource resource : note.getResources()) {
				if (resource.getGuid().equals(oldGuid)) {
					oldResource = resource;
				} else if (resource.getGuid().equals(oldNewResourcesMap.get(oldGuid))) {
					newResource = resource;
				}
			}
			if (oldResource != null & newResource != null) {
				hashResourceMap.put(Utils.bytesToHex(oldResource.getData().getBodyHash()), newResource);
			}
		}
		updateNoteResourcesByHash(note, hashResourceMap);
	}

	private void updateNoteResourcesByHash(final Note note, final Map<String, Resource> oldNewResourcesMap)
			throws XMLStreamException {

		long start = System.currentTimeMillis();
		log.finer("Update ENML content with Resource mapping of Note " + note.getGuid());

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		XMLEventReader reader = inputFactory.createXMLEventReader(new ByteArrayInputStream(note.getContent().getBytes(
				Charset.forName("UTF-8"))));

		XMLEventWriter writer = outputFactory.createXMLEventWriter(baos);

		List<String> hashToDelete = new ArrayList<String>();

		while (reader.hasNext()) {
			XMLEvent event = (XMLEvent) reader.next();
			if (event.getEventType() == XMLEvent.START_DOCUMENT) {
				StartElement startElement = event.asStartElement();
				if (startElement.getName().getLocalPart().equals(MEDIA)) {
					for (@SuppressWarnings("unchecked")
					Iterator<Attribute> iterator = startElement.getAttributes(); iterator.hasNext();) {
						Attribute attr = (Attribute) iterator.next();
						if (attr.getName().getLocalPart().equals("hash")) {
							// If the resource has to be updated (is in the map)
							if (oldNewResourcesMap.containsKey(attr.getValue())) {
								Resource toUpdate = oldNewResourcesMap.get(attr.getValue());
								writer.add(eventFactory.createAttribute("hash",
										Utils.bytesToHex(toUpdate.getData().getBodyHash())));
								// if the ressource to update is not in the Note, add it
								// This way the result Note will be consistent no matter if the Resource to update has
								// already been added to the Note
								if (!note.getResources().contains(toUpdate)) {
									hashToDelete.add(attr.getValue());
									note.addToResources(toUpdate);
								}
							} else {
								writer.add(attr);
							}
						} else {
							writer.add(attr);
						}
					}
				} else {
					writer.add(event);
				}
			} else {
				writer.add(event);
			}
		}
		// Remove the original resources after they have been updated
		for (Resource resource : note.getResources()) {
			if (hashToDelete.contains(Utils.bytesToHex(resource.getData().getBodyHash()))) {
				note.getResources().remove(resource);
			}
		}
		note.setContent(new String(baos.toByteArray(), Charset.forName("UTF-8")));
		log.fine("Note ENML content of " + note.getGuid() + " has been updated with resource mapping in "
				+ Utils.getDurationBreakdown(System.currentTimeMillis() - start));
	}

	/**
	 * @return the {@code XMLInputFactory} used to creates the {@code XMLEventWriter} used to write output HTML.
	 */
	public final XMLInputFactory getInputFactory() {
		return inputFactory;
	}

	/**
	 * @return the {@code XMLOutputFactory} used to creates the {@code XMLEventReader} used to read input ENML.
	 */
	public final XMLOutputFactory getOutputFactory() {
		return outputFactory;
	}

}
