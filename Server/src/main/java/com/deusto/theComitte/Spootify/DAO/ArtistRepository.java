package com.deusto.theComitte.Spootify.DAO;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.deusto.theComitte.Spootify.entity.Artist;

@Repository
public interface ArtistRepository extends JpaRepository<Artist, Long> {
    Artist findById(long id);
}
