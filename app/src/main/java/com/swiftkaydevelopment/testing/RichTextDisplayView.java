package com.swiftkaydevelopment.testing;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.webkit.WebView;


/**
 * Created by Kevin Haines on 9/23/15.
 *
 * custom view class extending WebView to display html rich text
 */
public class RichTextDisplayView extends WebView {

    SharedPreferences prefs;
    Context context;
    String html;
    public String FILE_PATH;

    public RichTextDisplayView(Context context) {
        super(context);
        this.context = context;
        init();
    }

    public RichTextDisplayView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }

    public RichTextDisplayView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init();
    }

    private void init(){

        prefs = PreferenceManager.getDefaultSharedPreferences(context);
        html = prefs.getString("html","<html>Click Here to enter note</html>");

        this.loadData(html, "text/html", null);
    }

    public void setHTML(String html){
        if(html == null){
            html = "";
        }
        if(html.equals("")){
            html = "<html><br><br>Click Here to add a note<br><br></html>";
        }
        this.loadData(html, "text/html", null);

    }








}
