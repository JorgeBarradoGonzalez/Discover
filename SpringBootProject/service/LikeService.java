package jb.dam2.discover.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jb.dam2.discover.pojo.Like;
import jb.dam2.discover.pojo.Share;
import jb.dam2.discover.pojo.User;
import jb.dam2.discover.repository.LikeRepository;
import jb.dam2.discover.repository.SessionRepository;
import jb.dam2.discover.repository.ShareRepository;
import jb.dam2.discover.repository.UserRepository;

@Service
public class LikeService implements ILikeService {
	
	@Autowired
	private LikeRepository likeRepo;
	@Autowired
	private UserRepository userRepo;
	@Autowired
	private SessionRepository sessionRepo;
	@Autowired
	private ShareRepository shareRepo;

	@Override
	public void save(Like like) {
		likeRepo.save(like);		
	}

	@Override
	public void delete(Like like) {
		likeRepo.delete(like);
	}

	@Override
	public List<Like> findUserLikes(String username) {
		return (List<Like>) likeRepo.findByUser(userRepo.findById(username).get());
	}

	@Override
	public Optional<Like> manageLike(String sessionId, String shareId) {
		Optional<Like> like = Optional.empty();
		User user = sessionRepo.findById(Long.valueOf(sessionId)).get().getUser();
		Share share = shareRepo.findById(Long.valueOf(shareId)).get();
		List<Like> isLike = (List<Like>) likeRepo.findByUserAndShare(user, share);
		if(!isLike.isEmpty()) {
			like = Optional.of(isLike.get(0));
		}else {
			save(Like.builder().share(share).user(user).date(LocalDateTime.now()).build());
		}
		return like;
	}

	@Override
	public Optional<Like> isLiked(String sessionId, String shareId) {
		Optional<Like> like = Optional.empty();
		User user = sessionRepo.findById(Long.valueOf(sessionId)).get().getUser();
		Share share = shareRepo.findById(Long.valueOf(shareId)).get();
		List<Like> isLike = (List<Like>) likeRepo.findByUserAndShare(user, share);
		if(!isLike.isEmpty()) {
			like = Optional.of(isLike.get(0));
		}
		return like;
	}

}
