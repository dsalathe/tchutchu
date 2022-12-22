<template>
  <nav>
    <router-link to="/">Game</router-link> |
    <router-link to="/rules">How to play</router-link> |
    <router-link to="/technical">Ticket to Tech</router-link>
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
    :onTicketsChosen="onTicketsChosen"
    :additionalCardsOptions="additionalCardsOptions"
    :onCardsForTunnelChosen="onCardsForTunnelChosen"
    :updatedStateCount="updatedState"
    :isGameOver="isGameOver" />
  <footer>This game is a re-creation of the game <strong>Ticket to ride: Switzerland</strong> from <strong>Day of Wonders.</strong>
  This project is for educational purposes only and is not intended for commercial use. This is a non-profit project and is not affiliated with the original game or its designers.
  All game assets, including names, trademarks, and copyrighted materials, belong to their respective owners.
  Source code available on <a href="https://github.com/dsalathe/tchutchu">github</a>. See official website of the publisher <a href="https://www.daysofwonder.com/en/">here</a>.
  </footer>
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
import JSConfetti from 'js-confetti'

import Debug from '@/components/DebugPanel.vue'

export default {
  name: 'App',
  components: {
    Debug
  },
  data () {
    return {
      stompClient: undefined,
      messages: this.retrieve('messages', [{ messageId: 'CHAT', data: 'V0VMQ09NRQ== V2VsY29tZSB0byBUY2h1VGNodSEgSWYgeW91IGV4cGVyaWVuY2UgYW55IGNvbm5lY3Rpb24gaXNzdWVzLCB0cnkgdG8gcmVsb2FkIHRoZSBwYWdlLg==' }]),
      metaAction: '',
      playAction: 'NOTHING',
      dataMsg: '',
      connected: true,
      userId: this.retrieve('userId', ''),
      // GAME STATE DATA
      inGame: this.retrieve('inGame', false),
      ownId: this.retrieve('ownId', -1),
      player1Name: this.retrieve('player1Name', 'Player 1'),
      player2Name: this.retrieve('player2Name', 'Player 2'),
      ticketsCount: this.retrieve('ticketsCount', 0),
      currentPlayerId: this.retrieve('currentPlayerId', -2),
      lastPlayerId: this.retrieve('lastPlayerId', undefined),
      faceUpCards: this.retrieve('faceUpCards', []),
      deckSize: this.retrieve('deckSize', 0),
      discardSize: this.retrieve('discardSize', 0),
      ticketsCountP1: this.retrieve('ticketsCountP1', 0),
      cardsCountP1: this.retrieve('cardsCountP1', 0),
      routesP1: this.retrieve('routesP1', []),
      ticketsCountP2: this.retrieve('ticketsCountP2', 0),
      cardsCountP2: this.retrieve('cardsCountP2', 0),
      routesP2: this.retrieve('routesP2', []),
      ownTickets: this.retrieve('ownTickets', []),
      ownCards: this.retrieve('ownCards', []),
      ownRoutes: this.retrieve('ownRoutes', []),
      // SETUP GAME DATA
      setupState: this.retrieve('setupState', ''),
      setupGameName: this.retrieve('setupGameName', ''),
      // Choosing initial or in-game tickets
      possibleTickets: this.retrieve('possibleTickets', []),
      // TUNNEL CLAIMING CARDS
      additionalCardsOptions: this.retrieve('additionalCardsOptions', []),
      // turn tracking
      updatedState: this.retrieve('updatedState', 0),
      isGameOver: this.retrieve('isGameOver', false)
    }
  },
  methods: {
    retrieve (key, defaultValue) {
      return JSON.parse(sessionStorage.getItem(key)) ?? defaultValue
    },
    persist (key, value) {
      sessionStorage.setItem(key, JSON.stringify(value))
      return value
    },
    addMessage (msg) {
      this.messages.push(msg)
      this.persist('messages', this.messages.slice(-50))
      // sessionStorage.setItem('messages', JSON.stringify(this.messages))
    },
    connect () {
      const socket = new SockJS(this.isDev() ? 'http://localhost:8080/game-ws' : '/game-ws')
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
            this.inGame = this.persist('inGame', true)
            const [ownIdStr, encodedNames] = msg.data.split(' ')
            this.ownId = this.persist('ownId', parseInt(ownIdStr))
            const [player1Name64, player2Name64] = encodedNames.split(',')
            this.player1Name = this.persist('player1Name', decodeURIComponent(escape(atob(player1Name64))))
            this.player2Name = this.persist('player2Name', decodeURIComponent(escape(atob(player2Name64))))
          } else if (msg.messageId === 'UPDATE_STATE') {
            this.updatedState = this.persist('updatedState', this.updatedState + 1)
            const [publicGS, ownState] = msg.data.split(' ')
            const [ticketsCountStr, publicCardState, currentPlayerIdStr, publicPS1, publicPS2, lastPlayerStr] = publicGS.split(':')
            this.ticketsCount = this.persist('ticketsCount', parseInt(ticketsCountStr))
            this.currentPlayerId = this.persist('currentPlayerId', parseInt(currentPlayerIdStr))
            if (lastPlayerStr !== '') {
              this.lastPlayerId = this.persist('lastPlayerId', parseInt(lastPlayerStr))
            }
            const [faceUpCards, deckSizeStr, discardSizeStr] = publicCardState.split(';')
            const faceUpCardsSplitted = faceUpCards.split(',')
            if (faceUpCards !== '' && faceUpCardsSplitted.length > 0) {
              this.faceUpCards = this.persist('faceUpCards', faceUpCardsSplitted.map(e => parseInt(e)))
            }
            this.deckSize = this.persist('deckSize', parseInt(deckSizeStr))
            this.discardSize = this.persist('discardSize', parseInt(discardSizeStr))
            const [ticketsCountP1Str, cardsCountP1Str, routesP1] = publicPS1.split(';')
            this.ticketsCountP1 = this.persist('ticketsCountP1', parseInt(ticketsCountP1Str))
            this.cardsCountP1 = this.persist('cardsCountP1', parseInt(cardsCountP1Str))
            const routesP1Splitted = this.persist('routesP1Splitted', routesP1.split(','))
            if (routesP1 !== '' && routesP1Splitted.length > 0) {
              this.routesP1 = this.persist('routesP1', routesP1Splitted.map(e => parseInt(e)))
            }
            const [ticketsCountP2Str, cardsCountP2Str, routesP2] = publicPS2.split(';')
            this.ticketsCountP2 = this.persist('ticketsCountP2', parseInt(ticketsCountP2Str))
            this.cardsCountP2 = this.persist('cardsCountP2', parseInt(cardsCountP2Str))
            const routesP2Splitted = routesP2.split(',')
            if (routesP2 !== '' && routesP2Splitted.length > 0) {
              this.routesP2 = this.persist('routesP2', routesP2Splitted.map(e => parseInt(e)))
            }
            const [ownTickets, ownCards, ownRoutes] = ownState.split(';')
            this.ownTickets = ownTickets === '' ? this.ownTickets : this.persist('ownTickets', ownTickets.split(',').map(e => parseInt(e)))
            this.ownCards = ownCards === '' ? this.ownCards : this.persist('ownCards', ownCards.split(',').map(e => parseInt(e)))
            this.ownRoutes = ownRoutes === '' ? this.ownRoutes : this.persist('ownRoutes', ownRoutes.split(',').map(e => parseInt(e)))
          } else if (msg.messageId === 'USER_ID') {
            this.userId = this.persist('userId', msg.data)
          } else if (msg.messageId === 'GAME_ID') {
            const [state, gameName] = msg.data.split(' ')
            this.setupState = this.persist('setupState', state)
            this.setupGameName = this.persist('setupGameName', gameName)
          } else if (msg.messageId === 'SET_INITIAL_TICKETS' || msg.messageId === 'CHOOSE_TICKETS') {
            this.possibleTickets = this.persist('possibleTickets', msg.data.split(',').map(e => parseInt(e)))
          } else if (msg.messageId === 'CHOOSE_ADDITIONAL_CARDS') {
            this.additionalCardsOptions = this.persist('additionalCardsOptions', msg.data.split(';').map(l => l.split(',').map(e => parseInt(e))))
          } else if (msg.messageId === 'CONGRATULATE') {
            this.isGameOver = true
            sessionStorage.clear()
            if (parseInt(msg.data) === this.ownId) {
              const confetti = new JSConfetti()
              confetti.addConfetti()
              setTimeout(() => confetti.addConfetti(), 100)
              setTimeout(() => confetti.addConfetti(), 200)
              setTimeout(() => confetti.addConfetti(), 300)
              setInterval(() => confetti.addConfetti(), 2000)
              setInterval(() => confetti.addConfetti(), 3000)
            }
          } else if (msg.messageId === 'RECONNECT') {
            sessionStorage.clear()
            location.reload()
          }
        })
        if (this.userId !== '') {
          this.sendRequest('RECONNECT', 'NOTHING', '')
          this.unsubscribeGeneral()
        }
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
      const userId = this.userId
      this.stompClient.send('/app/tchu',
        JSON.stringify({ metaAction, playAction, data, userId }), {})
    },
    unsubscribeGeneral () {
      this.stompClient.unsubscribe('topic')
      console.log('Topic was unsubscribed')
    },
    isDev () {
      return process.env.NODE_ENV === 'development'
    },
    onTicketsChosen (tickets) {
      const playAction = this.possibleTickets.length === 5 ? 'INITIAL_TICKETS_CHOSEN' : 'ADDITIONAL_TICKETS_CHOSEN'
      this.possibleTickets = this.persist('possibleTickets', [])
      this.sendRequest('PLAY', playAction, tickets + '')
    },
    onCardsForTunnelChosen (cards) {
      this.additionalCardsOptions = this.persist('additionalCardsOptions', [])
      this.sendRequest('PLAY', 'ADDITIONAL_CARDS_CHOSEN', cards)
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
body {
  overscroll-behavior: contain;
}
#app {
  margin: 0 auto 20px;
  background-color: #fff;
  border: 1px solid #e5e5e5;
  -webkit-border-radius: 5px;
  -moz-border-radius: 5px;
  border-radius: 5px;
  max-width: 1720px;
}

nav {
  padding: 30px;
  text-align: center;
}

nav a {
  font-weight: bold;
  color: #2c3e50;
}

nav a.router-link-exact-active {
  color: #DA291C;
}

footer {
  margin-top: 50px;
  font-style: italic;
  font-size: smaller;
  text-align: center;
  color: #666;
  background-color: #f8f8f8;
  border: 1px solid #ccc;
  border-radius: 5px;
  padding: 5px;
}

@media (max-width: 1200px) {
  #app {
    display: flex;
    flex-direction: column-reverse;
  }

  #main-content {
    display: none;
  }
  footer {
    display: none;
  }
}
</style>
