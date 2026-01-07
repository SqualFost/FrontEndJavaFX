package com.example.projetjavafx.model;

/**
 * Modèle CategoriePlat correspondant au modèle Categorie_Plat du backend.
 * Représente l'association entre une catégorie et un plat.
 */
public class CategoriePlat {
    private int id;
    private int id_plat;
    private int id_categorie;

    public CategoriePlat() {
    }

    public CategoriePlat(int id, int id_plat, int id_categorie) {
        this.id = id;
        this.id_plat = id_plat;
        this.id_categorie = id_categorie;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId_plat() {
        return id_plat;
    }

    public void setId_plat(int id_plat) {
        this.id_plat = id_plat;
    }

    public int getId_categorie() {
        return id_categorie;
    }

    public void setId_categorie(int id_categorie) {
        this.id_categorie = id_categorie;
    }
}

