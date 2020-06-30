package jb.dam2.discover.controller;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jb.dam2.discover.pojo.Share;
import jb.dam2.discover.pojo.User;
import jb.dam2.discover.service.ISessionService;
import jb.dam2.discover.service.IShareService;
import jb.dam2.discover.service.IUserService;
import jb.dam2.discover.service.ShareService;
import jb.dam2.discover.wrapper.ShareWrapper;

/*JB:
 * Controlador para las publicaciones
 */
@RestController
@RequestMapping(value = "/shares")
public class ShareController {
	
	@Autowired
	IShareService shareService;
	@Autowired
	IUserService userService;
	@Autowired
	private ISessionService sessionService;
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	
	@GetMapping(value="/all") 
	public List<Share> getAllCustomer(){return shareService.findAll();}
	
	//http://192.168.56.1:8080/shares/upload
	//JB: Se comparte una publicacion
	@RequestMapping(value = "/upload",method = RequestMethod.POST)
	public ResponseEntity<Share> upload (@RequestBody Map<String, String> shareInfo)
	{
		
		String videoId = shareInfo.get("videoId");
		Long sessionId = Long.valueOf(shareInfo.get("sessionId"));
		String comment = shareInfo.get("comment");
		String artist = shareInfo.get("artist");
		String videoTitle = shareInfo.get("videoTitle");
		
		HttpStatus status = HttpStatus.CREATED;
		Optional<Share> share = shareService.manageUpload(videoId,sessionId,comment,artist,videoTitle);
		
		if (share.isPresent()) {
			shareService.save(share.get());
			status = HttpStatus.OK;
		}else{
			status = HttpStatus.BAD_REQUEST;
		}
			
		return new ResponseEntity<Share>(share.get(),status);
	}
	
	//http://192.168.56.1:8080/shares/download/idSession
	//JB: Se descargan las publicaciones que le corresponde ver a un usuario
	@GetMapping("/download/{sessionId}")
	public ResponseEntity<ShareWrapper> download (@PathVariable("sessionId") String sessionId)
	{
		
		HttpStatus status = HttpStatus.CREATED;
		ShareWrapper shareWrapper = new ShareWrapper();
		List<Share> usersFollowedShares = shareService.findFollowedShares(sessionId);
		
		if(!usersFollowedShares.isEmpty()) {
			shareWrapper.setShares(usersFollowedShares);
			shareWrapper.sortByDate();
			shareWrapper.reverse();
			status = HttpStatus.OK;
		}else {
			status = HttpStatus.BAD_REQUEST;
		}
			
		return new ResponseEntity<ShareWrapper>(shareWrapper,status);
	}
	
	//JB: Se descargan las publicaciones de un usuario
	@GetMapping("/user/{username}")
	public ResponseEntity<ShareWrapper> downloadUserShares (@PathVariable("username") String username)
	{
		
		HttpStatus status = HttpStatus.CREATED;
		ShareWrapper shareWrapper = new ShareWrapper();
		List<Share> usersFollowedShares = shareService.findUserShares(userService.findByUsername(username).get());
		
		if(!usersFollowedShares.isEmpty()) {
			shareWrapper.setShares(usersFollowedShares);
			shareWrapper.sortByDate();
			shareWrapper.reverse();
			status = HttpStatus.OK;
		}else {
			status = HttpStatus.BAD_REQUEST;
		}
			
		return new ResponseEntity<ShareWrapper>(shareWrapper,status);
	}
	
	//JB: Se descargan las publicaciones del usuario del dispositivo
	@GetMapping("/self/{sessionId}")
	public ResponseEntity<ShareWrapper> downloadSelfShares (@PathVariable("sessionId") String sessionId)
	{
		
		HttpStatus status = HttpStatus.CREATED;
		ShareWrapper shareWrapper = new ShareWrapper();
		User user = sessionService.findUserBySessionId(sessionId).get();
		List<Share> usersFollowedShares = shareService.findUserShares(userService.findByUsername(user.getUsername()).get());
		
		if(!usersFollowedShares.isEmpty()) {
			shareWrapper.setShares(usersFollowedShares);
			shareWrapper.sortByDate();
			shareWrapper.reverse();
			status = HttpStatus.OK;
		}else {
			status = HttpStatus.BAD_REQUEST;
		}
			
		return new ResponseEntity<ShareWrapper>(shareWrapper,status);
	}
	
	//JB: Se descargan la informacion de una publicacion
	@GetMapping("/get/{shareId}")
	public ResponseEntity<Share> getShare (@PathVariable("shareId") String shareId)
	{
		
		HttpStatus status = HttpStatus.CREATED;
		Share share = null;
		if(shareService.exists(Long.valueOf(shareId))) {
			share = shareService.findById(Long.valueOf(shareId)).get();
			status = HttpStatus.OK;
		}else {
			status = HttpStatus.BAD_REQUEST;
		}
			
		return new ResponseEntity<Share>(share,status);
	}
}
