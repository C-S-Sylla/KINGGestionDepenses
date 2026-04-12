package sn.esmt.gestiondepenses.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "depenses")
public class Depense {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public double montant;
    public String rubrique;
    public int categorieId;
    public long date;
    public String description;
    public String moyenPaiement;

    // LE CONSTRUCTEUR DOIT AVOIR EXACTEMENT CES 5 ARGUMENTS
    public Depense(double montant, int categorieId, long date, String description, String moyenPaiement) {
        this.montant = montant;
        this.categorieId = categorieId;
        this.date = date;
        this.description = description;
        this.moyenPaiement = moyenPaiement;
    }
}