package ch.coachdave.tchutchu.gui;

import ch.coachdave.tchutchu.SortedBag;
import ch.coachdave.tchutchu.game.Card;
import ch.coachdave.tchutchu.game.Route;
import ch.coachdave.tchutchu.game.Station;
import ch.coachdave.tchutchu.game.Trail;

import static ch.coachdave.tchutchu.gui.StringsFr.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public final class Info {
    private final String playerName;

    public Info(String playerName) {
        this.playerName = playerName;
    }
    
    public String getPlayerName() {
        return playerName;
    }

    private static String getRouteName(Route route) {
        return route.stationsJava().stream().map(Station::name)
                .collect(Collectors.joining(EN_DASH_SEPARATOR));
    }

    private static String getCardsName(SortedBag<Card> cards) {
        List<String> cardNames = new ArrayList<>();
        for (Card c : cards.toSet()) {
            int count = cards.countOf(c);
            cardNames.add(count + " " + ALL_CARDS.get(c.ordinal()) + plural(count));
        }
        if (cardNames.size() == 0) {
            return "";
        }
        if (cardNames.size() == 1) {
            return cardNames.get(0);
        }
        return String.join(", ", cardNames.subList(0, cardNames.size() - 1)) + AND_SEPARATOR +
                cardNames.get(cardNames.size() - 1);
    }

    public static String cardName(Card card, int count) {
        return ALL_CARDS.get(card.ordinal()) + plural(count);
    }

    private static String getTrailName(Trail trail) {
        return trail.station1().get() + EN_DASH_SEPARATOR + trail.station2().get();
    }

    public static String draw(List<String> playerNames, int points) { // REFACTOR FOR MORE PLAYERS
        return String.format(DRAW, String.join(AND_SEPARATOR, playerNames), points);
    }

    public String willPlayFirst() {
        return String.format(WILL_PLAY_FIRST, playerName);
    }

    public String keptTickets(int count) {
        return String.format(KEPT_N_TICKETS, playerName, count, plural(count));
    }

    public String canPlay() {
        return String.format(CAN_PLAY, playerName);
    }

    public String drewTickets(int count) {
        return String.format(DREW_TICKETS, playerName, count, plural(count));
    }

    public String drewBlindCard() {
        return String.format(DREW_BLIND_CARD, playerName);
    }

    public String drewVisibleCard(Card card) {
        return String.format(DREW_VISIBLE_CARD, playerName, ALL_CARDS.get(card.ordinal()));
    }

    public String claimedRoute(Route route, SortedBag<Card> cards) {
        return String.format(CLAIMED_ROUTE, playerName, getRouteName(route), getCardsName(cards));
    }

    public String attemptsTunnelClaim(Route route, SortedBag<Card> initialCards) {
        return String.format(ATTEMPTS_TUNNEL_CLAIM, playerName, getRouteName(route),
                getCardsName(initialCards));
    }

    public String drewAdditionalCards(SortedBag<Card> drawnCards, int additionalCost) {
        return String.format(ADDITIONAL_CARDS_ARE, getCardsName(drawnCards)) +
                (additionalCost == 0 ? NO_ADDITIONAL_COST :
                        String.format(SOME_ADDITIONAL_COST, additionalCost, plural(additionalCost)));
    }

    public String didNotClaimRoute(Route route) {
        return String.format(DID_NOT_CLAIM_ROUTE, playerName, getRouteName(route));
    }

    public String lastTurnBegins(int carCount) {
        return String.format(LAST_TURN_BEGINS, playerName, carCount, plural(carCount));
    }

    public String getsLongestTrailBonus(Trail longestTrail) {
        return String.format(GETS_BONUS, playerName, getTrailName(longestTrail));
    }

    public String won(int points, int loserPoints) {
        return String.format(WINS, playerName, points, plural(points), loserPoints, plural(loserPoints));
    }

}

