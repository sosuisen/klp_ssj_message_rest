package com.example.model.message;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

import javax.sql.DataSource;

import jakarta.annotation.Resource;
import jakarta.enterprise.context.ApplicationScoped;
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

	public ArrayList<MessageDTO> getAll() {
		ArrayList<MessageDTO> list = new ArrayList<>();
		try (
				Connection conn = ds.getConnection();
				PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM messages");) {
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				int myId = rs.getInt("id");
				String name = rs.getString("name");
				String message = rs.getString("message");
				list.add(new MessageDTO(myId, name, message));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	public ArrayList<MessageDTO> search(String keyword) {
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
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	public MessageDTO create(MessageDTO mesDTO) {
		MessageDTO mes = null;
		try (
				Connection conn = ds.getConnection();
				PreparedStatement pstmt = conn
						.prepareStatement("INSERT INTO messages(name, message) VALUES(?, ?)", Statement.RETURN_GENERATED_KEYS);		
				) {
			pstmt.setString(1, mesDTO.getName());
			pstmt.setString(2, mesDTO.getMessage());
			pstmt.executeUpdate();
			
			// AUTOINCREMENTで生成された id を取得します。
			ResultSet rs = pstmt.getGeneratedKeys();
			rs.next();
			int id = rs.getInt(1);

			mes = new MessageDTO(id, mesDTO.getName(), mesDTO.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return mes;
	}

	public boolean deleteAll() {
		try (
				Connection conn = ds.getConnection();
				PreparedStatement pstmt = conn.prepareStatement("DELETE from messages");) {
			int num = pstmt.executeUpdate();
			if (num <= 0) {
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}
}
