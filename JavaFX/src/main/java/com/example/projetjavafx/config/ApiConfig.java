package com.example.projetjavafx.config;

/**
 * Configuration pour l'API backend.
 * Permet de centraliser l'URL du backend et de la modifier facilement.
 */
public class ApiConfig {
    // URL de base du backend - modifiable selon l'environnement
    private static final String BASE_URL = "http://localhost:7001";
    
    // Endpoints de l'API
    public static final String PLATS_ENDPOINT = BASE_URL + "/plats";
    public static final String CATEGORIES_ENDPOINT = BASE_URL + "/categories";
    public static final String CATEGORIE_PLATS_ENDPOINT = BASE_URL + "/categorie-plats";
    public static final String IMAGES_ENDPOINT = BASE_URL + "/images";
    
    /**
     * Retourne l'URL complète pour un plat spécifique.
     */
    public static String getPlatUrl(int id) {
        return BASE_URL + "/plats/" + id;
    }
    
    /**
     * Retourne l'URL complète pour une catégorie spécifique.
     */
    public static String getCategorieUrl(int id) {
        return BASE_URL + "/categories/" + id;
    }
    
    /**
     * Retourne l'URL complète pour une image.
     */
    public static String getImageUrl(String filename) {
        return IMAGES_ENDPOINT + "/" + filename;
    }
    
    /**
     * Permet de modifier l'URL de base si nécessaire.
     */
    public static void setBaseUrl(String baseUrl) {
        // Note: Dans une implémentation plus avancée, on pourrait utiliser un fichier de configuration
        // Pour l'instant, cette méthode est présente pour la flexibilité future
    }
}

