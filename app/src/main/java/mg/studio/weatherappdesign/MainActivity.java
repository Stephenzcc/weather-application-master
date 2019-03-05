package mg.studio.weatherappdesign;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.annotation.DrawableRes;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.text.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.*;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    static boolean flag=true;
    static boolean[] fl={true,false};

    public void btnClick(View view) {


        ConnectivityManager connManager = (ConnectivityManager) this.getSystemService(CONNECTIVITY_SERVICE);
        // 获取代表联网状态的NetWorkInfo对象
        NetworkInfo networkInfo = connManager.getActiveNetworkInfo();
        // 获取当前的网络连接是否可用
        if (null == networkInfo) {
            Toast.makeText(this, "The current network connection is unavailable", Toast.LENGTH_SHORT).show();
            Log.i("MainActivity", "当前的网络连接不可用");
        } else {
            boolean available = networkInfo.isAvailable();
            if (available) {
                Log.i("MainActivity", "当前的网络连接可用");
                if(flag == fl[0]){
                    new DownloadUpdate().execute();
                    flag = false;
                }
                else if(flag == fl[1]){
                    Toast.makeText(this, "Already update, please try later.", Toast.LENGTH_SHORT).show();
                    flag = true;
                }
            } else {
                Log.i("MainActivity", "当前的网络连接不可用");
                Toast.makeText(this, "The current network connection is unavailable", Toast.LENGTH_SHORT).show();
            }
        }
    }


    public void weathif(String str, ImageView img){
        if(str.equals("Clear")){
            img.setImageDrawable(getResources().getDrawable(R.drawable.sunny_small));
        }
        else if(str.equals("Clouds")){
            img.setImageDrawable(getResources().getDrawable(R.drawable.partly_sunny_small));
        }
        else if(str.equals("Windy")){
            img.setImageDrawable(getResources().getDrawable(R.drawable.windy_small));
        }
        else if(str.equals("Rainy")){
            img.setImageDrawable(getResources().getDrawable(R.drawable.rainy_small));
        }
    }

    public static String getWeekOfDate(Date dt) {
        String[] weekDays = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
        Calendar cal = Calendar.getInstance();
        cal.setTime(dt);
        int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (w < 0)
            w = 0;
        return weekDays[w];
    }



    private class DownloadUpdate extends AsyncTask<String, Void, String> {


        @Override
        protected String doInBackground(String... strings) {
            String stringUrl = "https://mpianatra.com/Courses/forecast.json";
            HttpURLConnection urlConnection = null;
            BufferedReader reader;

            try {
                URL url = new URL(stringUrl);

                // Create the request to get the information from the server, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();

                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Mainly needed for debugging
                    Log.d("TAG", line);
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                //The temperature
                return buffer.toString();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String temperature) {
            //Update the temperature displayed

            DateFormat  sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            DecimalFormat df = new DecimalFormat("0.0");
            Calendar calendar = new GregorianCalendar();


            try {


                JSONObject jsonObject = new JSONObject(temperature);
                JSONArray jsonList = jsonObject.getJSONArray("list");
                JSONObject jsonDay, jsonCity;
                JSONObject jsonMain, jsonWeath;
                JSONArray jsonWeather;

                for(int i=0;i<jsonList.length();i++) {
                    String temp, weather;

                    jsonDay = jsonList.getJSONObject(i);

                    calendar.setTime(sdf.parse(jsonDay.getString("dt_txt")));
                    if(i==0){
                        Log.d("MainActivity", "274.15f");
                        jsonMain = jsonDay.getJSONObject("main");
                        temp = jsonMain.getString("temp");
                        float t = Float.parseFloat(temp) - 273.15f;
                        temp = df.format(t);
                        //Log.d("MainActivity", "temp is" + temp);
                        ((TextView) findViewById(R.id.temperature_of_the_day)).setText(temp);

                        jsonWeather = jsonDay.getJSONArray("weather");
                        jsonWeath = jsonWeather.getJSONObject(0);
                        weather = jsonWeath.getString("main");
                        weathif(weather,(ImageView) findViewById(R.id.img_weather_condition));

                        ((TextView) findViewById(R.id.top_weekday)).setText(getWeekOfDate(calendar.getTime()));
                        //Log.d("MainActivity", getWeekOfDate(calendar.getTime()));

                        ((TextView) findViewById(R.id.tv_date)).setText(calendar.get(Calendar.DATE)+"/"+Integer.toString(calendar.get(Calendar.MONTH)+1)+"/"+calendar.get(Calendar.YEAR));
                        //Log.d("MainActivity", getWeekOfDate(calendar.getTime()));

                        jsonCity = jsonObject.getJSONObject("city");
                        ((TextView) findViewById(R.id.tv_location)).setText(jsonCity.getString("name"));
                    }
                    else if(i==8){

                        jsonMain = jsonDay.getJSONObject("main");
                        temp = jsonMain.getString("temp");
                        float t = Float.parseFloat(temp) - 274.15f;
                        temp = df.format(t);

                        jsonWeather = jsonDay.getJSONArray("weather");
                        jsonWeath = jsonWeather.getJSONObject(0);
                        weather = jsonWeath.getString("main");
                        weathif(weather,(ImageView) findViewById(R.id.img_1));

                        ((TextView) findViewById(R.id.text_1)).setText(getWeekOfDate(calendar.getTime())+"\n"+temp+"℃");
                        //Log.d("MainActivity", getWeekOfDate(calendar.getTime()));
                    }
                    else if(i==16){

                        jsonMain = jsonDay.getJSONObject("main");
                        temp = jsonMain.getString("temp");
                        float t = Float.parseFloat(temp) - 274.15f;
                        temp = df.format(t);

                        jsonWeather = jsonDay.getJSONArray("weather");
                        jsonWeath = jsonWeather.getJSONObject(0);
                        weather = jsonWeath.getString("main");
                        weathif(weather,(ImageView) findViewById(R.id.img_2));

                        ((TextView) findViewById(R.id.text_2)).setText(getWeekOfDate(calendar.getTime())+"\n"+temp+"℃");
                        //Log.d("MainActivity", getWeekOfDate(calendar.getTime()));
                    }
                    else if(i==24){

                        jsonMain = jsonDay.getJSONObject("main");
                        temp = jsonMain.getString("temp");
                        float t = Float.parseFloat(temp) - 274.15f;
                        temp = df.format(t);

                        jsonWeather = jsonDay.getJSONArray("weather");
                        jsonWeath = jsonWeather.getJSONObject(0);
                        weather = jsonWeath.getString("main");
                        weathif(weather,(ImageView) findViewById(R.id.img_3));

                        ((TextView) findViewById(R.id.text_3)).setText(getWeekOfDate(calendar.getTime())+"\n"+temp+"℃");
                        //Log.d("MainActivity", getWeekOfDate(calendar.getTime()));
                    }
                    else if(i==32){

                        jsonMain = jsonDay.getJSONObject("main");
                        temp = jsonMain.getString("temp");
                        float t = Float.parseFloat(temp) - 274.15f;
                        temp = df.format(t);

                        jsonWeather = jsonDay.getJSONArray("weather");
                        jsonWeath = jsonWeather.getJSONObject(0);
                        weather = jsonWeath.getString("main");
                        weathif(weather,(ImageView) findViewById(R.id.img_4));

                        ((TextView) findViewById(R.id.text_4)).setText(getWeekOfDate(calendar.getTime())+"\n"+temp+"℃");
                        //Log.d("MainActivity", getWeekOfDate(calendar.getTime()));
                    }
                }

            }catch (Exception e){
                e.printStackTrace();
            }

        }
    }
}
