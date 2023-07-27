package com.example.resources;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;

import com.example.auth.IdentityStoreConfig;
import com.example.model.user.UserDTO;
import com.example.model.user.UsersDAO;
import com.example.model.validator.CreateChecks;

import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.security.enterprise.identitystore.Pbkdf2PasswordHash;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.groups.ConvertGroup;
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
	}
	
	@GET
	public ArrayList<UserDTO> getUsers() {
		try {
			return usersDAO.getAll();
		} catch (SQLException e) {
			log.log(Level.SEVERE, "Error in getUsers()", e);
			throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
		}
	}
	
	@GET
	@Path("{name}")
	public UserDTO getUser(@PathParam("name") String name) {
		try {
			return usersDAO.get(name);
		} catch (SQLException e) {
			log.log(Level.SEVERE, "Error in getUser()", e);
			throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
		} catch (NotFoundException e) {
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		}
	}
	
	@POST
	public UserDTO postUser(@Valid @ConvertGroup(to = CreateChecks.class) @BeanParam UserDTO user) {
		try {
			return usersDAO.create(user);
		} catch (SQLException e) {
			log.log(Level.SEVERE, "Error in postUser()", e);
			throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
		}
	}
	
	@PUT
	@Path("{name}")
	public UserDTO putUser(@PathParam("name") String name, @Valid @BeanParam UserDTO user) {
		try {
			user.setName(name);
			usersDAO.update(user);
			return usersDAO.get(name);
		} catch (SQLException e) {
			log.log(Level.SEVERE, "Error in putUser()", e);
			throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
		} catch (NotFoundException e) {
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		}
	}
	
	@DELETE
	public void deleteAllUsers() {
		try {
			usersDAO.deleteAll();
		} catch (SQLException e) {
			log.log(Level.SEVERE, "Error in deleteAllUsers()", e);
			throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
		}
	}
	
	@DELETE
	@Path("{name}")
	public void deleteUser(@PathParam("name") String name) {
		try {
			usersDAO.delete(name);
		} catch (SQLException e) {
			log.log(Level.SEVERE, "Error in deleteUser()", e);
			throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
		} catch (NotFoundException e) {
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		}
	}
}
