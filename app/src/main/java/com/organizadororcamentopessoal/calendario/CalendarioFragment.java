package com.organizadororcamentopessoal.calendario;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.organizadororcamentopessoal.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CalendarioFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CalendarioFragment extends Fragment {
    private Button[] monthButtons;
    public CalendarioFragment() {
    }

    public static CalendarioFragment newInstance() {
        CalendarioFragment fragment = new CalendarioFragment();
//        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
//        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
//        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_calendario, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        monthButtons = new Button[] {
                view.findViewById(R.id.buttonJan),
                view.findViewById(R.id.buttonFev),
                view.findViewById(R.id.buttonMar),
                view.findViewById(R.id.buttonAbr),
                view.findViewById(R.id.buttonMai),
                view.findViewById(R.id.buttonJun),
                view.findViewById(R.id.buttonJul),
                view.findViewById(R.id.buttonAgo),
                view.findViewById(R.id.buttonSet),
                view.findViewById(R.id.buttonOut),
                view.findViewById(R.id.buttonNov),
                view.findViewById(R.id.buttonDez)
        };

    }
}