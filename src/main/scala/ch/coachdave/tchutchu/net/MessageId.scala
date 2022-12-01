package ch.coachdave.tchutchu.net

enum MessageId:
  case INIT_PLAYERS, RECEIVE_INFO, UPDATE_STATE, SET_INITIAL_TICKETS,
  CHOOSE_INITIAL_TICKETS, NEXT_TURN, CHOOSE_TICKETS, DRAW_SLOT, ROUTE, CARDS, CHOOSE_ADDITIONAL_CARDS, GAME_ID, CHAT,
  CONGRATULATE
