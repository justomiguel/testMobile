package com.jmv.frre.moduloestudiante.net;

/**
 * Created by Cleo on 12/1/13.
 */

import com.jmv.frre.moduloestudiante.constants.HomePageLinks;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.joda.time.DateTime;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author developer
 */
public class HTMLParser {

    private static final String ID = "id";
    private static final String NAME = "name";
    private static final String TITLE = "title";
    private static final String VALUE = "value";
    private static final String ERROR = "textoError";
    private static final String SPAN = "span";
    private static final String BLANK_SPACE = " ";
    private static final String CLAZZ = "class";

    private Document doc;

    public HTMLParser(String html) {
        doc = Jsoup.parse(html);
    }

    public boolean succefullyLoggin() {
        Elements wrapper = doc.getElementsByClass(ERROR);
        if (wrapper != null) {
            for (Element row : wrapper) {
                if (row != null) {
                    String rowID = row.attr(ID).toString();
                    Elements trips = row.getElementsContainingText("ERROR: Alumno inexistente o password incorrecto");
                    for (Element trip : trips) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public String getError(){
        Elements wrapper = doc.getElementsByClass(ERROR);
        if (wrapper != null) {
            for (Element row : wrapper) {
                if (row != null) {
                    String rowText = row.toString();
                    String error = rowText.substring(rowText.indexOf(ERROR) + ERROR.length() + 2, rowText.length() - 4);
                    return error;
                }
            }
        }
        return "Empty Error Response";
    }

    public HashMap<HomePageLinks, List<String>> getHomeLinks() {

        String baseUri = "http://sysacadweb.frre.utn.edu.ar/";

        HashMap<HomePageLinks, List<String>> links = new HashMap<HomePageLinks, List<String>>();
        Elements wrapper = doc.getElementsByClass("textoTabla");
        //inbound
        //faresReturn
        if (wrapper != null) {

            for (Element row : wrapper) {
                if (row != null) {
                    Elements trips = row.getElementsByTag("li");
                    for (Element trip : trips) {
                        String listElement = trip.getElementsByTag("a").get(0).toString();
                        String linkWithoutSessionID = listElement.substring(listElement.indexOf("href=\"")+6,listElement.indexOf("\">"));
                        linkWithoutSessionID = linkWithoutSessionID.substring(0, linkWithoutSessionID.indexOf("?"));
                        String link = baseUri+listElement.substring(listElement.indexOf("href=\"")+6,listElement.indexOf("\">"));
                        String content = listElement.substring(listElement.indexOf("\">")+2,listElement.indexOf("</a>"));
                        if (!content.contains("Salir")){
                            ArrayList<String> list = new ArrayList<String>(2);
                            list.add(link);
                            list.add(replaceAcutesHTML(content));
                            links.put(HomePageLinks.getLinkByValue(linkWithoutSessionID), list);
                        }
                    }
                }
            }
        }
        return links;
    }


    public String replaceAcutesHTML(String str) {

        str = str.replaceAll("&aacute;","á");
        str = str.replaceAll("&eacute;","é");
        str = str.replaceAll("&iacute;","í");
        str = str.replaceAll("&oacute;","ó");
        str = str.replaceAll("&uacute;","ú");
        str = str.replaceAll("&Aacute;","Á");
        str = str.replaceAll("&Eacute;","É");
        str = str.replaceAll("&Iacute;","Í");
        str = str.replaceAll("&Oacute;","Ó");
        str = str.replaceAll("&Uacute;","Ú");
        str = str.replaceAll("&ntilde;","ñ");
        str = str.replaceAll("&Ntilde;","Ñ");

        return str;

    }


    public boolean containsError() {
        Elements wrapper = doc.getElementsByClass(ERROR);
        if (wrapper != null && wrapper.size() > 0) {
            return  true;
        }
        return false;
    }

    public ArrayList<String> getExamsMade() {
        Elements wrapper = doc.getElementsByClass("textoTabla");
        ArrayList<String> list = new ArrayList<String>();
        if (wrapper != null) {

            for (Element row : wrapper) {
                if (row != null) {
                    StringBuilder builder = new StringBuilder();
                    for (org.jsoup.nodes.Element column : row.select("td")) {
                        builder.append(column.text());
                        builder.append("#");
                    }
                    String finalSt = builder.toString();
                    list.add(finalSt);
                }
            }
        }
        return list;
    }
}
