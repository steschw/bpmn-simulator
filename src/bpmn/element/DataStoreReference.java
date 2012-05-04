package bpmn.element;

public class DataStoreReference extends FlowElement {

	private static final long serialVersionUID = 1L;

	public DataStoreReference(final String id, final String name) {
		super(id, name);
	}

	@Override
	protected void updateElementLabelPosition() {
		getElementLabel().setCenterTopPosition(getInnerBounds().getCenterBottom());
	}

	@Override
	protected void paintBackground(final Graphics g) {
		super.paintBackground(g);

		g.fill(Graphics.createDataStoreShape(getElementInnerBounds()));
	}

	@Override
	protected void paintElement(final Graphics g) {
		g.drawDataStore(getElementInnerBounds());
	}

}
