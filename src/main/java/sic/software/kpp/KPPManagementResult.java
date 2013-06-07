/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sic.software.kpp;

import hudson.model.Hudson;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.cert.CertificateException;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import org.apache.commons.fileupload.FileItem;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

/**
 *
 * @author michaelbar
 */
public class KPPManagementResult {

    Logger logger = Logger.getLogger(KPPManagementResult.class.getName());
    private File filePath;

    /**
     * Constructor
     */
    public KPPManagementResult() {
        this.showForKeychains();
    }

    /**
     * This method handles file uploading, it saves the uploaded Keychain in the
     * kpp_upload directory in Jenkins root dir
     *
     * @param req
     * @param rsp
     * @throws ServletException
     * @throws IOException
     */
    public void doSaveConfig(StaplerRequest req, StaplerResponse rsp) throws
            ServletException,
            IOException {

        FileItem file = req.getFileItem("myFile.file");

        if (file == null) {
            throw new ServletException("no file uploaded");
        }

        byte[] data = file.get();
        logger.info("file size: " + file.getSize());

        File pathToDirectory = new File(Hudson.getInstance().getRootDir() + "/kpp_upload");
        pathToDirectory.mkdir();
        File toUploadFile = new File(pathToDirectory, file.getName());
        OutputStream os = new FileOutputStream(toUploadFile);
        try {
            os.write(data);
        } finally {
            os.close();
        }

        rsp.sendRedirect2("../manageZpp/");
    }

    /**
     * Checks if there are any keychains uploaded before to show them in a tabel
     */
    private void showForKeychains() {

        String[] fileList = null;
        this.filePath = new File(Hudson.getInstance().getRootDir() + "/kpp_upload");

        if (this.filePath.exists()) {
            fileList = this.filePath.list();
            if (fileList.length > 0) {

                KeyStore ks = null;
                try {
                    ks = loadKeystoreFromFile(this.filePath + "/" + fileList[0], "dh63+#vd");
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

            } else {
                logger.log(Level.INFO, "No files uploaded yet!");
            }
        } else {
            logger.log(Level.INFO, "Path does not exist");
        }

    }

    /**
     * This method loads the uplaoded keystore from a file
     *
     * @return loaded Keystore
     */
    private KeyStore loadKeystoreFromFile(String keystoreFilePath, String keyStorePW) throws
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
                new FileInputStream(keystoreFilePath);
        ks.load(fis, password);
        fis.close();

        /* Print all keys from HashTable
         * 
         Enumeration<String> aliases = ks.aliases();
         while(aliases.hasMoreElements()){
         System.out.println(aliases.nextElement());
         }
         */

        return ks;
    }
}
