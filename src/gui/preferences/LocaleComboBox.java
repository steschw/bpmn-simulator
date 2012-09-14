/*
 * Copyright (C) 2012 Stefan Schweitzer
 *
 * This software was created by Stefan Schweitzer as a student's project at
 * Fachhochschule Kaiserslautern (University of Applied Sciences).
 * Supervisor: Professor Dr. Thomas Allweyer. For more information please see
 * http://www.fh-kl.de/~allweyer
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this Software except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package gui.preferences;

import java.awt.Component;
import java.util.Locale;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.plaf.basic.BasicComboBoxRenderer;

@SuppressWarnings("serial")
public class LocaleComboBox extends JComboBox {

	private static final Locale[] LOCALES = new Locale[] {
		new Locale("en"), //$NON-NLS-1$
		new Locale("de"), //$NON-NLS-1$
	};

	public LocaleComboBox() {
		super(new DefaultComboBoxModel(LOCALES));
		setRenderer(new BasicComboBoxRenderer() {
			@Override
			public Component getListCellRendererComponent(final JList list,
					final Object value, final int index,
					final boolean isSelected, final boolean cellHasFocus) {
				final Component component = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
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
