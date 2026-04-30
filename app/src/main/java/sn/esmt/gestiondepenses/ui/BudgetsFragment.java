package sn.esmt.gestiondepenses.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import sn.esmt.gestiondepenses.R;
import sn.esmt.gestiondepenses.adapter.BudgetAdapter;
import sn.esmt.gestiondepenses.database.AppDatabase;
import sn.esmt.gestiondepenses.model.Budget;

public class BudgetsFragment extends Fragment {

    private RecyclerView recyclerView;
    private BudgetAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_budgets, container, false);

        recyclerView = root.findViewById(R.id.recyclerBudgets);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new BudgetAdapter();
        recyclerView.setAdapter(adapter);

        FloatingActionButton fab = root.findViewById(R.id.fabAddBudget);
        fab.setOnClickListener(v -> startActivity(new Intent(getActivity(), AddBudgetActivity.class)));

        chargerDonneesBudget();
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        chargerDonneesBudget();
    }

    private void chargerDonneesBudget() {
        if (getContext() == null) return;

        SharedPreferences prefs = getContext().getSharedPreferences("MesParametres", Context.MODE_PRIVATE);
        int userId = prefs.getInt("ID_UTILISATEUR", -1);

        // 1. Obtenir Mois et Année actuels
        Calendar cal = Calendar.getInstance();
        int mois = cal.get(Calendar.MONTH) + 1;
        int annee = cal.get(Calendar.YEAR);

        // 2. Calculer début et fin du mois pour les dépenses
        cal.set(Calendar.DAY_OF_MONTH, 1);
        long debut = cal.getTimeInMillis();
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        long fin = cal.getTimeInMillis();

        try {
            AppDatabase db = AppDatabase.getInstance(getContext());
            // 3. Récupérer la liste des plafonds définis
            List<Budget> listeBudgets = db.appDao().getBudgetsDuMois(userId, mois, annee);
            List<BudgetAdapter.BudgetUIModel> uiModels = new ArrayList<>();

            for (Budget b : listeBudgets) {
                // 4. Pour chaque budget, calculer la somme dépensée
                Double somme;
                if (b.categorieId == 0) {
                    // Budget Global : Somme de TOUTES les dépenses
                    somme = db.appDao().getTotalDepensesPeriode(userId, debut, fin);
                } else {
                    // Budget par catégorie
                    somme = db.appDao().getSommeDepensesParCategorie(userId, b.categorieId, debut, fin);
                }
                if (somme == null) somme = 0.0;

                // 5. Trouver le nom de la catégorie pour l'affichage
                String nomCat = getNomCategorie(b.categorieId);

                uiModels.add(new BudgetAdapter.BudgetUIModel(b, somme, nomCat));
            }

            adapter.setData(uiModels);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getNomCategorie(int id) {
        String[] cats = getResources().getStringArray(R.array.categories_budget);
        if (id < cats.length) return cats[id];
        return "Inconnu";
    }
}