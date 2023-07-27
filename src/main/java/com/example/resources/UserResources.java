package com.example.resources;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;

import com.example.auth.IdentityStoreConfig;
import com.example.model.user.UserRecord;
import com.example.model.user.UsersDAO;

import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.security.enterprise.identitystore.Pbkdf2PasswordHash;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.NoArgsConstructor;
import lombok.extern.java.Log;

@RequestScoped
@Produces(MediaType.APPLICATION_JSON)
@NoArgsConstructor(force = true)
@Log
@RolesAllowed("ADMIN")
@Path("/users")
public class UserResources {
	private final UsersDAO usersDAO;
	private final Pbkdf2PasswordHash passwordHash;
	private final HttpServletRequest req;

	@Inject
	public UserResources(UsersDAO usersDAO, Pbkdf2PasswordHash passwordHash, HttpServletRequest req) {
		this.usersDAO = usersDAO;
		this.passwordHash = passwordHash;
		passwordHash.initialize(IdentityStoreConfig.HASH_PARAMS);
		this.req = req;
		log.log(Level.INFO, "[user]%s [ip]%s [url]%s".formatted(
				req.getRemoteUser(),
				req.getRemoteAddr(),
				req.getRequestURL().toString()));		
	}
	
	@GET
	public ArrayList<UserRecord> getUsers() {
		try {
			return usersDAO.getAll();
		} catch (SQLException e) {
			log.log(Level.SEVERE, "Error in getUsers()", e);
			throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * 基本課題はこちらのコメントアウトを外して修正
	@GET
	@Path("{name}")
	public UserRecord getUser(@PathParam("name") String name) {


	}
	*/
	
	// 以下は発展課題

	
}
