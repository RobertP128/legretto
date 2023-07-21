package net.poppinger.legretto.server;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;;


public class APIController {
    public String GetTableJSON(Table table) throws JsonProcessingException {
        ObjectMapper mapper=new ObjectMapper();
         return mapper.writeValueAsString(table);
    }
}
