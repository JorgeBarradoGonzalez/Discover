package jb.dam2.discover.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import jb.dam2.discover.repository.UserRepository;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
	
	@Autowired
	private UserRepository userRepo;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Optional<jb.dam2.discover.pojo.User> user = userRepo.findById(username);
		if (!user.isPresent()) {
			throw new UsernameNotFoundException("User not found by name: " + username);
		}
		return toUserDetails(user.get());
	}
	
	private UserDetails toUserDetails(jb.dam2.discover.pojo.User userObject) {
        return User.withUsername(userObject.getUsername())
                   .password(userObject.getPassword())
                   .build();
    }

	

}
