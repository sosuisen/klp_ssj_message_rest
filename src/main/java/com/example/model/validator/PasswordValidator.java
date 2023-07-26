package com.example.model.validator;

import com.example.model.user.UserDTO;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordValidator implements ConstraintValidator<ValidPassword, String> {

	@Override
	public boolean isValid(String password, ConstraintValidatorContext context) {
		if (password == null || password.isEmpty()) {
			return true;
		}

		var pattern = java.util.regex.Pattern.compile(UserDTO.PASSWORD_REGEX);
		if (!pattern.matcher(password).matches()) {
			return false;
		}

		if (password.length() < UserDTO.MIN_PASSWORD_LENGTH
				|| password.length() > UserDTO.MAX_PASSWORD_LENGTH) {
			return false;
		}
		
		return true;
	}
}