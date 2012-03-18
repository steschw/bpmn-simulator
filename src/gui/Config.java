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

	private Properties properties = new Properties();

	private static final String SHOW_EXCLUSIVEGATEWAYSYMBOL = "showExclusiveGatewaySymbol";
	private static final String ANTIALIASING = "antialiasing";
	private static final String LAST_DIRECTORY = "lastDirectory";

	private static Config instance = null;

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
			FileInputStream input = new FileInputStream(FILENAME);
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
			FileOutputStream output = new FileOutputStream(FILENAME);
			properties.store(output, "");
			output.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	protected void update() {
		setShowExclusiveGatewaySymbol(getShowExclusiveGatewaySymbol());
		setAntialiasing(getAntialiasing());
	}

	protected boolean getPropertyBoolean(final String name) {
		final String value = properties.getProperty(name);
		boolean b = Boolean.parseBoolean(value);
		return b;
	}

	public void setAntialiasing(final boolean antialiasing) {
		properties.setProperty(ANTIALIASING, Boolean.toString(antialiasing));
		Graphics.setAntialiasing(antialiasing);
	}

	public boolean getAntialiasing() {
		return getPropertyBoolean(ANTIALIASING);
	}

	public void setShowExclusiveGatewaySymbol(final boolean show) {
		properties.setProperty(SHOW_EXCLUSIVEGATEWAYSYMBOL, Boolean.toString(show));
		ExclusiveGateway.setShowSymbol(show);
	}

	public boolean getShowExclusiveGatewaySymbol() {
		return getPropertyBoolean(SHOW_EXCLUSIVEGATEWAYSYMBOL);
	}

	public void setLastDirectory(final String directory) {
		properties.setProperty(LAST_DIRECTORY, directory);
	}

	public String getLastDirectory() {
		final String directory = properties.getProperty(LAST_DIRECTORY);
		if (directory != null) {
			return directory;
		}
		return "";
	}

}
