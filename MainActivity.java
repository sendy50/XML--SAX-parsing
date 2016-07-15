package com.example.sendy.xmlsaxparsing;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;


public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener
{
    private static final String TAG = MainActivity.class.getSimpleName();;
    String url = "http://www.divyabhaskar.co.in/rss-feed/1035/";
    public ListView listView;
    ArrayList<News> newsList;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this);

        swipeRefreshLayout.post(new Runnable()
        {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
                fetchData();
            }
        });



        listView = (ListView)findViewById(R.id.listView);
        new FatchNews().execute(url);
    }

    public void onRefresh() {
        fetchData();
    }


    private void fetchData()
    {
        swipeRefreshLayout.setRefreshing(true);

        SAXParserFactory spf = SAXParserFactory.newInstance();

        try {

            newsList= new ArrayList<>();
            SAXParser parser = spf.newSAXParser();

            DefaultHandler dh = new DefaultHandler()
            {
                News news;
                boolean titleb,shortDescriptionb;
                @Override
                public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
                    super.startElement(uri, localName, qName, attributes);

                    if(localName.equals("title"))
                    {
                        titleb=true;
                    }
                    else if(localName.equals("shortDescription"))
                    {
                        shortDescriptionb=true;
                    }


                }

                @Override
                public void characters(char[] ch, int start, int length) throws SAXException {
                    super.characters(ch, start, length);

                    if(titleb)
                    {
                        news = new News();
                        news.setTitle(new String(ch,start,length));
                    }
                    else if(shortDescriptionb)
                    {
                        news.setShortDesc(new String(ch,start,length));
                        newsList.add(news);

                    }
                }

                @Override
                public void endElement(String uri, String localName, String qName) throws SAXException {
                    super.endElement(uri, localName, qName);

                    if(localName.equals("title"))
                    {
                        titleb=false;
                    }
                    else if(localName.equals("shortDescription"))
                    {
                        shortDescriptionb=false;
                    }
                }
            };

            URL urll = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) urll.openConnection();

            conn.setRequestMethod("GET");
            conn.setConnectTimeout(15000);
            conn.setDoInput(true);
            conn.setConnectTimeout(10000);

            conn.connect();
            InputStream stream = conn.getInputStream();
            parser.parse(stream,dh);

           // swipeRefreshLayout.setRefreshing(false);

        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }



    }




    public class FatchNews extends AsyncTask<String,Void,String>
    {
        ProgressDialog pd;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd= ProgressDialog.show(MainActivity.this,"Waiting","Loading");

        }

        @Override
        protected String doInBackground(String... params)
        {
            fetchData();

            return null;
        }

        @Override
        protected void onPostExecute(String s)
        {
            super.onPostExecute(s);
            pd.dismiss();

            ArrayAdapter<News> adapter = new ArrayAdapter<News>(MainActivity.this,android.R.layout.simple_list_item_1,newsList);
            listView.setAdapter(adapter);

            swipeRefreshLayout.setRefreshing(false);


        }

        private void onErrorResponse(VolleyError error)
        {
            Log.e(TAG, "Server Error: " + error.getMessage());

            Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();

            // stopping swipe refresh
            swipeRefreshLayout.setRefreshing(false);
        }

    }




}
