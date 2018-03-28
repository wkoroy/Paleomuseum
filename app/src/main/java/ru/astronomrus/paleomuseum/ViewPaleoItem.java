package ru.astronomrus.paleomuseum;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.MotionEvent;
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
import java.io.IOException;
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_paleo_item);

        buttons = (LinearLayout)  findViewById(R.id.vp_buttons);
        content = (LinearLayout)  findViewById(R.id.vp_content);
        mimgv = (PhotoView) findViewById(R.id.vp_img);
        mtv = (TextView) findViewById(R.id.vp_text);
        mtv.setMovementMethod(new ScrollingMovementMethod());

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

        Picasso.with(this).load(getIntent().getStringExtra(GalleryFragment.I_IMG_LINK ).replace("-sm" , "-big"))
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

        ( new Description_getter()).execute(getIntent().getStringExtra(GalleryFragment.I_LINK));

        buttons.startAnimation( AnimationUtils.loadAnimation(MainActivity.ctx, R.anim.ll_show) );

        final ImageButton share = (ImageButton) findViewById(R.id.vp_share);
        final ImageButton download = (ImageButton) findViewById(R.id.vp_load);
        final ImageButton bookmark = (ImageButton) findViewById(R.id.vp_bookmark);

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                share.startAnimation(  AnimationUtils.loadAnimation(MainActivity.ctx, R.anim.scale));

                try {
                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setType("text/plain");
                    String shareSubText = "";
                    String shareBodyText =getIntent().getStringExtra(GalleryFragment.I_IMG_LINK ).replace("-sm" , "-big")+"\n"+ getIntent().getStringExtra(GalleryFragment.I_TEXT) +"\nАвтор:"
                            + getIntent().getStringExtra(GalleryFragment.I_AUTHOR)+
                            "\n"+getIntent().getStringExtra(GalleryFragment.I_DATE);
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

        bookmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bookmark.startAnimation(  AnimationUtils.loadAnimation(MainActivity.ctx, R.anim.scale_x));
            }
        });


    }

    //    ">
    public    class Description_getter extends AsyncTask<String, String, Void> {
        String htmlcode = "";
        String text="";
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
                }
                catch (Exception e)
                {
                    text = ViewPaleoItem.this.getIntent().getStringExtra(GalleryFragment.I_TEXT);
                }

                try {
                    String text2 = Utils.getTagValues(htmlcode , TAG_REGEX_descr2).get(0);
                    if(text2.indexOf(text) > -1)
                    {
                        text = text2;
                    }

                }
                catch (Exception e)
                {

                }
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
            mtv.setText(ViewPaleoItem.this.getIntent().getStringExtra(GalleryFragment.I_TEXT) +
                    "\n\n"+text+"\n\nАвтор:"+ViewPaleoItem.this.getIntent().getStringExtra(GalleryFragment.I_AUTHOR)
                    +"\n"+ViewPaleoItem.this.getIntent().getStringExtra(GalleryFragment.I_DATE) +
                    "\n  Смотреть на сайте: "+
                    getIntent().getStringExtra(GalleryFragment.I_LINK));
            Log.d("DESCR" , text);

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
}
