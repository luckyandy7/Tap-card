package com.example.myapplication;

import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.nfc.NfcAdapter;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class NfcDisplayActivity extends AppCompatActivity {

    private NfcAdapter nfcAdapter;
    private PendingIntent pendingIntent;
    private TextView infoText;
    private ImageView previewImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nfc_display);

        try {
            infoText = findViewById(R.id.nfcTextView);
            previewImage = findViewById(R.id.cardPreviewImage);

            // null 체크
            if (infoText == null || previewImage == null) {
                Toast.makeText(this, "레이아웃 초기화 오류", Toast.LENGTH_LONG).show();
                finish();
                return;
            }

            nfcAdapter = NfcAdapter.getDefaultAdapter(this);
            if (nfcAdapter == null) {
                Toast.makeText(this, "NFC 미지원 기기입니다.", Toast.LENGTH_LONG).show();
                finish();
                return;
            }

            if (!nfcAdapter.isEnabled()) {
                Toast.makeText(this, "NFC가 꺼져 있습니다. 설정에서 켜주세요.", Toast.LENGTH_LONG).show();
                startActivity(new Intent(Settings.ACTION_NFC_SETTINGS));
            }

            pendingIntent = PendingIntent.getActivity(
                    this, 0,
                    new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP),
                    PendingIntent.FLAG_MUTABLE
            );

            // NFC 수신 인텐트인지 확인
            Intent intent = getIntent();
            if (intent != null) {
                if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())) {
                    handleNfcIntent(intent);
                } else {
                    // 명시적 인텐트로 받은 경우
                    displayIntentData(intent);
                }
            } else {
                infoText.setText("Intent 데이터가 없습니다.");
            }

        } catch (Exception e) {
            System.out.println("Error in NfcDisplayActivity onCreate: " + e.getMessage());
            Toast.makeText(this, "NFC 화면 초기화 오류", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        try {
            if (intent != null) {
                if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())) {
                    handleNfcIntent(intent);
                } else {
                    displayIntentData(intent);
                }
            }
        } catch (Exception e) {
            System.out.println("Error in onNewIntent: " + e.getMessage());
        }
    }

    private void handleNfcIntent(Intent intent) {
        try {
            if (intent == null) {
                return;
            }

            NdefMessage[] messages = getNdefMessages(intent);
            if (messages != null && messages.length > 0 && messages[0] != null) {
                NdefRecord[] records = messages[0].getRecords();
                if (records != null && records.length > 0 && records[0] != null) {
                    NdefRecord record = records[0];
                    byte[] payload = record.getPayload();

                    if (payload != null && payload.length > 0) {
                        try {
                            // ✅ MIME 전송이므로 언어코드 없음 → 그냥 UTF-8 바로 디코딩
                            String text = new String(payload, StandardCharsets.UTF_8);

                            if (text != null && !text.trim().isEmpty()) {
                                // 🔍 추출
                                String name = extractField(text, "이름:");
                                String phone = extractField(text, "전화:");
                                String email = extractField(text, "이메일:");
                                String company = extractField(text, "회사:");
                                String address = extractField(text, "주소:");

                                // ⬇️ UI에 보여주기
                                if (infoText != null) {
                                    infoText.setText("✅ NFC 수신 정보:\n\n" + text);
                                }

                                // ✅ 로그 찍기 - null 체크 추가
                                Log.d("NFC_LOG", "수신된 이름: " + (name != null ? name : "null"));
                                Log.d("NFC_LOG", "수신된 전화: " + (phone != null ? phone : "null"));
                                Log.d("NFC_LOG", "수신된 이메일: " + (email != null ? email : "null"));
                            } else {
                                if (infoText != null) {
                                    infoText.setText("NFC 데이터가 비어있거나 형식이 올바르지 않습니다.");
                                }
                            }
                        } catch (Exception e) {
                            System.out.println("Error processing NFC payload: " + e.getMessage());
                            if (infoText != null) {
                                infoText.setText("NFC 데이터 처리 중 오류가 발생했습니다.");
                            }
                        }
                    } else {
                        if (infoText != null) {
                            infoText.setText("NFC 데이터가 비어있습니다.");
                        }
                    }
                } else {
                    if (infoText != null) {
                        infoText.setText("NFC 레코드를 찾을 수 없습니다.");
                    }
                }
            } else {
                if (infoText != null) {
                    infoText.setText("NFC 메시지를 찾을 수 없습니다.");
                }
            }
        } catch (Exception e) {
            System.out.println("Error in handleNfcIntent: " + e.getMessage());
            if (infoText != null) {
                infoText.setText("NFC 데이터 처리 중 오류가 발생했습니다.");
            }
        }
    }

    private String extractField(String text, String label) {
        try {
            if (text == null || label == null || label.isEmpty()) {
                return "(없음)";
            }

            String[] lines = text.split("\n");
            if (lines == null) {
                return "(없음)";
            }

            for (String line : lines) {
                if (line != null && !line.trim().isEmpty() && line.trim().startsWith(label)) {
                    // label이 null이 아님을 확인했지만, 추가 안전 체크
                    if (line.length() >= label.length()) {
                        String result = line.substring(label.length()).trim();
                        return (result == null || result.isEmpty()) ? "(없음)" : result;
                    }
                }
            }
        } catch (StringIndexOutOfBoundsException e) {
            System.out.println("StringIndexOutOfBoundsException in extractField: " + e.getMessage());
            System.out.println("text: " + text);
            System.out.println("label: " + label);
        } catch (Exception e) {
            System.out.println("Error in extractField: " + e.getMessage());
        }
        return "(없음)";
    }

    private void displayIntentData(Intent intent) {
        try {
            if (intent == null) {
                if (infoText != null) {
                    infoText.setText("Intent가 null입니다.");
                }
                return;
            }

            String name = intent.getStringExtra("name");
            String phone = intent.getStringExtra("phone");
            String email = intent.getStringExtra("email");
            String company = intent.getStringExtra("company");
            String address = intent.getStringExtra("address");
            String imagePath = intent.getStringExtra("imagePath");

            if (name == null) name = "(이름 없음)";
            if (phone == null) phone = "(전화 없음)";
            if (email == null) email = "(이메일 없음)";
            if (company == null) company = "(회사 없음)";
            if (address == null) address = "(주소 없음)";

            String businessCardText = "이름: " + name +
                    "\n전화: " + phone +
                    "\n이메일: " + email +
                    "\n회사: " + company +
                    "\n주소: " + address;

            if (infoText != null) {
                infoText.setText("명함 정보:\n\n" + businessCardText);
            }

            if (imagePath != null && previewImage != null) {
                try {
                    Uri uri = Uri.parse(imagePath);
                    InputStream inputStream = getContentResolver().openInputStream(uri);
                    if (inputStream != null) {
                        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                        if (bitmap != null) {
                            previewImage.setImageBitmap(bitmap);
                        }
                        inputStream.close();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(this, "이미지 로드 실패", Toast.LENGTH_SHORT).show();
                }
            }
        } catch (Exception e) {
            System.out.println("Error in displayIntentData: " + e.getMessage());
            if (infoText != null) {
                infoText.setText("Intent 데이터 처리 중 오류가 발생했습니다.");
            }
        }
    }

    @SuppressWarnings("deprecation")
    private NdefMessage[] getNdefMessages(Intent intent) {
        try {
            if (intent == null) {
                return null;
            }
            return (NdefMessage[]) intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
        } catch (Exception e) {
            System.out.println("Error in getNdefMessages: " + e.getMessage());
            return null;
        }
    }
}
