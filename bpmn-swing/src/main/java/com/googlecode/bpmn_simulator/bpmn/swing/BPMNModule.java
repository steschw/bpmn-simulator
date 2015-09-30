package com.googlecode.bpmn_simulator.bpmn.swing;

import java.util.Arrays;
import java.util.Collection;

import com.googlecode.bpmn_simulator.animation.input.Definition;
import com.googlecode.bpmn_simulator.animation.module.AbstractModule;
import com.googlecode.bpmn_simulator.animation.module.ModuleRegistry;
import com.googlecode.bpmn_simulator.bpmn.model.core.common.artifacts.Association;
import com.googlecode.bpmn_simulator.bpmn.model.core.common.artifacts.Group;
import com.googlecode.bpmn_simulator.bpmn.model.core.common.artifacts.TextAnnotation;
import com.googlecode.bpmn_simulator.bpmn.model.core.common.events.BoundaryEvent;
import com.googlecode.bpmn_simulator.bpmn.model.core.common.events.EndEvent;
import com.googlecode.bpmn_simulator.bpmn.model.core.common.events.IntermediateCatchEvent;
import com.googlecode.bpmn_simulator.bpmn.model.core.common.events.IntermediateThrowEvent;
import com.googlecode.bpmn_simulator.bpmn.model.core.common.events.StartEvent;
import com.googlecode.bpmn_simulator.bpmn.model.process.Lane;
import com.googlecode.bpmn_simulator.bpmn.swing.di.SwingDIDefinition;
import com.googlecode.bpmn_simulator.bpmn.swing.model.core.common.artifacts.AssociationEdge;
import com.googlecode.bpmn_simulator.bpmn.swing.model.core.common.artifacts.GroupShape;
import com.googlecode.bpmn_simulator.bpmn.swing.model.core.common.artifacts.TextAnnotationShape;
import com.googlecode.bpmn_simulator.bpmn.swing.model.core.common.events.BoundaryEventShape;
import com.googlecode.bpmn_simulator.bpmn.swing.model.core.common.events.EndEventShape;
import com.googlecode.bpmn_simulator.bpmn.swing.model.core.common.events.IntermediateCatchEventShape;
import com.googlecode.bpmn_simulator.bpmn.swing.model.core.common.events.IntermediateThrowEventShape;
import com.googlecode.bpmn_simulator.bpmn.swing.model.core.common.events.StartEventShape;
import com.googlecode.bpmn_simulator.bpmn.swing.model.process.LaneShape;

public class BPMNModule
		extends AbstractModule {

	private static final String NAME = "bpmn-swing"; //$NON-NLS-1$
	private static final String DESCRIPTION = "Business Process Model and Notation (BPMN) 2.0";

	private static final String FILE_DESCRIPTION = "BPMN 2.0 XML"; //$NON-NLS-1$
	private static final String[] FILE_EXTENSIONS = { "bpmn", "xml" }; //$NON-NLS-1$ //$NON-NLS-2$

	static {
		ModuleRegistry.getDefault().registerModule(new BPMNModule());
	}

	private BPMNModule() {
		super();
		addElements();
	}

	private void addElements() {
		addElement(Lane.class, LaneShape.class);
		addElement(Association.class, AssociationEdge.class);
		addElement(Group.class, GroupShape.class);
		addElement(TextAnnotation.class, TextAnnotationShape.class);
		addElement(StartEvent.class, StartEventShape.class);
		addElement(EndEvent.class, EndEventShape.class);
		addElement(BoundaryEvent.class, BoundaryEventShape.class);
		addElement(IntermediateCatchEvent.class, IntermediateCatchEventShape.class);
		addElement(IntermediateThrowEvent.class, IntermediateThrowEventShape.class);
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

	@Override
	public String toString() {
		return getDescription();
	}

}
