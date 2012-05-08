package gui;

import java.awt.Component;
import java.util.Locale;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.plaf.basic.BasicComboBoxRenderer;

@SuppressWarnings("serial")
public class LocaleComboBox extends JComboBox {

	private static final Locale LOCALES[] = new Locale[] {
		new Locale("en"),
		new Locale("de")
	};

	public LocaleComboBox() {
		super(new DefaultComboBoxModel(LOCALES));
		setRenderer(new BasicComboBoxRenderer() {
			@Override
			public Component getListCellRendererComponent(JList list, Object value,
					int index, boolean isSelected, boolean cellHasFocus) {
				Component component = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
				if (value == null) {
					final StringBuilder string = new StringBuilder("Default");
					string.append(" (");
					string.append(Locale.getDefault().getDisplayName());
					string.append(')');
					setText(string.toString());
				} else {
					final Locale locale = (Locale)value;
					setText(locale.getDisplayName(locale));
				}
				return component;
			}
		});
	}

}
