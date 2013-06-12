/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sic.jenkins.plugins.kpp;

import hudson.Plugin;
import java.util.logging.Logger;

/**
 *
 * @author michaelbar
 */
public class KPPPlugin extends Plugin {
    
    private final static Logger LOG = Logger.getLogger(KPPPlugin.class.getName());
    
    @Override
    public void start() throws Exception {
        LOG.info("starting KPP plugin");
    }
   
}
