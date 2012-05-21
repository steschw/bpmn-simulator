package bpmn.element.event;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Stroke;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.Icon;

import bpmn.element.ElementRef;
import bpmn.element.Graphics;
import bpmn.element.Visualization;
import bpmn.element.activity.Activity;
import bpmn.token.Instance;

@SuppressWarnings("serial")
public final class BoundaryEvent
		extends AbstractEvent
		implements CatchEvent, MouseListener {

	private final boolean cancelActivity;
	private final ElementRef<Activity> attachedToRef;

	public BoundaryEvent(final String id, final String name,
			final boolean cancelActivity,
			ElementRef<Activity> attachedToRef) {
		super(id, name, null);
		this.cancelActivity = cancelActivity;
		this.attachedToRef = attachedToRef;
		addMouseListener(this);
	}

	public boolean isInterrupting() {
		return cancelActivity;
	}

	protected ElementRef<Activity> getAttachedToRef() {
		return attachedToRef;
	}

	public Activity getAttachedTo() {
		final ElementRef<Activity> activityRef = getAttachedToRef();
		return ((activityRef == null) || !activityRef.hasElement())
				? null
				: activityRef.getElement();
	}

	@Override
	protected Color getElementDefaultBackground() {
		return getVisualization().getBackground(Visualization.Element.EVENT_BOUNDARY);
	}

	public void happen(final Instance instance) {
		getDefinition().throwHappen(null);
	}

	@Override
	protected Stroke getStroke() {
		return new BasicStroke(getBorderWidth(),
				BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER, 1.f, new float[] { 4.f, 6.f }, 0); 
	}

	@Override
	protected void paintElement(final Graphics g) {
		super.paintElement(g);

		if (!isInterrupting()) {
			paintInnerCircle(g);
		}
	}

	@Override
	protected Icon getTypeIcon() {
		return getDefinition().getIcon(getVisualization(), false);
	}

	@Override
	public void mouseClicked(final MouseEvent e) {
		happen(null);
	}

	@Override
	public void mousePressed(final MouseEvent e) {
	}

	@Override
	public void mouseReleased(final MouseEvent e) {
	}

	@Override
	public void mouseEntered(final MouseEvent e) {
	}

	@Override
	public void mouseExited(final MouseEvent e) {
	}

}
