package com.org.moneytransfer.service.util;

import javax.ws.rs.core.Response;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ServiceUtils {

    public static Response buildErrorResponse(Response.Status responseStatus, List<String> errorMessages) {

        List<ResponseError> errors = errorMessages.stream().map(ResponseError::new).collect(Collectors.toList());

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("errors", errors);

        return Response.status(responseStatus).entity(response).build();
    }

    public static Response buildErrorResponse(Response.Status responseStatus, String errorMessage) {
        return buildErrorResponse(responseStatus, Collections.singletonList(errorMessage));
    }

}
