package sn.esmt.gestiondepenses.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.google.android.material.button.MaterialButtonToggleGroup;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import sn.esmt.gestiondepenses.R;
import sn.esmt.gestiondepenses.adapter.DepenseAdapter;
import sn.esmt.gestiondepenses.database.AppDatabase;
import sn.esmt.gestiondepenses.model.Budget;
import sn.esmt.gestiondepenses.model.Depense;

public class DashboardFragment extends Fragment {

    private TextView txtSoldeValeur, txtBonjour;
    private LinearLayout layoutAlertes;
    private RecyclerView recyclerTop5;
    private PieChart pieChart;
    private DepenseAdapter adapterTop5;
    private int userId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_dashboard, container, false);

        txtSoldeValeur = root.findViewById(R.id.txtSoldeValeur);
        txtBonjour = root.findViewById(R.id.txtBonjour);
        layoutAlertes = root.findViewById(R.id.layoutAlertes);
        recyclerTop5 = root.findViewById(R.id.recyclerTop5Transactions);
        pieChart = root.findViewById(R.id.pieChartRepartition);
        MaterialButtonToggleGroup toggle = root.findViewById(R.id.toggleView);

        SharedPreferences prefs = getContext().getSharedPreferences("MesParametres", Context.MODE_PRIVATE);
        userId = prefs.getInt("ID_UTILISATEUR", -1);

        recyclerTop5.setLayoutManager(new LinearLayoutManager(getContext()));
        adapterTop5 = new DepenseAdapter();
        adapterTop5.setShowActions(false);
        recyclerTop5.setAdapter(adapterTop5);

        toggle.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (isChecked) {
                if (checkedId == R.id.btnShowList) {
                    recyclerTop5.setVisibility(View.VISIBLE);
                    pieChart.setVisibility(View.GONE);
                } else {
                    recyclerTop5.setVisibility(View.GONE);
                    pieChart.setVisibility(View.VISIBLE);
                    genererDiagramme();
                }
            }
        });

        actualiserDonnees();
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        actualiserDonnees();
    }

    private void actualiserDonnees() {
        afficherNomUtilisateur();
        calculerSoldeDuMois();
        chargerTop5Transactions();
        verifierAlertesBudgets();
        genererDiagramme();
    }

    // --- CORRECTION COULEUR SOLDE (Vert si positif, Rose/Rouge si négatif) ---
    private void calculerSoldeDuMois() {
        if (getContext() == null) return;
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, 1); long debut = cal.getTimeInMillis();
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH)); long fin = cal.getTimeInMillis();

        try {
            AppDatabase db = AppDatabase.getInstance(getContext());
            Double d = db.appDao().getTotalDepensesPeriode(userId, debut, fin);
            Double r = db.appDao().getTotalRevenusPeriode(userId, debut, fin);
            double solde = (r != null ? r : 0) - (d != null ? d : 0);

            txtSoldeValeur.setText(solde + " FCFA");

            // LOGIQUE COULEUR :
            if (solde < 0) {
                txtSoldeValeur.setTextColor(getResources().getColor(R.color.error_red)); // Rose/Rouge
            } else {
                txtSoldeValeur.setTextColor(getResources().getColor(R.color.success_green)); // Vert
            }

        } catch (Exception e) { e.printStackTrace(); }
    }

    // --- CORRECTION LÉGENDE DIAGRAMME (S'adapte au mode sombre/clair) ---
    private void genererDiagramme() {
        if (getContext() == null) return;

        // Détecter si on est en mode sombre
        int nightModeFlags = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        int dynamicTextColor = (nightModeFlags == Configuration.UI_MODE_NIGHT_YES) ? Color.WHITE : Color.BLACK;

        List<PieEntry> entries = new ArrayList<>();
        AppDatabase db = AppDatabase.getInstance(getContext());
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, 1); long debut = cal.getTimeInMillis();
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH)); long fin = cal.getTimeInMillis();

        for (int i = 1; i <= 8; i++) {
            Double total = db.appDao().getSommeDepensesParCategorie(userId, i, debut, fin);
            if (total != null && total > 0) {
                entries.add(new PieEntry(total.floatValue(), getNomCat(i)));
            }
        }

        if (entries.isEmpty()) {
            pieChart.setNoDataText("Aucune donnée à analyser");
            pieChart.setNoDataTextColor(dynamicTextColor);
            return;
        }

        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setColors(new int[]{Color.parseColor("#D4AF37"), Color.parseColor("#8A9A5B"), Color.LTGRAY, Color.CYAN});

        // Couleur des chiffres sur les parts du gâteau
        dataSet.setValueTextColor(Color.BLACK);
        dataSet.setValueTextSize(12f);

        PieData data = new PieData(dataSet);
        pieChart.setData(data);

        // CONFIGURATION VISUELLE DU GRAPHIQUE
        pieChart.getDescription().setEnabled(false);
        pieChart.setHoleColor(Color.TRANSPARENT);
        pieChart.setEntryLabelColor(dynamicTextColor); // Couleur des noms sur les parts

        // CONFIGURATION DE LA LÉGENDE (Celle du bas)
        Legend l = pieChart.getLegend();
        l.setTextColor(dynamicTextColor); // <-- ICI : La légende devient blanche en mode nuit !
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);

        pieChart.animateY(1000);
        pieChart.invalidate();
    }

    // --- LE RESTE EST INCHANGÉ ---
    private void verifierAlertesBudgets() {
        layoutAlertes.removeAllViews();
        layoutAlertes.setVisibility(View.GONE);
        Calendar cal = Calendar.getInstance();
        int m = cal.get(Calendar.MONTH) + 1; int a = cal.get(Calendar.YEAR);
        cal.set(Calendar.DAY_OF_MONTH, 1); long debut = cal.getTimeInMillis();
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH)); long fin = cal.getTimeInMillis();
        try {
            AppDatabase db = AppDatabase.getInstance(getContext());
            List<Budget> budgets = db.appDao().getBudgetsDuMois(userId, m, a);
            for (Budget b : budgets) {
                Double depense = (b.categorieId == 0) ? db.appDao().getTotalDepensesPeriode(userId, debut, fin) : db.appDao().getSommeDepensesParCategorie(userId, b.categorieId, debut, fin);
                if (depense != null && depense > b.montantPlafond) {
                    layoutAlertes.setVisibility(View.VISIBLE);
                    ajouterBandeauRouge("Alerte: Budget dépassé en " + getNomCat(b.categorieId));
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void chargerTop5Transactions() {
        try {
            AppDatabase db = AppDatabase.getInstance(getContext());
            List<Depense> list = db.appDao().getCinqDernieresDepenses(userId);
            adapterTop5.setDepenses(list);
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void afficherNomUtilisateur() {
        SharedPreferences prefs = getContext().getSharedPreferences("MesParametres", Context.MODE_PRIVATE);
        String nom = prefs.getString("NOM_UTILISATEUR", "");
        txtBonjour.setText(nom.isEmpty() ? "Bonjour !" : "Bonjour, " + nom + " !");
    }

    private void ajouterBandeauRouge(String message) {
        TextView tv = new TextView(getContext());
        tv.setText(message);
        tv.setTextColor(Color.WHITE);
        tv.setBackgroundColor(Color.parseColor("#CF6679"));
        tv.setPadding(30, 20, 30, 20);
        layoutAlertes.addView(tv);
    }

    private String getNomCat(int id) {
        if (id == 0) return "Global";
        String[] cats = getResources().getStringArray(R.array.categories_budget);
        return cats[id];
    }
}