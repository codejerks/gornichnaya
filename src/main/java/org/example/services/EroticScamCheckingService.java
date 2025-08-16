package org.example.services;

import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Микросервис проверки на эротический скам
 */
public class EroticScamCheckingService implements MessageChecking {
    private static final Pattern COMPILED_PATTERN = Pattern.compile(
            String.join("|", new String[]{
                    "(ищу\\s+парня|ищу\\s+девушку|одинок[а-я]{1})",
                    "(секс|интим|18\\+|обнаженн[а-я]{1})",
                    "(только\\s+сегодня|только\\s+для\\s+тебя)",
                    "(заработай\\s+\\d+\\s*₽)",
                    "(в\\s+профиль|по\\s+ссылке|личку|приват)",
                    "(my\\s+onlyfans|hot\\s+girl|click\\s+here|dm\\s+me)",
                    "(escort|fuck\\s+me|horny|wanna\\s+play|free\\s+nudes)",
                    "(💋|💦|🔥|🍑|👅|❤️|😘)"
            }),
            Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE
    );

    /**
     * Конструктор по умолчанию
     */
    public EroticScamCheckingService(){};

    /**
     * @param msg проверяемое сообщение
     * @return является ли сообщение допустимым
     */
    @Override
    public CompletableFuture<Boolean> check(Message msg) {
        String text = extractText(msg);
        if (text == null)
            return CompletableFuture.completedFuture(false);

        Matcher matcher = COMPILED_PATTERN.matcher(text);
        return CompletableFuture.completedFuture(! matcher.find());
    }
}
