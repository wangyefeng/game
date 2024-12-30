package org.game.login.service;

import org.game.login.entity.User;
import org.game.login.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public void save(User user) {
        userRepository.save(user);
    }

    public Optional<User> find(int id) {
        return userRepository.findById(id);
    }
}
