package com.kael.dao;

import com.kael.bean.Secret;
import com.kael.bean.PPPInterface;
import com.kael.bean.Profile;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import me.legrange.mikrotik.ApiConnection;
import me.legrange.mikrotik.MikrotikApiException;

/**
 * Created by kael on 17/08/2016.
 */
public class PPP {


    public List<Secret> getSecrets(ApiConnection cn){
        ArrayList<Secret> secretList = new ArrayList<Secret>();

        String command = "/ppp/secret/print";

        try {

            if(cn.isConnected()){
                List<Map<String, String>> result = cn.execute(command);
                Secret secret = null;

                for (Map<String, String> r : result) {
                    secret = new Secret();

                    for (Map.Entry<String, String> e : r.entrySet()) {

                        switch(e.getKey()){
                            case "name":
                                secret.setName(e.getValue());
                                break;
                            case ".id":
                                secret.setId(e.getValue());
                                break;
                            case "disabled":
                                if(e.getValue().equals("false")) secret.setDisabled(false);
                                else secret.setDisabled(true);
                                break;
                        }
                    }
                    secretList.add(secret);
                }
            }
        } catch (MikrotikApiException e) {
            e.printStackTrace();
        }finally{
            return secretList;
        }
    }


    public void changeStatus(String id, boolean disabled, ApiConnection cn){

        String s_disabled = disabled?"yes":"no";

        String command = "/ppp/secret/set .id="+id+" disabled="+s_disabled;

            try {
                if(cn.isConnected()){
                    cn.execute(command);
                }
            } catch (MikrotikApiException e) {
                e.printStackTrace();
            }
    }

    public List<PPPInterface> getInterfaces(ApiConnection cn){
        ArrayList<PPPInterface> list = new ArrayList<PPPInterface>();

        String command = "/ppp/active/print";

        try {

            if(cn.isConnected()){
                List<Map<String, String>> result = cn.execute(command);
                PPPInterface obj = null;

                for (Map<String, String> r : result) {
                    obj = new PPPInterface();

                    for (Map.Entry<String, String> e : r.entrySet()) {

                        switch(e.getKey()){
                            case "name":
                                obj.setName(e.getValue());
                                break;
                            case ".id":
                                obj.setId(e.getValue());
                                break;
                            case "address":
                                obj.setRemoteAdress(e.getValue());
                                break;
                            case "uptime":
                                obj.setUptime(e.getValue());
                                break;
                        }
                    }
                    list.add(obj);
                }
            }
        } catch (MikrotikApiException e) {
            e.printStackTrace();
        }finally{
            return list;
        }
    }

    public String addSecret(Secret secret, ApiConnection cn){
        String id = null;

        if(secret !=null){
            String command = "/ppp/secret/add name=\"%s\" password=\"%s\" profile=%s service=pppoe comment=\"%s\"";
            String comment = "Registrado desde android Fecha = ";
            Date date = new Date();
            comment += date.toString();


            command = String.format(command, secret.getName(), secret.getPassword(), secret.getProfile(), comment);

            if(cn.isConnected()){
                try {
                    List<Map<String, String>> result = cn.execute(command);
                    Map<String, String> r = result.get(0);
                    id = r.get("ret");
                } catch (MikrotikApiException e) {
                    id = null;
                    e.printStackTrace();
                }
            }
        }

        return id;
    }


    public List<Profile> getProfiles(ApiConnection cn){
        List<Profile> list = new ArrayList<Profile>();

        String command = "/ppp/profile/print";

        try {

            if(cn.isConnected()){
                List<Map<String, String>> result = cn.execute(command);
                Profile obj = null;

                for (Map<String, String> r : result) {
                    obj = new Profile();

                    for (Map.Entry<String, String> e : r.entrySet()) {

                        if(e.getKey().equals("name")){
                            obj.setName(e.getValue());
                            break;
                        }
                    }
                    list.add(obj);
                }
            }
        } catch (MikrotikApiException e) {
            e.printStackTrace();
        }finally{
            return list;
        }
    }

}
