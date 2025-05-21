package com.globits.hr.service.impl;

import com.globits.core.Constants;
import com.globits.core.domain.Department;
import com.globits.core.domain.Ethnics;
import com.globits.core.domain.Organization;
import com.globits.core.domain.Person;
import com.globits.core.dto.ActivityLogDto;
import com.globits.core.dto.OrganizationDto;
import com.globits.core.dto.PersonDto;
import com.globits.core.repository.OrganizationRepository;
import com.globits.core.service.ActivityLogService;
import com.globits.core.utils.SecurityUtils;
import com.globits.core.utils.SerializableUtil;
import com.globits.hr.HrConstants;
import com.globits.hr.domain.HRDepartment;
import com.globits.hr.domain.Position;
import com.globits.hr.domain.Staff;
import com.globits.hr.dto.StaffDto;
import com.globits.hr.dto.UserExtRoleDto;
import com.globits.hr.dto.loginkeycloak.AccessDto;
import com.globits.hr.dto.loginkeycloak.CredentialDto;
import com.globits.hr.dto.loginkeycloak.UserKeyCloackDto;
import com.globits.hr.dto.search.UserSearchDto;
import com.globits.hr.dto.staff.UserWithStaffDto;
import com.globits.hr.repository.HRDepartmentRepository;
import com.globits.hr.repository.StaffRepository;
import com.globits.hr.service.HrRoleService;
import com.globits.hr.service.UserExtService;
import com.globits.hr.utils.RestApiUtils;
import com.globits.keycloak.auth.service.KeycloakAdminService;
import com.globits.keycloak.auth.service.KeycloakProvider;
import com.globits.security.domain.Role;
import com.globits.security.domain.User;
import com.globits.security.domain.UserGroup;
import com.globits.security.dto.RoleDto;
import com.globits.security.dto.UserDto;
import com.globits.security.dto.UserGroupDto;
import com.globits.security.repository.RoleRepository;
import com.globits.security.repository.UserGroupRepository;
import com.globits.security.repository.UserRepository;
import com.globits.security.service.RoleService;
import com.globits.security.service.UserService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.AccessTokenResponse;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Transactional
@Service
public class UserExtServiceImpl implements UserExtService {
    private static final Logger logger = LoggerFactory.getLogger(UserExtServiceImpl.class);
    @Value("${keycloak.realm}")
    public String realm;

    @Autowired
    private KeycloakProvider kcProvider;

    @Autowired
    private KeycloakAdminService kcAdminClient;

    @Autowired
    HRDepartmentRepository departmentRepository;
    @Autowired
    private RoleRepository roleRepository;

    // public KeycloakAdminService(KeycloakProvider keycloakProvider) {
    // this.kcProvider = keycloakProvider;
    // }

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private StaffRepository staffRepository;

    @Autowired
    private Environment env;

    @Autowired
    private ActivityLogService activityLogService;

    @PersistenceContext
    EntityManager manager;

    @Autowired
    private OrganizationRepository organizationRepos;

    @Autowired
    private UserGroupRepository groupRepos;

    @Autowired
    private RoleRepository roleRepos;
    @Autowired
    private HrRoleService roleService;

    @Override
    public Page<UserDto> pagingUsers(UserSearchDto dto) {
        String sqlCount = "Select count(entity.id) from User entity";
        String sql = "select new com.globits.security.dto.UserDto(entity) from User entity where (1=1) ";
        String sqlWhere = "";
        if (dto.getKeyword() != null) {
            sqlWhere = " AND (entity.username LIKE :text OR entity.person.displayName LIKE :text OR entity.email LIKE :text) ";
        }
        sql += sqlWhere;
        Query query = manager.createQuery(sql);
        Query queryCount = manager.createQuery(sqlCount);
        if (dto.getKeyword() != null) {
            query.setParameter("text", '%' + dto.getKeyword() + '%');
        }
        int pageIndex = dto.getPageIndex();
        int pageSize = dto.getPageSize();
        if (pageIndex > 0) {
            pageIndex--;
        } else {
            pageIndex = 0;
        }
        int startPosition = pageIndex * pageSize;
        query.setFirstResult(startPosition);
        query.setMaxResults(pageSize);
        List<UserDto> userList = query.getResultList();
        long count = (long) queryCount.getSingleResult();
        Pageable pageable = PageRequest.of(pageIndex, pageSize);
        return new PageImpl<>(userList, pageable, count);
    }

    // @Override
    // public UserExtDto deleteById(Long userId) {
    // if (userId == null || userId <= 0) {
    // return null;
    // }
    // User user = userRepository.findById(userId).orElse(null);
    // if (user != null && !user.getUsername().equals("admin")) {
    // userRepository.delete(user);
    // return new UserExtDto(user);
    // } else {
    // return null;
    // }
    // }

    // @Override
    // public List<UserExtDto> deleteListId(List<Long> userId) {
    // if (userId == null || userId.size() <= 0) {
    // return null;
    // }
    // List<UserExtDto> ret = new ArrayList<>();
    // for (Long long1 : userId) {
    // UserExtDto dto = deleteById(long1);
    // if (dto != null) {
    // ret.add(dto);
    // }
    // }
    // return ret;
    // }

    @Override
    public ResponseEntity<UserKeyCloackDto> creatUserKeyCloak(UserDto dto) {
        if (dto != null) {
            UserKeyCloackDto dtoKey = new UserKeyCloackDto();
            dtoKey.setCreatedTimestamp(new Date());
            dtoKey.setUsername(dto.getUsername());
            dtoKey.setEnabled(true);
            dtoKey.setTotp(false);
            dtoKey.setEmailVerified(true);
            if (dto.getPerson() != null && dto.getPerson().getFirstName() != null) {
                dtoKey.setFirstName(dto.getPerson().getFirstName());
            } else if (dto.getFirstName() != null) {
                dtoKey.setFirstName(dto.getFirstName());
            }
            if (dto.getPerson() != null && dto.getPerson().getLastName() != null) {
                dtoKey.setLastName(dto.getPerson().getLastName());
            } else if (dto.getLastName() != null) {
                dtoKey.setLastName(dto.getLastName());
            }
            if (dtoKey.getFirstName() == null && dtoKey.getLastName() == null
                    && dto.getPerson().getDisplayName() != null) {
                String[] output = dto.getPerson().getDisplayName().split("\\s", 0);
                String last = "";
                StringBuilder first = new StringBuilder();
                if (output.length == 1) {
                    last = output[output.length - 1];
                } else if (output.length > 1) {
                    last = output[output.length - 1];
                    for (int i = 0; i < output.length - 1; i++) {
                        if (first.length() > 0) {
                            first.append(" ");
                        }
                        first.append(output[i]);
                    }
                }
                dtoKey.setLastName(last);
                dtoKey.setFirstName(first.toString());
            }

            dtoKey.setEmail(dto.getEmail());
            dtoKey.setDisableCredentialTypes(new ArrayList<>());
            dtoKey.setRequiredActions(new ArrayList<>());
            dtoKey.setNotBefore(0);
            dtoKey.setAccess(new AccessDto());
            dtoKey.getAccess().setImpersonate(true);
            dtoKey.getAccess().setManage(true);
            dtoKey.getAccess().setManageGroupMembership(true);
            dtoKey.getAccess().setMapRoles(true);
            dtoKey.getAccess().setView(true);
            dtoKey.setRealmRoles(new ArrayList<>());
            dtoKey.getRealmRoles().add("mb-user");
            dtoKey.setCredentials(new ArrayList<>());
            CredentialDto cDto = new CredentialDto();
            cDto.setType("password");
            cDto.setValue(dto.getPassword());
            dtoKey.getCredentials().add(cDto);
            String username = "admin";
            String password = "admin";
            String urlLogin = "";
            String urlUser = "";
            if (env.getProperty("hrm.urlLogin") != null) {
                urlLogin = env.getProperty("hrm.urlLogin");
            }
            if (env.getProperty("hrm.urlUser") != null) {
                urlUser = env.getProperty("hrm.urlUser");
            }
            System.out.println(urlLogin);
            System.out.println(urlUser);
            return RestApiUtils.post(username, password, urlLogin, urlUser, dtoKey, UserKeyCloackDto.class);
        }

        return null;
    }

    @Override
    public UserDto getCurrentUser() {
        // Authentication authentication =
        // SecurityContextHolder.getContext().getAuthentication();
        // Jwt jwt;
        // jwt = (Jwt) authentication.getPrincipal();
        // String userName = null;
        // if (jwt.getClaims() != null && jwt.getClaims().get("preferred_username") !=
        // null) {
        // userName = jwt.getClaims().get("preferred_username").toString();
        // }
        // User modifiedUser = userRepository.findByUsernameAndPerson(userName);
        // if (modifiedUser != null) {
        // return new UserDto(modifiedUser);
        // }
        // return null;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return null;
        }

        Jwt jwt = (Jwt) authentication.getPrincipal();
        String userName = null;

        if (jwt != null && jwt.getClaims() != null && jwt.getClaims().get("preferred_username") != null) {
            userName = jwt.getClaims().get("preferred_username").toString();
        }
        // String userName =
        // principal.getKeycloakSecurityContext().getToken().getPreferredUsername();

        User modifiedUser = userRepository.findByUsernameAndPerson(userName);

        if (modifiedUser == null) {
            return null;
        }

        // check role IsPositionManager, General Director, Deputy General Director
        return new UserDto(modifiedUser);
    }

    @Override
    public UserDto saveUser(UserDto dto) {
        if (dto == null)
            return null;

        if (dto.getId() != null) {
            // edit exit user
            dto = this.updateUser(dto);
        } else {
            // create user
            UsersResource usersResource = kcProvider.getInstance().realm(realm).users();
            CredentialRepresentation credentialRepresentation = createPasswordCredentials(dto.getPassword());
            // check if keycloak has user or not
            UserResource userResource = kcAdminClient.getUserResource(dto.getUsername());
            if (userResource != null) {
                logger.error("Error: Duplicate user from Keycloak! Remove it");
                return null;
            }

            UserRepresentation kcUser = new UserRepresentation();
            kcUser.setUsername(dto.getUsername());
            kcUser.setCredentials(Collections.singletonList(credentialRepresentation));
            if (dto.getPerson() != null) {
                kcUser.setFirstName(dto.getPerson().getFirstName());
                kcUser.setLastName(dto.getPerson().getLastName());

            }

            kcUser.setEmail(dto.getEmail());
            kcUser.setEnabled(true);
            kcUser.setEmailVerified(true);

            Response response = usersResource.create(kcUser);

            if (response.getStatus() == 201) {
                dto.setActive(true);
                try {
                    userService.save(dto);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    UserResource userKeycloak = kcAdminClient.getUserResource(kcUser.getUsername());
                    userKeycloak.remove();
                }

            }
        }

        return dto;
    }

    @Override
    public ResponseEntity<UserKeyCloackDto> updateUserKeyCloak(UserDto dto) {
        return null;
    }

    @Override
    public StaffDto getCurrentStaff() {
        UserDto dto = this.getCurrentUser();
        Staff staff = null;

        if (dto != null && dto.getUsername() != null) {
            staff = staffRepository.findByUsername(dto.getUsername());
            if (staff == null) {
                List<Staff> staffs = staffRepository.findByCode(dto.getUsername());
                if (staffs != null && !staffs.isEmpty()) {
                    staff = staffs.get(0);
                }
            }

            StaffDto staffDto = new StaffDto(staff);
            this.checkIsPositionManager(staff, staffDto);
            return staffDto;
            // return new StaffDto(staff);
        }
        return null;
    }

    public void checkIsPositionManager(Staff staff, StaffDto staffDto) {
        if (staffDto == null || staffDto.getUser() == null) return;

        if (staff == null || staff.getCurrentPositions() == null || staff.getCurrentPositions().isEmpty())
            return;

        boolean isPositionManager = false;
        for (Position position : staff.getCurrentPositions()) {
            if (position.getDepartment() == null || position.getDepartment().getPositionManager() == null)
                continue;

            if (position.getDepartment().getPositionManager().getId().equals(position.getId())) {
                isPositionManager = true;
            }
        }

        RoleDto roleDto = null;
        if (isPositionManager) {
            roleDto = new RoleDto();
            roleDto.setName(HrConstants.IS_POSITION_MANAGER);
            if (staffDto.getUser() != null && staffDto.getUser().getRoles() == null) {
                staffDto.getUser().setRoles(new HashSet<>());
            }
            staffDto.getUser().getRoles().add(roleDto);
            this.checkIsGeneralDirectorOrDeputyGeneralDirector(staffDto);
        }
    }

    @Override
    public void checkIsGeneralDirectorOrDeputyGeneralDirector(StaffDto staffDto) {
        if (staffDto == null || staffDto.getDepartment() == null || staffDto.getDepartment().getCode() == null) {
            return;
        }

        String departmentCode = staffDto.getDepartment().getCode();

        // Tạm hard-code, nhưng viết dưới dạng tập hợp để dễ mở rộng sau này
        Set<String> generalDirectorDeptCodes = Set.of("PB_0002");
        Set<String> deputyGeneralDirectorDeptCodes = Set.of("PB_0003");

        Set<String> roleNames = new HashSet<>();

        if (generalDirectorDeptCodes.contains(departmentCode)) {
            roleNames.add(HrConstants.IS_GENERAL_DIRECTOR);
        }

        if (deputyGeneralDirectorDeptCodes.contains(departmentCode)) {
            roleNames.add(HrConstants.IS_DEPUTY_GENERAL_DIRECTOR);
        }

        if (!roleNames.isEmpty() && staffDto.getUser() != null) {
            if (staffDto.getUser().getRoles() == null) {
                staffDto.getUser().setRoles(new HashSet<>());
            }

            for (String roleName : roleNames) {
                RoleDto roleDto = new RoleDto();
                roleDto.setName(roleName);
                staffDto.getUser().getRoles().add(roleDto);
            }
        }
    }


    @Override
    public Staff getCurrentStaffEntity() {
        UserDto dto = this.getCurrentUser();
        Staff staff = null;
        if (dto != null && dto.getUsername() != null) {
            staff = staffRepository.findByUsername(dto.getUsername());
            if (staff == null) {
                List<Staff> staffs = staffRepository.findByCode(dto.getUsername());
                if (staffs != null && staffs.size() > 0) {
                    staff = staffs.get(0);
                }
            }

            return staff;
        }

        return null;
    }

    @Override
    public UserDto updateUser(UserDto dto) {
        if (dto == null || dto != null && dto.getId() == null) {
            return null;
        }
        UsersResource usersResource = kcProvider.getInstance().realm(realm).users();

        UserDto user = userService.findByUserId(dto.getId());
        if (user == null)
            return null;

        List<UserRepresentation> users = usersResource.search(user.getUsername(), true);
        if (users == null) {
            // keycloak user not exits
            return null;
        }

        try {
            user = saveUserOnly(dto);
            // set keycloak user infomation
            UserResource userResource = kcAdminClient.getUserResource(user.getUsername());
            UserRepresentation updateUser = userResource.toRepresentation();
            updateUser.setEmail(dto.getEmail());
            updateUser.setLastName(dto.getLastName());
            updateUser.setFirstName(dto.getFirstName());

            if (!dto.getActive().equals(updateUser.isEnabled()))
                updateUser.setEnabled(dto.getActive());
            // update keycloak user infomation
            userResource.update(updateUser);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return user;
    }

    @Override
    public String changePassword(UserDto dto) {
        String message = "";
        if (dto == null || dto != null && dto.getId() == null) {
            message = "Error: Invalid input";
            return message;
        }

        UserDto user = userService.findByUserId(dto.getId());
        if (user == null) {
            message = "error.userNotFound";
            logger.error("Error: Local User not found!");
            return message;
        }

        UserDto loginUser = this.getCurrentUser();
        if (!loginUser.getUsername().equals(user.getUsername())) {
            message = "error.userNotMatch";
            logger.error("Error: Cant not access to different account!");
            return message;
        }

        UserResource userResource = kcAdminClient.getUserResource(user.getUsername());
        Boolean matched = false;
        Keycloak keycloak = kcProvider
                .newKeycloakBuilderWithPasswordCredentials(user.getUsername(), dto.getOldPassword()).build();

        try {
            AccessTokenResponse accessTokenResponse = keycloak.tokenManager().getAccessToken();
            matched = true;
        } catch (BadRequestException ex) {
            message = "error.oldPasswordNotMatch";
            logger.error("Error: Old Password not match!");
            return message;
        }

        if (!matched) {
            message = "error.oldPasswordNotMatch";
            logger.error("Error: Old Password not match!");
            return message;
        }

        if (!dto.getPassword().equals(dto.getConfirmPassword())) {
            message = "error.confirmPasswordNotMatch";
            logger.error("Error: Confirm Password not match to New Password!");
            return message;
        }
        // change keycloak password
        CredentialRepresentation credentialRepresentation = createPasswordCredentials(dto.getPassword());
        userResource.resetPassword(credentialRepresentation);
        // change local password for standalone version
        userService.changePassword(dto);
        message = "success.passwordChanged";
        return message;
    }

    @Override
    public String resetPassword(UserDto dto) {
        String message = "";
        if (dto == null || dto != null && dto.getId() == null) {
            message = "Error: Invalid input";
            return message;
        }

        UserDto user = userService.findByUserId(dto.getId());
        if (user == null) {
            message = "error.userNotFound";
            logger.error("Error: Local User not found!");
            return message;
        }

        UserResource userResource = kcAdminClient.getUserResource(user.getUsername());

        // change keycloak password
        CredentialRepresentation credentialRepresentation = createPasswordCredentials(dto.getPassword());
        userResource.resetPassword(credentialRepresentation);
        // change local password for standalone version
        userService.changePassword(dto);
        message = "success.passwordChanged";
        return message;
    }

    private static CredentialRepresentation createPasswordCredentials(String password) {
        CredentialRepresentation passwordCredentials = new CredentialRepresentation();
        passwordCredentials.setTemporary(false);
        passwordCredentials.setType(CredentialRepresentation.PASSWORD);
        passwordCredentials.setValue(password);
        return passwordCredentials;
    }

    @Override
    public List<UserDto> findListByUserName(String username) {
        String sql = "select new com.globits.security.dto.UserDto(entity) from User entity left join fetch entity.roles where (1=1) ";
        String sqlWhere = "";
        if (username != null) {
            sqlWhere = " AND (entity.username = :username) ";
        }
        sql += sqlWhere;
        Query query = manager.createQuery(sql);
        if (username != null) {
            query.setParameter("username", username);
        }

        List<UserDto> userList = query.getResultList();

        return userList;
    }

    @Override
    public UserWithStaffDto saveUserAndChooseUsingStaff(UserWithStaffDto dto) {
        User currentUser = null;
        if (this.getCurrentStaff() != null && this.getCurrentStaff().getUser() != null)
            currentUser = this.getCurrentStaffEntity().getUser();
        if (this.getCurrentUser() != null) {
            currentUser = this.getCurrentUserEntity();
        }

        if (currentUser == null || currentUser.getId() == null || currentUser.getOrg() == null
                || currentUser.getOrg().getId() == null || dto == null)
            return null;

        if (dto.getOrg() == null && currentUser.getOrg() != null)
            dto.setOrg(new OrganizationDto(currentUser.getOrg()));

        if (dto.getId() != null) {
            // update existed user
            UserDto updatedUser = this.updateUser(dto);

            UserWithStaffDto responseUserWithStaff = new UserWithStaffDto(updatedUser);
            responseUserWithStaff.setStaff(dto.getStaff());

            dto = responseUserWithStaff;
        } else {
            // create new user
            UsersResource usersResource = kcProvider.getInstance().realm(realm).users();
            CredentialRepresentation credentialRepresentation = createPasswordCredentials(dto.getPassword());
            // check if keycloak has user or not
            UserResource userResource = kcAdminClient.getUserResource(dto.getUsername());
            if (userResource != null) {
                logger.error("Error: Duplicate user from Keycloak!");
                userResource.remove();
                // return null;
            }

            UserRepresentation kcUser = new UserRepresentation();
            kcUser.setUsername(dto.getUsername());
            kcUser.setCredentials(Collections.singletonList(credentialRepresentation));
            if (dto.getPerson() != null) {
                kcUser.setFirstName(dto.getPerson().getFirstName());
                kcUser.setLastName(dto.getPerson().getLastName());

            }

            kcUser.setEmail(dto.getEmail());
            kcUser.setEnabled(true);
            kcUser.setEmailVerified(true);

            Response response = usersResource.create(kcUser);

            if (response.getStatus() == 201) {
//                if (true) {
                dto.setActive(true);
                try {
                    UserDto createdUser = saveUserOnly(dto);

                    UserWithStaffDto responseUserWithStaff = new UserWithStaffDto(createdUser);
                    responseUserWithStaff.setStaff(dto.getStaff());

                    dto = responseUserWithStaff;
                } catch (Exception ex) {
                    ex.printStackTrace();
                    UserResource userKeycloak = kcAdminClient.getUserResource(kcUser.getUsername());
                    userKeycloak.remove();
                }

            }
        }

        if (dto.getStaff() == null || dto.getStaff().getId() == null)
            return dto;

        // update choosing staff in 14/12/2024
        Staff chosenStaff = staffRepository.findById(dto.getStaff().getId()).orElse(null);
        if (chosenStaff != null) {
            User newAssignedAccount = userRepository.findById(dto.getId()).orElse(null);
            chosenStaff.setUser(newAssignedAccount);

            chosenStaff = staffRepository.save(chosenStaff);

            StaffDto responseStaff = new StaffDto(chosenStaff, false);

            dto.setStaff(responseStaff);
        }

        // TODO: assign 1 staff to 1 user(account)
//        //user account which is newly assigned to another staff can cause an error
//        //that 2 staffs have the same user account/have the same staffCode which is set by username of user
//
//        //2 staff are STAFF WHO USED TO USE THIS USER(ACCOUNT) and STAFF WHO WILL USE THIS USER(ACCOUNT)
//        Staff onClearStaff = staffRepository.findByUsername(dto.getUsername());
//        Staff onAssignStaff = staffRepository.findById(dto.getStaff().getId()).orElse(null);
//
//        if (onAssignStaff == null) return dto;
//
//        if (onClearStaff != null && onAssignStaff != null && onClearStaff.getId().equals(onAssignStaff.getId())) {
//            //STAFF WHO USED TO USE THIS USER(ACCOUNT) and STAFF WHO WILL USE THIS USER(ACCOUNT) are the same
//            // => DO NOT HAVE TO CHANGE ANYTHING
//            return dto;
//        }
//
//        //first, clear staffCode of the STAFF WHO USED TO USE THIS USER(ACCOUNT)
//        if (onClearStaff == null) {
//            //staff is null => NO STAFF WAS ASSIGNED FOR THIS USER ACCOUNT
//        } else {
//            //old staff using this account is not null => CLEAR fields
//            onClearStaff.setUser(null);
////            onClearStaff.setStaffCode(null);
//
//            onClearStaff = staffRepository.save(onClearStaff); // clear completed
//        }
//
//        //then, assign new staff who using this user account
//        //staffCode is always coincident with username
////        onAssignStaff.setStaffCode(dto.getUsername());
//        User account = userRepository.findById(dto.getId()).orElse(null);
//        if (account == null) {
//            System.out.println("ACCOUNT HAS NOT SAVED IN DATABASE YET");
//            return dto;
//        }
//        onAssignStaff.setUser(account);
//
//        onAssignStaff = staffRepository.save(onAssignStaff);
//
//        StaffDto responseStaff = new StaffDto(onAssignStaff, false);
//
//        dto.setStaff(responseStaff);

        return dto;
    }

    @Override
    @Transactional
    public List<UserWithStaffDto> saveAllUsersWithStaff(List<UserWithStaffDto> dtos) {
        User currentUser = null;
        if (this.getCurrentStaff() != null && this.getCurrentStaff().getUser() != null) {
            currentUser = this.getCurrentStaffEntity().getUser();
        }
        if (this.getCurrentUser() != null) {
            currentUser = this.getCurrentUserEntity();
        }

        if (currentUser == null || currentUser.getId() == null || currentUser.getOrg() == null
                || currentUser.getOrg().getId() == null) {
            return Collections.emptyList();
        }

        List<UserWithStaffDto> savedDtos = new ArrayList<>();

        for (UserWithStaffDto dto : dtos) {
            if (dto == null)
                continue;

            if (dto.getOrg() == null) {
                dto.setOrg(new OrganizationDto(currentUser.getOrg()));
            }

            if (dto.getId() != null) {
                // Update existing user
                UserDto updatedUser = this.updateUser(dto);
                UserWithStaffDto responseUserWithStaff = new UserWithStaffDto(updatedUser);
                responseUserWithStaff.setStaff(dto.getStaff());
                savedDtos.add(responseUserWithStaff);
            } else {
                // Create new user
                UsersResource usersResource = kcProvider.getInstance().realm(realm).users();
                CredentialRepresentation credentialRepresentation = createPasswordCredentials(dto.getPassword());

                // Check if user exists in Keycloak
                UserResource userResource = kcAdminClient.getUserResource(dto.getUsername());
                if (userResource != null) {
                    logger.error("Error: Duplicate user from Keycloak!");
                    userResource.remove();
                    continue;
                }

                UserRepresentation kcUser = new UserRepresentation();
                kcUser.setUsername(dto.getUsername());
                kcUser.setCredentials(Collections.singletonList(credentialRepresentation));
                kcUser.setEmail(dto.getEmail());
                kcUser.setEnabled(true);
                kcUser.setEmailVerified(true);

                if (dto.getPerson() != null) {
                    kcUser.setFirstName(dto.getPerson().getFirstName());
                    kcUser.setLastName(dto.getPerson().getLastName());
                }

                Response response = usersResource.create(kcUser);

                if (response.getStatus() == 201) {
                    dto.setActive(true);
                    try {
                        UserDto createdUser = saveUserOnly(dto);
                        UserWithStaffDto responseUserWithStaff = new UserWithStaffDto(createdUser);
                        responseUserWithStaff.setStaff(dto.getStaff());
                        savedDtos.add(responseUserWithStaff);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        UserResource userKeycloak = kcAdminClient.getUserResource(kcUser.getUsername());
                        userKeycloak.remove();
                    }
                }
            }
        }

        // Process staff assignments
        for (UserWithStaffDto dto : savedDtos) {
            if (dto.getStaff() == null || dto.getStaff().getId() == null)
                continue;

            Staff chosenStaff = staffRepository.findById(dto.getStaff().getId()).orElse(null);
            if (chosenStaff != null) {
                User newAssignedAccount = userRepository.findById(dto.getId()).orElse(null);
                chosenStaff.setUser(newAssignedAccount);
                chosenStaff = staffRepository.save(chosenStaff);
                dto.setStaff(new StaffDto(chosenStaff, false));
            }
        }

        return savedDtos;
    }

    @Override
    public UserDto getUserByStaffId(UUID staffId) {
        if (staffId == null) {
            return null;
        }
        Staff staff = staffRepository.findById(staffId).orElse(null);
        if (staff == null) {
            return null;
        }
        User user = staff.getUser();
        if (user == null) {
            return null;
        }
        return new UserDto(user);
    }

    @Override
    public Boolean hasRoleManageHCNS() {

        boolean hasRoleAdmin = roleService.hasRoleAdmin();
        if (hasRoleAdmin) {
            return true;
        }

        List<HRDepartment> department = departmentRepository.findByCode(HrConstants.PB_HCNS);
        if (department == null || department.isEmpty()) {
            return false;
        }
        HRDepartment hrDepartment = department.get(0);
        if (hrDepartment == null) {
            return false;
        }

        Position positionManger = hrDepartment.getPositionManager();
        if (positionManger == null) {
            return false;
        }
        Staff staff = this.getCurrentStaffEntity();
        if (staff == null) {
            return false;
        }

        StaffDto currentStaff = this.getCurrentStaff();

        return currentStaff.getId().equals(staff.getId());
    }

    @Override
    public UserDto saveUserOnly(UserDto userDto) {
        if (userDto == null) {
            throw new IllegalArgumentException();
        } else {
            User user = null;
            if (userDto.getId() != null) {
                user = (User) this.userRepository.getReferenceById(userDto.getId());
            }

            if (user == null) {
                user = userDto.toEntity();
                user.setJustCreated(true);
                if (userDto.getPassword() != null && userDto.getPassword().length() > 0) {
                    user.setPassword(SecurityUtils.getHashPassword(userDto.getPassword()));
                }
            } else {
                user.setUsername(userDto.getUsername());
                user.setEmail(userDto.getEmail());
                if (userDto.getPassword() != null && userDto.getPassword().length() > 0 && userDto.getChangePass()) {
                    user.setPassword(SecurityUtils.getHashPassword(userDto.getPassword()));
                }
            }

            if (userDto.getOrg() != null && userDto.getOrg().getId() != null) {
                Organization org = (Organization) this.organizationRepos.getReferenceById(userDto.getOrg().getId());
                user.setOrg(org);
            }

            Iterator var4;
            ArrayList gs;
            if (userDto.getRoles() != null) {
                gs = new ArrayList();
                var4 = userDto.getRoles().iterator();

                while (var4.hasNext()) {
                    RoleDto d = (RoleDto) var4.next();
                    Role r = (Role) this.roleRepos.getReferenceById(d.getId());
                    if (r != null) {
                        gs.add(r);
                    }
                }

                user.getRoles().clear();
                user.getRoles().addAll(gs);
            }

            if (userDto.getGroups() != null) {
                gs = new ArrayList();
                var4 = userDto.getGroups().iterator();

                while (var4.hasNext()) {
                    UserGroupDto d = (UserGroupDto) var4.next();
                    UserGroup g = (UserGroup) this.groupRepos.getReferenceById(d.getId());
                    if (g != null) {
                        gs.add(g);
                    }
                }

                user.getGroups().clear();
                user.getGroups().addAll(gs);
            }

            user.setActive(userDto.getActive());
            ActivityLogDto activityLog = new ActivityLogDto();
            String contentLog = "Update User:" + user.getUsername();
            String entityObjectType = user.getClass().getName();
            activityLog.setEntityObjectType(entityObjectType);
            activityLog.setLogType(Constants.ActionLogTypeEnum.SaveOrUpdate.getValue());

            try {
                String objectContent = SerializableUtil.toString(user);
                if (objectContent != null && objectContent.length() < 10000) {
                    contentLog = objectContent;
                }
            } catch (IOException var9) {
                var9.printStackTrace();
            }

            activityLog.setContentLog(contentLog);
            this.activityLogService.saveActivityLog(activityLog);
            user = (User) this.userRepository.save(user);
            return user != null ? new UserDto(user) : null;
        }
    }

    @Override
    public List<Role> getListRole() {
        List<Role> list = roleRepository.findAll();
        if (list != null && list.size() > 0) {
            UserExtRoleDto user = this.getCurrentRoleUser();
            if (user != null) {
                if (user.isRoleSuperAdmin() || user.isRoleAdmin()) {
                    return list;
                } else if (user.isRoleHrManager()) {
                    List<Role> listRole = new ArrayList<>();
                    for (Role item : list) {
                        if (item != null && item.getName() != null && (item.getName().equals(HrConstants.HR_USER)
                                || item.getName().equals(HrConstants.ROLE_USER))) {
                            listRole.add(item);
                        }
                    }
                    return listRole;
                }
            }
        }
        return null;
    }

    @Override
    public UserExtRoleDto getCurrentRoleUser() {
        User modifiedUser = this.getCurrentUserEntity();
        if (modifiedUser != null && modifiedUser.getRoles() != null && modifiedUser.getRoles().size() > 0) {
            UserExtRoleDto dto = new UserExtRoleDto();
            for (Role role : modifiedUser.getRoles()) {
                if (role != null && role.getName() != null) {
                    if (role.getName().trim().equals(HrConstants.ROLE_SUPER_ADMIN.trim())) {
                        dto.setRoleSuperAdmin(true);
                        continue;
                    } else if (role.getName().trim().equals(HrConstants.ROLE_ADMIN.trim())) {
                        dto.setRoleAdmin(true);
                        continue;
                    } else if (role.getName().trim().equals(HrConstants.ROLE_USER.trim())
                            || role.getName().trim().equals(HrConstants.HR_USER.trim())) {
                        dto.setRoleUser(true);
                        continue;
                    } else if (role.getName().trim().equals(HrConstants.HR_MANAGER.trim())) {
                        dto.setRoleHrManager(true);
                        continue;
                    } else if (role.getName().trim().equals(HrConstants.HR_RECRUITMENT.trim())) {
                        dto.setRoleRecruitment(true);
                        continue;
                    } else if (role.getName().trim().equals(HrConstants.HR_INSURANCE_MANAGER.trim())) {
                        dto.setRoleRecruitment(true);
                        continue;
                    } else if (role.getName().trim().equals(HrConstants.SUPER_HR.trim())) {
                        dto.setRoleSuperHr(true);
                        continue;
                    }
                }
            }
            if (modifiedUser.getUsername() != null) {
                Staff staff = staffRepository.findByUsername(modifiedUser.getUsername());
                if (staff == null) {
                    List<Staff> staffs = staffRepository.findByCode(modifiedUser.getUsername());
                    if (staffs != null && staffs.size() > 0) {
                        staff = staffs.get(0);
                    }
                }
                if (staff != null) {
                    dto.setStaffId(staff.getId());
                }

            }
            return dto;
        }
        return null;
    }

    @Override
    public Page<UserWithStaffDto> pagingUserWithStaff(UserSearchDto dto) {
        String sqlCount = "Select count(entity.id) from User entity join entity.person p where (1=1) ";
        String sql = "select new com.globits.security.dto.UserDto(entity) from User entity join entity.person p where (1=1) ";
        String sqlWhere = "";
        if (dto.getKeyword() != null) {
            sqlWhere = " AND (entity.username LIKE :text OR entity.email LIKE :text OR p.firstName LIKE :text OR p.lastName LIKE :text OR p.displayName LIKE :text OR p.staffCode LIKE :text ) ";
        }

        sql += sqlWhere;
        sqlCount += sqlWhere;

        Query query = manager.createQuery(sql);
        Query queryCount = manager.createQuery(sqlCount);
        if (dto.getKeyword() != null) {
            query.setParameter("text", '%' + dto.getKeyword() + '%');
            queryCount.setParameter("text", '%' + dto.getKeyword() + '%');
        }
        int pageIndex = dto.getPageIndex();
        int pageSize = dto.getPageSize();
        if (pageIndex > 0) {
            pageIndex--;
        } else {
            pageIndex = 0;
        }
        int startPosition = pageIndex * pageSize;
        query.setFirstResult(startPosition);
        query.setMaxResults(pageSize);

        List<UserDto> userList = query.getResultList();
        List<UserWithStaffDto> data = new ArrayList<>();

        // return staff who is using this account
        for (UserDto userDto : userList) {
            UserWithStaffDto item = new UserWithStaffDto(userDto);

            // fill data of staff into item
            Staff usingStaff = staffRepository.findByUsername(userDto.getUsername());
            if (usingStaff != null && usingStaff.getId() != null) {
                StaffDto responseStaff = new StaffDto(usingStaff, false);
                item.setStaff(responseStaff);
            }

            data.add(item);
        }

        long count = (long) queryCount.getSingleResult();
        Pageable pageable = PageRequest.of(pageIndex, pageSize);
        return new PageImpl<>(data, pageable, count);
    }

    @Override
    public UserWithStaffDto getUserWithUsingStaff(Long userId) {
        if (userId == null)
            return null;

        User onFindUser = userRepository.findById(userId).orElse(null);
        if (onFindUser == null)
            return null;
        UserWithStaffDto response = new UserWithStaffDto(onFindUser);

        Staff usingStaff = staffRepository.findByUsername(onFindUser.getUsername());
        if (usingStaff == null)
            return response;
        response.setStaff(new StaffDto(usingStaff));

        return response;
    }

    @Override
    public User getCurrentUserEntity() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Jwt jwt = (Jwt) authentication.getPrincipal();
        String userName = null;
        if (jwt != null && jwt.getClaims() != null && jwt.getClaims().get("preferred_username") != null) {
            userName = jwt.getClaims().get("preferred_username").toString();
        }
        User modifiedUser = userRepository.findByUsernameAndPerson(userName);
        if (modifiedUser == null)
            return null;
        return modifiedUser;
    }
}
