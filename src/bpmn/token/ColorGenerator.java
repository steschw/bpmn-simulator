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
package bpmn.token;

import java.awt.Color;

public class ColorGenerator {

	private static final Color[] COLORS = new Color[] {
			new Color(0xff5c26),
			new Color(0x2626ff),
			new Color(0xffff26),
			new Color(0xc926ff),
			new Color(0x5cff26),
			new Color(0x26ff93),
			new Color(0xff2692),
			new Color(0x26c9ff)
		};

	private int index = 0;

	public Color next() {
		if (index >= COLORS.length) {
			index = 0;
		}
		return COLORS[index++];
	}

}
