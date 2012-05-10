package bpmn.element;

public class Documentation {

	private String text;

	public Documentation(final String text) {
		super();
		setText(text);
	}

	public void setText(final String text) {
		assert (text != null) && !text.isEmpty();
		this.text = text;
	}

	public final String getText() {
		return text;
	}

	public String toHtml() {
		return getText().replaceAll("\n", "<br />");
	}

}
