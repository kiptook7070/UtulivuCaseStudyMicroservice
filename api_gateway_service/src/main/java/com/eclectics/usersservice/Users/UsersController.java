package com.eclectics.usersservice.Users;

import com.eclectics.usersservice.MailService.MailService;
import com.eclectics.usersservice.OTP.OTP;
import com.eclectics.usersservice.OTP.OTPRepository;
import com.eclectics.usersservice.OTP.OTPService;
import com.eclectics.usersservice.Requests.Forgotpassword;
import com.eclectics.usersservice.Requests.LoginRequest;
import com.eclectics.usersservice.Requests.PasswordResetRequest;
import com.eclectics.usersservice.Requests.SignupRequest;
import com.eclectics.usersservice.Responses.JwtResponse;
import com.eclectics.usersservice.Responses.MessageResponse;
import com.eclectics.usersservice.Responses.SignupResponse;
import com.eclectics.usersservice.Roles.ERole;
import com.eclectics.usersservice.Roles.Role;
import com.eclectics.usersservice.Roles.RoleRepository;
import com.eclectics.usersservice.Security.jwt.Clientinformation;
import com.eclectics.usersservice.Security.jwt.CurrentUserContext;
import com.eclectics.usersservice.Security.jwt.JwtUtils;
import com.eclectics.usersservice.Security.services.UserDetailsImpl;
import com.eclectics.usersservice.utils.EntityResponse;
import com.eclectics.usersservice.utils.HttpInterceptor.UserRequestContext;
import com.eclectics.usersservice.utils.PasswordGeneratorUtil;
import com.eclectics.usersservice.utils.Session.Activesession;
import com.eclectics.usersservice.utils.Session.SessionManager;
import com.eclectics.usersservice.utils.exception.InvalidRequestParameterException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.passay.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import javax.validation.Valid;
import java.time.Instant;
import java.util.*;


@RestController
@RequestMapping("/auth")
@Slf4j
public class UsersController {
    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    MailService mailService;

    @Autowired
    Clientinformation clientinformation;

    @Autowired
    private SessionManager sessionManager;


    @Autowired
    UsersRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    OTPService otpService;

    @Value("${spring.application.useOTP}")
    private String useOTP;
    @Value("${spring.application.otpProd}")
    private String otpProd;
    @Value("${spring.application.otpTestMail}")
    private String otpTestMail;


    @Autowired
    OTPRepository otpRepository;




    @PostMapping("/signin")
    public EntityResponse authenticateUser(@Valid @RequestBody LoginRequest loginRequest) throws MessagingException {
        EntityResponse response = new EntityResponse();
        Users user = userRepository.findByUsername(loginRequest.getUsername()).orElse(null);
        if (user == null) {
            response.setMessage("Account Access Restricted! Check with the  System Admin");
            response.setStatusCode(HttpStatus.UNAUTHORIZED.value());
            return response;
        } else {
            if (user.isAcctLocked) {
                response.setMessage("Account is Locked!");
                response.setStatusCode(HttpStatus.UNAUTHORIZED.value());
                return response;
            } else {
                if (user.getDeletedFlag() == 'Y') {
                    response.setMessage("This account has been deleted!");
                    response.setStatusCode(HttpStatus.UNAUTHORIZED.value());
                    return response;
                } else {
                    Authentication authentication = authenticationManager.authenticate(
                            new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    String jwt = jwtUtils.generateJwtToken(authentication);
                    UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

                    Set<Role> roles = user.getRoles();
                    String otp = "Your otp code is " + otpService.generateOTP(userDetails.getUsername());
                    JwtResponse jwtResponse = new JwtResponse();
                    jwtResponse.setToken(jwt);
                    jwtResponse.setId(userDetails.getId().longValue());
                    jwtResponse.setUsername(loginRequest.getUsername());
                    jwtResponse.setEmail(userDetails.getEmail());
                    jwtResponse.setFirstName(user.getFirstName());
                    jwtResponse.setLastName(user.getLastName());
                    jwtResponse.setFirstLogin(user.getFirstLogin());
                    jwtResponse.setIsSystemGenPassword(user.getIsSystemGenPassword());
                    jwtResponse.setRoles(roles);
                    jwtResponse.setUuid(CurrentUserContext.getCurrentActiveUser().getUuid());
                    jwtResponse.setStatus(CurrentUserContext.getCurrentActiveUser().getStatus());
                    jwtResponse.setLoginAt(CurrentUserContext.getCurrentActiveUser().getLoginAt());
                    jwtResponse.setAddress(CurrentUserContext.getCurrentActiveUser().getAddress());
                    jwtResponse.setOs(CurrentUserContext.getCurrentActiveUser().getOs());
                    jwtResponse.setBrowser(CurrentUserContext.getCurrentActiveUser().getBrowser());
                    response.setEntity(jwtResponse);

//                    Mailparams mailsample = new Mailparams();
//                    mailsample.setEmail(userDetails.getEmail());
//                    mailsample.setSubject("OTP Code");
//                    mailsample.setMessage(otp);
                    user.setIsAcctActive(true);
                    userRepository.save(user);
                    response.setMessage("Authenticated Successfully! Kindly verify token to complete Authorization process.");
                    response.setStatusCode(HttpStatus.OK.value());
                    return response;

                }
            }
        }
    }


    @GetMapping("sign/in/otp")
    public EntityResponse<?> signInOtp(
            @RequestParam(required = false) Integer otpCode,
            @RequestParam(required = false) String userName) {
        try {
            EntityResponse response = new EntityResponse();
            if (userName.equals(null) || userName.equals(" ")) {
                response.setMessage("You Must Provide Account UserName to Proceed: !!");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
            } else if (otpCode == null || otpCode == ' ') {
                response.setMessage("You Must Provide Account OTP for Verification: !!");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
            } else {
                OTP otp = otpRepository.validOTP(userName);
                if (Objects.isNull(otp) || !Objects.equals(otp.getOtp(), otpCode)) {
                    response.setMessage("Failed: !! OTP Code " + otpCode + " for user " + userName + " is invalid: !! " + " Make Sure You are FULLY Authenticated to avoid Anonymous!");
                    response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                } else {
                    Optional<Users> userCheck = userRepository.findByUsername(userName);
                    Users user = userCheck.get();
                    //gen jwt
                    JwtResponse jwtResponse = getAccessToken(userName);
                    jwtResponse.setOtpEnabled(true);
                    user.setIsAcctActive(true);
                    userRepository.save(user);
                    response.setMessage("Welcome " + user.getUsername() + ", You have been Authenticated Successfully at " + new Date());
                    response.setStatusCode(HttpStatus.OK.value());
                    response.setEntity(jwtResponse);
                }
            }
            return response;
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    private JwtResponse getAccessToken(String username) {
        Optional<Users> userCheck = userRepository.findByUsername(username);
        Users user = userCheck.get();
        String jwt = jwtUtils.generateJwtTokenWithUsername(username);
        Set<Role> roles = user.getRoles();
        JwtResponse jwtResponse = new JwtResponse();
        jwtResponse.setToken(jwt);
        jwtResponse.setId(user.getSn().longValue());
        jwtResponse.setUsername(user.getUsername());
        jwtResponse.setEmail(user.getEmail());
        jwtResponse.setFirstName(user.getFirstName());
        jwtResponse.setLastName(user.getLastName());
        jwtResponse.setFirstLogin(user.getFirstLogin());
        jwtResponse.setIsSystemGenPassword(user.getIsSystemGenPassword());
        jwtResponse.setRoles(roles);
        jwtResponse.setUuid(CurrentUserContext.getCurrentActiveUser().getUuid());
        jwtResponse.setStatus(CurrentUserContext.getCurrentActiveUser().getStatus());
        jwtResponse.setLoginAt(CurrentUserContext.getCurrentActiveUser().getLoginAt());
        jwtResponse.setAddress(CurrentUserContext.getCurrentActiveUser().getAddress());
        jwtResponse.setOs(CurrentUserContext.getCurrentActiveUser().getOs());
        jwtResponse.setBrowser(CurrentUserContext.getCurrentActiveUser().getBrowser());
        return jwtResponse;
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) throws MessagingException {
        EntityResponse response = new EntityResponse();
        if (UserRequestContext.getCurrentUser().isEmpty()) {
            response.setMessage("User Name not present in the Request Header");
            response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            PasswordGeneratorUtil passwordGeneratorUtil = new PasswordGeneratorUtil();
            String generatedPassword = passwordGeneratorUtil.generatePassayPassword();
            // Create new user's account
            signUpRequest.setPassword(generatedPassword);
            if (validateUser(signUpRequest).getStatusCode() == HttpStatus.OK.value()) {
                Users user = new Users();
                Set<Role> roles = new HashSet<>();
                Optional<Role> role = roleRepository.findById(Long.valueOf(signUpRequest.getRoleFk()));
                if (role.isPresent()) {
                    roles.add(role.get());
                } else {
                    Optional<Role> defRole = roleRepository.findByName(ERole.ROLE_USER.toString());
                    roles.add(defRole.get());
                }
                user.setRoles(roles);
                user.setPostedTime(new Date());
                user.setPostedFlag('Y');
                user.setPostedBy(UserRequestContext.getCurrentUser());
                user.setIsAcctLocked(false);
                user.setFirstLogin('Y');
                user.setFirstName(signUpRequest.getFirstName());
                user.setLastName(signUpRequest.getLastName());
                user.setPhoneNo(signUpRequest.getPhoneNo());
                user.setEmail(signUpRequest.getEmail());
                user.setUsername(signUpRequest.getUsername());
                user.setPassword(encoder.encode(signUpRequest.getPassword()));
                Users createUser = userRepository.save(user);
////                        String mailMessage = "Dear " + user.getFirstName() + " your account has been successfully created using username " + user.getUsername()
////                                + " and password " + signUpRequest.getPassword() + " Login in to change your password.";
////                        mailService.sendEmail(user.getEmail(), mailMessage, "Account Successfully Created");
////                        System.out.println(mailMessage);
////                        Mailparams mailsample = new Mailparams();
////                        mailsample.setEmail(user.getEmail());
////                        mailsample.setSubject("Account Successfully Created");
////                        mailsample.setMessage(mailMessage);
                response.setMessage("User " + createUser.getUsername() + " has been registered successfully on " + new Date() + " Your Password is : " + signUpRequest.getPassword());
                response.setStatusCode(HttpStatus.CREATED.value());
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(validateUser(signUpRequest), HttpStatus.OK);
            }

        }
    }

    public EntityResponse validateUser(SignupRequest signupRequest) {
        EntityResponse response = new EntityResponse();
        PasswordData passwords = new PasswordData(signupRequest.getPassword());
//        TODO: Password Should not contain username
        Rule rule = new UsernameRule();
        PasswordValidator usernamevalidator = new PasswordValidator(rule);
        passwords.setUsername(signupRequest.getUsername());
        RuleResult results = usernamevalidator.validate(passwords);
        if (results.isValid()) {
//            TODO: Username is unique
            if (userRepository.existsByUsername(signupRequest.getUsername())) {
                response.setMessage("Username is already taken!");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                return response;
            } else {
//                TODO: Email is unique
                if (userRepository.existsByEmail(signupRequest.getEmail())) {
                    response.setMessage("Email is already in use!");
                    response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                    return response;
                } else {
//                    TODO: Phone number is unique
                    if (userRepository.existsByPhoneNo(signupRequest.getPhoneNo())) {
                        response.setMessage("The Phone number is already registered to another account!");
                        response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                        return response;
                    } else {
//                        TODO: Check if user has a Role
                        if (signupRequest.getRoleFk() == null) {
                            response.setMessage("You must provide a role!");
                            response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                            response.setEntity("");
                            return response;
                        } else {
                            response.setMessage("User is valid");
                            response.setStatusCode(HttpStatus.OK.value());
                        }
                    }

                }
            }

        } else {
            response.setMessage("Password should not contain the username provided i.e " + signupRequest.getUsername());
            response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
        }
        return response;
    }

    @ExceptionHandler(org.springframework.web.bind.MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public SignupResponse handlePasswordValidationException(MethodArgumentNotValidException e) {
        //Returning password error message as a response.
        return SignupResponse.builder()
                .message(Objects.requireNonNull(e.getBindingResult().getFieldError()).getDefaultMessage())
                .timestamp(Instant.now())
                .build();
    }

    @ExceptionHandler(InvalidRequestParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public SignupResponse handleInvalidRequestParameterResponse(InvalidRequestParameterException e) {

        return SignupResponse.builder()
                .message(e.getMessage())
                .timestamp(Instant.now())
                .build();

    }

    @GetMapping(path = "/users")
    public ResponseEntity<?> getAllUsers() {
        try {
            if (UserRequestContext.getCurrentUser().isEmpty()) {
                EntityResponse response = new EntityResponse();
                response.setMessage("User Name not present in the Request Header");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                List<Users> usersList = userRepository.findAllByDeletedFlag('N');
                if (usersList.size() > 0) {
                    EntityResponse response = new EntityResponse();
                    response.setMessage(HttpStatus.FOUND.getReasonPhrase());
                    response.setStatusCode(HttpStatus.FOUND.value());
                    response.setEntity(usersList);
                    return new ResponseEntity<>(response, HttpStatus.OK);
                } else {
                    EntityResponse response = new EntityResponse();
                    response.setMessage(HttpStatus.NOT_FOUND.getReasonPhrase());
                    response.setStatusCode(HttpStatus.NOT_FOUND.value());
                    response.setEntity(usersList);
                    return new ResponseEntity<>(response, HttpStatus.OK);
                }
            }
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    @GetMapping(path = "/active/sessions")
    public ResponseEntity<?> getActiveSession() throws JsonProcessingException {
        EntityResponse response = new EntityResponse();
        if (UserRequestContext.getCurrentUser().isEmpty()) {
            response.setMessage("User Name not present in the Request Header");
            response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
            response.setEntity("");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            List<Activesession> responseArray = new ArrayList<>();
            HashMap<String, String> map = (HashMap<String, String>) sessionManager.getActiveSession().getEntity();
            Gson g = new Gson();
            for (Map.Entry<String, String> set :
                    map.entrySet()) {
                JwtResponse jwtResponse = g.fromJson(set.getValue(), JwtResponse.class);
                Activesession activesession = new Activesession();
                activesession.setUuid(jwtResponse.getUuid());
                activesession.setUsername(jwtResponse.getUsername());
                activesession.setStatus(jwtResponse.getStatus());
                activesession.setLoginAt(jwtResponse.getLoginAt());
                activesession.setAddress(jwtResponse.getAddress());
                activesession.setOs(jwtResponse.getOs());
                activesession.setBrowser(jwtResponse.getBrowser());
                responseArray.add(activesession);
            }
            response.setMessage(HttpStatus.OK.getReasonPhrase());
            response.setStatusCode(HttpStatus.OK.value());
            response.setEntity(responseArray);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
    }

    @GetMapping(path = "/retrieve/user/by/username")
    public EntityResponse<?> retrieveUserByUserName(@RequestParam String user) {
        try {
            EntityResponse response = new EntityResponse();
            String currentUser = UserRequestContext.getCurrentUser();
            if (currentUser.isEmpty()) {
                response.setMessage("User Name not present in the Request Header");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                response.setEntity(currentUser);
            } else if (user.equals(null) || user.equals(" ")) {
                response.setMessage("You Must Provide Account UserName to Proceed: !!");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
            } else {
                Optional<Users> users = userRepository.findByUsername(user);
                if (users.isPresent()) {
                    response.setMessage("Welcome : " + currentUser + ", User with username " + user + " FOUND : !!");
                    response.setStatusCode(HttpStatus.FOUND.value());
                    response.setEntity(users);
                } else {
                    response.setMessage("Welcome : " + currentUser + ", User with username " + user + " NOT FOUND");
                    response.setStatusCode(HttpStatus.NOT_FOUND.value());
                    response.setEntity(users);
                }
            }
            return response;
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }

    }

    @GetMapping(path = "/find/by/username")
    public EntityResponse<?> getUserByUsername(@RequestParam String user) {
        try {
            EntityResponse response = new EntityResponse();
            String currentUser = UserRequestContext.getCurrentUser();
            if (currentUser.isEmpty()) {
                response.setMessage("User Name not present in the Request Header");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                response.setEntity(currentUser);
            } else if (user.equals(null) || user.equals(" ")) {
                response.setMessage("You Must Provide Account UserName to Proceed: !!");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
            } else {
                Optional<Users> users = userRepository.findByUsername(user);
                if (users.isPresent()) {
                    response.setMessage("Welcome : " + currentUser + ", User with username " + user + " ALREADY Registered: !!");
                    response.setStatusCode(HttpStatus.FOUND.value());
                    response.setEntity(users);
                } else {
                    response.setMessage("Welcome : " + currentUser + ", User with username " + user + " AVAILABLE for new Registration");
                    response.setStatusCode(HttpStatus.NOT_FOUND.value());
                    response.setEntity(users);
                }
            }
            return response;
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }

    }

    @PutMapping(path = "/users/update")
    public ResponseEntity<?> updateUser(@Valid @RequestBody SignupRequest signUpRequest) throws MessagingException {
        EntityResponse response = new EntityResponse();
        if (UserRequestContext.getCurrentUser().isEmpty()) {
            response.setMessage("User Name not present in the Request Header");
            response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
            response.setEntity("");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            Optional<Users> user1 = userRepository.findById(signUpRequest.getSn());
            Users user = user1.get();
            Set<Role> roles = new HashSet<>();
            if (signUpRequest.getRoleFk() == null) {
                response.setMessage("You must provide a role!");
                response.setStatusCode(HttpStatus.BAD_REQUEST.value());
                response.setEntity("");
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                Optional<Role> role = roleRepository.findById(Long.valueOf(signUpRequest.getRoleFk()));
                if (role.isPresent()) {
                    roles.add(role.get());
                } else {
                    Optional<Role> defRole = roleRepository.findByName(ERole.ROLE_USER.toString());
                    roles.add(defRole.get());
                }

                user.setRoles(roles);
                user.setPostedTime(new Date());
                user.setPostedFlag('Y');
                user.setPostedBy(UserRequestContext.getCurrentUser());
                user.setIsAcctLocked(false);
                user.setFirstLogin('Y');
                user.setFirstName(signUpRequest.getFirstName());
                user.setLastName(signUpRequest.getLastName());
                user.setPhoneNo(signUpRequest.getPhoneNo());
                user.setEmail(signUpRequest.getEmail());
                user.setUsername(signUpRequest.getUsername());

                userRepository.save(user);
//                            String mailMessage = "Dear " + signUpRequest.getFirstName() + " your account details has been updated by " + UserRequestContext.getCurrentUser() + " on " + new Date() + ".Your Credentials are <b> " + user.getUsername() + "</b> Password <b>" + user.getPassword() + ".</b>";
//                            Mailparams mailsample = new Mailparams();
//                            mailsample.setEmail(signUpRequest.getEmail());
//                            mailsample.setSubject("Account Updated");
//                            mailsample.setMessage(mailMessage);
                response.setMessage("User " + signUpRequest.getUsername() + " has been updated successfully!");
                response.setStatusCode(HttpStatus.CREATED.value());
                return new ResponseEntity<>(response, HttpStatus.OK);
            }


        }
    }

    @PostMapping(path = "/reset-password")
    public ResponseEntity<?> resetPassword(@Valid @RequestBody PasswordResetRequest passwordResetRequest) throws MessagingException {
        if (passwordResetRequest.getPassword().length() < 6 || passwordResetRequest.getPassword().isEmpty() || passwordResetRequest.getPassword() == null) {
            return ResponseEntity.ok().body(new MessageResponse(HttpStatus.BAD_REQUEST.value(), "Password Can not be less than 6 charecters"));
        } else {
            if (!userRepository.existsByEmail(passwordResetRequest.getEmailAddress())) {
                return ResponseEntity.ok().body(new MessageResponse(HttpStatus.BAD_REQUEST.value(), "No such user exists"));
            } else {
                Users user = userRepository.findByEmail(passwordResetRequest.getEmailAddress()).orElse(null);
                if (passwordResetRequest.getPassword().equals(passwordResetRequest.getConfirmPassword())) {
                    user.setPassword(encoder.encode(passwordResetRequest.getPassword()));
                    user.setFirstLogin('N');
                    user.setIsSystemGenPassword('N');
                    userRepository.save(user);
//                    String mailMessage = "Dear " + user.getFirstName() + " your password has been changed successfully! These are your new Credentials. Username <b> " + user.getUsername()
//                            + " </b> and password is <b> as you just set it </b>.";
//                    mailService.sendEmail(user.getEmail(), mailMessage, "Password Reset Successful");

                    return ResponseEntity.ok().body(new MessageResponse(HttpStatus.OK.value(), "User Password updated successfully"));
                } else {
                    return ResponseEntity.ok().body(new MessageResponse(HttpStatus.BAD_REQUEST.value(), "Password mismatch. Try Again"));
                }
            }
        }
    }

    @PostMapping(path = "/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody Forgotpassword forgotpassword) throws MessagingException {
        if (!userRepository.existsByEmail(forgotpassword.getEmailAddress())) {
            EntityResponse response = new EntityResponse();
            response.setMessage(HttpStatus.NOT_FOUND.getReasonPhrase());
            response.setStatusCode(HttpStatus.NOT_FOUND.value());
            response.setEntity("");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            PasswordGeneratorUtil passwordGeneratorUtil = new PasswordGeneratorUtil();
            String generatedPassword = passwordGeneratorUtil.generatePassayPassword();
            Optional<Users> user = userRepository.findByEmail(forgotpassword.getEmailAddress());
            if (user.isPresent()) {
                Users user1 = user.get();
                user1.setPassword(encoder.encode(generatedPassword));
                user1.setModifiedBy(user.get().getUsername());
                user1.setIsSystemGenPassword('Y');
                user1.setModifiedFlag('Y');
                user1.setModifiedTime(new Date());
                userRepository.save(user1);
//                String mailMessage = "Dear " + user1.getFirstName() + " your password has been updated successfully! These are your new Credentials. Username <b> " + user1.getUsername()
//                        + " </b> and password <b>" + generatedPassword + " </b> Login in to change your password.";
//                mailService.sendEmail(user1.getEmail(), mailMessage, "Password Reset Successfull");
//
//                Mailparams mailsample = new Mailparams();
//                mailsample.setEmail(user1.getEmail());
//                mailsample.setSubject("Password Reset Successfully");
//                mailsample.setMessage(mailMessage);

                EntityResponse response = new EntityResponse();
                response.setMessage("Password Reset Successfully! Password has been sent to the requested email");
                response.setStatusCode(HttpStatus.OK.value());
                response.setEntity("");
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                EntityResponse response = new EntityResponse();
                response.setMessage("User with email address not found!");
                response.setStatusCode(HttpStatus.NOT_FOUND.value());
                response.setEntity("");
                return new ResponseEntity<>(response, HttpStatus.OK);
            }
        }
    }

    @PutMapping(path = "/lock/{id}")
    public EntityResponse lock(@PathVariable Long id) {
        EntityResponse response = new EntityResponse();
        Optional<Users> users = userRepository.findById(id);
        if (users.isPresent()) {
            Users users1 = users.get();
            users1.setIsAcctLocked(true);
            userRepository.save(users1);
            response.setMessage("Logout Successful!");
            response.setStatusCode(HttpStatus.OK.value());
            return response;
        } else {
            response.setMessage("User Not Found");
            response.setStatusCode(HttpStatus.NOT_FOUND.value());
            return response;
        }
    }

    @PutMapping(path = "/unlock/{id}")
    public EntityResponse unlock(@PathVariable Long id) {
        EntityResponse response = new EntityResponse();
        Optional<Users> users = userRepository.findById(id);
        if (users.isPresent()) {
            Users users1 = users.get();
            users1.setIsAcctLocked(false);
            userRepository.save(users1);
            response.setMessage("Locked Successful!");
            response.setStatusCode(HttpStatus.OK.value());
            return response;
        } else {
            response.setMessage("User Not Found");
            response.setStatusCode(HttpStatus.NOT_FOUND.value());
            return response;
        }
    }

    @PutMapping(path = "/logout/{id}")
    public EntityResponse logout(@PathVariable Long id) {
        EntityResponse response = new EntityResponse();
        Optional<Users> users = userRepository.findById(id);
        if (users.isPresent()) {
            Users users1 = users.get();
            users1.setIsAcctActive(false);
            userRepository.save(users1);
            response.setMessage("Unlocked Successful!");
            response.setStatusCode(HttpStatus.OK.value());
            return response;
        } else {
            response.setMessage("User Not Found");
            response.setStatusCode(HttpStatus.NOT_FOUND.value());
            return response;
        }
    }

    @PutMapping(path = "/restore/{id}")
    public ResponseEntity<?> restore(@PathVariable Long id) {
        EntityResponse response = new EntityResponse();
        Optional<Users> users = userRepository.findById(id);
        if (users.isPresent()) {
            Users user = users.get();
            user.setIsAcctActive(true);
            user.setDeletedFlag('N');
            user.setVerifiedFlag('N');
            user.setModifiedFlag('Y');
            user.setModifiedBy(UserRequestContext.getCurrentUser());
            user.setModifiedTime(new Date());
            userRepository.save(user);
            response.setMessage("Unlocked Successful!");
            response.setStatusCode(HttpStatus.OK.value());
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            response.setMessage("User Not Found");
            response.setStatusCode(HttpStatus.NOT_FOUND.value());
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
    }

    //    If is active
    @DeleteMapping(path = "/delete/{id}")
    public EntityResponse delete(@PathVariable Long id) {
        EntityResponse response = new EntityResponse();
        Optional<Users> users = userRepository.findById(id);
        if (users.isPresent()) {
            if (users.get().isAcctLocked) {
                response.setMessage("Active account can not be deleted");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                return response;
            } else {
                Boolean isSuperuser = false;
                Boolean isManager = false;
                for (Role role : users.get().getRoles()) {
                    if (role.getName().equalsIgnoreCase(String.valueOf(ERole.ROLE_SUPERUSER))) {
                        isSuperuser = true;
                    } else if (role.getName().equalsIgnoreCase(String.valueOf(ERole.ROLE_MANAGER))) {
                        isManager = true;
                    }
                }
                if (isSuperuser) {
                    response.setMessage("Superuser can not be deleted");
                    response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                    return response;
                } else {
                    if (isManager) {
                        response.setMessage("Manager can not be deleted");
                        response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                        return response;
                    } else {
                        Users user = users.get();
                        user.setIsAcctActive(false);
                        user.setDeletedFlag('Y');
                        user.setDeletedBy(UserRequestContext.getCurrentUser());
                        user.setDeletedTime(new Date());
                        userRepository.save(user);
                        response.setMessage("User Deleted");
                        response.setStatusCode(HttpStatus.OK.value());
                        return response;
                    }
                }
            }

        } else {
            response.setMessage("User Not Found");
            response.setStatusCode(HttpStatus.NOT_FOUND.value());
            return response;
        }
    }
}