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
package com.googlecode.bpmn_simulator.framework.exception;

import com.googlecode.bpmn_simulator.framework.element.Model;

@SuppressWarnings("serial")
public class SimulationException
		extends Exception {

	private final Model model;

	public SimulationException(final Model model) {
		super();
		this.model = model;
	}

	public SimulationException(final Model model, final String message) {
		super(message);
		this.model = model;
	}

	public SimulationException(final Model model, final Throwable cause) {
		super(cause);
		this.model = model;
	}

	public SimulationException(final Model model, final String message,
			final Throwable cause) {
		super(message, cause);
		this.model = model;
	}

	public final Model getModel() {
		return model;
	}

}
