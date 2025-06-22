package com.example.myapplication;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class CardDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // 간단한 토스트 메시지로 대체 (추후 구현 예정)
        BusinessCard card = (BusinessCard) getIntent().getSerializableExtra("card");
        if (card != null) {
            Toast.makeText(this, "명함 상세보기: " + card.getName(), Toast.LENGTH_SHORT).show();
        }
        
        finish(); // 바로 돌아가기
    }
} 