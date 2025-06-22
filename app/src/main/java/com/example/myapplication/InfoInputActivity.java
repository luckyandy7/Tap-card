package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import java.util.ArrayList;
import java.util.List;

public class InfoInputActivity extends AppCompatActivity implements TemplateAdapter.OnTemplateSelectedListener {

    private RecyclerView templatesRecyclerView;
    private TemplateAdapter templateAdapter;
    private List<Template> templates;
    private FrameLayout previewContainer;
    
    // Input fields
    private TextInputEditText editName, editPhone, editEmail, editCompany, editAddress, editTags;
    
    // Current selected template
    private Template selectedTemplate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_input);

        try {
            initializeViews();
            setupTemplates();
            setupRecyclerView();
            setupInputListeners();
            setupButtons();
            
            // 첫 번째 템플릿으로 기본 설정
            if (!templates.isEmpty()) {
                onTemplateSelected(templates.get(0), 0);
            }
        } catch (Exception e) {
            System.out.println("Error in InfoInputActivity onCreate: " + e.getMessage());
            Toast.makeText(this, "초기화 오류", Toast.LENGTH_SHORT).show();
        }
    }

    private void initializeViews() {
        templatesRecyclerView = findViewById(R.id.templatesRecyclerView);
        previewContainer = findViewById(R.id.previewContainer);
        
        editName = findViewById(R.id.editName);
        editPhone = findViewById(R.id.editPhone);
        editEmail = findViewById(R.id.editEmail);
        editCompany = findViewById(R.id.editCompany);
        editAddress = findViewById(R.id.editAddress);
        editTags = findViewById(R.id.editTags);
    }

    private void setupTemplates() {
        templates = new ArrayList<>();
        templates.add(new Template(1, "템플릿 1", "미니멀 & 클린", R.layout.card_template_1_minimal));
        templates.add(new Template(2, "템플릿 2", "다크 & 골드", R.layout.card_template_2_dark));
        templates.add(new Template(3, "템플릿 3", "자연 & 그린", R.layout.card_template_3_nature));
        templates.add(new Template(4, "템플릿 4", "웜 & 오렌지", R.layout.card_template_4_warm));
        templates.add(new Template(5, "템플릿 5", "모던 퍼플", R.layout.card_template_5_modern));
        templates.add(new Template(6, "템플릿 6", "핑크 글래머", R.layout.card_template_6_glamour));
        templates.add(new Template(7, "템플릿 7", "사이언 테크", R.layout.card_template_7_tech));
        templates.add(new Template(8, "템플릿 8", "골드 엘레강스", R.layout.card_template_8_elegant));
    }

    private void setupRecyclerView() {
        templateAdapter = new TemplateAdapter(templates, this);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        templatesRecyclerView.setLayoutManager(layoutManager);
        templatesRecyclerView.setAdapter(templateAdapter);
    }

    private void setupInputListeners() {
        // Name input listener
        if (editName != null) {
            editName.addTextChangedListener(new SimpleTextWatcher() {
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    updatePreview();
                }
            });
        }

        // Phone input listener
        if (editPhone != null) {
            editPhone.addTextChangedListener(new SimpleTextWatcher() {
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    updatePreview();
                }
            });
        }

        // Email input listener
        if (editEmail != null) {
            editEmail.addTextChangedListener(new SimpleTextWatcher() {
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    updatePreview();
                }
            });
        }

        // Company input listener
        if (editCompany != null) {
            editCompany.addTextChangedListener(new SimpleTextWatcher() {
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    updatePreview();
                }
            });
        }

        // Address input listener
        if (editAddress != null) {
            editAddress.addTextChangedListener(new SimpleTextWatcher() {
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    updatePreview();
                }
            });
        }

        // Tags input listener
        if (editTags != null) {
            editTags.addTextChangedListener(new SimpleTextWatcher() {
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    updatePreview();
                }
            });
        }
    }

    private void setupButtons() {
        MaterialButton btnCancel = findViewById(R.id.btnCancel);
        MaterialButton btnNext = findViewById(R.id.btnNext);

        if (btnCancel != null) {
            btnCancel.setOnClickListener(v -> finish());
        }

        if (btnNext != null) {
            btnNext.setOnClickListener(v -> {
                try {
                    if (validateInputs()) {
                        goToNextStep();
                    }
                } catch (Exception e) {
                    System.out.println("Error in btnNext onClick: " + e.getMessage());
                    Toast.makeText(this, "다음 단계로 이동 중 오류가 발생했습니다", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    public void onTemplateSelected(Template template, int position) {
        selectedTemplate = template;
        updatePreview();
    }

    private void updatePreview() {
        if (selectedTemplate == null || previewContainer == null) {
            return;
        }

        try {
            previewContainer.removeAllViews();
            
            // 선택된 템플릿 레이아웃을 인플레이트
            LayoutInflater inflater = LayoutInflater.from(this);
            View templateView = inflater.inflate(selectedTemplate.getLayoutResId(), previewContainer, false);
            
            // 사용자 입력 데이터로 템플릿 업데이트
            updateTemplateWithUserData(templateView);
            
            previewContainer.addView(templateView);
        } catch (Exception e) {
            System.out.println("Error updating preview: " + e.getMessage());
        }
    }

    private void updateTemplateWithUserData(View templateView) {
        try {
            String name = getInputText(editName, "홍길동");
            String phone = getInputText(editPhone, "010-1234-5678");
            String email = getInputText(editEmail, "hong@example.com");
            String company = getInputText(editCompany, "샘플 회사");
            String address = getInputText(editAddress, "서울시");

            TextView nameView = templateView.findViewById(R.id.nameText);
            TextView phoneView = templateView.findViewById(R.id.phoneText);
            TextView emailView = templateView.findViewById(R.id.emailText);
            TextView companyView = templateView.findViewById(R.id.companyText);
            TextView addressView = templateView.findViewById(R.id.addressText);

            if (nameView != null) nameView.setText(name);
            if (phoneView != null) phoneView.setText(phone);
            if (emailView != null) emailView.setText(email);
            if (companyView != null) companyView.setText(company);
            if (addressView != null) addressView.setText(address);
        } catch (Exception e) {
            System.out.println("Error updating template with user data: " + e.getMessage());
        }
    }

    private String getInputText(TextInputEditText editText, String defaultValue) {
        if (editText != null && editText.getText() != null) {
            String text = editText.getText().toString().trim();
            return text.isEmpty() ? defaultValue : text;
        }
        return defaultValue;
    }

    private boolean validateInputs() {
        if (editName == null || editName.getText() == null || editName.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "이름을 입력하세요", Toast.LENGTH_SHORT).show();
            if (editName != null) editName.requestFocus();
            return false;
        }

        if (editPhone == null || editPhone.getText() == null || editPhone.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "전화번호를 입력하세요", Toast.LENGTH_SHORT).show();
            if (editPhone != null) editPhone.requestFocus();
            return false;
        }

        return true;
    }

    private void goToNextStep() {
        Intent intent = new Intent(InfoInputActivity.this, CardCustomizeActivity.class);
        
        // 사용자 입력 데이터 전달
        intent.putExtra("name", getInputText(editName, ""));
        intent.putExtra("phone", getInputText(editPhone, ""));
        intent.putExtra("email", getInputText(editEmail, ""));
        intent.putExtra("company", getInputText(editCompany, ""));
        intent.putExtra("address", getInputText(editAddress, ""));
        intent.putExtra("tags", getInputText(editTags, ""));
        
        // 선택된 템플릿 정보 전달
        if (selectedTemplate != null) {
            intent.putExtra("templateId", selectedTemplate.getId());
            intent.putExtra("templateLayoutRes", selectedTemplate.getLayoutResId());
        }

        startActivity(intent);
    }

    // TextWatcher의 간단한 구현
    private abstract static class SimpleTextWatcher implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void afterTextChanged(Editable s) {}
    }
}
