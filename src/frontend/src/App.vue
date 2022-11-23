<template>
  <nav>
    <router-link to="/">Game</router-link> |
    <router-link to="/about">About</router-link> <!--TODO might use it for a "rule" or something panel. And might hide it for phone view-->
  </nav>
  <router-view :sendRequest="sendRequest" :inGame="inGame" :messages="messages" />
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
      inGame: false,
      connected: true
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
  max-width: 940px;
  padding: 2em 3em;
  margin: 0 auto 20px;
  background-color: #fff;
  border: 1px solid #e5e5e5;
  -webkit-border-radius: 5px;
  -moz-border-radius: 5px;
  border-radius: 5px;
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
