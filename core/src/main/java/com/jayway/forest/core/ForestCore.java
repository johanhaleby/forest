package com.jayway.forest.core;

import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.jayway.forest.di.DependencyInjectionSPI;
import com.jayway.forest.exceptions.MethodNotAllowedException;
import com.jayway.forest.exceptions.NotFoundException;
import com.jayway.forest.reflection.*;
import com.jayway.forest.roles.DescribedResource;
import com.jayway.forest.roles.IdDiscoverableResource;
import com.jayway.forest.roles.Linkable;
import com.jayway.forest.roles.Resource;

public class ForestCore {

	private final DependencyInjectionSPI dependencyInjectionSPI;
	private final ResourceUtil resourceUtil;
	private final Application application;

	public ForestCore(Application application, DependencyInjectionSPI dependencyInjectionSPI) {
		this.application = application;
		this.dependencyInjectionSPI = dependencyInjectionSPI;
		resourceUtil = new ResourceUtil(dependencyInjectionSPI);
	}

    private PathAndMethod setup(HttpServletRequest request) {
        String path = request.getPathInfo();
        if ( path == null ) {
            throw new NotFoundException();
        }

        // call application specific context setup
        application.setupRequestContext();
        return new PathAndMethod(path);
    }

    public Object evaluateGet( HttpServletRequest request) {
    	PathAndMethod pathAndMethod = setup(request);
        if ( pathAndMethod.method() == null ) {
            return capabilities(evaluatePath(pathAndMethod.pathSegments()), request);
        }
        return resourceUtil.get(request, evaluatePath(pathAndMethod.pathSegments()), pathAndMethod.method());
    }

    public void evaluatePostPut( HttpServletRequest request, InputStream stream, Map<String, String[]> formParams, MediaTypeHandler mediaTypeHandler ) {
    	PathAndMethod pathAndMethod = setup(request);
        if ( pathAndMethod.method() == null ) {
            // TODO allow post if CreatableResource
            throw new MethodNotAllowedException();
        }
        resourceUtil.post(evaluatePath(pathAndMethod.pathSegments()), pathAndMethod.method(), formParams, stream, mediaTypeHandler);
    }

    public void evaluateDelete( HttpServletRequest request ) {
    	PathAndMethod pathAndMethod = setup(request);
        if ( pathAndMethod.method() != null ) {
            throw new MethodNotAllowedException();
        }
        resourceUtil.invokeDelete(evaluatePath(pathAndMethod.pathSegments()));
    }

    private Resource evaluatePath( List<String> segments ) {
        Resource current = dependencyInjectionSPI.postCreate( application.root() );
        for ( String pathSegment: segments ) {
            current = resourceUtil.invokePathMethod( current, pathSegment );
            current = dependencyInjectionSPI.postCreate(current);
        }
        return current;
    }

    private Capabilities capabilities(Resource resource, HttpServletRequest request) {
        Class<?> clazz = resource.getClass();
        Capabilities capabilities = new Capabilities(clazz.getName());
        QueryForListCapability discoverMethod = null;
        for ( Method m : clazz.getDeclaredMethods() ) {
            if ( m.isSynthetic() ) continue;
            Capability method = resourceUtil.makeResourceMethod(resource, m);
            if (method instanceof CommandCapability) {
                capabilities.addCommand(method);
            } else if (method instanceof QueryForListCapability) {
                capabilities.addQuery(method);
                if ( method.name().equals( "discover") ) {
                    discoverMethod = (QueryForListCapability) method;
                }
            } else if (method instanceof QueryCapability) {
                capabilities.addQuery(method);
            } else if (method instanceof SubResource) {
                capabilities.addResource(method);
            }
        }
        if ( resource instanceof IdDiscoverableResource) {
            if ( discoverMethod != null ) {
                try {
                    capabilities.setDiscovered( discoverMethod.get(request) );
                } catch( Exception e) {
                    // nothing discovered ignore
                }
            }
        }
        if (resource instanceof DescribedResource) {
            try {
                capabilities.setDescriptionResult(((DescribedResource) resource).description());
            } catch ( Exception e) {
                capabilities.setDescriptionResult("Exception occurred when evaluating 'description'");
            }
        }
        return capabilities;
    }

}
