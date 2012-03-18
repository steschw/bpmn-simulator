package bpmn.element;

import java.awt.BasicStroke;
import java.awt.Stroke;

public class Association extends ConnectingElement {

	private static final long serialVersionUID = 1L;

	public Association(final String id, final String name, ElementRef<FlowElement> source,
			ElementRef<FlowElement> target) {
		super(id, name, source, target);
	}

	@Override
	protected Stroke getStroke() {
		return new BasicStroke(getBorderWidth(),
				BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER, 1.f, new float[] { 4.f, 6.f }, 0); 
	}

}
