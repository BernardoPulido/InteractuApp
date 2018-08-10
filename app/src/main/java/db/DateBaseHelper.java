package db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.example.bernardo.seguridadpersonal.R;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;

/**
 * Created by Bernardo on 20/04/2016.
 */
public class DateBaseHelper extends OrmLiteSqliteOpenHelper{

    private static final String DATABASE_NAME = "seguridad.db";
    private static final int DATABASE_VERSION = 6;

    private Dao<Usuario,Integer> daoUsuario;
    private Dao<Alertas,Integer> daoAlertas;
    private Dao<AmigoVigilante,Integer> daoAmigo;
    private Dao<HistorialAlertas,Integer> daoHistorial;
    private Dao<MediosConexion,Integer> daoMedios;
    private Dao<Parametros,Integer> daoParametros;


    private RuntimeExceptionDao<Usuario,Integer> daoREusuario;
    private RuntimeExceptionDao<Alertas,Integer> daoREalertas;
    private RuntimeExceptionDao<AmigoVigilante,Integer> daoREamigo;
    private RuntimeExceptionDao<HistorialAlertas,Integer> daoREhistorial;
    private RuntimeExceptionDao<MediosConexion,Integer> daoREmedios;
    private RuntimeExceptionDao<Parametros,Integer> daoREParametros;


    public DateBaseHelper (Context contexto){
        super(contexto, DATABASE_NAME, null, DATABASE_VERSION, R.raw.config);
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource) {

        try {
            TableUtils.createTable(connectionSource, Usuario.class);
            TableUtils.createTable(connectionSource, Alertas.class);
            TableUtils.createTable(connectionSource, AmigoVigilante.class);
            TableUtils.createTable(connectionSource, HistorialAlertas.class);
            TableUtils.createTable(connectionSource, MediosConexion.class);
            TableUtils.createTable(connectionSource, Parametros.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }


    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource, int i, int i1) {

        try {
            TableUtils.dropTable(connectionSource,Usuario.class,false);
            TableUtils.dropTable(connectionSource, Alertas.class, false);
            TableUtils.dropTable(connectionSource, AmigoVigilante.class, false);
            TableUtils.dropTable(connectionSource, HistorialAlertas.class, false);
            TableUtils.dropTable(connectionSource, MediosConexion.class, false);
            TableUtils.dropTable(connectionSource, Parametros.class, false);
            onCreate(sqLiteDatabase, connectionSource);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void close() {
        super.close();
        daoREusuario = null;
        daoREalertas = null;
        daoREamigo = null;
        daoREhistorial = null;
        daoREmedios = null;
        daoREParametros = null;

        daoUsuario = null;
        daoAlertas = null;
        daoAmigo = null;
        daoHistorial = null;
        daoMedios = null;
        daoParametros = null;

    }

    public Dao<Usuario, Integer> getDaoUsuario() throws SQLException{
        if (daoUsuario==null){
            daoUsuario = getDao(Usuario.class);

        }
        return daoUsuario;
    }
    public Dao<Alertas, Integer> getDaoAlertas() throws SQLException{
        if (daoAlertas==null){
            daoAlertas = getDao(Alertas.class);

        }
        return daoAlertas;
    }

    public Dao<AmigoVigilante, Integer> getDaoAmigo() throws SQLException{
        if (daoAmigo==null){
            daoAmigo = getDao(AmigoVigilante.class);

        }
        return daoAmigo;
    }
    public Dao<HistorialAlertas, Integer> getDaoHistorial() throws SQLException{
        if (daoHistorial==null){
            daoHistorial = getDao(HistorialAlertas.class);

        }
        return daoHistorial;
    }
    public Dao<MediosConexion, Integer> getDaoMedios() throws SQLException{
        if (daoMedios==null){
            daoMedios = getDao(MediosConexion.class);

        }
        return daoMedios;
    }
    public Dao<Parametros, Integer> getDaoParametros() throws SQLException{
        if (daoParametros==null){
            daoParametros = getDao(Parametros.class);

        }
        return daoParametros;
    }


    //DAO con excepciones
    public RuntimeExceptionDao<Usuario, Integer> getDaoREusuario() {

        if (daoREusuario==null){
            daoREusuario = getRuntimeExceptionDao(Usuario.class);

        }

        return daoREusuario;
    }
    public RuntimeExceptionDao<Alertas, Integer> getDaoREalertas() {

        if (daoREalertas==null){
            daoREalertas = getRuntimeExceptionDao(Alertas.class);

        }

        return daoREalertas;
    }
    public RuntimeExceptionDao<AmigoVigilante, Integer> getDaoREamigo() {

        if (daoREamigo==null){
            daoREamigo = getRuntimeExceptionDao(AmigoVigilante.class);

        }

        return daoREamigo;
    }

    public RuntimeExceptionDao<HistorialAlertas, Integer> getDaoREhistorial() {

        if (daoREhistorial==null){
            daoREhistorial = getRuntimeExceptionDao(HistorialAlertas.class);

        }

        return daoREhistorial;
    }

    public RuntimeExceptionDao<MediosConexion, Integer> getDaoREmedios() {

        if (daoREmedios==null){
            daoREmedios = getRuntimeExceptionDao(MediosConexion.class);

        }

        return daoREmedios;
    }

    public RuntimeExceptionDao<Parametros, Integer> getDaoREParametros() {

        if (daoREParametros==null){
            daoREParametros = getRuntimeExceptionDao(Parametros.class);

        }

        return daoREParametros;
    }

}

