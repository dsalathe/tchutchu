<template>
  <nav>
    <router-link to="/">Game</router-link> |
    <router-link to="/about">About</router-link> <!--TODO might use it for a "rule" or something panel. And might hide it for phone view-->
  </nav>
  <router-view
    :sendRequest="sendRequest"
    :inGame="inGame"
    :messages="messages"
    :ownId="ownId"
    :player1Name="player1Name"
    :player2Name="player2Name"
    :ticketsCount="ticketsCount"
    :currentPlayerId="currentPlayerId"
    :lastPlayerId="lastPlayerId"
    :faceUpCards="faceUpCards"
    :deckSize="deckSize"
    :discardSize="discardSize"
    :ticketsCountP1="ticketsCountP1"
    :cardsCountP1="cardsCountP1"
    :routesP1="routesP1"
    :ticketsCountP2="ticketsCountP2"
    :cardsCountP2="cardsCountP2"
    :routesP2="routesP2"
    :ownTickets="ownTickets"
    :ownCards="ownCards"
    :ownRoutes="ownRoutes"
    :setupState="setupState"
    :setupGameName="setupGameName"
    :possibleTickets="possibleTickets"
    :onTicketsChosen="onTicketsChosen" />
  <Debug
  v-if="isDev()"
  :connected="connected"
  :connect="connect"
  :disconnect="disconnect"
  :sendRequest="sendRequest"
  :messages="messages"
  />
</template>

<script>

import SockJS from 'sockjs-client'
import Stomp from 'webstomp-client'

import Debug from '@/components/DebugPanel.vue'

export default {
  name: 'App',
  components: {
    Debug
  },
  data () {
    return {
      stompClient: undefined,
      messages: [],
      metaAction: '',
      playAction: 'NOTHING',
      dataMsg: '',
      connected: true,
      // GAME STATE DATA
      inGame: false,
      ownId: -1,
      player1Name: 'Player 1',
      player2Name: 'Player 2',
      ticketsCount: 0,
      currentPlayerId: -2,
      lastPlayerId: undefined,
      faceUpCards: [],
      deckSize: 0,
      discardSize: 0,
      ticketsCountP1: 0,
      cardsCountP1: 0,
      routesP1: [],
      ticketsCountP2: 0,
      cardsCountP2: 0,
      routesP2: [],
      ownTickets: [],
      ownCards: [],
      ownRoutes: [],
      // SETUP GAME DATA
      setupState: '',
      setupGameName: '',
      // Choosing initial or in-game tickets
      possibleTickets: []
    }
  },
  methods: {
    addMessage (msg) {
      this.messages.push(msg)
    },
    connect () {
      const socket = new SockJS(this.isDev ? 'http://localhost:8080/game-ws' : '/game-ws')
      this.stompClient = Stomp.over(socket)
      this.stompClient.connect({}, frame => {
        this.connected = true
        console.log('Connected: ' + frame)
        this.stompClient.subscribe('/topic/tchu-events', notification => {
          this.addMessage(JSON.parse(notification.body))
        }, { id: 'topic' })
        this.stompClient.subscribe('/user/queue/tchu-events', notification => {
          const msg = JSON.parse(notification.body)
          this.addMessage(msg)
          if (msg.messageId === 'INIT_PLAYERS') {
            this.unsubscribeGeneral()
            this.inGame = true
            const [ownIdStr, encodedNames] = msg.data.split(' ')
            this.ownId = parseInt(ownIdStr)
            const [player1Name64, player2Name64] = encodedNames.split(',')
            this.player1Name = decodeURIComponent(escape(atob(player1Name64)))
            this.player2Name = decodeURIComponent(escape(atob(player2Name64)))
          } else if (msg.messageId === 'UPDATE_STATE') {
            const [publicGS, ownState] = msg.data.split(' ')
            const [ticketsCountStr, publicCardState, currentPlayerIdStr, publicPS1, publicPS2, lastPlayerStr] = publicGS.split(':')
            this.ticketsCount = parseInt(ticketsCountStr)
            this.currentPlayerId = parseInt(currentPlayerIdStr)
            if (lastPlayerStr !== '') {
              this.lastPlayerId = parseInt(lastPlayerStr)
            }
            const [faceUpCards, deckSizeStr, discardSizeStr] = publicCardState.split(';')
            const faceUpCardsSplitted = faceUpCards.split(',')
            if (faceUpCards !== '' && faceUpCardsSplitted.length > 0) {
              this.faceUpCards = faceUpCardsSplitted.map(e => parseInt(e))
            }
            this.deckSize = parseInt(deckSizeStr)
            this.discardSize = parseInt(discardSizeStr)
            const [ticketsCountP1Str, cardsCountP1Str, routesP1] = publicPS1.split(';')
            this.ticketsCountP1 = parseInt(ticketsCountP1Str)
            this.cardsCountP1 = parseInt(cardsCountP1Str)
            const routesP1Splitted = routesP1.split(',')
            if (routesP1 !== '' && routesP1Splitted.length > 0) {
              this.routesP1 = routesP1Splitted.map(e => parseInt(e))
            }
            const [ticketsCountP2Str, cardsCountP2Str, routesP2] = publicPS2.split(';')
            this.ticketsCountP2 = parseInt(ticketsCountP2Str)
            this.cardsCountP2 = parseInt(cardsCountP2Str)
            const routesP2Splitted = routesP2.split(',')
            if (routesP2 !== '' && routesP2Splitted.length > 0) {
              this.routesP2 = routesP2Splitted.map(e => parseInt(e))
            }
            const [ownTickets, ownCards, ownRoutes] = ownState.split(';')
            this.ownTickets = ownTickets === '' ? this.ownTickets : ownTickets.split(',').map(e => parseInt(e))
            this.ownCards = ownCards === '' ? this.ownCards : ownCards.split(',').map(e => parseInt(e))
            this.ownRoutes = ownRoutes === '' ? this.ownRoutes : ownRoutes.splt(',').map(e => parseInt(e))
          } else if (msg.messageId === 'GAME_ID') {
            const [state, gameName] = msg.data.split(' ')
            this.setupState = state
            this.setupGameName = gameName
          } else if (msg.messageId === 'SET_INITIAL_TICKETS') {
            this.possibleTickets = msg.data.split(',').map(e => parseInt(e))
          }
        })
      })
    },
    disconnect () {
      if (this.stompClient !== undefined) {
        this.stompClient.disconnect()
      }
      this.connected = false
      console.log('Disconnected')
    },
    sendRequest (metaAction, playAction, data) {
      this.stompClient.send('/app/tchu',
        JSON.stringify({ metaAction, playAction, data }), {})
    },
    unsubscribeGeneral () {
      this.stompClient.unsubscribe('topic')
      console.log('Topic was unsubscribed')
    },
    isDev () {
      return process.env.NODE_ENV === 'development'
    },
    onTicketsChosen (tickets) {
      this.possibleTickets = []
      this.sendRequest('PLAY', 'INITIAL_TICKETS_CHOSEN', tickets + '')
    }
  },
  mounted () {
    console.log('Mounted!')
    this.connect()
    console.log('Subscribing to websockets topics and queues...')
  }
}

</script>

<style>
#app {
  margin: 0 auto 20px;
  background-color: #fff;
  border: 1px solid #e5e5e5;
  -webkit-border-radius: 5px;
  -moz-border-radius: 5px;
  border-radius: 5px;
  max-width: 1920px;
}

nav {
  padding: 30px;
}

nav a {
  font-weight: bold;
  color: #2c3e50;
}

nav a.router-link-exact-active {
  color: #42b983;
}
</style>
