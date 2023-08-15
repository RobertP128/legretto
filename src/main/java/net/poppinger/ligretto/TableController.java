package net.poppinger.ligretto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import net.poppinger.legretto.server.APIController;
import net.poppinger.legretto.server.Application;
import net.poppinger.legretto.server.PutCardCommand;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.*;


@RestController
@RequestMapping("legrettoserver")
public class TableController {
    private Application application;
    private APIController apiController;

    public TableController() {
        application=new Application();
        application.init();

        apiController=new APIController();
    }

    @RequestMapping(value = "getTable",produces = "application/json")
    public String getTable() throws JsonProcessingException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        var loginUser= auth.getPrincipal();

        return apiController.GetTableJSON(application.table);
    }

    @RequestMapping(value = "init",produces = "application/json")
    public String init() throws JsonProcessingException{
        application.init();
        return apiController.GetTableJSON(application.table);
    }

    @RequestMapping(value = "sanityCheck",produces = "application/json")
    public String sanityCheck(HttpServletResponse response){
        if (application.saintyCheck()){
            return "\"OK\"";
        }

        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        return "\"SanityProblem detected\"";
    }


    @RequestMapping(value = "putCard",produces = "application/json")
    public String putCard(HttpServletResponse response, @RequestParam String data) throws JsonMappingException,JsonProcessingException {
        var command=data;

        ObjectMapper om=new ObjectMapper();
        var cmd= om.readValue(command, PutCardCommand.class);

        var success=application.putCard(cmd,application.table);
        var sanityOK=application.saintyCheck();
        if (!sanityOK){
            success="Sanity check failed!!!";
        }
        if (success==null) {
            return apiController.GetTableJSON(application.table);
        }

        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        return success;

    }

    @RequestMapping(value = "swapDeck",produces = "application/json")
    public String swapDeck(HttpServletResponse response, @RequestParam String player) throws JsonMappingException,JsonProcessingException {
        var playerStr=player;
        Integer playerInt=Integer.parseInt(playerStr);
        var success=application.swapDeck(playerInt);

        if (!application.saintyCheck()){
            success="SanityCheck Failed!!!";
        }
        if (success==null) {
            return apiController.GetTableJSON(application.table);
        }

        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        return success;
    }


}