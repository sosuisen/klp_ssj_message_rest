package com.example.resources;

import java.sql.SQLException;
import java.util.ArrayList;

import com.example.model.message.MessageDTO;
import com.example.model.message.MessagesDAO;

import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.BeanParam;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.NoArgsConstructor;

/**
 * JAX-RSのリソースクラスです。
 * 
 * @RequestScopedを付けてCDI Beanにしておくと、
 * 他のCDI Beanを注入できます。
 * 
 * CDI beanには引数のないコンストラクタが必須なので、
 * Lombokの@NoArgsConstructorで空っぽのコンストラクタを作成します。
 * ただし、このクラスは宣言時に初期化してないfinalフィールドを持つため、
 * このままだとフィールドが初期化されない可能性があってコンパイルエラーとなります。
 * これを防ぐには(force=true)指定が必要です。
 */
@RequestScoped
@NoArgsConstructor(force = true)
@PermitAll
@Path("/messages/")
@Produces(MediaType.APPLICATION_JSON)
public class MessageResources {
	private final MessagesDAO messagesDAO;

	@Inject
	public MessageResources(MessagesDAO messagesDAO) {
		this.messagesDAO = messagesDAO;
	}

	@GET
	public ArrayList<MessageDTO> getAllMessages() {
		try {
			return messagesDAO.getAll();
		} catch (SQLException e) {
			throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
		}
	}

	@GET
	@Path("{id}")
	public MessageDTO getMessage(@PathParam("id") int id) {
		try {
			return messagesDAO.get(id);
		} catch (SQLException e) {
			e.printStackTrace();
			throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
		} catch (NotFoundException e) {
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		}
	}

	@POST
	public MessageDTO postMessage(@Valid @BeanParam MessageDTO mes) {
		try {
			return messagesDAO.create(mes);
		} catch (SQLException e) {
			e.printStackTrace();
			throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
		}
	}

	@PUT
	public MessageDTO putMessage(@Valid @BeanParam MessageDTO mes) {
		try {
			messagesDAO.updateMessage(mes);
			return messagesDAO.get(mes.getId());
		} catch (SQLException e) {
			e.printStackTrace();
			throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
		} catch (NotFoundException e) {
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		}
	}

	@DELETE
	@RolesAllowed("ADMIN")
	public void deleteAllMessages() {
		try {
			messagesDAO.deleteAll();
		} catch (SQLException e) {
			e.printStackTrace();
			throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
		}
	}
	
	@DELETE
	@Path("{id}")
	@RolesAllowed("ADMIN")
	public void deleteMessage(@PathParam("id") int id) {
		try {
			messagesDAO.delete(id);
		} catch (SQLException e) {
			e.printStackTrace();
			throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
		} catch (NotFoundException e) {
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		}
	}
}
