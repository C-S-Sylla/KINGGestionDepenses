package sn.esmt.gestiondepenses.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "depenses")
public class Depense {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public double montant;
    public int categorieId;
    public long date;
    public String description;
    public String moyenPaiement;
    public String rubrique;

    // NOUVEAU : On stocke l'ID de celui qui a créé la dépense
    public int utilisateurId;

    // Constructeur mis à jour avec utilisateurId
    public Depense(double montant, int categorieId, long date, String description, String moyenPaiement, int utilisateurId) {
        this.montant = montant;
        this.categorieId = categorieId;
        this.date = date;
        this.description = description;
        this.moyenPaiement = moyenPaiement;
        this.utilisateurId = utilisateurId;
    }
}