package bpmn.element;

public class Signal extends InvisibleElement {

	private final String name;

	public Signal(final String id, final String name) {
		super(id);
		this.name = name;
	}

	public String getName() {
		return name;
	}

}
