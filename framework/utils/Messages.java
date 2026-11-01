package utils;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;

public final class Messages {

    /** Initialisé une seule fois au démarrage du FrontServlet. */
    private static volatile String baseName;

    private Messages() {}

    /**
     * À appeler une et une seule fois, au démarrage (dans FrontServlet.init()).
     * @param base chemin classpath du bundle SANS suffixe de langue.
     *             Ex: "messages", "i18n.messages", "lang/messages"...
     */
    public static void init(String base) {
        if (base == null || base.isBlank()) {
            throw new IllegalArgumentException("baseName ne peut pas être null ou vide");
        }
        Messages.baseName = base;
    }

     /**
     * Récupère un message localisé.
     * @param key La clé dans le fichier properties
     * @param locale La langue (ex: request.getLocale())
     * @param args Arguments optionnels pour remplacer {0}, {1}, etc.
     */
    public static String get(String key, Locale locale, Object... args) {
        ResourceBundle bundle = ResourceBundle.getBundle(baseName, locale);
        String pattern = bundle.getString(key);
        return (args.length == 0) ? pattern : MessageFormat.format(pattern, args);
    }
}