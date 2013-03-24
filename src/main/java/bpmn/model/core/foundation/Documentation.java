/*
 * Copyright (C) 2012 Stefan Schweitzer
 *
 * This software was created by Stefan Schweitzer as a student's project at
 * Fachhochschule Kaiserslautern (University of Applied Sciences).
 * Supervisor: Professor Dr. Thomas Allweyer. For more information please see
 * http://www.fh-kl.de/~allweyer
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this Software except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package bpmn.model.core.foundation;

import javax.activation.MimeType;
import javax.activation.MimeTypeParseException;

public class Documentation {

	private static final MimeType TEXT_PLAIN = newMimeType("text", "plain"); //$NON-NLS-1$ //$NON-NLS-2$
	private static final MimeType TEXT_HTML = newMimeType("text", "html"); //$NON-NLS-1$ //$NON-NLS-2$

	private static final MimeType DEFAULT_TEXTFORMAT = TEXT_PLAIN;

	private String text;

	private MimeType textFormat;

	public Documentation(final String text) {
		this(text, null);
	}

	public Documentation(final String text, final MimeType textFormat) {
		super();
		setText(text);
		setTextFormat(textFormat);
	}

	protected static final MimeType newMimeType(final String primary,
			final String sub) {
		try {
			return new MimeType(primary, sub);
		} catch (MimeTypeParseException e) {
			return null;
		}
	}

	private static String transformLineEnding(final String text) {
		return text.replaceAll("\r?\n", System.getProperty("line.separator")); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public void setText(final String text) {
		assert (text != null) && !text.isEmpty();
		this.text = transformLineEnding(text);
	}

	public final String getText() {
		return text;
	}

	public void setTextFormat(final MimeType textFormat) {
		assert (textFormat == null)
				|| textFormat.match(TEXT_PLAIN)
				|| textFormat.match(TEXT_HTML);
		this.textFormat = textFormat;
	}

	public MimeType getTextFormat() {
		return (textFormat == null) ? DEFAULT_TEXTFORMAT : textFormat;
	}

	private static String escapeHTML(final String text) {
		return text
				.replaceAll("&", "&amp;") //$NON-NLS-1$ //$NON-NLS-2$
				.replaceAll("<", "&lt;") //$NON-NLS-1$ //$NON-NLS-2$
				.replaceAll(">", "&gt;") //$NON-NLS-1$ //$NON-NLS-2$
				.replaceAll(System.getProperty("line.separator"), "<br/>"); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public String toHtml() {
		final MimeType textFormat = getTextFormat();
		if (TEXT_PLAIN.match(textFormat)) {
			return escapeHTML(getText());
		} else if (TEXT_HTML.match(textFormat)) {
			return getText();
		} else {
			assert false;
			return getText();
		}
	}

}
