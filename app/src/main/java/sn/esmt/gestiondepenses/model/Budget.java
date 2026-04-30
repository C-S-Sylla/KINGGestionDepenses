package sn.esmt.gestiondepenses.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "budgets")
public class Budget {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public int categorieId;
    public double montantPlafond;
    public int mois;
    public int annee;
    public int utilisateurId;

    public Budget(int categorieId, double montantPlafond, int mois, int annee, int utilisateurId) {
        this.categorieId = categorieId;
        this.montantPlafond = montantPlafond;
        this.mois = mois;
        this.annee = annee;
        this.utilisateurId = utilisateurId;
    }

}