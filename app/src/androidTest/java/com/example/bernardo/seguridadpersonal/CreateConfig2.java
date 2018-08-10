package com.example.bernardo.seguridadpersonal;

import com.j256.ormlite.android.apptools.OrmLiteConfigUtil;

import java.io.IOException;
import java.sql.SQLException;

/**
 * Created by Bernardo on 20/04/2016.
 */
public class CreateConfig2 extends OrmLiteConfigUtil {

    public static void main(String[] args)  throws IOException,SQLException{
        writeConfigFile("config.txt");
    }

}
