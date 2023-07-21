package net.poppinger.legretto.server;

import java.io.*;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class HelloServlet extends HttpServlet {
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