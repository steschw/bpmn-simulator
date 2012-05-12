package bpmn.element.gateway;

import bpmn.element.Graphics;
import bpmn.element.Rectangle;

@SuppressWarnings("serial")
public class EventBasedGateway extends Gateway {

	public EventBasedGateway(final String id, final String name) {
		super(id, name);
	}

	@Override
	protected void paintElement(final Graphics g) {
		super.paintElement(g);

		final Rectangle bounds = getElementInnerBounds();
		bounds.shrinkHalf();
		g.drawOval(bounds);
		bounds.shrink(2, 2, 2, 2);
		g.drawOval(bounds);
		bounds.shrink(2, 2, 2, 2);
		g.drawPentagon(bounds);
	}

}
