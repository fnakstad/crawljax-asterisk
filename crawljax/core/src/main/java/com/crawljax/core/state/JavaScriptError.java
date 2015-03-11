package com.crawljax.core.state;

import com.google.common.collect.ImmutableList;

public class JavaScriptError {
	private int _lineNumber;
	private String _msg;
	private String _url;
	
	public JavaScriptError(int lineNumber, String msg, String url) {
		_lineNumber = lineNumber;
		_msg = msg;
		_url = url;
	}
	
	public int getLineNumber() {
		return _lineNumber;
	}
	
	public String getMsg() {
		return _msg;
	}
	
	public String getUrl() {
		return _url;
	}
}
