package com.example.myapplication;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import android.content.pm.PackageManager;
import android.Manifest;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

public class QrDisplayActivity extends AppCompatActivity {

    private ImageView qrImageView;
    private TextView qrInfoText;
    private Button btnSaveImage, btnShareNfc, btnShareQr, btnBackToMain;
    
    private String cardData;
    private String imageUri;
    private Bitmap qrBitmap;
    
    private static final int STORAGE_PERMISSION_REQUEST = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_display);

        initializeViews();
        setupData();
        generateQrCode();
        setupButtons();
    }

    private void initializeViews() {
        qrImageView = findViewById(R.id.qrImageView);
        qrInfoText = findViewById(R.id.qrInfoText);
        btnSaveImage = findViewById(R.id.btnSaveImage);
        btnShareNfc = findViewById(R.id.btnShareNfc);
        btnShareQr = findViewById(R.id.btnShareQr);
        btnBackToMain = findViewById(R.id.btnBackToMain);
    }

    private void setupData() {
        // Intent에서 데이터 받기
        String name = getIntent().getStringExtra("name");
        String phone = getIntent().getStringExtra("phone");
        String email = getIntent().getStringExtra("email");
        String company = getIntent().getStringExtra("company");
        String address = getIntent().getStringExtra("address");
        String tags = getIntent().getStringExtra("tags");
        imageUri = getIntent().getStringExtra("imagePath");

        // null 체크
        if (name == null) name = "";
        if (phone == null) phone = "";
        if (email == null) email = "";
        if (company == null) company = "";
        if (address == null) address = "";
        if (tags == null) tags = "";

        // 명함 데이터 JSON 형태로 구성 (태그 추가)
        cardData = String.format(
            "{\"type\":\"businesscard\",\"name\":\"%s\",\"phone\":\"%s\",\"email\":\"%s\",\"company\":\"%s\",\"address\":\"%s\",\"tags\":\"%s\",\"image\":\"%s\"}",
            escapeJson(name), escapeJson(phone), escapeJson(email), 
            escapeJson(company), escapeJson(address), escapeJson(tags), escapeJson(imageUri != null ? imageUri : "")
        );

        // 명함을 저장소에 저장
        saveCardToStorage(name, phone, email, company, address, tags);

        // 정보 텍스트 설정
        qrInfoText.setText("QR 코드를 스캔하면 명함 정보를 확인할 수 있습니다");
    }

    private void saveCardToStorage(String name, String phone, String email, String company, String address, String tags) {
        try {
            CardStorageManager storageManager = new CardStorageManager(this);
            
            // BusinessCard 객체 생성
            BusinessCard card = new BusinessCard(name, phone, email, company, address);
            card.setTagsFromString(tags);
            card.setImagePath(imageUri != null ? imageUri : "");
            card.setQrCode(cardData);
            card.setReceived(false); // 만든 명함
            
            // 저장
            storageManager.saveCard(card);
            
            System.out.println("✅ 명함이 저장소에 저장되었습니다: " + name);
        } catch (Exception e) {
            System.out.println("❌ 명함 저장 오류: " + e.getMessage());
        }
    }

    private String escapeJson(String str) {
        if (str == null) return "";
        return str.replace("\"", "\\\"").replace("\n", "\\n");
    }

    private void generateQrCode() {
        try {
            QRCodeWriter writer = new QRCodeWriter();
            BitMatrix bitMatrix = writer.encode(cardData, BarcodeFormat.QR_CODE, 512, 512);
            
            int width = bitMatrix.getWidth();
            int height = bitMatrix.getHeight();
            qrBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
            
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    qrBitmap.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                }
            }
            
            qrImageView.setImageBitmap(qrBitmap);
            
        } catch (WriterException e) {
            e.printStackTrace();
            Toast.makeText(this, "QR 코드 생성 실패", Toast.LENGTH_SHORT).show();
        }
    }

    private void setupButtons() {
        if (btnSaveImage != null) {
            btnSaveImage.setOnClickListener(v -> saveQrImage());
        }
        
        if (btnShareNfc != null) {
            btnShareNfc.setOnClickListener(v -> shareViaNfc());
        }
        
        if (btnShareQr != null) {
            btnShareQr.setOnClickListener(v -> shareQrCode());
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

    private void saveQrImage() {
        if (qrBitmap == null) {
            Toast.makeText(this, "QR 코드를 먼저 생성해주세요", Toast.LENGTH_SHORT).show();
            return;
        }

        // Android 9 이하에서 권한 체크
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        STORAGE_PERMISSION_REQUEST);
                return;
            }
        }

        performImageSave();
    }

    private void performImageSave() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                // Android 10 이상: MediaStore 사용
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DISPLAY_NAME, "businesscard_qr_" + System.currentTimeMillis() + ".png");
                values.put(MediaStore.Images.Media.MIME_TYPE, "image/png");
                values.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/BusinessCard");

                ContentResolver resolver = getContentResolver();
                Uri uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

                if (uri != null) {
                    try (OutputStream out = resolver.openOutputStream(uri)) {
                        if (out != null) {
                            qrBitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
                            Toast.makeText(this, "✅ QR 코드가 갤러리에 저장되었습니다", Toast.LENGTH_LONG).show();
                        }
                    }
                } else {
                    Toast.makeText(this, "❌ 이미지 저장 실패", Toast.LENGTH_SHORT).show();
                }
            } else {
                // Android 9 이하: 외부 저장소 직접 사용
                File picturesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
                File businessCardDir = new File(picturesDir, "BusinessCard");
                if (!businessCardDir.exists()) {
                    businessCardDir.mkdirs();
                }

                File imageFile = new File(businessCardDir, "businesscard_qr_" + System.currentTimeMillis() + ".png");
                try (FileOutputStream out = new FileOutputStream(imageFile)) {
                    qrBitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
                    Toast.makeText(this, "✅ QR 코드가 저장되었습니다: " + imageFile.getName(), Toast.LENGTH_LONG).show();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "❌ 이미지 저장 중 오류 발생", Toast.LENGTH_SHORT).show();
        }
    }

    private void shareViaNfc() {
        // NFC 화면으로 이동
        Intent intent = new Intent(this, NfcDisplayActivity.class);
        
        // 모든 명함 데이터 전달
        String name = getIntent().getStringExtra("name");
        String phone = getIntent().getStringExtra("phone");
        String email = getIntent().getStringExtra("email");
        String company = getIntent().getStringExtra("company");
        String address = getIntent().getStringExtra("address");
        
        intent.putExtra("name", name != null ? name : "");
        intent.putExtra("phone", phone != null ? phone : "");
        intent.putExtra("email", email != null ? email : "");
        intent.putExtra("company", company != null ? company : "");
        intent.putExtra("address", address != null ? address : "");
        if (imageUri != null) {
            intent.putExtra("imagePath", imageUri);
        }
        
        startActivity(intent);
    }

    private void shareQrCode() {
        if (qrBitmap == null) {
            Toast.makeText(this, "QR 코드를 먼저 생성해주세요", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            // 임시 파일에 QR 코드 저장
            File cachePath = new File(getCacheDir(), "images");
            cachePath.mkdirs();
            File file = new File(cachePath, "qr_code.png");
            
            FileOutputStream stream = new FileOutputStream(file);
            qrBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            stream.close();

            // FileProvider를 통해 URI 생성
            Uri contentUri = FileProvider.getUriForFile(this, "com.example.myapplication.fileprovider", file);

            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("image/png");
            shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri);
            shareIntent.putExtra(Intent.EXTRA_TEXT, "내 명함 QR 코드입니다. 스캔해서 명함 정보를 확인하세요!");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "명함 QR 코드 공유");
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            startActivity(Intent.createChooser(shareIntent, "QR 코드 공유"));

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "QR 코드 공유 중 오류가 발생했습니다", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        
        if (requestCode == STORAGE_PERMISSION_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                performImageSave();
            } else {
                Toast.makeText(this, "저장 권한이 필요합니다", Toast.LENGTH_SHORT).show();
            }
        }
    }
} 