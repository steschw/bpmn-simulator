package bpmn.element;

import java.util.Vector;


public class LaneSet extends FlowElement {

	private static final long serialVersionUID = 1L;

	private Vector<Lane> lanes = new Vector<Lane>(); 

	public LaneSet(final String id) {
		super(id, null);
	}

	public void addLane(final Lane lane) {
		assert(lane != null);
		lanes.add(lane);
	}

	@Override
	protected void paintElement(Graphics g) {
	}

}
