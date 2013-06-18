package com.sic.plugins.kpp.model;

import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Represents an certificate inside a keychain.
 */
public class KPPCertificate {
    
    private static final Logger LOGGER = Logger.getLogger(KPPCertificate.class.getName());
    
    private final String alias;
    private final String validFrom;
    private final String validTo;
    
    public KPPCertificate(String alias, KeyStore keyStore) {
        this.alias = alias;
        
        Certificate cert = null;
        try {
            cert = keyStore.getCertificate(alias);
        } catch (KeyStoreException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
        
        if (cert instanceof X509Certificate) {
            X509Certificate x509 = (X509Certificate) cert;
            this.validFrom = x509.getNotBefore().toString();
            this.validTo = x509.getNotAfter().toString();
        } else {
            this.validFrom = "";
            this.validTo = "";
        }
    }
    
    /**
     * Get the alias of the certificate
     * @return alias
     */
    public String getAlias() {
        return alias;
    }
    
    /**
     * Get from data of the valid period
     * @return from
     */
    public String getValidFrom() {
        return validFrom;
    }

    /**
     * Get the to date of the valid period
     * @return to
     */
    public String getValidTo() {
        return validTo;
    }
    
}
