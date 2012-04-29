package bpmn.element;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JCheckBox;
import javax.swing.SwingConstants;

public class Expression extends JCheckBox implements ItemListener {

	private static final long serialVersionUID = 1L;

	private static final Color COLOR_TRUE = new Color(0, 196, 0);
	private static final Color COLOR_FALSE = new Color(0, 0, 0);

	private boolean value;

	public Expression() {
		this(null);
	}

	public Expression(final String text) {
		super((String)null);

		setToolTipText(text);
		setVerticalAlignment(SwingConstants.TOP);
		setHorizontalTextPosition(SwingConstants.CENTER);
		setVerticalTextPosition(SwingConstants.TOP);
		setFocusable(false);
		setOpaque(false);
		setBorderPaintedFlat(true);
		setExpressionValue(false);
		setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

		setText(text);

		addItemListener(this);
	}

	@Override
	public String getText() {
		final String text = super.getText();
		if (text != null) {
			final StringBuffer html = new StringBuffer("<html>");
			html.append(text.replaceAll("\n", "<br>"));
			html.append("</html>");
			return html.toString();
		}
		return null;
	}

	protected void setExpressionValue(final boolean value) {
		setForeground(value ? COLOR_TRUE : COLOR_FALSE);
		synchronized (this) {
			this.value = value;
		}
	}

	public synchronized boolean isTrue() {
		return value;
	}

	@Override
	public void itemStateChanged(final ItemEvent event) {
		setExpressionValue(event.getStateChange() == ItemEvent.SELECTED);
	}

}
