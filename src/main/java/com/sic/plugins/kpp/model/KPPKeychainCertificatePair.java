package com.sic.plugins.kpp.model;

import hudson.Extension;
import hudson.model.AbstractDescribableImpl;
import hudson.model.Descriptor;
import hudson.util.ListBoxModel;
import java.io.Serializable;
import java.util.List;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;

/**
 * Class represents a pair of a keychain and a selected certificate.
 */
public class KPPKeychainCertificatePair extends AbstractDescribableImpl<KPPKeychainCertificatePair> implements Serializable{
    
    private String keychain;
    private String codeSigningIdentity;
    
    @DataBoundConstructor
    public KPPKeychainCertificatePair(String keychain, String codeSigningIdentity) {
        this.keychain = keychain;
        
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
    
    /**
     * Get the keychain from a given string. The string has to start with the keychain filename.
     * @param keychainString
     * @return keychain
     */
    private static KPPKeychain getKeychainFromString(String keychainString) {
        KPPKeychain k = null;
        List<KPPKeychain> ks = KPPBaseKeychainsProvider.getInstance().getKeychains();
        if (ks.isEmpty() || keychainString == null || keychainString.length() == 0 ) {
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
        return String.format("%s (%s)", k.getFileName(), k.getDescription());
    }
    
    @Extension
    public static class DescriptorImpl extends Descriptor<KPPKeychainCertificatePair> {

        public ListBoxModel doFillKeychainItems(@QueryParameter String keychain) {
            ListBoxModel m = new ListBoxModel();
            List<KPPKeychain> ks = KPPBaseKeychainsProvider.getInstance().getKeychains();
            for (KPPKeychain k : ks) {
                m.add(KPPKeychainCertificatePair.getKeychainString(k));
            }
            return m;
        }
        
        public ListBoxModel doFillCodeSigningIdentityItems(@QueryParameter String keychain, @QueryParameter String codeSigningIdentity) {
            ListBoxModel m = new ListBoxModel();
            List<KPPKeychain> ks = KPPBaseKeychainsProvider.getInstance().getKeychains();
            if (ks.isEmpty()) {
                return m;
            }

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
