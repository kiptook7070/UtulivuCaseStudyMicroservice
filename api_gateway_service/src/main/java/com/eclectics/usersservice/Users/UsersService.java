package com.eclectics.usersservice.Users;

import com.eclectics.usersservice.Roles.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class UsersService {
    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RoleRepository roleRepository;

    private final Date today = new Date();

    public Users userRegistration(Users user) {
        roleRepository.saveAll(user.getRoles());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return usersRepository.save(user);
    }

    public Users updateUser(Users user) {
        return usersRepository.save(user);
    }

    public List<Users> getAllUsers() {
        return usersRepository.findAll();
    }

    public Users getUser(Long id) {
        return usersRepository.findById(id).orElse(null);
    }

    public List<Users> undeletedUsers() {
        return usersRepository.findAllByDeletedFlag('N');
    }
}
