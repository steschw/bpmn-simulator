package gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.Iterator;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import bpmn.Model;
import bpmn.element.event.StartEvent;

public class StartButton extends JButton implements ActionListener {

	private static final long serialVersionUID = 1L;

	private Model model = null;

	public StartButton(Icon icon) {
		super(icon);
		addActionListener(this);
	}

	public void setModel(final Model model) {
		this.model = model;
		setEnabled(model != null);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (model != null) {
			Collection<StartEvent> startEvents = model.getManuallStartEvents();
			Iterator<StartEvent> iterator = startEvents.iterator();
			if (startEvents.size() == 1) {
				iterator.next().start();
			} else {
				JPopupMenu menu = new JPopupMenu();
				while (iterator.hasNext()) {
					final StartEvent startEvent = iterator.next();
					JMenuItem menuItem = new JMenuItem(startEvent.getElementName());
					menuItem.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							startEvent.start();
						}
					});
					menu.add(menuItem);
				}
				menu.show(this, 0, getHeight());
			}
		}
	}

}
