package bpmn.element;

import java.awt.Point;


public class TextAnnotation extends Artifact {

	private static final long serialVersionUID = 1L;

	private String text = null;

	public TextAnnotation(final String id, final String text) {
		super(id);
		setText(text);
	}

	public final void setText(final String text) {
		this.text = text;
	}

	public final String getText() {
		return text;
	}

	@Override
	public Label createElementLabel() {
		final String text = getText();
		if ((text != null) && !text.isEmpty()) {
			Label label = new Label(text);
			initLabel(label);
			return label;
		}
		return null;
	}

	@Override
	protected void initLabel(Label label) {
		label.setAlignCenter(false);
		Rectangle innerBounds = getInnerBounds();
		innerBounds.shrinkLeft(4);
		label.setBounds(innerBounds);
	}

	@Override
	protected void paintElement(Graphics g) {
		final Rectangle bounds = getElementInnerBounds();
		final int x = (int)bounds.getMinX();
		final int y = (int)bounds.getMinY();
		final int SIZE = 10;
		g.drawLine(new Point(x, y), new Point(x + SIZE, y));
		g.drawLine(new Point(x, y), new Point(x, y + bounds.height));
		g.drawLine(new Point(x, y + bounds.height), new Point(x + SIZE, y + bounds.height));
	}

}
