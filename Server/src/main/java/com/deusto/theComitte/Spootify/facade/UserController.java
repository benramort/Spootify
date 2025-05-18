package com.deusto.theComitte.Spootify.facade;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.deusto.theComitte.Spootify.DTO.CreateUserDTO;
import com.deusto.theComitte.Spootify.DTO.LoginDTO;
import com.deusto.theComitte.Spootify.DTO.SongListDTO;
import com.deusto.theComitte.Spootify.DTO.TokenDTO;
import com.deusto.theComitte.Spootify.DTO.UserDTO;
import com.deusto.theComitte.Spootify.entity.User;
import com.deusto.theComitte.Spootify.service.UserService;

@RestController
@CrossOrigin(origins = "http://localhost:8080")
public class UserController {
    
    private UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }
    
    /**
     * Crea un nuevo usuario en el sistema.
     *
     * @param userDTO DTO con los datos del usuario a crear
     * @return OK si se crea correctamente, CONFLICT si el usuario ya existe, BAD_REQUEST en otros errores
     */
    @PostMapping("/users")
    public ResponseEntity<Void> createUser(@RequestBody CreateUserDTO userDTO) {
        try {
            userService.createUser(userDTO.name(), userDTO.email(), userDTO.password());
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (RuntimeException e) {
            if (e.getMessage().equals("User already exists")) {
                return new ResponseEntity<>(HttpStatus.CONFLICT);
            }
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }   
    }

    /**
     * Hace el inicio de sesión de un usuario y devuelve un token de sesión.
     *
     * @param loginDTO DTO con email y contraseña
     * @return TokenDTO con el id del usuario y el token de sesión, NOT_FOUND si el usuario no existe, FORBIDDEN si la contraseña es incorrecta, BAD_REQUEST en otros errores
     */
    @PostMapping("/login")
    public ResponseEntity<TokenDTO> login(@RequestBody LoginDTO loginDTO) {
        try {
            long token = userService.login(loginDTO.email(), loginDTO.password());
            User user = userService.getActiveUser(token);
            TokenDTO tokenDTO = new TokenDTO(user.getId(), token);
            return ResponseEntity.ok(tokenDTO);
        } catch (RuntimeException ex) {
            if (ex.getMessage().equals("User does not exist")) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            } else if (ex.getMessage().equals("Incorrect password")) {
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Cierra la sesión del usuario asociado al token.
     *
     * @param token Token de sesión del usuario
     * @return NO_CONTENT si se cierra correctamente, UNAUTHORIZED si el usuario no está logueado, BAD_REQUEST en otros errores
     */
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestParam long token) {
        try {
            userService.logout(token);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (RuntimeException ex) {
            if (ex.getMessage().equals("User not logged in")) {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        
    }

    /**
     * Obtiene la lista de todos los usuarios registrados.
     *
     * @return Lista de UserDTO o BAD_REQUEST si ocurre un error
     */
    @GetMapping("")
    public ResponseEntity<List<UserDTO>> getUsers() {
        try {
            List<User> users = userService.getUsers();
            List<UserDTO> userDTOs = new ArrayList<>();
            for(User user : users) {
                userDTOs.add(user.toDTO());
            }
            return ResponseEntity.ok(userDTOs);
        } catch (RuntimeException ex) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Obtiene el perfil del usuario autenticado.
     *
     * @param token Token de sesión del usuario
     * @return UserDTO con los datos del usuario, UNAUTHORIZED si no está logueado, BAD_REQUEST en otros errores
     */
    @GetMapping("/users/myProfile")
    public ResponseEntity<UserDTO> getMyProfile(@RequestParam long token) {
        try {
            User user = userService.getActiveUser(token);
            return ResponseEntity.ok(user.toDTO());
        } catch (RuntimeException e) {
            e.printStackTrace();
            if(e.getMessage().equals("User not logged in")) {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/users/liked")
    public ResponseEntity<SongListDTO> getLikedSongs(@RequestParam long token) {
        try {
            SongListDTO likedSongPlaylist = userService.getLikedSongs(token).toDTO();
            return ResponseEntity.ok(likedSongPlaylist);
        } catch (Exception e) {
            e.printStackTrace();
            if(e.getMessage().equals("User not logged in")) {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            } else if (e.getMessage().equals("Song list does not exist")) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    

}
