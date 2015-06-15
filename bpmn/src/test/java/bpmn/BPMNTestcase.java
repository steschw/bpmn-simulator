package bpmn;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;

import org.junit.Assert;

import com.googlecode.bpmn_simulator.animation.element.logical.LogicalFlowElement;
import com.googlecode.bpmn_simulator.animation.input.Definition;
import com.googlecode.bpmn_simulator.bpmn.model.AbstractBPMNDefinition;
import com.googlecode.bpmn_simulator.test.logic.AnimationStateSequence;
import com.googlecode.bpmn_simulator.test.logic.AttributedElementsState;
import com.googlecode.bpmn_simulator.test.logic.StateSequence;
import com.googlecode.bpmn_simulator.test.logic.StaticAttributedElementsState;
import com.googlecode.bpmn_simulator.test.logic.StaticStateSequence;

public abstract class BPMNTestcase {

	protected static final Integer ONE = Integer.valueOf(1);

	protected File file(final String filename) {
		final URL url = Thread.currentThread().getContextClassLoader().getResource(filename);
		Assert.assertNotNull("resource could not be found", url);
		return new File(url.getFile());
	}

	protected static Definition<?> loadDefinition(final File file) {
		final Definition<?> definition = new AbstractBPMNDefinition<>();
		try (final InputStream input = new FileInputStream(file)) {
			definition.load(input);
			return definition;
		} catch (IOException e) {
			Assert.fail(e.toString());
		}
		return null;
	}

	private static <T> T findByString(final Collection<T> collection, final String s) {
		if (s != null) {
			for (final T t : collection) {
				if ((t != null) && s.equals(t.toString())) {
					return t;
				}
			}
		}
		return null;
	}

	protected static LogicalFlowElement getStartElement(final Definition<?> definition, final String start) {
		final LogicalFlowElement flowElement = findByString(definition.getInstantiatingElements(), start);
		Assert.assertNotNull(flowElement);
		return flowElement;
	}

	protected static StateSequence<AttributedElementsState<String, Integer>> start(final Definition<?> definition, final String start) {
		final StateSequence<AttributedElementsState<String, Integer>> stateSequence
				= new AnimationStateSequence(definition, getStartElement(definition, start));
		return stateSequence;
	}

	private static <T> Collection<String> asToString(final Collection<T> collection) {
		if (collection == null) {
			return null;
		}
		final Collection<String> strings = new ArrayList<>();
		for (final T o : collection) {
			if (o != null) {
				strings.add(o.toString());
			}
		}
		return strings;
	}

	protected static StaticStateSequence<StaticAttributedElementsState<String, Integer>> beginSequence(final Definition<?> definition) {
		return new StaticStateSequence<>(new StaticAttributedElementsState<String, Integer>(asToString(definition.getFlowElements())));
	}

}
