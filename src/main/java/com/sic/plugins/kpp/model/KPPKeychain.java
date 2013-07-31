/*
 * The MIT License
 *
 * Copyright 2013 Michael Bär SIC! Software GmbH.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.sic.plugins.kpp.model;

import com.sic.plugins.kpp.Messages;
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
 * 
 * @author Michael Bär
 */
public final class KPPKeychain implements Describable<KPPKeychain>, Serializable {
    
    private final String fileName;
    private String description;
    private Secret password;
    private List<KPPCertificate> certificates;
    
    /**
     * Constructor
     * 
     * @param fileName keychain filename 
     */
    public KPPKeychain(String fileName) {
        this.fileName = fileName;
    }
    
    /**
     * Constructor
     * 
     * @param fileName keychain filename
     * @param password password to unlock keychain
     * @param description additional description
     * @param certificates list of certificates
     */
    @DataBoundConstructor
    public KPPKeychain(String fileName, String password, String description, List<KPPCertificate> certificates) {
        this.fileName = fileName;
        setPassword(password);
        setDescription(description);
        setCertificates(certificates);
    }
    
    /**
     * Get keychain filename.
     * @return filename
     */
    public final String getFileName() {
        return fileName;
    }
    
    /**
     * Set keychain password.
     * @param password password as plain text
     */
    public final void setPassword(String password) {
        this.password = Secret.fromString(password);
    }
    
    /**
     * Get password to unlock keychain.
     * @return password as plain text
     */
    public final String getPassword() {
        return Secret.toString(password);
    }
    
    /**
     * Get password as secret ot unlock keychain.
     * @return password as secret
     */
    public final Secret getPasswordAsSecret() {
        return password;
    }
    
    /**
     * Get description.
     * @return description
     */
    public final String getDescription() {
        getCertificates();
        return description;
    }
    
    /**
     * Set description.
     * @param description 
     */
    public final void setDescription(String description) {
        this.description = description;
    }
    
    /**
     * Get a list of {@link KPPCertificate} objects.
     * @return list of certificates.
     */
    public final List<KPPCertificate> getCertificates() {
        return certificates;
    }
    
    /**
     * Set a list of {@link KPPCertificate} objects.
     * @param certificates list of certificates
     */
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

    /**
     * Get the descriptor.
     * @return descriptor
     */
    public Descriptor getDescriptor() {
        Descriptor ds = Hudson.getInstance().getDescriptorOrDie(getClass());
        return ds;
    }
    
    /**
     * Descriptor of the {@link KPPKeychain}.
     */
    @Extension
    public static final class DescriptorImpl extends Descriptor<KPPKeychain> {

        @Override
        public String getDisplayName() {
            return Messages.KPPKeychain_DisplayName();
        }
    }
}