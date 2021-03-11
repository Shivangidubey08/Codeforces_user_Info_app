
package com.example.codeforces;


import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;

/**
 * Displays information about a single earthquake.
 */
public class MainActivity extends AppCompatActivity {

    /** Tag for the log messages */
    public static final String LOG_TAG = MainActivity.class.getSimpleName();

    /** URL to query the USGS dataset for earthquake information */
    private static  String handle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Kick off an {@link AsyncTask} to perform the network request
        Button Submit= (Button) findViewById(R.id.submit);
        Submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v("check","1");
                StringBuilder handleBuild= new StringBuilder();
                EditText handleEditText= (EditText) findViewById(R.id.userHandle);
                String userInput= handleEditText.getText().toString();
                handleBuild.append("https://codeforces.com/api/user.info?handles=");
                handleBuild.append(userInput);
                handle=handleBuild.toString();
               // Log.v("handle","https://codeforces.com/api/user.info?handles=");
                TsunamiAsyncTask task = new TsunamiAsyncTask();
                task.execute();

            }
        });

    }

    /**
     * Update the screen to display information from the given {@link Event}.
     */
    private void updateUi(Event earthquake) {
        // Display the earthquake title in the UI
        TextView titleTextView = (TextView) findViewById(R.id.rank);
        titleTextView.setText(earthquake.currentRank);
        titleTextView = (TextView) findViewById(R.id.handle);
        titleTextView.setText(earthquake.handle);
        titleTextView = (TextView) findViewById(R.id.name);
        String temp=earthquake.firstName+" "+ earthquake.lastName;
        titleTextView.setText(temp);
        titleTextView = (TextView) findViewById(R.id.rating);
        titleTextView.setText(earthquake.currentRating+"");
        titleTextView = (TextView) findViewById(R.id.maxRating);
        String temp2="Maximum Rank "+earthquake.maxRank+", "+earthquake.maxRating;
        titleTextView.setText(temp2);
        Long lastOnline= earthquake.lastOnline;
        String LastOnline="Last Online "+getDateString(1000*earthquake.lastOnline);

        titleTextView = (TextView) findViewById(R.id.lastOnline);
        titleTextView.setText(LastOnline);



    }
    private String getDateString(long timeInMilliseconds) {
        SimpleDateFormat formatter = new SimpleDateFormat("EEE, d MMM yyyy 'at' HH:mm:ss z");
        return formatter.format(timeInMilliseconds);
    }
    /**
     * Returns a formatted date and time string for when the earthquake happened.
     */


    /**
     * Return the display string for whether or not there was a tsunami alert for an earthquake.
     */


    /**
     * {@link AsyncTask} to perform the network request on a background thread, and then
     * update the UI with the first earthquake in the response.
     */
    private class TsunamiAsyncTask extends AsyncTask<URL, Void, Event> {

        @Override
        protected Event doInBackground(URL... urls) {
            // Create URL object
            URL url = createUrl(handle);

            // Perform HTTP request to the URL and receive a JSON response back
            String jsonResponse = "";
            try {
                jsonResponse = makeHttpRequest(url);

            } catch (IOException e) {
                // TODO Handle the IOException
            }

            // Extract relevant fields from the JSON response and create an {@link Event} object
            Event earthquake = extractFeatureFromJson(jsonResponse);

            // Return the {@link Event} object as the result fo the {@link TsunamiAsyncTask}
            return earthquake;
        }

        /**
         * Update the screen with the given earthquake (which was the result of the
         * {@link TsunamiAsyncTask}).
         */
        @Override
        protected void onPostExecute(Event earthquake) {
            if (earthquake == null) {
                Log.v("earthquake","null");
                return;
            }

            Log.v("earthquake",earthquake.lastOnline.toString());
            updateUi(earthquake);
        }

        /**
         * Returns new URL object from the given string URL.
         */
        private URL createUrl(String stringUrl) {
            URL url = null;
            try {
                url = new URL(stringUrl);
            } catch (MalformedURLException exception) {
                Log.e(LOG_TAG, "Error with creating URL", exception);
                return null;
            }
            return url;
        }

        /**
         * Make an HTTP request to the given URL and return a String as the response.
         */
        private String makeHttpRequest(URL url) throws IOException {
            String jsonResponse = "";
            HttpURLConnection urlConnection = null;
            InputStream inputStream = null;
            try {
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setReadTimeout(10000 /* milliseconds */);
                urlConnection.setConnectTimeout(15000 /* milliseconds */);
                urlConnection.connect();
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
                Log.v("json Response",jsonResponse);
            } catch (IOException e) {
                // TODO: Handle the exception
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (inputStream != null) {
                    // function must handle java.io.IOException here
                    inputStream.close();
                }
            }
            return jsonResponse;
        }

        /**
         * Convert the {@link InputStream} into a String which contains the
         * whole JSON response from the server.
         */
        private String readFromStream(InputStream inputStream) throws IOException {
            StringBuilder output = new StringBuilder();
            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
                BufferedReader reader = new BufferedReader(inputStreamReader);
                String line = reader.readLine();
                while (line != null) {
                    output.append(line);
                    line = reader.readLine();
                }
            }
            return output.toString();
        }

        /**
         * Return an {@link Event} object by parsing out information
         * about the first earthquake from the input earthquakeJSON string.
         */
        private Event extractFeatureFromJson(String earthquakeJSON) {
            try {
                JSONObject baseJsonResponse = new JSONObject(earthquakeJSON);
                JSONArray featureArray = baseJsonResponse.getJSONArray("result");

                // If there are results in the features array
                if (featureArray.length() > 0) {
                    Log.i("","array length greater than 0");
                    Log.i("","array length greater than 0");
                    // Extract out the first feature (which is an earthquake)
                    JSONObject properties = featureArray.getJSONObject(0);
                    //Log.i("properties.getString(handle)",properties.getString("handle"));

                    return new Event(properties.getString("handle"),properties.getString("rank"),properties.getString("maxRank"),properties.getString("firstName"),properties.getString("lastName"),properties.getInt("rating"),properties.getInt("maxRating"),properties.getLong("lastOnlineTimeSeconds"));
                }
                else{
                    Log.v("","array length greater than 1");
                }
            } catch (JSONException e) {
                Log.e(LOG_TAG, "Problem parsing the earthquake JSON results", e);
            }
            return null;
        }
    }
}