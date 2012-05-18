package bpmn.element.event.definition;

import javax.swing.Icon;

import bpmn.element.ElementRef;
import bpmn.element.Signal;
import bpmn.element.VisualConfig;

public class SignalEventDefinition implements EventDefinition {

	private final ElementRef<Signal> signalRef;

	public SignalEventDefinition(ElementRef<Signal> signalRef) {
		super();
		this.signalRef = signalRef; 
	}

	public final ElementRef<Signal> getSignalRef() {
		return signalRef;
	}

	@Override
	public Icon getIcon(final VisualConfig visualConfig, final boolean inverse) {
		return visualConfig.getIcon(inverse
				? VisualConfig.ICON_SIGNAL_INVERSE
				: VisualConfig.ICON_SIGNAL);
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj instanceof SignalEventDefinition) {
			final SignalEventDefinition definition = (SignalEventDefinition)obj;
			return super.equals(obj)
					|| (getSignalRef().equals(definition.getSignalRef()));
		}
		return false;
	}

}
