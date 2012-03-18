package bpmn.element;

public abstract class Activity extends TokenFlowElement implements ElementWithDefaultSequenceFlow {

	private static final long serialVersionUID = 1L;

	private ElementRef<SequenceFlow> defaultSequenceFlowRef = null;

	public Activity(final String id, final String name) {
		super(id, name);
	}

	public void setDefaultSequenceFlowRef(final ElementRef<SequenceFlow> sequenceFlowRef) {
		defaultSequenceFlowRef = sequenceFlowRef;
	}

	public ElementRef<SequenceFlow> getDefaultElementFlowRef() {
		return defaultSequenceFlowRef;
	}

}
