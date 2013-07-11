
package com.sic.plugins.kpp.model;

import hudson.Extension;
import hudson.model.Describable;
import hudson.model.Descriptor;
import hudson.model.Hudson;
import hudson.util.Secret;
import java.io.Serializable;
import java.util.List;
import org.kohsuke.stapler.DataBoundConstructor;

/**
 * Represents a keychain.
 * @author michaelbar
 */
public final class KPPKeychain implements Describable<KPPKeychain>, Serializable {
    
    private final String fileName;
    private String description;
    private Secret password;
    private List<KPPCertificate> certificates;
    
    public KPPKeychain(String fileName) {
        this.fileName = fileName;
    }
    
    @DataBoundConstructor
    public KPPKeychain(String fileName, String password, String description, List<KPPCertificate> certificates) {
        this.fileName = fileName;
        setPassword(password);
        setDescription(description);
        setCertificates(certificates);
    }
    
    public final String getFileName() {
        return fileName;
    }
    
    public final void setPassword(String password) {
        this.password = Secret.fromString(password);
    }
    
    public final String getPassword() {
        return Secret.toString(password);
    }
    
    public final Secret getPasswordAsSecret() {
        return password;
    }
    
    public final String getDescription() {
        getCertificates();
        return description;
    }
    
    public final void setDescription(String description) {
        this.description = description;
    }
    
    public final List<KPPCertificate> getCertificates() {
        return certificates;
    }
    
    public final void setCertificates(List<KPPCertificate>certificates){
        this.certificates = certificates;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final KPPKeychain other = (KPPKeychain) obj;
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

    public Descriptor getDescriptor() {
        Descriptor ds = Hudson.getInstance().getDescriptorOrDie(getClass());
        return ds;
    }
    
    @Extension
    public static final class DescriptorImpl extends Descriptor<KPPKeychain> {

        @Override
        public String getDisplayName() {
            return "Keychain";
        }
    }
}