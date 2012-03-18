package bpmn.element;

import java.awt.BasicStroke;
import java.awt.Point;
import java.awt.Stroke;
import java.util.Iterator;


public class MessageFlow extends ConnectingElement {

	private static final long serialVersionUID = 1L;

	public MessageFlow(final String id, ElementRef<FlowElement> source,
			ElementRef<FlowElement> target) {
		super(id, null, source, target);
	}

	@Override
	protected Stroke getStroke() {
		return new BasicStroke(getBorderWidth(),
				BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER, 1.f, new float[] { 3.f, 6.f }, 0); 
	}

	@Override
	protected void paintElement(Graphics g) {
		super.paintElement(g);

		g.setStroke(new BasicStroke(1));
		Iterator<Point> waypoints = getWaypoints().iterator();
		if (waypoints.hasNext()) {
			Point prevPoint = waypointToRelative(waypoints.next());
			if (waypoints.hasNext()) {
				// Startsymbol
				final Rectangle point = new Rectangle(prevPoint);
				point.grow(3, 3);
				g.setPaint(getBackground());
				g.fillOval(point);
				g.setPaint(getForeground());
				g.drawOval(point);
				// Endepfeil
				Point curPoint = waypointToRelative(waypoints.next());
				while (waypoints.hasNext()) {
					prevPoint = curPoint;
					curPoint = waypointToRelative(waypoints.next());
				}
				g.setPaint(getBackground());
				g.fillArrow(prevPoint, curPoint);
				g.setPaint(getForeground());
				g.drawArrow(prevPoint, curPoint);
			}
		}
	}

}
