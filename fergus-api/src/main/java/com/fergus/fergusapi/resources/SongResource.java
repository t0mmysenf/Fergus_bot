package com.fergus.fergusapi.resources;


import com.fergus.fergusapi.models.Song;
import com.fergus.fergusapi.repositories.SongRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;

@RestController
public class SongResource {
    @Autowired
    SongRepository songRepository;

    @PostMapping("/song")
    public void saveSong(Song song){
        songRepository.save(song);
    }

    @GetMapping("/song")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Song> getAllSongs() { return songRepository.findAll(); }

}
