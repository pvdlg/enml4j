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
package com.syncthemall.enml4j.exception;

import java.text.MessageFormat;

import com.evernote.edam.type.Note;
import com.evernote.edam.type.Resource;

/**
 * <em>Unchecked exceptions</em> Exception indicating an inconsistency between a resource referenced in the note content
 * and the {@link Resource} object list associated with the {@link Note}
 * 
 * @author Pierre-Denis Vanduynslager <pierre.denis.vanduynslager@gmail.com>
 */
public final class MissingResourceException extends RuntimeException {

	private static final long serialVersionUID = -745314206940896696L;
	private static final String MESSAGE = "The note {0} has a resource referenced in the note content but inexistant as a Resource object";

	/**
	 * Constructs a new {@code MissingResourceException} with a default detail message.
	 */
	public MissingResourceException() {
		super(MessageFormat.format(MESSAGE, "[unknown]"));
	}

	/**
	 * Constructs a new {@code MissingResourceException} with a detail message containing a {@code Note} title.
	 * 
	 * @param noteTitle the {@code Note} title.
	 */
	public MissingResourceException(final String noteTitle) {
		super(MessageFormat.format(MESSAGE, noteTitle));
	}

}
