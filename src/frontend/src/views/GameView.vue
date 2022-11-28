<template>
  <div class="game">
    <div class="top">
      <div class="top-left">
        <div id="stats">
          <StateInfo :player1="player1" :player2="player2" />
        </div>
        <div id="chat"><Chat :displayedMessages="displayedMessages" :sendChatMessage="sendChatMessage" :isInGame="inGame" /></div>
      </div>
      <div v-if="inGame" id="board">
        <Board :displayedTickets="displayedTickets" :onTicketsSelected="onTicketsChosen" :dataMap="dataMap"
        :isDisabled="isDisabled" :cards="ownCardsColor" :seizeRoute="seizeRoute" :isDrawing="isFirstCardDrawn" />
      </div>
      <div v-else id="game-setup">
        <GameSetup :sendSetupGame="sendSetupGame" :state="setupState" :chosenGameName="setupGameName" />
      </div>
      <div v-if="inGame" id="deck"><Deck :cards="faceUpCardsColor" :deckTicketsSize="ticketsCount"
        :deckCardsSize="deckSize" :deckDiscardSize="discardSize" :isDisabled="isDisabled"
        :drawFirst="drawFirstCard" :drawSecond="drawSecondCard" :drawTickets="drawTickets" /></div>
    </div>
    <div v-if="inGame" class="bottom">
      <div id="player-state"><PlayerState :tickets="ownDisplayedTickets" :cards="ownCardsColor" /></div>
    </div>
  </div>
</template>

<script>

import Chat from '@/components/Chat.vue'
import Board from '@/components/BoardGame.vue'
import StateInfo from '@/components/StateInfo.vue'
import GameSetup from '@/components/GameSetup.vue'
import Deck from '@/components/DeckHandler.vue'
import PlayerState from '@/components/PlayerState.vue'
import chMap from '@/chMap.json'

export default {
  name: 'GameView',
  components: {
    Chat,
    Board,
    StateInfo,
    GameSetup,
    Deck,
    PlayerState
  },
  data () {
    return {
      metaAction: '',
      playAction: 'NOTHING',
      dataMsg: '',
      dataMap: chMap,
      isFirstCardDrawn: false
    }
  },
  computed: {
    displayedMessages () {
      return this.messages.filter(m => m.messageId === 'CHAT' || m.messageId === 'RECEIVE_INFO')
        .map(m => m.messageId === 'CHAT' ? this.buildChatMessage(m) : this.buildInfoMessage(m))
        // For encoding: btoa(unescape(encodeURIComponent( str )))
    },
    player1 () {
      return {
        name: this.player1Name,
        tickets: this.ticketsCountP1,
        cards: this.cardsCountP1,
        wagons: 40, // TO BE COMPUTED
        points: 0 // TO BE COMPUTED
      }
    },
    player2 () {
      return {
        name: this.player2Name,
        tickets: this.ticketsCountP2,
        cards: this.cardsCountP2,
        wagons: 40, // TO BE COMPUTED
        points: 0 // TO BE COMPUTED
      }
    },
    faceUpCardsColor () {
      return this.faceUpCards.map(i => this.toCardColor(i))
    },
    ownCardsColor () {
      return this.ownCards.map(this.toCardColor)
    },
    isDisabled () {
      return this.possibleTickets.length || this.ownId !== this.currentPlayerId
    },
    ownDisplayedTickets () {
      return this.ownTickets.map(t => ({ id: t, display: this.displayTicket(t) }))
    },
    displayedTickets () {
      return this.possibleTickets.map(t => ({ id: t, display: this.displayTicket(t) }))
    }
  },
  methods: {
    buildChatMessage ({ data }) {
      const [author, ...content] = data.split(' ')
      return { style: 'chatMsg', author, content: content.join(' ') }
    },
    buildInfoMessage ({ data }) {
      return { style: 'infoMsg', author: '[GAME]', content: decodeURIComponent(escape(atob(data))) }
    },
    sendChatMessage (msg) {
      this.sendRequest('CHAT', 'NOTHING', msg) // TODO encode and decode both player names and messages in chat
    },
    sendSetupGame (setupMode, msg) {
      this.sendRequest(setupMode, 'NOTHING', msg)
    },
    toCardColor (index) {
      return ['BLACK', 'VIOLET', 'BLUE', 'GREEN', 'YELLOW', 'ORANGE', 'RED', 'WHITE', 'LOCOMOTIVE'][index]
    },
    drawFirstCard (index) {
      this.isFirstCardDrawn = true
      this.sendRequest('PLAY', 'PLAY_TURN', 'FIRST_CARD ' + index)
    },
    drawSecondCard (index) {
      this.isFirstCardDrawn = false
      this.sendRequest('PLAY', 'DRAW_SECOND', index)
    },
    drawTickets () {
      this.sendRequest('PLAY', 'PLAY_TURN', 'ASK_MORE_TICKETS')
    },
    displayTicket (ticket) {
      const ticketData = this.dataMap.tickets[ticket]
      if (ticketData.to.length > 1) {
        return ticketData.from[0].name + ' - {' + ticketData.to.map((to, i) => to.name + ' (' + ticketData.points[i] + ')').join(', ') + '}'
      }
      return ticketData.from[0].name + ' - ' + ticketData.to[0].name + ' (' + ticketData.points[0] + ')'
    },
    seizeRoute (index) {
      this.sendRequest('PLAY', 'PLAY_TURN', 'CLAIMING_ROUTE ' + index + ' 4')
    }
  },
  props: ['sendRequest', 'inGame', 'messages', 'ownId', 'player1Name', 'player2Name', 'ticketsCount', 'currentPlayerId', 'lastPlayerId', 'faceUpCards',
    'deckSize', 'discardSize', 'ticketsCountP1', 'cardsCountP1', 'routesP1', 'ticketsCountP2', 'cardsCountP2', 'routesP2', 'ownTickets', 'ownCards',
    'ownRoutes', 'setupState', 'setupGameName', 'possibleTickets', 'onTicketsChosen']
}
</script>

<style scoped>

.game {
  display: flex;
  flex-direction: column;
  border: 2px dotted black;
  width: 100%;
}

.top {
  flex: 1 0 300px;
  display: flex;
}

.top-left {
  flex: 3 0 150px;
  max-width: 30%;
  display: flex;
  flex-direction: column;
}

.bottom {
  flex: 0 0 110px;
  width: 100%;
  display: flex;
}

#stats {
  flex-grow: 0;
}

#chat {
  flex-grow: 1;
  height: min(580px, calc(42vw - 150px));
  background-color: aquamarine;
}

#board {
  /* flex: 0 0 600px; */
  background-color: violet;
}

#game-setup {
  flex-grow: 1;
}

#deck {
  flex: 1 0 50px;
}

#player-state {
  width: 100%;
}

</style>
