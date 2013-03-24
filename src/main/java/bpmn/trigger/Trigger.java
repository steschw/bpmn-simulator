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
package bpmn.trigger;

import java.awt.Color;

import bpmn.instance.Instance;

public class Trigger
		implements Comparable<Trigger> {

	public static final Color HIGHLIGHT_COLOR = new Color(128, 32, 32);

	private final long time;

	private final Instance sourceInstance;

	private final Instance destinationInstance;

	public Trigger(final Instance sourceInstance, final Instance destinationInstance) {
		super();
		time = System.currentTimeMillis();
		this.sourceInstance = sourceInstance;
		this.destinationInstance = destinationInstance;
	}

	public long getTime() {
		return time;
	}

	public Instance getSourceInstance() {
		return sourceInstance;
	}

	public Instance getDestinationInstance() {
		return destinationInstance;
	}

	@Override
	public int compareTo(final Trigger trigger) {
		assert getDestinationInstance() == trigger.getDestinationInstance();
		return (int)(getTime() - trigger.getTime());
	}

	@Override
	public int hashCode() {
		return (int)getTime();
	}


}
