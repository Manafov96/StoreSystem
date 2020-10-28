/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Tools;

// To work this functionality must add jar file in library "C:\Users\Вико\Downloads\apache-commons-net.jar"

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.ini4j.Wini;
import java.util.zip.*;
import javax.swing.JOptionPane;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
/**
 *
 * @author Viko
 */
public class ArchiveDataBase {
    
    public void archiveDb () throws FileNotFoundException, IOException{ 
        
        String file = null;
        String server = null;
        String user = null;
        String pass = null;
        try {
            Wini ini = new Wini(new File("dbPath.ini"));
            file = ini.get("database", "file");
            server = ini.get("archive", "server");
            user = ini.get("archive", "user");
            pass = ini.get("archive", "pass");
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }       
        
        //String server = "tunparts.de";
        int port = 21;
        //String user = "viko@tunparts.de";
        //String pass = "selvi2020";
        
        FTPClient ftpClient = new FTPClient();      
        
        ftpClient.connect(server, port);
        ftpClient.login(user, pass);
        ftpClient.enterLocalPassiveMode();
        
        ftpClient.setFileType(FTP.BINARY_FILE_TYPE);                 
          
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy.MM.dd");  
        LocalDateTime now = LocalDateTime.now(); 
        String currentDate = dtf.format(now);
                 
        String dir = System.getProperty("user.dir")+"\\";
        String srcFilename = file;
        
        //String zipFile = dir + "STORE_" + currentDate + ".zip";
        
        String secondRemoteFile = "STORE_" + currentDate + ".zip";
               
        try {
            byte[] buffer = new byte[1024];
            //To zip file local uncoment this
            //FileOutputStream fos = new FileOutputStream(zipFile);
            OutputStream fos = ftpClient.storeFileStream(secondRemoteFile);
            try (ZipOutputStream zos = new ZipOutputStream(fos)) {
                File srcFile = new File(srcFilename);
                try (FileInputStream fis = new FileInputStream(srcFile)) {
                    zos.putNextEntry(new ZipEntry(srcFile.getName()));
                    int length;
                    while ((length = fis.read(buffer)) > 0) {
                        zos.write(buffer, 0, length);
                    }
                    zos.closeEntry();
                }
            }
        }
        catch (IOException ioe) {
            System.out.println("Error creating zip file" + ioe);
            JOptionPane.showMessageDialog(null, "Грешка при създаването на файла!");
        }
        
        if (ftpClient.isConnected()) {
                ftpClient.logout();
                ftpClient.disconnect();
                    }
            }   
}
