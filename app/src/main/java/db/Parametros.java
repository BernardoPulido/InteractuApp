package db;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by luis_ on 17/08/2016.
 *
 * 1. ID Telegram
 * 2. Email usuario en curso
 * 3. IdUsuario
 * 4. Nombre
 * 5. Apellidos
 * 6. Edad
 * 7. Username
 */
@DatabaseTable(tableName = "parametros")
public class Parametros {

    @DatabaseField(generatedId = true)
    private int idParametro;
    @DatabaseField(canBeNull = false)
    private int id;
    @DatabaseField(canBeNull = false, width = 200)
    private String descripcion;
    @DatabaseField(width = 200)
    private String descripcion2;

    public Parametros(){

    }

    public Parametros(int id, String descripcion, String descripcion2) {
        this.id = id;
        this.descripcion = descripcion;
        this.descripcion2 = descripcion2;
    }

    public int getIdParametro() {
        return idParametro;
    }

    public int getId() {
        return id;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public String getDescripcion2() {
        return descripcion2;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public void setDescripcion2(String descripcion2) {
        this.descripcion2 = descripcion2;
    }
}
