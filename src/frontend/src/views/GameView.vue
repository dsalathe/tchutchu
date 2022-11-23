<template>
  <div class="game">
    IN PROGRESS

    Chat component:
    <Chat :displayedMessages="displayedMessages" :sendChatMessage="sendChatMessage" :isInGame="inGame" />
  </div>
</template>

<script>

import Chat from '@/components/Chat.vue'

export default {
  name: 'GameView',
  components: {
    Chat
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
    }
  },
  props: ['sendRequest', 'inGame', 'messages']
}
</script>
