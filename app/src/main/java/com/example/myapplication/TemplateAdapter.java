package com.example.myapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class TemplateAdapter extends RecyclerView.Adapter<TemplateAdapter.TemplateViewHolder> {

    private List<Template> templates;
    private OnTemplateSelectedListener listener;
    private int selectedPosition = 0; // 기본으로 첫 번째 템플릿 선택

    public interface OnTemplateSelectedListener {
        void onTemplateSelected(Template template, int position);
    }

    public TemplateAdapter(List<Template> templates, OnTemplateSelectedListener listener) {
        this.templates = templates;
        this.listener = listener;
        // 첫 번째 템플릿을 기본 선택
        if (!templates.isEmpty()) {
            templates.get(0).setSelected(true);
        }
    }

    @NonNull
    @Override
    public TemplateViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_template, parent, false);
        return new TemplateViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TemplateViewHolder holder, int position) {
        Template template = templates.get(position);
        holder.bind(template, position);
    }

    @Override
    public int getItemCount() {
        return templates.size();
    }

    public void selectTemplate(int position) {
        if (position >= 0 && position < templates.size()) {
            // 이전 선택 해제
            if (selectedPosition >= 0 && selectedPosition < templates.size()) {
                templates.get(selectedPosition).setSelected(false);
                notifyItemChanged(selectedPosition);
            }
            
            // 새 선택
            selectedPosition = position;
            templates.get(position).setSelected(true);
            notifyItemChanged(position);
            
            if (listener != null) {
                listener.onTemplateSelected(templates.get(position), position);
            }
        }
    }

    public Template getSelectedTemplate() {
        if (selectedPosition >= 0 && selectedPosition < templates.size()) {
            return templates.get(selectedPosition);
        }
        return null;
    }

    class TemplateViewHolder extends RecyclerView.ViewHolder {
        private FrameLayout templatePreviewContainer;
        private TextView templateName;
        private TextView templateDescription;
        private View selectionIndicator;

        public TemplateViewHolder(@NonNull View itemView) {
            super(itemView);
            templatePreviewContainer = itemView.findViewById(R.id.templatePreviewContainer);
            templateName = itemView.findViewById(R.id.templateName);
            templateDescription = itemView.findViewById(R.id.templateDescription);
            selectionIndicator = itemView.findViewById(R.id.selectionIndicator);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    selectTemplate(position);
                }
            });
        }

        public void bind(Template template, int position) {
            templateName.setText(template.getName());
            templateDescription.setText(template.getDescription());
            
            // 선택 상태 표시
            selectionIndicator.setVisibility(template.isSelected() ? View.VISIBLE : View.INVISIBLE);
            
            // 템플릿 미리보기 로드
            loadTemplatePreview(template);
        }

        private void loadTemplatePreview(Template template) {
            try {
                templatePreviewContainer.removeAllViews();
                
                // 템플릿 레이아웃을 미리보기 컨테이너에 인플레이트
                LayoutInflater inflater = LayoutInflater.from(itemView.getContext());
                View templateView = inflater.inflate(template.getLayoutResId(), templatePreviewContainer, false);
                
                // 미리보기용 샘플 데이터 설정
                setPreviewData(templateView);
                
                // 크기 조정을 위한 스케일 설정
                templateView.setScaleX(0.6f);
                templateView.setScaleY(0.6f);
                
                templatePreviewContainer.addView(templateView);
            } catch (Exception e) {
                System.out.println("템플릿 미리보기 로드 오류: " + e.getMessage());
            }
        }

        private void setPreviewData(View templateView) {
            try {
                // 각 템플릿의 TextView들에 샘플 데이터 설정
                TextView nameView = templateView.findViewById(R.id.nameText);
                TextView phoneView = templateView.findViewById(R.id.phoneText);
                TextView emailView = templateView.findViewById(R.id.emailText);
                TextView companyView = templateView.findViewById(R.id.companyText);
                TextView addressView = templateView.findViewById(R.id.addressText);

                if (nameView != null) nameView.setText("홍길동");
                if (phoneView != null) phoneView.setText("010-1234-5678");
                if (emailView != null) emailView.setText("hong@example.com");
                if (companyView != null) companyView.setText("샘플 회사");
                if (addressView != null) addressView.setText("서울시");
            } catch (Exception e) {
                System.out.println("미리보기 데이터 설정 오류: " + e.getMessage());
            }
        }
    }
} 