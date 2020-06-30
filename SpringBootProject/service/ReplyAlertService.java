package jb.dam2.discover.service;

import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jb.dam2.discover.pojo.ReplyAlert;
import jb.dam2.discover.pojo.User;
import jb.dam2.discover.repository.ReplyAlertRepository;
import jb.dam2.discover.wrapper.CustomReplyAlertWrapper;

@Service
public class ReplyAlertService implements IReplyAlertService {
	
	@Autowired
	ReplyAlertRepository replyAlertRepo;

	@Override
	public void save(ReplyAlert replyAlert) {
		replyAlertRepo.save(replyAlert);
	}

	@Override
	public List<ReplyAlert> findByUserShared(User user) {
		return (List<ReplyAlert>) replyAlertRepo.findByUserShared(user);
	}

	@Override
	public void manageSeenReplyAlerts(CustomReplyAlertWrapper replyAlerts) {
		List<String> alerts = replyAlerts.getReplyAlertIds();
		Iterator<String> i = alerts.iterator();
		while(i.hasNext()) {
			String alert = i.next();
			ReplyAlert alertToUpdate = replyAlertRepo.findById(Long.valueOf(alert)).get();
			alertToUpdate.setSeen(true);
			replyAlertRepo.save(alertToUpdate);
		}
	}
}
