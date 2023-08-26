package net.poppinger.ligretto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.poppinger.legretto.server.APIController;
import net.poppinger.legretto.server.Application;
import net.poppinger.legretto.server.PutCardCommand;
import net.poppinger.legretto.server.Table;
import net.poppinger.ligretto.model.TableResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("legrettoserver")
public class TableController {
    private Application application;
    private APIController apiController;


    private static Map<String, Integer> sessionPlayerMap;

    public TableController() {

        sessionPlayerMap=new HashMap<>();

        application=new Application();
        application.init(new TableResponse());

        apiController=new APIController();
    }

    private String getCookie(HttpServletRequest request, String name){
        var cookies= request.getCookies();
        for (var cook: cookies) {
            if (cook.getName().equals(name)){
                return cook.getValue();
            }
        }
        return null;
    }
    private String getSession(HttpServletRequest request){
        return getCookie(request,"JSESSIONID");
    }

    private int getNextFeePlayerNumber(){

        for(int x=0;x<4;x++){
            boolean found=false;
            for (var player: sessionPlayerMap.values()) {
                if (player==x) found=true;
            }
            if (!found){
                return x;
            }
        }
        return -2;
    }

    private int getPlayerNumFromSession(HttpServletRequest request){
        var session=getSession(request);
        if (sessionPlayerMap.containsKey(session) && sessionPlayerMap.get(session)!=null) {
            return sessionPlayerMap.get(session);
        }

        var nextFreePlayer=getNextFeePlayerNumber();
        if (nextFreePlayer>=0) {
            sessionPlayerMap.put(session, nextFreePlayer);
            return nextFreePlayer;
        }

        return nextFreePlayer;
    }


    @RequestMapping(value = "getTable",produces = "application/json")
    public String getTable(HttpServletRequest request) throws JsonProcessingException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        var loginUser= auth.getPrincipal();
        var playerId=getPlayerNumFromSession(request);
        ((TableResponse)application.table).currentPlayerId=playerId;
        return apiController.GetTableJSON(application.table);
    }

    @RequestMapping(value = "init",produces = "application/json")
    public String init(HttpServletRequest request) throws JsonProcessingException{
        application.init(new TableResponse());
        var playerId=getPlayerNumFromSession(request);
        ((TableResponse)application.table).currentPlayerId=playerId;
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
    public String putCard(HttpServletRequest request,HttpServletResponse response, @RequestParam String data) throws JsonMappingException,JsonProcessingException {
        var command=data;

        var loginPlayerId=getPlayerNumFromSession(request);

        ObjectMapper om=new ObjectMapper();
        var cmd= om.readValue(command, PutCardCommand.class);

        var success=application.putCard(cmd,loginPlayerId,application.table);
        var sanityOK=application.saintyCheck();
        if (!sanityOK){
            success="Sanity check failed!!!";
        }
        if (success==null) {
            var playerId=getPlayerNumFromSession(request);
            ((TableResponse)application.table).currentPlayerId=playerId;
            return apiController.GetTableJSON(application.table);
        }

        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        return success;

    }

    @RequestMapping(value = "swapDeck",produces = "application/json")
    public String swapDeck(HttpServletRequest request,HttpServletResponse response, @RequestParam String player) throws JsonMappingException,JsonProcessingException {
        var playerStr=player;
        Integer playerInt=Integer.parseInt(playerStr);
        var success=application.swapDeck(playerInt);

        if (!application.saintyCheck()){
            success="SanityCheck Failed!!!";
        }
        if (success==null) {
            var playerId=getPlayerNumFromSession(request);
            ((TableResponse)application.table).currentPlayerId=playerId;
            return apiController.GetTableJSON(application.table);
        }

        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        return success;
    }


}