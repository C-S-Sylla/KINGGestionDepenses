package sn.esmt.gestiondepenses.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

// @Entity dit à Room : "crée une table SQLite nommée 'revenus' à partir de cette classe"
@Entity(tableName = "revenus")
public class Revenu {
    // Clé primaire auto-incrémentée par SQLite (pas besoin de la fournir à l'insert)
    @PrimaryKey(autoGenerate = true)
    public int id;

    // Montant du revenu en FCFA. double car on peut avoir des décimales (ex: 1500.50)
    public double montant;

    public String source;

    public long date;

    // Description optionnelle saisie par l'utilisateur
    public String description;


    public int utilisateurId;

    public Revenu(double montant, String source, long date, String description, int utilisateurId) {
        this.montant = montant;
        this.source = source;
        this.date = date;
        this.description = description;
        this.utilisateurId = utilisateurId;
    }
}
