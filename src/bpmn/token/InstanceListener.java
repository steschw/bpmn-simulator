package bpmn.token;

public interface InstanceListener {

	void instanceAdded(final Instance instance);

	void instanceRemoved(final Instance instance);

}
