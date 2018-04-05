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
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    public static DrawerLayout mdrawer;
    static Context ctx;
    static Paleotag[] tag_geochrones;
    static Paleotag[] tag_places;
    static Paleotag[] tag_bioclass;
    Dialog dialog ;
    GalleryFragment galleryFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        tag_geochrones = convert_text_to_tag( getString(R.string.paleochrono));
        tag_places= convert_text_to_tag( getString(R.string.paleoplaces));
        tag_bioclass= convert_text_to_tag( getString(R.string.paleobioclasses));


        ctx = this;
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        mdrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
       try{
           setSupportActionBar(toolbar);
       }
       catch(Exception e)
       {

       }
        if (savedInstanceState == null) {

            galleryFragment = new GalleryFragment();;
            Fragment newFragment= galleryFragment;
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
        Log.d("BACK", "");
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
        if (id == R.id.home) {
            bundle.putString(GalleryFragment.BKEY_URL, "https://www.ammonit.ru/newfoto/");
            galleryFragment = new GalleryFragment();;
            fragment = galleryFragment;
            fragment.setArguments(bundle);
        } else if (id == R.id.photo_by_popular) {
            bundle.putString(GalleryFragment.BKEY_URL, "https://www.ammonit.ru/popfoto/");
            galleryFragment = new GalleryFragment();;
            fragment = galleryFragment;
            fragment.setArguments(bundle);
        } else if (id == R.id.about) {
            fragment = new AboutFragment();
        } else if (id == R.id.articles) {
            bundle.putString("url", "https://www.ammonit.ru/paleotexts.htm");
            fragment = new WebViewFragment();
            fragment.setArguments(bundle);
        } else if (id == R.id.paleonews) {
            bundle.putString("url", "https://www.ammonit.ru/news.htm");
            fragment = new WebViewFragment();
            fragment.setArguments(bundle);
        }
        else if (id == R.id.bookmarks) {

            fragment = new BookmarksView();
        }
        else if (id == R.id.photo_by_geochrones) {

            show_list_geochrones();

        }
        else if (id == R.id.photo_by_place) {

            show_list_geoplaces();

        }
        else if (id == R.id.photo_by_bioclass) {

            show_list_bioclasses();

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


    void show_list_geoplaces() {
        int req_pg = 1;
           dialog = new Dialog(MainActivity.ctx);
        dialog.setContentView(R.layout.dlg_list_geochrones);
        dialog.setTitle(" Место");
        dialog.show();
        final TextView title = (TextView) dialog.findViewById(R.id.lgh_title);
        title.setText("Место");
        final GridView listView = (GridView) dialog.findViewById(R.id.lgh_grid_tags);

        String[] geoplaces = new String[tag_places.length];
        for (int i = 0; i < tag_places.length; i++) {
            geoplaces[i] = new String(tag_places[i].name);
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, geoplaces);

        // Assign adapter to ListView
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Fragment fragment = null;
                Bundle bundle = new Bundle();
                bundle.putString(GalleryFragment.BKEY_URL, "https://ammonit.ru/place/" + tag_places[i].tag + "/popfotos/");
                galleryFragment = new GalleryFragment();;
                fragment = galleryFragment;
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

    void show_list_geochrones() {
        int req_pg = 1;
           dialog = new Dialog(MainActivity.ctx);
        dialog.setContentView(R.layout.dlg_list_geochrones);
        dialog.setTitle("Век / Ярус (Stage)");
        dialog.show();
        final TextView title = (TextView) dialog.findViewById(R.id.lgh_title);
        title.setText("Век / Ярус (Stage)");
        final GridView listView = (GridView) dialog.findViewById(R.id.lgh_grid_tags);

        String[] geochrones = new String[tag_geochrones.length];
        for (int i = 0; i < tag_geochrones.length; i++) {
            geochrones[i] = new String(tag_geochrones[i].name);
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, geochrones);

        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Fragment fragment = null;
                Bundle bundle = new Bundle();
                bundle.putString(GalleryFragment.BKEY_URL, "https://ammonit.ru/geochrono/" + tag_geochrones[i].tag + "/popfotos/");
                galleryFragment = new GalleryFragment();;
                fragment = galleryFragment;
                fragment.setArguments(bundle);
                if (fragment != null) {
                    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                    ft.replace(R.id.content_frame, fragment);
                    ft.commit();
                    dialog.hide();
                }
            }
        });

    }


    void show_list_bioclasses() {
        int req_pg = 1;
           dialog = new Dialog(MainActivity.ctx);
        dialog.setContentView(R.layout.dlg_list_geochrones);
        dialog.setTitle("Типы окаменелостей");
        dialog.show();
        final TextView title = (TextView) dialog.findViewById(R.id.lgh_title);
        title.setText("Типы окаменелостей");
        final GridView listView = (GridView) dialog.findViewById(R.id.lgh_grid_tags);

        String[] bioclasses = new String[tag_bioclass.length];
        for (int i = 0; i < tag_bioclass.length; i++) {
            bioclasses[i] = new String(tag_bioclass[i].name);
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, bioclasses);

        // Assign adapter to ListView
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Fragment fragment = null;
                Bundle bundle = new Bundle();
                bundle.putString(GalleryFragment.BKEY_URL, "https://ammonit.ru/fossil/" + tag_bioclass[i].tag + "/popfotos/");
                galleryFragment = new GalleryFragment();;
                fragment = galleryFragment;
                fragment.setArguments(bundle);
                if (fragment != null) {
                    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                    ft.replace(R.id.content_frame, fragment);
                    ft.commit();
                    dialog.hide();
                }
            }
        });

    }



    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        int action = event.getAction();
        int keyCode = event.getKeyCode();
        switch (keyCode) {
            case KeyEvent.KEYCODE_VOLUME_UP:
                if (action == KeyEvent.ACTION_DOWN) {
                    Log.w("LightWriter", "I WORK ---  BRO.");
                    galleryFragment.next_page();
                }
                return true;
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                if (action == KeyEvent.ACTION_DOWN) {
                    Log.w("LightWriter", "I WORK ---  BRO.");
                    galleryFragment.prev_page();
                }
                return true;
            default:
                return super.dispatchKeyEvent(event);
        }
    }

    Paleotag [] convert_text_to_tag(String text )
    {
        String[]  tag = text.split("#");
        String []tmptosort = new String[tag.length/2];
        for (int i = 0, k = 0; i < tag.length; i += 2)
        {
            tmptosort[k] = new String(tag[i]+"#"+tag[i+1]);
            k++;
        }


        Arrays.sort(tmptosort);
        String sortres="";
        for (int i = 0; i < tmptosort.length; i ++)
        {
            sortres+= tmptosort[i]+"#";
        }

         tag = sortres.split("#");
        Paleotag []pt = new Paleotag[tag.length / 2];
        for (int i = 0, k = 0; i < tag.length; i += 2) {
            pt[k] = new Paleotag();
            pt[k].name = tag[i];
            pt[k].tag = tag[i + 1];
            k++;
        }

        return pt;
    }


}
