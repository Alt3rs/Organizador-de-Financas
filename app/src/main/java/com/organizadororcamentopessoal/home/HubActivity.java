package com.organizadororcamentopessoal.home;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;

import com.google.android.material.navigation.NavigationView;
import com.organizadororcamentopessoal.NavGraphDirections;
import com.organizadororcamentopessoal.R;
import com.organizadororcamentopessoal.adicionar_movimentacao.MovimentacaoDiariaFragmentDirections;
import com.organizadororcamentopessoal.datasource.DatabaseContract;

import com.organizadororcamentopessoal.home.HomeFragmentDirections;

public class HubActivity extends AppCompatActivity {
    private String username;
    private NavHostFragment navHostFragment;
    private NavController navController;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hub);

        Bundle args = getIntent().getExtras();
        if(args != null) {
            username = args.getString(DatabaseContract.UsuarioTable.USERNAME);
        }

        Toolbar toolbar = findViewById(R.id.main_toolbar);
        //DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        //NavigationView navView = findViewById(R.id.nav_view);
        navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);

        navController = navHostFragment.getNavController();
        navController.setGraph(R.navigation.nav_graph, args);
        AppBarConfiguration appBarConfiguration =
                new AppBarConfiguration.Builder(navController.getGraph())
                        //.setOpenableLayout(drawerLayout)
                        .build();

        setSupportActionBar(toolbar);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(toolbar, navController, appBarConfiguration);
        //NavigationUI.setupWithNavController(navView, navController);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.hub_toolbar_actions, menu);
        //return super.onCreateOptionsMenu(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.actionLimiteSettings) {
            navHostFragment.getNavController().navigate(
                    HomeFragmentDirections.actionGlobalLimitesConfigFragment(username));
            return true;
        }
        return NavigationUI.onNavDestinationSelected(item, navController) ||
                super.onOptionsItemSelected(item);
    }
}