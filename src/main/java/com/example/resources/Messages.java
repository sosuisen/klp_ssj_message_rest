package com.example.resources;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;

import com.example.controller.ForbiddenExceptionMapper;
import com.example.model.message.MessageDTO;
import com.example.model.message.MessageForm;
import com.example.model.message.MessagesDAO;
import com.example.model.validator.CreateChecks;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.mvc.Models;
import jakarta.mvc.MvcContext;
import jakarta.mvc.binding.BindingResult;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.groups.ConvertGroup;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.ForbiddenException;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;

/**
 * JAX-RSのリソースクラスです。
 */
@RequestScoped
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@NoArgsConstructor(force = true)
@RequiredArgsConstructor(onConstructor_ = @Inject)
@PermitAll
@Log
@Path("/api/messages")
public class Messages {
	private final Models models;
	private final MessagesDAO messagesDAO;
	private final HttpServletRequest req;
	private final BindingResult bindingResult;
	private final MessageForm messageForm;
	private final MvcContext mvcContext;
	
	@PostConstruct
	public void afterInit() {
		log.log(Level.INFO, "[user]%s, [ip]%s [url]%s".formatted(
				req.getRemoteUser(),
				req.getRemoteAddr(),
				req.getRequestURL().toString()));
	}

	/**
	 * 以下は要認証（web.xmlで設定）
	 */
	// GET /api/messages
	@GET
	public ArrayList<MessageDTO> getMessages(@QueryParam("keyword") String keyword) throws SQLException {
		if (keyword == null) {
			return messagesDAO.getAll();
		}
		else {
			return messagesDAO.search(keyword);
		}
	}
	
	private void checkCsrf() {
		var csrf = req.getHeader("X-CSRF-Token");
		if (csrf == null || !csrf.equals(mvcContext.getCsrf().getToken())) {
            throw new ForbiddenException(ForbiddenExceptionMapper.FROM_API);
        }
	}
	
	// POST /api/messages
	@POST
	public MessageDTO postMessage(@Valid @ConvertGroup(to = CreateChecks.class) MessageDTO mes) throws SQLException {
		checkCsrf();
		return messagesDAO.create(mes);
	}

	// DELETE /api/messages
	@DELETE
	@RolesAllowed("ADMIN")
	public void deleteMessages() throws SQLException {
		messagesDAO.deleteAll();
	}
}
