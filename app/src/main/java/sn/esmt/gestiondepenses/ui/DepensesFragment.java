package sn.esmt.gestiondepenses.ui;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Calendar;
import java.util.List;

import sn.esmt.gestiondepenses.R;
import sn.esmt.gestiondepenses.adapter.DepenseAdapter;
import sn.esmt.gestiondepenses.database.AppDatabase;
import sn.esmt.gestiondepenses.model.Depense;

public class DepensesFragment extends Fragment {

    private RecyclerView recyclerView;
    private DepenseAdapter adapter;
    private Spinner spinnerPeriode, spinnerCategorie;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_depenses, container, false);

        // 1. Initialisation UI
        recyclerView = root.findViewById(R.id.recyclerDepenses);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new DepenseAdapter();
        recyclerView.setAdapter(adapter);

        spinnerPeriode = root.findViewById(R.id.spinnerFiltrePeriode);
        spinnerCategorie = root.findViewById(R.id.spinnerFiltreCategorie);

        // 2. Gestion de la Suppression / Modification
        adapter.setOnItemClickListener(new DepenseAdapter.OnItemClickListener() {
            @Override
            public void onEditClick(Depense depense) {
                // On ouvre la page AddExpenseActivity
                Intent intent = new Intent(getActivity(), AddExpenseActivity.class);
                // On lui passe l'ID de la dépense en "bagage"
                intent.putExtra("ID_DEPENSE_A_MODIFIER", depense.id);
                startActivity(intent);
            }

            @Override
            public void onDeleteClick(Depense depense) {
                new AlertDialog.Builder(getContext())
                        .setTitle("Suppression")
                        .setMessage("Supprimer cette dépense ?")
                        .setPositiveButton("OUI", (dialog, which) -> {
                            AppDatabase.getInstance(getContext()).appDao().deleteDepense(depense);
                            appliquerFiltres(); // On rafraîchit la liste avec les filtres actuels
                            Toast.makeText(getContext(), "Supprimé", Toast.LENGTH_SHORT).show();
                        })
                        .setNegativeButton("NON", null).show();
            }
        });

        // 3. Les Listeners pour les Filtres (Quand on change le Spinner, on met à jour)
        AdapterView.OnItemSelectedListener filterListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                appliquerFiltres();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        };
        spinnerPeriode.setOnItemSelectedListener(filterListener);
        spinnerCategorie.setOnItemSelectedListener(filterListener);

        // 4. Bouton Ajouter
        FloatingActionButton fab = root.findViewById(R.id.fabAddExpense);
        fab.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), AddExpenseActivity.class);
            startActivity(intent);
        });

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        appliquerFiltres(); // Charge la liste quand on arrive sur la page
    }

    // Le moteur de filtrage complet
    private void appliquerFiltres() {
        if (getContext() == null) return;

        int indexCat = spinnerCategorie.getSelectedItemPosition();
        // Index 0 = "Toutes les catégories". Index 1 = Alimentation (Id=1 en base), etc. Exactement ce qu'il faut !

        int indexPeriode = spinnerPeriode.getSelectedItemPosition();
        long dateDebut = 0;
        long dateFin = Long.MAX_VALUE; // Jusqu'à la fin des temps par défaut

        Calendar cal = Calendar.getInstance();

        switch (indexPeriode) {
            case 1: // Aujourd'hui
                cal.set(Calendar.HOUR_OF_DAY, 0); cal.set(Calendar.MINUTE, 0); cal.set(Calendar.SECOND, 0);
                dateDebut = cal.getTimeInMillis();
                cal.set(Calendar.HOUR_OF_DAY, 23); cal.set(Calendar.MINUTE, 59); cal.set(Calendar.SECOND, 59);
                dateFin = cal.getTimeInMillis();
                break;
            case 2: // Cette semaine
                cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek()); // Lundi
                cal.set(Calendar.HOUR_OF_DAY, 0); cal.set(Calendar.MINUTE, 0); cal.set(Calendar.SECOND, 0);
                dateDebut = cal.getTimeInMillis();
                cal.add(Calendar.DAY_OF_WEEK, 6); // Dimanche
                cal.set(Calendar.HOUR_OF_DAY, 23); cal.set(Calendar.MINUTE, 59); cal.set(Calendar.SECOND, 59);
                dateFin = cal.getTimeInMillis();
                break;
            case 3: // Ce mois
                cal.set(Calendar.DAY_OF_MONTH, 1); // 1er du mois
                cal.set(Calendar.HOUR_OF_DAY, 0); cal.set(Calendar.MINUTE, 0); cal.set(Calendar.SECOND, 0);
                dateDebut = cal.getTimeInMillis();
                cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH)); // Dernier jour
                cal.set(Calendar.HOUR_OF_DAY, 23); cal.set(Calendar.MINUTE, 59); cal.set(Calendar.SECOND, 59);
                dateFin = cal.getTimeInMillis();
                break;
        }

        try {
            // L'appel à la super-requête
            AppDatabase db = AppDatabase.getInstance(getContext());
            List<Depense> depensesFiltrees = db.appDao().getDepensesFiltrees(indexCat, dateDebut, dateFin);

            if (adapter != null) {
                adapter.setDepenses(depensesFiltrees);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}