package com.example.myapplication;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import com.google.android.material.slider.Slider;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView;
import android.graphics.Typeface;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.io.OutputStream;
import java.io.IOException;

public class CardCustomizeActivity extends AppCompatActivity {

    private TextView nameText, phoneText, emailText, companyText, addressText;
    private Slider nameSizeBar, phoneSizeBar, emailSizeBar, companySizeBar, addressSizeBar;
    private EditText fontInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_customize);

        try {
            initializeViews();
            setupData();
            setupUI();
        } catch (Exception e) {
            System.out.println("Error in onCreate: " + e.getMessage());
            Toast.makeText(this, "초기화 오류가 발생했습니다", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private void initializeViews() {
        // 먼저 템플릿을 로드한 다음 텍스트뷰들을 찾습니다
        loadSelectedTemplate();
        
        // 템플릿이 로드된 후 TextView 찾기
        nameText = findViewById(R.id.nameText);
        phoneText = findViewById(R.id.phoneText);
        emailText = findViewById(R.id.emailText);
        companyText = findViewById(R.id.companyText);
        addressText = findViewById(R.id.addressText);

        // TextView null 체크
        if (nameText == null || phoneText == null || emailText == null ||
            companyText == null || addressText == null) {
            Toast.makeText(this, "레이아웃 오류: 필수 TextView를 찾을 수 없습니다", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        nameSizeBar = findViewById(R.id.nameSeekBar);
        phoneSizeBar = findViewById(R.id.phoneSeekBar);
        emailSizeBar = findViewById(R.id.emailSeekBar);
        companySizeBar = findViewById(R.id.companySeekBar);
        addressSizeBar = findViewById(R.id.addressSeekBar);
        fontInput = findViewById(R.id.fontInput);
    }

    private void loadSelectedTemplate() {
        // Intent에서 템플릿 정보 가져오기
        int templateLayoutRes = getIntent().getIntExtra("templateLayoutRes", R.layout.card_template_1_minimal);
        
        // cardLayout 컨테이너 찾기
        FrameLayout cardLayout = findViewById(R.id.cardLayout);
        if (cardLayout != null) {
            try {
                cardLayout.removeAllViews();
                
                // 선택된 템플릿 레이아웃을 인플레이트
                LayoutInflater inflater = LayoutInflater.from(this);
                View templateView = inflater.inflate(templateLayoutRes, cardLayout, false);
                
                cardLayout.addView(templateView);
                
                System.out.println("템플릿 로드 성공: " + templateLayoutRes);
            } catch (Exception e) {
                System.out.println("템플릿 로드 오류: " + e.getMessage());
                Toast.makeText(this, "템플릿 로드 오류", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void setupData() {
        // 전달받은 데이터
        String name = getIntent().getStringExtra("name");
        String phone = getIntent().getStringExtra("phone");
        String email = getIntent().getStringExtra("email");
        String company = getIntent().getStringExtra("company");
        String address = getIntent().getStringExtra("address");
        String tags = getIntent().getStringExtra("tags");
        int templateId = getIntent().getIntExtra("templateId", 1);
        int templateLayoutRes = getIntent().getIntExtra("templateLayoutRes", R.layout.card_template_1_minimal);

        // null 체크 및 기본값 설정
        if (name == null) name = "";
        if (phone == null) phone = "";
        if (email == null) email = "";
        if (company == null) company = "";
        if (address == null) address = "";
        if (tags == null) tags = "";

        // 템플릿에 따른 데이터 설정
        nameText.setText(name);
        phoneText.setText(phone);
        emailText.setText(email);
        companyText.setText(company);
        addressText.setText(address);
    }

    private void setupUI() {
        setupSpinners();
        setupSliders();
        setupButtons();
        setupTextEditing();
        setupFontStyleSpinners();
    }

    private void setupSpinners() {
        Spinner nameColorSpinner = findViewById(R.id.nameColorSpinner);
        Spinner phoneColorSpinner = findViewById(R.id.phoneColorSpinner);
        Spinner emailColorSpinner = findViewById(R.id.emailColorSpinner);
        Spinner companyColorSpinner = findViewById(R.id.companyColorSpinner);
        Spinner addressColorSpinner = findViewById(R.id.addressColorSpinner);

        String[] colors = {"검정", "빨강", "파랑", "초록", "보라", "주황", "갈색", "회색"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, colors);

        // null 체크 후 adapter 설정
        if (nameColorSpinner != null) {
            nameColorSpinner.setAdapter(adapter);
            setupColorSpinner(nameColorSpinner, nameText);
        }
        if (phoneColorSpinner != null) {
            phoneColorSpinner.setAdapter(adapter);
            setupColorSpinner(phoneColorSpinner, phoneText);
        }
        if (emailColorSpinner != null) {
            emailColorSpinner.setAdapter(adapter);
            setupColorSpinner(emailColorSpinner, emailText);
        }
        if (companyColorSpinner != null) {
            companyColorSpinner.setAdapter(adapter);
            setupColorSpinner(companyColorSpinner, companyText);
        }
        if (addressColorSpinner != null) {
            addressColorSpinner.setAdapter(adapter);
            setupColorSpinner(addressColorSpinner, addressText);
        }
    }

    private void setupSliders() {
        if (nameSizeBar != null && nameText != null) setupSlider(nameSizeBar, nameText);
        if (phoneSizeBar != null && phoneText != null) setupSlider(phoneSizeBar, phoneText);
        if (emailSizeBar != null && emailText != null) setupSlider(emailSizeBar, emailText);
        if (companySizeBar != null && companyText != null) setupSlider(companySizeBar, companyText);
        if (addressSizeBar != null && addressText != null) setupSlider(addressSizeBar, addressText);
    }

    private void setupButtons() {
        Button btnCancel = findViewById(R.id.btnCancel);
        Button btnFinish = findViewById(R.id.btnFinish);

        if (btnCancel != null) {
            btnCancel.setOnClickListener(this::onCancelClick);
        }
        if (btnFinish != null) {
            btnFinish.setOnClickListener(this::onFinishClick);
        }
    }

    private void setupTextEditing() {
        // 텍스트뷰를 클릭하면 편집 가능하게 만들기
        setupTextEditClick(nameText, "이름을 입력하세요");
        setupTextEditClick(phoneText, "전화번호를 입력하세요");
        setupTextEditClick(emailText, "이메일을 입력하세요");
        setupTextEditClick(companyText, "회사/직책을 입력하세요");
        setupTextEditClick(addressText, "주소를 입력하세요");
    }

    private void setupTextEditClick(TextView textView, String hint) {
        if (textView == null) return;
        
        textView.setOnClickListener(v -> {
            showEditDialog(textView, hint);
        });
        
        // 클릭 가능한 상태임을 시각적으로 표시
        textView.setBackground(getResources().getDrawable(android.R.drawable.editbox_background, null));
        textView.setPadding(8, 8, 8, 8);
    }

    private void showEditDialog(TextView targetTextView, String hint) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("텍스트 편집");

        // EditText 생성
        final EditText input = new EditText(this);
        input.setHint(hint);
        input.setText(targetTextView.getText().toString());
        input.selectAll(); // 기존 텍스트 전체 선택
        builder.setView(input);

        // 확인 버튼
        builder.setPositiveButton("확인", (dialog, which) -> {
            String newText = input.getText().toString();
            targetTextView.setText(newText);
            Toast.makeText(this, "텍스트가 업데이트되었습니다", Toast.LENGTH_SHORT).show();
        });

        // 취소 버튼
        builder.setNegativeButton("취소", (dialog, which) -> dialog.cancel());

        AlertDialog dialog = builder.create();
        dialog.show();

        // 키보드 자동 표시
        if (dialog.getWindow() != null) {
            dialog.getWindow().setSoftInputMode(android.view.WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    private void setupFontStyleSpinners() {
        Spinner fontStyleSpinner = findViewById(R.id.fontStyleSpinner);
        Spinner textStyleSpinner = findViewById(R.id.textStyleSpinner);

        // 폰트 패밀리 옵션
        String[] fontStyles = {"기본", "세리프", "모노스페이스", "산세리프"};
        ArrayAdapter<String> fontAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, fontStyles);

        // 텍스트 스타일 옵션
        String[] textStyles = {"보통", "굵게", "기울임", "굵은 기울임"};
        ArrayAdapter<String> textAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, textStyles);

        if (fontStyleSpinner != null) {
            fontStyleSpinner.setAdapter(fontAdapter);
            fontStyleSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    applyFontFamily(position);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {}
            });
        }

        if (textStyleSpinner != null) {
            textStyleSpinner.setAdapter(textAdapter);
            textStyleSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    applyTextStyle(position);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {}
            });
        }
    }

    private void applyFontFamily(int position) {
        Typeface typeface = Typeface.DEFAULT;
        switch (position) {
            case 0: // 기본
                typeface = Typeface.DEFAULT;
                break;
            case 1: // 세리프
                typeface = Typeface.SERIF;
                break;
            case 2: // 모노스페이스
                typeface = Typeface.MONOSPACE;
                break;
            case 3: // 산세리프
                typeface = Typeface.SANS_SERIF;
                break;
        }

        // 모든 텍스트뷰에 폰트 적용
        if (nameText != null) nameText.setTypeface(typeface);
        if (phoneText != null) phoneText.setTypeface(typeface);
        if (emailText != null) emailText.setTypeface(typeface);
        if (companyText != null) companyText.setTypeface(typeface);
        if (addressText != null) addressText.setTypeface(typeface);
    }

    private void applyTextStyle(int position) {
        int style = Typeface.NORMAL;
        switch (position) {
            case 0: // 보통
                style = Typeface.NORMAL;
                break;
            case 1: // 굵게
                style = Typeface.BOLD;
                break;
            case 2: // 기울임
                style = Typeface.ITALIC;
                break;
            case 3: // 굵은 기울임
                style = Typeface.BOLD_ITALIC;
                break;
        }

        // 모든 텍스트뷰에 스타일 적용
        if (nameText != null) nameText.setTypeface(nameText.getTypeface(), style);
        if (phoneText != null) phoneText.setTypeface(phoneText.getTypeface(), style);
        if (emailText != null) emailText.setTypeface(emailText.getTypeface(), style);
        if (companyText != null) companyText.setTypeface(companyText.getTypeface(), style);
        if (addressText != null) addressText.setTypeface(addressText.getTypeface(), style);
    }

    // 메서드 참조를 위한 핸들러들
    private void onCancelClick(View view) {
        finish();
    }

    private void onFinishClick(View view) {
        handleFinishClick();
    }

    // 헬퍼 메서드들
    private void setupSlider(Slider slider, TextView target) {
        if (slider == null || target == null) {
            System.out.println("setupSlider: null parameter detected");
            return;
        }

        try {
            float currentSize = target.getTextSize();
            float density = getResources().getDisplayMetrics().density;
            float sizeInSp = Math.max(10f, currentSize / density);
            slider.setValue(sizeInSp);

            // 람다 표현식 사용
            slider.addOnChangeListener((slider1, value, fromUser) -> handleSliderChange(target, value));
        } catch (Exception e) {
            System.out.println("Error in setupSlider: " + e.getMessage());
        }
    }

    private void setupColorSpinner(Spinner spinner, TextView target) {
        if (spinner == null || target == null) {
            System.out.println("setupColorSpinner: null parameter detected");
            return;
        }

        try {
            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    handleColorSelection(target, position);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    // 빈 구현
                }
            });
        } catch (Exception e) {
            System.out.println("Error in setupColorSpinner: " + e.getMessage());
        }
    }

    private void setCenterAlign(TextView textView) {
        if (textView != null) {
            textView.setGravity(Gravity.CENTER_HORIZONTAL);
            textView.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            ));
        }
    }

    // 슬라이더 변경 처리를 위한 메서드
    private void handleSliderChange(TextView targetTextView, float value) {
        try {
            if (targetTextView != null && value >= 10 && value <= 40) {
                targetTextView.setTextSize(value);
            }
        } catch (Exception e) {
            System.out.println("Error in slider change: " + e.getMessage());
        }
    }

    // 스피너 색상 변경 처리를 위한 메서드
    private void handleColorSelection(TextView targetTextView, int position) {
        try {
            if (targetTextView != null) {
                int color = Color.BLACK;
                switch (position) {
                    case 0: // 검정
                        color = Color.BLACK;
                        break;
                    case 1: // 빨강
                        color = Color.RED;
                        break;
                    case 2: // 파랑
                        color = Color.BLUE;
                        break;
                    case 3: // 초록
                        color = Color.GREEN;
                        break;
                    case 4: // 보라
                        color = Color.parseColor("#9C27B0");
                        break;
                    case 5: // 주황
                        color = Color.parseColor("#FF9800");
                        break;
                    case 6: // 갈색
                        color = Color.parseColor("#8D6E63");
                        break;
                    case 7: // 회색
                        color = Color.GRAY;
                        break;
                    default:
                        color = Color.BLACK;
                        break;
                }
                targetTextView.setTextColor(color);
            }
        } catch (Exception e) {
            System.out.println("Error in spinner selection: " + e.getMessage());
        }
    }

    private void handleFinishClick() {
        try {
            // null 안전한 텍스트 추출
            String nameValue = "";
            String phoneValue = "";
            String emailValue = "";
            String companyValue = "";
            String addressValue = "";
            String tagsValue = "";
            
            if (nameText != null && nameText.getText() != null) {
                String temp = nameText.getText().toString();
                nameValue = temp != null ? temp : "";
            }
            if (phoneText != null && phoneText.getText() != null) {
                String temp = phoneText.getText().toString();
                phoneValue = temp != null ? temp : "";
            }
            if (emailText != null && emailText.getText() != null) {
                String temp = emailText.getText().toString();
                emailValue = temp != null ? temp : "";
            }
            if (companyText != null && companyText.getText() != null) {
                String temp = companyText.getText().toString();
                companyValue = temp != null ? temp : "";
            }
            if (addressText != null && addressText.getText() != null) {
                String temp = addressText.getText().toString();
                addressValue = temp != null ? temp : "";
            }
            
            // Intent에서 태그 값 가져오기
            String tags = getIntent().getStringExtra("tags");
            tagsValue = tags != null ? tags : "";

            String cardText = "이름: " + nameValue +
                    "\n전화: " + phoneValue +
                    "\n이메일: " + emailValue +
                    "\n회사: " + companyValue +
                    "\n주소: " + addressValue;

            // ✅ 전송되는 문자열 확인
            System.out.println("📤 전송되는 cardText:\n" + cardText);

            // 이미지 생성
            View cardLayout = findViewById(R.id.cardLayout);
            if (cardLayout != null) {
                Bitmap cardBitmap = Bitmap.createBitmap(cardLayout.getWidth(), cardLayout.getHeight(), Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(cardBitmap);
                cardLayout.draw(canvas);

                Uri savedImageUri = null;

                // 갤러리 저장
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    ContentValues values = new ContentValues();
                    values.put(MediaStore.Images.Media.DISPLAY_NAME, "mycard_" + System.currentTimeMillis() + ".png");
                    values.put(MediaStore.Images.Media.MIME_TYPE, "image/png");
                    values.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/MyBusinessCards");

                    ContentResolver resolver = getContentResolver();
                    Uri uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

                    if (uri != null) {
                        try {
                            OutputStream out = resolver.openOutputStream(uri);
                            if (out != null) {
                                cardBitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
                                out.close();
                                savedImageUri = uri;
                                Toast.makeText(this, "✅ 갤러리에 저장 완료!", Toast.LENGTH_SHORT).show();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                            Toast.makeText(this, "❌ 저장 실패", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    Toast.makeText(this, "⚠ Android 10 이상만 갤러리 저장 지원됨", Toast.LENGTH_SHORT).show();
                }

                // QR 코드 생성 화면으로 전환
                Intent intent = new Intent(CardCustomizeActivity.this, QrDisplayActivity.class);
                intent.putExtra("name", nameValue);
                intent.putExtra("phone", phoneValue);
                intent.putExtra("email", emailValue);
                intent.putExtra("company", companyValue);
                intent.putExtra("address", addressValue);
                intent.putExtra("tags", tagsValue);
                if (savedImageUri != null) {
                    intent.putExtra("imagePath", savedImageUri.toString());
                }
                startActivity(intent);
            }
        } catch (Exception e) {
            System.out.println("Error in handleFinishClick: " + e.getMessage());
            Toast.makeText(this, "처리 중 오류가 발생했습니다", Toast.LENGTH_SHORT).show();
        }
    }
}
