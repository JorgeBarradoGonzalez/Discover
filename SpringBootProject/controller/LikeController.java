package jb.dam2.discover.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jb.dam2.discover.pojo.Like;
import jb.dam2.discover.pojo.Share;
import jb.dam2.discover.pojo.User;
import jb.dam2.discover.service.ILikeService;
import jb.dam2.discover.service.ISessionService;
import jb.dam2.discover.service.IUserService;
import jb.dam2.discover.wrapper.LikeWrapper;
import jb.dam2.discover.wrapper.ShareWrapper;

/*JB:
 * Controlador de los likes
 */
@RestController
@RequestMapping(value = "/likes")
public class LikeController {
	@Autowired
	private ILikeService likeService;
	@Autowired
	private ISessionService sessionService;
	
	//JB: Se descargan los likes de un usuario
	@GetMapping("/user/{username}")
	public ResponseEntity<ShareWrapper> downloadUserLikes (@PathVariable("username") String username)
	{
		
		HttpStatus status = HttpStatus.CREATED;
		ShareWrapper shareWrapper = new ShareWrapper();
		List<Like> userLikes = likeService.findUserLikes(username);
		List<Share> sharesInLikes = new ArrayList<>();
		
		if(!userLikes.isEmpty()) {
			for (Like like : userLikes) {
				sharesInLikes.add(like.getShare());
			}
			shareWrapper.setShares(sharesInLikes);
			shareWrapper.sortByDate();
			shareWrapper.reverse();
			status = HttpStatus.OK;
		}else {
			status = HttpStatus.BAD_REQUEST;
		}
			
		return new ResponseEntity<ShareWrapper>(shareWrapper,status);
	}
	
	//JB: Se descargan los likes de un usuario si es el del dispositivo
	@GetMapping("/self/{sessionId}")
	public ResponseEntity<ShareWrapper> downloadSelfLikes (@PathVariable("sessionId") String sessionId)
	{
		
		HttpStatus status = HttpStatus.CREATED;
		ShareWrapper shareWrapper = new ShareWrapper();
		User user = sessionService.findUserBySessionId(sessionId).get();
		List<Like> userLikes = likeService.findUserLikes(user.getUsername());
		List<Share> sharesInLikes = new ArrayList<>();
		
		if(!userLikes.isEmpty()) {
			for (Like like : userLikes) {
				sharesInLikes.add(like.getShare());
			}
			shareWrapper.setShares(sharesInLikes);
			shareWrapper.sortByDate();
			shareWrapper.reverse();
			status = HttpStatus.OK;
		}else {
			status = HttpStatus.BAD_REQUEST;
		}
			
		return new ResponseEntity<ShareWrapper>(shareWrapper,status);
	}
	
	//JB: Se gestiona el like o dislike
	@RequestMapping(value = "/like",method = RequestMethod.POST)
	public ResponseEntity<Map<String, String>> manageLike (@RequestBody Map<String, String> likeInfo)
	{
		String sessionId = likeInfo.get("sessionId");
		String shareId = likeInfo.get("shareId");
		
		Map<String, String> isLikedInfo = new HashMap<String, String>();
		HttpStatus status = HttpStatus.CREATED;
		Optional<Like> like = likeService.manageLike(sessionId,shareId);
		
		if (like.isPresent()) {
			isLikedInfo.put("isLiked", "true");
			likeService.delete(like.get());
			status = HttpStatus.OK;
		}else{
			isLikedInfo.put("isLiked", "false");
			status = HttpStatus.OK;
		}
			
		return new ResponseEntity<Map<String, String>>(isLikedInfo,status);
	}
	
	//JB: Devuelve si una publciacion esta marcada como like por un usaurio
	@GetMapping(value = "/isLiked")
	public ResponseEntity<Map<String, String>> isLiked (@RequestParam("sessionId") String sessionId,@RequestParam("shareId") String shareId)
	{
		
		Map<String, String> isLikedInfo = new HashMap<String, String>();
		HttpStatus status = HttpStatus.CREATED;
		Optional<Like> like = likeService.isLiked(sessionId,shareId);
		
		if (like.isPresent()) {
			isLikedInfo.put("isLiked", "true");
			status = HttpStatus.OK;
		}else{
			isLikedInfo.put("isLiked", "false");
			status = HttpStatus.OK;
		}
			
		return new ResponseEntity<Map<String, String>>(isLikedInfo,status);
	}
}
