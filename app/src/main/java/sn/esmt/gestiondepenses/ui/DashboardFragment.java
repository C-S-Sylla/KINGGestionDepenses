package sn.esmt.gestiondepenses.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Calendar;
import java.util.List;

import sn.esmt.gestiondepenses.R;
import sn.esmt.gestiondepenses.adapter.DepenseAdapter;
import sn.esmt.gestiondepenses.database.AppDatabase;
import sn.esmt.gestiondepenses.model.Depense;

public class DashboardFragment extends Fragment {

    private TextView txtSoldeValeur, txtBonjour;
    private RecyclerView recyclerTop5;
    private DepenseAdapter adapterTop5;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_dashboard, container, false);

        // 1. Lier les éléments visuels
        txtSoldeValeur = root.findViewById(R.id.txtSoldeValeur);
        txtBonjour = root.findViewById(R.id.txtBonjour); // Le fameux TextView !

        // 2. Afficher le nom de l'utilisateur
        afficherNomUtilisateur();

        // 3. Configurer la petite liste des 5 transactions
        recyclerTop5 = root.findViewById(R.id.recyclerTop5Transactions);
        recyclerTop5.setLayoutManager(new LinearLayoutManager(getContext()));
        adapterTop5 = new DepenseAdapter();
        adapterTop5.setShowActions(false);
        recyclerTop5.setAdapter(adapterTop5);


        // On masque les boutons modifier/supprimer pour cette petite liste de résumé
        adapterTop5.setOnItemClickListener(new DepenseAdapter.OnItemClickListener() {
            @Override public void onEditClick(Depense depense) {}
            @Override public void onDeleteClick(Depense depense) {}
        });

        calculerSoldeDuMois();
        chargerTop5Transactions();

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        afficherNomUtilisateur();
        calculerSoldeDuMois();
        chargerTop5Transactions();
    }

    private void afficherNomUtilisateur() {
        if (getContext() == null) return;

        // On va lire dans la mémoire du téléphone (créée par le menu latéral)
        SharedPreferences prefs = getContext().getSharedPreferences("MesParametres", Context.MODE_PRIVATE);
        String nomUtilisateur = prefs.getString("NOM_UTILISATEUR", "");

        if (nomUtilisateur.isEmpty()) {
            txtBonjour.setText("Bonjour !");
        } else {
            txtBonjour.setText("Bonjour, " + nomUtilisateur + " !");
        }
    }

    private void calculerSoldeDuMois() {
        if (getContext() == null) return;

        // 1. Récupérer le userId
        SharedPreferences prefs = getContext().getSharedPreferences("MesParametres", Context.MODE_PRIVATE);
        int userId = prefs.getInt("ID_UTILISATEUR", -1);

        // 2. Calculer les dates du mois (Code identique à avant)
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.HOUR_OF_DAY, 0); cal.set(Calendar.MINUTE, 0);
        long dateDebutMois = cal.getTimeInMillis();

        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        cal.set(Calendar.HOUR_OF_DAY, 23); cal.set(Calendar.MINUTE, 59);
        long dateFinMois = cal.getTimeInMillis();

        try {
            AppDatabase db = AppDatabase.getInstance(getContext());

            // 3. Total des dépenses de l'utilisateur sur le mois en cours
            Double totalDepensesBrut = db.appDao().getTotalDepensesPeriode(userId, dateDebutMois, dateFinMois);
            // SUM peut retourner null s'il n'y a aucune ligne → on remet à 0
            double totalDepenses = (totalDepensesBrut == null) ? 0.0 : totalDepensesBrut;

            // 4. Total des revenus de l'utilisateur sur le mois en cours
            Double totalRevenusBrut = db.appDao().getTotalRevenusPeriode(userId, dateDebutMois, dateFinMois);
            double totalRevenus = (totalRevenusBrut == null) ? 0.0 : totalRevenusBrut;

            // 5. Calcul du solde disponible (CDC §2.2.2)
            double solde = totalRevenus - totalDepenses;
            txtSoldeValeur.setText(solde + " FCFA");

            // 6. Indicateur visuel : ROUGE si négatif, VERT si positif (CDC §2.2.2)
            if (solde < 0) {
                txtSoldeValeur.setTextColor(getResources().getColor(R.color.error_red));
            } else {
                txtSoldeValeur.setTextColor(getResources().getColor(R.color.success_green));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void chargerTop5Transactions() {
        if (getContext() == null) return;

        // 1. Récupérer le userId
        SharedPreferences prefs = getContext().getSharedPreferences("MesParametres", Context.MODE_PRIVATE);
        int userId = prefs.getInt("ID_UTILISATEUR", -1);

        try {
            AppDatabase db = AppDatabase.getInstance(getContext());
            // 2. Demander uniquement les dépenses de cet utilisateur
            List<Depense> top5 = db.appDao().getCinqDernieresDepenses(userId);

            if (adapterTop5 != null) {
                adapterTop5.setDepenses(top5);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}