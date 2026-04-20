package sn.esmt.gestiondepenses.ui;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import sn.esmt.gestiondepenses.R;
import sn.esmt.gestiondepenses.database.AppDatabase;
import sn.esmt.gestiondepenses.model.Utilisateur;

public class RegisterActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        EditText editPrenom = findViewById(R.id.editPrenomReg);
        EditText editPseudo = findViewById(R.id.editPseudoReg);
        EditText editPassword = findViewById(R.id.editPasswordReg);
        Button btnRegister = findViewById(R.id.btnRegister);

        btnRegister.setOnClickListener(v -> {
            String prenom = editPrenom.getText().toString();
            String pseudo = editPseudo.getText().toString();
            String mdp = editPassword.getText().toString();

            if (prenom.isEmpty() || pseudo.isEmpty() || mdp.isEmpty()) {
                Toast.makeText(this, "Remplissez tous les champs", Toast.LENGTH_SHORT).show();
                return;
            }

            Utilisateur newUser = new Utilisateur(pseudo, mdp, prenom);
            AppDatabase.getInstance(this).appDao().insertUtilisateur(newUser);

            Toast.makeText(this, "Compte créé avec succès !", Toast.LENGTH_SHORT).show();
            finish();
        });
    }
}