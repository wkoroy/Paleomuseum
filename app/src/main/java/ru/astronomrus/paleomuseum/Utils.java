package ru.astronomrus.paleomuseum;
import android.app.Activity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by vkoroy on 31.08.17.
 */

public class Utils {

    public  static List<String> getTagValues(final String str , final Pattern TAG_REGEX ) {
        final List<String> tagValues = new ArrayList<String>();
        final Matcher matcher = TAG_REGEX.matcher(str);
        while (matcher.find()) {
            tagValues.add(matcher.group(1));
        }
        return tagValues;
    }
    public  static  String RemoveTag(String html){
        html = html.replaceAll("\\<.*?>","");
        html = html.replaceAll("&nbsp;","");
        html = html.replaceAll("&amp;","");

        return html;
    }

    public  static String convert_to_simple_text(String text)
    {
        int c_index =0 , d_index =0;
        while(c_index> -1 && d_index > -1) {
            c_index = text.indexOf("&");
            if(c_index < 0) break;
            d_index = text.substring(c_index).indexOf(";") + c_index;

            if(d_index < 0) break;

            if ((d_index - c_index) < 7 && (d_index - c_index) >0) {
                String html_ch = text.substring(c_index, d_index);
                String repl_str = " ";
                switch (html_ch)
                {
                    case "&times" : repl_str = ":";
                        break;
                    case "&raquo" : repl_str = "'";
                        break;
                    case "&laquo" : repl_str = "'";
                      break;
                    case "quot" : repl_str = "'";
                    break;

                    case "&quot" : repl_str = "'";
                        break;
                }


                text = text.replaceAll(html_ch+";",repl_str);
                //<p style="text-align: justify;">

                //</h3><p>
            }
            else break;
        }
        text =RemoveTag(text);

        text = text.replaceAll("</h3>"," ");
        text = text.replaceAll("<h3>"," ");

        text = text.replaceAll("</p>"," ");
        text = text.replaceAll("<p>","\n");
        text = text.replaceAll("<p style=\"text-align: justify;\">","");
        return text;
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }


}
