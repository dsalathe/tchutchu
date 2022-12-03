package ch.coachdave.tchutchu.gui;

import java.util.List;

public final class StringsEn {
    private StringsEn() {}

    // Nom des cartes
    public static final String BLACK_CARD = ":BLACK";
    public static final String BLUE_CARD = ":BLUE";
    public static final String GREEN_CARD = ":GREEN";
    public static final String ORANGE_CARD = ":ORANGE";
    public static final String RED_CARD = ":RED";
    public static final String VIOLET_CARD = ":VIOLET";
    public static final String WHITE_CARD = ":WHITE";
    public static final String YELLOW_CARD = ":YELLOW";
    public static final String LOCOMOTIVE_CARD = ":LOCOMOTIVE";
    public static final List<String> ALL_CARDS = List.of(BLACK_CARD,
            VIOLET_CARD, BLUE_CARD, GREEN_CARD, YELLOW_CARD, ORANGE_CARD, RED_CARD,
            WHITE_CARD, LOCOMOTIVE_CARD);
    // Étiquettes des boutons
    public static final String TICKETS = "Tickets";
    public static final String CARDS = "Cards";
    public static final String CHOOSE = "Choose";

    // Titre des fenêtres
    public static final String TICKETS_CHOICE = "choice of tickets";
    public static final String CARDS_CHOICE = "choice of cards";

    // Invites
    public static final String CHOOSE_TICKETS =
            "Choose at least %s ticket%s ampng those :";
    public static final String CHOOSE_CARDS =
            "Choose which cards to use to seize this route :";
    public static final String CHOOSE_ADDITIONAL_CARDS =
            "Choose additional cards to seize this tunnel (or none to renounce and pass your turn) :";

    // Informations concernant le déroulement de la partie
    public static final String WILL_PLAY_FIRST =
            "%s will play first.\n\n";
    public static final String KEPT_N_TICKETS =
            "%s kept %s ticket%s.\n";
    public static final String CAN_PLAY =
            "\nIt's %s's turn.\n";
    public static final String DREW_TICKETS =
            "%s drew %s ticket%s...\n";
    public static final String DREW_BLIND_CARD =
            "%s drew a card from the deck.\n";
    public static final String DREW_VISIBLE_CARD =
            "%s drew a %s card.\n";
    public static final String CLAIMED_ROUTE =
            "%s took the route %s with %s \n";
    public static final String ATTEMPTS_TUNNEL_CLAIM =
            "%s tries to take possession of %s with %s !\n";
    public static final String ADDITIONAL_CARDS_ARE =
            "Additional cards are: %s ";
    public static final String NO_ADDITIONAL_COST =
            "They do not involve any additional costs.\n";
    public static final String SOME_ADDITIONAL_COST =
            "They involve an additional cost of %s card%s.\n";
    public static final String DID_NOT_CLAIM_ROUTE =
            "%s renounced to seize the route %s.\n";
    public static final String LAST_TURN_BEGINS =
            "\n%s has only %s wagon%s left, last turn begins!\n";
    public static final String GETS_BONUS =
            "\n%s receives 10 additional points for the longest trail (%s).\n";
    public static final String WINS =
            "\n%s wins with %s point%s, against %s point%s !\n";
    public static final String DRAW =
            "\n%s are ex æqo with %s points !\n";

    // Statistiques des joueurs
    public static final String PLAYER_STATS =
            " %s :\n– %s tickets,\n– %s cards,\n– %s wagons,\n– %s points.";

    // Séparateurs textuels
    public static final String AND_SEPARATOR = " and ";
    public static final String EN_DASH_SEPARATOR = " – ";

    /**
     * Retourne une chaîne marquant le pluriel, ou la chaîne vide.
     * @param value la valeur déterminant la chaîne retournée
     * @return la chaîne vide si la valeur vaut ±1, la chaîne "s" sinon
     */
    public static String plural(int value) {
        return Math.abs(value) <= 1 ? "" : "s";
    }
}

