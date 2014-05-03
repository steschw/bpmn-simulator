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
package com.googlecode.bpmn_simulator.gui.preferences;

import java.awt.Color;
import java.util.Locale;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import com.googlecode.bpmn_simulator.bpmn.model.core.common.AbstractFlowElement;
import com.googlecode.bpmn_simulator.bpmn.model.core.common.Behavior;
import com.googlecode.bpmn_simulator.bpmn.swing.di.Appearance;


public class Config {

	private static final String NODE = "bpmnsimulator"; //$NON-NLS-1$

	private static final String LANGUAGE = "language"; //$NON-NLS-1$

	private static final String DEFAULT_LANGUAGE = ""; //$NON-NLS-1$

	private static final String KEEP_EVENTS = "keepEvents"; //$NON-NLS-1$

	private static final String SHOW_EXCLUSIVEGATEWAYSYMBOL = "showExclusiveGatewaySymbol"; //$NON-NLS-1$
	private static final String ANTIALIASING = "antialiasing"; //$NON-NLS-1$

	private static final String IGNORE_COLORS = "ignoreColors"; //$NON-NLS-1$

	private static final String BACKGROUND_COLOR = "backgroundColor"; //$NON-NLS-1$

	private static final Color DEFAULT_BACKGROUND = Color.WHITE;

	private static final String EXTERNAL_EDITOR = "externalEditor"; //$NON-NLS-1$

	private static final String DEFAULT_EXTERNAL_EDITOR = ""; //$NON-NLS-1$

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

	protected static Preferences getElementNode(final Appearance.Element element) {
		return getRootNode().node(element.name().toLowerCase());
	}

	public void setVisualization(final Appearance visualization) {
		final Preferences preferences = getRootNode();

		preferences.putBoolean(ANTIALIASING, visualization.isAntialiasing());
		preferences.putBoolean(IGNORE_COLORS, visualization.getIgnoreExplicitColors());
		preferences.putBoolean(SHOW_EXCLUSIVEGATEWAYSYMBOL, visualization.getShowExclusiveGatewaySymbol());

		for (Appearance.Element element : Appearance.Element.values()) {
			final Preferences elementPreferences = getElementNode(element);
			final Color color = visualization.getBackground(element);
			setBackground(elementPreferences, color);
		}
	}

	public Appearance getVisualization() {
		final Appearance visualization = new Appearance();
		final Preferences preferences = getRootNode();

		visualization.setAntialiasing(preferences.getBoolean(ANTIALIASING, true));
		visualization.setIgnoreExplicitColors(preferences.getBoolean(IGNORE_COLORS, false));
		visualization.setShowExclusiveGatewaySymbol(preferences.getBoolean(SHOW_EXCLUSIVEGATEWAYSYMBOL, true));

		for (Appearance.Element element : Appearance.Element.values()) {
			final Color color = getBackground(getElementNode(element));
			visualization.setBackground(element, color);
		}

		return visualization;
	}

	protected static Color getBackground(final Preferences preferences) {
		return new Color(preferences.getInt(BACKGROUND_COLOR, DEFAULT_BACKGROUND.getRGB()));
	}

	protected static void setBackground(final Preferences preferences,
			final Color value) {
		preferences.putInt(BACKGROUND_COLOR, value.getRGB());
	}

	public Behavior getBehavior() {
		final Behavior behavior = new Behavior();
		final Preferences preferences = getRootNode();

		behavior.setKeepTriggers(preferences.getBoolean(KEEP_EVENTS, behavior.getKeepTriggers()));

		return behavior;
	}

	public void setBehavior(final Behavior behavior) {
		final Preferences preferences = getRootNode();

		preferences.putBoolean(KEEP_EVENTS, behavior.getKeepTriggers());
	}

	protected Locale getLocaleFromString(final String string) {
		for (Locale locale : Locale.getAvailableLocales()) {
			if (locale.toString().equals(string)) {
				return locale;
			}
		}
		return null;
	}

	public Locale getLocale() {
		return getLocaleFromString(getRootNode().get(LANGUAGE, DEFAULT_LANGUAGE));
	}

	public void setLocale(final Locale locale) {
		if (locale == null) {
			getRootNode().remove(LANGUAGE);
		} else {
			getRootNode().put(LANGUAGE, locale.toString());
		}
	}

	public String getExternalEditor() {
		return getRootNode().get(EXTERNAL_EDITOR, DEFAULT_EXTERNAL_EDITOR);
	}

	public void setExternalEditor(final String filename) {
		getRootNode().put(EXTERNAL_EDITOR, filename);
	}

	public String getLastDirectory() {
		return getRootNode().get(LAST_DIRECTORY, System.getProperty("user.home")); //$NON-NLS-1$
	}

	public void setLastDirectory(final String directory) {
		getRootNode().put(LAST_DIRECTORY, directory);
	}

	public void load() {
		final Locale locale = getLocale();
		if (locale != null) {
			Locale.setDefault(locale);
		}

		AbstractFlowElement.setDefaultBehavior(getBehavior());

		AbstractFlowElement.setDefaultVisualization(getVisualization());
	}

	public void store() {
		try {
			getRootNode().flush();
		} catch (BackingStoreException e) {
			e.printStackTrace();
		}
	}

}
