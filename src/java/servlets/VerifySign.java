package servlets;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.Signature;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import key_handler.PublicKeyVerifier;
import org.apache.tomcat.util.http.fileupload.servlet.ServletFileUpload;
import org.apache.tomcat.util.http.fileupload.util.Streams;
import utils.ProjectConstants;


@WebServlet(name="VerifySign", urlPatterns = {"/VerifySign"})
@MultipartConfig(
    fileSizeThreshold = 1024 * 1024, // 1MB
    maxFileSize = 1024 * 1024 * 10, // 10MB
    maxRequestSize = 1024 * 1024 * 10 + 1024 // 10.1 MB 
)
public class VerifySign extends HttpServlet {

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
        
        if (!ServletFileUpload.isMultipartContent(request)){
            response.setStatus(400);
            return;
        }
        
        Part pdf_part = request.getPart("pdf_file");
        Part public_key_part = request.getPart("public_key");
        
        String pdf = Streams.asString(pdf_part.getInputStream(), "UTF-8");        
        String public_key = Streams.asString(public_key_part.getInputStream(), "UTF-8");
        
        byte[] pdf_bytes = pdf.getBytes(StandardCharsets.UTF_8);
        try {
            /*
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        KeySpec keySpec = new PKCS8EncodedKeySpec(privKeyBytes, "RSA");
        return (this.privateKey = (RSAPrivateKey) keyFactory.generatePrivate(keySpec));
            */
            
            Signature signature = Signature.getInstance(ProjectConstants.SIGNATURE);
            PublicKeyVerifier pkv = new PublicKeyVerifier();
            pkv.loadPublicKey(public_key);
            
            boolean is_verified = pkv.isVerified(signature, pdf_bytes);
            
            response.setStatus(200, is_verified ? "Data is verified" : "Signature doesn't match");
            
            System.out.println("**********************************");
            System.out.println(is_verified);
            
        } catch (Exception ex) {
            response.setStatus(500);
            Logger.getLogger(VerifySign.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
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
