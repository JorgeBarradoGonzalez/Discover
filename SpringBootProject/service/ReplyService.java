package jb.dam2.discover.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jb.dam2.discover.pojo.Reply;
import jb.dam2.discover.pojo.ReplyAlert;
import jb.dam2.discover.pojo.Share;
import jb.dam2.discover.pojo.User;
import jb.dam2.discover.repository.ReplyAlertRepository;
import jb.dam2.discover.repository.ReplyRepository;
import jb.dam2.discover.repository.SessionRepository;
import jb.dam2.discover.repository.ShareRepository;

@Service
public class ReplyService implements IReplyService {
	@Autowired
	ReplyRepository replyRepo;
	@Autowired
	ShareRepository shareRepo;
	@Autowired
	SessionRepository sessionRepo;
	@Autowired
	ReplyAlertRepository replyAlertRepo;

	@Override
	public void save(Reply reply) {
		replyRepo.save(reply);
	}

	@Override
	public List<Reply> findAll() {
		return (List<Reply>) replyRepo.findAll();
	}

	@Override
	public List<Reply> findByShare(String shareId) {
		return (List<Reply>) replyRepo.findByShare(shareRepo.findById(Long.valueOf(shareId)).get());
	}

	@Override
	public Reply manageUpload(String sessionId,String shareId,String text) {
		User userReplied = sessionRepo.findById(Long.valueOf(sessionId)).get().getUser();
		Share share = shareRepo.findById(Long.valueOf(shareId)).get();
		User userShared = share.getUser();
		Reply reply = Reply.builder().username(userReplied.getUsername())
				.text(text)
				.share(share)
				.date(LocalDateTime.now())
				.build();
		save(reply);
		if(!userShared.getUsername().equals(userReplied.getUsername())) {
			setAlert(userReplied,userShared,share,text);
		}
		return reply;
	}

	private void setAlert(User userReplied,User userShared,Share share,String text) {
		ReplyAlert alert = ReplyAlert.builder()
				.share(share).userReplied(userReplied).userShared(userShared).date(LocalDateTime.now()).isSeen(false).repliedText(text)
				.build();
		replyAlertRepo.save(alert);
	}
}
