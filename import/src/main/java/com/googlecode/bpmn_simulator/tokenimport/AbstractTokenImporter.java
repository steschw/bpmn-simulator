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
package com.googlecode.bpmn_simulator.tokenimport;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.googlecode.bpmn_simulator.animation.token.RootInstances;
import com.googlecode.bpmn_simulator.bpmn.model.core.infrastructure.Definitions;

public abstract class AbstractTokenImporter
		implements TokenImporter {

	protected static final Logger LOG = LogManager.getLogger(LOGGER_NAME);

	private Definitions<?> definitions;

	private RootInstances instances;

	protected RootInstances getInstances() {
		return instances;
	}

	@Override
	public void setInstances(final RootInstances instances) {
		this.instances = instances;
	}

	protected Definitions<?> getDefinitions() {
		return definitions;
	}

	@Override
	public void setDefinition(final Definitions<?> definitions) {
		this.definitions = definitions;
	}

	@Override
	public boolean configure()
			throws TokenImportException {
		return true;
	}

	@Override
	public void importTokens()
			throws TokenImportException {
		if (instances == null) {
			throw new TokenImportException("instances not set");
		}
		if (definitions == null) {
			throw new TokenImportException("definition not set");
		}
	}

}