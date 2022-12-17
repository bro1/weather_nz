package nz.theysay.weathernz;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;
import android.widget.TextView;
import android.widget.Toast;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;


public class MyHandler extends Handler {

    public static Forecast forecast7Days = null;
    public static Forecast forecast2Days = null;

    final String kSunny =  "☀";
    final String kPartCloud =  "\uD83C\uDF24";
    final String kWind =  "\uD83C\uDF2C"; // Face blowing wind 🌬

    String kRainyCloud =  "\uD83C\uDF27"; // rainy cloud 🌧
    String kCloudy =  "☁"; // cloud ☁


    final String kDegrees = "\u00B0";

    //static public MyHandler myHandler;

    Activity activity;

    public MyHandler(Looper looper, Activity a) {
        super(looper);
        this.activity = a;
    }

    @Override
    public void handleMessage(Message msg) {

        // full response
        if (msg.what == 1) {
            String content = (String) msg.obj;

            forecast2Days = processJSONWeather(content);
            displayForecast2Days(forecast2Days);
        }

        // any exception
        if (msg.what == 2) {
            String content = (String) msg.obj;
            Toast.makeText(activity, content, Toast.LENGTH_LONG).show();

        }

        // 7 days
        if (msg.what == 3) {
            handle7Days(msg);
        }

    }

    public void displayForecast7Days(Forecast t) {

        TextView txt7Days = (TextView) activity.findViewById(R.id.textview_7days2);
        if (txt7Days != null) {
            txt7Days.setText(t.get7Days());
        }

    }


    public void displayForecast2Days(Forecast t) {
        TextView txtToday = (TextView) activity.findViewById(R.id.textview_today);

        if (txtToday == null) return;

        txtToday.setText(t.getToday());

        TextView txtDay1Summary = (TextView) activity.findViewById(R.id.textview_day1_summary);
        txtDay1Summary.setText(t.getDaySummary(0));

        TextView txtDay1Desc = (TextView) activity.findViewById(R.id.textview_day1_desc);
        txtDay1Desc.setText(t.getDayDesc(0));

        TextView txtDay2Summary = (TextView) activity.findViewById(R.id.textview_day2_summary);
        txtDay2Summary.setText(t.getDaySummary(1));

        TextView txtDay2Desc = (TextView) activity.findViewById(R.id.textview_day2_desc);
        txtDay2Desc.setText(t.getDayDesc(1));

        TextView txtText = (TextView) activity.findViewById(R.id.textview_first);
        txtText.setText(t.getCurrentTemperature());

    }

    private void handle7Days(Message msg) {
        String content = (String) msg.obj;

        forecast7Days = processJSON7Days(content);
        displayForecast7Days(forecast7Days);

    }


    private Forecast processJSON7Days(String content) {
        Forecast r = new Forecast();

        try {
            JSONObject json = new JSONObject(content);
            // jq .layout.primary.slots[\"left-major\"].modules[0].observations.temperature wellington_sample.json
            JSONObject joLayout = json.getJSONObject("layout");
            JSONObject joPrimary = joLayout.getJSONObject("primary");
            JSONObject joSlots = joPrimary.getJSONObject("slots");

            JSONObject joMain = joSlots.getJSONObject("main");
            JSONArray jaMainModules = joMain.getJSONArray("modules");

            Date todayNow = new Date();
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ssZ");


            // jq ".layout.primary.slots.main.modules[0].days[] | {date, breakdown, forecasts}" wellington*4

            JSONObject joMainModule0 = jaMainModules.getJSONObject(0);
            JSONArray jaDays = joMainModule0.getJSONArray("days");

            for (int i = 0; i < jaDays.length() ; i++) {

                DayForecast f = new DayForecast();

                JSONObject joDay = jaDays.getJSONObject(i);

                try {
                    f.date = df.parse(joDay.getString("date"));
                    System.out.println("7 days - date  is " +  f.date);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                // breakdown is only available for the first 3 days

                f.dayCondition = joDay.getString("condition");

                // I don't think I need breakdown for each day
//                if (joDay.has("breakdown")) {
//                    JSONObject bd = joDay.getJSONObject("breakdown");
//
//                    f.overnightCondition = bd.getJSONObject("overnight").getString("condition");
//                    f.morningCondition = bd.getJSONObject("morning").getString("condition");
//                    f.afternoonCondition = bd.getJSONObject("afternoon").getString("condition");
//                    f.eveningCondition = bd.getJSONObject("evening").getString("condition");
//                }

                JSONObject forecast = joDay.getJSONArray("forecasts").getJSONObject(0);
                f.low = forecast.getString("lowTemp");
                f.high = forecast.getString("highTemp");
                f.statementDescription = forecast.getString("statement");

                r.days2.add(f);
            }




            //   "date": "2021-11-01T12:00:00+13:00",
            //  "breakdown": {
            //    "afternoon": {
            //      "condition": "partly-cloudy"
            //    },
            //    "evening": {
            //      "condition": "fine"
            //    },
            //    "morning": {
            //      "condition": "cloudy"
            //    },
            //    "overnight": {
            //      "condition": "few-showers"
            //    }
            //  },
            //  "forecasts": [
            //    {
            //      "highTemp": "18",
            //      "lowTemp": "11",
            //      "statement": "Fine. Northerly winds.",
            //      "sunrise": "2021-11-01T06:06:00+13:00",
            //      "sunset": "2021-11-01T20:04:00+13:00"
            //    }
            //  ]
            //}
            //{
            //  "date": "2021-11-02T12:00:00+13:00",
            //  "breakdown": {
            //    "afternoon": {
            //      "condition": "few-showers"
            //    },
            //    "evening": {
            //      "condition": "few-showers"
            //    },
            //    "morning": {
            //      "condition": "partly-cloudy"
            //    },
            //    "overnight": {
            //      "condition": "fine"
            //    }
            //  },
            //  "forecasts": [
            //    {
            //      "highTemp": "16",
            //      "lowTemp": "9",
            //      "statement": "Cloud increasing in the morning. A few showers from around midday. Southerlies developing late morning, strong near the south coast.",
            //      "sunrise": "2021-11-02T06:04:00+13:00",
            //      "sunset": "2021-11-02T20:05:00+13:00"
            //    }
            //  ]
            //}


            return r;


        } catch (JSONException e) {
            e.printStackTrace();
        }


        return r;

    }

    private Forecast processJSONWeather(String content) {

        Forecast r = new Forecast();

        try {
            JSONObject json = new JSONObject(content);
            // jq .layout.primary.slots[\"left-major\"].modules[0].observations.temperature wellington_sample.json
            JSONObject joLayout = json.getJSONObject("layout");
            JSONObject joPrimary = joLayout.getJSONObject("primary");
            JSONObject joSlots = joPrimary.getJSONObject("slots");
            JSONObject joLeftMajor = joSlots.getJSONObject("left-major");
            JSONArray jaModules = joLeftMajor.getJSONArray("modules");
            JSONObject joModule0 = jaModules.getJSONObject(0);
            JSONObject joObservations = joModule0.getJSONObject("observations");
            JSONArray jaTemperature = joObservations.getJSONArray("temperature");
            JSONObject joTemperature0 = jaTemperature.getJSONObject(0);
            r.temperatureCurrent = joTemperature0.getDouble("current");
            r.temperatureFeelsLike = joTemperature0.getInt("feelsLike");
            r.temperatureHigh = joTemperature0.getInt("high");
            r.temperatureLow = joTemperature0.getInt("low");



            //   {
            //    "current": 12.6,
            //    "feelsLike": 12,
            //    "high": 17,
            //    "low": 10,
            //    "title": ""
            //  }



            // jq .layout.primary.slots.main.modules[1].graph.columns wellington_sample.json

            JSONObject joMain = joSlots.getJSONObject("main");
            JSONArray jaMainModules = joMain.getJSONArray("modules");
            JSONObject joMainModule1 = jaMainModules.getJSONObject(1);
            JSONObject joGraph = joMainModule1.getJSONObject("graph");
            JSONArray joColumns = joGraph.getJSONArray("columns");


            // Each object looks like this
            // 	{
            //  	"date": "2021-10-31T01:00:00+13:00",
            //		"rainFallPerHour": "0.2",
            //		"rainfall": "0.2",
            //		"temperature": "15.1",
            //		"wind": {
            //			"direction": "NW",
            //			"speed": "20"
            //			}
            //		},


            Date todayNow = new Date();

            DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
            for (int i = 0; i< joColumns.length(); i++) {
                Graph g = new Graph();
                JSONObject jograph = joColumns.getJSONObject(i);
                try {
                    g.date = df.parse(jograph.getString("date"));
                    System.out.println(g.date);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                g.rainFallPerHour = jograph.getString("rainFallPerHour");
                g.temperature = jograph.getString("temperature");

                JSONObject joWind = jograph.getJSONObject("wind");
                
                g.windDirection = joWind.getString("direction");
                g.windSpeed = joWind.getString("speed");

                if (g.date.after(todayNow)) {
                    r.hourlyForecast.add(g);
                }
            }






            // jq ".layout.primary.slots.main.modules[0].days[] | {date, breakdown, forecasts}" wellington*4

            JSONObject joMainModule0 = jaMainModules.getJSONObject(0);
            JSONArray jaDays = joMainModule0.getJSONArray("days");

            for (int i = 0; i < jaDays.length() ; i++) {

                DayForecast f = new DayForecast();

                JSONObject joDay = jaDays.getJSONObject(i);

                try {
                    f.date = df.parse(joDay.getString("date"));
                    System.out.println(f.date);
                } catch (ParseException e) {
                    e.printStackTrace();
                }


                JSONObject bd = joDay.getJSONObject("breakdown");

                f.overnightCondition = bd.getJSONObject("overnight").getString("condition");
                f.morningCondition = bd.getJSONObject("morning").getString("condition");
                f.afternoonCondition = bd.getJSONObject("afternoon").getString("condition");
                f.eveningCondition = bd.getJSONObject("evening").getString("condition");

                JSONObject forecast = joDay.getJSONArray("forecasts").getJSONObject(0);
                f.low = forecast.getString("lowTemp");
                f.high = forecast.getString("highTemp");
                f.statementDescription = forecast.getString("statement");

                r.days2.add(f);
            }




            //   "date": "2021-11-01T12:00:00+13:00",
            //  "breakdown": {
            //    "afternoon": {
            //      "condition": "partly-cloudy"
            //    },
            //    "evening": {
            //      "condition": "fine"
            //    },
            //    "morning": {
            //      "condition": "cloudy"
            //    },
            //    "overnight": {
            //      "condition": "few-showers"
            //    }
            //  },
            //  "forecasts": [
            //    {
            //      "highTemp": "18",
            //      "lowTemp": "11",
            //      "statement": "Fine. Northerly winds.",
            //      "sunrise": "2021-11-01T06:06:00+13:00",
            //      "sunset": "2021-11-01T20:04:00+13:00"
            //    }
            //  ]
            //}
            //{
            //  "date": "2021-11-02T12:00:00+13:00",
            //  "breakdown": {
            //    "afternoon": {
            //      "condition": "few-showers"
            //    },
            //    "evening": {
            //      "condition": "few-showers"
            //    },
            //    "morning": {
            //      "condition": "partly-cloudy"
            //    },
            //    "overnight": {
            //      "condition": "fine"
            //    }
            //  },
            //  "forecasts": [
            //    {
            //      "highTemp": "16",
            //      "lowTemp": "9",
            //      "statement": "Cloud increasing in the morning. A few showers from around midday. Southerlies developing late morning, strong near the south coast.",
            //      "sunrise": "2021-11-02T06:04:00+13:00",
            //      "sunset": "2021-11-02T20:05:00+13:00"
            //    }
            //  ]
            //}


            return r;


        } catch (JSONException e) {

        }


        return r;
    }

    // Main forecast class
    class Forecast {
        double temperatureCurrent;
        int temperatureFeelsLike;
        int temperatureHigh;
        int temperatureLow;
        List<Graph> hourlyForecast = new LinkedList<>();
        List <DayForecast> days2 = new LinkedList<>();

        String getToday() {
            String fl = "";

            if (temperatureCurrent != temperatureFeelsLike) {
                fl = " (feels like " + temperatureFeelsLike + ")";
            }

            String dayForecast =  "Low:" + temperatureLow + kDegrees+ " now: " + temperatureCurrent + kDegrees + fl + " high: " + temperatureHigh + kDegrees;

            return dayForecast;

        }


        public SpannableStringBuilder get7Days() {

            SpannableStringBuilder r = new SpannableStringBuilder();
            // Set the date format to just 3 letters of weekday (Mon, Tue, etc.)
            SimpleDateFormat sdf = new SimpleDateFormat("E");

            for (DayForecast d : days2) {
                String weekDay = sdf.format(d.date);
                int start = r.length();
                r.append(weekDay);
                int end = r.length();
                r.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                r.append(": " + d.low + kDegrees + " - " + d.high + kDegrees + " ")
                        .append(translate(d.dayCondition))
                        .append("\n")
                        .append(d.statementDescription)
                        .append("\n")
                ;
            }

            return r;
        }

        SpannableStringBuilder getCurrentTemperature() {

            SimpleDateFormat sdf = new SimpleDateFormat("E HH:00");



            SpannableStringBuilder hourly = new SpannableStringBuilder();

            for (Graph g : hourlyForecast) {
                String rain = "";
                if (!g.rainFallPerHour.equals("0")) {
                    rain = "      \uD83C\uDF27 " + g.rainFallPerHour + "mm";
                }
//                hourly += sdf.format(g.date) + " " +  g.temperature + kDegrees + "      " + g.windSpeed + "km/h " + g.windDirection + rain + "\n";
                hourly
                        .append(sdf.format(g.date))
                        .append("     ")
                        .append(g.temperature + kDegrees)
                        .append("      " + g.windSpeed + "km/h ")
                        .append(g.windDirection + rain + "\n");
                ;
            }

            SpannableStringBuilder r = new SpannableStringBuilder();
            r.append(hourly);
            return r;
        }


        private String translate(String cond) {

            String r;
            switch(cond) {
                case "drizzle":
                case "rain":
                case "showers":
                case "few-showers":     r = kRainyCloud;          break;
                case "wind-rain":       r = kRainyCloud + kWind;  break;
                case "cloudy":          r = kCloudy;              break;
                case "partly-cloudy":   r = kPartCloud;           break;
                case "fine":            r = kSunny;               break;
                case "windy":           r = kWind;                break;
                default:                r = cond;
            }

            return r;
        }


        private SpannableStringBuilder getDaySummary(int i) {

            SpannableStringBuilder r = new SpannableStringBuilder();
            // Set the date format to just 3 letters of weekday (Mon, Tue, etc.)
            SimpleDateFormat sdf = new SimpleDateFormat("E");

            DayForecast d = days2.get(i);

                String weekDay = sdf.format(d.date);
                int start = r.length();
                r.append(weekDay);
                int end = r.length();
                r.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                r.append(": " + d.low + kDegrees + " - " + d.high + kDegrees + " ")
                        .append(translate(d.overnightCondition) + " " +
                                translate(d.morningCondition) + " " +
                                translate(d.afternoonCondition) + " " +
                                translate(d.eveningCondition))
                        .append("\n")
                        .append(d.statementDescription);


            return r;
    }


        private SpannableStringBuilder getDayDesc(int i) {

            SpannableStringBuilder r = new SpannableStringBuilder();
            r.append(days2.get(i).statementDescription);
            return r;
        }


    }

    class Graph {
        //  	"date": "2021-10-31T01:00:00+13:00",
        //		"rainFallPerHour": "0.2",
        //		"rainfall": "0.2",
        //		"temperature": "15.1",
        //		"wind": {
        //			"direction": "NW",
        //			"speed": "20"
        //			}

        Date date;
        String rainFallPerHour;
        String temperature;
        String windDirection;
        String windSpeed;

    }



    class DayForecast{
        Date date;

        String morningCondition;
        String afternoonCondition;
        String eveningCondition;
        String overnightCondition;

        String low;
        String high;
        String statementDescription; // e.g. "Cloud increasing in the morning. A few showers from around midday. Southerlies developing late morning, strong near the south coast.",

        String dayCondition; // using this for 7 day forecasts

    }

}
