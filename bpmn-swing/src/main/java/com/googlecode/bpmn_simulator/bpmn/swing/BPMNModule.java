package com.googlecode.bpmn_simulator.bpmn.swing;

import java.util.Arrays;
import java.util.Collection;

import com.googlecode.bpmn_simulator.animation.input.Definition;
import com.googlecode.bpmn_simulator.animation.module.Module;
import com.googlecode.bpmn_simulator.animation.module.ModuleRegistry;
import com.googlecode.bpmn_simulator.bpmn.swing.di.SwingDIDefinition;

public class BPMNModule
		implements Module {

	private static final String NAME = "bpmn-swing";
	private static final String DESCRIPTION = "Business Process Model and Notation (BPMN) 2.0";

	private static final String FILE_DESCRIPTION = "BPMN 2.0 XML"; //$NON-NLS-1$
	private static final String[] FILE_EXTENSIONS = { "bpmn", "xml" }; //$NON-NLS-1$ //$NON-NLS-2$

	static {
		ModuleRegistry.getDefault().registerModule(new BPMNModule());
	}

	private BPMNModule() {
		super();
	}

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public String getDescription() {
		return DESCRIPTION;
	}

	@Override
	public String getFileDescription() {
		return FILE_DESCRIPTION;
	}

	@Override
	public Collection<String> getFileExtensions() {
		return Arrays.asList(FILE_EXTENSIONS);
	}

	@Override
	public Definition<?> createEmptyDefinition() {
		return new SwingDIDefinition();
	}

}
