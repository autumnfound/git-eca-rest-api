/*******************************************************************************
 * Copyright (C) 2020 Eclipse Foundation
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipsefoundation.git.eca.helper;

import org.eclipsefoundation.git.eca.model.Commit;

/**
 * Contains helpers for processing commits.
 *
 * @author Martin Lowe
 *
 */
public class CommitHelper {

	/**
	 * Validate the commits fields.
	 *
	 * @param c commit to validate
	 * @return true if valid, otherwise false
	 */
	public static boolean validateCommit(Commit c) {
		if (c == null) {
			return false;
		}

		boolean valid = true;
		// check current commit data
		if (c.getHash() == null) {
			valid = false;
		}
		// check author
		if (c.getAuthor() == null || c.getAuthor().getMail() == null) {
			valid = false;
		}
		// check committer
		if (c.getCommitter() == null || c.getCommitter().getMail() == null) {
			valid = false;
		}

		return valid;
	}

	private CommitHelper() {
	}
}
