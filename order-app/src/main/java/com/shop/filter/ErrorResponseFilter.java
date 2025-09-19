package com.shop.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.inject.Inject;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;
import org.jboss.logging.Logger;
import java.io.IOException;
import java.util.Optional;

/**
 * Response filter that throws WebApplicationException for error responses
 * This allows callers to handle exceptions instead of parsing error responses
 */
@Provider
public class ErrorResponseFilter implements ContainerResponseFilter {

    private static final Logger LOG = Logger.getLogger(ErrorResponseFilter.class);

    @Inject
    ObjectMapper objectMapper;

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException {
        // Only process error responses (4xx and 5xx)
        if (responseContext.getStatus() < 400) {
            return;
        }

        LOG.debugf("Processing error response: %d %s %s", 
                   responseContext.getStatus(), 
                   requestContext.getMethod(), 
                   requestContext.getUriInfo().getRequestUri().toString());

        // Create or parse ErrorResponse
        ErrorResponse errorResponse = createErrorResponse(requestContext, responseContext);
        
        LOG.infof("Throwing WebApplicationException for %s %s - Code: %s, Message: %s", 
                 requestContext.getMethod(), 
                 requestContext.getUriInfo().getRequestUri().toString(),
                 errorResponse.getErrorCode(), 
                 errorResponse.getErrorMessage());
        
        // Create Response with ErrorResponse entity and throw WebApplicationException
        throw new WebApplicationException(
            errorResponse.getErrorMessage(), 
            Response.status(responseContext.getStatus())
                .entity(errorResponse)
                .type(MediaType.APPLICATION_JSON)
                .build()
        );
    }
    
    private ErrorResponse createErrorResponse(ContainerRequestContext requestContext, ContainerResponseContext responseContext) {
        // Try to parse existing ErrorResponse or create new one functionally
        ErrorResponse errorResponse = Optional.ofNullable(responseContext.getEntity())
            .filter(String.class::isInstance)
            .map(String.class::cast)
            .map(entityString -> tryParseErrorResponse(entityString, responseContext.getStatus()))
            .orElseGet(() -> createNewErrorResponse(responseContext.getEntity(), responseContext.getStatus()));
            
        // Set context information functionally
        return setContextInfo(errorResponse, requestContext, responseContext);
    }
    
    private ErrorResponse tryParseErrorResponse(String entityString, int status) {
        try {
            ErrorResponse parsed = objectMapper.readValue(entityString, ErrorResponse.class);
            LOG.debugf("Parsed existing ErrorResponse: %s", parsed.getErrorMessage());
            return parsed;
        } catch (Exception e) {
            LOG.debugf("Failed to parse ErrorResponse, creating new one: %s", e.getMessage());
            return buildErrorResponse(String.valueOf(status), entityString, "HTTP " + status + " error");
        }
    }
    
    private ErrorResponse createNewErrorResponse(Object entity, int status) {
        return buildErrorResponse(
            String.valueOf(status),
            "HTTP " + status + " error",
            "Error response with " + (entity != null ? entity.getClass().getSimpleName() : "null") + " entity"
        );
    }
    
    private ErrorResponse buildErrorResponse(String errorCode, String errorMessage, String errorDescription) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setErrorCode(errorCode);
        errorResponse.setErrorMessage(errorMessage);
        errorResponse.setErrorDescription(errorDescription);
        return errorResponse;
    }
    
    private ErrorResponse setContextInfo(ErrorResponse errorResponse, ContainerRequestContext requestContext, ContainerResponseContext responseContext) {
        errorResponse.setMethod(requestContext.getMethod());
        errorResponse.setUri(requestContext.getUriInfo().getRequestUri().toString());
        errorResponse.setResource(requestContext.getUriInfo().getPath());
        return errorResponse;
    }
}