package bpmn.element;

@SuppressWarnings("serial")
public class DataAssociation extends Association {

	public DataAssociation(final String id,
			final ElementRef<FlowElement> source,
			final ElementRef<FlowElement> target) {
		super(id, source, target);
	}

}
