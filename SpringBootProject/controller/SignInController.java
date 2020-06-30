package jb.dam2.discover.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import jb.dam2.discover.pojo.Session;
import jb.dam2.discover.pojo.User;
import jb.dam2.discover.service.ISessionService;
import jb.dam2.discover.service.IUserService;

/*JB:
 * Controlador para el incio de sesion
 */
@RestController
public class SignInController {
		
		@Autowired
		private IUserService userService;
		@Autowired
		private ISessionService sessionService;
		
		//JB: Se gestiona el inicio de sesion
		//Si la contrasenya no es igual, resulta en un 500 (internal error), no en un 400 (response)
		@RequestMapping(value = "/signin",method = RequestMethod.POST)
		public ResponseEntity<Session> signIn (@RequestBody User user)
		{
			HttpStatus status = HttpStatus.CREATED;
			Optional<Session> session = Optional.empty();
			if (userService.verifyUser(user.getUsername(), user.getPassword()).isPresent()) {
				if(!sessionService.exists(user)) {
					sessionService.save(Session.builder().user(user).build());
				}
				session = sessionService.findByUser(user);
				
				status = HttpStatus.OK;

			}else {
				status = HttpStatus.BAD_REQUEST;
			}
				
			return new ResponseEntity<Session>(session.get(),status);
		}
}
