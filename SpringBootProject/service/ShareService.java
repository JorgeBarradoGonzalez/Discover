package jb.dam2.discover.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jb.dam2.discover.pojo.Artist;
import jb.dam2.discover.pojo.Comment;
import jb.dam2.discover.pojo.Share;
import jb.dam2.discover.pojo.User;
import jb.dam2.discover.pojo.UserFollowing;
import jb.dam2.discover.repository.ArtistRepository;
import jb.dam2.discover.repository.SessionRepository;
import jb.dam2.discover.repository.ShareRepository;
import jb.dam2.discover.repository.UserFollowingRepository;
import jb.dam2.discover.repository.UserRepository;

@Service
public class ShareService implements IShareService{
	
	@Autowired
	ShareRepository shareRepo;
	@Autowired
	SessionRepository sessionRepo;
	@Autowired
	ArtistRepository artistRepo;
	@Autowired
	UserRepository userRepo;
	@Autowired
	UserFollowingRepository userFollowingRepo;

	@Override
	public void save(Share share) {
		shareRepo.save(share);
	}

	@Override
	public Optional<Share> findById(Long id) {
		return shareRepo.findById(id);
	}

	@Override
	public List<Share> findAll() {
		return (List<Share>) shareRepo.findAll();
	}

	@Override
	public boolean exists(Long id) {
		boolean exists = false;
		if (shareRepo.findById(id).isPresent()) {
			exists = true;
		}
		return exists;
	}

	@Override
	public Optional<Share> manageUpload(String videoId, Long sessionId, String comment, String artist, String videoTitle) {
		Share share = new Share();
		share.setVideoId(videoId);
		share.setUser(sessionRepo.findById(sessionId).get().getUser());
		share.setComment(Comment.builder().text(comment).build());
		//ARTISTA
		if(artistRepo.existsById(artist)) {
			share.setArtist(artistRepo.findById(artist).get());
		}else {
			artistRepo.save(Artist.builder().name(artist).build());
			share.setArtist(artistRepo.findById(artist).get());
		}
		share.setVideoTitle(videoTitle);
		share.setDate(LocalDateTime.now());
		Optional<Share> shareOptional= Optional.of(share);
		
		return shareOptional;
	}

	@Override
	public List<Share> findFollowedShares(String sessionId) {
		//FIND USER BY SESSION ID
		User user  = sessionRepo.findById(Long.valueOf(sessionId)).get().getUser();
		//FIND USERFOLLOWING OBJECTS BY SESSION USER
		List<UserFollowing> userFollowingBySessionUser= userFollowingRepo.findByUserFollowing(user);
		//GET USERS FOLLOWED BY SESSION USER
		List<User> userFollowings = new ArrayList<User>();
		for (UserFollowing userFollowedBySessionUser : userFollowingBySessionUser) {
			userFollowings.add(userFollowedBySessionUser.getUserFollowed());
		}
		//GET SHARES BY USERS FOLLOWED BY SESSION USER
		List<Share> userShares = new ArrayList<Share>();
		for (User userFollowedBySessionUser : userFollowings) {
			List<Share> shares = findUserShares(userFollowedBySessionUser);
			for (Share share : shares) {
				userShares.add(share);
			}
		}
		return userShares;
	}
	
	@Override
	public List<Share> findUserShares(User user) {
		return shareRepo.findByUser(user);
	}

}
