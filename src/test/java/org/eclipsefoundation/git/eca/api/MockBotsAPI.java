/*******************************************************************************
 * Copyright (C) 2020 Eclipse Foundation
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipsefoundation.git.eca.api;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;

import org.eclipse.microprofile.rest.client.inject.RestClient;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;

import io.quarkus.test.Mock;

@Mock
@RestClient
@ApplicationScoped
public class MockBotsAPI implements BotsAPI {

	private List<JsonNode> src;
	
	@PostConstruct
	public void build() {
		this.src = new ArrayList<>();
		ObjectNode b1 = new ObjectNode(JsonNodeFactory.instance);
        b1.set("id", new TextNode("1"));
        b1.set("email", new TextNode("1.bot@eclipse.org"));
		b1.set("projectId", new TextNode("sample.proj"));
		b1.set("username", new TextNode("projbot"));
		src.add(b1);

        ObjectNode b2 = new ObjectNode(JsonNodeFactory.instance);
		b2.set("id", new TextNode("10"));
		b2.set("email", new TextNode("2.bot@eclipse.org"));
		b2.set("projectId", new TextNode("sample.proto"));
		b2.set("username", new TextNode("protobot"));
        ObjectNode ssbGH = new ObjectNode(JsonNodeFactory.instance);
		ssbGH.set("email", new TextNode("2.bot-github@eclipse.org"));
		ssbGH.set("username", new TextNode("protobot-gh"));
		b2.set("github.com", ssbGH);
		src.add(b2);

        ObjectNode b3 = new ObjectNode(JsonNodeFactory.instance);
		b3.set("id", new TextNode("11"));
		b3.set("email", new TextNode("3.bot@eclipse.org"));
		b3.set("projectId", new TextNode("spec.proj"));
		b3.set("username", new TextNode("specbot"));
		ObjectNode ssbGL = new ObjectNode(JsonNodeFactory.instance);
		ssbGL.set("email", new TextNode("3.bot-gitlab@eclipse.org"));
		ssbGL.set("username", new TextNode("protobot-gl"));
		b3.set("gitlab.eclipse.org",ssbGL);
		src.add(b3);
	}

	@Override
	public List<JsonNode> getBots() {
		return new ArrayList<>(src);
	}
}
