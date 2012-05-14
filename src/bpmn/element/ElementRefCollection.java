package bpmn.element;

import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;

public class ElementRefCollection<T extends Element> {

	private final Map<String, ElementRef<T>> data
			= new TreeMap<String, ElementRef<T>>();

	public Collection<ElementRef<T>> values() {
		return data.values();
	}

	public void set(final T element) {
		set(element.getId(), element);
	}

	protected void set(final String id, final T element) {
		if (id != null && !id.isEmpty()) {
			if (data.containsKey(id)) {
				final ElementRef<T> elementRef = data.get(id);
				if (!elementRef.hasElement()) {
					elementRef.setElement(element);
				} else {
					assert elementRef.getElement() == element;
				}
			} else {
				data.put(id, new ElementRef<T>(element));
			}
		}
	}

	public ElementRef<T> getRef(final String id) {
		if (id != null && !id.isEmpty()) {
			if (!data.containsKey(id)) {
				set(id, null);
			}
			return data.get(id);
		} else {
			return null;
		}
	}

	public T get(final String id) {
		final ElementRef<T> elementRef = getRef(id);
		return (elementRef == null) ? null : elementRef.getElement();
	}

}
