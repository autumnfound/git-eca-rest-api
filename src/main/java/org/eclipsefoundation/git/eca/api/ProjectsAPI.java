package org.eclipsefoundation.git.eca.api;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import org.eclipsefoundation.git.eca.model.Project;

/**
 * Interface for interacting with the PMI Projects API. Used to link Git
 * repos/projects with an Eclipse project to validate committer access.
 * 
 * @author Martin Lowe
 *
 */
@Path("/api/projects")
@RegisterRestClient
public interface ProjectsAPI {

	/**
	 * Retrieves all projects with the given repo URL.
	 * 
	 * @param repoUrl the target repos URL
	 * @return a list of Eclipse Foundation projects.
	 */
	@GET
	@Produces("application/json")
	List<Project> getProject(@QueryParam("repoUrl") String repoUrl);
}
