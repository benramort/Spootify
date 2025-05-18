package com.deusto.theComitte.Spootify.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.deusto.theComitte.Spootify.DAO.UserRepository;
import com.deusto.theComitte.Spootify.DAO.ArtistRepository;
import com.deusto.theComitte.Spootify.DAO.PlayListRepository;
import com.deusto.theComitte.Spootify.entity.Artist;
import com.deusto.theComitte.Spootify.entity.SongList;
import com.deusto.theComitte.Spootify.entity.User;

@Service
public class UserService {

    UserRepository userRepository;
    ArtistRepository artistRepository;
    PlayListRepository songListRepository;

    @Autowired
    public UserService(UserRepository userRepository, ArtistRepository artistRepository, PlayListRepository songListRepository) {
        this.userRepository = userRepository;
        this.artistRepository = artistRepository;
        this.songListRepository = songListRepository;
    }

    private Map<Long, User> activeUsers = new HashMap<>();

    /**
     * Crea un nuevo usuario y su lista de canciones que le gustan.
     *
     * @param name Nombre del usuario
     * @param email Email del usuario
     * @param password Contraseña del usuario
     * @throws RuntimeException Si el usuario ya existe
     */
    public void createUser(String name, String email, String password) {
        User existingUser = userRepository.findByEmail(email);
        if (existingUser != null) {
            throw new RuntimeException("User already exists");
        }
        
        // First save the user (without the song list reference)
        User user = new User(name, email, password);
        userRepository.save(user);
        
        // Then create and save the song list
        String nombre = "Canciones que me gustan";
        SongList cancionesQueMeGustan = new SongList(nombre, false, user);
        songListRepository.save(cancionesQueMeGustan);
        
        // Now that the song list has an ID, set it on the user and update
        user.addSongList(cancionesQueMeGustan);
        user.setCancionesMeGustanID(cancionesQueMeGustan.getId());
        userRepository.save(user);
    }

    /**
     * Hace el inicio de sesión de un usuario y devuelve un token de sesión.
     *
     * @param email Email del usuario
     * @param password Contraseña del usuario
     * @return Token de sesión generado
     * @throws RuntimeException Si el usuario no existe o la contraseña es incorrecta
     */
    public long login(String email, String password) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new RuntimeException("User does not exist");
        }
        if (!user.getPassword().equals(password)) {
            throw new RuntimeException("Incorrect password");
        }
        long token = System.currentTimeMillis();
        activeUsers.put(token, user);
        return token;
    }

    /**
     * Cierra la sesión del usuario asociado al token.
     *
     * @param token Token de sesión del usuario
     * @throws RuntimeException Si el usuario no está logueado
     */
    public void logout(long token) {
        User user = activeUsers.remove(token);
        if (user == null) {
            throw new RuntimeException("User not logged in");
        }
    }

    /**
     * Obtiene la lista de todos los usuarios registrados.
     *
     * @return Lista de usuarios
     */
    public List<User> getUsers() {
        return userRepository.findAll();
    }

    /**
     * Obtiene el usuario activo asociado a un token.
     *
     * @param token Token de sesión del usuario
     * @return Usuario activo
     * @throws RuntimeException Si el usuario no está logueado
     */
    public User getActiveUser(long token) {
        System.out.println("Token: " + token);
        // System.out.println("Active users: " + activeUsers);
        User user = activeUsers.get(token);
        // System.out.println("User: " + user);
        if (user == null) {
            throw new RuntimeException("User not logged in");
        }
        return user;
    }

    /**
     * Devuelve el mapa de usuarios activos.
     *
     * @return Mapa de usuarios activos
     */
    public Map<Long, User> getActiveUserMap() {
        return this.activeUsers;
    }
    
    /**
     * Permite a un usuario seguir a un artista.
     *
     * @param token Token de sesión del usuario
     * @param artistID ID del artista a seguir
     * @throws RuntimeException Si el usuario no está logueado o el artista no existe
     */
    public void followArtist(long token, long artistID) {
        User user = activeUsers.get(token);
        if (user == null) {
            throw new RuntimeException("User not logged in");
        }
        Artist artist = artistRepository.findById(artistID);
        if (artist == null) {
            throw new RuntimeException("Artist does not exist");
        }
        artist.followArtist(user);
        userRepository.save(user);
        artistRepository.save(artist);
    }

    /**
     * Obtiene la lista de canciones que le gustan del usuario.
     *
     * @param token Token de sesión del usuario
     * @return SongList de canciones favoritas
     * @throws RuntimeException Si el usuario no está logueado o la lista no existe
     */
    public SongList getLikedSongs(long token) {
        User user = activeUsers.get(token);
        if (user == null) {
            throw new RuntimeException("User not logged in");
        }
        SongList songList = songListRepository.findById(user.getCancionesMeGustanID());
        if (songList == null) {
            throw new RuntimeException("Song list does not exist");
        }
        return songList;
    }	
     

    
}
