package ru.astronomrus.paleomuseum;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
public static  DrawerLayout mdrawer;
    static Context ctx;
    static Paleotag [] tag_geochrones;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String [] geochronestag = getString(R.string.paleochrono).split("#");
        tag_geochrones = new Paleotag[geochronestag.length/2];


        ///////////////// test DB
        db_BookMark db = new db_BookMark(this);

        /**
         * CRUD Operations
         * */
        // add Books
        db.addBook(new PaleoItem("Android Application Development Cookbook", "Wei Meng Lee" , "13232323" , "der3ere" , "wse"));
        db.addBook(new PaleoItem("Android Programming: The Big Nerd Ranch Guide", "Bill Phillips and Brian Hardy" ,"23324","23324","23324"));
        db.addBook(new PaleoItem("Learn Android App Development", "Wallace Jackson" , "#$#%4" , "23eds34wsd" , "DFRT$"));

        // get all books
        List<PaleoItem> list = db.getAllBooks();

        // delete one book
        db.deleteBook(list.get(0));

        // get all books
        db.getAllBooks();
        //////////////////


        for( int i=0 , k =0;i<geochronestag.length;i+=2)
        {
            tag_geochrones[k] = new Paleotag();
            tag_geochrones[k].name = geochronestag[i];
            tag_geochrones[k].tag = geochronestag[i+1];
            k++;
        }

        ctx = this;
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
         mdrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        setSupportActionBar(toolbar);
        if (savedInstanceState == null) {
            Fragment newFragment = new GalleryFragment();
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.add(R.id.content_frame, newFragment);
            ft.addToBackStack(null);
            ft.commit();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        Log.d("BACK" ,"");
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        Fragment fragment = null;
        Bundle bundle = new Bundle();
        if (id == R.id.home)
        {
            bundle.putString(GalleryFragment.BKEY_URL, "https://www.ammonit.ru/newfoto/");
            fragment = new GalleryFragment();
            fragment.setArguments(bundle);
        } else if (id == R.id.photo_by_popular)
        {
            bundle.putString(GalleryFragment.BKEY_URL, "https://www.ammonit.ru/popfoto/");
            fragment = new GalleryFragment();
            fragment.setArguments(bundle);
        } else if (id == R.id.about) {
            fragment = new AboutFragment();
        } else if (id == R.id.articles) {
        bundle.putString("url", "https://www.ammonit.ru/paleotexts.htm");
            fragment = new WebViewFragment();
            fragment.setArguments(bundle);
        }  else if (id == R.id.paleonews) {
            bundle.putString("url", "https://www.ammonit.ru/news.htm");
            fragment = new WebViewFragment();
            fragment.setArguments(bundle);
        }
        else if (id == R.id.photo_by_geochrones) {

            show_list_geochrones();

        }
        if (fragment != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame, fragment);
            ft.commit();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

  void show_list_geochrones()
  {
      int req_pg  = 1;
      final Dialog dialog = new Dialog(MainActivity.ctx);
      dialog.setContentView(R.layout.dlg_list_geochrones);
      dialog.setTitle("Век / Ярус (Stage)");
      dialog.show();
      final TextView title = (TextView) dialog.findViewById(R.id.lgh_title);
      final GridView  listView  = (GridView) dialog.findViewById(R.id.lgh_grid_geochrones);

      String []geochrones = new String[tag_geochrones.length];
      for(int i=0;i<tag_geochrones.length;i++)
      {
          geochrones[i] = new String(tag_geochrones[i].name);
      }
      ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
              android.R.layout.simple_list_item_1, android.R.id.text1, geochrones );

      // Assign adapter to ListView
      listView.setAdapter(adapter);

      listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
          @Override
          public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

              Fragment fragment = null;
              Bundle bundle = new Bundle();
              bundle.putString(GalleryFragment.BKEY_URL, "https://ammonit.ru/geochrono/"+tag_geochrones[i].tag+"/popfotos/");
              fragment = new GalleryFragment();
              fragment.setArguments(bundle);
              if (fragment != null) {
                  FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                  ft.replace(R.id.content_frame, fragment);
                  ft.commit();
                  dialog.hide();
              }
          }
      });

      Button ok = (Button) dialog.findViewById(R.id.lgh_ok);
      Button cancel = (Button) dialog.findViewById(R.id.lgh_cancel);
      ok.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            //  pnum = seek.getProgress();
             // (new GalleryFragment.Imgitems_getter()).execute(url+pnum);
              dialog.hide();
          }
      });

      cancel.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
              dialog.hide();
          }
      });
  }

}
