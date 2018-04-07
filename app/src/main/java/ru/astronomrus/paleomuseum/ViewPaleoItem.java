package ru.astronomrus.paleomuseum;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.PowerManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.chrisbanes.photoview.PhotoView;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Pattern;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ViewPaleoItem extends AppCompatActivity {
    final Pattern TAG_REGEX_descr = Pattern.compile("<meta name=\"Description\" content=\""+"(.+?)"+"\">");
    public final static int PERMISSIONS_REQUEST_STRGRW = 10;
    LinearLayout buttons;
    LinearLayout content;
    ImageView loadimg;
    final Pattern TAG_REGEX_descr2 = Pattern.compile("<p style=\"text-align:left;\">"+"(.+?)"+"<");
    PhotoView mimgv;
    TextView mtv;
    Context ctx;
    db_BookMark db;
    String description="";
    String other_descr_data="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_paleo_item);

        ctx = this;
        buttons = (LinearLayout)  findViewById(R.id.vp_buttons);
        content = (LinearLayout)  findViewById(R.id.vp_content);
        mimgv = (PhotoView) findViewById(R.id.vp_img);
        mtv = (TextView) findViewById(R.id.vp_text);
        mtv.setMovementMethod(new ScrollingMovementMethod());

        final ImageButton share = (ImageButton) findViewById(R.id.vp_share);
        final ImageButton download = (ImageButton) findViewById(R.id.vp_load);
        final ImageButton bookmark = (ImageButton) findViewById(R.id.vp_bookmark);


        db = new db_BookMark(ViewPaleoItem.this);
        loadimg = (ImageView) findViewById(R.id.vp_imgload);
        try{
            getSupportActionBar().hide();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        Picasso.with(this).load(getIntent().getStringExtra(GalleryFragment.I_IMG_LINK).replace("-sm" , "-big")).placeholder(R.drawable.imgloading).into(mimgv);
        final Animation anim = AnimationUtils.loadAnimation(MainActivity.ctx, R.anim.loadanim);
        anim.setRepeatCount(-1);
        loadimg.startAnimation(anim);

        String imglink = getIntent().getStringExtra(GalleryFragment.I_IMG_LINK ).replace("-sm" , "-big");
        String [] nmtmp = imglink.split("/");
        final String flname = nmtmp[nmtmp.length -1];
        if( new File(getCacheDir(),flname).exists())
        {
            imglink = getCacheDir()+"/"+flname;
            Picasso.with(this).load(new File(imglink) )
                    .into(mimgv, new com.squareup.picasso.Callback() {
                        @Override
                        public void onSuccess() {
                            loadimg.setVisibility(View.GONE);
                            loadimg.clearAnimation();
                        }

                        @Override
                        public void onError() {

                        }
                    });
        }
        else

        Picasso.with(this).load(imglink)
                .into(mimgv, new com.squareup.picasso.Callback() {
                    @Override
                    public void onSuccess() {
                        loadimg.setVisibility(View.GONE);
                        loadimg.clearAnimation();
                    }

                    @Override
                    public void onError() {

                    }
                });

        mtv.setText(getIntent().getStringExtra(GalleryFragment.I_TEXT) +"\n Автор:"
                + getIntent().getStringExtra(GalleryFragment.I_AUTHOR)+
                "\n"+getIntent().getStringExtra(GalleryFragment.I_DATE));

      if(! getIntent().hasExtra(GalleryFragment.I_DESCR))  ( new Description_getter()).execute(getIntent().getStringExtra(GalleryFragment.I_LINK));
      else
      {
          description = getIntent().getStringExtra(GalleryFragment.I_DESCR);


          mtv.setText(ViewPaleoItem.this.getIntent().getStringExtra(GalleryFragment.I_TEXT) +
                  "\n\n"+description+"\n\nАвтор:"+ViewPaleoItem.this.getIntent().getStringExtra(GalleryFragment.I_AUTHOR)
                  +"\n"+ViewPaleoItem.this.getIntent().getStringExtra(GalleryFragment.I_DATE) +
                  "\n  Смотреть на сайте: "+
                  getIntent().getStringExtra(GalleryFragment.I_LINK));
      }


        buttons.startAnimation( AnimationUtils.loadAnimation(MainActivity.ctx, R.anim.ll_show) );
        final  String image = getIntent().getStringExtra(GalleryFragment.I_IMG_LINK ).replace("-sm" , "-big");;



        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                share.startAnimation(  AnimationUtils.loadAnimation(MainActivity.ctx, R.anim.scale));

                try {
                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setType("text/plain");
                    String shareSubText = "";
                    String shareBodyText =getIntent().getStringExtra(GalleryFragment.I_IMG_LINK ).replace("-sm" , "-big")+
                            "\n"+
                    mtv.getText().toString();
                    shareIntent.putExtra(Intent.EXTRA_SUBJECT, shareSubText);
                    shareIntent.putExtra(Intent.EXTRA_TEXT, shareBodyText);
                     startActivity(Intent.createChooser(shareIntent, "Поделиться"));
                }catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        });
        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                download.startAnimation(  AnimationUtils.loadAnimation(MainActivity.ctx, R.anim.scale_x));

                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {

                    if (ActivityCompat.checkSelfPermission(MainActivity.ctx, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
                    {
                        image_download(getIntent().getStringExtra(GalleryFragment.I_IMG_LINK ).replace("-sm" , "-big"));
                    }
                    else
                    {
                        ActivityCompat.requestPermissions((Activity) ViewPaleoItem.this,
                                new String[]{ android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                PERMISSIONS_REQUEST_STRGRW);

                    }
                }
            }
        });

        if(db.if_exists(image)) bookmark.setImageResource(R.drawable.ic_bookmark);
        else
            bookmark.setImageResource(R.drawable.ic_bookmark_border);


        bookmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bookmark.startAnimation(  AnimationUtils.loadAnimation(MainActivity.ctx, R.anim.scale_x));


                if(! db.if_exists(image))
                db.addBook(new PaleoItem( getIntent().getStringExtra(GalleryFragment.I_TEXT) ,
                        getIntent().getStringExtra(GalleryFragment.I_AUTHOR) , image
                       , description +"\n"+ other_descr_data +"\n"+   getIntent().getStringExtra(GalleryFragment.I_DATE), getIntent().getStringExtra(GalleryFragment.I_LINK) ));
                else
                    db.deleteBook(image);

                if(db.if_exists(image)) bookmark.setImageResource(R.drawable.ic_bookmark);
                else
                    bookmark.setImageResource(R.drawable.ic_bookmark_border);


                ( new DownloadTask(ViewPaleoItem.this)).execute(image.replace("-big" , "-sm"),flname.replace("-big" , "-sm"));
                ( new DownloadTask(ViewPaleoItem.this)).execute(image,flname);
            }
        });


    }

    //    ">
    public    class Description_getter extends AsyncTask<String, String, Void> {
        String htmlcode = "";
        String text="";
        String paleotime ="";
        String place="";
        String paleotype="";
        boolean is_reset= false;
        @Override
        protected Void doInBackground(String... strings) {
            //выполняем запрос и получаем ответ
            try {
                // is_reset = strings[0];
                Log.d("URL" , strings[0]);
                OkHttpClient clientd = new OkHttpClient();
                Request requestd = new Request.Builder().url(strings[0])
                        .build();
                Response responsed = clientd.newCall(requestd).execute();
                String resultd = responsed.body().string();
             // Log.d("RESPD" , resultd);
                htmlcode = resultd;

                htmlcode = htmlcode.replace("\n" ," ");
                htmlcode = htmlcode.replace("\r" ," ");
                htmlcode = htmlcode.replace("\n\r" ," ");

                try {
                    text = Utils.getTagValues(htmlcode, TAG_REGEX_descr).get(0);

                    String stext = Utils.RemoveTag(htmlcode);
                    paleotype =stext.substring(  stext.indexOf("Тип окаменелости:") , stext.indexOf("Ключевые слова")).trim();
                    paleotime =stext.substring(  stext.indexOf("Возраст окаменелости: ") , stext.indexOf("Место находки:")).trim();
                    place = stext.substring(  stext.indexOf("Место находки:") , stext.indexOf("Тип окаменелости: ")).trim();
                    Log.d("PLACE" , place);

                 other_descr_data = place+"\n"+paleotime+"\n"+paleotype;
                    text = stext.substring(  stext.indexOf("Фотогалерея:") , stext.indexOf("Фотография")).trim().replace("   \t\t\t","");

                }
                catch (Exception e)
                {
                    text = ViewPaleoItem.this.getIntent().getStringExtra(GalleryFragment.I_TEXT);
                }

                /*try {
                    String text2 = Utils.getTagValues(htmlcode , TAG_REGEX_descr2).get(0);
                    if(text2.indexOf(text) > -1)
                    {
                        text = text2;
                    }

                }
                catch (Exception e)
                {

                }*/
                //">  <meta content="toltek,  Ammonit.ru, paleontological internet portal" name="author
                text = text.replace(">  <meta content=\"" , "").replace(ViewPaleoItem.this.getIntent().getStringExtra(GalleryFragment.I_AUTHOR),"").
                        replace(",  Ammonit.ru, paleontological internet portal\" name=\"author" ,"");

            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();


        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);


           // List<String> lst_time = Utils.getTagValues(htmlcode , TAG_REGEX_item_time_descr);
          //  List<String> lst_authors = Utils.getTagValues(htmlcode , TAG_REGEX_item_authots);
           // String text = Utils.getTagValues(htmlcode , TAG_REGEX_descr).get(0);
            description = Utils.convert_to_simple_text(text).trim();
            if(description.length() ==1) description="";
            if(ViewPaleoItem.this.getIntent().getStringExtra(GalleryFragment.I_AUTHOR).equals("EvgenyK") )   description="пользователь EvgenyK запретил просматривать свои фото в данном приложении. Подробнее: https://ammonit.ru/text/2167.htm\n"+description;
            mtv.setText(ViewPaleoItem.this.getIntent().getStringExtra(GalleryFragment.I_TEXT) +
                    "\n\n"+description+"\n\nАвтор:"+ViewPaleoItem.this.getIntent().getStringExtra(GalleryFragment.I_AUTHOR)
                    +"\n"+ViewPaleoItem.this.getIntent().getStringExtra(GalleryFragment.I_DATE) +"\n"+place+"\n"+paleotime+"\n"+paleotype+
                    "\n  Смотреть на сайте: "+
                    getIntent().getStringExtra(GalleryFragment.I_LINK));
            Log.d("DESCR" , description);


        buttons.startAnimation( AnimationUtils.loadAnimation(MainActivity.ctx, R.anim.ll_hide) );

        }
    };



    @Override
    public void onRequestPermissionsResult(int permsRequestCode, String[] permissions, int[] grantResults){

        switch(permsRequestCode){

            case PERMISSIONS_REQUEST_STRGRW:
            {

                image_download( getIntent().getStringExtra(GalleryFragment.I_IMG_LINK).replace("-sm" , "-big") );
            }
            break;

        }

    }

    public static  void image_download(String imurl) {
        File direct = new File(Environment.getExternalStorageDirectory()
                + "/Download");
        if (!direct.exists()) {
            direct.mkdirs();
        }
        if(! direct.canWrite()) return;

        DownloadManager mgr = (DownloadManager) MainActivity.ctx.getSystemService(Context.DOWNLOAD_SERVICE);

        Uri downloadUri = Uri.parse(imurl);
        DownloadManager.Request request = new DownloadManager.Request(
                Uri.parse(imurl));


        String imgfname = "";
        String []tmpar  = imurl.split("/");
        imgfname = tmpar[tmpar.length-1];
        request.setAllowedNetworkTypes(
                DownloadManager.Request.NETWORK_WIFI
                        | DownloadManager.Request.NETWORK_MOBILE)
                .setAllowedOverRoaming(false).setTitle(MainActivity.ctx.getString(R.string.app_name))
                .setDescription("Загрузка изображения ")
                .setDestinationInExternalPublicDir("/Download",imgfname  ).setNotificationVisibility(View.VISIBLE);

        mgr.enqueue(request);
        Toast toast = Toast.makeText(MainActivity.ctx,
                "Изображение загружено в папку Download", Toast.LENGTH_SHORT);
        toast.show();
    }


    private class DownloadTask extends AsyncTask<String, Integer, String> {
        @Override
        protected void onProgressUpdate(Integer... values) {
            if(prevrpoc != values[0]) {
                Log.d("DOW", "perc  = " + values[0]);

            }
            super.onProgressUpdate(values);
            prevrpoc = values[0];
        }

        String flname="file";
        int prevrpoc = 0;
        private Context context;
        private PowerManager.WakeLock mWakeLock;

        public DownloadTask(Context context) {
            this.context = context;
        }

        @Override
        protected String doInBackground(String... sUrl) {

            InputStream input = null;
            OutputStream output = null;
            HttpURLConnection connection = null;
            try {


                String urlt =  sUrl[0];

                URL url = new URL(urlt);
                flname = sUrl[1];
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                // expect HTTP 200 OK, so we don't mistakenly save error report
                // instead of the file
                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    return "Server returned HTTP " + connection.getResponseCode()
                            + " " + connection.getResponseMessage();
                }

                // this will be useful to display download percentage
                // might be -1: server did not report the length
                int fileLength = connection.getContentLength();

                // download the file
                input = connection.getInputStream();
                output = new FileOutputStream(getCacheDir()+"/"+flname);

                byte data[] = new byte[512];
                long total = 0;
                int count;
                while ((count = input.read(data)) != -1) {
                    // allow canceling with back button
                    if (isCancelled()) {
                        input.close();
                        return null;
                    }
                    total += count;

                    output.write(data, 0, count);
                }
            } catch (Exception e) {
                return e.toString();
            } finally {
                try {
                    if (output != null)
                        output.close();
                    if (input != null)
                        input.close();
                } catch (IOException ignored) {
                }

                if (connection != null)
                    connection.disconnect();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {

            Log.d("DOW" , "completed!");

            super.onPostExecute(s);
        }
    }

}
