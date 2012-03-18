package bpmn.element;


public class TitledFlowElement extends FlowElement {

	private static final long serialVersionUID = 1L;

	private static final int HEADER_SIZE = 32;

	private boolean horizontal = false;

	public TitledFlowElement(final String id, final String name) {
		super(id, name);
		setHorizontal(true);
	}

	public void setHorizontal(final boolean horizontal) {
		this.horizontal = horizontal;
	}

	protected final boolean isHorizontal() {
		return horizontal;
	}

	@Override
	protected void paintBackground(Graphics g) {
		g.fillRect(getElementInnerBounds());
	}

	@Override
	protected void paintElement(Graphics g) {
		g.drawRect(getElementInnerBounds());
	}

	protected Rectangle getTitleBounds() {
		final Rectangle innerBounds = getElementInnerBounds();
		if (isHorizontal()) {
			return new Rectangle((int)innerBounds.getMinX(), (int)innerBounds.getMinY(),
					HEADER_SIZE, (int)innerBounds.getHeight());
		} else {
			return new Rectangle((int)innerBounds.getMinX(), (int)innerBounds.getMinY(),
					(int)innerBounds.getWidth(), HEADER_SIZE);
		}
	}

	@Override
	protected void paintText(Graphics g) {
		final Rectangle bounds = getTitleBounds();
		bounds.grow(-4, -4);
		if (isHorizontal()) {
			g.drawMultilineTextVertical(bounds, getName(), false, false);
		} else {
			g.drawMultilineText(bounds, getName(), false, false);
		}
	}

	@Override
	public Label createElementLabel() {
		return null;
	}

}
