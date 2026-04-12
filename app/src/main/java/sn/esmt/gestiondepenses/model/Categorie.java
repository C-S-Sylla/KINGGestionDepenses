package sn.esmt.gestiondepenses.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "categories")
public class Categorie {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public String nom;
    public String icone;
    public String couleur;
    public boolean estDefaut;

    // Constructeur
    public Categorie(String nom, String icone, String couleur) {
        this.nom = nom;
        this.icone = icone;
        this.couleur = couleur;
        this.estDefaut = false;
    }
}