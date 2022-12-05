package com.fergus.fergusapi.models;

import javax.persistence.*;

@Entity
@Table(name = "SONG")
public class Song {


    @Id
    @Column(name = "ID")
    private Long id;

    @Column(name = "TITLE")
    private String title;

    @Column(name = "ARTIST")
    private String artist;

    @Column(name = "REMIX")
    private String remix;

    @Column(name = "RADIONAME")
    private String radioname;

    @Column(name = "HASH")
    private String hash;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getRemix() {
        return remix;
    }

    public void setRemix(String remix) {
        this.remix = remix;
    }

    public String getRadioname() {
        return radioname;
    }

    public void setRadioname(String radioname) {
        this.radioname = radioname;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }
}
