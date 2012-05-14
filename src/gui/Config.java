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
import java.util.Locale;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import bpmn.element.VisibleElement;
import bpmn.element.VisualConfig;

public class Config {

	private static final String NODE = "bpmnsimulator";

	private static final String LANGUAGE = "language";

	private static final String SHOW_EXCLUSIVEGATEWAYSYMBOL = "showExclusiveGatewaySymbol"; //$NON-NLS-1$
	private static final String ANTIALIASING = "antialiasing"; //$NON-NLS-1$

	private static final String IGNORE_COLORS = "ignoreColors";

	private static final String BACKGROUND_COLOR = "backgroundColor";

	private static final Color DEFAULT_BACKGROUND = Color.WHITE;

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

	protected static Preferences getElementNode(final VisualConfig.Element element) {
		return getRootNode().node(element.name().toLowerCase());
	}

	public void setVisualConfig(final VisualConfig visualConfig) {
		final Preferences preferences = getRootNode();
		preferences.putBoolean(ANTIALIASING, visualConfig.isAntialiasing());
		preferences.putBoolean(IGNORE_COLORS, visualConfig.getIgnoreColors());
		preferences.putBoolean(SHOW_EXCLUSIVEGATEWAYSYMBOL, visualConfig.getShowExclusiveGatewaySymbol());

		for (VisualConfig.Element element : VisualConfig.Element.values()) {
			final Preferences elementPreferences = getElementNode(element); 
			final Color color = visualConfig.getBackground(element);
			setBackground(elementPreferences, color);
		}
	}

	public VisualConfig getVisualConfig() {
		final VisualConfig visualConfig = new VisualConfig();

		final Preferences preferences = getRootNode();
		visualConfig.setAntialiasing(preferences.getBoolean(ANTIALIASING, true));
		visualConfig.setIgnoreColors(preferences.getBoolean(IGNORE_COLORS, false));
		visualConfig.setShowExclusiveGatewaySymbol(preferences.getBoolean(SHOW_EXCLUSIVEGATEWAYSYMBOL, true));

		for (VisualConfig.Element element : VisualConfig.Element.values()) {
			final Color color = getBackground(getElementNode(element));
			visualConfig.setBackground(element, color);
		}

		return visualConfig;
	}

	protected static Color getBackground(final Preferences preferences) {
		return new Color(preferences.getInt(BACKGROUND_COLOR, DEFAULT_BACKGROUND.getRGB()));
	}

	protected static void setBackground(final Preferences preferences,
			final Color value) {
		preferences.putInt(BACKGROUND_COLOR, value.getRGB());
	}

	public void setLocale(final Locale locale) {
		if (locale == null) {
			getRootNode().remove(LANGUAGE);
		} else {
			Locale.setDefault(locale);
			getRootNode().put(LANGUAGE, locale.toString());
		}
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
		return getLocaleFromString(getRootNode().get(LANGUAGE, ""));
	}

	public String getLastDirectory() {
		return getRootNode().get(LAST_DIRECTORY, System.getProperty("user.home"));
	}

	public void setLastDirectory(final String directory) {
		getRootNode().put(LAST_DIRECTORY, directory);
	}

	public void load() {
		setLocale(getLocale());
		VisibleElement.setDefaultVisualConfig(getVisualConfig());
	}

	public void store() {
		try {
			getRootNode().flush();
		} catch (BackingStoreException e) {
			e.printStackTrace();
		}
	}

}
