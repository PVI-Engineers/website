package com.pviengineers.careersapi.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.Valid;
import java.util.List;

public class CareerApplicationRequest {

    @NotBlank(message = "Job ID is required")
    private String jobId;

    @NotBlank(message = "Job role is required")
    private String jobRole;

    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    @NotBlank(message = "Email is required")
    @Email(message = "Email is invalid")
    private String email;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^[0-9+\\-\\s()]{8,18}$", message = "Phone number is invalid")
    private String phone;

    @NotBlank(message = "Current location is required")
    private String currentLocation;

    @NotBlank(message = "Relocation response is required")
    private String willingToRelocate;

    @NotBlank(message = "Work authorization is required")
    private String workAuthorization;

    @NotBlank(message = "Current company is required")
    private String currentCompany;

    @NotBlank(message = "Current designation is required")
    private String currentDesignation;

    @NotBlank(message = "Total experience is required")
    private String totalExperience;

    @NotBlank(message = "Relevant experience is required")
    private String relevantExperience;

    @NotBlank(message = "Highest qualification is required")
    private String highestQualification;

    @NotBlank(message = "Specialization is required")
    private String specialization;

    @NotBlank(message = "Graduation year is required")
    private String graduationYear;

    @NotBlank(message = "Current CTC is required")
    private String currentCtc;

    @NotBlank(message = "Expected CTC is required")
    private String expectedCtc;

    @NotBlank(message = "Notice period is required")
    private String noticePeriod;

    @NotBlank(message = "Available from date is required")
    private String availableFrom;

    @NotBlank(message = "LinkedIn profile is required")
    @Pattern(regexp = "^https?://[^\\s]+$", message = "LinkedIn profile must be a valid URL")
    private String linkedin;

    @Pattern(regexp = "^(|https?://[^\\s]+)$", message = "Portfolio must be a valid URL")
    private String portfolio;

    @NotBlank(message = "Key skills are required")
    private String keySkills;

    @NotBlank(message = "Why join response is required")
    private String whyJoin;

    private String additionalInfo;

    @NotNull(message = "Privacy consent is required")
    @AssertTrue(message = "Privacy consent is required")
    private Boolean consentPrivacy;

    @NotNull(message = "Background verification consent is required")
    @AssertTrue(message = "Background verification consent is required")
    private Boolean consentBackground;

    @NotEmpty(message = "At least one uploaded file is required")
    @Valid
    private List<CareerApplicationFileReferenceRequest> files;

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

    public List<CareerApplicationFileReferenceRequest> getFiles() {
        return files;
    }

    public void setFiles(List<CareerApplicationFileReferenceRequest> files) {
        this.files = files;
    }
}
