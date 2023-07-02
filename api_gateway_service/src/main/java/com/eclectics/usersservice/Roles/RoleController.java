package com.eclectics.usersservice.Roles;

import com.eclectics.usersservice.utils.EntityResponse;
import com.eclectics.usersservice.utils.HttpInterceptor.UserRequestContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("/auth/role")
@Slf4j
public class RoleController {
    private final RoleRepository roleRepository;
    private final RoleService roleService;

    public RoleController(RoleRepository roleRepository, RoleService roleService) {
        this.roleRepository = roleRepository;
        this.roleService = roleService;
    }

    @PostMapping("/add")
    public ResponseEntity<?> addRole(@RequestBody Role role) {
        try {
            if (UserRequestContext.getCurrentUser().isEmpty()) {
                EntityResponse response = new EntityResponse();
                response.setMessage("User Name not present in the Request Header");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                Optional<Role> checkCode = roleRepository.findByRoleCode(role.getRoleCode());
                if (checkCode.isPresent()) {
                    EntityResponse response = new EntityResponse();
                    response.setMessage("Role with Code " + role.getRoleCode() + " Already Mapped to Role with Name " + checkCode.get().getName());
                    response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                    return new ResponseEntity<>(response, HttpStatus.OK);
                } else {
                    Optional<Role> checkName = roleRepository.findByName("ROLE_" + role.getName());
                    if (checkName.isPresent()) {
                        EntityResponse response = new EntityResponse();
                        response.setMessage("Role with Name " + role.getName() + " Already Mapped to Role with Code " + checkName.get().getRoleCode());
                        response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                        return new ResponseEntity<>(response, HttpStatus.OK);
                    } else {
                        role.setPostedBy(UserRequestContext.getCurrentUser());
                        String name = role.getName().toUpperCase();
                        role.setName(name);
                        role.setPostedFlag('Y');
                        role.setPostedTime(new Date());
                        Role newRole = roleService.addRole(role);
                        EntityResponse response = new EntityResponse();
                        response.setStatusCode(HttpStatus.CREATED.value());
                        response.setMessage("Role With Name " + newRole.getName() + " CREATED Successfully At " + newRole.getPostedTime());
                        response.setEntity(newRole);
                        return new ResponseEntity<>(response, HttpStatus.OK);
                    }
                }

            }
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllRoles() {
        try {
            if (UserRequestContext.getCurrentUser().isEmpty()) {
                EntityResponse response = new EntityResponse();
                response.setMessage("User Name not present in the Request Header");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                List<Role> role = roleRepository.findByDeletedFlag('N');
                if (role.size() > 0) {
                    EntityResponse response = new EntityResponse();
                    response.setMessage(HttpStatus.FOUND.getReasonPhrase());
                    response.setStatusCode(HttpStatus.FOUND.value());
                    response.setEntity(role);
                    return new ResponseEntity<>(response, HttpStatus.OK);
                } else {
                    EntityResponse response = new EntityResponse();
                    response.setMessage(HttpStatus.NOT_FOUND.getReasonPhrase());
                    response.setStatusCode(HttpStatus.NOT_FOUND.value());
                    response.setEntity(role);
                    return new ResponseEntity<>(response, HttpStatus.OK);
                }

            }
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    @GetMapping("/all/active")
    public ResponseEntity<?> getAllActiveRoles() {
        try {
            if (UserRequestContext.getCurrentUser().isEmpty()) {
                EntityResponse response = new EntityResponse();
                response.setMessage("User Name not present in the Request Header");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {

                List<Role> role = roleRepository.findByDeletedFlagAndVerifiedFlag('N', 'Y');
                if (role.size() > 0) {
                    EntityResponse response = new EntityResponse();
                    response.setMessage(HttpStatus.FOUND.getReasonPhrase());
                    response.setStatusCode(HttpStatus.FOUND.value());
                    response.setEntity(role);
                    return new ResponseEntity<>(response, HttpStatus.OK);
                } else {
                    EntityResponse response = new EntityResponse();
                    response.setMessage(HttpStatus.NOT_FOUND.getReasonPhrase());
                    response.setStatusCode(HttpStatus.NOT_FOUND.value());
                    response.setEntity(role);
                    return new ResponseEntity<>(response, HttpStatus.OK);
                }

            }
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }


    @GetMapping("/find/{id}")
    public ResponseEntity<?> getRoleById(@PathVariable("id") Long id) {
        try {
            if (UserRequestContext.getCurrentUser().isEmpty()) {
                EntityResponse response = new EntityResponse();
                response.setMessage("User Name not present in the Request Header");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {

                Role role = roleService.findById(id);
                EntityResponse response = new EntityResponse();
                response.setMessage(HttpStatus.OK.getReasonPhrase());
                response.setStatusCode(HttpStatus.OK.value());
                response.setEntity(role);
                return new ResponseEntity<>(response, HttpStatus.OK);
            }

        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    @GetMapping("/find/by/role/code/{roleCode}")
    public ResponseEntity<?> getRoleByRoleCode(@PathVariable("roleCode") String roleCode) {
        try {
            if (UserRequestContext.getCurrentUser().isEmpty()) {
                EntityResponse response = new EntityResponse();
                response.setMessage("User Name not present in the Request Header");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {

                Optional<Role> role = roleRepository.findByRoleCode(roleCode);
                if (role.isPresent()) {
                    EntityResponse response = new EntityResponse();
                    response.setMessage("Role with Code " + role.get().getRoleCode() + " Already Mapped to Role with Name " + role.get().getName());
                    response.setStatusCode(HttpStatus.FOUND.value());
                    response.setEntity(role);
                    return new ResponseEntity<>(response, HttpStatus.OK);
                } else {
                    EntityResponse response = new EntityResponse();
                    response.setMessage(HttpStatus.NOT_FOUND.getReasonPhrase());
                    response.setStatusCode(HttpStatus.NOT_FOUND.value());
                    return new ResponseEntity<>(response, HttpStatus.OK);
                }

            }
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    @PutMapping("/modify")
    public ResponseEntity<?> updateRole(@RequestBody Role role) {
        try {
            if (UserRequestContext.getCurrentUser().isEmpty()) {
                EntityResponse response = new EntityResponse();
                response.setMessage("User Name not present in the Request Header");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                response.setEntity("");
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {

                role.setModifiedBy(UserRequestContext.getCurrentUser());
                Optional<Role> role1 = roleRepository.findById(role.getId());
                if (role1.isPresent()) {
                    role.setModifiedFlag('Y');
                    role.setVerifiedFlag('N');
                    role.setModifiedTime(new Date());
                    role.setPostedTime(role1.get().getPostedTime());
                    role.setPostedBy(role1.get().getPostedBy());
                    role.setModifiedBy(UserRequestContext.getCurrentUser());
                    roleService.updateRole(role);
                    EntityResponse response = new EntityResponse();
                    response.setMessage("ROLE WITH NAME " + role.getName() + " MODIFIED SUCCESSFULLY AT " + role.getModifiedTime());
                    response.setStatusCode(HttpStatus.OK.value());
                    response.setEntity(role);
                    return new ResponseEntity<>(response, HttpStatus.OK);
                } else {
                    EntityResponse response = new EntityResponse();
                    response.setMessage(HttpStatus.NOT_FOUND.getReasonPhrase());
                    response.setStatusCode(HttpStatus.NOT_FOUND.value());
                    response.setEntity("");
                    return new ResponseEntity<>(response, HttpStatus.OK);
                }

            }
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    @PutMapping("/verify/{id}")
    public ResponseEntity<?> verify(@PathVariable Long id) {
        try {
            if (UserRequestContext.getCurrentUser().isEmpty()) {
                EntityResponse response = new EntityResponse();
                response.setMessage("User Name not present in the Request Header");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                response.setEntity("");
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {

                Optional<Role> role1 = roleRepository.findById(id);
                if (role1.isPresent()) {
                    Role role = role1.get();
                    // Check Maker Checker
                    if (role.getPostedBy().equalsIgnoreCase(UserRequestContext.getCurrentUser())) {
                        EntityResponse response = new EntityResponse();
                        response.setMessage("You Can Not Verify What you initiated");
                        response.setStatusCode(HttpStatus.UNAUTHORIZED.value());
                        response.setEntity("");
                        return new ResponseEntity<>(response, HttpStatus.OK);
                    } else {
                        role.setVerifiedFlag('Y');
                        role.setVerifiedTime(new Date());
                        role.setVerifiedBy(UserRequestContext.getCurrentUser());
                        roleRepository.save(role);
                        EntityResponse response = new EntityResponse();
                        response.setMessage("ROLE WITH NAME " + role.getName() + " VERIFIED SUCCESSFULLY AT " + role.getVerifiedTime());
                        response.setStatusCode(HttpStatus.OK.value());
                        response.setEntity(role);
                        return new ResponseEntity<>(response, HttpStatus.OK);
                    }
                } else {
                    EntityResponse response = new EntityResponse();
                    response.setMessage(HttpStatus.NOT_FOUND.getReasonPhrase());
                    response.setStatusCode(HttpStatus.NOT_FOUND.value());
                    response.setEntity("");
                    return new ResponseEntity<>(response, HttpStatus.OK);
                }

            }
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteRole(@PathVariable("id") Long id) {
        try {
            if (UserRequestContext.getCurrentUser().isEmpty()) {
                EntityResponse response = new EntityResponse();
                response.setMessage("User Name not present in the Request Header");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {

                Optional<Role> checkId = roleRepository.findById(id);
                if (checkId.isPresent()) {
                    if (checkId.get().getDeletedFlag().equals('Y')) {
                        EntityResponse response = new EntityResponse();
                        response.setMessage("ROLE WITH NAME " + checkId.get().getName() + " ALREADY DELETED ON " + checkId.get().getDeletedTime());
                        response.setStatusCode(HttpStatus.OK.value());
                        return new ResponseEntity<>(response, HttpStatus.OK);
                    } else {
                        Role role = checkId.get();
                        role.setDeletedFlag('Y');
                        role.setDeletedTime(new Date());
                        role.setPostedTime(checkId.get().getPostedTime());
                        role.setPostedBy(checkId.get().getPostedBy());
                        role.setDeletedBy(UserRequestContext.getCurrentUser());
                        Role deleteRole = roleRepository.save(role);
                        EntityResponse response = new EntityResponse();
                        response.setMessage("ROLE WITH NAME " + deleteRole.getName() + " DELETED SUCCESSFULLY AT " + deleteRole.getDeletedTime());
                        response.setStatusCode(HttpStatus.OK.value());
                        response.setEntity(role);
                        return new ResponseEntity<>(response, HttpStatus.OK);
                    }
                } else {
                    EntityResponse response = new EntityResponse();
                    response.setMessage(HttpStatus.NOT_FOUND.getReasonPhrase());
                    response.setStatusCode(HttpStatus.NOT_FOUND.value());
                    response.setEntity("");
                    return new ResponseEntity<>(response, HttpStatus.OK);
                }

            }
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    @DeleteMapping("/permanent/delete/{id}")
    public EntityResponse deleteRolePermanently(@PathVariable("id") Long id) {
        try {
            EntityResponse response = new EntityResponse();
            if (UserRequestContext.getCurrentUser().isEmpty()) {
                response.setMessage("User Name not present in the Request Header");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
            } else {

                Optional<Role> checkId = roleRepository.findById(id);
                if (checkId.isPresent()) {
                    roleRepository.deleteById(id);
                    response.setMessage("ROLE WITH NAME " + checkId.get().getName() + " DELETED PERMANENTLY AT " + new Date());
                    response.setStatusCode(HttpStatus.OK.value());
                } else {
                    response.setMessage(HttpStatus.NOT_FOUND.getReasonPhrase());
                    response.setStatusCode(HttpStatus.NOT_FOUND.value());
                }

            }
            return response;
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

}
