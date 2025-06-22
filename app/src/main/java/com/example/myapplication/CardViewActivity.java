package com.example.myapplication;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

public class CardViewActivity extends AppCompatActivity {

    private TextView nameText, phoneText, emailText, companyText, addressText;
    private ImageView cardImageView;
    private Button btnCallPhone, btnSendEmail, btnBackToMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_view);

        initializeViews();
        processScannedData();
        setupButtons();
    }

    private void initializeViews() {
        nameText = findViewById(R.id.viewNameText);
        phoneText = findViewById(R.id.viewPhoneText);
        emailText = findViewById(R.id.viewEmailText);
        companyText = findViewById(R.id.viewCompanyText);
        addressText = findViewById(R.id.viewAddressText);
        cardImageView = findViewById(R.id.viewCardImage);
        btnCallPhone = findViewById(R.id.btnCallPhone);
        btnSendEmail = findViewById(R.id.btnSendEmail);
        btnBackToMain = findViewById(R.id.btnBackToMain);
    }

    private void processScannedData() {
        Intent intent = getIntent();
        String scannedData = intent.getStringExtra("scannedData");
        
        if (scannedData == null) {
            // Intent에서 직접 데이터 받기 (내부 앱에서 호출된 경우)
            String name = intent.getStringExtra("name");
            String phone = intent.getStringExtra("phone");
            String email = intent.getStringExtra("email");
            String company = intent.getStringExtra("company");
            String address = intent.getStringExtra("address");
            String imagePath = intent.getStringExtra("imagePath");
            
            displayCardInfo(name, phone, email, company, address, imagePath);
        } else {
            // QR 코드 스캔 데이터 파싱
            parseQrData(scannedData);
        }
    }

    private void parseQrData(String qrData) {
        try {
            JSONObject json = new JSONObject(qrData);
            
            String type = json.optString("type", "");
            if (!"businesscard".equals(type)) {
                Toast.makeText(this, "유효하지 않은 명함 QR 코드입니다", Toast.LENGTH_LONG).show();
                finish();
                return;
            }

            String name = json.optString("name", "");
            String phone = json.optString("phone", "");
            String email = json.optString("email", "");
            String company = json.optString("company", "");
            String address = json.optString("address", "");
            String imagePath = json.optString("image", "");

            displayCardInfo(name, phone, email, company, address, imagePath);

        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(this, "QR 코드 데이터 파싱 오류", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void displayCardInfo(String name, String phone, String email, 
                               String company, String address, String imagePath) {
        // null 체크 및 기본값 설정
        if (name == null || name.isEmpty()) name = "이름 없음";
        if (phone == null || phone.isEmpty()) phone = "전화번호 없음";
        if (email == null || email.isEmpty()) email = "이메일 없음";
        if (company == null || company.isEmpty()) company = "회사 정보 없음";
        if (address == null || address.isEmpty()) address = "주소 없음";

        // 텍스트 설정
        nameText.setText(name);
        phoneText.setText(phone);
        emailText.setText(email);
        companyText.setText(company);
        addressText.setText(address);

        // 이미지 로드 (있는 경우)
        if (imagePath != null && !imagePath.isEmpty()) {
            try {
                Uri imageUri = Uri.parse(imagePath);
                Glide.with(this)
                    .load(imageUri)
                    .placeholder(R.drawable.ic_launcher_background)
                    .error(R.drawable.ic_launcher_background)
                    .into(cardImageView);
            } catch (Exception e) {
                e.printStackTrace();
                cardImageView.setImageResource(R.drawable.ic_launcher_background);
            }
        } else {
            cardImageView.setImageResource(R.drawable.ic_launcher_background);
        }
    }

    private void setupButtons() {
        if (btnCallPhone != null) {
            btnCallPhone.setOnClickListener(v -> {
                String phone = phoneText.getText().toString();
                if (phone != null && !phone.equals("전화번호 없음") && !phone.isEmpty()) {
                    Intent callIntent = new Intent(Intent.ACTION_DIAL);
                    callIntent.setData(Uri.parse("tel:" + phone));
                    startActivity(callIntent);
                } else {
                    Toast.makeText(this, "유효한 전화번호가 없습니다", Toast.LENGTH_SHORT).show();
                }
            });
        }

        if (btnSendEmail != null) {
            btnSendEmail.setOnClickListener(v -> {
                String email = emailText.getText().toString();
                if (email != null && !email.equals("이메일 없음") && !email.isEmpty()) {
                    Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
                    emailIntent.setData(Uri.parse("mailto:" + email));
                    emailIntent.putExtra(Intent.EXTRA_SUBJECT, "안녕하세요");
                    startActivity(Intent.createChooser(emailIntent, "이메일 보내기"));
                } else {
                    Toast.makeText(this, "유효한 이메일이 없습니다", Toast.LENGTH_SHORT).show();
                }
            });
        }

        if (btnBackToMain != null) {
            btnBackToMain.setOnClickListener(v -> {
                Intent intent = new Intent(this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            });
        }
    }
} 