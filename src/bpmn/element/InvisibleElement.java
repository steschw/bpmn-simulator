package bpmn.element;

public class InvisibleElement implements Element {

	private final String id;

	private Documentation documentation;

	public InvisibleElement(final String id) {
		super();
		this.id = id;
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public void setDocumentation(final Documentation documentation) {
		this.documentation = documentation;
	}

	@Override
	public boolean hasDocumentation() {
		return getDocumentation() != null;
	}

	@Override
	public Documentation getDocumentation() {
		return documentation;
	}

}
