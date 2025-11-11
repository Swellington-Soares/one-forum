package br.one.forum.controller;

import br.one.forum.dtos.CriarUserDto;
import br.one.forum.entities.Profile;
import br.one.forum.entities.User;
import br.one.forum.repositories.UserRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @PostMapping
    public ResponseEntity<User> criarUsuario(@RequestBody @Valid CriarUserDto dto) {

        var user = new User(dto.email(), dto.password());
        var profile = new Profile(user, dto.name());

        user.setProfile(profile);

        var saved = userRepository.save(user);

        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }
}

