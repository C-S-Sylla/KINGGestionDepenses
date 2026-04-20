package sn.esmt.gestiondepenses.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "utilisateurs")
public class Utilisateur {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public String pseudo;
    public String motDePasse;
    public String prenom;

    public Utilisateur(String pseudo, String motDePasse, String prenom) {
        this.pseudo = pseudo;
        this.motDePasse = motDePasse;
        this.prenom = prenom;
    }
}