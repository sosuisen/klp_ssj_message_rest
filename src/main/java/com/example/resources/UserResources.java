package com.example.resources;

import java.sql.SQLException;
import java.util.logging.Level;

import com.example.auth.IdentityStoreConfig;
import com.example.model.user.UserDTO;
import com.example.model.user.UsersDAO;
import com.example.model.validator.CreateChecks;

import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.security.enterprise.identitystore.Pbkdf2PasswordHash;
import jakarta.validation.Valid;
import jakarta.validation.groups.ConvertGroup;
import jakarta.ws.rs.BeanParam;
import jakarta.ws.rs.POST;
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

	@Inject
	public UserResources(UsersDAO usersDAO, Pbkdf2PasswordHash passwordHash) {
		this.usersDAO = usersDAO;
		this.passwordHash = passwordHash;
		passwordHash.initialize(IdentityStoreConfig.HASH_PARAMS);
	}
	
	@POST
	public UserDTO createUser(@Valid @ConvertGroup(to = CreateChecks.class) @BeanParam UserDTO user) {
		try {
			return usersDAO.create(user);
		} catch (SQLException e) {
			log.log(Level.SEVERE, "Error in postUser()", e);
			throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
		}
	}

}
