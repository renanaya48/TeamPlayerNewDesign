package com.example.teamplayer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.webianks.library.scroll_choice.ScrollChoice;

import java.util.ArrayList;
import java.util.List;

public class activity_Search extends AppCompatActivity {
    List<String> ages = new ArrayList<>();
    TextView textView;
    ScrollChoice scrollChoice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity__search);

        initView();

        loadData();
        scrollChoice.addItems(ages, 4);
        scrollChoice.setOnItemSelectedListener(new ScrollChoice.OnItemSelectedListener() {
            @Override
            public void onItemSelected(ScrollChoice scrollChoice, int position, String name) {
                textView.setText("choise  " + name);
            }
        });
    }

    private void loadData() {
        ages.add("12-16");
        ages.add("16-18");
        ages.add("18-21");
        ages.add("21-30");
        ages.add("30-40");
        ages.add("40-50");
        ages.add("50+");
        ages.add("Other");
    }

    private void initView() {
        textView = (TextView)findViewById(R.id.txt_result);
        scrollChoice = (ScrollChoice)findViewById(R.id.scroll_choice);
    }

    public void backButton(View view) {
        Intent intent=new Intent(this,select_action.class);
        startActivity(intent);
    }

    public void searchResult(View view) {
        Intent intent=new Intent(this,search_result.class);
        startActivity(intent);
    }
}
