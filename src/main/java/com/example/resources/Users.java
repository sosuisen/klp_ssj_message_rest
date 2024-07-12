package com.example.resources;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;

import com.example.auth.IdentityStoreConfig;
import com.example.model.user.UserDTO;
import com.example.model.user.UsersDAO;
import com.example.model.validator.CreateChecks;
import com.example.resources.ConstraintViolationExceptionMapper.ErrorResponse;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.security.PermitAll;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.mvc.MvcContext;
import jakarta.security.enterprise.identitystore.Pbkdf2PasswordHash;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.groups.ConvertGroup;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.ForbiddenException;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
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
@Path("/api/users")
public class Users {
	private final UsersDAO usersDAO;
	private final HttpServletRequest req;
	private final MvcContext mvcContext;
	private final ServletContext servletContext;
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
	
	// POST /api/users
	@POST
	public UserDTO postUser(@Valid @ConvertGroup(to = CreateChecks.class) UserDTO user) throws SQLException {
		checkCsrf();			
		var hash = passwordHash.generate(user.getPassword().toCharArray());
		user.setPassword(hash);
		return usersDAO.create(user);		
	}

	// DELETE /api/users/{id}
	@DELETE
	@Path("{id}")
	public String deleteUser(@FormParam("name") String name) throws SQLException {
		checkCsrf();			
		usersDAO.delete(name);
		return "redirect:users";
	}

	// PUT /api/users/{id}
	@PUT
	@Path("{id}")
	public Response updateUser(@Valid UserDTO user) throws SQLException {
		checkCsrf();
		if (!user.getPassword().isEmpty()) {
			var pattern = java.util.regex.Pattern.compile(UserDTO.PASSWORD_REGEX);
			if (!pattern.matcher(user.getPassword()).matches()) {
				var response = new ErrorResponse("error", List.of(getValidationMessage("user.password.Pattern")));
				// パラメータの検証エラーの場合、400 Bad Request を返すのが一般的です。
				return Response.status(Response.Status.BAD_REQUEST).entity(response).build();
			}

			if (!(user.getPassword().length() >= UserDTO.MIN_PASSWORD_LENGTH
					&& user.getPassword().length() <= UserDTO.MAX_PASSWORD_LENGTH)) {
				var response = new ErrorResponse("error", 
						List.of(getValidationMessage("user.password.Size")
								.replace("{min}", String.valueOf(UserDTO.MIN_PASSWORD_LENGTH))
								.replace("{max}", String.valueOf(UserDTO.MAX_PASSWORD_LENGTH))));
				return Response.status(Response.Status.BAD_REQUEST).entity(response).build();
			}
			var hash = passwordHash.generate(user.getPassword().toCharArray());
			user.setPassword(hash);

		}		
		var updatedUser = usersDAO.update(user);
		return Response.ok(updatedUser).build();
	}

	private String getValidationMessage(String key) {
		var properties = new Properties();
		try (InputStream resourceStream = servletContext
				.getResourceAsStream("/WEB-INF/classes/ValidationMessages.properties");) {
			properties.load(resourceStream);
		} catch (IOException e) {
			e.printStackTrace();
		}
		String message = properties.getProperty(key);
		var utf8Message = new String(message.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
		return utf8Message;
	}
}
