/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SOAService.DAOAdapter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import SOAService.Model.telefon;

public class DOAParsowanie {

	public static String parsowanieSOAListNaJSON(List<telefon> Komunikaty) {
        JSONObject noweKomunikaty = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        for (telefon k : Komunikaty) {
            JSONObject nowyKom = new JSONObject();
            nowyKom.put("Id", k.getId());
            nowyKom.put("MSISDN", k.getMSISDN());
            nowyKom.put("Komunikat", k.getKomunikat());
            //nowyKom.put("Kolejka", k.getKolejka());
            jsonArray.add(nowyKom);
        }
        noweKomunikaty.put("Komunikaty", jsonArray);
        return noweKomunikaty.toJSONString();

    }

	public static List<telefon> parsowanieSOAJSONNaClass(String komunikaty) throws Exception {

        List<telefon> SOAKomunikaty = new ArrayList<>();
        JSONParser jsonParser = new JSONParser();

        JSONObject jsonObject = (JSONObject) jsonParser.parse(komunikaty);
        JSONArray Komunikaty = (JSONArray) jsonObject.get("Komunikaty");
        Iterator i = Komunikaty.iterator();
        while (i.hasNext()) {
            JSONObject iObj = (JSONObject) i.next();
            telefon iKom = new telefon();
            iKom.setId(Integer.parseInt(iObj.get("Id").toString()));
            //iKom.setKolejka(iObj.get("Kolejka").toString());
            iKom.setMSISDN(iObj.get("MSISDN").toString());
            iKom.setKomunikat(iObj.get("Komunikaty").toString());
            SOAKomunikaty.add(iKom);
        }
        return SOAKomunikaty;
    }
    
		public static String parsowanieDAOListNaJSON(List<telefon> Komunikaty) {
        JSONObject noweKomunikaty = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        for (telefon k : Komunikaty) {
            JSONObject nowyKom = new JSONObject();
            nowyKom.put("Id", k.getId());
            nowyKom.put("MSISDN", k.getMSISDN());
            nowyKom.put("Komunikat", k.getKomunikat());
            jsonArray.add(nowyKom);
        }
        noweKomunikaty.put("Komunikaty", jsonArray);
        return noweKomunikaty.toJSONString();

    }

	public static List<telefon> parsowanieDAOJSONNaClass(String komunikaty) throws Exception {

        List<telefon> DAOKomunikaty = new ArrayList<>();
        JSONParser jsonParser = new JSONParser();

        JSONObject jsonObject = (JSONObject) jsonParser.parse(komunikaty);
        JSONArray Komunikaty = (JSONArray) jsonObject.get("Komunikaty");
        Iterator i = Komunikaty.iterator();
        while (i.hasNext()) {
            JSONObject iObj = (JSONObject) i.next();
            telefon iKom = new telefon();
            iKom.setId(Integer.parseInt(iObj.get("Id").toString()));;
            iKom.setMSISDN(iObj.get("MSISDN").toString());
            iKom.setKomunikat(iObj.get("Komunikat").toString());
            DAOKomunikaty.add(iKom);
        }
        return DAOKomunikaty;
    }

}
