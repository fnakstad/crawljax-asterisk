package com.crawljax.core;

import java.util.List;

import org.w3c.dom.Element;

import com.crawljax.browser.EmbeddedBrowser;
import com.crawljax.condition.eventablecondition.EventableCondition;
import com.crawljax.core.state.Eventable;
import com.crawljax.core.state.Identification;
import com.crawljax.core.state.Eventable.EventType;
import com.crawljax.forms.FormInput;
import com.crawljax.util.DomUtils;
import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

/**
 * Candidate element for crawling. It is possible to link this {@link Eventable} to form inputs, so
 * that Crawljax knows which values to set for this elements before it is clicked.
 */
public class CandidateElement {

	private final Identification identification;

	private final Element element;

	private final ImmutableList<FormInput> formInputs;
	private final String relatedFrame;
	
	private final EventType eventType;

	private EventableCondition eventableCondition;

	/**
	 * Constructor for a element a identification and a relatedFrame.
	 * 
	 * @param element
	 *            the element.
	 * @param identification
	 *            the identification.
	 * @param relatedFrame
	 *            the frame this element belongs to.
	 */
	public CandidateElement(Element element, Identification identification, String relatedFrame,
	        List<FormInput> formInputs, EventType eventType) {
		this.identification = identification;
		this.element = element;
		this.relatedFrame = relatedFrame;
		this.formInputs = ImmutableList.copyOf(formInputs);
		this.eventType = eventType;
	}

	/**
	 * Constructor for a element a xpath-identification and no relatedFrame.
	 * 
	 * @param element
	 *            the element
	 * @param xpath
	 *            the xpath expression of the element
	 */
	public CandidateElement(Element element, String xpath, List<FormInput> formInputs) {
		this(element, new Identification(Identification.How.xpath, xpath), "", formInputs, EventType.click);
	}

	public CandidateElement(Element sourceElement, Identification identification,
	        String relatedFrame) {
		this(sourceElement, identification, relatedFrame, ImmutableList.<FormInput> of(), EventType.click);
	}
	
	public CandidateElement(Element sourceElement, Identification identification, String relatedFrame, EventType eventType) {
		this(sourceElement, identification, relatedFrame, ImmutableList.<FormInput> of(), eventType);
	}

	/**
	 * @return unique string without atusa attribute
	 */
	public String getGeneralString() {
		ImmutableSet<String> exclude = ImmutableSet.of("atusa");

		StringBuilder result = new StringBuilder();
		if (element != null) {
			result.append(this.element.getNodeName()).append(": ");

		}
		result.append(DomUtils.getElementAttributes(this.element, exclude)).append(' ')
		        .append(this.identification).append(' ').append(relatedFrame);

		return result.toString();
	}

	/**
	 * @return unique string of this candidate element
	 */
	public String getUniqueString() {

		String result = "";

		if (element != null) {
			result +=
			        this.element.getNodeName() + ": "
			                + DomUtils.getAllElementAttributes(this.element) + " ";
		}

		result += this.identification + " " + relatedFrame;

		return result;
	}

	/**
	 * @return the element
	 */
	public Element getElement() {
		return element;
	}
	
	public EventType getEventType() {
		return eventType;
	}

	/**
	 * @return list with related formInputs
	 */
	public List<FormInput> getFormInputs() {
		return formInputs;
	}

	/**
	 * @param eventableCondition
	 *            the EventableCondition
	 */
	public void setEventableCondition(EventableCondition eventableCondition) {
		this.eventableCondition = eventableCondition;
	}

	/**
	 * @return the identification object.
	 */
	public Identification getIdentification() {
		return identification;
	}

	/**
	 * @return the relatedFrame
	 */
	public String getRelatedFrame() {
		return relatedFrame;
	}

	/**
	 * Check all eventable Condition for correctness.
	 * 
	 * @see #eventableCondition
	 * @see EventableCondition#checkAllConditionsSatisfied(EmbeddedBrowser)
	 * @param browser
	 *            the current browser instance that contains the current dom
	 * @return true if all conditions are satisfied or no conditions are specified
	 */
	public boolean allConditionsSatisfied(EmbeddedBrowser browser) {
		if (eventableCondition != null) {
			return eventableCondition.checkAllConditionsSatisfied(browser);
		}
		// No condition specified so return true....
		return true;
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this)
		        .add("identification", identification)
		        .add("element", element)
		        .add("formInputs", formInputs)
		        .add("eventableCondition", eventableCondition)
		        .add("relatedFrame", relatedFrame)
		        .toString();
	}
}
