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
package com.googlecode.bpmn_simulator.framework.utils;

import java.util.regex.Pattern;

public final class HtmlUtils {

	private static final String TAG_BR = "<br />"; //$NON-NLS-1$

	private static final Pattern REGEX_LINEBREAK = Pattern.compile("\r?\n"); //$NON-NLS-1$

	private HtmlUtils() {
		super();
	}

	public static String nl2br(final String text) {
		if (text == null) {
			return null;
		}
		return REGEX_LINEBREAK.matcher(text).replaceAll(TAG_BR);
	}

}
