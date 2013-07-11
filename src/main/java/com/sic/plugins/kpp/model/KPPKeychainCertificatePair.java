package com.sic.plugins.kpp.model;

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
 */
public class KPPKeychainCertificatePair extends AbstractDescribableImpl<KPPKeychainCertificatePair> implements Serializable{
    
    private final static String KEYCHAIN_BASE_VARIABLE_NAME = "KEYCHAIN";
    private final static String KEYCHAIN_PASSWORD_BASE_VARIABLE_NAME = "KEYCHAIN_PASSWORD";
    private final static String CODESIGNING_IDENTITY_BASE_VARIABLE_NAME = "CODESIGNING_IDENTITY";
    
    private String keychain;
    private String codeSigningIdentity;
    private String varPrefix;
    
    @DataBoundConstructor
    public KPPKeychainCertificatePair(String keychain, String codeSigningIdentity, String varPrefix) {
        this.keychain = keychain;
        this.varPrefix = varPrefix.trim();
        this.codeSigningIdentity = codeSigningIdentity;
    }
    
    public String getKeychain() {
        // Description of a keychain could be changed. So we have to assemble the string with keychain filename + description delivered via keychain provider.
        KPPKeychain k = getKeychainFromString(keychain);
        return getKeychainString(k);
    }
    
    public String getCodeSigningIdentity() {
        return codeSigningIdentity;
    }
    
    public String getVarPrefix() {
        return varPrefix;
    }
    
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
    
    public String getKeychainVariableName() {
        return getVariableName(varPrefix, KEYCHAIN_BASE_VARIABLE_NAME);
    }
    
    public String getKeychainPasswordVariableName() {
        return getVariableName(varPrefix, KEYCHAIN_PASSWORD_BASE_VARIABLE_NAME);
    }
    
    public String getCodeSigningIdentityVariableName() {
        return getVariableName(varPrefix, CODESIGNING_IDENTITY_BASE_VARIABLE_NAME);
    }
    
    public String getKeychainFilePath() {
        KPPKeychain k = getKeychainFromString(keychain);
        String filePath = null;
        if (k!=null && k.getFileName()!=null) {
            filePath = String.format("%s%s%s", KPPKeychainsProvider.getInstance().getUploadDirectoryPath(), File.separator, k.getFileName());
        }
        return filePath;
    }
    
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
     * @param keychainString
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
     * @param k
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
    
    @Extension
    public static class DescriptorImpl extends Descriptor<KPPKeychainCertificatePair> {

        public ListBoxModel doFillKeychainItems(@QueryParameter String keychain) {
            ListBoxModel m = new ListBoxModel();
            List<KPPKeychain> ks = KPPKeychainsProvider.getInstance().getKeychains();
            for (KPPKeychain k : ks) {
                m.add(KPPKeychainCertificatePair.getKeychainString(k));
            }
            return m;
        }
        
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
            return "";
        }
    }
}
