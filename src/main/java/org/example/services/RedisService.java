package org.example.services;

import org.telegram.telegrambots.meta.api.objects.Message;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.concurrent.CompletableFuture;

/**
 * Микросервис взаимодействия с Реддисом
 * <p>
 *     {@literal @}annoying_rusk: Ничего не менял
 *     (только имплементацию добавил, но на методе стоит заглушка)
 *     Очень сырой, но легко адаптировать
 * </p>
 */
public class RedisService implements MessageChecking {
    private final JedisPool pool;
    private static final int TTL_SECONDS = 3600; // время жизни ключа – 1 час

    public RedisService(String rhost, String rport) {
        String host = System.getenv().getOrDefault("REDIS_HOST", rhost);
        int port = Integer.parseInt(System.getenv().getOrDefault("REDIS_PORT", rport));
        this.pool = new JedisPool(host, port);
    }
    //проверяем полученный текст, если такой есть уже в редисе возвращаем тру,

    public boolean checkExist(String text) {
        try (Jedis jedis = pool.getResource()) {
            String textKey = key(text);
            // Проверка наличия текста в Redis
            return jedis.exists(textKey);
        }
    }
    // если такоего нет, но мы определили
    // что это спам - вносим в редис
    public void addInReddis(String text){
        try (Jedis jedis = pool.getResource()) {
            String textKey = key(text);
                // Сохраняем ключ с TTL
            jedis.setex(textKey, TTL_SECONDS, text.toString());
        }
    }
    public boolean isRateLimited(Long userId, int limit, int windowSeconds) {
        String rateKey = "rate:" + userId;
        try (Jedis jedis = pool.getResource()) {
            Long count = jedis.incr(rateKey);
            if (count == 1) {
                jedis.expire(rateKey, windowSeconds);
            }
            return count > limit;
        }
    }
    private String key(Object value) {
        return "user:" + value.toString();
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public String getIdentifier() {
        return null;
    }

    /**
     * Заглушка
     *
     * @param msg проверяемое сообщение
     * @return является ли сообщение допустимым
     */
    @Override
    public CompletableFuture<Boolean> check(Message msg) {
        return null;
    }
}
