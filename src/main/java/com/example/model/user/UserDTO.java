package com.example.model.user;

import com.example.model.validator.CreateChecks;
import com.example.model.validator.UniqueName;
import com.example.model.validator.ValidRole;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserDTO {
	// @Sizeと@Patternで網羅できるため@NotBlankは不要
	@Size(min = 3, max = 30, message = "{user.name.Size}", groups = CreateChecks.class)
	@Pattern(regexp = "^[a-zA-Z0-9]*$", message = "{user.name.Pattern}", groups = CreateChecks.class)
	@UniqueName(message = "{user.name.UniqueName}", groups = CreateChecks.class)
	private String name;
	
	// @ValidRoleで対応できるため@NotBlankは不要
	@ValidRole(message = "{user.role.ValidRole}")
	private String role;
	
	// @Sizeと@Patternで網羅できるため@NotBlankは不要
	// 文字数の制約はPOSTのとき（つまりCreateChecksグループ指定のとき）のみ
	@Size(min = 8, max = 16, message = "{user.password.Size}", groups = CreateChecks.class)
	@Pattern(regexp = "^[0-9a-zA-Z_\\-#\\^\\$%&@\\+\\*\\?]*$", message = "{user.password.Pattern}")
	private String password;
}
