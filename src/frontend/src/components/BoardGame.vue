<template>
  <div id="board-game">
    <div v-for="(r, i) in dataMap.routes" :key="r.id" class="route">
      <div v-for="chunk in r.length" :key="r.id+'_'+chunk+i" :id="r.id+'_'+chunk" @mouseover="increaseRoute(i)" @mouseleave="decreaseRoute(i)"
      :style="{ backgroundColor: getStyle(r, i), border: getBorder(r, i), scale: getTransform(r, i)}" @click="seize(r, i)"
      class="chunk">{{ getContent(i) }}
      </div>
    </div>
    <div v-for="s in dataMap.stations" :key="'station'+s.id" :style="{ backgroundColor: getBCStation(s), border: getBorderStation(s)}"
    class="station" :id="'s'+s.id"><div :class="{destination: isDestination(s), destinationSelected: isPossibleDestination(s)}"></div></div>
    <ItemSelector v-if="displayedTickets.length" :items="displayedTickets" :confirmChoice="onTicketsSelected" :minItems="minItems"
    maxItems="5" :areCards="false" :addPossibleDestination="addPossibleDestination" :removePossibleDestination="removePossibleDestination"
    :clearPossibleDestinations="clearPossibleDestinations" class="selector" >Please select at least {{ minItems }} ticket{{minItems > 1 ? 's' : ''}}</ItemSelector>
    <ItemSelector v-if="possibleCardsForSeizingDisplayed.length" :items="possibleCardsForSeizingDisplayed"
    :confirmChoice="OnCardsForSeizing" :minItems="1" :areCards="true" :addPossibleDestination="addPossibleDestination"
    :maxItems="1" :removePossibleDestination="removePossibleDestination"
    :clearPossibleDestinations="clearPossibleDestinations" class="selector" >Select how you want to seize the route</ItemSelector>
    <ItemSelector v-if="tunnelAdditonalCards.length" :items="tunnelAddtionalCardsDisplayed"
    :confirmChoice="OnCardsForSeizingTunnel" :minItems="0" :areCards="true" :addPossibleDestination="addPossibleDestination"
    :maxItems="1" :removePossibleDestination="removePossibleDestination"
    :clearPossibleDestinations="clearPossibleDestinations" class="selector" >Select an option if you want to pursue (Or None to renounce)</ItemSelector>
    <img src="/img/game/map.png" alt="Board Game"/>
  </div>
</template>

<script>

import ItemSelector from '@/components/ItemSelector.vue'
import '@/assets/styles/stations.css'

export default {
  name: 'BoardGame',
  components: {
    ItemSelector
  },
  props: ['displayedTickets', 'onTicketsSelected', 'dataMap', 'isDisabled', 'cards',
    'seizeRoute', 'isDrawing', 'takenRoutesP1', 'takenRoutesP2', 'tunnelAdditonalCards',
    'seizeTunnel', 'wagons', 'tickets'],
  data () {
    return {
      routesSize: Array.from({ length: 100 }, _ => false),
      colorNames: ['BLACK', 'VIOLET', 'BLUE', 'GREEN', 'YELLOW', 'ORANGE', 'RED', 'WHITE'],
      routeToBeTaken: -1,
      possibleCardsForSeizing: [],
      currentSelectedTickets: []
    }
  },
  computed: {
    minItems () {
      return this.displayedTickets.length === 5 ? 3 : 1
    },
    possibleCardsForSeizingDisplayed () {
      return this.possibleCardsForSeizing.map(a => ({ id: a.join(','), display: this.countsToFlattenCards(a).join('|') }))
    },
    tunnelAddtionalCardsDisplayed () {
      return this.tunnelAdditonalCards.map(a => ({ id: a.join(','), display: a.map(i => i === 8 ? 'LOCOMOTIVE' : this.colorNames[i]).join('|') }))
    },
    takenRoutes () {
      return this.takenRoutesP1.concat(this.takenRoutesP2)
    },
    destinations () {
      return new Set(this.dataMap.tickets.filter(t => this.tickets.includes(t.id))
        .flatMap(t => t.from.map(f => f.id).concat(t.to.map(f => f.id))))
    },
    possibleDestinations () {
      return new Set(this.dataMap.tickets.filter(t => this.currentSelectedTickets.includes(t.id))
        .flatMap(t => t.from.map(f => f.id).concat(t.to.map(f => f.id))))
    }
  },
  methods: {
    isDestination ({ id }) {
      return this.destinations.has(id)
    },
    isPossibleDestination ({ id }) {
      return this.possibleDestinations.has(id)
    },
    getBCStation ({ id }) {
      if (this.isPossibleDestination({ id })) {
        return 'mediumaquamarine'
      }
      return this.isDestination({ id }) ? 'white' : 'transparent'
    },
    getBorderStation ({ id }) {
      if (this.isPossibleDestination({ id })) {
        return '1px dashed gray'
      }
      return this.isDestination({ id }) ? '1px solid gray' : '0px solid gray'
    },
    addPossibleDestination (id) {
      this.currentSelectedTickets.push(id)
    },
    removePossibleDestination (id) {
      this.currentSelectedTickets = this.currentSelectedTickets.filter(i => i !== id)
    },
    clearPossibleDestinations () {
      this.currentSelectedTickets = []
    },
    countsToFlattenCards (counts) {
      const [count1, color1, count2, color2] = counts
      return Array(count1).fill(this.colorNames[color1]).concat(Array(count2).fill(color2 === 8 ? 'LOCOMOTIVE' : this.colorNames[color2]))
    },
    isUnderground (route) {
      return route.level === 'UNDERGROUND'
    },
    getStyle (route, index) {
      if (this.takenRoutesP1.includes(index)) {
        return 'lightpink'
      } else if (this.takenRoutesP2.includes(index)) {
        return 'lightblue'
      }
      return {
        BLACK: 'rgb(70,70,70)',
        VIOLET: 'violet',
        BLUE: 'deepskyblue',
        GREEN: 'green',
        YELLOW: 'yellow',
        ORANGE: 'orange',
        RED: 'red',
        WHITE: 'white',
        NEUTRAL: 'lightgray'
      }[route.color]
    },
    getBorder (route, index) {
      if (this.takenRoutes.includes(index)) {
        return '1px dotted black'
      }
      return route.level === 'UNDERGROUND' ? '2px dashed black' : '2px solid black'
    },
    increaseRoute (index) {
      this.routesSize[index] = true
    },
    decreaseRoute (index) {
      this.routesSize[index] = false
    },
    getTransform (route, index) {
      return this.routesSize[index] && !this.isDisabled && this.canClaim(route) && !this.isDrawing && !(this.takenRoutes.includes(index)) ? 1.1 : 1
    },
    canClaim (route) {
      const nLoco = route.level === 'OVERGROUND' ? 0 : this.cards.filter(c => c === 'LOCOMOTIVE').length
      if (route.color !== 'NEUTRAL') {
        return this.cards.filter(c => c === route.color).length + nLoco >= route.length && route.length <= this.wagons
      }
      return this.colorNames.some(color => this.cards.filter(card => card === color).length >= (route.length - nLoco)) && route.length <= this.wagons
    },
    seize (route, index) {
      if (this.canClaim(route) && !this.isDisabled && !this.isDrawing && !(this.takenRoutes.includes(index))) {
        const possibleCards = []
        const route = this.dataMap.routes[index]
        const maxLoco = route.level === 'OVERGROUND' ? 0 : this.cards.filter(c => c === 'LOCOMOTIVE').length
        for (let i = 0; i <= Math.min(maxLoco, route.length); i++) {
          let j = -1
          for (const color of this.colorNames) {
            j++
            if ((route.color === 'NEUTRAL' || route.color === color) &&
              this.cards.filter(c => c === color).length >= route.length - i) {
              possibleCards.push([route.length - i, j, i, 8])
              if (route.length - i === 0) {
                break
              }
            }
          }
        }
        console.log(possibleCards)
        if (possibleCards.length === 1) {
          this.sendRouteSeized(index, possibleCards[0])
        } else {
          this.routeToBeTaken = index
          this.possibleCardsForSeizing = possibleCards
        }
      }
    },
    sendRouteSeized (index, counts) {
      const [count1, color1, count2, color2] = counts
      const arrayCards = Array(count1).fill(color1).concat(Array(count2).fill(color2))
      this.seizeRoute(index, arrayCards)
    },
    OnCardsForSeizing (item) {
      this.sendRouteSeized(this.routeToBeTaken, item[0].split(',').map(c => parseInt(c)))
      this.routeToBeTaken = -1
      this.possibleCardsForSeizing = []
    },
    OnCardsForSeizingTunnel (item) {
      this.seizeTunnel(item)
    },
    getContent (index) {
      return this.takenRoutes.includes(index) ? 'X' : ''
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

@media (max-width: 1200px) {
  .selector {
    right: 100%;
    justify-self: flex-end;
  }
}

@import '@/assets/styles/routes.css'
</style>
