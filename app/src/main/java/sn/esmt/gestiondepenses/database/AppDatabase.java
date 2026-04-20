package sn.esmt.gestiondepenses.database;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import sn.esmt.gestiondepenses.model.Categorie;
import sn.esmt.gestiondepenses.model.Depense;
import sn.esmt.gestiondepenses.model.Utilisateur;


// On ajoute Depense.class ici dans la liste des entities
// On passe la version à 2 car on a ajouté une table
// On met exportSchema = false pour enlever le warning jaune
@Database(entities = {Categorie.class, Depense.class, Utilisateur.class}, version = 5, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    public abstract AppDao appDao();
    private static AppDatabase instance;

    public static AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "gestion_depenses_db")
                    .fallbackToDestructiveMigration()
                    .allowMainThreadQueries()
                    .build();
        }
        return instance;
    }
}