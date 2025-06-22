package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

public class CardStorageActivity extends AppCompatActivity {

    private RecyclerView cardsRecyclerView, tagsRecyclerView;
    private TextView cardsCountText;
    private LinearLayout emptyStateLayout;
    private MaterialButton btnAllCards, btnCreatedCards, btnReceivedCards, btnBack, btnCreateNewCard;

    private CardStorageManager storageManager;
    private BusinessCardAdapter cardAdapter;
    private TagFilterAdapter tagAdapter;
    
    private List<BusinessCard> currentCards;
    private List<String> availableTags;
    private String selectedTag = null;
    private FilterType currentFilter = FilterType.ALL;

    private enum FilterType {
        ALL, CREATED, RECEIVED
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_storage);

        initializeViews();
        setupStorageManager();
        setupRecyclerViews();
        setupButtons();
        loadCards();
    }

    private void initializeViews() {
        cardsRecyclerView = findViewById(R.id.cardsRecyclerView);
        tagsRecyclerView = findViewById(R.id.tagsRecyclerView);
        cardsCountText = findViewById(R.id.cardsCountText);
        emptyStateLayout = findViewById(R.id.emptyStateLayout);
        
        btnAllCards = findViewById(R.id.btnAllCards);
        btnCreatedCards = findViewById(R.id.btnCreatedCards);
        btnReceivedCards = findViewById(R.id.btnReceivedCards);
        btnBack = findViewById(R.id.btnBack);
        btnCreateNewCard = findViewById(R.id.btnCreateNewCard);
    }

    private void setupStorageManager() {
        storageManager = new CardStorageManager(this);
    }

    private void setupRecyclerViews() {
        // 명함 목록 RecyclerView
        cardsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        currentCards = new ArrayList<>();
        cardAdapter = new BusinessCardAdapter(currentCards, this::onCardClick);
        cardsRecyclerView.setAdapter(cardAdapter);

        // 태그 필터 RecyclerView (수평)
        tagsRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        availableTags = new ArrayList<>();
        tagAdapter = new TagFilterAdapter(availableTags, this::onTagClick);
        tagsRecyclerView.setAdapter(tagAdapter);
    }

    private void setupButtons() {
        btnAllCards.setOnClickListener(v -> {
            currentFilter = FilterType.ALL;
            selectedTag = null;
            updateFilterButtons();
            loadCards();
        });

        btnCreatedCards.setOnClickListener(v -> {
            currentFilter = FilterType.CREATED;
            selectedTag = null;
            updateFilterButtons();
            loadCards();
        });

        btnReceivedCards.setOnClickListener(v -> {
            currentFilter = FilterType.RECEIVED;
            selectedTag = null;
            updateFilterButtons();
            loadCards();
        });

        btnBack.setOnClickListener(v -> finish());

        btnCreateNewCard.setOnClickListener(v -> {
            Intent intent = new Intent(this, InfoInputActivity.class);
            startActivity(intent);
        });
    }

    private void updateFilterButtons() {
        // 모든 버튼 리셋
        resetFilterButton(btnAllCards);
        resetFilterButton(btnCreatedCards);
        resetFilterButton(btnReceivedCards);

        // 선택된 버튼 활성화
        MaterialButton selectedButton = null;
        switch (currentFilter) {
            case ALL:
                selectedButton = btnAllCards;
                break;
            case CREATED:
                selectedButton = btnCreatedCards;
                break;
            case RECEIVED:
                selectedButton = btnReceivedCards;
                break;
        }

        if (selectedButton != null) {
            activateFilterButton(selectedButton);
        }
    }

    private void resetFilterButton(MaterialButton button) {
        button.setStrokeColorResource(R.color.outline);
        button.setTextColor(getResources().getColor(R.color.on_surface_variant, null));
        button.setBackgroundTintList(null);
    }

    private void activateFilterButton(MaterialButton button) {
        button.setStrokeColorResource(R.color.primary);
        button.setTextColor(getResources().getColor(R.color.primary, null));
        button.setBackgroundTintList(getResources().getColorStateList(R.color.primary_container, null));
    }

    private void loadCards() {
        try {
            List<BusinessCard> allCards;
            
            // 필터에 따라 명함 로드
            switch (currentFilter) {
                case CREATED:
                    allCards = storageManager.getCreatedCards();
                    break;
                case RECEIVED:
                    allCards = storageManager.getReceivedCards();
                    break;
                default:
                    allCards = storageManager.getAllCards();
                    break;
            }

            // 태그 필터 적용
            if (selectedTag != null && !selectedTag.isEmpty()) {
                currentCards.clear();
                for (BusinessCard card : allCards) {
                    if (card.hasTag(selectedTag)) {
                        currentCards.add(card);
                    }
                }
            } else {
                currentCards.clear();
                currentCards.addAll(allCards);
            }

            // 태그 목록 업데이트
            updateAvailableTags();

            // UI 업데이트
            updateUI();

        } catch (Exception e) {
            System.out.println("Error loading cards: " + e.getMessage());
            Toast.makeText(this, "명함 로드 중 오류가 발생했습니다", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateAvailableTags() {
        availableTags.clear();
        
        // "전체" 태그 추가
        availableTags.add("전체");
        
        // 모든 고유 태그 수집
        List<String> allTags = storageManager.getAllTags();
        for (String tag : allTags) {
            if (!availableTags.contains(tag)) {
                availableTags.add(tag);
            }
        }
        
        tagAdapter.notifyDataSetChanged();
    }

    private void updateUI() {
        // 명함 수 업데이트
        String countText = String.format("명함 %d개", currentCards.size());
        cardsCountText.setText(countText);

        // 빈 상태 처리
        if (currentCards.isEmpty()) {
            cardsRecyclerView.setVisibility(View.GONE);
            emptyStateLayout.setVisibility(View.VISIBLE);
        } else {
            cardsRecyclerView.setVisibility(View.VISIBLE);
            emptyStateLayout.setVisibility(View.GONE);
        }

        // 어댑터 업데이트
        cardAdapter.notifyDataSetChanged();
    }

    private void onCardClick(BusinessCard card) {
        // 명함 상세보기로 이동
        Intent intent = new Intent(this, CardDetailActivity.class);
        intent.putExtra("card", card);
        startActivity(intent);
    }

    private void onTagClick(String tag) {
        if ("전체".equals(tag)) {
            selectedTag = null;
        } else {
            selectedTag = tag;
        }
        
        // 태그 필터 어댑터에 선택 상태 업데이트
        tagAdapter.setSelectedTag(selectedTag);
        
        // 명함 다시 로드
        loadCards();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 화면이 다시 보일 때마다 명함 목록 새로고침
        loadCards();
    }
} 