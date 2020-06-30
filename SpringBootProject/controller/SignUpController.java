package jb.dam2.discover.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import jb.dam2.discover.pojo.User;
import jb.dam2.discover.service.IUserService;

/*JB:
 * Controlador para el registro
 */
@RestController
public class SignUpController {
		
		@Autowired
		private IUserService userService;
		
		//JB: Se gestiona el registro
		@RequestMapping(value = "/signup",method = RequestMethod.POST)
		public ResponseEntity<User> signUp (@RequestBody User user)
		{
			
			HttpStatus status = HttpStatus.CREATED;
			
			if (!userService.exists(user.getUsername())) {
				
				userService.manageSignUp(user);
				
				status = HttpStatus.OK;

			}else {
				status = HttpStatus.BAD_REQUEST;
			}
				
			return new ResponseEntity<User>(user,status);
		}
}
