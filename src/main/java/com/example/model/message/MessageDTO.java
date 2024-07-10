package com.example.model.message;

import jakarta.mvc.binding.MvcBinding;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.ws.rs.FormParam;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * メッセージ情報の受け渡しに用いるDTO（Data Transfer Object）です。
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class MessageDTO {
	@FormParam("id")
	private int id;
	@FormParam("name")
	private String name;

	@MvcBinding
	@NotBlank(message = "{message.NotBlank}")
	@Size(max = 140, message = "{message.Size}")
	@FormParam("message")
	private String message;
}