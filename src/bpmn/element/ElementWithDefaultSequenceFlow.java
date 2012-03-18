package bpmn.element;

public interface ElementWithDefaultSequenceFlow {

	public void setDefaultSequenceFlowRef(final ElementRef<SequenceFlow> sequenceFlowRef);

	public ElementRef<SequenceFlow> getDefaultElementFlowRef();

}
