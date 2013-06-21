
package com.sic.plugins.kpp.model;

import hudson.Extension;
import hudson.model.Describable;
import hudson.model.Descriptor;
import hudson.model.Hudson;
import hudson.util.Secret;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.cert.CertificateException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.kohsuke.stapler.DataBoundConstructor;

/**
 * Represents a keychain.
 * @author michaelbar
 */
public final class KPPKeychain implements Describable<KPPKeychain>, Serializable {
    
    private final static Logger LOGGER = Logger.getLogger(KPPKeychain.class.getName());
    private final static String VARIABLE_NAME_KEYCHAIN = "KEYCHAIN";
    private final static String VARIABLE_NAME_CODE_SIGNING_IDENTITY ="CODE_SIGNING_IDENTITY";
    private final static String VARIABLE_NAME_KEYCHAIN_PASSWORD = "KEYCHAIN_PASSWORD";
    
    private final String fileName;
    private String description;
    private Secret password;
    private String varPrefix;
    private List<KPPCertificate> certificates;
    
    public KPPKeychain(String fileName) {
        this.fileName = fileName;
    }
    
    @DataBoundConstructor
    public KPPKeychain(String fileName, String password, String description, String varPrefix, List<KPPCertificate> certificates) {
        this.fileName = fileName;
        setPassword(password);
        setDescription(description);
        setVarPrefix(varPrefix);
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
    
    public final void setVarPrefix(String varPrefix) {
        this.varPrefix = varPrefix;
    }
    
    public final String getVarPrefix() {
        return varPrefix;
    }
    
    public final List<KPPCertificate> getCertificates() {
        return certificates;
    }
    
    public final void setCertificates(List<KPPCertificate>certificates){
        this.certificates = certificates;
    }
    
    public String getKeychainVariableName() {
        return getPrefixedVariableName(VARIABLE_NAME_KEYCHAIN);
    }
    
    public String getKeychainPasswordVariableName() {
        return getPrefixedVariableName(VARIABLE_NAME_KEYCHAIN_PASSWORD);
    }
    
    public String getCodeSigningIdentityVariableName() {
        return getPrefixedVariableName(VARIABLE_NAME_CODE_SIGNING_IDENTITY);
    }
    
    private String getPrefixedVariableName(String variableName) {
        String name = variableName;
        if (varPrefix==null || varPrefix.length()==0) {
            return String.format("%s", name);
        }
        name = String.format("%s_%s", varPrefix, name);
        return name;
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
    
    /**
     * This method loads the uplaoded keystore from a file.
     *
     * @return loaded Keystore
     */
    private static KeyStore loadKeystoreFromFile(String keyStoreFilePath, String keyStorePW) throws
            KeyStoreException,
            FileNotFoundException,
            IOException,
            NoSuchAlgorithmException,
            CertificateException,
            NoSuchProviderException {
 
        //KeyStore ks = KeyStore.getInstance("KeychainStore", "Apple");
        //KeyStore ks = KeyStore.getInstance("KeychainStore");
        KeyStore ks = KeyStore.getInstance("KeychainStore");

        //keyStoreFilePath = "/Users/michaelbar/Documents/Projekte/SIC/Jenkins_ZPP_Plugin/source/kpp/work/kpp_upload/empty.keychain";
        
        keyStorePW = "123456";
        char[] password = keyStorePW.toCharArray();
        File keychainFile = new File(keyStoreFilePath);
        LOGGER.log(Level.INFO, "content: {0}", keychainFile.toString());
        if (!keychainFile.isFile()) {
            if (keychainFile.isDirectory()) {
                String[] files = keychainFile.list();
                for (String filename : files) {
                    LOGGER.log(Level.INFO, filename);
                }
            }
        }

        FileInputStream fis = new FileInputStream(keychainFile);
        ks.load(fis, password);
        fis.close();
        
        LOGGER.log(Level.INFO, "Provider1: {0}", ks.getProvider());
        LOGGER.log(Level.INFO, "Provider2: {0}", ks.getProvider().toString());
 
        return ks;
    }
    
}