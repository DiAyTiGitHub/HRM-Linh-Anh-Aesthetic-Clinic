package com.globits.keycloak.auth.service;

import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;
import com.globits.hr.dto.UserExtDto;
import com.globits.hr.dto.search.SearchDto;
import javax.ws.rs.core.Response;
import java.util.Collections;
import java.util.List;

@Service
public class KeycloakAdminService {
    @Value("${keycloak.realm}")
    public String realm;

    private final KeycloakProvider kcProvider;

    public KeycloakAdminService(KeycloakProvider keycloakProvider) {
        this.kcProvider = keycloakProvider;
    }

    public Page<UserRepresentation> searchUsers(SearchDto search) {
        Page<UserRepresentation> result;
        UsersResource usersResource = kcProvider.getInstance().realm(realm).users();
        int pageIndex = (search.getPageIndex() > 0) ? search.getPageIndex() : 1;
        int pageSize = (search.getPageSize() > 0) ? search.getPageSize() : 10;
        String userName = search.getKeyword();

        Integer firstResult = (pageIndex - 1) * pageSize;
        Integer maxResult = pageSize;

        List<UserRepresentation> response = usersResource.search(userName, firstResult, maxResult);
        long count = (long) usersResource.count(userName);
        Pageable pageable = PageRequest.of(pageIndex, pageSize);
        result = new PageImpl<UserRepresentation>(response, pageable, count);
        return result;
    }

    public List<UserRepresentation> listUsers(Integer pageIndex, Integer pageSize) {
        UsersResource usersResource = kcProvider.getInstance().realm(realm).users();

        Integer firstResult = (pageIndex - 1) * pageSize;
        Integer maxResult = pageSize;
        List<UserRepresentation> response = usersResource.list(firstResult, maxResult);
        // List<UserRepresentation> response = usersResource.list();
        return response;

    }

    public UserResource getUserResource(String userName) {
        UsersResource usersResource = kcProvider.getInstance().realm(realm).users();
        UserRepresentation user = getUser(userName);
        if (user != null) {
            return usersResource.get(user.getId());
        }
        return null;
    }

    // public void update(UserProfileRequest profile) {
    //     UserResource userResource = getUserResource(profile.getUserName());
    //     CredentialRepresentation credentialRepresentation = createPasswordCredentials("123456");
    //     userResource.resetPassword(credentialRepresentation);
    // }

    public UserRepresentation getUser(String userName) {
        UsersResource usersResource = kcProvider.getInstance().realm(realm).users();
        List<UserRepresentation> users = usersResource.search(userName, true);
        if (users != null && users.size() > 0) {
            return users.get(0);
        }
        return null;
    }

    public Response createKeycloakUser(UserExtDto user) {
        UsersResource usersResource = kcProvider.getInstance().realm(realm).users();
        CredentialRepresentation credentialRepresentation = createPasswordCredentials(user.getPassword());

        UserRepresentation kcUser = new UserRepresentation();
        kcUser.setUsername(user.getEmail());
        kcUser.setCredentials(Collections.singletonList(credentialRepresentation));
        kcUser.setFirstName(user.getFirstname());
        kcUser.setLastName(user.getLastname());
        kcUser.setEmail(user.getEmail());
        kcUser.setEnabled(true);
        kcUser.setEmailVerified(false);

        Response response = usersResource.create(kcUser);

        if (response.getStatus() == 201) {
            // If you want to save the user to your other database, do it here, for example:
            // User localUser = new User();
            // localUser.setFirstName(kcUser.getFirstName());
            // localUser.setLastName(kcUser.getLastName());
            // localUser.setEmail(user.getEmail());
            // localUser.setCreatedDate(Timestamp.from(Instant.now()));
            // String userId = response.getLocation().getPath().replaceAll(".*/([^/]+)$",
            // "$1");
            // usersResource.get(userId).sendVerifyEmail();
            // userRepository.save(localUser);
        }

        return response;

    }

    private static CredentialRepresentation createPasswordCredentials(String password) {
        CredentialRepresentation passwordCredentials = new CredentialRepresentation();
        passwordCredentials.setTemporary(false);
        passwordCredentials.setType(CredentialRepresentation.PASSWORD);
        passwordCredentials.setValue(password);
        return passwordCredentials;
    }

}
