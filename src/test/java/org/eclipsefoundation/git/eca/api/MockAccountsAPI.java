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
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.eclipsefoundation.git.eca.model.EclipseUser;
import org.eclipsefoundation.git.eca.model.EclipseUser.ECA;

import io.quarkus.test.Mock;

@Mock
@RestClient
@ApplicationScoped
public class MockAccountsAPI implements AccountsAPI {

	private List<EclipseUser> src;
	
	@PostConstruct
	public void build() {
		this.src = new ArrayList<>();
		int id = 0;

		EclipseUser e1 = new EclipseUser();
		e1.setCommitter(false);
		e1.setId(id++);
		e1.setMail("newbie@important.co");
		e1.setName("newbieAnon");
		e1.setEca(new ECA());
		src.add(e1);
		
		EclipseUser e2 = new EclipseUser();
		e2.setCommitter(false);
		e2.setId(id++);
		e2.setMail("slom@eclipse-foundation.org");
		e2.setName("barshall_blathers");
		e2.setEca(new ECA(true, true));
		src.add(e2);
		
		EclipseUser e3 = new EclipseUser();
		e3.setCommitter(false);
		e3.setId(id++);
		e3.setMail("tester@eclipse-foundation.org");
		e3.setName("mctesterson");
		e3.setEca(new ECA(true, false));
		src.add(e3);
		
		EclipseUser e4 = new EclipseUser();
		e4.setCommitter(true);
		e4.setId(id++);
		e4.setMail("code.wiz@important.co");
		e4.setName("da_wizz");
		e4.setEca(new ECA(true, true));
		src.add(e4);
		
		EclipseUser e5 = new EclipseUser();
		e5.setCommitter(true);
		e5.setId(id++);
		e5.setMail("grunt@important.co");
		e5.setName("grunter");
		e5.setEca(new ECA(true, false));
		src.add(e5);
		
		EclipseUser e6 = new EclipseUser();
		e6.setCommitter(false);
		e6.setId(id++);
		e6.setMail("paper.pusher@important.co");
		e6.setName("sumAnalyst");
		e6.setEca(new ECA(true, false));
		src.add(e6);
	}
	
	@Override
	public List<EclipseUser> getUsers(String authBearer, String id, String name, String mail) {
		return src.stream().filter(user -> {
			boolean matches = true;
			if (id != null && !Integer.toString(user.getId()).equals(id)) {
				matches = false;
			}
			if (name != null && !user.getName().equals(name)) {
				matches = false;
			}
			if (mail != null && !user.getMail().equalsIgnoreCase(mail)) {
				matches = false;
			}
			return matches;
		}).collect(Collectors.toList());
	}

	@Override
	public EclipseUser getUserByGithubUname(String authBearer, String uname) {
		// assume GH username == Eclipse uname for simplicity of test
		return src.stream().filter(user -> uname.equalsIgnoreCase(user.getName())).findFirst().orElse(null);
	}

}
