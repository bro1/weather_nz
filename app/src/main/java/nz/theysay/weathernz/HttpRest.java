package nz.theysay.weathernz;

import android.content.Context;
import android.content.res.Resources;
import android.os.Message;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;


public class HttpRest {

    private Context context = null;
    private static CookieManager cookieManager = null;
    private  MyHandler myHandler = null;

    public HttpRest(Context context, MyHandler myHandler) {
        this.context = context;
        this.myHandler = myHandler;

//        if (cookieManager == null) {
//            cookieManager = new CookieManager(new PersistentCookieStore(context), CookiePolicy.ACCEPT_ALL);
//            CookieHandler.setDefault(cookieManager);
//        }
    }




    public  void get48HoursForecast() {

        if (false) {
            StringBuilder sb = new StringBuilder();

            try (Scanner s = new Scanner(context.getResources().openRawResource(R.raw.wellington_sample))) {

                while (s.hasNextLine()) {
                    sb.append(s.nextLine() + "\n");
                }


                String val = sb.toString();


                Message m = myHandler.obtainMessage(1, val);
                m.sendToTarget();

            } catch (Exception e) {
                Message m = myHandler.obtainMessage(2, "Failed to load resource");
                m.sendToTarget();
            }

            return;
        }

        try {
            URL url = new URL("https://www.metservice.com/publicData/webdata/towns-cities/locations/wellington");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();

            con.setRequestMethod("GET");
            con.setRequestProperty("Accept", "application/json");
            con.setConnectTimeout(5000);
            con.setReadTimeout(5000);

            int status = con.getResponseCode();

            if (status != 401) {

                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String inputLine;
                StringBuffer content = new StringBuffer();
                while ((inputLine = in.readLine()) != null) {
                    content.append(inputLine);
                }
                in.close();

                Message m1 =  Message.obtain(myHandler, 1, content.toString());
                m1.sendToTarget();

            }


        } catch (Exception e) {

            StringWriter errors = new StringWriter();
            e.printStackTrace(new PrintWriter(errors));
            Message m =  Message.obtain(myHandler, 2, "Exception " + e.getMessage() + errors.toString());
            m.sendToTarget();
        }
    }


    public  void get7Days() {

        try {
            URL url = new URL("https://www.metservice.com/publicData/webdata/towns-cities/locations/wellington/7-days");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();

            con.setRequestMethod("GET");
            con.setRequestProperty("Accept", "application/json");
            con.setConnectTimeout(5000);
            con.setReadTimeout(5000);

            int status = con.getResponseCode();

            if (status != 401) {

                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String inputLine;
                StringBuffer content = new StringBuffer();
                while ((inputLine = in.readLine()) != null) {
                    content.append(inputLine);
                }
                in.close();

                Message m1 =  Message.obtain(myHandler, 3, content.toString());
                m1.sendToTarget();

            }


        } catch (Exception e) {

            StringWriter errors = new StringWriter();
            e.printStackTrace(new PrintWriter(errors));
            Message m =  Message.obtain(myHandler, 2, "Exception " + e.getMessage() + errors.toString());
            m.sendToTarget();
        }
    }


}
