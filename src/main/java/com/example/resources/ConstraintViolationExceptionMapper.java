package com.example.resources;

import java.util.HashMap;
import java.util.Map;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

/**
 * ConstraintViolationExceptionを引き取って
 * 独自の処理をします。
 */
@Provider
public class ConstraintViolationExceptionMapper implements ExceptionMapper<ConstraintViolationException> {
    
    @Override
    public Response toResponse(ConstraintViolationException exception) {
        Map<String, String> response = new HashMap<>();
        response.put("status", "error");

        // 複数の制約エラーメッセージを取り出して、1つの文字列に繋げます
        StringBuilder errors = new StringBuilder();
        for (ConstraintViolation violation : exception.getConstraintViolations()) {
            errors.append(violation.getMessage()).append(", ");
        }
        // 末尾の , を削除
        if (errors.length() > 0) {
            errors.setLength(errors.length() - 2);
        }
        response.put("errors", errors.toString());

        // パラメータの検証エラーの場合、404 Bad Request を返すのが一般的です。
        return Response.status(Response.Status.BAD_REQUEST).entity(response).build();
    }
}
