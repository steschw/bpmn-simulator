package bpmn.element.event;

import java.awt.Cursor;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import bpmn.element.ExpandedProcess;
import bpmn.element.Graphics;
import bpmn.token.InstanceController;

public class StartEvent extends Event implements MouseListener {

	private static final long serialVersionUID = 1L;

	public StartEvent(final String id, final String name,
			final InstanceController tockenController) {
		super(id, name, tockenController);
		addMouseListener(this);
	}

	@Override
	protected void paintBackground(Graphics g) {
		super.paintBackground(g);
	}

	@Override
	protected void setTokenController(InstanceController controller) {
		super.setTokenController(controller);
		updateCursor();
	}

	@Override
	public void setParentProcess(ExpandedProcess parentProcess) {
		super.setParentProcess(parentProcess);
		updateCursor();
	}

	public boolean canStartManuell() {
		final ExpandedProcess process = getParentProcess();
		return (getInstanceController() != null) && (process != null) && !process.hasIncoming(); 
	}

	protected void updateCursor() {
		setCursor(canStartManuell() ?
				new Cursor(Cursor.HAND_CURSOR)
				: Cursor.getDefaultCursor());
	}

	public void start() {
		final InstanceController instanceController = getInstanceController();
		if (instanceController != null) {
			instanceController.newInstance().newToken(this);
		} else {
			assert(false);
		}
	}

	@Override
	public void mouseClicked(MouseEvent event) {
		if (canStartManuell()) {
			start();
		}
	}

	@Override
	public void mouseEntered(MouseEvent event) {
	}

	@Override
	public void mouseExited(MouseEvent event) {
	}

	@Override
	public void mousePressed(MouseEvent event) {
	}

	@Override
	public void mouseReleased(MouseEvent event) {
	}

}
