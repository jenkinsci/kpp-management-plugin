/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sic.software.kpp.model;

import hudson.util.Secret;
import org.kohsuke.stapler.DataBoundConstructor;

/**
 * Represents an keychain.
 * @author michaelbar
 */
public class KPPKeychain {
    
    private final String fileName;
    private Secret password;
    private String description;
    
    @DataBoundConstructor
    public KPPKeychain(String fileName) {
        this.fileName = fileName;
    }
    
    public String getFileName() {
        return fileName;
    }
    
    public void setPassword(String password) {
        this.password = Secret.fromString(password);
    }
    
    public String getPassword() {
        return Secret.toString(password);
    }
    
    public Secret getPasswordAsSecret() {
        return password;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
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

    
}
