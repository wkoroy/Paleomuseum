package ru.astronomrus.paleomuseum;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.File;

/**
 * Created by vkoroy on 25.02.18.
 */

public class ItemGallery extends ArrayAdapter<String> {

    private final Activity context;
    private final String[] web;
    private final String [] image_linx;


    public ItemGallery(Activity context,
                       String[] web_, String [] image_linx_) {
        super(context, R.layout.item_gallery, web_);
        this.context = context;
        this.web = web_;
        this.image_linx = image_linx_;

    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.item_gallery, null, true);
        TextView txtTitle = (TextView) rowView.findViewById(R.id.text);

        ImageView imageView = (ImageView) rowView.findViewById(R.id.img);
        if(image_linx[position].length()>0)
        {
            String imglink = image_linx[position];
            String [] nmtmp = imglink.split("/");
            final String flname = nmtmp[nmtmp.length -1];
            if( new File(MainActivity.ctx.getCacheDir(),flname).exists())
            {
                imglink = MainActivity.ctx.getCacheDir()+"/"+flname;
                Picasso.with(MainActivity.ctx).load(new File(imglink) )
                        .into(imageView);
            }
            else
            Picasso.with(MainActivity.ctx).load(image_linx[position]).into(imageView);

        }
        txtTitle.setText(web[position]);
        Log.d("IMAGERES", String.valueOf(position));
        // if( position<imgs.length) imageView.setImageBitmap(imgs[position]); // загрузка из озу
        return rowView;
    }
}