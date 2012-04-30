package gui;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JColorChooser;

public class ColorSelector extends JButton implements ActionListener {

	private static final long serialVersionUID = 1L;

	public ColorSelector(final String text) {
		super(text);
		setBorder(BorderFactory.createRaisedBevelBorder());

		addActionListener(this);

		updateTooltip();
	}

	public void setSelectedColor(final Color color) {
		setBackground(color);
		updateTooltip();
	}

	public Color getSelectedColor() {
		return getBackground();
	}

	@Override
	public void actionPerformed(final ActionEvent e) {
		final Color color = JColorChooser.showDialog(this, null, getSelectedColor());
		setSelectedColor(color);
	}

	protected void updateTooltip() {
		final StringBuilder html = new StringBuilder();
		html.append("<html>");
		html.append("<body>");
		html.append("<b>");
		html.append(getText());
		html.append(":</b>");
		final Color color = getSelectedColor();
		html.append("<table>");
		html.append("<tr>");
		html.append("<td>HEX</td><td>");
		html.append(String.format("%X", color.getRGB()));
		html.append("</td>");
		html.append("</tr>");
		html.append("<tr>");
		html.append("<td>RGB</td><td>");
		html.append(String.format("%d, %d, %d",
				color.getRed(), color.getBlue(), color.getBlue()));
		html.append("</td>");
		html.append("</tr>");
		html.append("</table>");
		html.append("</body>");
		html.append("</html>");
		setToolTipText(html.toString());
	}

}
