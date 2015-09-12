package com.googlecode.bpmn_simulator.gui.dialogs;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileSystemView;

import com.googlecode.bpmn_simulator.animation.module.Module;
import com.googlecode.bpmn_simulator.animation.module.ModuleRegistry;

@SuppressWarnings("serial")
public class DefinitionFileChooser
		extends JFileChooser {

	@Override
	protected void setup(final FileSystemView view) {
		super.setup(view);

		for (final Module module : ModuleRegistry.getDefault().getModules()) {
			addChoosableFileFilter(new ModuleFileFilter(module));
		}
		setAcceptAllFileFilterUsed(false);
	}

	public Module getModule() {
		return ((ModuleFileFilter) getFileFilter()).getModule();
	}

}
