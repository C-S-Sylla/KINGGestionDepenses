package sn.esmt.gestiondepenses.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import sn.esmt.gestiondepenses.MainActivity;
import sn.esmt.gestiondepenses.R;
import sn.esmt.gestiondepenses.database.AppDatabase;
import sn.esmt.gestiondepenses.model.Utilisateur;

public class LoginActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        EditText editPseudo = findViewById(R.id.editPseudoLog);
        EditText editPassword = findViewById(R.id.editPasswordLog);
        Button btnLogin = findViewById(R.id.btnLogin);
        TextView txtGoToRegister = findViewById(R.id.txtGoToRegister);

        // Aller vers la page d'inscription
        txtGoToRegister.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
        });

        // Bouton Connexion
        btnLogin.setOnClickListener(v -> {
            String pseudo = editPseudo.getText().toString();
            String mdp = editPassword.getText().toString();

            // Requête à la base de données
            Utilisateur user = AppDatabase.getInstance(this).appDao().connexion(pseudo, mdp);

            if (user != null) {
                SharedPreferences prefs = getSharedPreferences("MesParametres", Context.MODE_PRIVATE);
                prefs.edit().putString("NOM_UTILISATEUR", user.prenom).apply();
                prefs.edit().putInt("ID_UTILISATEUR", user.id).apply();

                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                finish();
            } else {
                Toast.makeText(this, "Identifiants incorrects", Toast.LENGTH_SHORT).show();
            }
        });
    }
}