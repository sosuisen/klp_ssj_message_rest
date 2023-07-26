package com.example.model.user;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.sql.DataSource;

import jakarta.annotation.Resource;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.NotFoundException;

/**
 * DAO for users table
 */
@ApplicationScoped
public class UsersDAO {
	/**
	 * JNDIで管理されたDataSourceオブジェクトは@Resourceアノテーションで
	 * 取得できます。lookup属性でJNDI名を渡します。
	 */
	@Resource(lookup = "jdbc/__default")
	private DataSource ds;

	public ArrayList<UserDTO> getAll() throws SQLException {
		ArrayList<UserDTO> list = new ArrayList<>();
		try (
				Connection conn = ds.getConnection();
				PreparedStatement pstmt = conn.prepareStatement("SELECT name, role FROM users");) {
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				list.add(new UserDTO(
						rs.getString("name"),
						rs.getString("role"),
						""));
			}
		}
		return list;
	}

	public UserDTO get(String name) throws SQLException, NotFoundException {
		try (
				Connection conn = ds.getConnection();
				PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM users WHERE name=?");) {
			pstmt.setString(1, name);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next())
				return new UserDTO(rs.getString("name"), rs.getString("role"), rs.getString("password"));
		}
		throw new NotFoundException();
	}

	public UserDTO create(UserDTO userDTO) throws SQLException {
		try (
				Connection conn = ds.getConnection();
				PreparedStatement pstmt = conn
						.prepareStatement("INSERT INTO users VALUES(?, ?, ?)");) {
			pstmt.setString(1, userDTO.getName());
			pstmt.setString(2, userDTO.getRole());
			pstmt.setString(3, userDTO.getPassword());
			pstmt.executeUpdate();

			return new UserDTO(userDTO.getName(), userDTO.getRole(), userDTO.getPassword());
		}
	}

	public void deleteAll() throws SQLException {
		try (
				Connection conn = ds.getConnection();
				PreparedStatement pstmt = conn.prepareStatement("DELETE FROM users");) {
			pstmt.executeUpdate();
		}
	}

	public void delete(String name) throws SQLException, NotFoundException {
		try (
				Connection conn = ds.getConnection();
				PreparedStatement pstmt = conn.prepareStatement("DELETE FROM users WHERE name=?");) {
			pstmt.setString(1, name);
			int num = pstmt.executeUpdate();
			if (num <= 0) {
				throw new NotFoundException();
			}
		}
	}

	public void update(UserDTO userDTO) throws SQLException, NotFoundException {
		try (
				Connection conn = ds.getConnection();) {
			int num = 0;
			if (userDTO.getPassword().isEmpty())
				try (PreparedStatement pstmt = conn.prepareStatement("UPDATE users SET role=? where name=?");) {
					pstmt.setString(1, userDTO.getRole());
					pstmt.setString(2, userDTO.getName());
					num = pstmt.executeUpdate();
				}
			else
				try (PreparedStatement pstmt = conn
						.prepareStatement("UPDATE users SET role=?,password=? where name=?");) {
					pstmt.setString(1, userDTO.getRole());
					pstmt.setString(2, userDTO.getPassword());
					pstmt.setString(3, userDTO.getName());
					num = pstmt.executeUpdate();
				}
			if (num <= 0) {
				throw new NotFoundException();
			}
		}
	}
}
