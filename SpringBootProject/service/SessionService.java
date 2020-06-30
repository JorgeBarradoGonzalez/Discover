package jb.dam2.discover.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jb.dam2.discover.pojo.Session;
import jb.dam2.discover.pojo.User;
import jb.dam2.discover.repository.SessionRepository;

@Service
public class SessionService implements ISessionService {
	
	@Autowired
	private SessionRepository sessionRepo;

	@Override
	public void save(Session session) {
		sessionRepo.save(session);
	}
	
	@Override
	public Optional<Session> findByUser(User user) {
		Optional<Session> session = Optional.empty();
		if(sessionRepo.findByUser(user).isPresent()) {
			session = sessionRepo.findByUser(user);
		}
		return session;
	}

	@Override
	public List<Session> findAll() {
		return (List<Session>)sessionRepo.findAll();
	}

	@Override
	public boolean exists(User user) {
		boolean exists = false;
		if(sessionRepo.findByUser(user).isPresent()) {
			exists=true;
		}
		return exists;
	}
	
	public Optional<User> findUserBySessionId(String sessionId){	
		return Optional.of(sessionRepo.findById(Long.valueOf(sessionId)).get().getUser());
	}

}
