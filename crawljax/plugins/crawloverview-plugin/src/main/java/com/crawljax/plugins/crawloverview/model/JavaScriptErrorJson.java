package com.crawljax.plugins.crawloverview.model;

import javax.annotation.concurrent.Immutable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;

@Immutable
public class JavaScriptErrorJson {

	private final int lineNumber;
	private final String msg;
	private final String url;
	
	/*public JavaScriptError(int lineNumber, String msg, String url) {
		this.lineNumber = lineNumber;
		this.msg = msg;
		this.url = url;
	}*/
	
	@JsonCreator
	public JavaScriptErrorJson(
	        @JsonProperty("lineNumber") int lineNumber,
	        @JsonProperty("msg") String msg,
	        @JsonProperty("url") String url) {
		super();
		this.lineNumber = lineNumber;
		this.msg = msg;
		this.url = url;
	}
	
	public int getLineNumber() {
		return lineNumber;
	}
	
	public String getMsg() {
		return msg;
	}
	
	public String getUrl() {
		return url;
	}
	
	@Override
	public int hashCode() {
		return Objects.hashCode(lineNumber, msg, url);
	}
	
	@Override
	public boolean equals(Object object) {
		if (object instanceof JavaScriptErrorJson) {
			JavaScriptErrorJson that = (JavaScriptErrorJson) object;
			return Objects.equal(this.lineNumber, that.lineNumber)
			        && Objects.equal(this.msg, that.msg)
			        && Objects.equal(this.url, that.url);
		}
		return false;
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this).add("lineNumber", lineNumber).add("msg", msg)
		        .add("url", url).toString();
	}
	
}
