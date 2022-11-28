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

.route {
  align-self: baseline;
  justify-self: baseline;
}

.chunk {
  position: absolute;
  width: 1%;
  height: 4%;
}

#AT1_STG_1_1 {
  left: 86%;
  top: 28%;
  transform: rotate(-66deg);
}

#AT1_STG_1_2 {
  left: 82%;
  top: 25.3%;
  transform: rotate(-66deg);
}

#AT1_STG_1_3 {
  left: 78.5%;
  top: 22.9%;
  transform: rotate(-66deg);
}

#AT1_STG_1_4 {
  left: 75%;
  top: 20.4%;
  transform: rotate(-66deg);
}

#AT2_VAD_1_1 {
  left: 79.5%;
  top: 32%;
  rotate: 85deg;
}

#BAD_BAL_1_1 {
  left: 46%;
  top: 15%;
  rotate: 120deg;
}

#BAD_BAL_1_2 {
  left: 42.25%;
  top: 13%;
  rotate: 102deg;
}

#BAD_BAL_1_3 {
  left: 38%;
  top: 13%;
  rotate: 85deg;
}

#BAD_OLT_1_1 {
  left: 46%;
  top: 20%;
  rotate: 53deg;
}

#BAD_OLT_1_2 {
  left: 42.5%;
  top: 23.5%;
  rotate: 53deg;
}

#BAD_ZUR_1_1 {
  left: 51%;
  top: 20%;
  rotate: 120deg;
}

#BAL_DE1_1_1 {
  left: 35.2%;
  top: 9.5%;
  rotate: 10deg;
}

#BAL_DEL_1_1 {
  left: 33.5%;
  top: 18%;
  rotate: 37deg;
}

#BAL_DEL_1_2 {
  left: 30.5%;
  top: 21.5%;
  rotate: 65deg;
}

#BAL_OLT_1_1 {
  left: 36.7%;
  top: 18.5%;
  rotate: 155deg;
}

#BAL_OLT_1_2 {
  left: 38.5%;
  top: 23.2%;
  rotate: 145deg;
}

#BEL_LOC_1_1 {
  left: 63.3%;
  top: 80.5%;
  rotate: 80deg;
}

#BEL_LUG_1_1 {
  left: 65.5%;
  top: 84%;
  rotate: 15deg;
}

#BEL_LUG_2_1 {
  left: 67.2%;
  top: 84.5%;
  rotate: 15deg;
}

#BEL_WAS_1_1 {
  left: 64.5%;
  top: 76%;
  rotate: -35deg;
}

#BEL_WAS_1_2 {
  left: 62.4%;
  top: 71.5%;
  rotate: -35deg;
}

#BEL_WAS_1_3 {
  left: 60.3%;
  top: 67%;
  rotate: -35deg;
}

#BEL_WAS_1_4 {
  left: 58.2%;
  top: 62.5%;
  rotate: -35deg;
}

#BEL_WAS_2_1 {
  left: 66%;
  top: 74.6%;
  rotate: -35deg;
}

#BEL_WAS_2_2 {
  left: 63.9%;
  top: 70.1%;
  rotate: -35deg;
}

#BEL_WAS_2_3 {
  left: 61.8%;
  top: 65.6%;
  rotate: -35deg;
}

#BEL_WAS_2_4 {
  left: 59.7%;
  top: 61.1%;
  rotate: -35deg;
}

</style>
