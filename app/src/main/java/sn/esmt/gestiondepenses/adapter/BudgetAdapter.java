package sn.esmt.gestiondepenses.adapter;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;
import sn.esmt.gestiondepenses.R;
import sn.esmt.gestiondepenses.model.Budget;

public class BudgetAdapter extends RecyclerView.Adapter<BudgetAdapter.BudgetHolder> {

    // On crée une petite classe interne pour transporter les données calculées
    public static class BudgetUIModel {
        public Budget budget;
        public double depenseActuelle;
        public String nomCategorie;

        public BudgetUIModel(Budget budget, double depenseActuelle, String nomCategorie) {
            this.budget = budget;
            this.depenseActuelle = depenseActuelle;
            this.nomCategorie = nomCategorie;
        }
    }

    private List<BudgetUIModel> items = new ArrayList<>();

    public void setData(List<BudgetUIModel> newItems) {
        this.items = newItems;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public BudgetHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_budget, parent, false);
        return new BudgetHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull BudgetHolder holder, int position) {
        BudgetUIModel model = items.get(position);
        double plafond = model.budget.montantPlafond;
        double consomme = model.depenseActuelle;

        // 1. Calcul du pourcentage (Page 8 du CDC)
        int pourcentage = (int) ((consomme / plafond) * 100);

        holder.txtNom.setText(model.nomCategorie);
        holder.txtPourcentage.setText(pourcentage + "%");
        holder.txtConsomme.setText((int)consomme + " / " + (int)plafond + " F");

        double reste = plafond - consomme;
        holder.txtReste.setText("Dispo : " + (int)(reste < 0 ? 0 : reste) + " F");

        // 2. Mise à jour de la barre
        holder.progress.setProgress(Math.min(pourcentage, 100));

        // 3. LOGIQUE COULEUR (Page 4 du CDC)
        // Vert < 70% | Orange 70-90% | Rouge > 90%
        int couleur;
        if (pourcentage < 70) {
            couleur = Color.parseColor("#4CAF50");
        } else if (pourcentage <= 90) {
            couleur = Color.parseColor("#FFA500");
        } else {
            couleur = Color.RED;
        }

        holder.progress.setProgressTintList(ColorStateList.valueOf(couleur));

        // 4. Afficher l'alerte si dépassé
        if (pourcentage > 100) {
            holder.txtAlerte.setVisibility(View.VISIBLE);
        } else {
            holder.txtAlerte.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() { return items.size(); }

    class BudgetHolder extends RecyclerView.ViewHolder {
        TextView txtNom, txtPourcentage, txtConsomme, txtReste, txtAlerte;
        ProgressBar progress;

        public BudgetHolder(@NonNull View itemView) {
            super(itemView);
            txtNom = itemView.findViewById(R.id.txtNomCategorie);
            txtPourcentage = itemView.findViewById(R.id.txtPourcentage);
            txtConsomme = itemView.findViewById(R.id.txtConsomme);
            txtReste = itemView.findViewById(R.id.txtReste);
            txtAlerte = itemView.findViewById(R.id.txtAlerte);
            progress = itemView.findViewById(R.id.progressBudget);
        }
    }
}