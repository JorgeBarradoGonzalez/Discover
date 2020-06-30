package jb.dam2.discover.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import jb.dam2.discover.pojo.Reply;
import jb.dam2.discover.pojo.ReplyAlert;
import jb.dam2.discover.service.IReplyAlertService;
import jb.dam2.discover.service.ISessionService;
import jb.dam2.discover.wrapper.CustomReplyAlertWrapper;
import jb.dam2.discover.wrapper.ReplyAlertWrapper;

/*JB:
 * Controlador para las respuestas
 */
@RestController
@RequestMapping(value = "/replyAlerts")
public class ReplyAlertController {
	
	@Autowired
	private IReplyAlertService replyAlertService;
	@Autowired
	private ISessionService sessionService;
	
	//JB: Se descargan las alertas de un usuario
	@GetMapping("/download/{sessionId}")
	public ResponseEntity<ReplyAlertWrapper> downloadAlerts (@PathVariable("sessionId") String sessionId)
	{
		
		HttpStatus status = HttpStatus.CREATED;
		
		List<ReplyAlert> replyAlerts = replyAlertService.findByUserShared(sessionService.findUserBySessionId(sessionId).get());
		ReplyAlertWrapper replyAlertsWrapper = new ReplyAlertWrapper(replyAlerts);
		replyAlertsWrapper.setReplyAlerts(replyAlerts);
		replyAlertsWrapper.sortByDate();
		status = HttpStatus.OK;
		return new ResponseEntity<ReplyAlertWrapper>(replyAlertsWrapper,status);
	}
	
	//JB: Se actualizan las alertas de un usuario cuando se comenta en su publicacion
	@RequestMapping(value = "/update",method = RequestMethod.POST)
	public ResponseEntity<String> updateAlerts (@RequestBody CustomReplyAlertWrapper replyAlerts)
	{
		
		HttpStatus status = HttpStatus.CREATED;
		replyAlertService.manageSeenReplyAlerts(replyAlerts);
		
		status = HttpStatus.OK;
			
		return new ResponseEntity<String>("OK",status);
	}
}
