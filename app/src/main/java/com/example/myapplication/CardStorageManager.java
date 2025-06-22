package com.example.myapplication;

import android.content.Context;
import android.content.SharedPreferences;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CardStorageManager {
    private static final String PREF_NAME = "business_cards";
    private static final String KEY_CARDS = "cards";
    private SharedPreferences preferences;
    private Context context;

    public CardStorageManager(Context context) {
        this.context = context;
        this.preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    // 명함 저장
    public void saveCard(BusinessCard card) {
        try {
            List<BusinessCard> cards = getAllCards();
            cards.add(card);
            saveAllCards(cards);
        } catch (Exception e) {
            System.out.println("Error saving card: " + e.getMessage());
        }
    }

    // 모든 명함 조회
    public List<BusinessCard> getAllCards() {
        List<BusinessCard> cards = new ArrayList<>();
        try {
            String cardsJson = preferences.getString(KEY_CARDS, "[]");
            JSONArray jsonArray = new JSONArray(cardsJson);
            
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonCard = jsonArray.getJSONObject(i);
                BusinessCard card = cardFromJson(jsonCard);
                if (card != null) {
                    cards.add(card);
                }
            }
        } catch (JSONException e) {
            System.out.println("Error loading cards: " + e.getMessage());
        }
        return cards;
    }

    // 태그별 명함 조회
    public List<BusinessCard> getCardsByTag(String tag) {
        List<BusinessCard> allCards = getAllCards();
        List<BusinessCard> filteredCards = new ArrayList<>();
        
        for (BusinessCard card : allCards) {
            if (card.hasTag(tag)) {
                filteredCards.add(card);
            }
        }
        
        return filteredCards;
    }

    // 받은 명함만 조회
    public List<BusinessCard> getReceivedCards() {
        List<BusinessCard> allCards = getAllCards();
        List<BusinessCard> receivedCards = new ArrayList<>();
        
        for (BusinessCard card : allCards) {
            if (card.isReceived()) {
                receivedCards.add(card);
            }
        }
        
        return receivedCards;
    }

    // 만든 명함만 조회
    public List<BusinessCard> getCreatedCards() {
        List<BusinessCard> allCards = getAllCards();
        List<BusinessCard> createdCards = new ArrayList<>();
        
        for (BusinessCard card : allCards) {
            if (!card.isReceived()) {
                createdCards.add(card);
            }
        }
        
        return createdCards;
    }

    // 모든 태그 조회
    public List<String> getAllTags() {
        List<String> allTags = new ArrayList<>();
        List<BusinessCard> cards = getAllCards();
        
        for (BusinessCard card : cards) {
            for (String tag : card.getTags()) {
                if (!allTags.contains(tag)) {
                    allTags.add(tag);
                }
            }
        }
        
        return allTags;
    }

    // 명함 삭제
    public void deleteCard(BusinessCard cardToDelete) {
        try {
            List<BusinessCard> cards = getAllCards();
            List<BusinessCard> filteredCards = new ArrayList<>();
            for (BusinessCard card : cards) {
                if (!(card.getName().equals(cardToDelete.getName()) && 
                      card.getPhone().equals(cardToDelete.getPhone()) &&
                      card.getCreatedDate() == cardToDelete.getCreatedDate())) {
                    filteredCards.add(card);
                }
            }
            saveAllCards(filteredCards);
        } catch (Exception e) {
            System.out.println("Error deleting card: " + e.getMessage());
        }
    }

    // 명함 업데이트
    public void updateCard(BusinessCard oldCard, BusinessCard newCard) {
        try {
            List<BusinessCard> cards = getAllCards();
            for (int i = 0; i < cards.size(); i++) {
                BusinessCard card = cards.get(i);
                if (card.getName().equals(oldCard.getName()) && 
                    card.getPhone().equals(oldCard.getPhone()) &&
                    card.getCreatedDate() == oldCard.getCreatedDate()) {
                    cards.set(i, newCard);
                    break;
                }
            }
            saveAllCards(cards);
        } catch (Exception e) {
            System.out.println("Error updating card: " + e.getMessage());
        }
    }

    // 모든 명함 저장 (내부 메서드)
    private void saveAllCards(List<BusinessCard> cards) {
        try {
            JSONArray jsonArray = new JSONArray();
            for (BusinessCard card : cards) {
                JSONObject jsonCard = cardToJson(card);
                if (jsonCard != null) {
                    jsonArray.put(jsonCard);
                }
            }
            
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString(KEY_CARDS, jsonArray.toString());
            editor.apply();
        } catch (Exception e) {
            System.out.println("Error saving all cards: " + e.getMessage());
        }
    }

    // BusinessCard를 JSON으로 변환
    private JSONObject cardToJson(BusinessCard card) {
        try {
            JSONObject json = new JSONObject();
            json.put("name", card.getName());
            json.put("phone", card.getPhone());
            json.put("email", card.getEmail());
            json.put("company", card.getCompany());
            json.put("address", card.getAddress());
            json.put("createdDate", card.getCreatedDate());
            json.put("imagePath", card.getImagePath());
            json.put("templateId", card.getTemplateId());
            json.put("qrCode", card.getQrCode());
            json.put("isReceived", card.isReceived());
            
            // 태그 배열
            JSONArray tagsArray = new JSONArray();
            for (String tag : card.getTags()) {
                tagsArray.put(tag);
            }
            json.put("tags", tagsArray);
            
            return json;
        } catch (JSONException e) {
            System.out.println("Error converting card to JSON: " + e.getMessage());
            return null;
        }
    }

    // JSON에서 BusinessCard로 변환
    private BusinessCard cardFromJson(JSONObject json) {
        try {
            BusinessCard card = new BusinessCard();
            card.setName(json.optString("name", ""));
            card.setPhone(json.optString("phone", ""));
            card.setEmail(json.optString("email", ""));
            card.setCompany(json.optString("company", ""));
            card.setAddress(json.optString("address", ""));
            card.setCreatedDate(json.optLong("createdDate", System.currentTimeMillis()));
            card.setImagePath(json.optString("imagePath", ""));
            card.setTemplateId(json.optInt("templateId", 1));
            card.setQrCode(json.optString("qrCode", ""));
            card.setReceived(json.optBoolean("isReceived", false));
            
            // 태그 배열 복원
            JSONArray tagsArray = json.optJSONArray("tags");
            if (tagsArray != null) {
                List<String> tags = new ArrayList<>();
                for (int i = 0; i < tagsArray.length(); i++) {
                    try {
                        tags.add(tagsArray.getString(i));
                    } catch (JSONException e) {
                        // 개별 태그 오류는 무시하고 계속 진행
                    }
                }
                card.setTags(tags);
            }
            
            return card;
        } catch (Exception e) {
            System.out.println("Error converting JSON to card: " + e.getMessage());
            return null;
        }
    }

    // 저장소 비우기 (디버그용)
    public void clearAll() {
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.apply();
    }

    // 명함 개수 조회
    public int getCardCount() {
        return getAllCards().size();
    }

    // 태그 검색
    public List<BusinessCard> searchByTag(String searchTag) {
        List<BusinessCard> results = new ArrayList<>();
        List<BusinessCard> allCards = getAllCards();
        
        for (BusinessCard card : allCards) {
            for (String tag : card.getTags()) {
                if (tag.toLowerCase().contains(searchTag.toLowerCase())) {
                    results.add(card);
                    break;
                }
            }
        }
        
        return results;
    }
} 