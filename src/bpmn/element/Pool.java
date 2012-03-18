package bpmn.element;

import java.awt.Point;
import java.util.Vector;


/**
 * A Pool is the graphical representation of a Participant in a Collaboration
 */
public class Pool extends TitledFlowElement {

	private static final long serialVersionUID = 1L;

	private Vector<LaneSet> laneSets = new Vector<LaneSet>(); 

	public Pool(final String id, final String name, ElementRef<ExpandedProcess> processRef) {
		super(id, name);
	}

	public void addLaneSet(final LaneSet laneSet) {
		assert(laneSet != null);
		laneSets.add(laneSet);
	}

	@Override
	protected int getBorderWidth() {
		return 2;
	}

	@Override
	protected void paintElement(Graphics g) {
		super.paintElement(g);

		final Rectangle bounds = getTitleBounds();
		if (isHorizontal()) {
			g.drawLine(new Point((int)bounds.getMaxX(), (int)bounds.getMinY()),
					new Point((int)bounds.getMaxX(), (int)bounds.getMaxY()));
		} else {
			g.drawLine(new Point((int)bounds.getMinX(), (int)bounds.getMaxY()),
					new Point((int)bounds.getMaxX(), (int)bounds.getMaxY()));
		}
	}

}
