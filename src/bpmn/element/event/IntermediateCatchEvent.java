package bpmn.element.event;

import java.awt.Cursor;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Iterator;

import bpmn.token.Token;

@SuppressWarnings("serial")
public class IntermediateCatchEvent extends IntermediateEvent
	implements MouseListener {

	public IntermediateCatchEvent(final String id, final String name) {
		super(id, name);
		addMouseListener(this);
	}

	protected void updateCursor() {
		setCursor(
				getInnerTokens().isEmpty()
						? Cursor.getDefaultCursor()
						: Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
	}

	@Override
	public void tokenEnter(Token token) {
		super.tokenEnter(token);
		updateCursor();
	}

	@Override
	public void tokenExit(Token token) {
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
		return false;
	}

	@Override
	public void mouseClicked(final MouseEvent e) {
		passFirstTokenToAllOutgoing();
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

}
