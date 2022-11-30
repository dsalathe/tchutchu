<template>
  <div id="board-game">
    <div v-for="(r, i) in dataMap.routes" :key="r.id" class="route">
      <div v-for="chunk in r.length" :key="r.id+'_'+chunk+i" :id="r.id+'_'+chunk" @mouseover="increaseRoute(i)" @mouseleave="decreaseRoute(i)"
      :style="{ backgroundColor: getStyle(r), border: getBorder(r), scale: getTransform(r, i)}" @click="seize(r, i)"
      class="chunk" :underground="isUnderground(r)">
      </div>
    </div>
    <ItemSelector v-if="displayedTickets.length" :items="displayedTickets" :confirmChoice="onTicketsSelected" :minItems="minItems"
    class="selector" >Please select at least {{ minItems }} ticket{{minItems > 1 ? 's' : ''}}</ItemSelector>
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
  props: ['displayedTickets', 'onTicketsSelected', 'dataMap', 'isDisabled', 'cards', 'seizeRoute', 'isDrawing'],
  data () {
    return {
      routesSize: Array.from({ length: 100 }, _ => false)
    }
  },
  computed: {
    minItems () {
      return this.displayedTickets.length === 5 ? 3 : 1
    }
  },
  methods: {
    isUnderground (route) {
      return route.level === 'UNDERGROUND'
    },
    getStyle (route) {
      return {
        BLACK: 'rgb(70,70,70)',
        VIOLET: 'violet',
        BLUE: 'blue',
        GREEN: 'green',
        YELLOW: 'yellow',
        ORANGE: 'orange',
        RED: 'red',
        WHITE: 'white',
        NEUTRAL: 'lightgray'
      }[route.color]
    },
    getBorder (route) {
      return route.level === 'UNDERGROUND' ? '2px dashed black' : '2px solid black'
    },
    increaseRoute (index) {
      this.routesSize[index] = true
    },
    decreaseRoute (index) {
      this.routesSize[index] = false
    },
    getTransform (route, index) {
      return this.routesSize[index] && !this.isDisabled && this.canClaim(route) && !this.isDrawing ? 1.1 : 1
    },
    canClaim (route) {
      const nLoco = this.cards.filter(c => c === 'LOCOMOTIVE').length
      if (route.color !== 'NEUTRAL') {
        return this.cards.filter(c => c === route.color).length + nLoco >= route.length
      }
      return ['BLACK', 'VIOLET', 'BLUE', 'GREEN', 'YELLOW', 'ORANGE', 'RED', 'WHITE']
        .some(color => this.cards.filter(card => card === color).length >= (route.length - nLoco))
    },
    seize (route, index) {
      if (this.canClaim(route) && !this.isDisabled && !this.isDrawing) {
        this.seizeRoute(index)
      }
    }
  }
}
</script>

<style scoped>

#board-game {
  display: flex;
  position: relative;
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

@import '@/assets/styles/routes.css'
</style>
