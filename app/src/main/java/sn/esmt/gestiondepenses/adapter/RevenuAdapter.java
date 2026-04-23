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
import sn.esmt.gestiondepenses.model.Revenu;

// Adapter = pont entre les données (List<Revenu>) et la RecyclerView qui les affiche
public class RevenuAdapter extends RecyclerView.Adapter<RevenuAdapter.RevenuHolder> {

    // Liste des revenus à afficher (vide au démarrage)
    private List<Revenu> revenus = new ArrayList<>();

    // Listener pour remonter les clics au Fragment
    private OnItemClickListener listener;

    // Permet de cacher les boutons Modifier/Supprimer (utile pour un résumé Dashboard)
    private boolean showActions = true;

    // Interface implémentée par le Fragment pour réagir aux clics
    public interface OnItemClickListener {
        void onEditClick(Revenu revenu);
        void onDeleteClick(Revenu revenu);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    // Appelée par le Fragment pour rafraîchir l'affichage avec une nouvelle liste
    public void setRevenus(List<Revenu> revenus) {
        this.revenus = revenus;
        notifyDataSetChanged(); // Demande à la RecyclerView de redessiner
    }

    public void setShowActions(boolean show) {
        this.showActions = show;
    }

    // Crée une nouvelle "cellule" en gonflant le layout item_revenu.xml
    @NonNull
    @Override
    public RevenuHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_revenu, parent, false);
        return new RevenuHolder(itemView);
    }

    // Remplit une cellule avec les données du revenu à la position donnée
    @Override
    public void onBindViewHolder(@NonNull RevenuHolder holder, int position) {
        Revenu current = revenus.get(position);

        // Le "+" devant le montant signale visuellement une entrée d'argent
        holder.txtMontant.setText("+ " + current.montant + " FCFA");

        // Format date lisible (JJ/MM/AAAA) à partir du timestamp
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        holder.txtDate.setText(sdf.format(new Date(current.date)));

        // La source est déjà une String, pas besoin de mapping
        holder.txtSource.setText(current.source);

        // Affiche ou cache les boutons d'action
        holder.layoutActions.setVisibility(showActions ? View.VISIBLE : View.GONE);

        // Branche les clics au listener (qui sera défini par le Fragment)
        holder.btnEdit.setOnClickListener(v -> {
            if (listener != null) listener.onEditClick(current);
        });

        holder.btnDelete.setOnClickListener(v -> {
            if (listener != null) listener.onDeleteClick(current);
        });
    }

    // Combien de cellules faut-il afficher ?
    @Override
    public int getItemCount() {
        return revenus.size();
    }

    // Le ViewHolder garde des références aux Views d'une cellule (perfo : pas de findViewById répété)
    class RevenuHolder extends RecyclerView.ViewHolder {
        TextView txtSource, txtDate, txtMontant;
        ImageView btnEdit, btnDelete;
        View layoutActions;

        public RevenuHolder(View itemView) {
            super(itemView);
            txtSource = itemView.findViewById(R.id.txtSource);
            txtDate = itemView.findViewById(R.id.txtDate);
            txtMontant = itemView.findViewById(R.id.txtMontant);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            layoutActions = itemView.findViewById(R.id.layoutActions);
        }
    }
}