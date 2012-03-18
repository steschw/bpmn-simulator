package bpmn.token;

import java.util.Vector;

public abstract class Animator implements Runnable {

	private Vector<AnimationListener> animationListeners = new Vector<AnimationListener>();

	private Thread thread = null;

	private boolean pause = false;

	private float speedFactor = 1.0f;

	public Animator() {
		super();
	}

	public void addAnimationListener(AnimationListener listener) {
		synchronized (animationListeners) {
			animationListeners.add(listener);
		}
	}

	public void removeAnimationListener(AnimationListener listener) {
		synchronized (animationListeners) {
			animationListeners.remove(listener);
		}
	}

	protected void notifyAnimationPlay() {
		synchronized (animationListeners) {
			for (AnimationListener listener : animationListeners) {
				listener.animationPlay();
			}
		}
	}

	protected void notifyAnimationPause() {
		synchronized (animationListeners) {
			for (AnimationListener listener : animationListeners) {
				listener.animationPause();
			}
		}
	}

	protected void notifyAnimationReset() {
		synchronized (animationListeners) {
			for (AnimationListener listener : animationListeners) {
				listener.animationReset();
			}
		}
	}

	public synchronized void setSpeed(final float factor) {
		assert(factor > 0.0f);
		speedFactor = factor;
	}

	protected synchronized final long getStepSleep() {
		final long FPS_25 = (1000 / 25);
		return (long)(FPS_25 / speedFactor);
	}

	@Override
	public void run() {
		try {
			while (thread.isAlive() && !thread.isInterrupted()) {
				Thread.sleep(getStepSleep());
				if (!isPaused()) {
					step(1);
				}
			}
		} catch (InterruptedException e) {
			// ok
		}
	}

	public abstract void step(int count);

	public void play() {
		pause = false;
		notifyAnimationPlay();
	}

	public void pause() {
		pause = true;
		notifyAnimationPause();
	}

	public boolean isPaused() {
		return pause;
	}

	public void reset() {
		notifyAnimationReset();
	}

	protected synchronized void start() {
		assert(thread == null);
		if (thread == null) {
			thread = new Thread(this);
			thread.setName("Animation");
			thread.start();
		}
	}

	public void end() {
		synchronized (thread) {
			assert(thread != null);
			if (thread != null) {
				thread.interrupt();
				try {
					thread.join();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				thread = null;
			}
		}
		pause = false;
	}

}
