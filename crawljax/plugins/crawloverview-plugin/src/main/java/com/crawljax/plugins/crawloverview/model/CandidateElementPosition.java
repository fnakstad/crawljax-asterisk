package com.crawljax.plugins.crawloverview.model;

import javax.annotation.concurrent.Immutable;

import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;

import com.crawljax.core.CandidateElement;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Objects;

/**
 * Position of a candidate element of a state. This type is used to build the overlays of screenshot
 * to show where the {@link CandidateElement}s were located.
 */
@Immutable
public class CandidateElementPosition {

	private final int top;
	private final int left;
	private final String xpath;
	private final String eventType;
	private final int width;
	private final int height;
	private final String elementType;

	/**
	 * @param xpath
	 * @param location
	 *            The element's offset.
	 * @param size
	 *            The size of the element.
	 */
	public CandidateElementPosition(String xpath, String eventType, Point location, Dimension size, String elementType) {
		this.top = location.y;
		this.left = location.x;
		this.xpath = xpath;
		this.eventType = eventType;
		this.width = size.width;
		this.height = size.height;
		this.elementType = elementType;
	}

	@JsonCreator
	public CandidateElementPosition(@JsonProperty("top") int top,
	        @JsonProperty("left") int left, @JsonProperty("xpath") String xpath, @JsonProperty("eventType") String eventType,
	        @JsonProperty("width") int width, @JsonProperty("height") int height, @JsonProperty("elementType") String elementType) {
		this.top = top;
		this.left = left;
		this.xpath = xpath;
		this.eventType = eventType;
		this.width = width;
		this.height = height;
		this.elementType = elementType;
	}

	/**
	 * @return The offset to the top of the document.
	 */
	public int getTop() {
		return top;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	/**
	 * @return The offset to the left of the document.
	 */
	public int getLeft() {
		return left;
	}

	public String getXpath() {
		return xpath;
	}
	
	public String getEventType() {
		return eventType;
	}
	
	public String getElementType() {
		return elementType;
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this)
		        .add("top", top)
		        .add("left", left)
		        .add("xpath", xpath)
		        .add("eventtype", eventType)
		        .add("width", width)
		        .add("height", height)
		        .add("elementType", elementType)
		        .toString();
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(top, left, xpath, eventType, width, height, elementType);
	}

	@Override
	public boolean equals(Object object) {
		if (object instanceof CandidateElementPosition) {
			CandidateElementPosition that = (CandidateElementPosition) object;
			return Objects.equal(this.top, that.top)
			        && Objects.equal(this.left, that.left)
			        && Objects.equal(this.xpath, that.xpath)
			        && Objects.equal(this.eventType, that.eventType)
			        && Objects.equal(this.width, that.width)
			        && Objects.equal(this.height, that.height)
			        && Objects.equal(this.elementType, that.elementType);
		}
		return false;
	}

}
