package com.example.myapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.card.MaterialCardView;
import java.util.List;

public class TagFilterAdapter extends RecyclerView.Adapter<TagFilterAdapter.TagViewHolder> {

    private List<String> tags;
    private OnTagClickListener listener;
    private String selectedTag = null;

    public interface OnTagClickListener {
        void onTagClick(String tag);
    }

    public TagFilterAdapter(List<String> tags, OnTagClickListener listener) {
        this.tags = tags;
        this.listener = listener;
    }

    public void setSelectedTag(String selectedTag) {
        this.selectedTag = selectedTag;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TagViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_tag_filter, parent, false);
        return new TagViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TagViewHolder holder, int position) {
        String tag = tags.get(position);
        holder.bind(tag);
    }

    @Override
    public int getItemCount() {
        return tags.size();
    }

    class TagViewHolder extends RecyclerView.ViewHolder {
        private MaterialCardView cardView;
        private TextView tagText;

        public TagViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.tagCard);
            tagText = itemView.findViewById(R.id.tagText);

            cardView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    String tag = tags.get(position);
                    setSelectedTag("전체".equals(tag) ? null : tag);
                    listener.onTagClick(tag);
                }
            });
        }

        public void bind(String tag) {
            tagText.setText(tag);
            
            // 선택 상태에 따른 스타일 변경
            boolean isSelected = (selectedTag == null && "전체".equals(tag)) || 
                               (selectedTag != null && selectedTag.equals(tag));
            
            if (isSelected) {
                cardView.setCardBackgroundColor(itemView.getContext().getColor(R.color.primary_container));
                tagText.setTextColor(itemView.getContext().getColor(R.color.primary));
            } else {
                cardView.setCardBackgroundColor(itemView.getContext().getColor(R.color.surface));
                tagText.setTextColor(itemView.getContext().getColor(R.color.on_surface_variant));
            }
        }
    }
} 