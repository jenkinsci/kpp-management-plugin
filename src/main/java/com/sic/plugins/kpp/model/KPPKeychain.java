/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
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
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.kohsuke.stapler.DataBoundConstructor;

/**
 * Represents an keychain.
 * @author michaelbar
 */
public final class KPPKeychain implements Describable<KPPKeychain>, Serializable {
    
    private final static Logger LOGGER = Logger.getLogger(KPPKeychain.class.getName());
    
    private final String fileName;
    private String description;
    private Secret password;
    
    public KPPKeychain(String fileName) {
        this.fileName = fileName;
    }
    
    @DataBoundConstructor
    public KPPKeychain(String fileName, String password, String description) {
        this.fileName = fileName;
        setPassword(password);
        setDescription(description);
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
        List<KPPCertificate> certificates = new ArrayList<KPPCertificate>();
        try {
            final String filePath = KPPBaseKeychainsProvider.getInstance().getKeychainsUploadDirectoryPath() + File.separator + this.getFileName();
            KeyStore ks = KPPKeychain.loadKeystoreFromFile(filePath, this.getPassword());
            if (ks != null) {
                Enumeration<String> aliases = ks.aliases();
                String alias = null;
                while (aliases.hasMoreElements()) {
                    alias = aliases.nextElement();
                    KPPCertificate cert = new KPPCertificate(alias, ks);
                    certificates.add(cert);
                }

            }
        } catch (KeyStoreException ex) {
            Logger.getLogger(KPPKeychain.class.getName()).log(Level.SEVERE, null, ex);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(KPPKeychain.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(KPPKeychain.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(KPPKeychain.class.getName()).log(Level.SEVERE, null, ex);
        } catch (CertificateException ex) {
            Logger.getLogger(KPPKeychain.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchProviderException ex) {
            Logger.getLogger(KPPKeychain.class.getName()).log(Level.SEVERE, null, ex);
        }
        return certificates;
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
 
        KeyStore ks = KeyStore.getInstance("KeychainStore", "Apple");
 
        // get user password and file input stream
        char[] password = keyStorePW.toCharArray();
        FileInputStream fis =
                new FileInputStream(keyStoreFilePath);
        ks.load(fis, password);
        fis.close();
 
        return ks;
    }
    
}
