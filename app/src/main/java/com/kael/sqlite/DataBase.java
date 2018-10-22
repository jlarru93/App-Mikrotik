package com.kael.sqlite;

/**
 * Created by kael on 29/08/2016.
 */

import android.database.sqlite.SQLiteDatabase;

public class DataBase {
    public final static int DATABASE_VERSION=1;
    public final static String DATABASE_NAME="db_mikrotik";


    public static final class servidor{
        public final static String TABLE_NAME="TBL_SERVER";
        public final static String KEY_COD="COD_SERVER";
        public final static String KEY_SERVERNAME="SERVERNAME_SERVER";
        public final static String KEY_HOST="HOST_SERVER";
        public final static String KEY_PORT="PORT_SERVER";
        public final static String KEY_USER="USER_SERVER";
        public final static String KEY_PASSWORD="PASSWORD_SERVER";
        public final static String KEY_SSLCONNECTION="SSLCONNECTION_SERVER";
        public final static String KEY_SSLCERTIFICATE="SSLCERTIFICATE_SERVER";

        public final static String QUERY_CREATE=
                "create table "+TABLE_NAME+" ( "
                        +KEY_COD+" Integer primary key autoincrement,"
                        +KEY_SERVERNAME+" text,"
                        +KEY_HOST+" text,"
                        +KEY_PORT+" Integer,"
                        +KEY_USER+" text,"
                        +KEY_PASSWORD+" text,"
                        +KEY_SSLCONNECTION+" numeric,"
                        +KEY_SSLCERTIFICATE+" numeric"
                        +" );";
        public final static String QUERY_DROP=
                "DROP TABLE IF EXISTS "+TABLE_NAME+";";
    }


    public static void onCreate(SQLiteDatabase db) {
        db.execSQL(DataBase.servidor.QUERY_CREATE);
    }

    public static void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DataBase.servidor.QUERY_DROP);
        onCreate(db);
    }

}
