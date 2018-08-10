package db;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by Bernardo on 16/05/2016.
 * 1. Facebook
 * 2. Twitter
 * 3. Telegram
 * 4. Llamada
 * 5. Amigo vigilante
 */
@DatabaseTable (tableName = "medios_conexion")
public class MediosConexion {

    @DatabaseField (generatedId = true)
    private int idMedio;
    @DatabaseField (canBeNull = false)
    private int medio;
    @DatabaseField (canBeNull = false, width = 450)
    private String personas;
    @DatabaseField (canBeNull = false)
    private int idAlerta;

    public MediosConexion(){

    }

    public MediosConexion(int medio, String personas, int idAlerta) {
        this.medio = medio;
        this.personas = personas;
        this.idAlerta = idAlerta;
    }

    public int getIdMedio() {
        return idMedio;
    }

    public int getMedio() {
        return medio;
    }

    public String getPersonas() {
        return personas;
    }

    public int getIdAlerta() {
        return idAlerta;
    }

    public void setMedio(int medio) {
        this.medio = medio;
    }

    public void setPersonas(String personas) {
        this.personas = personas;
    }

    public void setIdAlerta(int idAlerta) {
        this.idAlerta = idAlerta;
    }
}
