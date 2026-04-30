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
import sn.esmt.gestiondepenses.adapter.RevenuAdapter;
import sn.esmt.gestiondepenses.database.AppDatabase;
import sn.esmt.gestiondepenses.model.Revenu;

public class RevenusFragment extends Fragment {

    private RecyclerView recyclerView;
    private RevenuAdapter adapter;
    private Spinner spinnerPeriode, spinnerSource;
    private View layoutEmpty;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Gonfle le layout XML en View Java
        View root = inflater.inflate(R.layout.fragment_revenus, container, false);
        layoutEmpty = root.findViewById(R.id.layoutEmpty);

        // 1. CONFIGURATION DE LA RECYCLERVIEW
        recyclerView = root.findViewById(R.id.recyclerRevenus);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new RevenuAdapter();
        recyclerView.setAdapter(adapter);

        // Récupération des deux Spinners de filtres
        spinnerPeriode = root.findViewById(R.id.spinnerFiltrePeriode);
        spinnerSource = root.findViewById(R.id.spinnerFiltreSource);

        // 2. GESTION DES CLICS SUR LES BOUTONS DE CHAQUE LIGNE
        adapter.setOnItemClickListener(new RevenuAdapter.OnItemClickListener() {
            @Override
            public void onEditClick(Revenu revenu) {
                // Lance le formulaire en mode MODIFICATION
                Intent intent = new Intent(getActivity(), AddIncomeActivity.class);
                // On passe l'id en "bagage" pour que l'activity sache quoi modifier
                intent.putExtra("ID_REVENU_A_MODIFIER", revenu.id);
                startActivity(intent);
            }

            @Override
            public void onDeleteClick(Revenu revenu) {
                // Confirmation avant suppression définitive
                new AlertDialog.Builder(getContext())
                        .setTitle("Suppression")
                        .setMessage("Supprimer ce revenu ?")
                        .setPositiveButton("OUI", (dialog, which) -> {
                            AppDatabase.getInstance(getContext()).appDao().deleteRevenu(revenu);
                            appliquerFiltres(); // Rafraîchit la liste
                            Toast.makeText(getContext(), "Supprimé", Toast.LENGTH_SHORT).show();
                        })
                        .setNegativeButton("NON", null)
                        .show();
            }
        });

        // 3. LISTENER POUR LES FILTRES — déclenché à chaque changement de spinner
        AdapterView.OnItemSelectedListener filterListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                appliquerFiltres();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        };
        spinnerPeriode.setOnItemSelectedListener(filterListener);
        spinnerSource.setOnItemSelectedListener(filterListener);

        // 4. BOUTON FAB : lance le formulaire en mode AJOUT
        FloatingActionButton fab = root.findViewById(R.id.fabAddIncome);
        fab.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), AddIncomeActivity.class);
            startActivity(intent);
        });

        return root;
    }

    // onResume : appelé chaque fois qu'on revient sur la page (ex: après un ajout)
    @Override
    public void onResume() {
        super.onResume();
        appliquerFiltres();
    }

    // Cœur du filtrage : lit les spinners, calcule les dates, interroge la BDD
    private void appliquerFiltres() {
        if (getContext() == null) return;

        SharedPreferences prefs = getContext().getSharedPreferences("MesParametres", Context.MODE_PRIVATE);
        int userId = prefs.getInt("ID_UTILISATEUR", -1);

        int indexSource = spinnerSource.getSelectedItemPosition();
        int indexPeriode = spinnerPeriode.getSelectedItemPosition();
        String sourceFiltre = (indexSource == 0) ? "" : spinnerSource.getSelectedItem().toString();

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
            List<Revenu> revenusFiltres = db.appDao().getRevenusFiltres(userId, sourceFiltre, dateDebut, dateFin);

            if (adapter != null) {
                adapter.setRevenus(revenusFiltres);

                if (revenusFiltres.isEmpty()) {
                    layoutEmpty.setVisibility(View.VISIBLE);
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