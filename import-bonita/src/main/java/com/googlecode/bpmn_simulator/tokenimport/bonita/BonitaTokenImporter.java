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
package com.googlecode.bpmn_simulator.tokenimport.bonita;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bonitasoft.engine.api.IdentityAPI;
import org.bonitasoft.engine.api.ProcessAPI;
import org.bonitasoft.engine.api.TenantAPIAccessor;
import org.bonitasoft.engine.bpm.actor.ActorCriterion;
import org.bonitasoft.engine.bpm.actor.ActorInstance;
import org.bonitasoft.engine.bpm.actor.ActorMember;
import org.bonitasoft.engine.bpm.data.DataInstance;
import org.bonitasoft.engine.bpm.flownode.ActivityInstance;
import org.bonitasoft.engine.bpm.flownode.ActivityInstanceNotFoundException;
import org.bonitasoft.engine.bpm.flownode.FlowNodeType;
import org.bonitasoft.engine.bpm.flownode.HumanTaskInstance;
import org.bonitasoft.engine.bpm.process.ProcessDefinition;
import org.bonitasoft.engine.bpm.process.ProcessDefinitionNotFoundException;
import org.bonitasoft.engine.exception.SearchException;
import org.bonitasoft.engine.search.SearchOptions;
import org.bonitasoft.engine.search.SearchOptionsBuilder;
import org.bonitasoft.engine.session.APISession;

import com.googlecode.bpmn_simulator.animation.token.Instance;
import com.googlecode.bpmn_simulator.animation.token.Token;
import com.googlecode.bpmn_simulator.bpmn.model.NamedElement;
import com.googlecode.bpmn_simulator.bpmn.model.core.common.FlowElement;
import com.googlecode.bpmn_simulator.bpmn.model.core.common.FlowElementsContainer;
import com.googlecode.bpmn_simulator.bpmn.model.process.activities.Process;
import com.googlecode.bpmn_simulator.tokenimport.AbstractTokenImporter;
import com.googlecode.bpmn_simulator.tokenimport.TokenImportException;

public class BonitaTokenImporter
		extends AbstractTokenImporter {

	private static final SearchOptions ALL = new SearchOptionsBuilder(0, Integer.MAX_VALUE).done();

	private static final String STATE_FAILED = "failed";

	private APISession session = null;
	private ProcessAPI processAPI = null;
	private IdentityAPI identityAPI = null;

	private final Map<Long, Instance> instances = new HashMap<>();

	private boolean importFailedActivities = true;

	private UserFilter userFilter = UserFilter.NONE;
	private Collection<Long> userIds = new ArrayList<>();
	private Collection<Long> processDefinitionIds = new ArrayList<>();

	@Override
	public void login(final String username, final String password)
			throws TokenImportException {
		logout();
		try {
			session = TenantAPIAccessor.getLoginAPI().login(username, password);
			processAPI = TenantAPIAccessor.getProcessAPI(session);
			identityAPI = TenantAPIAccessor.getIdentityAPI(session);
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
		identityAPI = null;
	}

	@Override
	public boolean configure()
			throws TokenImportException {
		try {
			final ConfigurationDialog dialog = new ConfigurationDialog(
					processAPI.searchProcessDeploymentInfos(ALL).getResult(),
					identityAPI.searchUsers(ALL).getResult());
			dialog.setImportFailed(importFailedActivities);
			final boolean ret = dialog.showDialog();
			if (ret) {
				processDefinitionIds = dialog.getProcessDefinitionIds();
				importFailedActivities = dialog.getImportFailedActivities();
				userFilter = dialog.getUserFilter();
				userIds = dialog.getUserIds();
			}
			dialog.dispose();
			return ret;
		} catch (SearchException e) {
			throw new TokenImportException(e);
		}
	}

	@Override
	public void importTokens()
			throws TokenImportException {
		super.importTokens();
		if ((identityAPI == null) || (processAPI == null)) {
			throw new TokenImportException("api not set");
		}
		try {
			importActivities();
		} catch (Exception e) {
			throw new TokenImportException(e);
		}
	}

	private void importActivities()
			throws SearchException, ActivityInstanceNotFoundException {
		for (final ActivityInstance activityInstance : processAPI.searchActivities(ALL).getResult()) {
			if (isFilteredByProcessDefinition(activityInstance)) {
				LOG.info(MessageFormat.format("Ignoring activity instance {0} (processdefinition)",
						activityInstance));
				continue;
			}
			if (isFilteredByState(activityInstance)) {
				LOG.info(MessageFormat.format("Ignoring activity instance {0} (failed)",
						activityInstance));
				continue;
			}
			if (isFilteredByUser(activityInstance)) {
				LOG.info(MessageFormat.format("Ignoring activity instance {0} (user)",
						activityInstance));
				continue;
			}
			importActivityInstance(activityInstance);
		}
	}

	private boolean isFilteredByProcessDefinition(final ActivityInstance activityInstance) {
		return !processDefinitionIds.contains(Long.valueOf(activityInstance.getProcessDefinitionId()));
	}

	private boolean isFilteredByState(final ActivityInstance activityInstance) {
		return !importFailedActivities && STATE_FAILED.equals(activityInstance.getState());
	}

	private boolean actorContainsAnyUserOf(final long actorId, final Collection<Long> userIds) {
		final List<ActorMember> members = processAPI.getActorMembers(actorId, 0, Integer.MAX_VALUE);
		for (final ActorMember member : members) {
			if (member.getUserId() != -1) {
				if (userIds.contains(member.getUserId())) {
					return true;
				}
			}
		}
		return false;
	}

	private boolean processDefinitionContainsAnyUserOf(final long processDefinitionId, final Collection<Long> userIds) {
		for (final ActorInstance actorInstance : processAPI.getActors(processDefinitionId, 0, Integer.MAX_VALUE, ActorCriterion.NAME_ASC)) {
			if (actorContainsAnyUserOf(actorInstance.getId(), userIds)) {
				return true;
			}
		}
		return false;
	}

	private boolean isFilteredByUser(final ActivityInstance activityInstance)
			throws ActivityInstanceNotFoundException {
		switch (userFilter) {
			case EXECUTED_BY:
				return !userIds.contains(activityInstance.getExecutedBy());
			case ACTOR:
				return !processDefinitionContainsAnyUserOf(activityInstance.getProcessDefinitionId(), userIds);
			case ASSIGNED_TO:
				if (activityInstance.getType() == FlowNodeType.HUMAN_TASK) {
					final HumanTaskInstance humanTaskInstance = processAPI.getHumanTaskInstance(activityInstance.getId());
					return !userIds.contains(humanTaskInstance.getAssigneeId());
				}
				return false;
			default:
		}
		return false;
	}

	private static <T extends NamedElement> T findByName(final String name, final Collection<T> elements) {
		for (final T element : elements) {
			if (name.equals(element.getName())) {
				return element;
			}
		}
		return null;
	}

	private static FlowElement findFlowElementInContainer(final FlowElementsContainer container, final String name) {
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

	private Instance addInstance(final long processInstanceId) {
		final Instance instance;
		if (instances.containsKey(processInstanceId)) {
			instance = instances.get(processInstanceId);
		} else {
			instance = getInstances().addNewChildInstance();
			instances.put(processInstanceId, instance);
			instance.setData(getProcessInstanceData(processInstanceId));
		}
		return instance;
	}

	private void addToken(final long rootProcessInstanceId, final long activityInstanceId, final FlowElement flowElement) {
		final Token token = addInstance(rootProcessInstanceId).createNewToken(flowElement);
		token.setData(getActivityInstanceData(activityInstanceId));
	}

	private Map<Object, Object> getActivityInstanceData(final long activityInstanceId) {
		final Map<Object, Object> data = new HashMap<>();
		for (final DataInstance dataInstance : processAPI.getActivityDataInstances(activityInstanceId, 0, Integer.MAX_VALUE)) {
			data.put(dataInstance.getName(), dataInstance.getValue());
		}
		return data;
	}

	private Map<Object, Object> getProcessInstanceData(final long processInstanceId) {
		final Map<Object, Object> data = new HashMap<>();
		for (final DataInstance dataInstance : processAPI.getProcessDataInstances(processInstanceId, 0, Integer.MAX_VALUE)) {
			data.put(dataInstance.getName(), dataInstance.getValue());
		}
		return data;
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
			LOG.debug(MessageFormat.format("Instance of {0} is in state {1}/{2} of {3}",
					activityInstance.getType(), activityInstance.getStateCategory(), activityInstance.getState(),
					processAPI.getSupportedStates(activityInstance.getType())));
			LOG.info(MessageFormat.format("Importing activity instance ''{0}'' ({1}) from process ''{2}''",
					activityInstance.getName(), activityInstance.getType(), processName));
			final Process process = findProcessByName(processName);
			if (process == null) {
				LOG.debug(MessageFormat.format("Process ''{0}'' not found in definition", processName));
				return;
			}
			final FlowElement flowElement = findFlowElementInContainer(process, activityInstance.getName());
			if (flowElement == null) {
				LOG.error(MessageFormat.format("Flow element ''{0}'' not found in process ''{1}''",
						activityInstance.getName(), processName));
				return;
			}
			addToken(activityInstance.getRootContainerId(), activityInstance.getId(), flowElement);
		} catch (ProcessDefinitionNotFoundException e) {
			LOG.catching(e);
		}
	}

}
