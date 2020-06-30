package jb.dam2.discover.controller;

import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import jb.dam2.discover.pojo.UserFollowing;
import jb.dam2.discover.service.UserFollowingService;

/*JB:
 * Controlador para los seguimientos de usuario
 */
@RestController
public class FollowController {
	
	@Autowired
	UserFollowingService userFollowingService;
	
	//http://192.168.56.1:8080/follow
	
	//JB: Se sigue a un usuario
	@RequestMapping(value = "/follow", method = RequestMethod.POST)
	public ResponseEntity<UserFollowing> follow(@RequestBody Map<String, String> followInfo) {

		HttpStatus status = HttpStatus.CREATED;
		String userFollowingSessionId = followInfo.get("sessionId");
		String userFollowedUsername = followInfo.get("username");
		UserFollowing userFollowing= userFollowingService.manageFollow(userFollowingSessionId, userFollowedUsername);
		status = HttpStatus.OK;
		return new ResponseEntity<UserFollowing>(userFollowing,status);
	}
	
	//JB: Se deja de seguir a un usuario
	@RequestMapping(value = "/unfollow", method = RequestMethod.DELETE)
	public ResponseEntity<UserFollowing> unfollow(@RequestHeader("sessionId") String sessionId,@RequestHeader("username") String username) {

		HttpStatus status = HttpStatus.CREATED;
		userFollowingService.manageUnfollow(sessionId, username);
		status = HttpStatus.OK;

		return new ResponseEntity<UserFollowing>(new UserFollowing(),status);
	}
}
