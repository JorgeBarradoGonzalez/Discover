package jb.dam2.discover.service;

import java.util.List;
import java.util.Optional;

import jb.dam2.discover.pojo.User;

public interface IUserService {
	public void save(User user);
	public Optional<User> findByUsername(String username);
	public List<User> findAll();
	public Optional<User> verifyUser(String username, String password);
	public boolean exists(String username);
	public List<User> findFollowedUsers(String user);
	public void manageSignUp(User user);
}
