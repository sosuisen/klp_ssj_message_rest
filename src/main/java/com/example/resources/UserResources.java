package com.example.resources;

import com.example.auth.IdentityStoreConfig;
import com.example.model.user.UsersDAO;

import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.security.enterprise.identitystore.Pbkdf2PasswordHash;
import jakarta.ws.rs.Path;
import lombok.NoArgsConstructor;

@RequestScoped
@NoArgsConstructor(force = true)
@RolesAllowed("ADMIN")
@Path("/")
public class UserResources {
	private final UsersDAO usersDAO;
	private final Pbkdf2PasswordHash passwordHash;

	@Inject
	public UserResources(UsersDAO usersDAO, Pbkdf2PasswordHash passwordHash) {
		this.usersDAO = usersDAO;
		this.passwordHash = passwordHash;
		passwordHash.initialize(IdentityStoreConfig.HASH_PARAMS);
	}
}
