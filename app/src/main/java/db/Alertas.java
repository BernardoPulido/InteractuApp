package db;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by Bernardo on 16/05/2016.
 */
@DatabaseTable (tableName = "alertas")
public class Alertas {

    @DatabaseField (generatedId = true)
    private int idAlerta;
    @DatabaseField (canBeNull = false, width = 45)
    private String nombreAlerta;
    @DatabaseField (canBeNull = false, width = 25)
    private String patronDetonante;
    @DatabaseField (canBeNull = false, width = 250)
    private String mensaje;
    @DatabaseField (canBeNull = false)
    private int idUsuario;

    public Alertas(){

    }

    public Alertas(String nombreAlerta, String patronDetonante, String mensaje, int idUsuario) {
        this.nombreAlerta = nombreAlerta;
        this.patronDetonante = patronDetonante;
        this.mensaje = mensaje;
        this.idUsuario = idUsuario;
    }

    public int getIdAlerta() {
        return idAlerta;
    }

    public String getNombreAlerta() {
        return nombreAlerta;
    }

    public String getPatronDetonante() {
        return patronDetonante;
    }

    public String getMensaje() {
        return mensaje;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setNombreAlerta(String nombreAlerta) {
        this.nombreAlerta = nombreAlerta;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public void setPatronDetonante(String patronDetonante) {
        this.patronDetonante = patronDetonante;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }
}
