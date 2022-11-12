package ch.coachdave.tchutchu.game

import ch.coachdave.tchutchu.SortedBag;

object Constants:

  /**
   * Nombre de cartes wagon de chaque couleur.
   */
  val CAR_CARDS_COUNT = 12

  /**
   * Nombre de cartes locomotive.
   */
  val LOCOMOTIVE_CARDS_COUNT = 14

  /**
   * Nombre total de cartes wagon/locomotive.
   */
  val TOTAL_CARDS_COUNT: Int = LOCOMOTIVE_CARDS_COUNT + CAR_CARDS_COUNT * Color.COUNT

  /**
   * Ensemble de toutes les cartes (110 au total).
   */
  val ALL_CARDS: SortedBag[Card] = computeAllCards

  private def computeAllCards = {
    val cardsBuilder = new SortedBag.Builder[Card]
    cardsBuilder.add(LOCOMOTIVE_CARDS_COUNT, Card.LOCOMOTIVE)
    for (card <- Card.CARS) {
      cardsBuilder.add(CAR_CARDS_COUNT, card)
    }
    assert(cardsBuilder.size == TOTAL_CARDS_COUNT)
    cardsBuilder.build
  }

  /**
   * Numéro d'emplacement fictif désignant la pioche de cartes.
   */
  val DECK_SLOT: Int = -1

  /**
   * Liste de tous les numéros d'emplacements de cartes face visible.
   */
  val FACE_UP_CARD_SLOTS: Vector[Integer] = Vector(0, 1, 2, 3, 4)

  /**
   * Nombre d'emplacements pour les cartes face visible.
   */
  val FACE_UP_CARDS_COUNT: Int = FACE_UP_CARD_SLOTS.size

  /**
   * Nombre de billets distribués à chaque joueur en début de partie.
   */
  val INITIAL_TICKETS_COUNT = 5

  /**
   * Nombre de cartes distribuées à chaque joueur en début de partie.
   */
  val INITIAL_CARDS_COUNT = 4

  /**
   * Nombre de wagons dont dispose chaque joueur en début de partie.
   */
  val INITIAL_CAR_COUNT = 40

  /**
   * Nombre de billets tirés à la fois en cours de partie.
   */
  val IN_GAME_TICKETS_COUNT = 3

  /**
   * Nombre maximum de billets qu'un joueur peut défausser lors d'un tirage.
   */
  val DISCARDABLE_TICKETS_COUNT = 2

  /**
   * Nombre de cartes à tirer lors de la construction d'un tunnel.
   */
  val ADDITIONAL_TUNNEL_CARDS = 3

  /**
   * Nombre de points obtenus pour la construction de routes de longueur 1 à 6.
   * (L'élément à l'index i correspond à une longueur de route i. Une valeur
   * invalide est placée à l'index 0, car les routes de longueur 0 n'existent pas).
   */
  val ROUTE_CLAIM_POINTS: Vector[Integer] = Vector(Integer.MIN_VALUE, 1, 2, 4, 7, 10, 15)

  /**
   * Longueur minimum d'une route.
   */
  val MIN_ROUTE_LENGTH = 1

  /**
   * Longueur maximum d'une route.
   */
  val MAX_ROUTE_LENGTH = ROUTE_CLAIM_POINTS.size - 1;


  /**
   * Nombre de points bonus obtenus par le(s) joueur(s) disposant du plus long chemin.
   */
  val LONGEST_TRAIL_BONUS_POINTS = 10
