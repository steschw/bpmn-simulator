package com.googlecode.bpmn_simulator.gui;

import java.io.File;

import javax.swing.filechooser.FileFilter;

import org.apache.commons.io.FilenameUtils;

import com.googlecode.bpmn_simulator.animation.module.Module;

class ModuleFileFilter extends FileFilter {

	private final Module module;

	public ModuleFileFilter(final Module module) {
		super();
		this.module = module;
	}

	public Module getModule() {
		return module;
	}

	@Override
	public boolean accept(final File file) {
		if (file != null) {
			if (file.isDirectory()) {
				return true;
			}
			return FilenameUtils.isExtension(file.getName(), module.getFileExtensions());
		}
		return false;
	}

	@Override
	public String getDescription() {
		return module.getFileDescription();
	}
	
}