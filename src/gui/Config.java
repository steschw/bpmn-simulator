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

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import bpmn.element.Graphics;
import bpmn.element.gateway.ExclusiveGateway;

public class Config {

	private static final String FILENAME = "config";

	private final Properties properties = new Properties();

	private static final String SHOW_EXCLUSIVEGATEWAYSYMBOL = "showExclusiveGatewaySymbol";
	private static final String ANTIALIASING = "antialiasing";
	private static final String LAST_DIRECTORY = "lastDirectory";

	private static Config instance;

	public Config() {
		super();
		defaults();
	}

	public static synchronized Config getInstance() {
		if (instance == null) {
			instance = new Config();
		}
		return instance;
	}

	protected void defaults() {
		setShowExclusiveGatewaySymbol(true);
		setAntialiasing(true);
	}

	public void load() {
		try {
			final FileInputStream input = new FileInputStream(FILENAME);
			properties.load(input);
			input.close();
			update();
		} catch (FileNotFoundException e) {
			defaults();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void store() {
		try {
			final FileOutputStream output = new FileOutputStream(FILENAME);
			properties.store(output, "");
			output.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	protected void update() {
		setShowExclusiveGatewaySymbol(isShowExclusiveGatewaySymbol());
		setAntialiasing(isAntialiasing());
	}

	protected boolean getPropertyBoolean(final String name) {
		final String value = properties.getProperty(name);
		return Boolean.parseBoolean(value);
	}

	public void setAntialiasing(final boolean antialiasing) {
		properties.setProperty(ANTIALIASING, Boolean.toString(antialiasing));
		Graphics.setAntialiasing(antialiasing);
	}

	public boolean isAntialiasing() {
		return getPropertyBoolean(ANTIALIASING);
	}

	public void setShowExclusiveGatewaySymbol(final boolean show) {
		properties.setProperty(SHOW_EXCLUSIVEGATEWAYSYMBOL, Boolean.toString(show));
		ExclusiveGateway.setShowSymbol(show);
	}

	public boolean isShowExclusiveGatewaySymbol() {
		return getPropertyBoolean(SHOW_EXCLUSIVEGATEWAYSYMBOL);
	}

	public void setLastDirectory(final String directory) {
		properties.setProperty(LAST_DIRECTORY, directory);
	}

	public String getLastDirectory() {
		final String directory = properties.getProperty(LAST_DIRECTORY);
		return (directory == null) ? "" : directory;
	}

}
