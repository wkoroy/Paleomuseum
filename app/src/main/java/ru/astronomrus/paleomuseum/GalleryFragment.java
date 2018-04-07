package ru.astronomrus.paleomuseum;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.IOException;
import java.util.List;
import java.util.regex.Pattern;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Avinash on 11-03-2017.
 */
public class GalleryFragment extends Fragment {

    final public static String  BKEY_URL="bkeyurl";
    final public static String I_IMG_LINK="imlink";
    final public static String I_LINK="ilink";
    final public static String I_TEXT="itext";
    final public static String I_DATE="idate";
    final public static String I_AUTHOR="iauthor";
    final public static String I_DESCR="idescr";


    final Animation textAnimation = AnimationUtils.loadAnimation(MainActivity.ctx, R.anim.alpha_up_text);


    int pnum =1;
    int max_page =2879;
    GridView gv;
    ImageButton b_prev;
    ImageButton b_next;
    ImageButton b_page;
    ProgressBar pb;
    TextView page;
    String url = "https://www.ammonit.ru/newfoto/";

    final Pattern TAG_REGEX_itemfull_descr = Pattern.compile("<a href=\""+"(.+?)"+"title");

    final Pattern TAG_REGEX_item_time_descr = Pattern.compile("AAA" +"(.+?)"+"BBBBB");

    final Pattern TAG_REGEX_item_authots = Pattern.compile("user/" +"(.+?)"+"\\.htm");
    final Pattern TAG_REGEX_maxpg = Pattern.compile("class=\"pagination\" href=\"" +"(.+?)"+"\"><span>>><");
    //"../user/toltek.htm"
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle bundle = this.getArguments();
        if (bundle != null)
        {
            url = bundle.getString(BKEY_URL, url);
        }

        // Button tt = (Button) getView().findViewById(R.id.btnTimeTable);
        pb = (ProgressBar) getView().findViewById(R.id.fg_progressBar);
        page = (TextView) getView().findViewById(R.id.fg_page);
        gv = (GridView) getView().findViewById(R.id.cat_grid);
        (new Imgitems_getter()).execute(url+pnum);
        b_prev = (ImageButton)   getView().findViewById(R.id.fg_prev);
        b_next = (ImageButton)   getView().findViewById(R.id.fg_next);
        b_page = (ImageButton)   getView().findViewById(R.id.fg_setpage);


        final Animation sunRiseAnimation = AnimationUtils.loadAnimation(MainActivity.ctx, R.anim.scale);

        page.startAnimation(textAnimation);

        b_prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                b_prev.startAnimation(sunRiseAnimation);
                prev_page();


            }
        });

        b_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                b_next.startAnimation(sunRiseAnimation);
                next_page();
            }
        });

        b_page.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                b_page.startAnimation(sunRiseAnimation);
                //show_progress_pages_dialog();
                page_set_dlg();
            }
        });


        String []ar = {
                "Загрузка...",

        };
        String []ar1 ={
                "",

        };

        ItemGallery adapter = new ItemGallery( (Activity) MainActivity.ctx ,ar , ar1);
        gv.setAdapter(adapter);
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_gallery, container, false);

    }

    public    class Imgitems_getter extends AsyncTask<String, String, Void> {
        String htmlcode = "";
        boolean is_reset= false;
        @Override
        protected Void doInBackground(String... strings) {
            try {
                Log.d("URL" , strings[0]);
                OkHttpClient clientd = new OkHttpClient();
                Request requestd = new Request.Builder().url(strings[0])
                        .build();
                Response responsed = clientd.newCall(requestd).execute();
                String resultd = responsed.body().string();
                //Log.d("RESPD" , resultd);
                htmlcode = resultd;
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            final Animation alphadown = AnimationUtils.loadAnimation(MainActivity.ctx, R.anim.alpha_down);
            gv.startAnimation(alphadown);
            //gv.setAlpha(0.f);
            pb.setVisibility(View.VISIBLE);
            page.setAlpha(1.f);
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            page.setText( String.valueOf(pnum));
            page.startAnimation(textAnimation);

            List<String> lst = Utils.getTagValues(htmlcode , TAG_REGEX_itemfull_descr);

            htmlcode = htmlcode.replace("\n" ," ");
            htmlcode = htmlcode.replace("\r" ," ");
            htmlcode = htmlcode.replace("\n\r" ," ");
            htmlcode = htmlcode.replace("||" ,"BBBBB");//italic;color:gray">
            htmlcode = htmlcode.replace("italic;color:gray\"> " ,"AAA");
            List<String> lst_time = Utils.getTagValues(htmlcode , TAG_REGEX_item_time_descr);
            List<String> lst_authors = Utils.getTagValues(htmlcode , TAG_REGEX_item_authots);


            final String []  names =  new String[lst.size()];
            final String []  imgs =  new String[lst.size()];
            final String []  links =  new String[lst.size()];
            final String []  titles =  new String[lst.size()];
            String []  times   =  new String[lst_time.size()];
            String []authors = new String[lst_authors.size()];
            authors =  lst_authors.toArray(authors);
            times = (String[]) lst_time.toArray(times);
            //../foto/57670.htm"><img src="../upload/foto/858/151957999114695-sm.jpg" alt="Крошечные Geesops schlotheimi"

            final Pattern TAG_REGEX_link = Pattern.compile("foto/"+"(.+?)"+"\"><img src");
            final Pattern TAG_REGEX_imgs = Pattern.compile("img src=\"\\.\\."+"(.+?)"+"\" alt");
            final Pattern TAG_REGEX_names = Pattern.compile(" alt=\""+"(.+?)"+"\"");

            final Pattern TAG_REGEX_descr = Pattern.compile(" alt=\""+"(.+?)"+"\"");
            for( int i=0 ; i<lst.size(); i++)
            {
                //Log.d("RESPD" ,  lst.get(i));
                imgs[i] = "https://www.ammonit.ru"+Utils.getTagValues( lst.get(i) ,TAG_REGEX_imgs ).get(0);
                if(authors[i].equals("EvgenyK") )  imgs[i] = "https://ammonit.ru/pict/logo2.jpg";/// специально для гневного EvgenyG
                links[i] ="https://www.ammonit.ru/foto/"+ Utils.getTagValues( lst.get(i) ,TAG_REGEX_link ).get(0);
                names[i] =Utils.convert_to_simple_text( Utils.getTagValues( lst.get(i) ,TAG_REGEX_names ).get(0) );
                titles[i] =Utils.convert_to_simple_text( Utils.getTagValues( lst.get(i) ,TAG_REGEX_names ).get(0) ) +"\n\n"+times[i]+"\nАвтор: "+authors[i];
                if(authors[i].equals("EvgenyK") )   titles[i]+="\n\nпользователь EvgenyK запретил просматривать свои фото в данном приложении";
                Log.d("RESPD" ,   imgs[i] +" "+links[i]+ "  "+titles[i]);

            }

            List<String> lmaxpage = Utils.getTagValues(htmlcode , TAG_REGEX_maxpg);
            try{
            if(lmaxpage.size()>0)
            {
                String []tmpp = lmaxpage.get(0).split("/");
                if(tmpp.length > 1)
                {
                    max_page = new Integer(tmpp[tmpp.length-1]);
                }
            }
            }catch (Exception e)
            {
            }

            ItemGallery adapter = new ItemGallery( (Activity) MainActivity.ctx  , titles ,imgs);
            gv.setAdapter(adapter);

            final String[] finalTimes = times;
            final String[] finalAuthors = authors;
            gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                    Intent isv = new Intent(MainActivity.ctx , ViewPaleoItem.class);
                    isv.putExtra(I_IMG_LINK , imgs[position]);
                    isv.putExtra(I_LINK , links[position]);
                    isv.putExtra(I_TEXT , names[position]);
                    isv.putExtra(I_DATE, finalTimes[position]);
                    isv.putExtra(I_AUTHOR , finalAuthors[position]);
                    Log.d("AMM" , links[position]);
                    startActivity(isv);
                }
            });

            pb.setVisibility(View.GONE);
            final Animation alphaup = AnimationUtils.loadAnimation(MainActivity.ctx, R.anim.alpha_up);
            gv.startAnimation(alphaup);
        }
    };

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


   public void page_set_dlg()
   {
       int req_pg  = 1;
       final Dialog dialog = new Dialog(MainActivity.ctx);
       dialog.setContentView(R.layout.dlg_set_num_page);
       dialog.setTitle("Title...");
       dialog.show();
       final  TextView title = (TextView) dialog.findViewById(R.id.ps_title);
       final SeekBar seek = (SeekBar) dialog.findViewById(R.id.ps_seek_bar);
       seek.setMax(max_page);
       seek.setKeyProgressIncrement(1);
       seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

           public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser){
               title.setText(" Номер выбранной страницы: "+progress);

           }
           public void onStartTrackingTouch(SeekBar arg0) {
           }
           public void onStopTrackingTouch(SeekBar seekBar) {

           }
       });

       Button ok = (Button) dialog.findViewById(R.id.ps_ok);
       Button cancel = (Button) dialog.findViewById(R.id.ps_cancel);
       ok.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               pnum = seek.getProgress();
               (new Imgitems_getter()).execute(url+pnum);
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
   
   public  void  next_page()
   {
       if(pnum < max_page)
       {
           pnum++;
           (new Imgitems_getter()).execute(url+pnum);
       }
   }

   public  void  prev_page()
   {
       if(pnum >1){
           pnum --;
           (new Imgitems_getter()).execute(url+pnum);
       }
   }


}
