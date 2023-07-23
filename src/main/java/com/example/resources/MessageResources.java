package com.example.resources;

import java.util.ArrayList;

import com.example.model.message.MessageDTO;
import com.example.model.message.MessagesDAO;

import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.BeanParam;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
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
@Path("/")
public class MessageResources {
	private final MessagesDAO messagesDAO;
	
	@Inject
	public MessageResources(MessagesDAO messagesDAO) {
		this.messagesDAO = messagesDAO;
	}

	@GET
	@Path("list")
	@Produces(MediaType.APPLICATION_JSON)
	public ArrayList<MessageDTO> getMessages() {
		return messagesDAO.getAll();
	}

	@POST
	@Path("list")
	@Produces(MediaType.APPLICATION_JSON)
	public MessageDTO postMessage(@Valid @BeanParam MessageDTO mes) {
		return messagesDAO.create(mes);
	}

	@POST
	@Path("clear")
	@RolesAllowed("ADMIN")
	@Produces(MediaType.APPLICATION_JSON)
	public boolean clearMessages() {
		return messagesDAO.deleteAll();
	}

	@POST
	@Path("search")
	@Produces(MediaType.APPLICATION_JSON)
	public ArrayList<MessageDTO> searchMessages(@FormParam("keyword") String keyword) {
		return messagesDAO.search(keyword);
	}

}
