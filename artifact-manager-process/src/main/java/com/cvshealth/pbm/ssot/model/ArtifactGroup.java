package com.cvshealth.pbm.ssot.model;

public class ArtifactGroup {
    private String identifier; 
    private String name; 
    private String description; 
    private String businessNeed; 
    private String generalImpact; 
    private String variancesToImpact; 
    private String otherDependencies; 
    private String implementationInformation; 
    private String approvalLevel; 
    

    public ArtifactGroup() {}

    public ArtifactGroup(String identifier, String name, String description, String businessNeed, String generalImpact, String variancesToImpact, String otherDependencies, String implementationInformation, String approvalLevel) {
        this.identifier = identifier;
        this.name = name;
        this.description = description;
        this.businessNeed = businessNeed;
        this.generalImpact = generalImpact;
        this.variancesToImpact = variancesToImpact;
        this.otherDependencies = otherDependencies;
        this.implementationInformation = implementationInformation;
        this.approvalLevel = approvalLevel;
    }

    public String getIdentifier() { return identifier; }
    public void setIdentifier(String identifier) { this.identifier = identifier; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getBusinessNeed() { return businessNeed; }
    public void setBusinessNeed(String businessNeed) { this.businessNeed = businessNeed; }
    public String getGeneralImpact() { return generalImpact; }
    public void setGeneralImpact(String generalImpact) { this.generalImpact = generalImpact; }
    public String getVariancesToImpact() { return variancesToImpact; }
    public void setVariancesToImpact(String variancesToImpact) { this.variancesToImpact = variancesToImpact; }
    public String getOtherDependencies() { return otherDependencies; }
    public void setOtherDependencies(String otherDependencies) { this.otherDependencies = otherDependencies; }
    public String getImplementationInformation() { return implementationInformation; }
    public void setImplementationInformation(String implementationInformation) { this.implementationInformation = implementationInformation; }
    public String getApprovalLevel() { return approvalLevel; }
    public void setApprovalLevel(String approvalLevel) { this.approvalLevel = approvalLevel; }
    
    public String toString() {
        return "{\n" +
                "  \"identifier\": \"" + identifier + "\",\n" +
                "  \"name\": \"" + name + "\",\n" +
                "  \"description\": \"" + description + "\",\n" +
                "  \"businessNeed\": \"" + businessNeed + "\",\n" +
                "  \"generalImpact\": \"" + generalImpact + "\",\n" +
                "  \"variancesToImpact\": \"" + variancesToImpact + "\",\n" +
                "  \"otherDependencies\": \"" + otherDependencies + "\",\n" +
                "  \"implementationInformation\": \"" + implementationInformation + "\",\n" +
                "  \"approvalLevel\": \"" + approvalLevel + "\",\n" +
                "}";
    }
}
