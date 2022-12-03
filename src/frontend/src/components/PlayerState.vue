<template>
<div class="player-state">
  <div class="tickets-container">
    <div v-for="ticket in tickets" :key="ticket.id" class="ticket">
      <tr><td>{{ ticket.display }}</td></tr>
    </div>
  </div>
  <div class="cards-container">
    <div v-if="blacks" class="card">
      <div class="number"> {{ blacks > 1 ? blacks : '' }} </div>
      <Card color="BLACK" />
    </div>
    <div v-if="violets" class="card">
      <div class="number"> {{ violets > 1 ? violets : '' }} </div>
      <Card color="VIOLET" />
    </div>
    <div v-if="blues" class="card">
      <div class="number"> {{ blues > 1 ? blues : '' }} </div>
      <Card color="BLUE" />
    </div>
    <div v-if="greens" class="card">
      <div class="number"> {{ greens > 1 ? greens : '' }} </div>
      <Card color="GREEN" />
    </div>
    <div v-if="yellows" class="card">
      <div class="number"> {{ yellows > 1 ? yellows : '' }} </div>
      <Card color="YELLOW" />
    </div>
    <div v-if="oranges" class="card">
      <div class="number"> {{ oranges > 1 ? oranges : '' }} </div>
      <Card color="ORANGE" />
    </div>
    <div v-if="reds" class="card">
      <div class="number"> {{ reds > 1 ? reds : '' }} </div>
      <Card color="RED" />
    </div>
    <div v-if="whites" class="card">
      <div class="number"> {{ whites > 1 ? whites : '' }} </div>
      <Card color="WHITE" />
    </div>
    <div v-if="locomotives" class="card">
      <div class="number"> {{ locomotives > 1 ? locomotives : '' }} </div>
      <Card color="LOCOMOTIVE" />
    </div>
  </div>
</div>
</template>

<script>
import Card from '@/components/GameCard.vue'

export default {
  props: ['tickets', 'cards'],
  components: {
    Card
  },
  computed: {
    blacks () {
      return this.cards.filter(c => c === 'BLACK').length
    },
    violets () {
      return this.cards.filter(c => c === 'VIOLET').length
    },
    blues () {
      return this.cards.filter(c => c === 'BLUE').length
    },
    greens () {
      return this.cards.filter(c => c === 'GREEN').length
    },
    yellows () {
      return this.cards.filter(c => c === 'YELLOW').length
    },
    oranges () {
      return this.cards.filter(c => c === 'ORANGE').length
    },
    reds () {
      return this.cards.filter(c => c === 'RED').length
    },
    whites () {
      return this.cards.filter(c => c === 'WHITE').length
    },
    locomotives () {
      return this.cards.filter(c => c === 'LOCOMOTIVE').length
    }
  }
}
</script>

<style scoped>
.player-state {
  display: flex;
  height: 120px;
}
.tickets-container {
  width: 450px;
  height: 116px;
  border: 2px solid #444;
  border-radius: 5px;
  box-shadow: 1px 1px #222;
  overflow-y: scroll;
}

.cards-container {
  display: flex;
  padding-left: 40px;
  gap: 40px;
  align-items: center;
}
.card {
  display: flex;
  justify-content: center;
  justify-items: center;
  padding: 5px;
  border: 1px solid #444;
  border-radius: 5px;
  background-color: #eee;
}

.number {
  position: absolute;
  margin-top: 15px;
  font-size: 50px;
  color: #eee;
  text-shadow: -3px 0 black, 0 3px black, 3px 0 black, 0 -3px black;
}

@media (max-width: 1200px) {
  .cards-container {
    padding-left: 10px;
    gap: 10px;
  }

  .number {
  position: absolute;
  margin-top: 5px;
  font-size: 30px;
  color: #eee;
  text-shadow: -3px 0 black, 0 3px black, 3px 0 black, 0 -3px black;
}
}
</style>
