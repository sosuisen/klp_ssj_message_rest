package com.example.model.user;

import com.example.model.validator.CreateChecks;
import com.example.model.validator.UniqueName;
import com.example.model.validator.UpdateChecks;
import com.example.model.validator.ValidPassword;
import com.example.model.validator.ValidRole;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import jakarta.ws.rs.FormParam;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DAOにおけるデータの詰め替えや、
 * @BeanParam の注入先となるクラスです。
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserDTO {
	public static final int MIN_NAME_LENGTH = 3;
	public static final int MAX_NAME_LENGTH = 30;
	public static final String PASSWORD_REGEX = "^[0-9a-zA-Z_\\-#\\^\\$%&@\\+\\*\\?]+$";
	public static final int MIN_PASSWORD_LENGTH = 8;
	public static final int MAX_PASSWORD_LENGTH = 16;

	@NotBlank(message = "{user.name.NotBlank}", groups = CreateChecks.class)
	@Size(min = MIN_NAME_LENGTH, max = MAX_NAME_LENGTH, message = "{user.name.Size}", groups = CreateChecks.class)
	@Pattern(regexp = "a-zA-Z", message = "{user.name.Pattern}", groups = CreateChecks.class)
	@UniqueName(message = "{user.name.UniqueName}", groups = CreateChecks.class)
	@FormParam("name")
	private String name;

	@NotBlank(message = "{user.role.NotBlank}")
	@ValidRole(message = "{user.role.ValidRole}")
	@FormParam("role")
	private String role;

	@NotBlank(message = "{user.password.NotBlank}", groups = CreateChecks.class)
	@Size(min = MIN_PASSWORD_LENGTH, max = MAX_PASSWORD_LENGTH, message = "{user.password.Size}", groups = CreateChecks.class)
	@Pattern(regexp = PASSWORD_REGEX, message = "{user.password.Pattern}", groups = CreateChecks.class)
	@ValidPassword(message = "{user.passwordValidPassword}", groups = UpdateChecks.class)
	@FormParam("password")
	private String password;

	/**
	 * 複数のフィールド間の相関関係は@AssertTrueでチェックできます。
	 */
	@AssertTrue(message = "{assert.NameMustNotIncludeAdminIfNotAdminRole}")
	public boolean isNameMustNotIncludeAdminIfNotAdminRole() {
		var pattern = java.util.regex.Pattern.compile("ADMIN", java.util.regex.Pattern.CASE_INSENSITIVE);
		if (!role.equals("ADMIN") && pattern.matcher(name).find())
			return false;

		return true;
	}
}
