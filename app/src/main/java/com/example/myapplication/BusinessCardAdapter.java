package com.example.myapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.card.MaterialCardView;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class BusinessCardAdapter extends RecyclerView.Adapter<BusinessCardAdapter.CardViewHolder> {

    private List<BusinessCard> cards;
    private OnCardClickListener listener;

    public interface OnCardClickListener {
        void onCardClick(BusinessCard card);
    }

    public BusinessCardAdapter(List<BusinessCard> cards, OnCardClickListener listener) {
        this.cards = cards;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_business_card, parent, false);
        return new CardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CardViewHolder holder, int position) {
        BusinessCard card = cards.get(position);
        holder.bind(card);
    }

    @Override
    public int getItemCount() {
        return cards.size();
    }

    class CardViewHolder extends RecyclerView.ViewHolder {
        private MaterialCardView cardView;
        private TextView nameText;
        private TextView companyText;
        private TextView phoneText;
        private TextView emailText;
        private TextView tagsText;
        private TextView dateText;
        private TextView typeText;

        public CardViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = (MaterialCardView) itemView;
            nameText = itemView.findViewById(R.id.nameText);
            companyText = itemView.findViewById(R.id.companyText);
            phoneText = itemView.findViewById(R.id.phoneText);
            emailText = itemView.findViewById(R.id.emailText);
            tagsText = itemView.findViewById(R.id.tagsText);
            dateText = itemView.findViewById(R.id.dateText);
            typeText = itemView.findViewById(R.id.typeText);

            cardView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onCardClick(cards.get(position));
                }
            });
        }

        public void bind(BusinessCard card) {
            nameText.setText(card.getName());
            companyText.setText(card.getCompany());
            phoneText.setText(card.getPhone());
            emailText.setText(card.getEmail());
            
            // 태그 표시
            String tags = card.getTagsAsString();
            if (tags.isEmpty()) {
                tagsText.setVisibility(View.GONE);
            } else {
                tagsText.setVisibility(View.VISIBLE);
                tagsText.setText(tags);
            }
            
            // 날짜 표시
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd", Locale.getDefault());
            dateText.setText(dateFormat.format(new Date(card.getCreatedDate())));
            
            // 유형 표시
            typeText.setText(card.isReceived() ? "받은 명함" : "만든 명함");
        }
    }
} 