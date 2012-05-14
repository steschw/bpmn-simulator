package bpmn.element;

public interface Element {

	String getId();

	void setDocumentation(final Documentation documentation);

	boolean hasDocumentation();

	Documentation getDocumentation();

}
