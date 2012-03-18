package bpmn.element;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;

import javax.swing.JLabel;

public class Label extends JLabel {

	private static final long serialVersionUID = 1L;

	private boolean alignCenter = true;

	private static final Font FONT = new Font("Tahoma", Font.PLAIN, 12); 

	public Label(final String text) {
		super(text, JLabel.LEADING);
		setFont(FONT);
		//setFont(getFont().deriveFont(Font.PLAIN));
	}

	@Override
	public void setText(String text) {
		/*
		 * Scheinbar wird keine Aktualisierung durchgeführt,
		 * wenn der neu zu setzende Text dem alten entspricht.
		 * Die Methode setAlignCenter benötigt dieses Verhalten aber.
		 */
		super.setText("");
		super.setText(text);
	}

	public final void setAlignCenter(final boolean center) {
		this.alignCenter = center;
		setText(super.getText());
	}

	public final boolean getAlignCenter() {
		return alignCenter;
	}

	@Override
	public String getText() {
		StringBuffer text = new StringBuffer("<html>");
		final boolean center = getAlignCenter();
		if (center) {
			text.append("<center>");
		}
		text.append(super.getText().replaceAll("\n", "<br>"));
		if (center) {
			text.append("</center>");
		}
		text.append("</html>");
		return text.toString();
	}

	public void setMaxWidth(final int width) {
		final Dimension maximumSize = getMaximumSize();
		maximumSize.width = width;
		setMaximumSize(maximumSize);
	}

	public void setCenterPosition(final Point center) {
		final Dimension size = getPreferredSize();
		setSize(size);
		setLocation(center.x - (size.width / 2), center.y - (size.height / 2));
	}

	public void setCenterTopPosition(final Point center) {
		final Dimension size = getPreferredSize();
		setSize(size);
		setLocation(center.x - (size.width / 2), center.y);
	}

	public void setLeftTopPosition(final Point center) {
		final Dimension size = getPreferredSize();
		setSize(size);
		setLocation(center.x, center.y);
	}

}
