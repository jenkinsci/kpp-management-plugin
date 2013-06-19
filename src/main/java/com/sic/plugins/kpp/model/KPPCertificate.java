package com.sic.plugins.kpp.model;

import java.io.Serializable;
import org.kohsuke.stapler.DataBoundConstructor;

/**
 * Represents an certificate inside a keychain.
 */
public class KPPCertificate implements Serializable{
    
    private String codeSigningIdentityName;
    
    /**
     * Constructor
     * @param codeSigningIdentityName 
     */
    @DataBoundConstructor
    public KPPCertificate(String codeSigningIdentityName) {
        this.codeSigningIdentityName = codeSigningIdentityName;
    }
    
    /**
     * Get the code signing identity name.
     * @return codeSigningIdentityName 
     */
    public String getCodeSigningIdentityName() {
        return codeSigningIdentityName;
    }
    
    /**
     * Set the code singing identity name.
     * @param codeSigningIdentityName 
     */
    public void setCodeSigningIdentityName(String codeSigningIdentityName) {
        this.codeSigningIdentityName = codeSigningIdentityName;
    }
    
}
