package com.globits.keycloak.auth.controller;


import org.keycloak.KeycloakPrincipal;
import org.keycloak.KeycloakSecurityContext;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.keycloak.representations.AccessToken;
import org.keycloak.representations.idm.UserRepresentation;
import org.slf4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import com.globits.hr.dto.search.SearchDto;
import com.globits.keycloak.auth.service.KeycloakAdminService;
import com.globits.keycloak.auth.service.KeycloakProvider;
import com.globits.keycloak.auth.utils.Constants;

@RestController
@RequestMapping("/api/keycloak-user")
public class KeycloakUserController {
    @Autowired
    private KeycloakAdminService kcAdminClient;

    @Autowired
    private KeycloakProvider kcProvider;

    private static final Logger LOG = org.slf4j.LoggerFactory.getLogger(KeycloakUserController.class);


    public KeycloakUserController(KeycloakAdminService kcAdminClient, KeycloakProvider kcProvider) {
        this.kcProvider = kcProvider;
        this.kcAdminClient = kcAdminClient;
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping(value = "/currentuser")
    public AccessToken getCurrentUser() {
    	Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    	KeycloakAuthenticationToken token = (KeycloakAuthenticationToken) authentication;
        
    	KeycloakPrincipal<KeycloakSecurityContext> principal = (KeycloakPrincipal<KeycloakSecurityContext>)token.getPrincipal();
        String userName = principal.getKeycloakSecurityContext().getToken().getPreferredUsername();
        return principal.getKeycloakSecurityContext().getToken();
    }
    // @GetMapping(value = "/{userName}")
    // public ResponseEntity<UserRepresentation> getUser(@PathVariable String userName) {
    // 	UserRepresentation response = kcAdminClient.getUser(userName);
    // 	UserProfileRequest profile = new UserProfileRequest();
    // 	profile.setUserName(userName);
    // 	kcAdminClient.executeActionsEmail(profile);
    //     return ResponseEntity.ok(response);
    // }

    // @RequestMapping(value = "/list/{pageIndex}/{pageSize}", method = RequestMethod.GET)
    // @Secured({Constants.ROLE_ADMIN_KEY_CLOAK})
    // public ResponseEntity<?> listUsers(@PathVariable int pageIndex, @PathVariable int pageSize) {
    // 	List<UserRepresentation> result = kcAdminClient.listUsers(pageIndex,pageSize);
    // 	return ResponseEntity.ok(result);
    // }
    
    //@Secured({Constants.ROLE_ADMIN_KEY_CLOAK})
    @PostMapping(value = "/paging-user")
    @Secured({Constants.ROLE_ADMIN_KEY_CLOAK})
    public ResponseEntity<?> searchUser(@RequestBody SearchDto search) {
    	Page<UserRepresentation> result  = kcAdminClient.searchUsers(search);
    	return ResponseEntity.ok(result);
    }
  
    // @Secured({Constants.ROLE_ADMIN_KEY_CLOAK})
    // @PostMapping(value = "/create")
    // public ResponseEntity<?> createUser(@RequestBody CreateUserRequest user) {
    //     Response createdResponse = kcAdminClient.createKeycloakUser(user);
    //     return ResponseEntity.status(createdResponse.getStatus()).build();

    // }

    // @PostMapping("/login")
    // public ResponseEntity<AccessTokenResponse> login(@NotNull @RequestBody LoginRequest loginRequest) {
    //     Keycloak keycloak = kcProvider.newKeycloakBuilderWithPasswordCredentials(loginRequest.getUsername(), loginRequest.getPassword()).build();

    //     AccessTokenResponse accessTokenResponse = null;
    //     try {
    //         accessTokenResponse = keycloak.tokenManager().getAccessToken();
    //         return ResponseEntity.status(HttpStatus.OK).body(accessTokenResponse);
    //     } catch (BadRequestException ex) {
    //         LOG.warn("invalid account. User probably hasn't verified email.", ex);
    //         return ResponseEntity.status(HttpStatus.FORBIDDEN).body(accessTokenResponse);
    //     }

    // }

	

}
