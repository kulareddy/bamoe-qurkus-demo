package com.cvshealth.pbm.ssot.model;

public class Inquiry {
    public enum RiskLevel {
        LOW,
        MEDIUM,
        CRITICAL
    }

    public enum InquiryType {
        ISSUE,
        QUESTION,
        REQUEST
    }

    private String identifier;    
    private InquiryType inquiryType;
    private ArtifactGroup assumptionGroup;
    private String subject;
    private String details;
    private RiskLevel materiality;
    private RiskLevel priority;
    private String primaryContact;
    private InquiryStatus inquiryStatus;
    private StakeHolder agLeader;
    private StakeHolder agMember;

    public Inquiry() {
    }

    public Inquiry(String identifier, InquiryType inquiryType, ArtifactGroup assumptionGroup, String subject, String details, RiskLevel materiality, RiskLevel priority, String primaryContact, InquiryStatus inquiryStatus, StakeHolder agLeader, StakeHolder agMember) {
        this.identifier = identifier;
        this.inquiryType = inquiryType;
        this.assumptionGroup = assumptionGroup;
        this.subject = subject;
        this.details = details;
        this.materiality = materiality;
        this.priority = priority;
        this.primaryContact = primaryContact;
        this.inquiryStatus = inquiryStatus;
        this.agLeader = agLeader;
        this.agMember = agMember;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public InquiryType getInquiryType() {
        return inquiryType;
    }

    public void setInquiryType(InquiryType inquiryType) {
        this.inquiryType = inquiryType;
    }

    public ArtifactGroup getAssumptionGroup() {
        return assumptionGroup;
    }

    public void setAssumptionGroup(ArtifactGroup assumptionGroup) {
        this.assumptionGroup = assumptionGroup;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public RiskLevel getMateriality() {
        return materiality;
    }

    public void setMateriality(RiskLevel materiality) {
        this.materiality = materiality;
    }

    public RiskLevel getPriority() {
        return priority;
    }

    public void setPriority(RiskLevel priority) {
        this.priority = priority;
    }

    public String getPrimaryContact() {
        return primaryContact;
    }

    public void setPrimaryContact(String primaryContact) {
        this.primaryContact = primaryContact;
    }

    public InquiryStatus getInquiryStatus() {
        return inquiryStatus;
    }

    public void setInquiryStatus(InquiryStatus inquiryStatus) {
        this.inquiryStatus = inquiryStatus;
    }

    public StakeHolder getAgLeader() {
        return agLeader;
    }

    public void setAgLeader(StakeHolder agLeader) {
        this.agLeader = agLeader;
    }

    public StakeHolder getAgMember() {
        return agMember;
    }

    public void setAgMember(StakeHolder agMember) {
        this.agMember = agMember;
    }

    @Override
    public String toString() {
        return "{\n" +
                "  \"identifier\": \"" + identifier + "\",\n" +
                "  \"inquiryType\": \"" + inquiryType + "\",\n" +
                "  \"assumptionGroup\": " + (assumptionGroup != null ? assumptionGroup.toString() : null) + ",\n" +
                "  \"subject\": \"" + subject + "\",\n" +
                "  \"details\": \"" + details + "\",\n" +
                "  \"materiality\": \"" + materiality + "\",\n" +
                "  \"priority\": \"" + priority + "\",\n" +
                "  \"primaryContact\": \"" + primaryContact + "\",\n" +
                "  \"inquiryStatus\": " + (inquiryStatus != null ? inquiryStatus.toString() : null) + ",\n" +
                "  \"agLeader\": \"" + agLeader + "\",\n" +
                "  \"agMember\": \"" + agMember + "\"\n" +
                "}";
    }
}
