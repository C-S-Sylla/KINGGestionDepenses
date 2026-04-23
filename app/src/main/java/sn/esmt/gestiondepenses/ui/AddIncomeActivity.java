package sn.esmt.gestiondepenses.ui;

import android.app.DatePickerDialog;
import android.content.SharedPreferences;
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
import sn.esmt.gestiondepenses.model.Revenu;

public class AddIncomeActivity extends AppCompatActivity {

    // Références aux composants du layout
    private TextView txtTitrePage;
    private EditText editMontant, editDescription;
    private Spinner spinnerSource;
    private Button btnDate, btnSave;

    // Date sélectionnée par l'utilisateur (par défaut : maintenant)
    private long dateSelectionneeTimestamp;

    // -1 = mode AJOUT, sinon = id du revenu en cours de MODIFICATION
    private int idRevenuEdition = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_income);

        // 1. LIER LES COMPOSANTS XML aux variables Java
        txtTitrePage = findViewById(R.id.txtTitrePage);
        editMontant = findViewById(R.id.editMontant);
        spinnerSource = findViewById(R.id.spinnerSource);
        btnDate = findViewById(R.id.btnDate);
        editDescription = findViewById(R.id.editDescription);
        btnSave = findViewById(R.id.btnSave);

        // Par défaut, la date est celle d'aujourd'hui (CDC §2.2.1)
        dateSelectionneeTimestamp = System.currentTimeMillis();

        // 2. DÉTECTER LE MODE (Ajout vs Modification)
        // Si l'intent contient "ID_REVENU_A_MODIFIER", on est en mode modification
        idRevenuEdition = getIntent().getIntExtra("ID_REVENU_A_MODIFIER", -1);

        if (idRevenuEdition != -1) {
            // MODE MODIFICATION : adapter le titre et le bouton
            txtTitrePage.setText("Modifier le Revenu");
            btnSave.setText("METTRE À JOUR");
            // Pré-remplir les champs avec les anciennes valeurs
            chargerDonneesRevenu(idRevenuEdition);
        }

        // 3. GESTION DES CLICS
        btnDate.setOnClickListener(v -> afficherCalendrier());
        btnSave.setOnClickListener(v -> enregistrerOuModifierRevenu());
    }

    // Pré-remplit le formulaire avec les données d'un revenu existant
    private void chargerDonneesRevenu(int id) {
        AppDatabase db = AppDatabase.getInstance(this);
        Revenu revenuAModifier = db.appDao().getRevenuById(id);

        // Sécurité : si le revenu n'existe plus en BDD, on ne fait rien
        if (revenuAModifier == null) return;

        // Champ montant
        editMontant.setText(String.valueOf(revenuAModifier.montant));

        // Champ description
        editDescription.setText(revenuAModifier.description);

        // Spinner source : on cherche la position de l'item qui correspond à la source
        ArrayAdapter<CharSequence> adapter = (ArrayAdapter<CharSequence>) spinnerSource.getAdapter();
        if (adapter != null) {
            int position = adapter.getPosition(revenuAModifier.source);
            spinnerSource.setSelection(position);
        }

        // Date : on remet le timestamp et on affiche au format JJ/MM/AAAA
        dateSelectionneeTimestamp = revenuAModifier.date;
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(dateSelectionneeTimestamp);
        String dateAffichee = cal.get(Calendar.DAY_OF_MONTH) + "/"
                + (cal.get(Calendar.MONTH) + 1) + "/" // +1 car les mois commencent à 0 en Java
                + cal.get(Calendar.YEAR);
        btnDate.setText("Date : " + dateAffichee);
    }

    // Ouvre le sélecteur de date Android natif
    private void afficherCalendrier() {
        Calendar calendrier = Calendar.getInstance();
        // Le calendrier s'ouvre sur la date déjà sélectionnée (utile en mode modif)
        calendrier.setTimeInMillis(dateSelectionneeTimestamp);
        int annee = calendrier.get(Calendar.YEAR);
        int mois = calendrier.get(Calendar.MONTH);
        int jour = calendrier.get(Calendar.DAY_OF_MONTH);

        // Lambda : appelée quand l'utilisateur valide une date
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year, month, dayOfMonth) -> {
                    String dateAffichee = dayOfMonth + "/" + (month + 1) + "/" + year;
                    btnDate.setText("Date : " + dateAffichee);

                    // Convertir la date choisie en timestamp pour le stockage en BDD
                    Calendar dateChoisie = Calendar.getInstance();
                    dateChoisie.set(year, month, dayOfMonth);
                    dateSelectionneeTimestamp = dateChoisie.getTimeInMillis();
                }, annee, mois, jour);

        // Empêche la sélection d'une date dans le futur
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        datePickerDialog.show();
    }

    // Validation des champs + insertion ou mise à jour en BDD
    private void enregistrerOuModifierRevenu() {
        String montantStr = editMontant.getText().toString();

        // VALIDATION 1 : montant obligatoire
        if (montantStr.isEmpty()) {
            Toast.makeText(this, "Veuillez entrer un montant", Toast.LENGTH_SHORT).show();
            return;
        }

        double montant = Double.parseDouble(montantStr);

        // VALIDATION 2 : montant strictement positif
        if (montant <= 0) {
            Toast.makeText(this, "Le montant doit être > 0", Toast.LENGTH_SHORT).show();
            return;
        }

        // RÉCUPÉRATION des autres champs
        String source = spinnerSource.getSelectedItem().toString();
        String description = editDescription.getText().toString();

        // RÉCUPÉRATION de l'ID utilisateur stocké au login
        SharedPreferences prefs = getSharedPreferences("MesParametres", MODE_PRIVATE);
        int userId = prefs.getInt("ID_UTILISATEUR", -1);

        // CONSTRUCTION de l'objet Revenu (sans id, Room le génère à l'insert)
        Revenu revenu = new Revenu(montant, source, dateSelectionneeTimestamp, description, userId);

        // ACCÈS BDD
        AppDatabase db = AppDatabase.getInstance(this);

        if (idRevenuEdition == -1) {
            // MODE AJOUT : nouvelle ligne
            db.appDao().insertRevenu(revenu);
            Toast.makeText(this, "Revenu enregistré !", Toast.LENGTH_SHORT).show();
        } else {
            // MODE MODIFICATION : on garde l'ancien id et on update
            revenu.id = idRevenuEdition;
            db.appDao().updateRevenu(revenu);
            Toast.makeText(this, "Revenu modifié !", Toast.LENGTH_SHORT).show();
        }

        // Fermeture de l'activity → retour à la liste
        finish();
    }
}