<template>
  <div class="selector">
    <h3><slot>Select items</slot></h3>
    <button v-for="(item, i) in items" :key="i"
    :class="{itemChoice: true, selected: isSelected(item.id)}"
    @click="triggerItem(item.id)">{{displayItem(item.display)}}
    <span v-for="card in listOfCards(i)" :key="card"> <GameCard :color="card" class="smallCard" /> </span></button>
    <button class="confirmer" @click="onConfirm" :disabled="!isThereEnoughItems">Confirm choice </button>
  </div>
</template>

<script>

import GameCard from '@/components/GameCard.vue'

export default {
  props: ['items', 'confirmChoice', 'minItems', 'maxItems', 'areCards'],
  components: {
    GameCard
  },
  data () {
    return {
      selectedItems: new Set()
    }
  },
  methods: {
    triggerItem (item) {
      if (this.selectedItems.has(item)) {
        this.selectedItems.delete(item)
      } else {
        this.selectedItems.add(item)
      }
    },
    onConfirm () {
      if (this.isThereEnoughItems) {
        this.confirmChoice([...this.selectedItems])
      }
    },
    isSelected (item) {
      return this.selectedItems.has(item)
    },
    displayItem (item) { // TODO find a better way to display cards than special logic here
      if (this.areCards) {
        return ''
      }
      return item
    },
    listOfCards (i) {
      return this.areCards ? this.items[i].display.split('|') : []
    }
  },
  computed: {
    isThereEnoughItems () {
      const min = parseInt(this.minItems)
      const max = parseInt(this.maxItems)
      return (min === -1 || this.selectedItems.size >= min) && (max === -1 || this.selectedItems.size <= max)
    }
  }
}
</script>

<style scoped>

h3, button {
  font-family: Helvetica, Arial, sans-serif;
}

.itemChoice {
  width: 410px;
}

button {
  background-color:palegoldenrod;
}

button:hover {
  background-color: rgb(159, 160, 95);
}

.selected:hover {
  background-color: #be1006;
}

.selector {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10px;
  width: 420px;
  padding-bottom: 50px;
  border-radius: 10px;
  background-color: rgb(200, 200, 135);
  opacity: 0.35;
  max-height: 600px;
  overflow-y: scroll;
}

.selector:hover {
  opacity: 0.97;
}

.selected {
  background-color: #ee3016;
  border: 2px solid #380f09;
}

.confirmer {
  height: 50px;
  font-weight: bold;
  width: 150px;
}

.confirmer {
  --clr: #380f09;
  --outline: 2px solid #380f09;
  color: var(--clr);
  outline: var(--outline);
  border: var(--outline);
  outline-offset: -2px;
  transition: outline-width 200ms ease, outline-offset 200ms ease;
}
.confirmer:hover, .confirmer:focus {
  outline: var(--outline);
  outline-width: 8px;
  outline-offset: -8px;
}

.confirmer:disabled,
.confirmer[disabled]:hover {
  background-color: #ccc;
  outline-width: 0px;
}

.smallCard {
  height: 70px;
  width: 40px;
  rotate: 90deg;
  margin: -10px 20px -10px 20px;

}
</style>
