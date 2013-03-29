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
package com.google.code.bpmn_simulator.bpmn.model.core.common;

import com.google.code.bpmn_simulator.bpmn.model.Model;
import com.google.code.bpmn_simulator.bpmn.model.core.foundation.BaseElement;
import com.google.code.bpmn_simulator.bpmn.model.core.foundation.Documentation;

public class InvisibleElement
		implements BaseElement {

	private final Model model;

	private final String id;

	private Documentation documentation;

	public InvisibleElement(final Model model, final String id) {
		super();
		this.model = model;
		this.id = id;
	}

	@Override
	public Model getModel() {
		return model;
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public void setDocumentation(final Documentation documentation) {
		this.documentation = documentation;
	}

	@Override
	public boolean hasDocumentation() {
		return getDocumentation() != null;
	}

	@Override
	public Documentation getDocumentation() {
		return documentation;
	}

	@Override
	public String getFullName() {
		return getId();
	}

}
