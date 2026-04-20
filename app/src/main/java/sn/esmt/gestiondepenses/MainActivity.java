package sn.esmt.gestiondepenses; // Vérifie que c'est bien ton package

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import sn.esmt.gestiondepenses.ui.DashboardFragment;
import sn.esmt.gestiondepenses.ui.DepensesFragment;
import android.content.SharedPreferences;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatDelegate;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.switchmaterial.SwitchMaterial;
import android.view.View;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // --- GESTION DU TIROIR PARAMÈTRES (GLISSEMENT GAUCHE) ---
        NavigationView navSettings = findViewById(R.id.nav_view_settings);
        View headerView = navSettings.getHeaderView(0);

        EditText editUserName = headerView.findViewById(R.id.editUserName);
        Button btnSaveName = headerView.findViewById(R.id.btnSaveName);
        SwitchMaterial switchTheme = headerView.findViewById(R.id.switchTheme);

        // La mémoire du téléphone (SharedPreferences)
        SharedPreferences prefs = getSharedPreferences("MesParametres", MODE_PRIVATE);

        String nomSauvegarde = prefs.getString("NOM_UTILISATEUR", "");
        editUserName.setText(nomSauvegarde);

        boolean isNightMode = prefs.getBoolean("MODE_NUIT", true);
        switchTheme.setChecked(isNightMode);

        btnSaveName.setOnClickListener(v -> {
            String nouveauNom = editUserName.getText().toString();
            prefs.edit().putString("NOM_UTILISATEUR", nouveauNom).apply();

            // On recharge le Dashboard pour que le nom s'affiche
            loadFragment(new DashboardFragment());

            // Optionnel : fermer le tiroir automatiquement
            // findViewById(R.id.drawer_layout).closeDrawers();
        });

        // 3. Bouton Switch Thème
        switchTheme.setOnCheckedChangeListener((buttonView, isChecked) -> {
            prefs.edit().putBoolean("MODE_NUIT", isChecked).apply();

            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES); // Charbon/Gold
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO); // Marbre/Platine
            }
        });

        // 1. Charger le Dashboard par défaut au lancement
        loadFragment(new DashboardFragment());

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);

        // 2. Gérer le clic sur les onglets
        bottomNav.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int id = item.getItemId();

            if (id == R.id.nav_accueil) {
                selectedFragment = new DashboardFragment();
            } else if (id == R.id.nav_depenses) {
                selectedFragment = new DepensesFragment();
            } else if (id == R.id.nav_revenus) {
                selectedFragment = new DashboardFragment(); // Placeholder
            } else if (id == R.id.nav_budgets) {
                selectedFragment = new DashboardFragment(); // Placeholder
            }

            if (selectedFragment != null) {
                loadFragment(selectedFragment);
                return true;
            }
            return false;
        });
    }

    // MÉTHODE OBLIGATOIRE POUR CHANGER DE FRAGMENT (C'est elle qui manquait !)
    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }
}