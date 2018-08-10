package db;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by Bernardo on 16/05/2016.
 */
@DatabaseTable (tableName = "historial_alertas")
public class HistorialAlertas {
    @DatabaseField (generatedId = true)
    private int id;
    @DatabaseField (canBeNull = false, width = 150)
    private String medioConexion;
    @DatabaseField (canBeNull = false, width = 45)
    private String alertaDeVoz;
    @DatabaseField (canBeNull = false, width = 250)
    private String mensaje;
    @DatabaseField (canBeNull = false, width = 45)
    private String posicionGPS;
    @DatabaseField (canBeNull = false, width = 45)
    private String fecha;
    @DatabaseField (canBeNull = false, width = 45)
    private String hora;
    @DatabaseField (canBeNull = false)
    private int idUsuario;

    public HistorialAlertas(){

    }

    public HistorialAlertas(String medioConexion, String alertaDeVoz, String mensaje, String posicionGPS, String fecha, String hora, int idUsuario) {
        this.medioConexion = medioConexion;
        this.alertaDeVoz = alertaDeVoz;
        this.mensaje = mensaje;
        this.posicionGPS = posicionGPS;
        this.fecha = fecha;
        this.hora = hora;
        this.idUsuario = idUsuario;
    }

    public void setMedioConexion(String medioConexion) {
        this.medioConexion = medioConexion;
    }

    public void setAlertaDeVoz(String alertaDeVoz) {
        this.alertaDeVoz = alertaDeVoz;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public void setPosicionGPS(String posicionGPS) {
        this.posicionGPS = posicionGPS;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public int getId() {
        return id;
    }

    public String getMedioConexion() {
        return medioConexion;
    }

    public String getAlertaDeVoz() {
        return alertaDeVoz;
    }

    public String getMensaje() {
        return mensaje;
    }

    public String getPosicionGPS() {
        return posicionGPS;
    }

    public String getFecha() {
        return fecha;
    }

    public String getHora() {
        return hora;
    }

    public int getIdUsuario() {
        return idUsuario;
    }
}