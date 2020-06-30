package jb.dam2.discover.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import jb.dam2.discover.pojo.Share;
import jb.dam2.discover.pojo.User;
import jb.dam2.discover.pojo.UserFollowing;
import jb.dam2.discover.repository.SessionRepository;
import jb.dam2.discover.repository.UserFollowingRepository;
import jb.dam2.discover.repository.UserRepository;
import jb.dam2.discover.wrapper.ShareWrapper;

@Service
public class UserService implements IUserService {

	@Autowired
	private UserRepository userRepo;
	@Autowired
	private UserFollowingRepository userFollowingRepo;
	@Autowired
	private PasswordEncoder passwordEncoder;

	public void save(User user) {
		userRepo.save(user);
	}

	public List<User> findAll() {
		return (List<User>) userRepo.findAll();
	}

	public Optional<User> findByUsername(String username) {
		return userRepo.findById(username);
	}

	@Override
	public Optional<User> verifyUser(String username, String password) {
		Optional<User> user = Optional.empty();
		if (passwordEncoder.matches(password, userRepo.findById(username).get().getPassword())) {
			user = userRepo.findById(username);
		}

		return user;
	}

	@Override
	public boolean exists(String username) {
		boolean exists = false;
		if (userRepo.findById(username).isPresent()) {
			exists = true;
		}
		return exists;
	}

	@Override
	public List<User> findFollowedUsers(String username) {
		// FIND USER BY SESSION ID
		User user = userRepo.findById(username).get();
		// FIND USERFOLLOWING OBJECTS BY SESSION USER
		List<UserFollowing> usersFollowedByUser = userFollowingRepo.findByUserFollowing(user);
		// GET USERS FOLLOWED BY SESSION USER
		List<User> usersFollowed= new ArrayList<User>();
		for (UserFollowing userFollowedByUser : usersFollowedByUser) {
			usersFollowed.add(userFollowedByUser.getUserFollowed());
		}
		return usersFollowed;
	}

	@Override
	public void manageSignUp(User user) {
		User userSaved = User.builder().username(user.getUsername()).password(passwordEncoder.encode(user.getPassword())).email(user.getEmail()).build();
		save(userSaved);
	}

}
