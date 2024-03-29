package com.organizadororcamentopessoal.home;

import androidx.lifecycle.ViewModelProvider;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.widget.Toolbar;

import com.organizadororcamentopessoal.R;
import com.organizadororcamentopessoal.datasource.DatabaseContract;
import com.organizadororcamentopessoal.home.HomeFragmentDirections;
import com.organizadororcamentopessoal.relatorios.RelatoriosFragmentDirections;

public class HomeFragment extends Fragment {
    private String username;
    private Button resumoButton, calendarioButton, movimentacaoButton;

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        username = getArguments().getString(DatabaseContract.UsuarioTable.USERNAME);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        resumoButton = view.findViewById(R.id.resumoButton);
        calendarioButton = view.findViewById(R.id.calendarioButton);
        movimentacaoButton = view.findViewById(R.id.movimentacaoButton);

        getActivity().setTitle(username);
        movimentacaoButton.setOnClickListener((View button) -> {
            Navigation.findNavController(button)
                    .navigate(HomeFragmentDirections
                    .actionHomeFragmentToMovimentacaoDiaria(username));
        });

        resumoButton.setOnClickListener((View button) -> {
            Navigation.findNavController(button)
                    .navigate(HomeFragmentDirections
                    .actionHomeFragmentToRelatoriosFragment(username));
        });

        Toolbar toolbar = getActivity().findViewById(R.id.main_toolbar);
        toolbar.setBackgroundColor(getContext().getColor(R.color.amarelo));
        if (Build.VERSION.SDK_INT >= 21) {
            Window window = this.getActivity().getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(getContext().getColor(R.color.amarelo));
        }
    }
}