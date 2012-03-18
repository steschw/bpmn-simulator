package bpmn.element;

import java.awt.BasicStroke;
import java.awt.Stroke;


public class Group extends Artifact {

	private static final long serialVersionUID = 1L;

	public Group(final String id) {
		super(id);
	}

	@Override
	protected Stroke getStroke() {
		return new BasicStroke(getBorderWidth(),
				BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER, 1.f, new float[] { 8.f, 5.f, 1.f, 5.f }, 0); 
	}

	@Override
	protected void paintElement(Graphics g) {

		g.drawRoundRect(getElementInnerBounds(), 20, 20);
	}

}
