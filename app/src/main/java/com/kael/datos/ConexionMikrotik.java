package com.kael.datos;

import android.util.Log;

import com.kael.bean.Servidor;
import com.kael.security.AnonymousSocketFactory;

import javax.net.SocketFactory;
import javax.net.ssl.SSLSocketFactory;

import me.legrange.mikrotik.ApiConnection;
import me.legrange.mikrotik.MikrotikApiException;

/**
 * Created by kael on 17/08/2016.
 */

//Advertencia: Para la conexion TLS anonima se recomienda utilizar JDK > 7
//En el entorno desktop no funciona TLS anonimo con JDK 7
//En android por suerte si funciona :v

public class ConexionMikrotik {
    private static ConexionMikrotik instance;
    private int timeout;

    private ConexionMikrotik() {
        timeout = 4000;
    }


    public static ConexionMikrotik getInstance() {
        if (instance == null) instance = new ConexionMikrotik();
        return instance;
    }

    public ApiConnection getConnection(Servidor servidor) throws MikrotikApiException {
        ApiConnection cn = null;

        if (servidor != null) {
            boolean secureTLS = servidor.isSSLConnection();
            boolean certificateTLS = servidor.isSSLCertificate();

            String IP = servidor.getHost();
            int port = servidor.getPort();
            String user = servidor.getUser();
            String password = servidor.getPassword();

            if (secureTLS) {
                if (certificateTLS) {
                    //Conexion TLS con certificado SSL
                    cn = ApiConnection.connect(SSLSocketFactory.getDefault(), IP, port, timeout);
                } else {
                    //Conexion TLS sin certificado SSL = conexion TLS anonima
                    cn = ApiConnection.connect(AnonymousSocketFactory.getDefault(), IP, port, timeout);
                }
            } else {
                //Conexion simple sin TLS
                cn = ApiConnection.connect(SocketFactory.getDefault(), IP, port, timeout);
            }

            if (cn.isConnected()) {
                cn.login(user, password);
            }
            Log.d("LOGIN", "conexion establecida");
        }

        return cn;
    }
}
