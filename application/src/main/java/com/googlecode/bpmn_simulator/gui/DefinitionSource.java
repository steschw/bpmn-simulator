package com.googlecode.bpmn_simulator.gui;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import com.googlecode.bpmn_simulator.animation.module.Module;

public class DefinitionSource {

	private File file = null;
	private InputStream inputStream = null;

	private Module module = null;

	private DefinitionSource(final Module module) {
		super();
		this.module = module;
	}

	public DefinitionSource(final File file, final Module module) {
		this(module);
		if (file == null) {
			throw new IllegalArgumentException();
		}
		this.file = file;
	}

	public DefinitionSource(final InputStream inputStream, final Module module) {
		this(module);
		if (inputStream == null) {
			throw new IllegalArgumentException();
		}
		this.inputStream = inputStream;
	}

	public Module getModule() {
		return module;
	}

	public File getFile() {
		return file;
	}

	public static File getFile(final DefinitionSource source) {
		return ((source != null) && source.isFile()) ? source.getFile() : null;
	}

	public boolean isFile() {
		return getFile() != null;
	}

	public boolean isClipboard() {
		return !isFile();
	}

	public String getName() {
		if (isFile()) {
			return getFile().getAbsolutePath();
		} else if (isClipboard()) {
			return "Clipboard";
		}
		return null;
	}

	public InputStream getStream()
			throws IOException {
		if (isClipboard()) {
			return inputStream;
		} else if (isFile()) {
			return new FileInputStream(file);
		}
		return null;
	}

}
