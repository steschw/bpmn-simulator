package bpmn.element;

import java.awt.Dimension;
import java.awt.Paint;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.Vector;

import javax.swing.Scrollable;

import bpmn.element.event.Event;
import bpmn.element.event.StartEvent;
import bpmn.token.Instance;
import bpmn.token.Token;
import bpmn.token.TokenFlow;

public class ExpandedProcess extends Activity implements Scrollable {

	private static final long serialVersionUID = 1L;

	private Vector<BaseElement> elements = new Vector<BaseElement>();

	private CollapsedProcess collapsedProcess = null; 

	public ExpandedProcess(final String id, final String name) {
		super(id, name);
		setAutoscrolls(true);
	}

	public void addElement(final BaseElement element) {
		assert(!elements.contains(element));
		elements.add(element);
		element.setParentProcess(this);
	}

	protected boolean containsTokenFlow(final TokenFlow tokenFlow) {
		for (BaseElement element : elements) {
			if (element instanceof TokenFlow) {
				if ((TokenFlow)element == tokenFlow) {
					return true;
				}
			}
		}
		return false;
	}

	public CollapsedProcess createCollapsed() {
		assert(collapsedProcess == null);
		collapsedProcess = new CollapsedProcess(this);
		collapsedProcess.setBackground(getBackground());
		collapsedProcess.setForeground(getForeground());
		ExpandedProcess parentProcess = getParentProcess();
		if (parentProcess != null) {
			parentProcess.addElement(collapsedProcess);
		}
		return collapsedProcess;
	}

	@Override
	public void tokenEnter(Token token) {
		final TokenFlow from = token.getPreviousFlow();
		if (from.equals(this) || containsTokenFlow(from)) {
			// Token kommt von einem Prozesselement und soll aus dem Prozess austreten
			// Es wird zuerst in die Tokenliste des Prozesses hinzugefügt
			// und erst wenn alle Token dieser Instanz am Ende des Prozesses angekommen sind
			// mit diesen vereint und weitergeleitet
			super.tokenEnter(token);
		} else {
			// Token tritt in den Prozess ein
			// Es wird direkt an die Elemente innerhalb des Prozesses weitergeleitet
			forwardTokenToInner(token);
			token.remove();
		}
	}

	@Override
	public void tokenExit(Token token) {
		super.tokenExit(token);
	}

	@Override
	protected boolean canForwardToken(Token token) {
		final int exitTokenCount = getTokens().byInstance(token.getInstance()).getCount();
		final int instanceTokenCount = token.getInstance().getTokenCount();
		return (super.canForwardToken(token) && (exitTokenCount == instanceTokenCount));
	}

	@Override
	protected boolean forwardTokenToAllOutgoing(final Token token) {
		final Instance subInstance = token.getInstance(); 
		final Instance parentInstance = subInstance.getParentInstance();
		boolean forwarded = true; // der hauptprozess hat keine ausgehenden sequence flows
		if (parentInstance != null) {
			forwarded = super.forwardTokenToAllOutgoing(token, parentInstance);
		}
		if (collapsedProcess != null) {
			collapsedProcess.removeInstance(subInstance);
		}
		subInstance.remove();
		return forwarded;
	}

	protected void forwardTokenToInner(final Token token) {
		final Instance subInstance = token.getInstance().newChildInstance();
		if (collapsedProcess != null) {
			collapsedProcess.addInstance(subInstance);
		}
		Event startEvent = getStartEvent();
		if (startEvent != null) {
			token.passTo(startEvent, subInstance);
		} else {
			Vector<Activity> activities = getStartActivities();
			if (!activities.isEmpty()) {
				for (Activity startActivity : activities) {
					token.passTo(startActivity, subInstance);
				}
			} else {
				token.passTo(this, subInstance);
			}
		}
	}

	public void updateSizeByComponents() {
		setSize(getPreferredSize());
	}

	@Override
	public Dimension getPreferredSize() {
		return calcSizeByComponents();
	}

	public Vector<Activity> getStartActivities() {
		Vector<Activity> activities = new Vector<Activity>();
		for (BaseElement element : elements) {
			if (element instanceof Activity) {
				Activity activity = (Activity)element;
				if (!activity.hasIncoming()) {
					activities.add(activity);
				}
			}
		}
		return activities;
	}

	public StartEvent getStartEvent() {
		StartEvent start = null;
		for (BaseElement element : elements) {
			if (element instanceof StartEvent) {
				StartEvent event = (StartEvent)element;
				assert(start == null);
				start = event;
			}
		}
		return start;
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
	protected void paintBackground(Graphics g) {
		super.paintBackground(g);

		Paint paint = getBackgroundPaint();
		if (paint != null) {
			g.setPaint(paint);
			g.fillRoundRect(getElementInnerBounds(), 20, 20);
		}
	}

	@Override
	protected void paintElement(Graphics g) {
		g.drawRoundRect(getElementInnerBounds(), 20, 20);
	}

	@Override
	protected void initLabel(Label label) {
		label.setAlignCenter(false);
		Point position = getElementLeftTop();
		position.translate(4, 4);
		label.setLeftTopPosition(position);
	}

}
