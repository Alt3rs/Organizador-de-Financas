package com.organizadororcamentopessoal.adicionar_movimentacao;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.organizadororcamentopessoal.R;
import com.organizadororcamentopessoal.entities.Movimentacao;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class MovimentacaoAdapter extends RecyclerView.Adapter<MovimentacaoAdapter.ViewHolder> {
    private List<Movimentacao> dataSet;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView dataTextView, valorTextView, descricaoTextView;

        public ViewHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View
            dataTextView = view.findViewById(R.id.dataTextView);
            valorTextView = view.findViewById(R.id.valorTextView);
            descricaoTextView = view.findViewById(R.id.descricaoTextView);
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
    }


    public MovimentacaoAdapter(List<Movimentacao> dataSet) {
        this.dataSet = dataSet;
    }

    public void setDataSet(List<Movimentacao> dataSet) {
        this.dataSet = dataSet;
    }
    public List<Movimentacao> getDataSet() {
        return dataSet;
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
        //SimpleDateFormat hhmmFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        SimpleDateFormat fullDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        Movimentacao atual = dataSet.get(position);
        viewHolder.getDataTextView().setText(fullDateFormat.format(atual.getDataMovimentacao()));
        viewHolder.getDescricaoTextView().setText(atual.getDescricao());
        TextView valorTextView = viewHolder.getValorTextView();
        if(atual.getValor() >= 0) {
            valorTextView.setTextColor(valorTextView.getResources().getColor(R.color.verde_recebimento, valorTextView.getContext().getTheme()));
            valorTextView.setText(String.format("+R$ %.2f", Math.abs(atual.getValor())));
        } else {
            valorTextView.setTextColor(valorTextView.getResources().getColor(R.color.vermelho_gasto, valorTextView.getContext().getTheme()));
            valorTextView.setText(String.format("-R$ %.2f", Math.abs(atual.getValor())));
        }
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }
}