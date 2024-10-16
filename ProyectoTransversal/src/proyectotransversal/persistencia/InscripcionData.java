/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package proyectotransversal.persistencia;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import proyectotransversal.modelo.Alumno;
import proyectotransversal.modelo.Conexion;
import proyectotransversal.modelo.Inscripcion;
import proyectotransversal.modelo.Materia;

/**
 *
 * @author Osman Herman
 * @author Ulises Perez
 * @author Nahuel Alegre
 * @author Nicolas Dominguez
 */
public class InscripcionData {
    private Connection con = null;
    private MateriaData md = new MateriaData();
    private AlumnoData ad = new AlumnoData();

    public InscripcionData() {
        this.con = Conexion.getConexion();
    }
    
    public void guardarInscripcion(Inscripcion insc) {
        String sql = "INSERT INTO inscripcion(idAlumno, idMateria, nota) VALUES(?,?,?)";
        
        try {
            PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            
            ps.setInt(1, insc.getAlumno().getIdAlumno());
            ps.setInt(2, insc.getMateria().getIdMateria());
            ps.setDouble(3, insc.getNota());
            
            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            
            if (rs.next()) {
                insc.setIdInscripcion(rs.getInt(1));
                JOptionPane.showMessageDialog(null, "Inscripción exitosa!");
            }
            
            ps.close();
            
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error al acceder a la tabla inscripción: "+ex.getMessage());
        }
    }
    
    public void actualizarNota(int idAlumno, int idMateria, double nota) {
        String sql = "UPDATE inscripcion SET nota = ? WHERE idAlumno = ? AND idMateria = ?";
        
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            
            ps.setDouble(1, nota);
            ps.setInt(2, idAlumno);
            ps.setInt(3, idMateria);
            
           if (ps.executeUpdate() > 0) 
               JOptionPane.showMessageDialog(null, "Actualización exitosa!");
           
           ps.close();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error al actualizar la nota: "+ex.getMessage());
        }   
    }
        
    public void borrarInscripcion(int idAlumno, int idMateria) {
        String sql = "DELETE FROM inscripcion WHERE idAlumno = ? AND idMateria = ?";
        
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            
            
            ps.setInt(1, idAlumno);
            ps.setInt(2, idMateria);
            
           if (ps.executeUpdate() > 0) 
               JOptionPane.showMessageDialog(null, "Eliminación exitosa!");
           
           ps.close();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error al actualizar la nota: "+ex.getMessage());
        }   
    }

    public List<Inscripcion> obtenerInscripciones() {
        List<Inscripcion> lista = new ArrayList<>();
        
        String sql = "SELECT * FROM inscripcion";
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            
            while(rs.next()) {
                Inscripcion insc = new Inscripcion(
                    rs.getInt("idInscripto"),
                    ad.buscarAlumno(rs.getInt("idAlumno")),
                    md.buscarMateria(rs.getInt("idMateria")),
                    rs.getDouble("nota")
                );
                
                lista.add(insc);
            }
            
            ps.close();
            
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error al acceder a la tabla inscripción");
        }
        
        return lista;
    }
    
    public List<Inscripcion> obtenerInscripcionesPorAlumno(int idAlumno) {
        List<Inscripcion> lista = new ArrayList<>();
        
        String sql = "SELECT * FROM inscripcion WHERE idAlumno = ?";
        
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, idAlumno);
            
            ResultSet rs = ps.executeQuery();
            
            while(rs.next()) {
                Inscripcion insc = new Inscripcion(
                    rs.getInt("idInscripto"),
                    ad.buscarAlumno(rs.getInt("idAlumno")),
                    md.buscarMateria(rs.getInt("idMateria")),
                    rs.getDouble("nota")
                );
                
                lista.add(insc);
            }
            
            ps.close();
            
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error al acceder a la tabla inscripción");
        }
        
        return lista;
    }

    public List<Materia> obtenerMateriasDeAlumno(int idAlumno) {
        List<Materia> listaMaterias = new ArrayList<>();
        
        String sql = 
            "SELECT inscripcion.idMateria, nombre, año "
          + "FROM inscripcion, materia "
          + "WHERE inscripcion.idMateria = materia.idMateria "
          + "AND inscripcion.idAlumno = ?;";

//        String sql = "SELECT * FROM inscripcion, materia WHERE inscripcion.idMateria = materia.idMateria AND inscripcion.idAlumno = ?;";
        
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, idAlumno);
            
            ResultSet rs = ps.executeQuery();
            while(rs.next()) {
                Materia mat = new Materia(
                    rs.getInt("idMateria"),
                    rs.getString("nombre"),
                    rs.getInt("año")
                );
                
                listaMaterias.add(mat);
            }
            
            ps.close();
            
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error al acceder a la tabla inscripción XXXXXXX"+ex.getMessage());
        }
        
        return listaMaterias;
    }
    
    public List<Materia> obtenerNoMateriasDeAlumno(int idAlumno) {
        List<Materia> listaMaterias = new ArrayList<>();
        
        String sql = "SELECT * FROM materia WHERE estado = 1 AND idMateria NOT IN"
                + "(SELECT idMateria FROM inscripcion WHERE idAlumno = ?);";
        
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, idAlumno);
            
            ResultSet rs = ps.executeQuery();
            while(rs.next()) {
                Materia mat = new Materia(
                    rs.getInt("idMateria"),
                    rs.getString("nombre"),
                    rs.getInt("año")
                );
                
                listaMaterias.add(mat);
            }
            
            ps.close();
            
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error al acceder a la tabla inscripción");
        }
        
        return listaMaterias;
    }

    public List<Alumno> obtenerAlumnosDeMateria(int idMateria) {
        List<Alumno> listaAlumnos = new ArrayList<>();
        
        String sql = "SELECT a.idAlumno, dni, nombre, apellido, fechaNacimiento, estado "
                + "FROM inscripcion i, alumno a "
                + "WHERE i.idAlumno = a.idAlumno "
                + "AND idMateria = ? "
                + "AND estado = 1";
        
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, idMateria);
            
            ResultSet rs = ps.executeQuery();
            while(rs.next()) {
                Alumno alumno = new Alumno(
                    rs.getInt("idAlumno"),
                    rs.getString("apellido"),
                    rs.getString("nombre"),
                    rs.getDate("fechaNacimiento").toLocalDate(),
                    rs.getBoolean("estado")
                );
                
                listaAlumnos.add(alumno);
            }
            
            ps.close();
            
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error al acceder a la tabla inscripción");
        }
        
        return listaAlumnos;
    }
}
