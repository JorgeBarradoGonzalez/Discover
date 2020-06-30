package jb.dam2.discover.controller;

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
import org.springframework.web.bind.annotation.RestController;

import jb.dam2.discover.pojo.Reply;
import jb.dam2.discover.pojo.Share;
import jb.dam2.discover.service.IReplyService;
import jb.dam2.discover.wrapper.ReplyWrapper;

/*JB:
 * Controlador para los comentarios de una publicacion
 */
@RestController
@RequestMapping(value = "/replies")
public class ReplyController {
	
	@Autowired
	private IReplyService replyService;
	
	@GetMapping(value="/all") 
	public List<Reply> getAllCustomer(){return replyService.findAll();}
	
	//JB: Se descargan los comentarios de una publicacion
	@GetMapping("/download/{shareId}")
	public ResponseEntity<ReplyWrapper> downloadReplies (@PathVariable("shareId") String shareId)
	{
		
		HttpStatus status = HttpStatus.CREATED;
		List<Reply> replies = replyService.findByShare(shareId);
		ReplyWrapper replyWrapper = new ReplyWrapper(replies);
		replyWrapper.setReplies(replies);
		replyWrapper.sortByDate();
		status = HttpStatus.OK;
			
		return new ResponseEntity<ReplyWrapper>(replyWrapper,status);
	}
	
	//JB: Se publica un comentario
	@RequestMapping(value = "/upload",method = RequestMethod.POST)
	public ResponseEntity<Reply> postReply (@RequestBody Map<String, String> replyInfo)
	{
		String sessionId = replyInfo.get("sessionId");
		String shareId = replyInfo.get("shareId");
		String text = replyInfo.get("text");
		
		
		HttpStatus status = HttpStatus.CREATED;
		Reply reply = replyService.manageUpload(sessionId,shareId,text);
		
		status = HttpStatus.OK;
			
		return new ResponseEntity<Reply>(reply,status);
	}
}
