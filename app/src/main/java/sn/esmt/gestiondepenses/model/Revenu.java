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

    // Source du revenu : "Salaire", "Commerce", "Freelance", "Don", "Autre"
    // On stocke en String (pas d'enum) pour simplifier le mapping Room
    public String source;

    // Date du revenu en timestamp (millisecondes depuis 1970)
    // Pratique pour les comparaisons SQL : date >= dateDebut AND date <= dateFin
    public long date;

    // Description optionnelle saisie par l'utilisateur
    public String description;

    // ID de l'utilisateur connecté qui possède ce revenu
    // INDISPENSABLE pour l'isolation multi-utilisateur (sinon tout le monde voit tout)
    public int utilisateurId;

    // Constructeur public utilisé quand on crée un nouveau Revenu en mémoire avant l'insert
    // Note : on ne passe PAS l'id, c'est Room qui le génère
    public Revenu(double montant, String source, long date, String description, int utilisateurId) {
        this.montant = montant;
        this.source = source;
        this.date = date;
        this.description = description;
        this.utilisateurId = utilisateurId;
    }
}
