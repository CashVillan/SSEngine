package com.swingsword.ssengine.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import com.swingsword.ssengine.MasterPlugin;

public class ResourceUtils {

	static public String ExportResource(String resourceName, String targetFolder) throws Exception {
        InputStream stream = null;
        OutputStream resStreamOut = null;
        String jarFolder;
        
        try {
            stream = MasterPlugin.class.getResourceAsStream('/' + resourceName);
            
            if(stream == null) {
                throw new Exception("Cannot get resource \"" + resourceName + "\" from Jar file.");
            }

            int readBytes;
            byte[] buffer = new byte[4096];
            jarFolder = new File("plugins").getPath().replace('\\', '/') + '/' + targetFolder;
                        
            if(targetFolder == null) {
            	resStreamOut = new FileOutputStream(jarFolder + resourceName);
            } else {
            	resStreamOut = new FileOutputStream(jarFolder + resourceName);
            }
            while ((readBytes = stream.read(buffer)) > 0) {
                resStreamOut.write(buffer, 0, readBytes);
            }
        } catch (Exception ex) {
            throw ex;
        } finally {
        	if(stream != null && resStreamOut != null) {
        		stream.close();
        		resStreamOut.close();
        	}
        }

        return jarFolder + resourceName;
    }
	
}
