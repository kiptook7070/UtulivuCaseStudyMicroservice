package com.eclectics.usersservice;

import com.eclectics.usersservice.MailService.MailService;
import com.eclectics.usersservice.Roles.ERole;
import com.eclectics.usersservice.Roles.Role;
import com.eclectics.usersservice.Roles.RoleRepository;
import com.eclectics.usersservice.Users.Users;
import com.eclectics.usersservice.Users.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.mail.MessagingException;
import java.util.*;

@SpringBootApplication
@EnableZuulProxy
@EnableDiscoveryClient
public class UsersServiceApplication {
    @Autowired
    private UsersRepository userRepository;


    @Autowired
    PasswordEncoder encoder;


    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    MailService mailService;

    @Value("${organisation.superUserEmail}")
    private String superUserEmail;
    @Value("${organisation.superUserFirstName}")
    private String superUserFirstName;
    @Value("${organisation.superUserLastName}")
    private String superUserLastName;
    @Value("${organisation.superUserUserName}")
    private String superUserUserName;
    @Value("${organisation.superUserPhone}")
    private String superUserPhone;
    @Value("${organisation.superUserPassword}")
    private String superUserPassword;
    String NONE = "NONE";



    public static void main(String[] args) {
        SpringApplication.run(UsersServiceApplication.class, args);
    }


    private void initRoles() {
        List<Role> roleList = new ArrayList<>();
        if (roleRepository.findByName(ERole.ROLE_USER.toString()).isEmpty()) {
            Role userRole = new Role();
            userRole.setName(ERole.ROLE_USER.toString());
            userRole.setEntityId("001");
            userRole.setRoleCode("002");
            userRole.setManagerial(false);
            userRole.setPostedBy("SYSTEM");
            userRole.setPostedFlag('Y');
            userRole.setVerifiedBy("SYSTEM");
            userRole.setVerifiedFlag('Y');
            userRole.setVerifiedTime(new Date());
            userRole.setPostedTime(new Date());
            roleList.add(userRole);
        }
        if (roleRepository.findByName(ERole.ROLE_TELLER.toString()).isEmpty()) {
            Role tellerRole = new Role();
            tellerRole.setEntityId("001");
            tellerRole.setRoleCode("003");
            tellerRole.setManagerial(false);
            tellerRole.setPostedBy("SYSTEM");
            tellerRole.setPostedFlag('Y');
            tellerRole.setVerifiedBy("SYSTEM");
            tellerRole.setVerifiedFlag('Y');
            tellerRole.setVerifiedTime(new Date());
            tellerRole.setPostedTime(new Date());
            tellerRole.setName(ERole.ROLE_TELLER.toString());
            roleList.add(tellerRole);
        }
        if (roleRepository.findByName(ERole.ROLE_OFFICER.toString()).isEmpty()) {
            Role officerRole = new Role();
            officerRole.setEntityId("001");
            officerRole.setRoleCode("004");
            officerRole.setManagerial(false);
            officerRole.setPostedBy("SYSTEM");
            officerRole.setPostedFlag('Y');
            officerRole.setVerifiedBy("SYSTEM");
            officerRole.setVerifiedFlag('Y');
            officerRole.setVerifiedTime(new Date());
            officerRole.setPostedTime(new Date());
            officerRole.setName(ERole.ROLE_OFFICER.toString());
            roleList.add(officerRole);
        }

        if (roleRepository.findByName(ERole.ROLE_SENIOR_OFFICER.toString()).isEmpty()) {
            Role seniorOfficerRole = new Role();
            seniorOfficerRole.setEntityId("001");
            seniorOfficerRole.setRoleCode("005");
            seniorOfficerRole.setManagerial(false);
            seniorOfficerRole.setPostedBy("SYSTEM");
            seniorOfficerRole.setPostedFlag('Y');
            seniorOfficerRole.setVerifiedBy("SYSTEM");
            seniorOfficerRole.setVerifiedFlag('Y');
            seniorOfficerRole.setVerifiedTime(new Date());
            seniorOfficerRole.setPostedTime(new Date());
            seniorOfficerRole.setName(ERole.ROLE_SENIOR_OFFICER.toString());
            roleList.add(seniorOfficerRole);
        }
        if (roleRepository.findByName(ERole.ROLE_MANAGER.toString()).isEmpty()) {
            Role managerRole = new Role();
            managerRole.setEntityId("001");
            managerRole.setRoleCode("006");
            managerRole.setManagerial(false);
            managerRole.setPostedBy("SYSTEM");
            managerRole.setPostedFlag('Y');
            managerRole.setVerifiedBy("SYSTEM");
            managerRole.setVerifiedFlag('Y');
            managerRole.setVerifiedTime(new Date());
            managerRole.setPostedTime(new Date());
            managerRole.setName(ERole.ROLE_MANAGER.toString());
            roleList.add(managerRole);
        }

        if (roleRepository.findByName(ERole.ROLE_NONE.toString()).isEmpty()) {
            Role noneRole = new Role();
            noneRole.setEntityId("001");
            noneRole.setRoleCode("007");
            noneRole.setManagerial(false);
            noneRole.setPostedBy("SYSTEM");
            noneRole.setPostedFlag('Y');
            noneRole.setVerifiedBy("SYSTEM");
            noneRole.setVerifiedFlag('Y');
            noneRole.setVerifiedTime(new Date());
            noneRole.setPostedTime(new Date());
            noneRole.setName(ERole.ROLE_NONE.toString());
            roleList.add(noneRole);
        }
        if (roleRepository.findByName(ERole.ROLE_SUPERUSER.toString()).isEmpty()) {
            Role superuserRole = new Role();
            superuserRole.setEntityId("001");
            superuserRole.setRoleCode("001");
            superuserRole.setManagerial(true);
            superuserRole.setPostedBy("SYSTEM");
            superuserRole.setPostedFlag('Y');
            superuserRole.setVerifiedBy("SYSTEM");
            superuserRole.setVerifiedFlag('Y');
            superuserRole.setVerifiedTime(new Date());
            superuserRole.setPostedTime(new Date());
            superuserRole.setName(ERole.ROLE_SUPERUSER.toString());
            roleList.add(superuserRole);
        }
        roleRepository.saveAll(roleList);
    }


    private void initSuperUser() throws MessagingException {
        if (!userRepository.existsByUsername(superUserUserName)) {
            Set<Role> roles = new HashSet<>();
            Role userRole = roleRepository.findByName(ERole.ROLE_SUPERUSER.toString())
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            roles.add(userRole);
            Users user = new Users();
            user.setFirstName(superUserFirstName);
            user.setLastName(superUserLastName);
            user.setEmail(superUserEmail);
            user.setPhoneNo(superUserPhone);
            user.setModifiedBy("SYSTEM");
            user.setUsername(superUserUserName);
            user.setRoles(roles);
            user.setPostedTime(new Date());
            user.setPostedFlag('Y');
            user.setPostedBy("SYSTEM");
            user.setIsAcctLocked(false);
            user.setFirstLogin('Y');
            user.setPassword(encoder.encode(superUserPassword));
            userRepository.save(user);

//            String mailMessage = "Dear " + user.getFirstName() + " your account has been successfully created using username " + user.getUsername()
//                    + " and password " + superUserPassword + " Login in to change your password.";
//            mailService.sendEmail(user.getEmail(), mailMessage, "Account Successfully Created");
//            Mailparams mailsample = new Mailparams();
//            mailsample.setEmail(user.getEmail());
//            mailsample.setSubject("Account Successfully Created");
//            mailsample.setMessage(mailMessage);
        }
    }


    @Bean
    CommandLineRunner runner() {
        return args -> {
            initRoles();
            initSuperUser();
            System.out.println("API GATEWAY INITIALIZED SUCCESSFULLY AT " + new Date());
        };
    }
}