package com.example.projetjavafx.service;

import com.example.projetjavafx.model.CartItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Service de gestion du panier (logique métier).
 * Ne contient pas de code JavaFX.
 */
public class CartService {

    private final List<CartItem> cartItems = new ArrayList<>();

    /**
     * Retourne une vue non modifiable de la liste des items.
     * Les objets eux-mêmes restent modifiables.
     */
    public List<CartItem> getItems() {
        return Collections.unmodifiableList(cartItems);
    }

    public void ajouterAuPanier(String nom, double prix, List<String> options) {
        CartItem item = cartItems.stream()
                .filter(i -> i.estIdentique(nom, options))
                .findFirst()
                .orElse(null);

        if (item != null) {
            item.setQuantite(item.getQuantite() + 1);
        } else {
            cartItems.add(new CartItem(nom, prix, 1, options));
        }
    }

    public void supprimerItem(CartItem item) {
        cartItems.remove(item);
    }

    public double calculerTotal() {
        return cartItems.stream()
                .mapToDouble(i -> i.getPrixUnitaire() * i.getQuantite())
                .sum();
    }
}


