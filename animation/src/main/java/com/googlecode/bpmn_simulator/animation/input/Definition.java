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
package com.googlecode.bpmn_simulator.animation.input;

import java.io.InputStream;
import java.util.Collection;

import com.googlecode.bpmn_simulator.animation.element.logical.LogicalElement;
import com.googlecode.bpmn_simulator.animation.element.logical.LogicalFlowElement;
import com.googlecode.bpmn_simulator.animation.element.logical.Model;
import com.googlecode.bpmn_simulator.animation.element.visual.Diagram;

public interface Definition<DIAGRAM extends Diagram<?>>
		extends Model {

	String LOGGER_NAME = "definition";

	Collection<DIAGRAM> getDiagrams();

	Collection<LogicalElement> getElements();

	Collection<LogicalFlowElement> getInstantiatingElements();

	void load(InputStream input);

}
