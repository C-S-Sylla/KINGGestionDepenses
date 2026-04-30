package sn.esmt.gestiondepenses.database;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import sn.esmt.gestiondepenses.model.Budget;
import sn.esmt.gestiondepenses.model.Categorie;
import sn.esmt.gestiondepenses.model.Depense;
import sn.esmt.gestiondepenses.model.Revenu;
import sn.esmt.gestiondepenses.model.Utilisateur;
import sn.esmt.gestiondepenses.model.Revenu;

@Database(entities = {Categorie.class, Depense.class, Utilisateur.class, Budget.class, Revenu.class}, version = 6, exportSchema = false)
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
