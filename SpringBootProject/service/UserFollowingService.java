package jb.dam2.discover.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jb.dam2.discover.pojo.User;
import jb.dam2.discover.pojo.UserFollowing;
import jb.dam2.discover.repository.SessionRepository;
import jb.dam2.discover.repository.UserFollowingRepository;
import jb.dam2.discover.repository.UserRepository;

@Service
public class UserFollowingService implements IUserFollowingService {
	
	@Autowired
	UserFollowingRepository userFollowingRepo;
	@Autowired
	SessionRepository sessionRepo;
	@Autowired
	UserRepository userRepo;

	@Override
	public void save(UserFollowing userFollowing) {
		userFollowingRepo.save(userFollowing);
	}
	
	public void delete(UserFollowing userFollowing) {
		userFollowingRepo.delete(userFollowing);
	}
	
	@Override
	public UserFollowing manageFollow(String sessionUserId, String followedUserUsername) {
		User sessionUser = sessionRepo.findById(Long.valueOf(sessionUserId)).get().getUser();
		User followedUser = userRepo.findById(followedUserUsername).get();
		UserFollowing userFollowing = UserFollowing.builder().userFollowing(sessionUser).userFollowed(followedUser).build();
		save(userFollowing);
		return userFollowing;
	}
	
	public void manageUnfollow(String sessionUserId,String followedUserUsername) {
		User sessionUser = sessionRepo.findById(Long.valueOf(sessionUserId)).get().getUser();
		User followedUser = userRepo.findById(followedUserUsername).get();
		List<UserFollowing> followings = userFollowingRepo.findByUserFollowedAndUserFollowing(followedUser, sessionUser);
		UserFollowing userFollowing = userFollowingRepo.findByUserFollowedAndUserFollowing(followedUser, sessionUser).get(0);
		delete(userFollowing);
	}

	@Override
	public boolean isFollowing(User isFollowedUser, User sessionUser) {
		List<UserFollowing> aa = userFollowingRepo.findByUserFollowedAndUserFollowing(isFollowedUser, sessionUser);
		return userFollowingRepo.findByUserFollowedAndUserFollowing(isFollowedUser, sessionUser).isEmpty();
	}
	
	private User getUserBySessionId(String sessionId) {
		return sessionRepo.findById(Long.valueOf(sessionId)).get().getUser();
	}

	

}
