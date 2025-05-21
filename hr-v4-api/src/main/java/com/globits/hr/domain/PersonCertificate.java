package com.globits.hr.domain;

import java.util.Date;

import com.globits.core.domain.FileDescription;
import com.globits.core.dto.FileDescriptionDto;
import jakarta.persistence.*;

import com.globits.core.domain.BaseObject;
import com.globits.core.domain.Person;

@Table(name = "tbl_person_certificate")
@Entity
public class PersonCertificate extends BaseObject {
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "person_id")
    private Person person;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "certificate_id")
    private Certificate certificate;
    @Column(name = "issueDate")
    private Date issueDate;
    @Column(name = "level")
    private String level;
    @Column(name = "name")
    private String name;
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "certificate_file_id")
    private FileDescription certificateFile;


    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public Certificate getCertificate() {
        return certificate;
    }

    public void setCertificate(Certificate certificate) {
        this.certificate = certificate;
    }

    public Date getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(Date issueDate) {
        this.issueDate = issueDate;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public FileDescription getCertificateFile() {
        return certificateFile;
    }

    public void setCertificateFile(FileDescription certificateFile) {
        this.certificateFile = certificateFile;
    }
}
