package bpmn.element;

import java.awt.Point;

import javax.swing.Icon;

public class DataObjectReference extends FlowElement {

	private static final long serialVersionUID = 1L;

	private boolean isCollection;

	public DataObjectReference(final String id, final String name) {
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
			final Icon icon = getVisualConfig().getIcon(VisualConfig.ICON_COLLECTION);
			final Point position = bounds.getCenterBottom();
			position.translate(
					-icon.getIconWidth() / 2,
					-icon.getIconHeight());
			g.drawIcon(icon, position);
		}
	}

}
