package bpmn.element;

import java.awt.Point;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

import javax.swing.SwingUtilities;


public abstract class ConnectingElement extends BaseElement {

	private static final long serialVersionUID = 1L;

	private LinkedList<Point> waypoints = new LinkedList<Point>();

	private ElementRef<FlowElement> sourceRef = null;
	private ElementRef<FlowElement> targetRef = null;

	public ConnectingElement(final String id, final String name, ElementRef<FlowElement> source, ElementRef<FlowElement> target) {
		super(id, name);
		setSourceRef(source);
		setTarget(target);
	}

	protected void setSourceRef(final ElementRef<FlowElement> elementRef) {
		assert(elementRef != null);
		this.sourceRef = elementRef;
	}

	protected ElementRef<FlowElement> getSourceRef() {
		return sourceRef;
	}

	protected static final <E extends BaseElement> E getElementFromElementRef(ElementRef<E> elementRef) {
		if ((elementRef != null) && elementRef.hasElement()) {
			return elementRef.getElement();
		}
		return null;
	}

	public FlowElement getSource() {
		return getElementFromElementRef(getSourceRef());
	}

	protected void setTarget(final ElementRef<FlowElement> elementRef) {
		assert(elementRef != null);
		this.targetRef = elementRef;
	}

	protected ElementRef<FlowElement> getTargetRef() {
		return targetRef;
	}

	public FlowElement getTarget() {
		return getElementFromElementRef(getTargetRef());
	}

	public void addWaypoint(Point point) {
		waypoints.add(point);
		updateBounds();
	}

	protected Collection<Point> getWaypoints() {
		return waypoints;
	}

	protected void updateBounds() {
		if (waypoints.isEmpty()) {
			assert(false);
			return;
		}
		int minX = waypoints.getFirst().x;
		int maxX = minX + 1;
		int minY = waypoints.getFirst().y;
		int maxY = minY + 1;
		for (Point point : waypoints) {
			if (point.x < minX) {
				minX = point.x; 
			}
			if (point.x > maxX) {
				maxX = point.x; 
			}
			if (point.y < minY) {
				minY = point.y; 
			}
			if (point.y > maxY) {
				maxY = point.y; 
			}
		}
		final int width = maxX - minX;
		final int height = maxY - minY;
		setInnerBounds(new Rectangle(minX, minY, width, height));
	}

	@Override
	protected int getBorderWidth() {
		return 2;
	}

	protected Point waypointToRelative(Point point) {
		return SwingUtilities.convertPoint(getParent(), point, this);
	}

	@Override
	protected void paintElement(Graphics g) {
		Point lastPoint = null;
		Iterator<Point> i = getWaypoints().iterator();
		Point currentPoint = null;
		while (i.hasNext()) {
			currentPoint = i.next();
			if (lastPoint != null) {
				final Point fromPoint = waypointToRelative(lastPoint);
				final Point toPoint = waypointToRelative(currentPoint);
				g.drawLine(fromPoint, toPoint);
			}
			lastPoint = currentPoint;
		}
	}

	protected int getLength() {
		int steps = 0;
		Point last = null;
		for (Point current : getWaypoints()) {
			if (last != null) {
				steps += (int)last.distance(current);
			}
			last = current;
		}
		return steps;
	}

	protected Point getPosition(final int length) {
		int position = 0;
		Point last = null;
		for (Point current : getWaypoints()) {
			if (last != null) {
				final int distance = (int)last.distance(current); 
				if ((position + distance) >= length) {
					final double a = Graphics.getAngle(current, last);
					final Point p = Graphics.polarToCartesian(last, position - length, a);
					return p;
				}
				position += distance;
			}
			last = current;
		}
		return null;
	}

	@Override
	protected Point getElementCenter() {
		return getPosition(getLength() / 2);
	}

	@Override
	protected void initLabel(Label label) {
		label.setCenterTopPosition(getElementCenter());
	}

}
