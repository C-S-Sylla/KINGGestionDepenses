package sn.esmt.gestiondepenses.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
    private View layoutEmpty;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_depenses, container, false);
        layoutEmpty = root.findViewById(R.id.layoutEmpty);

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
        appliquerFiltres();
    }

    // Le moteur de filtrage complet
    private void appliquerFiltres() {
        if (getContext() == null) return;

        SharedPreferences prefs = getContext().getSharedPreferences("MesParametres", Context.MODE_PRIVATE);
        int userId = prefs.getInt("ID_UTILISATEUR", -1);

        int indexCat = spinnerCategorie.getSelectedItemPosition();
        int indexPeriode = spinnerPeriode.getSelectedItemPosition();

        long dateDebut = 0;
        long dateFin = Long.MAX_VALUE;
        Calendar cal = Calendar.getInstance();

        switch (indexPeriode) {
            case 1: // Aujourd'hui
                cal.set(Calendar.HOUR_OF_DAY, 0); cal.set(Calendar.MINUTE, 0); cal.set(Calendar.SECOND, 0);
                dateDebut = cal.getTimeInMillis();
                cal.set(Calendar.HOUR_OF_DAY, 23); cal.set(Calendar.MINUTE, 59); cal.set(Calendar.SECOND, 59);
                dateFin = cal.getTimeInMillis();
                break;
            case 2: // Cette semaine
                cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek());
                cal.set(Calendar.HOUR_OF_DAY, 0); cal.set(Calendar.MINUTE, 0);
                dateDebut = cal.getTimeInMillis();
                cal.add(Calendar.DAY_OF_WEEK, 6);
                cal.set(Calendar.HOUR_OF_DAY, 23); cal.set(Calendar.MINUTE, 59);
                dateFin = cal.getTimeInMillis();
                break;
            case 3: // Ce mois
                cal.set(Calendar.DAY_OF_MONTH, 1);
                cal.set(Calendar.HOUR_OF_DAY, 0);
                dateDebut = cal.getTimeInMillis();
                cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
                cal.set(Calendar.HOUR_OF_DAY, 23);
                dateFin = cal.getTimeInMillis();
                break;
        }

        try {
            AppDatabase db = AppDatabase.getInstance(getContext());
            List<Depense> depensesFiltrees = db.appDao().getDepensesFiltrees(userId, indexCat, dateDebut, dateFin);

            if (adapter != null) {
                adapter.setDepenses(depensesFiltrees);

                if (depensesFiltrees.isEmpty()) {
                    layoutEmpty.setVisibility(View.VISIBLE); // Affiche l'image d'absence de données
                    recyclerView.setVisibility(View.GONE);
                } else {
                    layoutEmpty.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}