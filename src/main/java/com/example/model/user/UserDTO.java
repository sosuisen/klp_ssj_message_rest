package com.example.model.user;

import com.example.model.validator.CreateChecks;
import com.example.model.validator.UniqueName;
import com.example.model.validator.ValidRole;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserDTO {
	public static final int MIN_NAME_LENGTH = 3;
	public static final int MAX_NAME_LENGTH = 30;
	public static final String PASSWORD_REGEX = "^[0-9a-zA-Z_\\-#\\^\\$%&@\\+\\*\\?]+$";
	public static final int MIN_PASSWORD_LENGTH = 8;
	public static final int MAX_PASSWORD_LENGTH = 16;

	@Pattern(regexp = "[a-zA-Z0-9]+", message = "{user.name.Pattern}", groups = CreateChecks.class)
	@NotBlank(message = "{user.name.NotBlank}", groups = CreateChecks.class)
	@Size(min = MIN_NAME_LENGTH, max = MAX_NAME_LENGTH, message = "{user.name.Size}", groups = CreateChecks.class)
	@UniqueName(message = "{user.name.UniqueName}", groups = CreateChecks.class)
	private String name;
	
	@NotBlank(message = "{user.role.NotBlank}")
	@ValidRole(message = "{user.role.ValidRole}")
	private String role;
	
	@NotBlank(message = "{user.password.NotBlank}", groups = CreateChecks.class)
	@Size(min = MIN_PASSWORD_LENGTH, max = MAX_PASSWORD_LENGTH, message = "{user.password.Size}", groups = CreateChecks.class)
	@Pattern(regexp = PASSWORD_REGEX, message = "{user.password.Pattern}", groups = CreateChecks.class)
	private String password;
}

