package com.globits.hr.dto;

import java.time.LocalDateTime;
import java.util.Date;



import com.globits.security.domain.User;

public class UserExtDto {
    private String codeName;
    private String username;
    private Boolean active;
    private String displayName;
    private String firstname;
    private String lastname;
    private String note;
    private Date birthDate;
    private LocalDateTime modifyDate;
    private String password;
    private String email;




    public UserExtDto() {

    }

    public UserExtDto(User sr) {
        this.username = sr.getUsername();
        this.codeName = sr.getUsername();
        this.active = sr.getActive();
        this.modifyDate = sr.getModifyDate();
        if (sr.getPerson() != null) {
            this.birthDate = sr.getPerson().getBirthDate();
            if (sr.getPerson().getDisplayName() != null) {
                this.codeName = this.codeName + "(" + sr.getPerson().getDisplayName() + ")";
                this.displayName = sr.getPerson().getDisplayName();
            } else if (sr.getPerson().getLastName() != null && sr.getPerson().getFirstName() != null) {
                this.codeName = this.codeName + "(" + sr.getPerson().getLastName() + " " + sr.getPerson().getFirstName()
                        + ")";
                this.displayName = sr.getPerson().getLastName() + " " + sr.getPerson().getFirstName();
            }
        }
    }

    public String getCodeName() {
        return codeName;
    }

    public void setCodeName(String codeName) {
        this.codeName = codeName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public LocalDateTime getModifyDate() {
        return modifyDate;
    }

    public void setModifyDate(LocalDateTime modifyDate) {
        this.modifyDate = modifyDate;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    
}
