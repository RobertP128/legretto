package net.poppinger.legretto.server;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class TableController extends HttpServlet {
    private String message;
    private Application application;
    private APIController apiController;

    public void init() {
        application=new Application();
        application.init();

        apiController=new APIController();
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        String uri=request.getRequestURI();
        if (uri.endsWith("getTable")){
            response.setContentType("application/json");
            PrintWriter out = response.getWriter();
            out.println(apiController.GetTableJSON(application.table));
            return;
        }
        if (uri.endsWith("init")){
            response.setContentType("application/json");
            PrintWriter out = response.getWriter();
            application.init();
            out.println("\"OK\"");
            return;
        }
        if (uri.endsWith("sanityCheck")){
            response.setContentType("application/json");
            PrintWriter out = response.getWriter();
            if (application.saintyCheck()){
                out.println("\"OK\"");
            }
            else {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                out.println("\"SanityProblem detected\"");
            }
            return;
        }
        if (uri.endsWith("putCard")){
            var command=request.getParameter("data");

            ObjectMapper om=new ObjectMapper();
            var cmd= om.readValue(command,PutCardCommand.class);

            var success=application.putCard(cmd,application.table);
            response.setContentType("application/json");
            PrintWriter out = response.getWriter();
            var sanityOK=application.saintyCheck();
            if (!sanityOK){
                success="Sanity check failed!!!";
            }
            if (success==null) {
                out.println(apiController.GetTableJSON(application.table));
            }
            else {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                out.println(success);
            }
            return;
        }
        if (uri.endsWith("swapDeck")){
            var playerStr=request.getParameter("player");
            Integer player=Integer.parseInt(playerStr);
            var success=application.swapDeck(player);

            response.setContentType("application/json");
            PrintWriter out = response.getWriter();
            if (!application.saintyCheck()){
                success="SanityCheck Failed!!!";
            }
            if (success==null) {
                out.println(apiController.GetTableJSON(application.table));
            }
            else {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                out.println(success);
            }
            return;
        }


        response.setContentType("text/html");
        // Hello
        PrintWriter out = response.getWriter();
        out.println("<html><body>");
        out.println(application.table.players[0]);
        out.println("<h1>" + message + "</h1>");
        out.println("</body></html>");
    }






    public void destroy() {
    }
}