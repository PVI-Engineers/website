package com.pviengineers.careersapi.model;

import jakarta.persistence.Basic;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "career_applications")
public class CareerApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 40)
    private String applicationRef;

    @Column(nullable = false, length = 50)
    private String jobId;

    @Column(nullable = false, length = 150)
    private String jobRole;

    @Column(nullable = false, length = 100)
    private String firstName;

    @Column(nullable = false, length = 100)
    private String lastName;

    @Column(nullable = false, length = 150)
    private String email;

    @Column(nullable = false, length = 30)
    private String phone;

    @Column(nullable = false, length = 120)
    private String currentLocation;

    @Column(nullable = false, length = 40)
    private String willingToRelocate;

    @Column(nullable = false, length = 60)
    private String workAuthorization;

    @Column(nullable = false, length = 150)
    private String currentCompany;

    @Column(nullable = false, length = 150)
    private String currentDesignation;

    @Column(nullable = false, length = 40)
    private String totalExperience;

    @Column(nullable = false, length = 40)
    private String relevantExperience;

    @Column(nullable = false, length = 150)
    private String highestQualification;

    @Column(nullable = false, length = 150)
    private String specialization;

    @Column(nullable = false, length = 20)
    private String graduationYear;

    @Column(nullable = false, length = 40)
    private String currentCtc;

    @Column(nullable = false, length = 40)
    private String expectedCtc;

    @Column(nullable = false, length = 60)
    private String noticePeriod;

    @Column(nullable = false, length = 30)
    private String availableFrom;

    @Column(nullable = false, length = 255)
    private String linkedin;

    @Column(length = 255)
    private String portfolio;

    @Column(nullable = false, length = 1000)
    private String keySkills;

    @Column(nullable = false, length = 2000)
    private String whyJoin;

    @Column(length = 2000)
    private String additionalInfo;

    @Column(nullable = false)
    private Boolean consentPrivacy;

    @Column(nullable = false)
    private Boolean consentBackground;

    @Column(nullable = false, length = 255)
    private String resumeFileName;

    @Column(nullable = false, length = 80)
    private String resumeContentType;

    @Column(nullable = false)
    private Long resumeFileSize;

    @Column(length = 700)
    private String resumeStorageKey;

    @Lob
    @Basic
    @Column(columnDefinition = "bytea")
    private byte[] resumeData;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "application", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<CareerApplicationFile> files = new ArrayList<>();

    @PrePersist
    void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public String getApplicationRef() {
        return applicationRef;
    }

    public void setApplicationRef(String applicationRef) {
        this.applicationRef = applicationRef;
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public String getJobRole() {
        return jobRole;
    }

    public void setJobRole(String jobRole) {
        this.jobRole = jobRole;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCurrentLocation() {
        return currentLocation;
    }

    public void setCurrentLocation(String currentLocation) {
        this.currentLocation = currentLocation;
    }

    public String getWillingToRelocate() {
        return willingToRelocate;
    }

    public void setWillingToRelocate(String willingToRelocate) {
        this.willingToRelocate = willingToRelocate;
    }

    public String getWorkAuthorization() {
        return workAuthorization;
    }

    public void setWorkAuthorization(String workAuthorization) {
        this.workAuthorization = workAuthorization;
    }

    public String getCurrentCompany() {
        return currentCompany;
    }

    public void setCurrentCompany(String currentCompany) {
        this.currentCompany = currentCompany;
    }

    public String getCurrentDesignation() {
        return currentDesignation;
    }

    public void setCurrentDesignation(String currentDesignation) {
        this.currentDesignation = currentDesignation;
    }

    public String getTotalExperience() {
        return totalExperience;
    }

    public void setTotalExperience(String totalExperience) {
        this.totalExperience = totalExperience;
    }

    public String getRelevantExperience() {
        return relevantExperience;
    }

    public void setRelevantExperience(String relevantExperience) {
        this.relevantExperience = relevantExperience;
    }

    public String getHighestQualification() {
        return highestQualification;
    }

    public void setHighestQualification(String highestQualification) {
        this.highestQualification = highestQualification;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public String getGraduationYear() {
        return graduationYear;
    }

    public void setGraduationYear(String graduationYear) {
        this.graduationYear = graduationYear;
    }

    public String getCurrentCtc() {
        return currentCtc;
    }

    public void setCurrentCtc(String currentCtc) {
        this.currentCtc = currentCtc;
    }

    public String getExpectedCtc() {
        return expectedCtc;
    }

    public void setExpectedCtc(String expectedCtc) {
        this.expectedCtc = expectedCtc;
    }

    public String getNoticePeriod() {
        return noticePeriod;
    }

    public void setNoticePeriod(String noticePeriod) {
        this.noticePeriod = noticePeriod;
    }

    public String getAvailableFrom() {
        return availableFrom;
    }

    public void setAvailableFrom(String availableFrom) {
        this.availableFrom = availableFrom;
    }

    public String getLinkedin() {
        return linkedin;
    }

    public void setLinkedin(String linkedin) {
        this.linkedin = linkedin;
    }

    public String getPortfolio() {
        return portfolio;
    }

    public void setPortfolio(String portfolio) {
        this.portfolio = portfolio;
    }

    public String getKeySkills() {
        return keySkills;
    }

    public void setKeySkills(String keySkills) {
        this.keySkills = keySkills;
    }

    public String getWhyJoin() {
        return whyJoin;
    }

    public void setWhyJoin(String whyJoin) {
        this.whyJoin = whyJoin;
    }

    public String getAdditionalInfo() {
        return additionalInfo;
    }

    public void setAdditionalInfo(String additionalInfo) {
        this.additionalInfo = additionalInfo;
    }

    public Boolean getConsentPrivacy() {
        return consentPrivacy;
    }

    public void setConsentPrivacy(Boolean consentPrivacy) {
        this.consentPrivacy = consentPrivacy;
    }

    public Boolean getConsentBackground() {
        return consentBackground;
    }

    public void setConsentBackground(Boolean consentBackground) {
        this.consentBackground = consentBackground;
    }

    public String getResumeFileName() {
        return resumeFileName;
    }

    public void setResumeFileName(String resumeFileName) {
        this.resumeFileName = resumeFileName;
    }

    public String getResumeContentType() {
        return resumeContentType;
    }

    public void setResumeContentType(String resumeContentType) {
        this.resumeContentType = resumeContentType;
    }

    public Long getResumeFileSize() {
        return resumeFileSize;
    }

    public void setResumeFileSize(Long resumeFileSize) {
        this.resumeFileSize = resumeFileSize;
    }

    public String getResumeStorageKey() {
        return resumeStorageKey;
    }

    public void setResumeStorageKey(String resumeStorageKey) {
        this.resumeStorageKey = resumeStorageKey;
    }

    public byte[] getResumeData() {
        return resumeData;
    }

    public void setResumeData(byte[] resumeData) {
        this.resumeData = resumeData;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public List<CareerApplicationFile> getFiles() {
        return files;
    }

    public void addFile(CareerApplicationFile file) {
        file.setApplication(this);
        this.files.add(file);
    }
}
