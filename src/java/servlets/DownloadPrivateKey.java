package servlets;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import javax.servlet.ServletContext;
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
        final String file_name = ProjectConstants.PRIVATE_KEY_FILE_DEFAULT_PATH
            .concat(String.valueOf(System.currentTimeMillis()))
            .concat(ProjectConstants.PRIVATE_KEY_FILE_DEFAULT_NAME);
        
        KeyPairGen keyPairGen = new KeyPairGen();
        
        try {
            if(passphrase == null)
                keyPairGen.createAndSaveKeys(new File(file_name));
            else
                keyPairGen.createAndSaveKeys(new File(file_name), passphrase);
        } catch (Exception ex) {
            response.setStatus(500);
            return;
        }
        
        File file_to_download = new File(file_name);
        FileInputStream inStream = new FileInputStream(file_to_download);
                  
        // obtains ServletContext
        ServletContext context = getServletContext();
         
        // gets MIME type of the file
        String mimeType = context.getMimeType(file_name);
        // set to binary type if MIME mapping not found
        if (mimeType == null)
            mimeType = ProjectConstants.DEFAULT_MIME_TYPE;
        
        //System.out.println("MIME type: " + mimeType);
         
        // modifies response
        response.setContentType(mimeType);
        response.setContentLength((int) file_to_download.length());
         
        // forces download
        String headerKey = "Content-Disposition";
        String headerValue = String.format("attachment; filename=\"%s\"", ProjectConstants.PRIVATE_KEY_FILE_DEFAULT_NAME);
        response.setHeader(headerKey, headerValue);
         
        // obtains response's output stream
        OutputStream outStream = response.getOutputStream();
         
        byte[] buffer = new byte[BUFFER_SIZE];
        int bytesRead;
        while ((bytesRead = inStream.read(buffer)) != -1)
            outStream.write(buffer, 0, bytesRead);
                 
        inStream.close();
        outStream.close();  
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

}
