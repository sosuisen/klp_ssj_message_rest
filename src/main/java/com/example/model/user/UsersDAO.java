package com.example.model.user;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import javax.sql.DataSource;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

/**
 * DAO for users table
 */
@ApplicationScoped
@NoArgsConstructor(force = true)
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class UsersDAO {
	private final DataSource ds;

	public ArrayList<UserDTO> getAll() throws SQLException {
		var usersModel = new ArrayList<UserDTO>();
		try (
				Connection conn = ds.getConnection();
				PreparedStatement pstmt = conn.prepareStatement("SELECT name, role FROM users");) {
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				usersModel.add(new UserDTO(rs.getString("name"), rs.getString("role"), ""));
			}
		}
		return usersModel;
	}

	public UserDTO get(String name) throws SQLException {
		try (
				Connection conn = ds.getConnection();
				PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM users WHERE name=?");) {
			pstmt.setString(1, name);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				return new UserDTO(rs.getString("name"), rs.getString("role"), rs.getString("password"));
			}
		}
		return null;
	}

	public UserDTO create(UserDTO userDTO) throws SQLException {
		try (
				Connection conn = ds.getConnection();
				PreparedStatement pstmt = conn
						.prepareStatement("INSERT INTO users VALUES(?, ?, ?)",
						Statement.RETURN_GENERATED_KEYS)) {
			pstmt.setString(1, userDTO.getName());
			pstmt.setString(2, userDTO.getRole());
			pstmt.setString(3, userDTO.getPassword());
			pstmt.executeUpdate();
			
			// AUTOINCREMENTで生成された id を取得します。
			ResultSet rs = pstmt.getGeneratedKeys();
			rs.next();
			userDTO.setName(rs.getString(1));
			userDTO.setPassword("");
			return userDTO;
		}
	}

	public void deleteAll() throws SQLException {
		try (
				Connection conn = ds.getConnection();
				PreparedStatement pstmt = conn.prepareStatement("DELETE FROM users");) {
			pstmt.executeUpdate();
		}
	}

	public void delete(String name) throws SQLException {
		try (
				Connection conn = ds.getConnection();
				PreparedStatement pstmt = conn.prepareStatement("DELETE FROM users WHERE name=?");) {
			pstmt.setString(1, name);
			pstmt.executeUpdate();
		}
	}

	public UserDTO update(UserDTO userDTO) throws SQLException {
		try (
				Connection conn = ds.getConnection()) {
			if (userDTO.getPassword().equals(""))
				try (PreparedStatement pstmt = conn.prepareStatement("UPDATE users SET role=? where name=?")) {
					pstmt.setString(1, userDTO.getRole());
					pstmt.setString(2, userDTO.getName());
					pstmt.executeUpdate();

				}
			else
				try (PreparedStatement pstmt = conn
						.prepareStatement("UPDATE users SET role=?,password=? where name=?")) {
					pstmt.setString(1, userDTO.getRole());
					pstmt.setString(2, userDTO.getPassword());
					pstmt.setString(3, userDTO.getName());
					pstmt.executeUpdate();
				}
			var updatedUser = get(userDTO.getName());
			updatedUser.setPassword("");
			return updatedUser;
		}
	}

}
