package org.example.domain;

public class ChatSettings {
    private final long chatId;
    private final boolean easyMlSpamCheck;
    private final boolean aiCheck;
    private final boolean eroticCheck;
    private final boolean curseCheck;

    public ChatSettings(long chatId,
                        boolean easyMlSpamCheck,
                        boolean aiCheck,
                        boolean eroticCheck,
                        boolean curseCheck) {
        this.chatId = chatId;
        this.easyMlSpamCheck = easyMlSpamCheck;
        this.aiCheck = aiCheck;
        this.eroticCheck = eroticCheck;
        this.curseCheck = curseCheck;
    }

    public long getChatId() { return chatId; }

    public boolean isEasyMlSpamCheck() { return easyMlSpamCheck; }
    public boolean isAiCheck() { return aiCheck; }
    public boolean isEroticCheck() { return eroticCheck; }
    public boolean isCurseCheck() { return curseCheck; }
}