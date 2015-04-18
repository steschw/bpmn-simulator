/*
 * Copyright (C) 2015 Stefan Schweitzer
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
package com.googlecode.bpmn_simulator.animation.element.visual.swing;

import java.awt.font.TextAttribute;
import java.util.HashMap;
import java.util.Map;

import com.googlecode.bpmn_simulator.animation.element.visual.Font;

public final class FontUtils {

	private FontUtils() {
	}

	public static Map<TextAttribute, ?> toAttributes(final Font styleFont) {
		final Map<TextAttribute, Object> attributes = new HashMap<>();
		final String name = styleFont.getName();
		if (name != null) {
			attributes.put(TextAttribute.FAMILY, name);
		}
		final Double size = styleFont.getSize();
		if (size != null) {
			attributes.put(TextAttribute.SIZE, size);
		}
		if (styleFont.isBold()) {
			attributes.put(TextAttribute.WEIGHT, TextAttribute.WEIGHT_BOLD);
		}
		if (styleFont.isItalic()) {
			attributes.put(TextAttribute.POSTURE, TextAttribute.POSTURE_OBLIQUE);
		}
		if (styleFont.isUnderline()) {
			attributes.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
		}
		if (styleFont.isStrikeThrough()) {
			attributes.put(TextAttribute.STRIKETHROUGH, TextAttribute.STRIKETHROUGH_ON);
		}
		return attributes;
	}

}
