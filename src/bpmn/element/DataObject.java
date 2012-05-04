package bpmn.element;

import java.awt.Point;

import javax.swing.Icon;

public class DataObject extends FlowElement {

	private static final long serialVersionUID = 1L;

	private static final Icon COLLECTION_ICON = Graphics.loadIcon("collection.png");

	private boolean isCollection;

	public DataObject(final String id, final String name) {
		super(id, name);
	}

	public void setCollection(final boolean isCollection) {
		this.isCollection = isCollection;
	}

	public boolean isCollection() {
		return isCollection;
	}

	@Override
	protected void paintBackground(final Graphics g) {
		super.paintBackground(g);

		g.fill(Graphics.createDataObjectShape(getElementInnerBounds()));
	}

	@Override
	protected void paintElement(final Graphics g) {
		final Rectangle bounds = getElementInnerBounds();
		g.drawDataObject(bounds);

		if (isCollection()) {
			final Point position = bounds.getCenterBottom();
			position.translate(
					-COLLECTION_ICON.getIconWidth() / 2,
					-COLLECTION_ICON.getIconHeight());
			g.drawIcon(COLLECTION_ICON, position);
		}
	}

}
