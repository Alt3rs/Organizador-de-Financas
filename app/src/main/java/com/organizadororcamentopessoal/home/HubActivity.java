package com.organizadororcamentopessoal.home;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;

import com.google.android.material.navigation.NavigationView;
import com.organizadororcamentopessoal.R;
import com.organizadororcamentopessoal.datasource.DatabaseContract;

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

        navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);

        navController = navHostFragment.getNavController();
        navController.setGraph(R.navigation.nav_graph, args);

        AppBarConfiguration appBarConfiguration =
                new AppBarConfiguration.Builder(navController.getGraph())
                        //.setOpenableLayout(drawerLayout)
                        .build();
        setSupportActionBar(toolbar);
        //NavigationView navView = findViewById(R.id.nav_view);
        NavigationUI.setupWithNavController(toolbar, navController, appBarConfiguration);
        //NavigationUI.setupWithNavController(navView, navController);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.activity_main_appbar_actions, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
//        if(item.getItemId() == R.id.action_share) {
//            //Compartilha o historico em formato JSON
//            try(CorridaRepository repository = new CorridaRepository(getApplicationContext())) {
//                String export = repository.listar(false).toString();
//                Intent sendIntent = new Intent();
//                sendIntent.setAction(Intent.ACTION_SEND);
//                sendIntent.putExtra(Intent.EXTRA_TEXT, export);
//                sendIntent.setType("text/plain"); //Especifica o formato de arquivo
//                Intent shareIntent = Intent.createChooser(sendIntent, null); //O usuário escolhe o aplicativo consumidor
//                startActivity(shareIntent);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            return true;
//        }
        return NavigationUI.onNavDestinationSelected(item, navController) ||
                super.onOptionsItemSelected(item);
    }
}