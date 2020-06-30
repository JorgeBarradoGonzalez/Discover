package jb.dam2.discover.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jb.dam2.discover.pojo.Session;
import jb.dam2.discover.pojo.Share;
import jb.dam2.discover.pojo.User;
import jb.dam2.discover.service.ISessionService;
import jb.dam2.discover.service.IShareService;
import jb.dam2.discover.service.IUserFollowingService;
import jb.dam2.discover.service.IUserService;
import jb.dam2.discover.wrapper.ShareWrapper;
import jb.dam2.discover.wrapper.UserWrapper;

/*JB:
 * Controlador para la gestion de usuarios
 */
@RestController
@RequestMapping(value = "/users")
public class UserController {
	
	@Autowired
	private IUserService userService;
	@Autowired
	private ISessionService sessionService;
	@Autowired
	private IUserFollowingService userFollowingService;
	
	@GetMapping(value="/all") 
	public List<User> getAllCustomer(){return userService.findAll();}
	
	//JB: Se busca un usuario
	@GetMapping("/finduser")
	public ResponseEntity<Map<String, String>> findUser (@RequestParam("sessionId") String sessionId,@RequestParam("username") String username)
	{
		
		HttpStatus status = HttpStatus.CREATED;
		Map<String, String> isFollowingInfo = new HashMap<String, String>();
		
		Optional<User> sessionUser = sessionService.findUserBySessionId(sessionId);
		Optional<User> isFolloweduser = userService.findByUsername(username);
		
		if(isFolloweduser.isPresent()) {
			//si no existe un registro con estos 2 usuarios
			if(!userFollowingService.isFollowing(isFolloweduser.get(), sessionUser.get())) {
				isFollowingInfo.put("isFollowing", "unfollow");
				status = HttpStatus.OK;
			}else {
				isFollowingInfo.put("isFollowing", "follow");
			}
		}else {
			status = HttpStatus.BAD_REQUEST;
		}
			
		return new ResponseEntity<Map<String, String>>(isFollowingInfo,status);
	}
	

	//JB: Se buscan los usuarios seguidos por un usuario
	@GetMapping("/followed/{username}")
	public ResponseEntity<UserWrapper> getFollowedUsers (@PathVariable("username") String username)
	{
		
		HttpStatus status = HttpStatus.CREATED;
		List<User> usersFollowedList = userService.findFollowedUsers(username);
		UserWrapper usersFollowed = new UserWrapper(usersFollowedList);
		
		if(!usersFollowed.getUsers().isEmpty()) {
			status = HttpStatus.OK;
		}else {
			status = HttpStatus.BAD_REQUEST;
		}
			
		return new ResponseEntity<UserWrapper>(usersFollowed,status);
	}
	
	//JB: Se buscan los usuarios seguidos por el usuario del dispositivo
	@GetMapping("/followed/self/{sessionId}")
	public ResponseEntity<UserWrapper> getFollowedUsersSelf (@PathVariable("sessionId") String sessionId)
	{
		
		HttpStatus status = HttpStatus.CREATED;
		User user = sessionService.findUserBySessionId(sessionId).get();
		List<User> usersFollowedList = userService.findFollowedUsers(user.getUsername());
		UserWrapper usersFollowed = new UserWrapper(usersFollowedList);
		
		if(!usersFollowed.getUsers().isEmpty()) {
			status = HttpStatus.OK;
		}else {
			status = HttpStatus.BAD_REQUEST;
		}
			
		return new ResponseEntity<UserWrapper>(usersFollowed,status);
	}
	
	//JB: Se busca si el usuario buscado el el usuario del dispositivo
	@GetMapping("/isUser")
	public ResponseEntity<Map<String, String>> isSessionUser (@RequestParam("sessionId") String sessionId,@RequestParam("username") String username)
	{
		
		HttpStatus status = HttpStatus.CREATED;
		Map<String, String> isUserInfo = new HashMap<String, String>();
		
		Optional<User> sessionUser = sessionService.findUserBySessionId(sessionId);
		Optional<User> isThisUser = userService.findByUsername(username);
		
		if(!sessionUser.get().getUsername().equals(isThisUser.get().getUsername())) {
			status = HttpStatus.OK;
			isUserInfo.put("isUser", "false");
		}else {
			status = HttpStatus.OK;
			isUserInfo.put("isUser", "true");
		}
			
		return new ResponseEntity<Map<String, String>>(isUserInfo,status);
	}
	
	
	
}
