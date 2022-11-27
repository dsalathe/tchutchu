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
        <Board />
      </div>
      <div v-else id="game-setup">
        <GameSetup :sendSetupGame="sendSetupGame" :state="setupState" :chosenGameName="setupGameName" />
      </div>
      <div v-if="inGame" id="deck">Deck</div>
    </div>
    <div class="bottom">
      <div id="player-state">Player State</div>
    </div>
  </div>
</template>

<script>

import Chat from '@/components/Chat.vue'
import Board from '@/components/BoardGame.vue'
import StateInfo from '@/components/StateInfo.vue'
import GameSetup from '@/components/GameSetup.vue'

export default {
  name: 'GameView',
  components: {
    Chat,
    Board,
    StateInfo,
    GameSetup
  },
  data () {
    return {
      metaAction: '',
      playAction: 'NOTHING',
      dataMsg: ''
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
    }
  },
  methods: {
    buildChatMessage ({ data }) {
      const [author, ...content] = data.split(' ')
      return { style: 'chatMsg', author, content: content.join(' ') }
    },
    buildInfoMessage ({ data }) {
      return { style: 'infoMsg', author: 'GAME', content: decodeURIComponent(escape(atob(data))) }
    },
    sendChatMessage (msg) {
      this.sendRequest('CHAT', 'NOTHING', msg)
    },
    sendSetupGame (setupMode, msg) {
      this.sendRequest(setupMode, 'NOTHING', msg)
    }
  },
  props: ['sendRequest', 'inGame', 'messages', 'ownId', 'player1Name', 'player2Name', 'ticketsCount', 'currentPlayerId', 'lastPlayerId', 'faceUpCards',
    'deckSize', 'discardSize', 'ticketsCountP1', 'cardsCountP1', 'routesP1', 'ticketsCountP2', 'cardsCountP2', 'routesP2', 'ownTickets', 'ownCards',
    'ownRoutes', 'setupState', 'setupGameName']
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
  flex: 1 0 150px;
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
  background-color: darkgoldenrod;
}

#player-state {
  width: 100%;
  background-color: chartreuse;
}

</style>
