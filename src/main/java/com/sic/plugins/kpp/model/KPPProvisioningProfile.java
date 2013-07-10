package com.sic.plugins.kpp.model;

import com.dd.plist.NSDictionary;
import com.dd.plist.PropertyListParser;
import com.sic.plugins.kpp.provider.KPPProvisioningProfilesProvider;
import hudson.Extension;
import hudson.model.Describable;
import hudson.model.Descriptor;
import hudson.model.Hudson;
import java.io.File;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.kohsuke.stapler.DataBoundConstructor;

/**
 * Represents a provisioning profile.
 */
public class KPPProvisioningProfile implements Describable<KPPProvisioningProfile>, Serializable {
    
    private final String fileName;
    private transient final String uuid;
    
    @DataBoundConstructor
    public KPPProvisioningProfile(String fileName) {
        this.fileName = fileName;
        //this.uuid = getUUIDFromFileName(fileName);
        this.uuid = "uuid";
    }
    
    /**
     * Get the filename of the provisioning profile.
     * @return filename
     */
    public String getFileName() {
        return fileName;
    }
    
    /**
     * Get the uuid of the provisioning profile.
     * @return uuid
     */
    public String getUuid() {
        //return getUUIDFromFileName(fileName);
        return uuid;
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
    
    private static String getUUIDFromFileName(String fileName) {
        
        try {
            File file = KPPProvisioningProfilesProvider.getInstance().getProvisioningFile(fileName);
            NSDictionary rootDict = (NSDictionary)PropertyListParser.parse(file);
        } catch (Exception ex) {
            Logger.getLogger(KPPProvisioningProfile.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    @Extension
    public static final class DescriptorImpl extends Descriptor<KPPProvisioningProfile> {

        @Override
        public String getDisplayName() {
            return "Provisioning Profile";
        }
    }
    
    
}
