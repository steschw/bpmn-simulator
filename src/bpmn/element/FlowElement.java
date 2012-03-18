package bpmn.element;

import java.util.Collection;
import java.util.Vector;

public abstract class FlowElement extends BaseElement {

	private static final long serialVersionUID = 1L;

	private Vector<ElementRef<SequenceFlow>> incoming = new Vector<ElementRef<SequenceFlow>>(); 
	private Vector<ElementRef<SequenceFlow>> outgoing = new Vector<ElementRef<SequenceFlow>>(); 

	public FlowElement(final String id, final String name) {
		super(id, name);
	}

	protected Collection<ElementRef<SequenceFlow>> getIncoming() {
		return incoming;
	}

	protected Collection<ElementRef<SequenceFlow>> getOutgoing() {
		return outgoing;
	}

	public boolean hasIncoming() {
		return (getIncoming().size() > 0);
	}

	public boolean hasOutgoing() {
		return (getOutgoing().size() > 0);
	}

	public void addIncoming(final ElementRef<SequenceFlow> element) {
		assert(element != null);
		if (!incoming.contains(element)) {
			incoming.add(element);
		} else {
			assert(false);
		}
	}

	public void addOutgoing(final ElementRef<SequenceFlow> element) {
		assert(element != null);
		if (!outgoing.contains(element)) {
			outgoing.add(element);
		} else {
			assert(false);
		}
	}

}
