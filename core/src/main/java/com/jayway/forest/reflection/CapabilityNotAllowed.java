package com.jayway.forest.reflection;

import java.io.InputStream;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.jayway.forest.core.MediaTypeHandler;
import com.jayway.forest.exceptions.NotFoundException;
import com.jayway.forest.roles.Resource;

public class CapabilityNotAllowed extends Capability {
	public CapabilityNotAllowed(String name) {
		super(name, null);
	}
	@Override
	public Object get(HttpServletRequest request) {
		throw new NotFoundException();
	}
	@Override
	public void post(Map<String, String[]> formParams, InputStream stream, MediaTypeHandler mediaTypeHandler) {
		throw new NotFoundException();
	}
	@Override
	public void delete() {
		throw new NotFoundException();
	}
	@Override
	public Resource subResource(String path) {
		throw new NotFoundException();
	}
	@Override
	public String httpMethod() {
		return "GET";
	}
	@Override
	public Object renderForm(RestReflection restReflection) {
		throw new UnsupportedOperationException();
	}
}
