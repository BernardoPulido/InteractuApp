package db;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by Bernardo on 20/04/2016.
 */
@DatabaseTable(tableName = "usuarios")
public class Usuario {

    @DatabaseField(generatedId = true)
    private int id;
    @DatabaseField (canBeNull = false, width = 100)
    private String nombre;
    @DatabaseField (canBeNull = false, width = 100)
    private String apellidos;
    @DatabaseField
    private int edad;
    @DatabaseField(canBeNull =false, width = 100)
    private String username;
    @DatabaseField (width = 16)
    private String contrasena;
    @DatabaseField(canBeNull = false, width = 45)
    private String correo;
    @DatabaseField (canBeNull = false)
    private int tipoRegistro;

    public Usuario(){

    }

    public Usuario(String nombre, String apellidos, int edad, String username, String contrasena, String correo, int tipoRegistro) {
       // this.id = id;
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.edad = edad;
        this.username = username;
        this.contrasena = contrasena;
        this.correo = correo;
        this.tipoRegistro = tipoRegistro;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public void setEdad(int edad) {
        this.edad = edad;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public void setTipoRegistro(int tipoRegistro) {
        this.tipoRegistro = tipoRegistro;
    }

    public int getIde() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getApellidos() {
        return apellidos;
    }

    public int getEdad() {
        return edad;
    }

    public String getUsername() {
        return username;
    }

    public String getContrasena() {
        return contrasena;
    }

    public String getCorreo() {
        return correo;
    }

    public int getTipoRegistro() {
        return tipoRegistro;
    }
}
