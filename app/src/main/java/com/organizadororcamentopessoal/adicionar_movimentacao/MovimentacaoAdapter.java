package com.organizadororcamentopessoal.adicionar_movimentacao;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.selection.ItemDetailsLookup;
import androidx.recyclerview.selection.SelectionTracker;
import androidx.recyclerview.widget.RecyclerView;

import com.organizadororcamentopessoal.R;
import com.organizadororcamentopessoal.entities.Movimentacao;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class MovimentacaoAdapter extends RecyclerView.Adapter<MovimentacaoAdapter.ViewHolder> {
    private List<Movimentacao> dataSet;
    private SelectionTracker<Long> tracker;

    public MovimentacaoAdapter(List<Movimentacao> dataSet) {
        this.dataSet = dataSet;
        setHasStableIds(true);
    }

    public void setDataSet(List<Movimentacao> dataSet) {
        this.dataSet = dataSet;
    }
    public List<Movimentacao> getDataSet() {
        return dataSet;
    }

    public void setTracker(SelectionTracker<Long> tracker) {
        this.tracker = tracker;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.movimentacao_row_adapter, viewGroup, false);

        return new ViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        SimpleDateFormat fullDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        Movimentacao atual = dataSet.get(position);

        viewHolder.setActivated(tracker.isSelected((long)position));
        viewHolder.getDataTextView().setText(fullDateFormat.format(atual.getDataMovimentacao()));
        viewHolder.getDescricaoTextView().setText(atual.getDescricao());

        TextView valorTextView = viewHolder.getValorTextView();
        if(atual.getValor() >= 0) {
            valorTextView.setTextColor(valorTextView.getResources().getColor(R.color.verde_recebimento, valorTextView.getContext().getTheme()));
            valorTextView.setText(String.format(Locale.getDefault(),"+R$ %.2f", Math.abs(atual.getValor())));
        } else {
            valorTextView.setTextColor(valorTextView.getResources().getColor(R.color.vermelho_gasto, valorTextView.getContext().getTheme()));
            valorTextView.setText(String.format(Locale.getDefault(),"-R$ %.2f", Math.abs(atual.getValor())));
        }
    }
    @Override
    public int getItemCount() {
        return dataSet.size();
    }
    @Override
    public long getItemId(int position) {
        return position;
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TableLayout backgroundLayout;
        private final TextView dataTextView, valorTextView, descricaoTextView;

        public ViewHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View
            dataTextView = view.findViewById(R.id.dataTextView);
            valorTextView = view.findViewById(R.id.valorTextView);
            descricaoTextView = view.findViewById(R.id.descricaoTextView);
            backgroundLayout = view.findViewById(R.id.backgroundLayout);
        }
        public TextView getDataTextView() {
            return dataTextView;
        }
        public TextView getValorTextView() {
            return valorTextView;
        }
        public TextView getDescricaoTextView() {
            return descricaoTextView;
        }
        public void setActivated(boolean isActivated) {
            backgroundLayout.setActivated(isActivated);
        }

        public ItemDetailsLookup.ItemDetails<Long> getItemDetails() {
            return new ItemDetailsLookup.ItemDetails<Long>() {
                @Override
                public int getPosition() {
                    return getAdapterPosition();
                }
                @Nullable
                @Override
                public Long getSelectionKey() {
                    return ViewHolder.this.getItemId();
                }
            };
        }
    }

    public static class MovimentacaoDetailsLookUp  extends ItemDetailsLookup<Long> {

        private final RecyclerView recyclerView;
        public MovimentacaoDetailsLookUp(RecyclerView recyclerView) {
            this.recyclerView = recyclerView;
        }
        @Nullable
        @Override
        public ItemDetails<Long> getItemDetails(@NonNull MotionEvent e) {
            View view = recyclerView.findChildViewUnder(e.getX(), e.getY());
            if(view != null) {
                return ((MovimentacaoAdapter.ViewHolder)recyclerView.getChildViewHolder(view)).getItemDetails();
            }
            return null;
        }
        public RecyclerView getRecyclerView() {
            return recyclerView;
        }
    }
}

