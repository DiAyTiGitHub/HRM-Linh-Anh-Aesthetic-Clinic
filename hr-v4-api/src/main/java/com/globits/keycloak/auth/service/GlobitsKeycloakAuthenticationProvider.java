package com.globits.keycloak.auth.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.keycloak.KeycloakPrincipal;
import org.keycloak.KeycloakSecurityContext;
import org.keycloak.adapters.springsecurity.account.KeycloakRole;
import org.keycloak.adapters.springsecurity.authentication.KeycloakAuthenticationProvider;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
// import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;

import com.globits.security.dto.RoleDto;
import com.globits.security.dto.UserDto;
import com.globits.security.service.RoleService;
import com.globits.security.service.UserService;

public class GlobitsKeycloakAuthenticationProvider extends KeycloakAuthenticationProvider{
	
	UserService userService;
	
	RoleService roleService;
	public GlobitsKeycloakAuthenticationProvider(){
	}
	
	public GlobitsKeycloakAuthenticationProvider(UserService userService){
		this.userService = userService;
	}	
	
    private GrantedAuthoritiesMapper grantedAuthoritiesMapper;

    public void setGrantedAuthoritiesMapper(GrantedAuthoritiesMapper grantedAuthoritiesMapper) {
        this.grantedAuthoritiesMapper = grantedAuthoritiesMapper;
    }
    
    private Collection<? extends GrantedAuthority> mapAuthorities(
            Collection<? extends GrantedAuthority> authorities) {
        return grantedAuthoritiesMapper != null
            ? grantedAuthoritiesMapper.mapAuthorities(authorities)
            : authorities;
    }
    
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        KeycloakAuthenticationToken token = (KeycloakAuthenticationToken) authentication;
        List<GrantedAuthority> grantedAuthorities = new ArrayList<GrantedAuthority>();
        KeycloakPrincipal<KeycloakSecurityContext> principal = (KeycloakPrincipal<KeycloakSecurityContext>)token.getPrincipal();
        String userName = principal.getKeycloakSecurityContext().getToken().getPreferredUsername();
        
        UserDto user = userService.findByUsername(userName);
		
		if(user!=null) {
			if(user.getRoles()!=null && user.getRoles().size()>0) {
				for(RoleDto role:user.getRoles()) {
					grantedAuthorities.add(new KeycloakRole(role.getName()));
    			}	
			}
			//hashMapUsers.put(userName, user);
		}else {//Create new local user for the resource server
    		//Get Local User here, get local role here then add to result
    		user = new UserDto();
    		user.setUsername(userName);
    		user.setActive(true);
    		userService.save(user);
		}
        return new KeycloakAuthenticationToken(token.getAccount(), token.isInteractive(), mapAuthorities(grantedAuthorities));
    }
}
