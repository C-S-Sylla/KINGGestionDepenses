package sn.esmt.gestiondepenses.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import sn.esmt.gestiondepenses.R;
import sn.esmt.gestiondepenses.model.Depense;

public class DepenseAdapter extends RecyclerView.Adapter<DepenseAdapter.DepenseHolder> {

    private List<Depense> depenses = new ArrayList<>();
    private OnItemClickListener listener;
    private boolean showActions = true; // Gère l'affichage des boutons

    public interface OnItemClickListener {
        void onEditClick(Depense depense);
        void onDeleteClick(Depense depense);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void setDepenses(List<Depense> depenses) {
        this.depenses = depenses;
        notifyDataSetChanged();
    }

    public void setShowActions(boolean show) {
        this.showActions = show;
    }

    @NonNull
    @Override
    public DepenseHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_depense, parent, false);
        return new DepenseHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull DepenseHolder holder, int position) {
        Depense current = depenses.get(position);

        holder.txtMontant.setText("- " + current.montant + " FCFA");
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        holder.txtDate.setText(sdf.format(new Date(current.date)));

        if (current.rubrique != null && !current.rubrique.trim().isEmpty()) {
            holder.txtCategorie.setText(current.rubrique.substring(0, 1).toUpperCase() + current.rubrique.substring(1));
        } else {
            String nomCategorie = getNomCategorieTemporaire(current.categorieId);
            holder.txtCategorie.setText(nomCategorie);
        }

        if (showActions) {
            holder.layoutActions.setVisibility(View.VISIBLE);
        } else {
            holder.layoutActions.setVisibility(View.GONE);
        }

        holder.btnEdit.setOnClickListener(v -> {
            if (listener != null) listener.onEditClick(current);
        });

        holder.btnDelete.setOnClickListener(v -> {
            if (listener != null) listener.onDeleteClick(current);
        });
    }

    @Override
    public int getItemCount() {
        return depenses.size();
    }

    private String getNomCategorieTemporaire(int id) {
        switch (id) {
            case 1: return "Alimentation";
            case 2: return "Transport";
            case 3: return "Logement";
            case 4: return "Santé";
            case 5: return "Éducation";
            case 6: return "Loisirs";
            case 7: return "Habillement";
            default: return "Autre";
        }
    }

    class DepenseHolder extends RecyclerView.ViewHolder {
        TextView txtCategorie, txtDate, txtMontant;
        ImageView btnEdit, btnDelete;
        View layoutActions;

        public DepenseHolder(View itemView) {
            super(itemView);
            txtCategorie = itemView.findViewById(R.id.txtCategorie);
            txtDate = itemView.findViewById(R.id.txtDate);
            txtMontant = itemView.findViewById(R.id.txtMontant);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);

            layoutActions = itemView.findViewById(R.id.layoutActions);
        }
    }
}