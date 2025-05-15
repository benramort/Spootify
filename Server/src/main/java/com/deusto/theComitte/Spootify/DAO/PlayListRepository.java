package com.deusto.theComitte.Spootify.DAO;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.deusto.theComitte.Spootify.entity.SongList;

@Repository
public interface PlayListRepository extends JpaRepository<SongList, Long>{
    SongList findById(long id);
    SongList findByName(String name);
}
