package com.example.model.validator;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.example.auth.IdentityStoreConfig;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

@ApplicationScoped
public class NameValidator implements ConstraintValidator<ValidName, String> {
	// JAX-RSではなぜか注入できない。迂回策として通常のJDBCドライバを用いて接続
	// @Resource(lookup = "jdbc/__default")
	// private DataSource ds;
	
	public NameValidator() {
		try {
			Class.forName("org.h2.Driver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean isValid(String name, ConstraintValidatorContext context) {
		boolean nameExists = false;
		try (
				// Connection conn = ds.getConnection();
				Connection conn = DriverManager.getConnection(IdentityStoreConfig.jdbc, "ssj", "ssj");
				PreparedStatement pstmt = conn.prepareStatement("SELECT COUNT(*) FROM users WHERE name=?");) {
			pstmt.setString(1, name);
			ResultSet rs = pstmt.executeQuery();
			rs.next();
			int count = rs.getInt(1);
			if (count > 0)
				nameExists = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return nameExists;
	}
}