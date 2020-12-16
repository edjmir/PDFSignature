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
import utils.ProjectConstants;

public class DownloadFile extends HttpServlet {
    
    /**
     * This Servlets needs a file path to download it
     * The file path must be setted as a request Attribute
     * named "file_path"
    */
    
    private final short BUFFER_SIZE = 4096;

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        request.setCharacterEncoding("UTF-8");
        
        final String file_path = (String) request.getAttribute("file_path");
        final String file_name = (String) request.getAttribute("file_name");
        
        File file_to_download = new File(file_path);
        FileInputStream inStream = new FileInputStream(file_to_download);
                  
        // obtains ServletContext
        ServletContext context = getServletContext();
         
        // gets MIME type of the file
        String mimeType = context.getMimeType(file_path);
        // set to binary type if MIME mapping not found
        if (mimeType == null)
            mimeType = ProjectConstants.DEFAULT_MIME_TYPE;
        
        //System.out.println("MIME type: " + mimeType);
         
        // modifies response
        response.setContentType(mimeType);
        response.setContentLength((int) file_to_download.length());
         
        // forces download
        String headerKey = "Content-Disposition";
        String headerValue = String.format("attachment; filename=\"%s\"", file_name);
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
    
}
