package jb.dam2.discover.service;

import java.util.List;
import java.util.Optional;

import jb.dam2.discover.pojo.Session;
import jb.dam2.discover.pojo.User;

public interface ISessionService {
	public void save(Session session);
	public Optional<Session> findByUser(User user);
	public List<Session> findAll();
	public boolean exists(User user);
	public Optional<User> findUserBySessionId(String sessionId);
}
