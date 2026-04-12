package sn.esmt.gestiondepenses.ui;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

import sn.esmt.gestiondepenses.R;
import sn.esmt.gestiondepenses.database.AppDatabase;
import sn.esmt.gestiondepenses.model.Depense;

public class AddExpenseActivity extends AppCompatActivity {

    private TextView txtTitrePage;
    private EditText editMontant, editRubrique, editDescription;
    private Spinner spinnerCategorie, spinnerPaiement;
    private Button btnDate, btnSave;

    private long dateSelectionneeTimestamp;
    private int idDepenseEdition = -1; // -1 signifie qu'on est en mode "Ajout"

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_expense);

        // 1. Lier les composants XML
        txtTitrePage = findViewById(R.id.txtTitrePage);
        editMontant = findViewById(R.id.editMontant);
        spinnerCategorie = findViewById(R.id.spinnerCategorie);
        editRubrique = findViewById(R.id.editRubrique);
        btnDate = findViewById(R.id.btnDate);
        spinnerPaiement = findViewById(R.id.spinnerPaiement);
        editDescription = findViewById(R.id.editDescription);
        btnSave = findViewById(R.id.btnSave);

        dateSelectionneeTimestamp = System.currentTimeMillis();

        // 2. VÉRIFIER SI ON EST EN MODE MODIFICATION
        idDepenseEdition = getIntent().getIntExtra("ID_DEPENSE_A_MODIFIER", -1);

        if (idDepenseEdition != -1) {
            // MODE MODIFICATION
            txtTitrePage.setText("Modifier la Dépense");
            btnSave.setText("METTRE À JOUR");
            chargerDonneesDepense(idDepenseEdition); // On pré-remplit les champs
        }

        // 3. Gestion des clics
        btnDate.setOnClickListener(v -> afficherCalendrier());
        btnSave.setOnClickListener(v -> enregistrerOuModifierDepense());
    }

    // Méthode pour pré-remplir les champs avec l'ancienne donnée
    private void chargerDonneesDepense(int id) {
        AppDatabase db = AppDatabase.getInstance(this);
        Depense depenseAModifier = db.appDao().getDepenseById(id);

        if (depenseAModifier != null) {
            editMontant.setText(String.valueOf(depenseAModifier.montant));
            editRubrique.setText(depenseAModifier.rubrique);
            editDescription.setText(depenseAModifier.description);

            // Re-sélectionner la bonne catégorie (L'ID commence à 1, l'index du spinner à 0)
            spinnerCategorie.setSelection(depenseAModifier.categorieId - 1);

            // Re-sélectionner le moyen de paiement
            ArrayAdapter<CharSequence> adapter = (ArrayAdapter<CharSequence>) spinnerPaiement.getAdapter();
            if (adapter != null) {
                int spinnerPosition = adapter.getPosition(depenseAModifier.moyenPaiement);
                spinnerPaiement.setSelection(spinnerPosition);
            }

            // Gérer la date
            dateSelectionneeTimestamp = depenseAModifier.date;
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(dateSelectionneeTimestamp);
            String dateAffichee = cal.get(Calendar.DAY_OF_MONTH) + "/" + (cal.get(Calendar.MONTH) + 1) + "/" + cal.get(Calendar.YEAR);
            btnDate.setText("Date : " + dateAffichee);
        }
    }

    private void afficherCalendrier() {
        Calendar calendrier = Calendar.getInstance();
        calendrier.setTimeInMillis(dateSelectionneeTimestamp); // Ouvre sur la date déjà choisie
        int annee = calendrier.get(Calendar.YEAR);
        int mois = calendrier.get(Calendar.MONTH);
        int jour = calendrier.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year, month, dayOfMonth) -> {
                    String dateAffichee = dayOfMonth + "/" + (month + 1) + "/" + year;
                    btnDate.setText("Date : " + dateAffichee);
                    Calendar dateChoisie = Calendar.getInstance();
                    dateChoisie.set(year, month, dayOfMonth);
                    dateSelectionneeTimestamp = dateChoisie.getTimeInMillis();
                }, annee, mois, jour);

        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        datePickerDialog.show();
    }

    private void enregistrerOuModifierDepense() {
        String montantStr = editMontant.getText().toString();

        if (montantStr.isEmpty()) {
            Toast.makeText(this, "Veuillez entrer un montant", Toast.LENGTH_SHORT).show();
            return;
        }

        double montant = Double.parseDouble(montantStr);
        if (montant <= 0) {
            Toast.makeText(this, "Le montant doit être > 0", Toast.LENGTH_SHORT).show();
            return;
        }

        String rubrique = editRubrique.getText().toString();
        String description = editDescription.getText().toString();
        String moyenPaiement = spinnerPaiement.getSelectedItem().toString();
        int categorieId = spinnerCategorie.getSelectedItemPosition() + 1;

        Depense depense = new Depense(montant, categorieId, dateSelectionneeTimestamp, description, moyenPaiement);
        depense.rubrique = rubrique;

        AppDatabase db = AppDatabase.getInstance(this);

        if (idDepenseEdition == -1) {
            // MODE AJOUT : On insert une nouvelle ligne
            db.appDao().insertDepense(depense);
            Toast.makeText(this, "Dépense enregistrée !", Toast.LENGTH_SHORT).show();
        } else {
            // MODE MODIFICATION : On garde l'ancien ID et on met à jour
            depense.id = idDepenseEdition;
            db.appDao().updateDepense(depense);
            Toast.makeText(this, "Dépense modifiée !", Toast.LENGTH_SHORT).show();
        }

        finish();
    }
}