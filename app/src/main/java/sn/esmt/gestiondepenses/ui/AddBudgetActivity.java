package sn.esmt.gestiondepenses.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Calendar;
import sn.esmt.gestiondepenses.R;
import sn.esmt.gestiondepenses.database.AppDatabase;
import sn.esmt.gestiondepenses.model.Budget;

public class AddBudgetActivity extends AppCompatActivity {

    private Spinner spinnerCat;
    private EditText editMontant;
    private Button btnSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_budget);

        spinnerCat = findViewById(R.id.spinnerBudgetCat);
        editMontant = findViewById(R.id.editMontantBudget);
        btnSave = findViewById(R.id.btnSaveBudget);

        btnSave.setOnClickListener(v -> sauvegarderBudget());
    }

    private void sauvegarderBudget() {
        String montantStr = editMontant.getText().toString();

        if (montantStr.isEmpty()) {
            Toast.makeText(this, "Entrez un montant", Toast.LENGTH_SHORT).show();
            return;
        }

        double montant = Double.parseDouble(montantStr);
        if (montant <= 0) {
            Toast.makeText(this, "Le montant doit être > 0", Toast.LENGTH_SHORT).show();
            return;
        }

        // 1. Récupérer les infos système
        SharedPreferences prefs = getSharedPreferences("MesParametres", Context.MODE_PRIVATE);
        int userId = prefs.getInt("ID_UTILISATEUR", -1);

        Calendar cal = Calendar.getInstance();
        int moisActuel = cal.get(Calendar.MONTH) + 1;
        int anneeActuelle = cal.get(Calendar.YEAR);

        // 2. Récupérer la catégorie choisie
        // Index 0 = Budget Global. Index 1 = Alimentation (ID=1), etc.
        int catId = spinnerCat.getSelectedItemPosition();

        // 3. Créer l'objet Budget
        Budget nouveauBudget = new Budget(catId, montant, moisActuel, anneeActuelle, userId);

        // 4. Enregistrer en base de données
        AppDatabase.getInstance(this).appDao().insertBudget(nouveauBudget);

        Toast.makeText(this, "Budget défini pour ce mois !", Toast.LENGTH_SHORT).show();
        finish();
    }
}