package sn.esmt.gestiondepenses; // Vérifie que c'est bien ton package

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

// IMPORTS IMPORTANTS !
import sn.esmt.gestiondepenses.ui.DashboardFragment;
import sn.esmt.gestiondepenses.ui.DepensesFragment;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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