<template>
  <div class="tab-wrap">

    <input @click="scrolldown" type="radio" id="tab1" name="tabGroup1" class="tab" :checked="!isInGame">
    <label for="tab1">{{isInGame ? 'Chat' : 'General'}}</label>

    <input @click="scrolldown" type="radio" id="tab2" name="tabGroup1" class="tab">
    <label for="tab2">Game</label>

    <input @click="scrolldown" type="radio" id="tab3" name="tabGroup1" class="tab" :checked="isInGame">
    <label for="tab3">All</label>

    <div class="tab__content">
      <div v-for="(msg, i) in generalMessages" :key="i">
        <tr><td :class="msg.style"> <span :class="msg.style">{{msg.author}}</span> :
          <span v-for="(chunk,i) in splitContent(msg.content)" :key="i">&nbsp;
            <Card v-if="isColor(chunk)" :color="getColor(chunk)" class="smallCard" />
            <span v-else>{{ chunk }}</span>
          </span> </td></tr>
      </div>
    </div>

    <div class="tab__content">
      <div v-for="(msg, i) in gameMessage" :key="i">
        <tr><td :class="msg.style"> <span :class="msg.style">{{msg.author}}</span> :
          <span v-for="(chunk,i) in splitContent(msg.content)" :key="i">&nbsp;
            <Card v-if="isColor(chunk)" :color="getColor(chunk)" class="smallCard" />
            <span v-else>{{ chunk }}</span>
          </span> </td></tr>
      </div>
    </div>

    <div class="tab__content">
      <div v-for="(msg, i) in displayedMessages" :key="i">
        <tr><td :class="msg.style"> <span :class="msg.style">{{msg.author}}</span> :
          <span v-for="(chunk,i) in splitContent(msg.content)" :key="i">&nbsp;
            <Card v-if="isColor(chunk)" :color="getColor(chunk)" class="smallCard" />
            <span v-else>{{ chunk }}</span>
          </span> </td></tr>
      </div>
    </div>
    <form class="form-inline">
      <input class="text-message" v-model="sendingMsg" type="text" placeholder="message...">
      <button class="send-button" @click.prevent="send">Send</button>
    </form>
  </div>
</template>

<script>
import Card from '@/components/GameCard.vue'

export default {
  name: 'ChatPanel',
  components: {
    Card
  },
  data () {
    return {
      sendingMsg: ''
    }
  },
  props: ['displayedMessages', 'sendChatMessage', 'isInGame'],
  computed: {
    generalMessages () {
      return this.displayedMessages.filter(m => m.style === 'chatMsg')
    },
    gameMessage () {
      return this.displayedMessages.filter(m => m.style === 'infoMsg')
    }
  },
  methods: {
    send () {
      if (this.sendingMsg.trim() !== '') {
        this.sendChatMessage(this.sendingMsg)
        this.sendingMsg = ''
      }
    },
    scrolldown () {
      const elements = document.getElementsByClassName('tab__content')
      for (let i = 0; i < elements.length; i++) {
        elements.item(i).scrollTop = elements.item(i).scrollHeight
      }
    },
    splitContent (s) {
      return s.split(' ')
    },
    getColor (chunk) {
      if (chunk === ':BLACK') {
        return 'BLACK'
      } else if (chunk === ':VIOLET') {
        return 'VIOLET'
      } else if (chunk === ':BLUE') {
        return 'BLUE'
      } else if (chunk === ':GREEN') {
        return 'GREEN'
      } else if (chunk === ':YELLOW') {
        return 'YELLOW'
      } else if (chunk === ':ORANGE') {
        return 'ORANGE'
      } else if (chunk === ':RED') {
        return 'RED'
      } else if (chunk === ':WHITE') {
        return 'WHITE'
      } else if (chunk === ':LOCOMOTIVE') {
        return 'LOCOMOTIVE'
      } else {
        return 'normal'
      }
    },
    isColor (chunk) {
      return this.getColor(chunk) !== 'normal'
    }
  },
  updated () {
    this.scrolldown()
  }
}
</script>

<style scoped>

.infoMsg {
  color: #444;
}

span.infoMsg {
  font-weight: bold;
}

.chatMsg {
  color: #999;
}

span.chatMsg {
  font-weight: bold;
  font-style: italic;
}

.tab-wrap {
  transition: 0.3s box-shadow ease;
  border-radius: 6px;
  max-width: 100%;
  display: flex;
  height: 100%;
  flex-wrap: wrap;
  position: relative;
  list-style: none;
  background-color: #fff;
  /* margin: 40px 0; */
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.12), 0 1px 2px rgba(0, 0, 0, 0.24);
}
 .tab-wrap:hover {
  box-shadow: 0 12px 23px rgba(0, 0, 0, 0.23), 0 10px 10px rgba(0, 0, 0, 0.19);
}
 .tab {
  display: none;
}
 .tab:checked:nth-of-type(1) ~ .tab__content:nth-of-type(1) {
  opacity: 1;
  transition: 0.5s opacity ease-in, 0.8s transform ease;
  position: relative;
  top: 0;
  z-index: 100;
  transform: translateY(0px);
  text-shadow: 0 0 0;
}
 .tab:checked:nth-of-type(2) ~ .tab__content:nth-of-type(2) {
  opacity: 1;
  transition: 0.5s opacity ease-in, 0.8s transform ease;
  position: relative;
  top: 0;
  z-index: 100;
  transform: translateY(0px);
  text-shadow: 0 0 0;
}
 .tab:checked:nth-of-type(3) ~ .tab__content:nth-of-type(3) {
  opacity: 1;
  transition: 0.5s opacity ease-in, 0.8s transform ease;
  position: relative;
  top: 0;
  z-index: 100;
  transform: translateY(0px);
  text-shadow: 0 0 0;
}
 .tab:first-of-type:not(:last-of-type) + label {
  border-top-right-radius: 0;
  border-bottom-right-radius: 0;
}
 .tab:not(:first-of-type):not(:last-of-type) + label {
  border-radius: 0;
}
 .tab:last-of-type:not(:first-of-type) + label {
  border-top-left-radius: 0;
  border-bottom-left-radius: 0;
}
 .tab:checked + label {
  background-color: #fff;
  box-shadow: 0 -1px 0 #fff inset;
  cursor: default;
}
 .tab:checked + label:hover {
  box-shadow: 0 -1px 0 #fff inset;
  background-color: #fff;
}
 .tab + label {
  box-shadow: 0 -1px 0 #eee inset;
  border-radius: 6px 6px 0 0;
  cursor: pointer;
  display: block;
  text-decoration: none;
  color: #333;
  flex-grow: 3;
  text-align: center;
  background-color: #f2f2f2;
  user-select: none;
  text-align: center;
  transition: 0.3s background-color ease, 0.3s box-shadow ease;
  height: 50px;
  box-sizing: border-box;
  padding: 15px;
}
 .tab + label:hover {
  background-color: #f9f9f9;
  box-shadow: 0 1px 0 #f4f4f4 inset;
}
 .tab__content {
  padding: 10px 25px;
  background-color: transparent;
  overflow-y: scroll;
  height: calc(100% - 94px);
  position: absolute;
  width: 100%;
  z-index: -1;
  opacity: 0;
  left: 0;
  font-family: Helvetica, Arial, sans-serif;
  transform: translateY(-3px);
  border-radius: 6px;
}

.form-inline {
  align-self: flex-end;
  display: flex;
  width: 100%;
}

.text-message {
  flex: 1 0 150px;
}

.send-button {
  flex: 0 0 50px;
}
/* boring stuff */
 body {
  font-family: 'Helvetica', sans-serif;
  background-color: #e7e7e7;
  color: #777;
  padding: 30px 0;
  font-weight: 300;
}
 .container {
  margin: 0 auto;
  display: block;
  max-width: 800px;
}
 .container > *:not(.tab-wrap) {
  padding: 0 80px;
}
 h1, h2 {
  margin: 0;
  color: #444;
  text-align: center;
  font-weight: 400;
}
 h2 {
  font-size: 1em;
  margin-bottom: 30px;
}
 h3 {
  font-weight: 400;
}
 p {
  line-height: 1.6;
  margin-bottom: 20px;
}

.smallCard {
  height: 35px;
  width: 20px;
  rotate: 90deg;
  margin: -7px 5px -14px 5px;
  border: 1px solid black;
}

@media (max-width: 1200px) {
  body {
    font-size: 10px;
  }

  .tab__content {
    font-size: 10px;
  }

  .smallCard {
    height: 17px;
    width: 10px;
    rotate: 90deg;
    margin: -3px 2px -7px 2px;
    border: 1px solid black;
  }
}
</style>
