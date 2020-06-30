package jb.dam2.discover.service;

import jb.dam2.discover.pojo.User;
import jb.dam2.discover.pojo.UserFollowing;

public interface IUserFollowingService {
	public void save(UserFollowing userFollowing);
	public void delete(UserFollowing userFollowing);
	public UserFollowing manageFollow(String sessionUserId,String followedUserUsername);
	public void manageUnfollow(String sessionUserId,String followedUserUsername);
	public boolean isFollowing(User sessionUser,User searchUser);
}
