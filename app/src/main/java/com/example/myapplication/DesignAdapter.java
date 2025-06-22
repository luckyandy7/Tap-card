// 경로: com/example/myapplication/DesignAdapter.java

package com.example.myapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class DesignAdapter extends RecyclerView.Adapter<DesignAdapter.ViewHolder> {

    private List<Integer> designList;
    private OnDesignClickListener listener;

    public interface OnDesignClickListener {
        void onDesignClick(int imageResId);
    }

    public DesignAdapter(List<Integer> designList, OnDesignClickListener listener) {
        this.designList = designList;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        try {
            if (parent == null) {
                throw new IllegalArgumentException("Parent cannot be null");
            }
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_design, parent, false);
            return new ViewHolder(view);
        } catch (Exception e) {
            System.out.println("Error in onCreateViewHolder: " + e.getMessage());
            // 빈 뷰를 반환하여 크래시 방지
            View fallbackView = new View(parent.getContext());
            return new ViewHolder(fallbackView);
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        try {
            if (holder == null || designList == null || position < 0 || position >= designList.size()) {
                System.out.println("Invalid parameters in onBindViewHolder");
                return;
            }

            Integer imageResId = designList.get(position);
            if (imageResId == null) {
                System.out.println("ImageResId is null at position: " + position);
                return;
            }

            if (holder.imageView != null) {
                holder.imageView.setImageResource(imageResId);
                holder.imageView.setOnClickListener(v -> {
                    try {
                        if (listener != null) {
                            listener.onDesignClick(imageResId);
                        }
                    } catch (Exception e) {
                        System.out.println("Error in imageView click: " + e.getMessage());
                    }
                });
            }
        } catch (Exception e) {
            System.out.println("Error in onBindViewHolder: " + e.getMessage());
        }
    }

    @Override
    public int getItemCount() {
        try {
            return designList != null ? designList.size() : 0;
        } catch (Exception e) {
            System.out.println("Error in getItemCount: " + e.getMessage());
            return 0;
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public ViewHolder(View itemView) {
            super(itemView);
            try {
                if (itemView != null) {
                    imageView = itemView.findViewById(R.id.imageView); // item_design.xml 안에 있어야 함
                    if (imageView == null) {
                        System.out.println("Warning: imageView not found in item_design layout");
                    }
                }
            } catch (Exception e) {
                System.out.println("Error in ViewHolder constructor: " + e.getMessage());
            }
        }
    }
}
