package db;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by Bernardo on 16/05/2016.
 */
@DatabaseTable (tableName = "amigos_vigilantes")
public class AmigoVigilante {

    @DatabaseField (generatedId = true)
    private int idAmigoVigilante;
    @DatabaseField (canBeNull = false)
    private int amigoRecepetor;
    @DatabaseField (canBeNull = false)
    private int amigoEmisor;
    @DatabaseField (canBeNull = false, width = 45)
    private String correoAmigoReceptor;
    @DatabaseField (canBeNull = false, width = 45)
    private String correoAmigoEmisor;
    @DatabaseField (width = 250)
    private String mensaje;
    @DatabaseField
    private int estatus;

    public AmigoVigilante(){

    }

    public AmigoVigilante(int amigoRecepetor, int amigoEmisor, String correoAmigoReceptor, String correoAmigoEmisor, String mensaje, int estatus) {
        this.amigoRecepetor = amigoRecepetor;
        this.amigoEmisor = amigoEmisor;
        this.correoAmigoReceptor = correoAmigoReceptor;
        this.correoAmigoEmisor = correoAmigoEmisor;
        this.mensaje = mensaje;
        this.estatus = estatus;
    }

    public void setAmigoRecepetor(int amigoRecepetor) {
        this.amigoRecepetor = amigoRecepetor;
    }

    public void setAmigoEmisor(int amigoEmisor) {
        this.amigoEmisor = amigoEmisor;
    }

    public void setCorreoAmigoReceptor(String correoAmigoReceptor) {
        this.correoAmigoReceptor = correoAmigoReceptor;
    }

    public void setCorreoAmigoEmisor(String correoAmigoEmisor) {
        this.correoAmigoEmisor = correoAmigoEmisor;
    }
    public void setEstatus(int estatus) {
        this.estatus = estatus;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public int getIdAmigoVigilante() {
        return idAmigoVigilante;
    }

    public int getAmigoRecepetor() {
        return amigoRecepetor;
    }

    public int getAmigoEmisor() {
        return amigoEmisor;
    }

    public String getCorreoAmigoReceptor() {
        return correoAmigoReceptor;
    }

    public String getCorreoAmigoEmisor() {
        return correoAmigoEmisor;
    }

    public String getMensaje() {
        return mensaje;
    }
    public int getEstatus() {
        return estatus;
    }
}
