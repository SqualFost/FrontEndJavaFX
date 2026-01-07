package com.example.projetjavafx.service;

import com.example.projetjavafx.config.ApiConfig;
import com.example.projetjavafx.model.Categorie;
import com.example.projetjavafx.model.CategoriePlat;
import com.example.projetjavafx.model.Product;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service pour communiquer avec l'API backend.
 * Gère toutes les requêtes HTTP vers le backend.
 */
public class ApiService {
    private static final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();
    
    private static final ObjectMapper objectMapper = new ObjectMapper();
    
    /**
     * Récupère tous les plats depuis le backend.
     */
    public List<Product> getAllPlats() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(ApiConfig.PLATS_ENDPOINT))
                    .GET()
                    .timeout(Duration.ofSeconds(10))
                    .build();
            
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            
            if (response.statusCode() == 200) {
                return objectMapper.readValue(response.body(), 
                    new TypeReference<List<Product>>() {});
            } else {
                System.err.println("Erreur lors de la récupération des plats: " + response.statusCode());
                return new ArrayList<>();
            }
        } catch (IOException | InterruptedException e) {
            System.err.println("Erreur lors de la communication avec le backend: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    /**
     * Récupère un plat par son ID.
     */
    public Product getPlatById(int id) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(ApiConfig.getPlatUrl(id)))
                    .GET()
                    .timeout(Duration.ofSeconds(10))
                    .build();
            
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            
            if (response.statusCode() == 200) {
                return objectMapper.readValue(response.body(), Product.class);
            } else {
                System.err.println("Erreur lors de la récupération du plat: " + response.statusCode());
                return null;
            }
        } catch (IOException | InterruptedException e) {
            System.err.println("Erreur lors de la communication avec le backend: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Récupère toutes les catégories depuis le backend.
     */
    public List<Categorie> getAllCategories() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(ApiConfig.CATEGORIES_ENDPOINT))
                    .GET()
                    .timeout(Duration.ofSeconds(10))
                    .build();
            
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            
            if (response.statusCode() == 200) {
                return objectMapper.readValue(response.body(), 
                    new TypeReference<List<Categorie>>() {});
            } else {
                System.err.println("Erreur lors de la récupération des catégories: " + response.statusCode());
                return new ArrayList<>();
            }
        } catch (IOException | InterruptedException e) {
            System.err.println("Erreur lors de la communication avec le backend: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    /**
     * Récupère toutes les associations catégorie-plat.
     */
    public List<CategoriePlat> getAllCategoriePlats() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(ApiConfig.CATEGORIE_PLATS_ENDPOINT))
                    .GET()
                    .timeout(Duration.ofSeconds(10))
                    .build();
            
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            
            if (response.statusCode() == 200) {
                return objectMapper.readValue(response.body(), 
                    new TypeReference<List<CategoriePlat>>() {});
            } else {
                System.err.println("Erreur lors de la récupération des associations: " + response.statusCode());
                return new ArrayList<>();
            }
        } catch (IOException | InterruptedException e) {
            System.err.println("Erreur lors de la communication avec le backend: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    /**
     * Normalise une chaîne pour la comparaison (supprime accents, espaces, met en majuscules).
     */
    private String normalizeString(String str) {
        if (str == null) return "";
        return str.toUpperCase()
                .replace("É", "E")
                .replace("È", "E")
                .replace("Ê", "E")
                .replace("Ë", "E")
                .replace("À", "A")
                .replace("Â", "A")
                .replace("Ä", "A")
                .replace("Ù", "U")
                .replace("Û", "U")
                .replace("Ü", "U")
                .replace("Ô", "O")
                .replace("Ö", "O")
                .replace("Ç", "C")
                .replace(" ", "")
                .trim();
    }
    
    /**
     * Récupère les plats d'une catégorie spécifique.
     * Cette méthode filtre les plats en utilisant les associations catégorie-plat.
     */
    public List<Product> getPlatsByCategorie(String nomCategorie) {
        try {
            // Récupérer toutes les données nécessaires
            List<Categorie> categories = getAllCategories();
            List<CategoriePlat> associations = getAllCategoriePlats();
            List<Product> allPlats = getAllPlats();
            
            // Debug : afficher les catégories disponibles
            System.out.println("Catégories disponibles dans la BDD:");
            categories.forEach(c -> System.out.println("  - ID: " + c.getId() + ", Nom: '" + c.getNom() + "'"));
            System.out.println("Recherche de la catégorie: '" + nomCategorie + "'");
            
            // Normaliser le nom recherché
            String nomNormalise = normalizeString(nomCategorie);
            
            // Trouver l'ID de la catégorie (comparaison normalisée)
            Categorie categorie = categories.stream()
                    .filter(c -> {
                        String nomCategorieNormalise = normalizeString(c.getNom());
                        boolean match = nomCategorieNormalise.equals(nomNormalise);
                        if (match) {
                            System.out.println("Catégorie trouvée: '" + c.getNom() + "' (ID: " + c.getId() + ")");
                        }
                        return match;
                    })
                    .findFirst()
                    .orElse(null);
            
            if (categorie == null) {
                System.err.println("Catégorie non trouvée: '" + nomCategorie + "'");
                System.err.println("Catégories disponibles: " + 
                    categories.stream().map(Categorie::getNom).collect(Collectors.joining(", ")));
                return new ArrayList<>();
            }
            
            // Trouver les IDs des plats associés à cette catégorie
            List<Integer> platIds = associations.stream()
                    .filter(assoc -> assoc.getId_categorie() == categorie.getId())
                    .map(CategoriePlat::getId_plat)
                    .collect(Collectors.toList());
            
            System.out.println("Nombre de plats associés à cette catégorie: " + platIds.size());
            
            // Filtrer les plats
            List<Product> platsFiltres = allPlats.stream()
                    .filter(plat -> platIds.contains(plat.getId()))
                    .collect(Collectors.toList());
            
            System.out.println("Nombre de plats retournés: " + platsFiltres.size());
            return platsFiltres;
                    
        } catch (Exception e) {
            System.err.println("Erreur lors de la récupération des plats par catégorie: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
}

