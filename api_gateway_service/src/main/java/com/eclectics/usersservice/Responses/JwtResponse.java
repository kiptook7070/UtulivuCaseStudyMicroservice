package com.eclectics.usersservice.Responses;

import com.eclectics.usersservice.Roles.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Set;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@ToString
@Data
public class JwtResponse {
    private boolean otpEnabled = false;
    private String token;
    private String type = "Bearer";
    private Long id;
    private String username;
    private String firstName;
    private String lastName;

    private String email;
    private Set<Role> roles;
    private Character firstLogin;
    private UUID uuid;
    private String Status;
    private String loginAt;
    private String address;
    private String os;
    private String browser;
    private Character isSystemGenPassword;
}
