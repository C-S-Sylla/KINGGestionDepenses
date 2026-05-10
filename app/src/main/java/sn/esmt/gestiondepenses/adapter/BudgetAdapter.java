package sn.esmt.gestiondepenses.adapter;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;
import sn.esmt.gestiondepenses.R;
import sn.esmt.gestiondepenses.model.Budget;


public class BudgetAdapter extends RecyclerView.Adapter<BudgetAdapter.BudgetHolder> {

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
    private OnBudgetClickListener listener;

    public interface OnBudgetClickListener {
        void onDeleteClick(Budget budget);
    }

    public void setOnBudgetClickListener(OnBudgetClickListener l) {
        this.listener = l;
    }

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

        int pourcentage = (int) ((consomme / plafond) * 100);

        holder.txtNom.setText(model.nomCategorie);
        holder.txtPourcentage.setText(pourcentage + "%");
        holder.txtConsomme.setText((int)consomme + " / " + (int)plafond + " F");

        double reste = plafond - consomme;
        holder.txtReste.setText("Dispo : " + (int)(reste < 0 ? 0 : reste) + " F");

        holder.progress.setProgress(Math.min(pourcentage, 100));

        int couleur;
        if (pourcentage < 70) {
            couleur = Color.parseColor("#4CAF50");
        } else if (pourcentage <= 90) {
            couleur = Color.parseColor("#FFA500");
        } else {
            couleur = Color.RED;
        }

        holder.progress.setProgressTintList(ColorStateList.valueOf(couleur));

        if (pourcentage > 100) {
            holder.txtAlerte.setVisibility(View.VISIBLE);
        } else {
            holder.txtAlerte.setVisibility(View.GONE);
        }

        holder.btnDelete.setOnClickListener(v -> {
            if (listener != null && model.budget != null) {
                listener.onDeleteClick(model.budget);
            }
        });
    }

    @Override
    public int getItemCount() { return items.size(); }

    class BudgetHolder extends RecyclerView.ViewHolder {
        TextView txtNom, txtPourcentage, txtConsomme, txtReste, txtAlerte;
        ProgressBar progress;
        ImageView btnDelete;

        public BudgetHolder(@NonNull View itemView) {
            super(itemView);
            txtNom = itemView.findViewById(R.id.txtNomCategorie);
            txtPourcentage = itemView.findViewById(R.id.txtPourcentage);
            txtConsomme = itemView.findViewById(R.id.txtConsomme);
            txtReste = itemView.findViewById(R.id.txtReste);
            txtAlerte = itemView.findViewById(R.id.txtAlerte);
            progress = itemView.findViewById(R.id.progressBudget);
            btnDelete = itemView.findViewById(R.id.btnDeleteBudget);
        }
    }
}