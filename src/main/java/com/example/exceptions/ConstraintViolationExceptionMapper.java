package com.example.exceptions;

import java.util.List;

import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

/**
 * JAX-RSのConstraintViolationExceptionを引き取って
 * 独自の処理をします。
 */
@Provider
public class ConstraintViolationExceptionMapper implements ExceptionMapper<ConstraintViolationException> {
	public record ErrorResponse(String type, List<String> errors) {
	};

	@Override
	public Response toResponse(ConstraintViolationException exception) {
		var response = new ErrorResponse("constraint_error",
				exception.getConstraintViolations().stream().map(violation -> violation.getMessage()).toList());

		// パラメータの検証エラーの場合、400 Bad Request を返すのが一般的です。
		return Response.status(Response.Status.BAD_REQUEST).entity(response).build();
	}
}
