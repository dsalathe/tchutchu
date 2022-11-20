<template>
  <nav>
    <router-link to="/">Home</router-link> |
    <router-link to="/about">About</router-link> <!--TODO might use it for a "rule" or something panel. And might hide it for phone view-->
  </nav>
  <router-view/>
  <div id="main-content" class="container">
    <div class="row">
        <div class="col-md-6">
            <form class="form-inline">
                <div class="form-group">
                    <label for="connect">WebSocket connection:</label>
                    <button id="connect" class="btn btn-default" @click="connect" :disabled="connected" type="button">Connect</button>
                    <button id="disconnect" class="btn btn-default" @click="disconnect" :disabled="!connected" type="button">Disconnect</button>
                </div>
            </form>
        </div>
        <div class="col-md-6">
            <form class="form-inline">
                <div class="form-group">
                    <label for="meta-action">Meta Action</label>
                    <select v-model="metaAction" id="meta-action" class="form-control">
                        <option value="">--Please choose a meta option--</option>
                        <option value="INIT_GAME">INIT_GAME</option>
                        <option value="JOIN_GAME">JOIN_GAME</option>
                        <option value="CHAT">CHAT</option>
                        <option value="PLAY">PLAY</option>
                    </select>
                </div>
                <div class="form-group">
                    <label for="play-action">Play Action</label>
                    <select v-model="playAction" id="play-action" class="form-control">

                        <option value="NOTHING">--Please choose a game option--</option>
                        <option value="JOINING_GAME">JOINING_GAME</option>
                        <option value="INITIAL_TICKETS_CHOSEN">INITIAL_TICKETS_CHOSEN</option>
                        <option value="PLAY_TURN">FIRST_CARD</option>
                        <option value="PLAY_TURN">CLAIMING_ROUTE</option>
                        <option value="PLAY_TURN">ASK_MORE_TICKETS</option>
                        <option value="DRAW_SECOND">DRAW_SECOND</option>
                        <option value="ADDITIONAL_CARDS_CHOSEN">ADDITIONAL_CARDS_CHOSEN</option>
                        <option value="ADDITIONAL_TICKETS_CHOSEN">ADDITIONAL_TICKETS_CHOSEN</option>
                    </select>
                </div>
                <div class="form-group">
                    <label for="data">Data</label>
                    <input v-model="dataMsg" type="text" id="data" class="form-control" placeholder="Data here...">
                </div>
                <button id="send" class="btn btn-default" @click.prevent="sendName">Send</button>
            </form>
        </div>
    </div>
    <div class="row">
        <div class="col-md-12">
            <table id="conversation" class="table table-striped">
                <thead>
                <tr>
                    <th>Info</th>
                </tr>
                </thead>
                <tbody id="info">
                  <div v-for="(msg, i) in messages" :key="i">
                    <tr><td>{{ msg.messageId }}: {{ msg.data }}</td></tr>
                  </div>
                </tbody>
            </table>
        </div>
    </div>
</div>
</template>

<script>

import SockJS from 'sockjs-client'
import Stomp from 'webstomp-client'

export default {
  name: 'App',
  data () {
    return {
      stompClient: undefined,
      messages: [],
      metaAction: '',
      playAction: 'NOTHING',
      dataMsg: '',
      connected: false
    }
  },
  methods: {
    addMessage (msg) {
      this.messages.push(msg)
    },
    connect () {
      const socket = new SockJS('/game-ws')
      this.stompClient = Stomp.over(socket)
      this.stompClient.connect({}, frame => {
        this.connected = true
        console.log('Connected: ' + frame)
        this.stompClient.subscribe('/topic/tchu-events', notification => {
          this.addMessage(JSON.parse(notification.body))
        })
        this.stompClient.subscribe('/user/queue/tchu-events', notification => {
          this.addMessage(JSON.parse(notification.body))
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
    sendName () {
      this.stompClient.send('/app/tchu',
        JSON.stringify({ metaAction: this.metaAction, playAction: this.playAction, data: this.dataMsg }), {})
    }
  },
  mounted () {
    console.log('Mounted!')
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
  background-color: #f5f5f5;
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
