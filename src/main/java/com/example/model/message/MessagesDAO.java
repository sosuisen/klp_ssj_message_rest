package com.example.model.message;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import javax.sql.DataSource;

import jakarta.annotation.Resource;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.NotFoundException;
import lombok.NoArgsConstructor;

/**
 * DAO for messages table
 */
@ApplicationScoped
@NoArgsConstructor(force = true)
public class MessagesDAO {
	/**
	 * JNDIで管理されたDataSourceオブジェクトは@Resourceアノテーションで
	 * 取得できます。lookup属性でJNDI名を渡します。
	 */
	@Resource(lookup = "jdbc/__default")
	private DataSource ds;

	public ArrayList<MessageDTO> getAll() throws SQLException {
		ArrayList<MessageDTO> list = new ArrayList<>();
		try (
				Connection conn = ds.getConnection();
				PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM messages");) {
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				list.add(new MessageDTO(
						rs.getInt("id"),
						rs.getString("name"),
						rs.getString("message")));
			}
		}
		return list;
	}

	public MessageDTO get(int id) throws SQLException, NotFoundException {
		try (
				Connection conn = ds.getConnection();
				PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM messages WHERE id=?");) {
			pstmt.setInt(1, id);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				return new MessageDTO(
						rs.getInt("id"),
						rs.getString("name"),
						rs.getString("message"));
			}
		}
		throw new NotFoundException();
	}

	public ArrayList<MessageDTO> search(String keyword) throws SQLException {
		ArrayList<MessageDTO> list = new ArrayList<>();
		try (
				Connection conn = ds.getConnection();
				PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM messages WHERE message LIKE ?");) {
			// 部分一致で検索する場合、LIKEの後には　%キーワード%
			// この%はバインド時に与える必要があります。
			pstmt.setString(1, "%" + keyword + "%");
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				list.add(new MessageDTO(
						rs.getInt("id"),
						rs.getString("name"),
						rs.getString("message")));
			}
		}
		return list;
	}

	public MessageDTO create(MessageDTO mesDTO) throws SQLException {
		try (
				Connection conn = ds.getConnection();
				PreparedStatement pstmt = conn
						.prepareStatement("INSERT INTO messages(name, message) VALUES(?, ?)",
								Statement.RETURN_GENERATED_KEYS);) {
			pstmt.setString(1, mesDTO.getName());
			pstmt.setString(2, mesDTO.getMessage());
			pstmt.executeUpdate();

			// AUTOINCREMENTで生成された id を取得します。
			ResultSet rs = pstmt.getGeneratedKeys();
			rs.next();
			int id = rs.getInt(1);

			return new MessageDTO(id, mesDTO.getName(), mesDTO.getMessage());
		}
	}

	public void deleteAll() throws SQLException {
		try (
				Connection conn = ds.getConnection();
				PreparedStatement pstmt = conn.prepareStatement("DELETE FROM messages");) {
			pstmt.executeUpdate();
		}
	}

	public void delete(int id) throws SQLException, NotFoundException {
		try (
				Connection conn = ds.getConnection();
				PreparedStatement pstmt = conn.prepareStatement("DELETE FROM messages WHERE id=?");) {
			pstmt.setInt(1, id);
			int num = pstmt.executeUpdate();
			if (num <= 0) {
				throw new NotFoundException();
			}
		}
	}
	
	public void updateMessage(MessageDTO mesDTO) throws SQLException, NotFoundException {
		try (
				Connection conn = ds.getConnection();
				PreparedStatement pstmt = conn
						.prepareStatement("UPDATE messages SET message=? where id=?");) {
			pstmt.setString(1, mesDTO.getMessage());
			pstmt.setInt(2, mesDTO.getId());
			int num = pstmt.executeUpdate();
			if (num <= 0) {
				throw new NotFoundException();
			}
		}
	}
}
