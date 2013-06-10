/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sic.software.kpp;

import hudson.XmlFile;
import hudson.model.Hudson;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author michaelbar
 */
public class KPPManagement {
    private final static Logger LOGGER = Logger.getLogger(KPPManagement.class.getName());
    private final static String CLASS_NAME = KPPManagement.class.getName();
    
    private final static String CONFIG_XML = "sic.software.kpp.KPPManagement.xml";
    
    private static KPPManagement management;
    
    
    /**
     * Constructor
     */
    public KPPManagement() {
        
    }
    
    public static KPPManagement getInstance() {
        if (management == null) {
            management = init();
        }
        return management;
    }
    
    private static KPPManagement init() {
        LOGGER.entering(CLASS_NAME, "load");
        try {
            return (KPPManagement)getConfigXML().read();
        }
        catch(FileNotFoundException e) {
            LOGGER.log(Level.WARNING, "No configuration found.");
        }
        catch(Exception e) {
            LOGGER.log(Level.WARNING, "Unable to load configuration from " + CONFIG_XML, e);
        }
        
        return new KPPManagement();
    }
    
    private static XmlFile getConfigXML() {
        return new XmlFile(new File(Hudson.getInstance().getRootDir(), CONFIG_XML));
    }
    
    public static void save(KPPManagement config) throws IOException {
        LOGGER.entering(CLASS_NAME, "save");
        getConfigXML().write(config);
        LOGGER.exiting(CLASS_NAME, "save");
    }
}
