package sn.esmt.gestiondepenses.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import java.util.List;
import sn.esmt.gestiondepenses.model.Categorie;
import sn.esmt.gestiondepenses.model.Depense;

@Dao
public interface AppDao {
    @Insert
    void insertCategorie(Categorie categorie);

    @Query("SELECT * FROM categories")
    List<Categorie> getAllCategories();

    // --- GESTION DES DÉPENSES ---

    @Insert
    void insertDepense(Depense depense);

    @Update
    void updateDepense(Depense depense); // Pour MODIFIER

    @Delete
    void deleteDepense(Depense depense); // Pour SUPPRIMER

    @Query("SELECT * FROM depenses ORDER BY date DESC")
    List<Depense> getAllDepenses(); // Tri par date décroissant (Déjà fait !)

    @Query("SELECT * FROM depenses WHERE categorieId = :catId ORDER BY date DESC")
    List<Depense> getDepensesByCategorie(int catId);

    @Query("SELECT * FROM depenses WHERE date >= :dateDebut AND date <= :dateFin ORDER BY date DESC")
    List<Depense> getDepensesByPeriode(long dateDebut, long dateFin);
    @Query("SELECT * FROM depenses WHERE (:catId = 0 OR categorieId = :catId) AND date >= :dateDebut AND date <= :dateFin ORDER BY date DESC")
    List<Depense> getDepensesFiltrees(int catId, long dateDebut, long dateFin);
    @Query("SELECT * FROM depenses WHERE id = :id LIMIT 1")
    Depense getDepenseById(int id);

}