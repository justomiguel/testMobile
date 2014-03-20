package com.jmv.frre.moduloestudiante.comps;

import android.content.Context;
import android.widget.Button;

import com.jmv.frre.moduloestudiante.constants.HomePageLinks;

/**
 * Created by Cleo on 12/2/13.
 */
public class MyButton extends Button {

    private String link;
    private HomePageLinks enumLink;

    public MyButton(Context context) {
        super(context);
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public HomePageLinks getEnumLink() {
        return enumLink;
    }

    public void setEnumLink(HomePageLinks enumLink) {
        this.enumLink = enumLink;
    }
}
