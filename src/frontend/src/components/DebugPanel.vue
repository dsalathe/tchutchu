<template>
  <div id="main-content" class="container">
    <h2>DEBUG PANEL</h2>
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
                <button id="send" class="btn btn-default" @click.prevent="sendForm">Send</button>
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

export default {
  data () {
    return {
      metaAction: '',
      playAction: 'NOTHING',
      dataMsg: ''
    }
  },
  props: ['connected', 'connect', 'disconnect', 'sendRequest', 'messages'],
  methods: {
    sendForm () {
      this.sendRequest(this.metaAction, this.playAction, this.dataMsg)
    }
  }
}

</script>

<style>
#main-content {
  margin-top: 20px;
  background-color: #f5f5f5;
}
</style>
