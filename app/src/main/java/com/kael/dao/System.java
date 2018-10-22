package com.kael.dao;

import java.util.List;
import java.util.Map;
import java.util.Set;

import me.legrange.mikrotik.ApiConnection;
import me.legrange.mikrotik.MikrotikApiException;

/**
 * Created by kael on 24/08/2016.
 */
public class System {


    public String getUptime(ApiConnection cn){
        String uptime = null;

        String command = "/system/resource/print";

        try {
            if(cn.isConnected()){
                List<Map<String, String>> result = cn.execute(command);

                if(result!=null && result.size()>0){
                    Map<String, String> map = result.get(0);
                    Set<Map.Entry<String, String>> entries = map.entrySet();
                    for(Map.Entry<String, String> entry : entries){
                        if(entry.getKey().equals("uptime")){
                            uptime = entry.getValue();
                            break;
                        }
                    }
                }

            }
        } catch (MikrotikApiException e) {
            e.printStackTrace();
        }finally{
            return uptime;
        }
    }
}
