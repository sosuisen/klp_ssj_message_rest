package com.example.model.message;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
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
	private int id;
	private String name;
	@NotBlank(message = "{message.NotBlank}")
	@Size(max = 140, message = "{message.Size}")
	private String message;
}