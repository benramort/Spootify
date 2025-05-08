package com.deusto.theComitte.Spootify.DAO;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.deusto.theComitte.Spootify.entity.Artist;

@Repository
public interface ArtistRepository extends JpaRepository<Artist, Long> {
    Artist findById(long id);
    Artist findByEmail(String email);

    @Query("SELECT a FROM Artist a WHERE LOWER(a.name) LIKE LOWER(CONCAT('%',:name, '%'))")
    List<Artist> findByName(String name);
}
