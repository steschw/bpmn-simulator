package bpmn.element;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.util.Vector;

import javax.swing.Scrollable;


public class Collaboration extends FlowElement implements Scrollable {

	private static final long serialVersionUID = 1L;

	private Vector<BaseElement> elements = new Vector<BaseElement>(); 

	public Collaboration(final String id) {
		super(id, null);
	}

	public void addElement(BaseElement element) {
		assert(element != null);
		elements.add(element);
	}

	@Override
	public Dimension getPreferredSize() {
		return calcSizeByComponents();
	}

	@Override
	public Dimension getPreferredScrollableViewportSize() {
		return getPreferredSize();
	}

	@Override
	public int getScrollableBlockIncrement(Rectangle arg0, int arg1, int arg2) {
		return 0;
	}

	@Override
	public boolean getScrollableTracksViewportHeight() {
		return false;
	}

	@Override
	public boolean getScrollableTracksViewportWidth() {
		return false;
	}

	@Override
	public int getScrollableUnitIncrement(Rectangle arg0, int arg1, int arg2) {
		return 0;
	}

	@Override
	protected void paintElement(Graphics g) {
		// TODO Auto-generated method stub
		
	}

}
