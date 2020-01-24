/* Copyright (c) 2019 Eclipse Foundation and others.
 * This program and the accompanying materials are made available
 * under the terms of the Eclipse Public License 2.0
 * which is available at http://www.eclipse.org/legal/epl-v20.html,
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipsefoundation.git.eca.service;

import java.util.Optional;
import java.util.Set;
import java.util.concurrent.Callable;

/**
 * Interface defining the caching service to be used within the application.
 * 
 * @author Martin Lowe
 * @param <T> the type of object to be stored in the cache.
 */
public interface CachingService {

	/**
	 * Returns an Optional object of type T, returning a cached object if available,
	 * otherwise using the callable to generate a value to be stored in the cache
	 * and returned.
	 * 
	 * @param cacheKey the cache key of the object to store in the cache
	 * @param callable a runnable that returns an object of type T
	 * @return the cached result
	 */
	<T> Optional<T> get(String cacheKey, Callable<? extends T> callable, Class<T> clazz);

	/**
	 * Returns the expiration date in millis since epoch.
	 * 
	 * @param cacheKey the cache key to check for a value, and if set its
	 *                 expiration.
	 * @return an Optional expiration date for the current object if its set. If
	 *         there is no underlying data, then empty would be returned
	 */
	Optional<Long> getExpiration(String cacheKey);

	/**
	 * @return the max age of cache entries
	 */
	long getMaxAge();

	/**
	 * Retrieves a set of cache keys available to the current cache.
	 * 
	 * @return unmodifiable set of cache entry keys.
	 */
	Set<String> getCacheKeys();

	/**
	 * Removes cache entry for given cache entry key.
	 * 
	 * @param key cache entry key
	 */
	void remove(String key);

	/**
	 * Removes all cache entries.
	 */
	void removeAll();
}
