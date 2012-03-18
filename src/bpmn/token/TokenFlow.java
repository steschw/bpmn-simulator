package bpmn.token;

public interface TokenFlow {

	public void tokenEnter(Token token);

	public void tokenDispatch(Token token);

	public void tokenExit(Token token);

	public boolean hasIncomingPathWithActiveToken(Instance instance);

}
