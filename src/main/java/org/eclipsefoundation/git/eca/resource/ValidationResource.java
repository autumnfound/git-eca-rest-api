package org.eclipsefoundation.git.eca.resource;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.eclipsefoundation.git.eca.api.AccountsAPI;
import org.eclipsefoundation.git.eca.api.BotsAPI;
import org.eclipsefoundation.git.eca.api.ProjectsAPI;
import org.eclipsefoundation.git.eca.helper.CommitHelper;
import org.eclipsefoundation.git.eca.model.BotUser;
import org.eclipsefoundation.git.eca.model.Commit;
import org.eclipsefoundation.git.eca.model.EclipseUser;
import org.eclipsefoundation.git.eca.model.GitUser;
import org.eclipsefoundation.git.eca.model.Project;
import org.eclipsefoundation.git.eca.model.ValidationRequest;
import org.eclipsefoundation.git.eca.model.ValidationResponse;
import org.eclipsefoundation.git.eca.namespace.APIStatusCode;
import org.eclipsefoundation.git.eca.service.CachingService;
import org.eclipsefoundation.git.eca.service.OAuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ECA validation endpoint for Git commits. Will use information from the bots,
 * projects, and accounts API to validate commits passed to this endpoint.
 * Should be as system agnostic as possible to allow for any service to request
 * validation with less reliance on services external to the Eclipse foundation.
 * 
 * @author Martin Lowe
 *
 */
@Path("/git/eca")
@Consumes({ MediaType.APPLICATION_JSON })
@Produces({ MediaType.APPLICATION_JSON })
public class ValidationResource {
	private static final Logger LOGGER = LoggerFactory.getLogger(ValidationResource.class);

	// eclipse API rest client interfaces
	@Inject
	@RestClient
	AccountsAPI accounts;
	@Inject
	@RestClient
	ProjectsAPI projects;
	@Inject
	@RestClient
	BotsAPI bots;

	// external API/service harnesses
	@Inject
	OAuthService oauth;
	@Inject
	CachingService cache;

	/**
	 * Consuming a JSON request, this method will validate all passed commits, using
	 * the repo URL and the repository provider. These commits will be validated to
	 * ensure that all users are covered either by an ECA, or are committers on the
	 * project. In the case of ECA-only contributors, an additional sign off footer
	 * is required in the body of the commit.
	 * 
	 * @param req the request containing basic data plus the commits to be validated
	 * @return a web response indicating success or failure for each commit, along
	 *         with standard messages that may be used to give users context on
	 *         failure.
	 */
	@POST
	public Response validate(ValidationRequest req) {
		ValidationResponse r = new ValidationResponse();
		// check that we have commits to validate
		if (req.getCommits() == null || req.getCommits().isEmpty()) {
			addError(r, "A commit is required to validate", null);
			r.updateStatus(APIStatusCode.ERROR_DEFAULT);
			return r.toResponse();
		}
		// check that we have a repo set
		if (req.getRepoUrl() == null) {
			addError(r, "A base repo URL needs to be set in order to validate", null);
			r.updateStatus(APIStatusCode.ERROR_DEFAULT);
			return r.toResponse();
		}
		// check that we have a type set
		if (req.getProvider() == null) {
			addError(r, "A provider needs to be set to validate a request", null);
			r.updateStatus(APIStatusCode.ERROR_DEFAULT);
			return r.toResponse();
		}

		for (Commit c : req.getCommits()) {
			// ensure the commit is valid, and has required fields
			if (!CommitHelper.validateCommit(c)) {
				addError(r, "One or more commits were invalid. Please check the payload and try again", null);
				r.updateStatus(APIStatusCode.ERROR_DEFAULT);
				return r.toResponse();
			}
			// retrieve the author + committer for the current request
			GitUser author = c.getAuthor();
			GitUser committer = c.getCommitter();

			addMessage(r, String.format("Reviewing commit: %1$s", c.getHash()), c.getHash());
			addMessage(r, String.format("Authored by: %1$s <%2$s>", author.getName(), author.getMail()), c.getHash());

			// retrieve the eclipse account for the author
			EclipseUser eclipseAuthor = getIdentifiedUser(author);
			if (eclipseAuthor == null) {
				addMessage(r, String.format("Could not find an Eclipse user with mail '%1$s' for author of commit %2$s",
						committer.getMail(), c.getHash()), c.getHash());
				addError(r, "Author must have an Eclipse Account", c.getHash());
				continue;
			}

			// retrieve the eclipse account for the committer
			EclipseUser eclipseCommitter = getIdentifiedUser(committer);
			if (eclipseCommitter == null) {
				addMessage(r,
						String.format("Could not find an Eclipse user with mail '%1$s' for committer of commit %2$s",
								committer.getMail(), c.getHash()),
						c.getHash());
				addError(r, "Committing user must have an Eclipse Account", c.getHash());
				continue;
			}
			// validate author access to the current repo
			validateAuthorAccess(r, c, eclipseAuthor, req.getRepoUrl());

			// only committers can push on behalf of other users
			if (!eclipseAuthor.equals(eclipseCommitter)
					&& !isCommitter(r, eclipseCommitter, c.getHash(), req.getRepoUrl())) {
				addMessage(r, "You are not a project committer.", c.getHash());
				addMessage(r, "Only project committers can push on behalf of others.", c.getHash());
				addError(r, "You must be a committer to push on behalf of others.", c.getHash());
			}
		}
		if (r.getErrorCount() == 0) {
			r.setPassed(true);
			r.updateStatus(APIStatusCode.SUCCESS_DEFAULT);
		} else {
			r.updateStatus(APIStatusCode.ERROR_DEFAULT);
		}
		r.updateStatus(APIStatusCode.ERROR_DEFAULT);
		return r.toResponse();
	}

	/**
	 * Validates author access for the current commit. If there are errors, they are
	 * recorded in the response for the current request to be returned once all
	 * validation checks are completed.
	 * 
	 * @param r             the current response object for the request
	 * @param c             the commit that is being validated
	 * @param eclipseAuthor the user to validate on a branch
	 * @param repoUrl       repo URL for the current commit set, to be used when
	 *                      checking projects
	 */
	private void validateAuthorAccess(ValidationResponse r, Commit c, EclipseUser eclipseAuthor, String repoUrl) {
		// check if the author matches to an eclipse user and is a committer
		if (isCommitter(r, eclipseAuthor, c.getHash(), repoUrl)) {
			addMessage(r, "The author is a committer on the project.", c.getHash());
			r.updateStatus(APIStatusCode.SUCCESS_COMMITTER);
		} else {
			addMessage(r, "The author is not a committer on the project.", c.getHash());
			// check if the author is signed off if not a committer
			if (eclipseAuthor.getEca().isSigned()) {
				addMessage(r, "The author has a current Eclipse Contributor Agreement (ECA) on file.", c.getHash());
			} else {
				addMessage(r,
						"The author does not have a current Eclipse Contributor Agreement (ECA) on file.\n"
								+ "If there are multiple Typecommits, please ensure that each author has a ECA.",
						c.getHash());
				addError(r, "An Eclipse Contributor Agreement is required.", c.getHash());
			}

			// retrieve the email of the Signed-off-by footer
			String signedOffBy = CommitHelper.getSignedOffByEmail(c);
			if (signedOffBy != null && signedOffBy.equalsIgnoreCase(eclipseAuthor.getMail())) {
				addMessage(r, "The author has \"signed-off\" on the contribution.", c.getHash());
			} else {
				addMessage(r,
						"The author has not \"signed-off\" on the contribution.\n"
								+ "If there are multiple commits, please ensure that each commit is signed-off.",
						c.getHash());
				addError(r, "The contributor must \"sign-off\" on the contribution.", c.getHash());
				r.updateStatus(APIStatusCode.ERROR_SIGN_OFF);
			}
		}
	}

	/**
	 * Checks whether the given user is a committer on the project. If they are and
	 * the project is also a specification for a working group, an additional access
	 * check is made against the user.
	 * 
	 * Additionally, a check is made to see if the user is a registered bot user for
	 * the given project. If they match for the given project, they are granted
	 * committer-like access to the repository.
	 * 
	 * @param r       the current response object for the request
	 * @param user    the user to validate on a branch
	 * @param hash    the hash of the commit that is being validated
	 * @param repoUrl repo URL for the current commit set, to be used when checking
	 *                projects
	 * @return true if user is considered a committer, false otherwise.
	 */
	private boolean isCommitter(ValidationResponse r, EclipseUser user, String hash, String repoUrl) {
		// TODO repo URL doesn't currently filter, so this may return false positives
		// atm
		// check for all projects that make use of the given repo
		Optional<List<Project>> filteredProjects = cache.get("projects|" + repoUrl, () -> projects.getProject(repoUrl),
				(Class<List<Project>>) (Object) List.class);
		if (!filteredProjects.isPresent() || filteredProjects.get().isEmpty()) {
			return false;
		}
		// iterate over filtered projects
		for (Project p : filteredProjects.get()) {
			LOGGER.debug("Checking project '{}' for user '{}'", p.getName(), user.getName());

			// check if any of the committers usernames match the current user
			if (p.getCommitters().stream().anyMatch(u -> u.getUsername().equals(user.getName()))) {
				// check if the current project is a committer project, and if the user can
				// commit to specs
				if (p.getSpecWorkingGroup() != null && !user.getEca().isCanContributeSpecProject()) {
					// set error + update response status
					r.addError(hash, String.format(
							"Project is a specification for the working group '%1$s', but user does not have permission to modify a specification project",
							p.getSpecWorkingGroup()), APIStatusCode.ERROR_SPEC_PROJECT);
					r.updateStatus(APIStatusCode.ERROR_SPEC_PROJECT);
					return false;
				} else {
					LOGGER.debug("User '{}' was found to be a committer on current project repo '{}'", user.getMail(),
							p.getName());
					return true;
				}
			}

			// get a list of all bots that Eclipse is aware of
			Optional<List<BotUser>> allBots = cache.get("allBots", () -> bots.getBots(),
					(Class<List<BotUser>>) (Object) List.class);
			// check that we have bots to iterate over
			if (allBots.isPresent()) {
				// get all bot users (if any) that match the current users email
				List<BotUser> botUsers = allBots.get().stream()
						.filter(bot -> user.getMail().equalsIgnoreCase(bot.getEmail())).collect(Collectors.toList());
				// if any of the bots that match the current user match the current project ID,
				// then the user is considered a committer
				if (botUsers.stream().anyMatch(b -> b.getProjectId().equalsIgnoreCase(p.getProjectId()))) {
					LOGGER.debug("User '{}' was found to be a bot on current project repo '{}'", user.getName(),
							p.getName());
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Retrieves an Eclipse Account user object given the Git users email address
	 * (at minimum). This is facilitated using the Eclipse Foundation accounts API,
	 * along short lived in-memory caching for performance and some protection
	 * against duplicate requests.
	 * 
	 * @param user the user to retrieve Eclipse Account information for
	 * @return the Eclipse Account user information if found, or null if there was
	 *         an error or no user exists.
	 */
	private EclipseUser getIdentifiedUser(GitUser user) {
		// get the Eclipse account for the user
		try {
			// use cache to avoid asking for the same user repeatedly on repeated requests
			Optional<List<EclipseUser>> users = cache.get("user|" + user.getMail(),
					() -> accounts.getUsers("Bearer " + oauth.getToken(), null, null, user.getMail()),
					(Class<List<EclipseUser>>) (Object) List.class);
			if (!users.isPresent() || users.get().isEmpty()) {
				LOGGER.error("No users found for mail '{}'", user.getMail());
				return null;
			}
			return users.get().get(0);
		} catch (WebApplicationException e) {
			Response r = e.getResponse();
			if (r != null && r.getStatus() == 404) {
				LOGGER.error("No users found for mail '{}'", user.getMail());
			} else {
				LOGGER.error("Error while checking for user", e);
			}
		}
		return null;
	}

	private void addMessage(ValidationResponse r, String message, String hash) {
		addMessage(r, hash, message, APIStatusCode.SUCCESS_DEFAULT);
	}

	private void addError(ValidationResponse r, String message, String hash) {
		addError(r, message, hash, APIStatusCode.ERROR_DEFAULT);
	}

	private void addMessage(ValidationResponse r, String message, String hash, APIStatusCode code) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(message);
		}
		r.addMessage(hash, message, code);
	}

	private void addError(ValidationResponse r, String message, String hash, APIStatusCode code) {
		LOGGER.error(message);
		r.addError(hash, message, code);
	}
}
