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
package com.syncthemall.enml4j.util;

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