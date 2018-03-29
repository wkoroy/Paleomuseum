package ru.astronomrus.paleomuseum;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link BookmarksView.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link BookmarksView#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BookmarksView extends Fragment {

    GridView gv;
    ProgressBar pb;

    private OnFragmentInteractionListener mListener;

    public BookmarksView() {
        // Required empty public constructor
    }


    public static BookmarksView newInstance(String param1, String param2) {
        BookmarksView fragment = new BookmarksView();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_bookmarks_view, container, false);
    }



    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        pb = (ProgressBar) getView().findViewById(R.id.bv_progressBar);
        gv = (GridView) getView().findViewById(R.id.bv_cat_grid);


        update_list();
    }

    private void update_list() {
        db_BookMark db = new db_BookMark(MainActivity.ctx);

        // get all books
        List<PaleoItem> list = db.getAllBooks();
        final String []titles = new String[ list.size()];
        final String []imgs = new String[ list.size()];
        final String []links = new String[ list.size()];

        String []times = new String[ list.size()];
        String []authors = new String[ list.size()];
        final  String []descr = new String[ list.size()];

        for(int i =0;i<list.size() ;i++)
        {
            titles[i] = new String(list.get(i).getTitle());
            imgs[i] = new String(list.get(i).getImage());
            links[i] = new String(list.get(i).getOther());
            times[i] = new String("");
            authors[i] = new String(list.get(i).getAuthor());
            descr[i] =  new String(list.get(i).getDescription());

        }


        ItemGallery adapter = new ItemGallery( (Activity) MainActivity.ctx  , titles ,imgs);
        gv.setAdapter(adapter);

        final String[] finalTimes = times;
        final String[] finalAuthors = authors;
        gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                Intent isv = new Intent(MainActivity.ctx , ViewPaleoItem.class);
                isv.putExtra(GalleryFragment.I_IMG_LINK , imgs[position]);
                isv.putExtra(GalleryFragment.I_LINK , links[position]);
                isv.putExtra(GalleryFragment.I_TEXT , titles[position]);
                isv.putExtra(GalleryFragment.I_DATE, finalTimes[position]);
                isv.putExtra(GalleryFragment.I_AUTHOR , finalAuthors[position]);
                isv.putExtra(GalleryFragment.I_DESCR , descr[position]);

                Log.d("AMM" , links[position]);
                startActivity(isv);
            }
        });
        pb.setVisibility(View.GONE);
    }



    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
