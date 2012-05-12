package bpmn.element.event;

import java.awt.Cursor;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Iterator;

import javax.swing.Icon;

import bpmn.token.Token;

@SuppressWarnings("serial")
public class IntermediateCatchEvent extends IntermediateEvent
	implements MouseListener {

	public IntermediateCatchEvent(final String id, final String name) {
		super(id, name);
		addMouseListener(this);
	}

	private boolean isInteractive() {
		return isTimer() || isMessage();
	}

	protected void updateCursor() {
		if (isInteractive()) {
			setCursor(
					getInnerTokens().isEmpty()
							? Cursor.getDefaultCursor()
							: Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		}
	}

	@Override
	public void tokenEnter(final Token token) {
		super.tokenEnter(token);
		updateCursor();
	}

	@Override
	public void tokenExit(final Token token) {
		super.tokenExit(token);
		updateCursor();
	}

	protected void passFirstTokenToAllOutgoing() {
		final Iterator<Token> iterator = getInnerTokens().iterator();
		if (iterator.hasNext()) {
			final Token firstToken = iterator.next();
			passTokenToAllOutgoing(firstToken);
			firstToken.remove();
		}
	}

	@Override
	protected boolean canForwardToken(final Token token) {
		if (isInteractive()) {
			return false;
		} else {
			return super.canForwardToken(token);
		}
	}

	@Override
	public void mouseClicked(final MouseEvent e) {
		if (isInteractive()) {
			passFirstTokenToAllOutgoing();
		}
	}

	@Override
	public void mousePressed(final MouseEvent e) {
	}

	@Override
	public void mouseReleased(final MouseEvent e) {
	}

	@Override
	public void mouseEntered(final MouseEvent e) {
	}

	@Override
	public void mouseExited(final MouseEvent e) {
	}

	@Override
	protected Icon getTypeIcon() {
		return getDefinition().getIcon(getVisualConfig(), false);
	}

}
