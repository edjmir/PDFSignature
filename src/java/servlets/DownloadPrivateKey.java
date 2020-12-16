package servlets;

import java.io.File;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import key_handler.KeyPairGen;
import utils.ProjectConstants;

public class DownloadPrivateKey extends HttpServlet {
    
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");
        
        String passphrase = request.getParameter("passphrase");
        
        final String file_path = ProjectConstants.PRIVATE_KEY_FILE_DEFAULT_PATH
            .concat(String.valueOf(System.currentTimeMillis()))
            .concat(ProjectConstants.PRIVATE_KEY_FILE_DEFAULT_NAME);
        
        request.setAttribute("file_path", file_path);
        request.setAttribute("file_name", ProjectConstants.PRIVATE_KEY_FILE_DEFAULT_NAME);
        KeyPairGen keyPairGen = new KeyPairGen();
        
        try {
            if("null".equals(String.valueOf(passphrase)))
                keyPairGen.createAndSaveKeys(new File(file_path));
            else
                keyPairGen.createAndSaveKeys(new File(file_path), passphrase);
        } catch (Exception ex) {
            response.setStatus(500);
            return;
        }
        
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

}
