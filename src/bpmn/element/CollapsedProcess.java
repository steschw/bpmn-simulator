package bpmn.element;

import java.awt.Color;
import java.awt.Point;
import java.util.Collection;
import java.util.Vector;

import bpmn.token.Instance;
import bpmn.token.Token;

public class CollapsedProcess extends FlowElement {

	private static final long serialVersionUID = 1L;

	private Vector<Instance> instances = new Vector<Instance>(); 

	public CollapsedProcess(ExpandedProcess expandedProcess) {
		super(expandedProcess.getId(), expandedProcess.getName());
	}

	public void addInstance(final Instance instance) {
		assert(!instances.contains(instance));
		instances.add(instance);
		repaint();
	}

	public void removeInstance(final Instance instance) {
		assert(instances.contains(instance));
		instances.remove(instance);
		repaint();
	}

	protected Collection<Instance> getInstances() {
		return instances;
	}

	@Override
	public Color getForeground() {
		final Collection<Instance> instances = getInstances();
		if ((instances != null) && (instances.size() > 0)) {
			return Token.HIGHLIGHT_COLOR;
		}
		return super.getForeground();
	}

	@Override
	protected void paintBackground(Graphics g) {
		g.fillRoundRect(getElementInnerBounds(), 10, 10);
	}

	@Override
	protected void paintElement(Graphics g) {
		final Rectangle bounds = getElementInnerBounds();
		g.drawRoundRect(bounds, 10, 10);

		final int WIDTH = 20;
		final int HEIGHT = 20;
		final Rectangle symbolBounds = new Rectangle(bounds.x + (bounds.width - WIDTH) / 2, bounds.y + bounds.height - HEIGHT, WIDTH, HEIGHT);
		drawSymbol(g, symbolBounds);
	}

	protected void drawSymbol(Graphics g, final Rectangle bounds) {
		g.drawRect(bounds);
		bounds.grow(-4, -4);
		g.drawCross(bounds, false);
	}

	@Override
	protected void paintTokens(Graphics g) {
		super.paintTokens(g);

		final Rectangle bounds = getElementInnerBounds();
		final Point point = bounds.getRightTop();
		for (Instance instance : getInstances()) {
			instance.paint(g, point);
			point.translate(-5, 0);
		}
	}

}
