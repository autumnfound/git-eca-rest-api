package org.eclipsefoundation.git.eca.config;

import javax.inject.Singleton;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.quarkus.jackson.ObjectMapperCustomizer;

/**
 * Sets Jackson serializer to not fail when an unknown property is encountered.
 * 
 * @author Martin Lowe
 *
 */
@Singleton
public class CustomJacksonConfig implements ObjectMapperCustomizer {

	@Override
	public void customize(ObjectMapper objectMapper) {
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	}

}
