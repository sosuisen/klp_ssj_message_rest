package com.example.resources;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;

import com.example.auth.IdentityStoreConfig;
import com.example.model.user.UserDTO;
import com.example.model.user.UsersDAO;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.mvc.MvcContext;
import jakarta.security.enterprise.identitystore.Pbkdf2PasswordHash;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.ForbiddenException;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
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
// @RolesAllowed("ADMIN")
@Log
@Path("/api/users")
public class Users {
	private final UsersDAO usersDAO;
	private final HttpServletRequest req;
	private final MvcContext mvcContext;
	private final Pbkdf2PasswordHash passwordHash;

	@PostConstruct
	public void afterInit() {
		passwordHash.initialize(IdentityStoreConfig.getHashParams());
		log.log(Level.INFO, "[method]%s, [user]%s, [ip]%s [url]%s".formatted(
				req.getMethod(),
				req.getRemoteUser(),
				req.getRemoteAddr(),
				req.getRequestURL().toString()));
	}
	
	private void checkCsrf() {
		var csrf = req.getHeader("X-CSRF-Token");
		if (csrf == null || !csrf.equals(mvcContext.getCsrf().getToken())) {
	           throw new ForbiddenException();
       }
	}
	
	// GET /api/users
	@GET
	public ArrayList<UserDTO> getUser() throws SQLException {
		checkCsrf();			
		return usersDAO.getAll();
	}

}
