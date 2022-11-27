<template>
  <div class="main-state-info">
    <div v-for="(player, i) in players" :key="i" class="player-section">
      <ul>
        <li><span :id="'dotp'+ (i+1)" class="dot"></span> <strong>{{ limit(player.name, 12) }}</strong></li>
        <li>- {{ player.tickets }} tickets</li>
        <li>- {{ player.cards }} cards</li>
        <li>- {{ player.wagons }} wagons</li>
        <li>- {{ player.points }} points</li>
      </ul>
    </div>
  </div>
</template>

<script>

export default {
  name: 'StateInfo',
  props: ['player1', 'player2'],
  methods: {
    limit (text, maxChars) {
      if (text.length <= maxChars) {
        return text
      }
      const firstWord = text.split(' ')[0]
      return firstWord.length > maxChars ? firstWord.substring(0, maxChars - 1) + '.' : firstWord
    }
  },
  computed: {
    players () {
      return [this.player1, this.player2]
    }
  }
}

</script>

<style scoped>

li {
  font-size: min(1vw, 20px);
}

ul {
  list-style-type: none;
  margin: 0;
  padding: 0;
}

.main-state-info {
  display: flex;
  justify-content: space-around;
  background-image: linear-gradient(to right, rgba(255, 182, 193, 0.5), white, rgb(173, 216, 230, 0.5));
  padding: 10px;
  height: 100%;
}
.dot {
  height: 0.75vw;
  width: 0.75vw;
  border-radius: 50%;
  border: 1px solid black;
  display: inline-block;
}
#dotp1 {
  background-color:lightpink;
}
#dotp2 {
  background-color: lightblue;
}
</style>
