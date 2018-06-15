package com.example.anshulsharma.interviewpreparation;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class InterviewQuestions extends AppCompatActivity {

    TextView question;
    TextView answer;
    ArrayList<String>interviewQuestions=new ArrayList<>();
    ArrayList<String>interviewAnswers=new ArrayList<>();
    int a=0;
    Button next;
    Button answers;
    private AdView mAdView;

    public class DownloadTask extends AsyncTask<String,Void ,String> {


        @Override
        protected String doInBackground(String... urls) {

            String result="";
            URL url;
            HttpURLConnection urlConnection=null;
            try {
                url = new URL(urls[0]);
                urlConnection=(HttpURLConnection)url.openConnection();
                InputStream in=urlConnection.getInputStream();
                InputStreamReader reader=new InputStreamReader(in);
                int data=reader.read();
                while (data!=-1){
                    char current=(char)data;
                    result+=current;
                    data=reader.read();
                }
                return result;

            }catch (Exception e){
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            interviewAnswers.clear();
            interviewQuestions.clear();
            try {
                JSONObject jsonObject = new JSONObject(result);
                String nameInfo=jsonObject.getString("questions");
                JSONArray nameArr=new JSONArray(nameInfo);
                for(int i=0;i<nameArr.length();i++){
                    JSONObject jsonPart=nameArr.getJSONObject(i);
                    interviewQuestions.add(jsonPart.getString("question"));
                    interviewAnswers.add(jsonPart.getString("Answer"));
                }

                question.setText(interviewQuestions.get(0));
            }catch (Exception e){
                e.printStackTrace();
            }

        }
    }

    public void getAnswer(View view){

        answer.setText(interviewAnswers.get(a));
        if(a==interviewAnswers.size()-1)
            Toast.makeText(this, "Congrats!! Now you are ready for the Interview!!", Toast.LENGTH_SHORT).show();
    }
    public void changerAnswer(View view){

        answer.setText("");
        a++;
        if(a<interviewAnswers.size()-1) {
            question.setText(interviewQuestions.get(a));
        }else if(a==interviewAnswers.size()-1){
            question.setText(interviewQuestions.get(a));
            next.setEnabled(false);
            next.setVisibility(View.INVISIBLE);
        }
    }
    public void previous(View view){
        next.setEnabled(true);
        next.setVisibility(View.VISIBLE);
        if(a>0) {
            a--;
            answer.setText("");
            question.setText(interviewQuestions.get(a));
        }else{
            Toast.makeText(this, "You are at the beginning", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interview_questions);

        question=findViewById(R.id.question);
        answer=findViewById(R.id.answer);
        next=findViewById(R.id.changeQuestion);
        answers=findViewById(R.id.getAnswer);
        DownloadTask task=new DownloadTask();
        task.execute("https://learncodeonline.in/api/android/datastructure.json");


        MobileAds.initialize(this, "ca-app-pub-6160218324498757~4546003493");

        AdView adView = new AdView(this);
        adView.setAdSize(AdSize.BANNER);
        adView.setAdUnitId("ca-app-pub-6160218324498757/1615753677");

        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }
}
