<template>
  <div class="deck-view">
    <button @click="drawTickets" :disabled="isTicketDisabled"> TICKETS<br/><progress :value="deckTicketsSize" max="46">???</progress></button>
    <button class="card" v-for="(card, i) in cards" :key="i" @click="onDraw(i)" :disabled="areCardsDisabled">
      <Card :color="card" />
    </button>
    <button @click="onDraw(-1)" :disabled="areCardsDisabled"> DECK<br/><progress :value="deckCardsSize" max="110">???</progress> </button>
  </div>
</template>

<script>
import Card from '@/components/GameCard.vue'

export default {
  props: ['cards', 'deckTicketsSize', 'deckCardsSize', 'deckDiscardSize', 'isDisabled', 'drawFirst', 'drawSecond', 'drawTickets'],
  components: {
    Card
  },
  data () {
    return {
      isFirst: true
    }
  },
  methods: {
    onDraw (index) {
      if (this.isFirst) {
        this.drawFirst(index)
      } else {
        this.drawSecond(index)
      }
      this.isFirst = !this.isFirst
    }
  },
  computed: {
    isTicketDisabled () {
      return this.isDisabled || this.deckTicketsSize < 3 || !this.isFirst
    },
    areCardsDisabled () {
      return this.isDisabled || (this.deckCardsSize + this.deckDiscardSize) < 2
    }
  }
}
</script>

<style scoped>

.deck-view {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10px;
  max-height: min(40vw, 700px);
  overflow-y: scroll;
  padding-top: 30px;
}

button {
  flex-grow: 0;
}

</style>
