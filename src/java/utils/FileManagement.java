package utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class FileManagement {
    
    public void createZip(String zip_path, File... files) throws IOException{
        try(ZipOutputStream zipOutputStream = new ZipOutputStream(
            new FileOutputStream(
                new File(zip_path)), StandardCharsets.UTF_8
            )
        ){
            for(File file : files)
                writeToZip(file, zipOutputStream);
        }
    }
    
    public void deleteFiles(File... files){
        for(File file : files)
            file.delete();
    }
    
    public File createFile(String filePath, boolean append, byte[] data) throws FileNotFoundException, IOException {
        File file = new File(filePath);
        try(FileOutputStream fos = new FileOutputStream(file, append)){
            fos.write(data);
        }
        return file;
        
    }
    
    private void writeToZip(File file, ZipOutputStream zipOutputStream) throws FileNotFoundException, IOException{
        FileInputStream fileInputStream = new FileInputStream(file);
        ZipEntry zipEntry = new ZipEntry(file.getName());
        zipOutputStream.putNextEntry(zipEntry);
        
        byte[] buffer = new byte[ProjectConstants.BUFFER_SIZE];
        int bytesRead;
        while ((bytesRead = fileInputStream.read(buffer)) != -1)
            zipOutputStream.write(buffer, 0, bytesRead);
        zipOutputStream.closeEntry();
        fileInputStream.close();
    }
}
