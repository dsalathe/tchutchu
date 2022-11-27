<template>
  <div id="board-game">
    <ItemSelector v-if="tickets.length" :items="tickets" :confirmChoice="onTicketsSelected" minItems="3"
    class="selector" >Please select at least 3 tickets</ItemSelector>
    <img src="/img/game/map.png" alt="Board Game"/>
  </div>
</template>

<script>

import ItemSelector from '@/components/ItemSelector.vue'

export default {
  name: 'BoardGame',
  components: {
    ItemSelector
  },
  props: ['displayedTickets', 'onTicketsSelected', 'dataMap'],
  computed: {
    tickets () {
      return this.displayedTickets.map(dt => ({ id: dt, display: this.displayTicket(dt) }))
    }
  },
  methods: {
    displayTicket (ticket) {
      const ticketData = this.dataMap.tickets[ticket]
      if (ticketData.to.length > 1) {
        return ticketData.from[0].name + ' - {' + ticketData.to.map((to, i) => to.name + ' (' + ticketData.points[i] + ')').join(', ') + '}'
      }
      return ticketData.from[0].name + ' - ' + ticketData.to[0].name + ' (' + ticketData.points[0] + ')'
    }
  }
}
</script>

<style scoped>

#board-game {
  display: flex;
  justify-content: center;
  align-items: center;
  width: 100%;
}

.selector {
  position: absolute;
}

img {
  width: min(65vw, 1120px);
  display: flex;
}

</style>
