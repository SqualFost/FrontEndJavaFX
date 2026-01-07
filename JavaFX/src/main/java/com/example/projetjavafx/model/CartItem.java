package com.example.projetjavafx.model;

import java.util.List;
import java.util.Objects;

public class CartItem {

    private String nom;
    private double prixUnitaire;
    private int quantite;
    private List<String> options;

    public CartItem(String nom, double prixUnitaire, int quantite, List<String> options) {
        this.nom = nom;
        this.prixUnitaire = prixUnitaire;
        this.quantite = quantite;
        this.options = options;
    }

    public boolean estIdentique(String nom, List<String> options) {
        return Objects.equals(this.nom, nom) && Objects.equals(this.options, options);
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public double getPrixUnitaire() {
        return prixUnitaire;
    }

    public void setPrixUnitaire(double prixUnitaire) {
        this.prixUnitaire = prixUnitaire;
    }

    public int getQuantite() {
        return quantite;
    }

    public void setQuantite(int quantite) {
        this.quantite = quantite;
    }

    public List<String> getOptions() {
        return options;
    }

    public void setOptions(List<String> options) {
        this.options = options;
    }
}


