<template>
  <div class="game-setup">
    <form v-if="isFormNeeded" class="login-form" @submit.prevent="sendForm">
      <h1>Setup</h1>
    <div class="form-group">
        <label for="nameInput">
          <input v-model.trim="name" type="text" id="nameInput" autocomplete="off"
         class="form-control" placeholder="Player Name" maxlength="25" required />
          <span>Player Name</span>
        </label>
    </div>
    <div v-if="isShortNameRequired" class="form-group">
        <label for="shortNameInput">
          <input v-model.trim="gameName" type="text" id="shortNameInput" class="form-control" autocomplete="off"
         placeholder="Game Name" maxlength="15" required />
         <span>Game Name</span>
        </label>
    </div>
    <input id="send" class="submit-button" type="submit" :value="actionName"/>
    </form>
    <div v-if="isWaiting" class="info">
      <p>Waiting for a courageous player...</p>
    </div>
    <div v-if="isWaitingError" class="info">
      <p>The server cannot find any game with this name. Are you sure you wrote it correctly?</p>
    </div>
    <div v-if="isGameNameConfirmed" class="info">
      <p>Game created! Share your game name to your friend and start playing!</p>
      <p>Game name: <strong>{{ chosenGameName }}</strong></p>
    </div>
    <div v-if="isGameNameRectified" class="info">
      <p>Game created! However it seemed your game name was already taken...</p>
      <p>Here is a new one: <strong>{{ chosenGameName }}</strong> </p>
      <p>Share it with a friend and start playing!</p>
    </div>

    <div class="train-container">
      <div class="track"></div>
      <div :class="{train: true, leaving: hasChosenMode}">
        <div class="front"></div>
        <div class="wheels">
          <div class="smallWheel" id="w1"></div>
          <div class="smallWheel" id="w2"></div>
          <div class="smallWheel" id="w3"></div>
          <div class="smallWheel" id="w4"></div>
          <div class="smallWheel" id="w5"></div>
          <div class="smallWheel" id="w6"></div>
          <div class="smallWheel" id="w7"></div>
          <div class="smallWheel" id="w8"></div>
          <div class="big"></div>
        </div>
        <div class="cord"></div>
        <div class="coach" id="c1"><button @click="joinAny" class="coach-button">Join Any Game</button></div>
        <div class="coach" id="c2"><button @click="joinCustom" class="coach-button">Join Custom Game</button></div>
        <div class="coach" id="c3"><button @click="createCustom" class="coach-button">Create Custom Game</button></div>
        <div class="steam" id="s1"></div>
        <div class="steam" id="s2"></div>
      </div>
    </div>
  </div>
</template>

<script>

export default {
  props: ['sendSetupGame', 'state', 'chosenGameName'],
  data () {
    return {
      chosenMode: '',
      name: '',
      gameName: '',
      actionName: '',
      dataSent: false
    }
  },
  computed: {
    hasChosenMode () {
      return this.chosenMode !== ''
    },
    isFormNeeded () {
      return this.hasChosenMode && (!this.dataSent || this.chosenMode === 'JOIN_SPECIFIC_GAME')
    },
    isShortNameRequired () {
      return this.chosenMode === 'INIT_GAME' || this.chosenMode === 'JOIN_SPECIFIC_GAME'
    },
    isWaiting () {
      return this.dataSent && (this.chosenMode === 'JOIN_ANY_GAME' || (this.chosenMode === 'INIT_GAME' && this.chosenGameName === ''))
    },
    isWaitingError () {
      return this.dataSent && this.chosenMode === 'JOIN_SPECIFIC_GAME'
    },
    isGameNameConfirmed () {
      return this.state === 'ok'
    },
    isGameNameRectified () {
      return this.state === 'nok'
    }
  },
  methods: {
    createCustom () {
      this.chosenMode = 'INIT_GAME'
      this.actionName = 'Create'
    },
    joinCustom () {
      this.chosenMode = 'JOIN_SPECIFIC_GAME'
      this.actionName = 'Join'
    },
    joinAny () {
      this.chosenMode = 'JOIN_ANY_GAME'
      this.actionName = 'Join'
    },
    sendForm () {
      if (!this.chosenMode !== '' && this.name !== '' && (this.chosenMode === 'JOIN_ANY_GAME' || this.gameName !== '')) {
        this.sendSetupGame(this.chosenMode, (btoa(unescape(encodeURIComponent(this.name))) + ' ' + this.gameName).trim())
        this.dataSent = true
      }
    }
  }
}
</script>

<style scoped>
.game-setup {
  display: flex;
  flex-direction: column;
  width: 100%;
  height: 100%;
  align-items: center;
  background-image: linear-gradient(lightblue, white, lightgray)
}

.info {
  display: flex;
  flex-direction: column;
  align-items: center;
  border: 1px solid black;
  border-radius: 10px;
  padding: 10px;
  box-shadow: 5px 5px #999;
}

p {
  font-family: Helvetica, Arial, sans-serif;
}

/* FORM RELATED */

.login-form {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 50px 40px;
  animation: leave 1s linear forwards reverse;
}

h1 {
  margin: 0 auto 40px;
  font: 20px Helvetica;
  text-transform: uppercase;
  letter-spacing: 3px;
}

form label {
  position: relative;
  display: block;
}
form label input {
  font-family: Helvetica, Arial, sans-serif;
  box-sizing: border-box;
  display: block;
  border: none;
  padding: 10px;
  margin-bottom: 10px;
  outline: none;
  transition: all 0.2s ease-in-out;
}
form label input::placeholder {
  transition: all 0.2s ease-in-out;
  color: #999;
  font-family: Helvetica, Arial, sans-serif;
}
form label input:focus, form label input.populated {
  padding-top: 28px;
  padding-bottom: 12px;
}
form label input:focus::placeholder, form label input.populated::placeholder {
  color: transparent;
}
form label input:focus + span, form label input.populated + span {
  opacity: 1;
  top: 10px;
}
form label span {
  color: #ee3016;
  font: 13px Helvetica, Arial, sans-serif;
  position: absolute;
  top: 0px;
  left: 20px;
  opacity: 0;
  transition: all 0.2s ease-in-out;
}
form input[type="submit"] {
  transition: all 0.2s ease-in-out;
  font: 18px Helvetica, Arial, sans-serif;
  border: none;
  background: #ee3016;
  color: #fff;
  padding: 16px 40px;
}
form input[type="submit"]:hover {
  background: #be1006;
}

/* TRAIN RELATED */

.train-container {
  display:flex;
  justify-content: center;
  align-items: flex-end;
  position: relative;
  overflow: hidden;
  flex-grow: 1;
  width: min(100%, 1340px); /* 400px */
}

.track {
  position: absolute;
  width: 100%;
  height: 5px;
  background-color: #422;
  bottom: 20px;
}

.track:before {
  content:"";
  position: absolute;
  width: 10px;
  height: 5px;
  background-color: #422;
  top: 4px;
  box-shadow: 20px 0 #422, 40px 0 #422, 60px 0 #422, 80px 0 #422, 100px 0 #422, 120px 0 #422, 140px 0 #422, 160px 0 #422,
  180px 0 #422, 200px 0 #422, 220px 0 #422, 240px 0 #422, 260px 0 #422, 280px 0 #422, 300px 0 #422, 320px 0 #422,
  340px 0 #422, 360px 0 #422, 380px 0 #422, 400px 0 #422, 420px 0 #422, 440px 0 #422, 460px 0 #422, 480px 0 #422,
  500px 0 #422, 520px 0 #422, 540px 0 #422, 560px 0 #422, 580px 0 #422, 600px 0 #422, 620px 0 #422, 640px 0 #422,
  660px 0 #422, 680px 0 #422, 700px 0 #422, 720px 0 #422, 740px 0 #422, 760px 0 #422, 780px 0 #422, 800px 0 #422,
  820px 0 #422, 840px 0 #422, 860px 0 #422, 880px 0 #422, 900px 0 #422, 920px 0 #422, 940px 0 #422, 960px 0 #422,
  980px 0 #422, 1000px 0 #422, 1020px 0 #422, 1040px 0 #422, 1060px 0 #422, 1080px 0 #422, 1100px 0 #422, 1120px 0 #422,
  1140px 0 #422, 1160px 0 #422, 1180px 0 #422, 1200px 0 #422, 1220px 0 #422, 1240px 0 #422, 1260px 0 #422, 1280px 0 #422,
  1300px 0 #422, 1320px 0 #422, 1340px 0 #422, 1360px 0 #422, 1380px 0 #422, 1400px 0 #422, 1420px 0 #422, 1440px 0 #422,
  -20px 0 #422, -40px 0 #422, -60px 0 #422, -80px 0 #422, -100px 0 #422, -120px 0 #422, -140px 0 #422;
  animation: movetrack 3s linear infinite reverse;
}

@keyframes movetrack {
  from {left:-100px;}
  to {left:100px;}
}

.train {
  position: absolute;
  width: 50px;
  height: 30px;
  border: 5px solid #333;
  background-color: maroon;
  border-radius: 0 10px 0 0;
  bottom: 52.5px;
  left: calc(50% + 225px);
  animation: scale 1s linear infinite;
}

.leaving {
  animation: leave 10s linear forwards;
}

.train:before {
  content: "";
  position: absolute;
  border: 5px solid #333;
  background-color: #335c67;
  width: 35px;
  height: 60px;
  left: -45px; top: -35px;
}

.train:after {
  content: "";
  position: absolute;
  border: 5px solid #333;
  width: 100px;
  height: 5px;
  border-radius: 10px;
  background-color: #fff3b0;
  top: 30px;
  left: -50px;
}

.front {
  position: absolute;
  border: 5px solid #333;
  background-color: #aef9e0;
  box-shadow: inset 2px -5px rgba(255, 255, 255, 0.3);
  width: 20px;
  height: 30px;
  left: -37.5px;
  top: -20px;
}

.front:before {
  content: "";
  position: absolute;
  background-color: #333;
  width: 15px;
  height: 5px;
  border-radius: 10px;
  top: 25px;
  left: 70px;
  box-shadow: 0 10px #333, -50px -45px #333, -22px -41px #333, -11px -41px #333;
}

.front:after {
  content: "";
  position: absolute;
  width: 12px;
  height: 20px;
  border: 5px solid #333;
  left: 50px;
  top: -16px;
  background-color: #adb5bd;
}

.wheels {
  position: absolute;
  z-index: 1;
}

.smallWheel {
  position: absolute;
  border: 5px solid #333;
  border-radius: 50%;
  width: 15px;
  height: 15px;
  top: 40px;
  background-color: #e09f3e;
  box-shadow: inset 2px 2px white;
  z-index: 2;
  animation: spin .25s infinite linear;
}

#w1 {
  left: 30px;
}

#w2 {
  left: 0;
}

#w3 {
  left: -310px;
}

#w4 {
  left: -230px;
}

#w5 {
  left: -175px;
}

#w6 {
  left: -95px;
}

#w7 {
  left: -365px;
}

#w8 {
  left: -445px;
}

.big {
  position: absolute;
  border: 5px solid #333;
  border-radius: 50%;
  width: 25px;
  height: 25px;
  background-color: #e09f3e;
  box-shadow: inset 2px 2px white;
  top: 30px;
  left: -40px;
  animation: spin .5s infinite linear;
}

.cord {
  position: absolute;
  width: 10px;
  height: 5px;
  background-color: #333;
  top: 35px;
  left: -59px;
  z-index: 3;
}

.cord:before {
  content: "";
  position: absolute;
  height: 5px;
  width: 70px;
  background-color: #333;
  top: 15px;
  left: 35px;
}

.cord:after {
  content: "";
  position: absolute;
  background-color: #333;
  width: 15px;
  height: 15px;
  border-radius: 50%;
  top: 5px;
  left: 29px;
}

.coach {
  position: absolute;
  width: 120px;
  height: 70px;
  border: 5px solid #333;
  background-color: #9e2a2b;
  bottom: -20px;
}

#c1 {
  left: -185px;
}

#c1:before {
  content: "";
  position: absolute;
  width: 10px;
  height: 5px;
  background-color: #333;
  bottom: 5px;
  left: -15px;
}

#c2 {
  left: -320px;
}

#c2:before {
  content: "";
  position: absolute;
  width: 10px;
  height: 5px;
  background-color: #333;
  bottom: 5px;
  left: -15px;
}

#c3 {
  left: -455px;
}

#c1:after {
  content: "";
  position: absolute;
  background-color: #333;
  height: 5px;
  width: 85px;
  bottom: -11px;
  left: 15px;
  box-shadow: -135px 0 #333, -270px 0 #333;
  z-index: 3;
}

@keyframes spin {
  from {transform: rotate(0deg);}
  to {transform: rotate(360deg);}
}

.steam {
  position: absolute;
  width: 20px;
  height: 20px;
  background-color: #fff;
  border-radius: 50%;
  opacity: 0;
  top: -30px;
  left: 25.5px;
}

@keyframes leave {
  100% {transform: translateX(1340px);}
}

@keyframes up {
  0% {transform: translateY(0) translateX(0) scale(0); opacity: 1;}
  100% {transform: translateY(-110px) translateX(-80px) scale(1.5); opacity: 0.2;}
}

@keyframes scale {
  0% {transform: scaleY(1);}
  50% {transform: scaleY(0.98);}
  100% {transform: scaleY(1);}
}

#s1 {
  animation: up 2.5s ease-out infinite;
}

#s2 {
  animation: up 1.7s ease-out infinite 1s;
}

#s2:before {
  content: "";
  position: absolute;
  left: 5px;
  width: 15px;
  height: 15px;
  background-color: #fff;
  border-radius: 50%;
  top: 15px;
}

.coach-button {
  background-color: red;
  color: white;
  width: 100%;
  height: 100%;
}

.coach-button:hover {
  background-color: #422;
}

@media (max-width: 1200px) {
  .login-form {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 0px 0px;
  animation: leave 1s linear forwards reverse;
}
}

</style>
