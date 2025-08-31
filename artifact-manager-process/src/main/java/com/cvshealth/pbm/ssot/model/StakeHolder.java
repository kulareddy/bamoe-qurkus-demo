package com.cvshealth.pbm.ssot.model;

public class StakeHolder {
    private String type; 
    private String name; 
    private String emailid; 
    private String additionalInformation; 
    private String identifier; 

    public StakeHolder() {}

    public StakeHolder(String type, String name, String emailid, String additionalInformation, String identifier) {
        this.type = type;
        this.name = name;
        this.emailid = emailid;
        this.additionalInformation = additionalInformation;
        this.identifier = identifier;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmailid() {
        return emailid;
    }

    public void setEmailid(String emailid) {
        this.emailid = emailid;
    }

    public String getAdditionalInformation() {
        return additionalInformation;
    }

    public void setAdditionalInformation(String additionalInformation) {
        this.additionalInformation = additionalInformation;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    @Override
    public String toString() {
        return "{\n" +
                "  \"type\": \"" + type + "\",\n" +
                "  \"name\": \"" + name + "\",\n" +
                "  \"emailid\": \"" + emailid + "\",\n" +
                "  \"additionalInformation\": \"" + additionalInformation + "\",\n" +
                "  \"identifier\": \"" + identifier + "\"\n" +
                "}";
    }
}
