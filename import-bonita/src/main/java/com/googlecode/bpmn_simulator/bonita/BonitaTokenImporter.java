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
package com.googlecode.bpmn_simulator.bonita;

import java.text.MessageFormat;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.bonitasoft.engine.api.ProcessAPI;
import org.bonitasoft.engine.api.TenantAPIAccessor;
import org.bonitasoft.engine.bpm.flownode.ActivityInstance;
import org.bonitasoft.engine.bpm.process.ProcessDefinition;
import org.bonitasoft.engine.bpm.process.ProcessDefinitionNotFoundException;
import org.bonitasoft.engine.exception.SearchException;
import org.bonitasoft.engine.search.SearchOptions;
import org.bonitasoft.engine.search.SearchOptionsBuilder;
import org.bonitasoft.engine.session.APISession;

import com.googlecode.bpmn_simulator.animation.token.Instance;
import com.googlecode.bpmn_simulator.bpmn.model.NamedElement;
import com.googlecode.bpmn_simulator.bpmn.model.core.common.FlowElement;
import com.googlecode.bpmn_simulator.bpmn.model.core.common.FlowElementsContainer;
import com.googlecode.bpmn_simulator.bpmn.model.process.activities.Process;

public class BonitaTokenImporter
		extends AbstractTokenImporter {

	private static final SearchOptions ALL = new SearchOptionsBuilder(0, Integer.MAX_VALUE).done();

	private final Map<Long, Instance> instances = new HashMap<>();

	private APISession session = null;
	private ProcessAPI processAPI = null; 

	@Override
	public void login(final String username, final String password)
			throws TokenImportException {
		logout();
		try {
			session = TenantAPIAccessor.getLoginAPI().login(username, password);
			processAPI = TenantAPIAccessor.getProcessAPI(session);
		} catch (Exception e) {
			throw new TokenImportException(e);
		}
	}

	@Override
	public void logout()
			throws TokenImportException {
		if (session != null) {
			try {
				TenantAPIAccessor.getLoginAPI().logout(session);
			} catch (Exception e) {
				throw new TokenImportException(e);
			}
			session = null;
		}
		processAPI = null;
	}

	@Override
	public void importTokens()
			throws TokenImportException {
		super.importTokens();
		if (processAPI == null) {
			throw new TokenImportException("api not set");
		}
		try {
			importActivities();
		} catch (Exception e) {
			throw new TokenImportException(e);
		}
	}

	private void importActivities()
			throws SearchException {
		for (final ActivityInstance activityInstance : processAPI.searchActivities(ALL).getResult()) {
			importActivityInstance(activityInstance);
		}
	}

	private static <T extends NamedElement> T findByName(final String name, final Collection<T> elements) {
		for (final T element : elements) {
			if (name.equals(element.getName())) {
				return element;
			}
		}
		return null;
	}

	private FlowElement findFlowElementInContainer(final String name, final FlowElementsContainer container) {
		for (final FlowElement flowElement : container.getFlowElements()) {
			if (name.equals(flowElement.getName())) {
				return flowElement;
			}
		}
		return null;
	}

	private Process findProcessByName(final String name) {
		return findByName(name, getDefinitions().getProcesses());
	}

	private boolean isLatestProcessDefinition(final ProcessDefinition processDefinition)
			throws ProcessDefinitionNotFoundException {
		return (processAPI.getLatestProcessDefinitionId(processDefinition.getName()) == processDefinition.getId());
	}

	private void addToken(final long rootProcessInstanceId, final FlowElement flowElement) {
		final Instance instance;
		if (instances.containsKey(rootProcessInstanceId)) {
			instance = instances.get(rootProcessInstanceId);
		} else {
			instance = getInstances().addNewChildInstance();
			instances.put(rootProcessInstanceId, instance);
		}
		instance.createNewToken(flowElement);
	}

	private void importActivityInstance(final ActivityInstance activityInstance) {
		try {
			final ProcessDefinition processDefinition = processAPI.getProcessDefinition(activityInstance.getProcessDefinitionId());
			final String processName = processDefinition.getName();
			if (!isLatestProcessDefinition(processDefinition)) {
				LOG.warn(MessageFormat.format("Activity instance ''{0}'' is from a previous version ({1}) of process ''{2}'' and will be ignored",
						activityInstance.getName(), processDefinition.getVersion(), processName));
				return;
			}
			LOG.info(MessageFormat.format("Importing activity instance ''{0}'' ({1}) for process ''{2}''",
					activityInstance.getName(), activityInstance.getType(), processName));
			final Process process = findProcessByName(processName);
			if (process == null) {
				LOG.debug(MessageFormat.format("Process ''{0}'' not found in definition", processName));
				return;
			}
			final FlowElement flowElement = findFlowElementInContainer(activityInstance.getName(), process);
			if (flowElement == null) {
				LOG.error(MessageFormat.format("Flow element ''{0}'' not found in process ''{1}''",
						activityInstance.getName(), processName));
				return;
			}
			addToken(activityInstance.getRootContainerId(), flowElement);
		} catch (ProcessDefinitionNotFoundException e) {
			LOG.catching(e);
		}
	}

}
