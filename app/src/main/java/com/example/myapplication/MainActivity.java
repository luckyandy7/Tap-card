package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.card.MaterialCardView;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;
import androidx.activity.result.ActivityResultLauncher;

public class MainActivity extends AppCompatActivity {

    private MaterialCardView createCardButton;
    private MaterialCardView nfcReceiveButton;
    private MaterialCardView qrScanButton;
    private MaterialCardView cardStorageButton;
    private ActivityResultLauncher<ScanOptions> barcodeLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            // QR 스캐너 초기화
            barcodeLauncher = registerForActivityResult(new ScanContract(), result -> {
                if (result.getContents() != null) {
                    // QR 코드 스캔 성공
                    String scannedData = result.getContents();
                    Intent intent = new Intent(MainActivity.this, CardViewActivity.class);
                    intent.putExtra("scannedData", scannedData);
                    startActivity(intent);
                } else {
                    Toast.makeText(this, "QR 코드 스캔이 취소되었습니다", Toast.LENGTH_SHORT).show();
                }
            });

            // Initialize views
            createCardButton = findViewById(R.id.createCardButton);
            nfcReceiveButton = findViewById(R.id.nfcReceiveButton);
            qrScanButton = findViewById(R.id.qrScanButton);
            cardStorageButton = findViewById(R.id.cardStorageButton);

            // null check
            if (createCardButton == null || nfcReceiveButton == null || qrScanButton == null || cardStorageButton == null) {
                Toast.makeText(this, "레이아웃 초기화 오류", Toast.LENGTH_LONG).show();
                finish();
                return;
            }

            // Set click listeners
            createCardButton.setOnClickListener(v -> {
                try {
                    // Go to InfoInputActivity to create a new card
                    Intent intent = new Intent(MainActivity.this, InfoInputActivity.class);
                    startActivity(intent);
                } catch (Exception e) {
                    System.out.println("Error in createCardButton click: " + e.getMessage());
                    Toast.makeText(MainActivity.this, "화면 전환 오류", Toast.LENGTH_SHORT).show();
                }
            });

            nfcReceiveButton.setOnClickListener(v -> {
                try {
                    // Go to NfcDisplayActivity to receive NFC data
                    Intent intent = new Intent(MainActivity.this, NfcDisplayActivity.class);
                    startActivity(intent);
                } catch (Exception e) {
                    System.out.println("Error in nfcReceiveButton click: " + e.getMessage());
                    Toast.makeText(MainActivity.this, "화면 전환 오류", Toast.LENGTH_SHORT).show();
                }
            });

            qrScanButton.setOnClickListener(v -> {
                try {
                    // QR 코드 스캔 시작
                    ScanOptions options = new ScanOptions();
                    options.setDesiredBarcodeFormats(ScanOptions.QR_CODE);
                    options.setPrompt("QR 코드를 스캔하세요");
                    options.setCameraId(0);  // 후면 카메라 사용
                    options.setBeepEnabled(false);
                    options.setBarcodeImageEnabled(true);
                    options.setOrientationLocked(false);
                    
                    barcodeLauncher.launch(options);
                } catch (Exception e) {
                    System.out.println("Error in qrScanButton click: " + e.getMessage());
                    Toast.makeText(MainActivity.this, "QR 스캔 오류", Toast.LENGTH_SHORT).show();
                }
            });

            cardStorageButton.setOnClickListener(v -> {
                try {
                    // Go to CardStorageActivity
                    Intent intent = new Intent(MainActivity.this, CardStorageActivity.class);
                    startActivity(intent);
                } catch (Exception e) {
                    System.out.println("Error in cardStorageButton click: " + e.getMessage());
                    Toast.makeText(MainActivity.this, "화면 전환 오류", Toast.LENGTH_SHORT).show();
                }
            });

        } catch (Exception e) {
            System.out.println("Error in MainActivity onCreate: " + e.getMessage());
            Toast.makeText(this, "앱 초기화 오류", Toast.LENGTH_LONG).show();
            finish();
        }
    }
}
