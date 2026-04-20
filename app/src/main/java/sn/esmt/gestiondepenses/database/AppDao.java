package sn.esmt.gestiondepenses.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import java.util.List;
import sn.esmt.gestiondepenses.model.Categorie;
import sn.esmt.gestiondepenses.model.Depense;
import sn.esmt.gestiondepenses.model.Utilisateur;

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
}