package com.sic.plugins.kpp.model;

import com.sic.plugins.kpp.provider.KPPProvisioningProfilesProvider;
import hudson.Extension;
import hudson.model.Describable;
import hudson.model.Descriptor;
import hudson.model.Hudson;
import hudson.util.ListBoxModel;
import java.io.File;
import java.io.Serializable;
import java.util.List;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;

/**
 * Represents a provisioning profile.
 */
public class KPPProvisioningProfile implements Describable<KPPProvisioningProfile>, Serializable {
    
    private final static String PROVISIONING_PROFILE_BASE_VARIABLE_NAME = "PROVISIONING_PROFILE";
    
    private final String fileName;
    private final String varPrefix; // variable prefix for build step integration
    private transient String uuid;
    
    @DataBoundConstructor
    public KPPProvisioningProfile(String fileName, String varPrefix) {
        this.fileName = fileName;
        this.varPrefix = varPrefix;
    }
    
    /**
     * Get the filename of the provisioning profile.
     * @return filename
     */
    public String getFileName() {
        return fileName;
    }
    
    /**
     * Get the filepath to the provisioning profile stored on the master or standalone jenkins instance.
     * @return filepath
     */
    public String getProvisioningProfileFilePath() {
        String file = KPPProvisioningProfilesProvider.removeUUIDFromFileName(fileName);
        return String.format("%s%s%s", KPPProvisioningProfilesProvider.getInstance().getUploadDirectoryPath(), File.separator, file);
    }
    
    /**
     * Get the variable prefix for build step integration.
     * @return variable prefix
     */
    public String getVarPrefix() {
        return varPrefix;
    }
    
    /**
     * Get the variable name for the provisioning profile.
     * @return variable name.
     */
    public String getProvisioningProfileVariableName() {
        String name;
        String prefix = getVarPrefix();
        if (prefix!=null && !prefix.isEmpty()) {
            name = String.format("%s_%s", prefix, PROVISIONING_PROFILE_BASE_VARIABLE_NAME);
        } else {
            name = PROVISIONING_PROFILE_BASE_VARIABLE_NAME;
        }
        return name;
    }
    
    /**
     * Get the variable name included in ${}.
     * @return variable name
     */
    public String getVariableName() {
        return String.format("${%s}", getProvisioningProfileVariableName());
    }
    
    /**
     * Get the uuid of the provisioning profile.
     * @return uuid
     */
    public String getUuid() {
        if (uuid==null || uuid.isEmpty()) {
            uuid = KPPProvisioningProfilesProvider.parseUUIDFromProvisioningProfileFile(fileName);
        }
        return uuid;
    }
    
    /**
     * Get the filename and uuid in one string.
     * @return filename and uuid
     */
    public String getFileNameUuidDescription() {
        return String.format("%s (%s)", getFileName(), getUuid());
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final KPPProvisioningProfile other = (KPPProvisioningProfile) obj;
        if ((this.fileName == null) ? (other.fileName != null) : !this.fileName.equals(other.fileName)) {
            return false;
        }
        return true;
    }
    
    @Override
    public int hashCode() {
        int hash = 3;
        hash = 67 * hash + (this.fileName != null ? this.fileName.hashCode() : 0);
        return hash;
    }
    
    public Descriptor<KPPProvisioningProfile> getDescriptor() {
        Descriptor ds = Hudson.getInstance().getDescriptorOrDie(getClass());
        return ds;
    }
    
    @Extension
    public static class DescriptorImpl extends Descriptor<KPPProvisioningProfile> {

        @Override
        public String getDisplayName() {
            return "Provisioning Profile";
        }
        
        public ListBoxModel doFillFileNameItems(@QueryParameter String fileName) {
            ListBoxModel m = new ListBoxModel();
            List<KPPProvisioningProfile> pps = KPPProvisioningProfilesProvider.getInstance().getProvisioningProfiles();
            for (KPPProvisioningProfile pp : pps) {
                m.add(pp.getFileNameUuidDescription());
            }
            return m;
        }
    }
    
    
}
