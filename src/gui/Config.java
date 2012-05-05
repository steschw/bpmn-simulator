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
package gui;

import java.awt.Color;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import bpmn.Model;
import bpmn.element.Graphics;
import bpmn.element.activity.task.Task;
import bpmn.element.event.EndEvent;
import bpmn.element.event.IntermediateEvent;
import bpmn.element.event.StartEvent;
import bpmn.element.gateway.ExclusiveGateway;
import bpmn.element.gateway.Gateway;

public class Config {

	private static final String NODE = "bpmnsimulator";

	private static final String SHOW_EXCLUSIVEGATEWAYSYMBOL = "showExclusiveGatewaySymbol"; //$NON-NLS-1$
	private static final String ANTIALIASING = "antialiasing"; //$NON-NLS-1$

	private static final String IGNORE_COLORS = "ignoreColors";

	private static final Color DEFAULT_BACKGROUND = Color.WHITE;

	private static final String STARTEVENT_BACKGROUND = "startEventBackground";
	private static final String INTERMEDIATEEVENT_BACKGROUND = "intermediateEventBackground";
	private static final String ENDEVENT_BACKGROUND = "endEventBackground";
	private static final String GATEWAY_BACKGROUND = "gatewayBackground";
	private static final String TASK_BACKGROUND = "taskBackground";

	private static final String LAST_DIRECTORY = "lastDirectory"; //$NON-NLS-1$

	private static Config instance;

	protected Config() {
		super();
	}

	public static synchronized Config getInstance() {
		if (instance == null) {
			instance = new Config();
		}
		return instance;
	}

	protected static Preferences getRootNode() {
		return Preferences.userRoot().node(NODE);
	}

	protected static Color getBackground(final Preferences preferences, final String key) {
		return new Color(preferences.getInt(key, DEFAULT_BACKGROUND.getRGB()));
	}

	protected static void putColor(final Preferences preferences, final String key,
			final Color value) {
		preferences.putInt(key, value.getRGB());
	}

	public void load() {
		final Preferences preferences = getRootNode();
		Graphics.setAntialiasing(preferences.getBoolean(ANTIALIASING, true));
		ExclusiveGateway.setShowSymbol(preferences.getBoolean(SHOW_EXCLUSIVEGATEWAYSYMBOL, true));
		Model.setIgnoreColors(preferences.getBoolean(IGNORE_COLORS, false));
		StartEvent.setDefaultBackground(getBackground(preferences, STARTEVENT_BACKGROUND));
		IntermediateEvent.setDefaultBackground(getBackground(preferences, INTERMEDIATEEVENT_BACKGROUND));
		EndEvent.setDefaultBackground(getBackground(preferences, ENDEVENT_BACKGROUND));
		Gateway.setDefaultBackground(getBackground(preferences, GATEWAY_BACKGROUND));
		Task.setDefaultBackground(getBackground(preferences, TASK_BACKGROUND));
	}

	public void store() {
		final Preferences preferences = getRootNode();
		preferences.putBoolean(ANTIALIASING, Graphics.isAntialiasing());
		preferences.putBoolean(SHOW_EXCLUSIVEGATEWAYSYMBOL, ExclusiveGateway.getShowSymbol());
		preferences.putBoolean(IGNORE_COLORS, Model.getIgnoreColors());
		putColor(preferences, STARTEVENT_BACKGROUND, StartEvent.getDefaultBackground());
		putColor(preferences, INTERMEDIATEEVENT_BACKGROUND, IntermediateEvent.getDefaultBackground());
		putColor(preferences, ENDEVENT_BACKGROUND, EndEvent.getDefaultBackground());
		putColor(preferences, GATEWAY_BACKGROUND, Gateway.getDefaultBackground());
		putColor(preferences, TASK_BACKGROUND, Task.getDefaultBackground());
		try {
			preferences.flush();
		} catch (BackingStoreException e) {
			e.printStackTrace();
		}
	}

	public String getLastDirectory() {
		return getRootNode().get(LAST_DIRECTORY, System.getProperty("user.home"));
	}

	public void setLastDirectory(final String directory) {
		getRootNode().put(LAST_DIRECTORY, directory);
	}

}
