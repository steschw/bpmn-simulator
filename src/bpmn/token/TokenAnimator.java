package bpmn.token;

public class TokenAnimator extends Animator {

	private InstanceController instanceController = new InstanceController();

	public TokenAnimator() {
		super();
		start();
	}

	public InstanceController getInstanceController() {
		return instanceController;
	}

	@Override
	public synchronized void step(int count) {
		getInstanceController().stepAll(count);
	}

	@Override
	public void reset() {
		getInstanceController().removeAll();
		super.reset();
	}

}
