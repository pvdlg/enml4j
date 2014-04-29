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
package com.syncthemall.enml4j;

import static com.syncthemall.enml4j.util.Constants.ALT;
import static com.syncthemall.enml4j.util.Constants.CHARSET;
import static com.syncthemall.enml4j.util.Constants.CRYPT;
import static com.syncthemall.enml4j.util.Constants.HASH;
import static com.syncthemall.enml4j.util.Constants.HEIGHT;
import static com.syncthemall.enml4j.util.Constants.HTML;
import static com.syncthemall.enml4j.util.Constants.MEDIA;
import static com.syncthemall.enml4j.util.Constants.NOTE;
import static com.syncthemall.enml4j.util.Constants.TODO;
import static com.syncthemall.enml4j.util.Constants.TYPE;
import static com.syncthemall.enml4j.util.Constants.WIDTH;
import static com.syncthemall.enml4j.util.Constants.XMLNS;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLResolver;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import com.evernote.edam.type.Data;
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
 * 
 * @author Pierre-Denis Vanduynslager <pierre.denis.vanduynslager@gmail.com>
 */
public class ENMLProcessor {

	private static Logger log = Logger.getLogger(ENMLProcessor.class.getName());

	/** XHTML Transitional doctype. */
	private static final String XHTML_DOCTYPE = "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">";

	/** XHTML namespace. */
	private static final String XHTML_NAMESPACE = "http://www.w3.org/1999/xhtml";

	private Map<String, Converter> converters = new HashMap<String, Converter>();
	private Map<String, Converter> inlineConverters = new HashMap<String, Converter>();

	/** An instance of {@code XMLEventFactory} used to creates new {@link XMLEvent}s. */
	private XMLEventFactory eventFactory = XMLEventFactory.newInstance();

	/** An instance of {@code XMLInputFactory} used to read XML content. */
	private XMLInputFactory inputFactory = XMLInputFactory.newInstance();

	/** An instance of {@code XMLOutputFactory} used to write XML content. */
	private XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();

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
		converters.put(NOTE, new DefaultNoteTagConverter().setEventFactory(eventFactory));
		converters.put(MEDIA, new DefaultMediaTagConverter().setEventFactory(eventFactory));
		converters.put(TODO, new DefaultTodoTagConverter().setEventFactory(eventFactory));
		converters.put(CRYPT, new DefaultCryptTagConverter().setEventFactory(eventFactory));

		inlineConverters.put(NOTE, new DefaultNoteTagConverter().setEventFactory(eventFactory));
		inlineConverters.put(MEDIA, new DefaultInlineMediaTagConverter().setEventFactory(eventFactory));
		inlineConverters.put(TODO, new DefaultTodoTagConverter().setEventFactory(eventFactory));
		inlineConverters.put(CRYPT, new DefaultCryptTagConverter().setEventFactory(eventFactory));

		inputFactory.setXMLResolver(new XMLResolver() {
			@Override
			public Object resolveEntity(final String publicID, final String systemID, final String baseURI,
					final String namespace) throws XMLStreamException {
				if ("http://xml.evernote.com/pub/enml2.dtd".equals(systemID)) {
					return getClass().getResourceAsStream("/dtd/enml2.dtd");
				} else if ("http://www.w3.org/TR/xhtml1/DTD/xhtml-lat1.ent".equals(systemID)) {
					return getClass().getResourceAsStream("/dtd/xhtml-lat1.ent");
				} else if ("http://www.w3.org/TR/xhtml1/DTD/xhtml-symbol.ent".equals(systemID)) {
					return getClass().getResourceAsStream("/dtd/xhtml-symbol.ent");
				} else if ("http://www.w3.org/TR/xhtml1/DTD/xhtml-special.ent".equals(systemID)) {
					return getClass().getResourceAsStream("/dtd/xhtml-special.ent");
				}
				return null;
			}
		});
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
			converters.put(NOTE, noteConverter.setEventFactory(eventFactory));
		} else {
			converters.put(NOTE, new DefaultNoteTagConverter().setEventFactory(eventFactory));
		}
		if (mediaConverter != null) {
			converters.put(MEDIA, mediaConverter.setEventFactory(eventFactory));
		} else {
			converters.put(MEDIA, new DefaultMediaTagConverter().setEventFactory(eventFactory));
		}
		if (todoConverter != null) {
			converters.put(TODO, todoConverter.setEventFactory(eventFactory));
		} else {
			converters.put(TODO, new DefaultTodoTagConverter().setEventFactory(eventFactory));
		}
		if (cryptConverter != null) {
			converters.put(CRYPT, cryptConverter.setEventFactory(eventFactory));
		} else {
			converters.put(CRYPT, new DefaultCryptTagConverter().setEventFactory(eventFactory));
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
			inlineConverters.put(NOTE, noteConverter.setEventFactory(eventFactory));
		} else {
			inlineConverters.put(NOTE, new DefaultNoteTagConverter().setEventFactory(eventFactory));
		}
		if (mediaConverter != null) {
			inlineConverters.put(MEDIA, mediaConverter.setEventFactory(eventFactory));
		} else {
			inlineConverters.put(MEDIA, new DefaultInlineMediaTagConverter().setEventFactory(eventFactory));
		}
		if (todoConverter != null) {
			inlineConverters.put(TODO, todoConverter);
		} else {
			inlineConverters.put(TODO, new DefaultTodoTagConverter().setEventFactory(eventFactory));
		}
		if (cryptConverter != null) {
			inlineConverters.put(CRYPT, cryptConverter.setEventFactory(eventFactory));
		} else {
			inlineConverters.put(CRYPT, new DefaultCryptTagConverter().setEventFactory(eventFactory));
		}
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
		return new String(baos.toByteArray(), Charset.forName(CHARSET));
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
	 * @return the {@code OutputStream} in parameter containing the resulting HTML file
	 * @throws XMLStreamException if there is an unexpected processing error, like a malformed ENML content in the Note
	 */
	public final OutputStream noteToInlineHTML(final Note note, final OutputStream out) throws XMLStreamException {
		return noteToHTML(note, null, out, true);
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
	 * @param mapGUIDURL the mapping of {@code Resource}s GUID with their corresponding physical files path
	 * @return a {@code String} containing the resulting HTML file
	 * @throws XMLStreamException if there is an unexpected processing error, like a malformed ENML content in the Note
	 */
	public final String noteToHTMLString(final Note note, final Map<String, String> mapGUIDURL)
			throws XMLStreamException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		Map<String, String> hashURLMap = new HashMap<String, String>();
		if (mapGUIDURL != null) {
			for (Map.Entry<String, String> mapGUIDURLEntry : mapGUIDURL.entrySet()) {
				for (Resource resource : note.getResources()) {
					if (resource.getGuid().equals(mapGUIDURLEntry.getKey())) {
						hashURLMap.put(Utils.bytesToHex(resource.getData().getBodyHash()), mapGUIDURLEntry.getValue());
					}
				}
			}
		}
		noteToHTML(note, hashURLMap, baos, false);
		return new String(baos.toByteArray(), Charset.forName(CHARSET));
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
	 * @param mapGUIDURL the mapping of {@code Resource}s GUID with their corresponding physical files path
	 * @param out an {@code OutputStream} in which to write the resulting HTML file
	 * @return the {@code OutputStream} in parameter containing the resulting HTML file
	 * @throws XMLStreamException if there is an unexpected processing error, like a malformed ENML content in the Note
	 */
	public final OutputStream noteToHTML(final Note note, final Map<String, String> mapGUIDURL, final OutputStream out)
			throws XMLStreamException {

		Map<String, String> hashURLMap = new HashMap<String, String>();

		if (mapGUIDURL != null) {
			for (Map.Entry<String, String> mapGUIDURLEntry : mapGUIDURL.entrySet()) {
				for (Resource resource : note.getResources()) {
					if (resource.getGuid().equals(mapGUIDURLEntry.getKey())) {
						hashURLMap.put(Utils.bytesToHex(resource.getData().getBodyHash()), mapGUIDURLEntry.getValue());
					}
				}
			}
		}
		return noteToHTML(note, hashURLMap, out, false);
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
	 * The methods assumes the {@code Note} has an ENML content and the {@code Resource} in the {@code Map} has data (to
	 * be able to compute the hash). <br>
	 * If a {@code Resource} in the Map is not in the {@code List<Resource>} of the {@code Note} no update will be
	 * performed for the {@code Map} entry.<br>
	 * The methods will take care of removing the old {@code Resource} objects in the {@code Note} list after they have
	 * been updated.
	 * 
	 * @param note the Note to update. It has to contain an ENML content.
	 * @param oldNewResourcesMap the mapping of old and new {@code Resource}s
	 * @return the {@code Note} in parameter with updated content and {@code Resource}
	 * @throws XMLStreamException if there is an unexpected processing error, like a malformed ENML content in the Note
	 * @throws NoSuchAlgorithmException if no Provider supports a MessageDigestSpi implementation for MD5 algorithm.
	 */
	public final Note updateNoteResources(final Note note, final Map<Resource, Resource> oldNewResourcesMap)
			throws XMLStreamException, NoSuchAlgorithmException {

		Map<String, Resource> hashResourceMap = new HashMap<String, Resource>();

		for (Map.Entry<Resource, Resource> oldNewResourcesMapEntry : oldNewResourcesMap.entrySet()) {
			hashResourceMap.put(Utils.bytesToHex(oldNewResourcesMapEntry.getKey().getData().getBodyHash()),
					oldNewResourcesMapEntry.getValue());
		}
		return updateNoteResourcesByHash(note, hashResourceMap);
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
	 * If a {@code Resource}s referenced by its GUID in the Map is not in the {@code List<Resource>} of the {@code Note}
	 * no update will be performed for the {@code Map} entry.<br>
	 * The method will take care of removing the old {@code Resource} objects in the {@code Note} list after they have
	 * been updated.
	 * 
	 * @param note the Note to update. It has to contain an ENML content.
	 * @param oldNewResourcesMap the mapping of old and new {@code Resource}s
	 * @return the {@code Note} in parameter with updated content and {@code Resource}
	 * @throws XMLStreamException if there is an unexpected processing error, like a malformed ENML content in the Note
	 * @throws NoSuchAlgorithmException if no Provider supports a MessageDigestSpi implementation for MD5 algorithm.
	 */
	public final Note updateNoteResourcesByGUID(final Note note, final Map<String, String> oldNewResourcesMap)
			throws XMLStreamException, NoSuchAlgorithmException {

		Map<String, Resource> hashResourceMap = new HashMap<String, Resource>();

		for (Map.Entry<String, String> oldNewResourcesMapEntry : oldNewResourcesMap.entrySet()) {
			Resource oldResource = null;
			Resource newResource = null;
			for (Resource resource : note.getResources()) {
				if (resource.getGuid().equals(oldNewResourcesMapEntry.getKey())) {
					oldResource = resource;
				} else if (resource.getGuid().equals(oldNewResourcesMapEntry.getValue())) {
					newResource = resource;
				}
			}
			if (oldResource != null & newResource != null) {
				hashResourceMap.put(Utils.bytesToHex(oldResource.getData().getBodyHash()), newResource);
			}
		}
		return updateNoteResourcesByHash(note, hashResourceMap);
	}

	/**
	 * Updates the {@code Note} content by removing the information of a {@code List<Resource>}.
	 * <p>
	 * The update consist in removing the tags {@code <en-media>} in the ENML content of the {@code Note}. The tags are
	 * removed based on the {@code List<Resource>} in parameter. <br>
	 * The methods assumes the {@code Note} has an ENML content and the {@code Resource}s in the {@code List<Resource}
	 * in parameter has data (to be able to compute the hash). <br>
	 * 
	 * If a {@code Resource} is present in the {@code Note} but not in the {@code List<Resource>} to delete it will
	 * stays untouched both in the ENML content and in the {@code Resource} list of the {@code Note} <br>
	 * The method will take care of removing the deleted {@code Resource} object from the {@code Note} list.
	 * 
	 * @param note the Note to update. It has to contain an ENML content.
	 * @param resourcesToDelete {@code List<Resource>} to remove from the {@code Note}
	 * @return the {@code Note} in parameter with updated content and {@code Resource}
	 * @throws XMLStreamException if there is an unexpected processing error, like a malformed ENML content in the Note
	 */
	public final Note deleteNoteResources(final Note note, final List<Resource> resourcesToDelete)
			throws XMLStreamException {

		List<String> hashToDelete = new ArrayList<String>();

		for (Resource resource : resourcesToDelete) {
			hashToDelete.add(Utils.bytesToHex(resource.getData().getBodyHash()));
		}
		return deleteNoteResourcesByHash(note, hashToDelete);
	}

	/**
	 * Updates the {@code Note} content by removing the information of a {@code Resource} represented by a
	 * {@code List<String>} of GUID.
	 * <p>
	 * The update consist in removing the tags {@code <en-media>} in the ENML content of the {@code Note}. The tags are
	 * removed based on the {@code List<String>} in parameter. <br>
	 * The methods assumes the {@code Note} has an ENML content and it's {@code Resource}s has data (to be able to
	 * compute the hash). <br>
	 * If a {@code Resource} is present in the {@code Note} but not in the {@code List<String>} to delete it will stays
	 * untouched both in the ENML content and in the {@code Resource} list of the {@code Note} <br>
	 * The method will take care of removing the deleted {@code Resource} object from the {@code Note} list.
	 * 
	 * @param note the Note to update. It has to contain an ENML content.
	 * @param guidsToDelete {@code List<String>} of GUID to remove from the {@code Note}
	 * @return the {@code Note} in parameter with updated content and {@code Resource}
	 * @throws XMLStreamException if there is an unexpected processing error, like a malformed ENML content in the Note
	 */
	public final Note deleteNoteResourcesByGUID(final Note note, final List<String> guidsToDelete)
			throws XMLStreamException {

		List<String> hashToDelete = new ArrayList<String>();

		for (String guidToDelete : guidsToDelete) {
			for (Resource resource : note.getResources()) {
				if (resource.getGuid().equals(guidToDelete)) {
					hashToDelete.add(Utils.bytesToHex(resource.getData().getBodyHash()));
				}
			}
		}
		return deleteNoteResourcesByHash(note, hashToDelete);
	}

	/**
	 * Updates the {@code Note} content by adding the information of a {@code List<Resource>}.
	 * <p>
	 * The update consist in adding the tags {@code <en-media>} in the ENML content of the {@code Note}. The tags are
	 * added based on the {@code List<Resource>} in parameter. <br>
	 * The methods assumes the {@code Note} has an ENML content and the {@code Resource}s in the {@code List<Resource}
	 * in parameter has data (to be able to compute the hash). <br>
	 * 
	 * All {@code Resource} present in the {@code Note} will stays untouched both in the ENML content and in the
	 * {@code Resource} list of the {@code Note} <br>
	 * The method will take care of adding the added {@code Resource} object to the {@code Note} list.
	 * 
	 * @param note the Note to update. It has to contain an ENML content.
	 * @param resourcesToAdd {@code List<Resource>} to add to the {@code Note}
	 * @param addToTop true to add the {@code <en-media>} at the top of the Note content, false to add it at the bottom
	 * @return the {@code Note} in parameter with updated content and {@code Resource}
	 * @throws XMLStreamException if there is an unexpected processing error, like a malformed ENML content in the Note
	 * @throws NoSuchAlgorithmException if no Provider supports a MessageDigestSpi implementation for MD5 algorithm.
	 */
	public final Note addNoteResources(final Note note, final List<Resource> resourcesToAdd, final boolean addToTop)
			throws XMLStreamException, NoSuchAlgorithmException {
		long start = System.currentTimeMillis();
		log.finer("Add resources from ENML content of Note " + note.getGuid());

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		XMLEventReader reader = inputFactory.createXMLEventReader(new ByteArrayInputStream(note.getContent().getBytes(
				Charset.forName(CHARSET))));
		XMLEventWriter writer = outputFactory.createXMLEventWriter(baos);

		while (reader.hasNext()) {
			XMLEvent event = (XMLEvent) reader.next();
			if ((addToTop && event.getEventType() == XMLEvent.START_ELEMENT && event.asStartElement().getName()
					.getLocalPart().equals(NOTE))
					|| (!addToTop && event.getEventType() == XMLEvent.END_ELEMENT && event.asEndElement().getName()
							.getLocalPart().equals(NOTE))) {
				if (addToTop) {
					writer.add(event);
				}
				for (Resource resource : resourcesToAdd) {
					List<Attribute> attrs = new ArrayList<Attribute>();
					if (resource.getWidth() != 0) {
						attrs.add(eventFactory.createAttribute(WIDTH, String.valueOf(resource.getWidth())));
					}
					if (resource.getWidth() != 0) {
						attrs.add(eventFactory.createAttribute(HEIGHT, String.valueOf(resource.getHeight())));
					}
					attrs.add(eventFactory.createAttribute(TYPE, resource.getMime()));
					if (resource.getAttributes() != null) {
						attrs.add(eventFactory.createAttribute(ALT, resource.getAttributes().getFileName()));
					}
					// Make sure the Resource Data is valid with proper hash and length
					resource.setData(createData(resource.getData().getBody()));
					attrs.add(eventFactory.createAttribute(HASH, Utils.bytesToHex(resource.getData().getBodyHash())));
					writer.add(eventFactory.createStartElement("", "", MEDIA, attrs.iterator(), null));
					writer.add(eventFactory.createEndElement("", "", MEDIA));
					// Add the resource to the note's resources if not already there
					addResourceObjectToNote(note, resource);
				}
				if (!addToTop) {
					writer.add(event);
				}

			} else {
				writer.add(event);
			}
		}

		note.setContent(new String(baos.toByteArray(), Charset.forName(CHARSET)));
		log.fine("Note ENML content of " + note.getGuid() + " has been updated with resource mapping in "
				+ Utils.getDurationBreakdown(System.currentTimeMillis() - start));
		return note;
	}

	private OutputStream noteToHTML(final Note note, final Map<String, String> mapHashURL, final OutputStream out,
			final boolean inline) throws XMLStreamException {

		long start = System.currentTimeMillis();
		log.finer("Converting Note " + note.getGuid() + " to HTML");

		Map<String, Converter> currentConverter;
		if (inline) {
			currentConverter = inlineConverters;
		} else {
			currentConverter = converters;
		}

		ArrayDeque<EndElement> stack = new ArrayDeque<EndElement>();
		Map<EndElement, List<XMLEvent>> toInsertAfter = new HashMap<EndElement, List<XMLEvent>>();

		XMLEventReader reader = inputFactory.createXMLEventReader(new ByteArrayInputStream(note.getContent().getBytes(
				Charset.forName(CHARSET))));

		XMLEventWriter writer = outputFactory.createXMLEventWriter(out);

		XMLEvent lastEvent = null;

		while (reader.hasNext()) {
			XMLEvent event = (XMLEvent) reader.next();
			if (event.getEventType() == XMLEvent.DTD) {
				writer.add(eventFactory.createDTD(XHTML_DOCTYPE));
				StartElement newElement = eventFactory.createStartElement("", "", HTML,
						Arrays.asList(eventFactory.createAttribute(XMLNS, XHTML_NAMESPACE)).iterator(), null);
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

					if (toInsertAfter.containsKey(endElement) && toInsertAfter.get(endElement) != null) {
						for (XMLEvent element : toInsertAfter.get(endElement)) {
							writer.add(element);
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
		return out;
	}

	private Data createData(final byte[] dataBody) throws NoSuchAlgorithmException {
		Data data = new Data();
		data.setSize(dataBody.length);
		data.setBodyHash(MessageDigest.getInstance("MD5").digest(dataBody));
		data.setBody(dataBody);
		return data;
	}

	private void addResourceObjectToNote(final Note note, final Resource resource) {
		if (note.getResources() == null) {
			note.setResources(new ArrayList<Resource>());
		}
		boolean resourceExisting = false;
		for (Iterator<Resource> iterator = note.getResources().iterator(); iterator.hasNext();) {
			Resource existingResource = iterator.next();
			if (Arrays.equals(existingResource.getData().getBodyHash(), resource.getData().getBodyHash())) {
				resourceExisting = true;
				break;
			}
		}
		if (!resourceExisting) {
			note.addToResources(resource);
		}
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
	 * <li>the hash of the {@code Resource} to update (the old Resource) as it is in the {@code Note} ENML content</li>
	 * <li>the {@code Resource} to update with (the new Resource)</li>
	 * </ul>
	 * The method will take care of removing the old {@code Resource} objects in the {@code Note} list after they have
	 * been updated.
	 * 
	 * @param note the Note to update. It has to contain an ENML content.
	 * @param oldNewResourcesMap the mapping of old and new {@code Resource}s
	 * @return the {@code Note} in parameter with updated content
	 * @throws XMLStreamException if there is an unexpected processing error, like a malformed ENML content in the Note
	 * @throws NoSuchAlgorithmException if no Provider supports a MessageDigestSpi implementation for MD5 algorithm.
	 */
	private Note updateNoteResourcesByHash(final Note note, final Map<String, Resource> oldNewResourcesMap)
			throws XMLStreamException, NoSuchAlgorithmException {

		long start = System.currentTimeMillis();
		log.finer("Update ENML content with Resource mapping of Note " + note.getGuid());

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		XMLEventReader reader = inputFactory.createXMLEventReader(new ByteArrayInputStream(note.getContent().getBytes(
				Charset.forName(CHARSET))));

		XMLEventWriter writer = outputFactory.createXMLEventWriter(baos);

		List<String> hashToDelete = new ArrayList<String>();

		while (reader.hasNext()) {
			XMLEvent event = (XMLEvent) reader.next();
			if (event.getEventType() == XMLEvent.START_ELEMENT) {
				StartElement startElement = event.asStartElement();
				if (startElement.getName().getLocalPart().equals(MEDIA)) {
					for (@SuppressWarnings("unchecked")
					Iterator<Attribute> iterator = startElement.getAttributes(); iterator.hasNext();) {
						Attribute attr = iterator.next();
						if (attr.getName().getLocalPart().equals(HASH)) {
							// If the resource has to be updated (is in the map)
							if (oldNewResourcesMap.containsKey(attr.getValue())) {
								Resource toUpdate = oldNewResourcesMap.get(attr.getValue());
								List<Attribute> attributes = new ArrayList<Attribute>();
								// Make sure the Resource Data is valid with proper hash and length
								toUpdate.setData(createData(toUpdate.getData().getBody()));
								attributes.add(eventFactory.createAttribute(HASH,
										Utils.bytesToHex(toUpdate.getData().getBodyHash())));
								attributes.add(eventFactory.createAttribute(TYPE, toUpdate.getMime()));
								for (@SuppressWarnings("unchecked")
								Iterator<Attribute> iterator2 = startElement.getAttributes(); iterator2.hasNext();) {
									Attribute attribute = iterator2.next();
									if (!attribute.getName().getLocalPart().equals(HASH)
											&& !attribute.getName().getLocalPart().equals(TYPE)) {
										attributes.add(attribute);
									}
								}
								hashToDelete.add(attr.getValue());
								// Add the resource to the note's resources if not already there
								addResourceObjectToNote(note, toUpdate);
								writer.add(eventFactory.createStartElement("", "", MEDIA, attributes.iterator(), null));
							} else {
								writer.add(event);
							}
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
		removeResourceObjectFromNote(note, hashToDelete);

		note.setContent(new String(baos.toByteArray(), Charset.forName(CHARSET)));
		log.fine("Note ENML content of " + note.getGuid() + " has been updated with resource mapping in "
				+ Utils.getDurationBreakdown(System.currentTimeMillis() - start));
		return note;
	}

	private Note deleteNoteResourcesByHash(final Note note, final List<String> hashToDelete) throws XMLStreamException {

		long start = System.currentTimeMillis();
		log.finer("Delete resources from ENML content of Note " + note.getGuid());

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		XMLEventReader reader = inputFactory.createXMLEventReader(new ByteArrayInputStream(note.getContent().getBytes(
				Charset.forName(CHARSET))));
		XMLEventWriter writer = outputFactory.createXMLEventWriter(baos);
		boolean dropNext = false;
		while (reader.hasNext()) {
			XMLEvent event = (XMLEvent) reader.next();
			if (event.getEventType() == XMLEvent.START_ELEMENT) {
				StartElement startElement = event.asStartElement();
				if (startElement.getName().getLocalPart().equals(MEDIA)) {
					for (@SuppressWarnings("unchecked")
					Iterator<Attribute> iterator = startElement.getAttributes(); iterator.hasNext();) {
						Attribute attr = iterator.next();
						if (attr.getName().getLocalPart().equals(HASH)) {
							// If the resource is in the list to delete
							if (!hashToDelete.contains(attr.getValue())) {
								writer.add(event);
							} else {
								dropNext = true;
							}
						}
					}
				} else {
					if (!dropNext) {
						writer.add(event);
					}
				}
			} else if (event.getEventType() == XMLEvent.END_ELEMENT) {
				EndElement endElement = event.asEndElement();
				if (!dropNext) {
					writer.add(event);
				}
				if (endElement.getName().getLocalPart().equals(MEDIA)) {
					dropNext = false;
				}
			} else {
				if (!dropNext) {
					writer.add(event);
				}
			}
		}
		// Remove the original resources after they have been updated
		removeResourceObjectFromNote(note, hashToDelete);
		note.setContent(new String(baos.toByteArray(), Charset.forName(CHARSET)));
		log.fine("Note ENML content of " + note.getGuid() + " has been updated with resource mapping in "
				+ Utils.getDurationBreakdown(System.currentTimeMillis() - start));
		return note;
	}

	private void removeResourceObjectFromNote(final Note note, final List<String> hashToDelete) {
		if (note.getResources() != null) {
			for (Iterator<Resource> iterator = note.getResources().iterator(); iterator.hasNext();) {
				Resource resource = iterator.next();
				if (hashToDelete.contains(Utils.bytesToHex(resource.getData().getBodyHash()))) {
					iterator.remove();
				}
			}
		}
	}
}
