package bpmn.element;

public class ElementRef<E extends BaseElement> {

	private E element = null;

	public ElementRef() {
		super();
	}

	public ElementRef(final E element) {
		this();
		setElement(element);
	}

	public void setElement(final E element) {
		this.element = element;
	}

	public E getElement() {
		return element;
	}

	public boolean hasElement() {
		return (element != null);
	}

	private boolean equalsElement(BaseElement element) {
		final BaseElement e = getElement();
		if ((e == null) || (element == null)) {
			return false;
		}
		return e.equals(element);
	}

	@Override
	public boolean equals(Object object) {
		if (object instanceof ElementRef<?>) {
			ElementRef<?> elementRef = (ElementRef<?>)object;
			return equalsElement(elementRef.getElement());
		} else if (object instanceof BaseElement) {
			return equalsElement((BaseElement)object);
		}
		return super.equals(object);
	}

}
