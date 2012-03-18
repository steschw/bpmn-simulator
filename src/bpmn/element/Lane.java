package bpmn.element;

import java.util.Vector;

public class Lane extends TitledFlowElement {

	private static final long serialVersionUID = 1L;

	private Vector<LaneSet> laneSets = new Vector<LaneSet>(); 

	public Lane(final String id, final String name) {
		super(id, name);
	}

	public void addLaneSet(LaneSet laneSet) {
		assert(laneSet != null);
		laneSets.add(laneSet);
	}

}
