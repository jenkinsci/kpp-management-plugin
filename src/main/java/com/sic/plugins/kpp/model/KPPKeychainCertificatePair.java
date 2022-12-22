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
import com.sic.plugins.kpp.provider.KPPKeychainsProvider;
import hudson.Extension;
import hudson.model.AbstractDescribableImpl;
import hudson.model.Descriptor;
import hudson.util.ListBoxModel;
import java.io.File;
import java.io.Serializable;
import java.util.List;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;

/**
 * Class represents a pair of a keychain and a selected certificate.
 * 
 * @author Michael Bär
 */
public class KPPKeychainCertificatePair extends AbstractDescribableImpl<KPPKeychainCertificatePair> implements Serializable{
    
    private final static String KEYCHAIN_BASE_VARIABLE_NAME = "KEYCHAIN_PATH";
    private final static String KEYCHAIN_PASSWORD_BASE_VARIABLE_NAME = "KEYCHAIN_PASSWORD";
    private final static String CODESIGNING_IDENTITY_BASE_VARIABLE_NAME = "CODE_SIGNING_IDENTITY";
    
    private String keychain;
    private String codeSigningIdentity;
    private String varPrefix;
    
    /**
     * Constructor
     * @param keychain keychain in the form filename and description
     * @param codeSigningIdentity general name of the code signing identity
     * @param varPrefix variable prefix to use
     */
    @DataBoundConstructor
    public KPPKeychainCertificatePair(String keychain, String codeSigningIdentity, String varPrefix) {
        this.keychain = keychain;
        this.varPrefix = varPrefix.trim();
        this.codeSigningIdentity = codeSigningIdentity;
    }
    
    /**
     * Get keychain.
     * @return keychain filename and description
     */
    public String getKeychain() {
        // Description of a keychain could be changed. So we have to assemble the string with keychain filename + description delivered via keychain provider.
        KPPKeychain k = getKeychainFromString(keychain);
        return getKeychainString(k);
    }
    
    /**
     * Get code signing identity.
     * @return general name of the code signing identity
     */
    public String getCodeSigningIdentity() {
        return codeSigningIdentity;
    }
    
    /**
     * Get variable prefix.
     * @return prefix
     */
    public String getVarPrefix() {
        return varPrefix;
    }
    
    /**
     * Get variable names which can be used in other build steps.
     * @return keychain, password and code signing identity variable name
     */
    public String getVariableNames() {
        String variables = String.format("${%s} ${%s} ${%s}", getKeychainVariableName(), getKeychainPasswordVariableName(), getCodeSigningIdentityVariableName());
        return variables;
    }
    
    private String getVariableName(String prefix, String base) {
        String name;
        if (prefix!=null && !prefix.isEmpty()) {
            name = String.format("%s_%s", prefix, base);
        } else {
            name = base;
        }
        return name;
    }
    
    /**
     * Get the variable name for the keychain variable that can be used in other build steps.
     * @return variable name in the form varprefix and variable name
     */
    public String getKeychainVariableName() {
        return getVariableName(varPrefix, KEYCHAIN_BASE_VARIABLE_NAME);
    }
    
    /**
     * Get the variable name for the password variable that can be used in other build steps.
     * @return variable name in the form varprefix and variable name
     */
    public String getKeychainPasswordVariableName() {
        return getVariableName(varPrefix, KEYCHAIN_PASSWORD_BASE_VARIABLE_NAME);
    }
    
    /**
     * Get the variable name for the code signing identity variable that can be used in other build steps.
     * @return variable name in the form varprefix and variable name
     */
    public String getCodeSigningIdentityVariableName() {
        return getVariableName(varPrefix, CODESIGNING_IDENTITY_BASE_VARIABLE_NAME);
    }
    
    /**
     * Get the filepath to the keychain stored inside the upload folder.
     * @return file path
     */
    public String getKeychainFilePath() {
        KPPKeychain k = getKeychainFromString(keychain);
        String filePath = null;
        if (k!=null && k.getFileName()!=null) {
            filePath = String.format("%s%s%s", KPPKeychainsProvider.getInstance().getUploadDirectoryPath(), File.separator, k.getFileName());
        }
        return filePath;
    }
    
    /**
     * Get the keychain filename from the keychain
     * @return filename
     */
    public String getKeychainFileName() {
        KPPKeychain k = getKeychainFromString(keychain);
        String fileName = null;
        if (k!=null && k.getFileName()!=null) {
            fileName = k.getFileName();
        }
        return fileName;
    }
    
    /**
     * Get the keychain from a given string. The string has to start with the keychain filename.
     * @return keychain
     */
    public static KPPKeychain getKeychainFromString(String keychainString) {
        KPPKeychain k = null;
        if (keychainString==null || keychainString.length()==0) {
            return k;
        }
        List<KPPKeychain> ks = KPPKeychainsProvider.getInstance().getKeychains();
        if (ks.isEmpty() || keychainString.length() == 0 ) {
            return k;
        }

        for (KPPKeychain kc : ks) {
            if (keychainString.startsWith(kc.getFileName())) {
                k = kc;
                break;
            }
        }

        return k;
    }
    
    /**
     * Concatenation keychain filename and keychain description in one string.
     * @return concatenated keychain string
     */
    private static String getKeychainString(KPPKeychain k) {
        if (k==null) {
            return "";
        }
        if (k.getDescription()==null || k.getDescription().length()==0) {
            return k.getFileName();
        }
        return String.format("%s (%s)", k.getFileName(), k.getDescription());
    }
    
    /**
     * Descriptor of the {@link KPPKeychainCertificatePair}.
     */
    @Extension
    public static class DescriptorImpl extends Descriptor<KPPKeychainCertificatePair> {
        
        /**
         * Action method to fill out the keychain items.
         * @param keychain query parameter of the keychain
         * @return list of filename items
         */
        public ListBoxModel doFillKeychainItems(@QueryParameter String keychain) {
            ListBoxModel m = new ListBoxModel();
            List<KPPKeychain> ks = KPPKeychainsProvider.getInstance().getKeychains();
            for (KPPKeychain k : ks) {
                m.add(KPPKeychainCertificatePair.getKeychainString(k));
            }
            return m;
        }
        
        /**
         * Action method to fill out the code signing identities.
         * @param keychain query parameter of the keychain
         * @param codeSigningIdentity query parameter of the code signing identity
         * @return list of filename items
         */
        public ListBoxModel doFillCodeSigningIdentityItems(@QueryParameter String keychain, @QueryParameter String codeSigningIdentity) {
            ListBoxModel m = new ListBoxModel();
            KPPKeychain k = KPPKeychainCertificatePair.getKeychainFromString(keychain);
            if (k != null) {
                for (KPPCertificate c : k.getCertificates()) {
                    m.add(c.getCodeSigningIdentityName());
                }
            }
            return m;
        }
        
        @Override
        public String getDisplayName() {
            return Messages.KPPKeychainCertificatePair_DisplayName();
        }
    }
}
