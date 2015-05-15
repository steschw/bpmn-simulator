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
package com.googlecode.bpmn_simulator.animation.token;

public class RootInstances
		extends InstanceContainer {

	private static final int[] COLORS = {
			0xff5c26,
			0x2626ff,
			0xffff26,
			0xc926ff,
			0x5cff26,
			0x26ff93,
			0xff2692,
			0x26c9ff,
	};

	private int colorIndex;

	private synchronized int nextColor() {
		if (colorIndex >= COLORS.length) {
			colorIndex = 0;
		}
		return COLORS[colorIndex++];
	}

	@Override
	protected Instance createNewChildInstance() {
		return new Instance(nextColor());
	}

}
