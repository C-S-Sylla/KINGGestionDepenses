package sn.esmt.gestiondepenses.database;
import sn.esmt.gestiondepenses.model.Budget;
import sn.esmt.gestiondepenses.model.Revenu;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import java.util.List;
import sn.esmt.gestiondepenses.model.Categorie;
import sn.esmt.gestiondepenses.model.Depense;
import sn.esmt.gestiondepenses.model.Utilisateur;
import sn.esmt.gestiondepenses.model.Revenu;
import sn.esmt.gestiondepenses.model.Revenu;

@Dao
public interface AppDao {
    @Insert
    void insertCategorie(Categorie categorie);

    @Query("SELECT * FROM categories")
    List<Categorie> getAllCategories();

    @Insert
    void insertDepense(Depense depense);

    @Update
    void updateDepense(Depense depense);

    @Delete
    void deleteDepense(Depense depense);

    // FILTRES PAR UTILISATEUR (Indispensable !)
    @Query("SELECT * FROM depenses WHERE utilisateurId = :userId ORDER BY date DESC")
    List<Depense> getAllDepenses(int userId);

    @Query("SELECT * FROM depenses WHERE utilisateurId = :userId AND (:catId = 0 OR categorieId = :catId) AND date >= :dateDebut AND date <= :dateFin ORDER BY date DESC")
    List<Depense> getDepensesFiltrees(int userId, int catId, long dateDebut, long dateFin);

    @Query("SELECT SUM(montant) FROM depenses WHERE utilisateurId = :userId AND date >= :dateDebut AND date <= :dateFin")
    Double getTotalDepensesPeriode(int userId, long dateDebut, long dateFin);

    @Query("SELECT * FROM depenses WHERE utilisateurId = :userId ORDER BY date DESC LIMIT 5")
    List<Depense> getCinqDernieresDepenses(int userId);

    @Query("SELECT * FROM depenses WHERE id = :id LIMIT 1")
    Depense getDepenseById(int id);

    @Insert
    void insertUtilisateur(Utilisateur utilisateur);

    @Query("SELECT * FROM utilisateurs WHERE pseudo = :pseudo AND motDePasse = :mdp LIMIT 1")
    Utilisateur connexion(String pseudo, String mdp);


    // Insertion : on passe l'objet complet, Room génère l'id automatiquement
    @Insert
    void insertRevenu(Revenu revenu);

    // Mise à jour : Room repère la ligne grâce à l'id (clé primaire)
    @Update
    void updateRevenu(Revenu revenu);

    // Suppression : pareil, identifie la ligne par son id
    @Delete
    void deleteRevenu(Revenu revenu);

    // Tous les revenus de l'utilisateur, du plus récent au plus ancien
    @Query("SELECT * FROM revenus WHERE utilisateurId = :userId ORDER BY date DESC")
    List<Revenu> getAllRevenus(int userId);

    // Filtrage combiné : par source et par période de dates
    // Astuce : (:source = '' OR source = :source) → si source vide, on ne filtre pas
    @Query("SELECT * FROM revenus WHERE utilisateurId = :userId " +
            "AND (:source = '' OR source = :source) " +
            "AND date >= :dateDebut AND date <= :dateFin " +
            "ORDER BY date DESC")
    List<Revenu> getRevenusFiltres(int userId, String source, long dateDebut, long dateFin);


    @Query("SELECT SUM(montant) FROM revenus WHERE utilisateurId = :userId " +
            "AND date >= :dateDebut AND date <= :dateFin")
    Double getTotalRevenusPeriode(int userId, long dateDebut, long dateFin);

    // 5 derniers revenus pour un éventuel résumé sur le Dashboard
    @Query("SELECT * FROM revenus WHERE utilisateurId = :userId ORDER BY date DESC LIMIT 5")
    List<Revenu> getCinqDerniersRevenus(int userId);

    // Récupère un revenu unique par son id — utilisé en mode Modification
    @Query("SELECT * FROM revenus WHERE id = :id LIMIT 1")
    Revenu getRevenuById(int id);


    @Query("SELECT * FROM budgets WHERE utilisateurId = :userId AND mois = :m AND annee = :a")
    List<Budget> getBudgetsDuMois(int userId, int m, int a);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertBudget(Budget budget);

    @Query("SELECT SUM(montant) FROM depenses WHERE utilisateurId = :userId AND categorieId = :catId AND date >= :debut AND date <= :fin")
    Double getSommeDepensesParCategorie(int userId, int catId, long debut, long fin);
}