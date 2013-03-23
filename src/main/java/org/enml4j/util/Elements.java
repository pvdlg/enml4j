package org.enml4j.util;

import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;

/**
 * A simple class to contain a {@code StartElement} and the corresponding {@code EndElement}.
 */
public class Elements {

	private final StartElement start;
	private final EndElement end;

	/**
	 * @param start  the {@code StartElement}
	 * @param end  the corresponding {@code EndElement}
	 */
	public Elements(final StartElement start, final EndElement end) {
		this.start = start;
		this.end = end;
	}

	/**
	 * @return the {@code StartElement}
	 */
	public final StartElement getStartElement() {
		return start;
	}

	/**
	 * @return the corresponding {@code EndElement}
	 */
	public final EndElement getEndElement() {
		return end;
	}

	@Override
	public final int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((start == null) ? 0 : start.hashCode());
		result = prime * result + ((end == null) ? 0 : end.hashCode());
		return result;
	}

	@Override
	public final boolean equals(final Object o) {
		if (o == null)
			return false;
		if (!(o instanceof Elements))
			return false;
		Elements pairo = (Elements) o;
		return this.start.equals(pairo.getStartElement())
				&& this.end.equals(pairo.getEndElement());
	}

}