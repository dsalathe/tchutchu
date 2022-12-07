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
        :isDisabled="isDisabled" :cards="ownCardsColor" :seizeRoute="seizeRoute" :isDrawing="isFirstCardDrawn"
        :takenRoutesP1="routesP1" :takenRoutesP2="routesP2" :tunnelAdditonalCards="additionalCardsOptions" :seizeTunnel="seizeTunnel"
        :wagons="ownWagons" />
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
    },
    player1 () {
      return {
        name: this.player1Name,
        tickets: this.ticketsCountP1,
        cards: this.cardsCountP1,
        wagons: this.player1Wagons,
        points: this.player1Points
      }
    },
    player2 () {
      return {
        name: this.player2Name,
        tickets: this.ticketsCountP2,
        cards: this.cardsCountP2,
        wagons: this.player2Wagons,
        points: this.player2Points
      }
    },
    player1Wagons () {
      return 40 - this.routesP1.map(index => this.dataMap.routes[index].length).reduce((a, b) => a + b, 0)
    },
    player2Wagons () {
      return 40 - this.routesP2.map(index => this.dataMap.routes[index].length).reduce((a, b) => a + b, 0)
    },
    ownWagons () {
      return this.ownId === 0 ? this.player1Wagons : this.player2Wagons
    },
    player1Points () {
      return this.routesP1.map(index => this.routePoint(this.dataMap.routes[index].length)).reduce((a, b) => a + b, 0)
    },
    player2Points () {
      return this.routesP2.map(index => this.routePoint(this.dataMap.routes[index].length)).reduce((a, b) => a + b, 0)
    },
    faceUpCardsColor () {
      return this.faceUpCards.map(i => this.toCardColor(i))
    },
    ownCardsColor () {
      return this.ownCards.map(this.toCardColor)
    },
    isDisabled () {
      return this.possibleTickets.length || this.ownId !== this.currentPlayerId || this.updatedStateCount < 3 || this.isGameOver
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
      const [author, content] = data.split(' ')
      return { style: 'chatMsg', author: decodeURIComponent(escape(atob(author))), content: decodeURIComponent(escape(atob(content))) }
    },
    buildInfoMessage ({ data }) {
      return { style: 'infoMsg', author: '[GAME]', content: decodeURIComponent(escape(atob(data))) }
    },
    sendChatMessage (msg) {
      this.sendRequest('CHAT', 'NOTHING', btoa(unescape(encodeURIComponent(msg))))
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
    seizeRoute (index, arrayCards) {
      this.sendRequest('PLAY', 'PLAY_TURN', 'CLAIMING_ROUTE ' + index + ' ' + arrayCards.join(','))
    },
    seizeTunnel (item) {
      this.onCardsForTunnelChosen(item.join(','))
    },
    routePoint (length) {
      return [0, 1, 2, 4, 7, 10, 15][length]
    }
  },
  props: ['sendRequest', 'inGame', 'messages', 'ownId', 'player1Name', 'player2Name', 'ticketsCount', 'currentPlayerId', 'lastPlayerId', 'faceUpCards',
    'deckSize', 'discardSize', 'ticketsCountP1', 'cardsCountP1', 'routesP1', 'ticketsCountP2', 'cardsCountP2', 'routesP2', 'ownTickets', 'ownCards',
    'ownRoutes', 'setupState', 'setupGameName', 'possibleTickets', 'onTicketsChosen', 'additionalCardsOptions', 'onCardsForTunnelChosen', 'updatedStateCount',
    'isGameOver']
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
  max-width: 24%;
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

@media screen and (orientation:portrait) {
  .top-left {
    max-width: 70%;
  }
  #board, #game-setup {
    opacity: 0.3;
  }

  .top-left::after {
    position: absolute;
    top: 15%;
    font-weight: bold;
    font-style: italic;
    border: 2px solid black;
    border-radius: 4px;
    padding: 5px;
    text-align: center;
    color: #DA291C;
    content: "Please Turn your phone when in 'Game' Mode!";
  }
}

</style>
