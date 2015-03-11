package com.crawljax.core.state;

import java.util.ArrayList;

/**
 * The default factory that creates State vertexes with a {@link Object#hashCode()} and {@link Object#equals(Object)}
 * function based on the Stripped dom.
 */
public class DefaultStateVertexFactory extends StateVertexFactory {

	@Override
	public StateVertex newStateVertex(int id, String url, String name, String dom, String strippedDom, ArrayList<JavaScriptError> jsErrors) {
		return new StateVertexImpl(id, url, name, dom, strippedDom, jsErrors);
	}
}
