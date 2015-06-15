package bpmn;

import org.junit.Test;

import com.googlecode.bpmn_simulator.animation.input.Definition;
import com.googlecode.bpmn_simulator.test.logic.Assert;
import com.googlecode.bpmn_simulator.test.logic.AttributedElementsState;
import com.googlecode.bpmn_simulator.test.logic.StateSequence;
import com.googlecode.bpmn_simulator.test.logic.StaticAttributedElementsState;
import com.googlecode.bpmn_simulator.test.logic.StaticStateSequence;

public class TerminationEndEventTestcase
		extends BPMNTestcase {

	@Test
	public void test() {
		final Definition<?> definition = loadDefinition(file("end_event_termination.bpmn"));
		final StaticStateSequence<StaticAttributedElementsState<String, Integer>> expectedSequence = beginSequence(definition);
		expectedSequence.getLast().addAttribute("sid-3C27EB24-3919-47B2-83F8-AAA6C67C29E0", ONE); // start event
		expectedSequence.addLast().moveAttribute("sid-3C27EB24-3919-47B2-83F8-AAA6C67C29E0", ONE, "sid-77D867CD-C3A7-4BED-B34C-278AB1A9A507"); // sequence flow
		expectedSequence.addLast().moveAttribute("sid-77D867CD-C3A7-4BED-B34C-278AB1A9A507", ONE, "sid-8367F0BD-95B4-420C-9AEF-387AE88E2F49"); // gateway
		expectedSequence.addLast()
				.removeAttribute("sid-8367F0BD-95B4-420C-9AEF-387AE88E2F49", ONE)
				.addAttribute("sid-B8ED0205-AC8A-4197-BCB8-4816088B606D", ONE)
				.addAttribute("sid-C9E310A8-0743-42B4-A0C2-96AC5159E4D1", ONE);
		expectedSequence.addLast()
				.moveAttribute("sid-B8ED0205-AC8A-4197-BCB8-4816088B606D", ONE, "sid-AA3ADEAB-04CE-414A-9D08-92A6AD8B2F05")
				.moveAttribute("sid-C9E310A8-0743-42B4-A0C2-96AC5159E4D1", ONE, "sid-610330A4-4CEC-4ECA-8FC7-57D9180A9ECC");
		expectedSequence.addLast()
				.removeAttribute("sid-AA3ADEAB-04CE-414A-9D08-92A6AD8B2F05", ONE)
				.removeAttribute("sid-610330A4-4CEC-4ECA-8FC7-57D9180A9ECC", ONE);
		final StateSequence<AttributedElementsState<String, Integer>> sequence = start(definition, "sid-3C27EB24-3919-47B2-83F8-AAA6C67C29E0");
		Assert.assertExpectedFlow(expectedSequence, sequence);
	}

}
