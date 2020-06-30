package jb.dam2.discover.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jb.dam2.discover.pojo.Artist;
import jb.dam2.discover.repository.ArtistRepository;

@Service
public class ArtistService implements IArtistService {
	
	@Autowired
	ArtistRepository artistRepo;

	@Override
	public void save(Artist artist) {
		artistRepo.save(artist);
		
	}

	@Override
	public Optional<Artist> findById(String id) {
		return artistRepo.findById(id);
	}

	@Override
	public List<Artist> findAll() {
		return (List<Artist>) artistRepo.findAll();
	}

	@Override
	public boolean exists(String id) {
		boolean exists = false;
		if (artistRepo.findById(id).isPresent()) {
			exists = true;
		}
		return exists;
	}

}
