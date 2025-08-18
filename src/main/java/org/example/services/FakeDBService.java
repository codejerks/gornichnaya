package org.example.services;

import java.util.HashMap;

public class FakeDBService {

    private HashMap<Long, String> chats = new HashMap<>();
    private HashMap<String, Boolean> chatSettings = new HashMap<>();

    public FakeDBService(){
        chats.put((long)12345, "Чат фанатов My little pony");

        chatSettings.put("ai-vs-human", true);
        chatSettings.put("curse-checking", true);
        chatSettings.put("easy-ml-checking", true);
        chatSettings.put("erotic-scam", true);
    }

    public HashMap<Long, String> getChatsByUserId(long userId){
        return chats;
    }

    public void addChatForUser(long chatId, String chatName, long userId){
        chats.put(chatId, chatName);
    }

    public HashMap<String, Boolean> getChatSettings(long chatId){ return chatSettings; }

    public void changeChatSetting(long chatId, String settingToChange){
        boolean previousValue = chatSettings.get(settingToChange);
        chatSettings.put(settingToChange, ! previousValue);
    }
}
