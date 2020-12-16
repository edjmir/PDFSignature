package servlets;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import javax.crypto.EncryptedPrivateKeyInfo;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import key_handler.KeyPairGen;
import utils.ProjectConstants;

public class DownloadPrivateKey extends HttpServlet {
    
    private final short BUFFER_SIZE = 4096;
    
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");
        
        String passphrase = request.getParameter("passphrase");
        
        final String pk_path = ProjectConstants.PRIVATE_KEY_FILE_DEFAULT_PATH
            .concat(String.valueOf(System.currentTimeMillis()))
            .concat("-")
            .concat(ProjectConstants.PRIVATE_KEY_FILE_DEFAULT_NAME);
        
        final String pb_path = ProjectConstants.PUBLIC_KEY_FILE_DEFAULT_PATH
            .concat(String.valueOf(System.currentTimeMillis()))
            .concat("-")
            .concat(ProjectConstants.PUBLIC_KEY_FILE_DEFAULT_NAME);
        
        KeyPairGen keyPairGen = new KeyPairGen();
        File pk_file = new File(pk_path);
        File pb_file = new File(pb_path);
        try {
            if("null".equals(String.valueOf(passphrase)))
                keyPairGen.createAndSaveKeys(pk_file, pb_file);
            else
                keyPairGen.createAndSaveKeys(pk_file, pb_file, passphrase);
        } catch (Exception ex) {
            response.setStatus(500);
            return;
        }
        
        final String zip_path = ProjectConstants.ZIP_KEYS_FILE_DEFAULT_PATH
            .concat(String.valueOf(System.currentTimeMillis()))
            .concat(ProjectConstants.ZIP_KEYS_FILE_DEFAULT_NAME);
        
        try(ZipOutputStream zout = new ZipOutputStream(
            new FileOutputStream(
                new File(zip_path)), StandardCharsets.UTF_8
            )
        ){
            writeToZip(pk_file, zout);
            writeToZip(pb_file, zout);
        }
        
        pk_file.delete();
        pb_file.delete();
        
        request.setAttribute("file_path", zip_path);
        request.setAttribute("file_name", ProjectConstants.ZIP_KEYS_FILE_DEFAULT_NAME);
        request.getRequestDispatcher("DownloadFile").forward(request, response);
               
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

    private void writeToZip(File file, ZipOutputStream zos) throws FileNotFoundException, IOException{
        FileInputStream fis = new FileInputStream(file);
        ZipEntry zipEntry = new ZipEntry(file.getName());
        zos.putNextEntry(zipEntry);
        
        byte[] buffer = new byte[BUFFER_SIZE];
        int bytesRead;
        while ((bytesRead = fis.read(buffer)) != -1)
            zos.write(buffer, 0, bytesRead);
        zos.closeEntry();
        fis.close();
    }
}
