<template>
  <div class="selector" id="selector">
    <div class="item-head" @mousedown="dragMouseDown">
      <h3><slot>Select items</slot></h3>
    </div>
    <div class="item-body">
      <button v-for="(item, i) in items" :key="i"
      :class="{itemChoice: true, selected: isSelected(item.id)}"
      @click="triggerItem(item.id)">{{displayItem(item.display)}}
      <span v-for="card in listOfCards(i)" :key="card"> <GameCard :color="card" class="smallCard" /> </span></button>
    </div>
    <div class="item-footer">
      <button class="confirmer" @click="onConfirm" :disabled="!isThereEnoughItems">Confirm choice </button>
    </div>
  </div>
</template>

<script>

import GameCard from '@/components/GameCard.vue'

export default {
  props: ['items', 'confirmChoice', 'minItems', 'maxItems', 'areCards', 'addPossibleDestination', 'removePossibleDestination', 'clearPossibleDestinations'],
  components: {
    GameCard
  },
  data () {
    return {
      selectedItems: new Set(),
      pos1: 0,
      pos2: 0,
      pos3: 0,
      pos4: 0
    }
  },
  methods: {
    triggerItem (item) {
      if (this.maxItems === 1) {
        if (this.selectedItems.has(item)) {
          if (!this.areCards) {
            this.removePossibleDestination(item)
          }
          this.selectedItems.delete(item)
        } else {
          if (!this.areCards) {
            this.addPossibleDestination(item)
          }
          this.selectedItems = new Set()
          this.selectedItems.add(item)
        }
      } else {
        if (this.selectedItems.has(item)) {
          if (!this.areCards) {
            this.removePossibleDestination(item)
          }
          this.selectedItems.delete(item)
        } else {
          if (!this.areCards) {
            this.addPossibleDestination(item)
          }
          this.selectedItems.add(item)
        }
      }
    },
    onConfirm () {
      if (this.isThereEnoughItems) {
        this.clearPossibleDestinations()
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
    },
    dragMouseDown (e) {
      e.preventDefault()
      this.pos3 = e.clientX
      this.pos4 = e.clientY
      document.onmouseup = this.closeDragElement
      document.onmousemove = this.elementDrag
    },
    elementDrag (e) {
      e.preventDefault()
      this.pos1 = this.pos3 - e.clientX
      this.pos2 = this.pos4 - e.clientY
      this.pos3 = e.clientX
      this.pos4 = e.clientY
      const elmnt = document.getElementById('selector')
      elmnt.style.top = (elmnt.offsetTop - this.pos2) + 'px'
      elmnt.style.left = (elmnt.offsetLeft - this.pos1) + 'px'
    },
    closeDragElement () {
      document.onmouseup = null
      document.onmousemove = null
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

.item-head {
  width: calc(100% - 6px);
  cursor: grabbing;
  text-align: center;
  border-radius: 3px;
  margin: 2px 2px 10px 2px;
  border: 1px solid black;
  background-color: rgb(150, 150, 95);
}

.item-body {
  display: flex;
  flex-direction: column;
  gap: 10px;
  overflow-y: scroll;
  max-height: 800px;
}

.item-footer {
  margin-top: 20px;
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
  /* gap: 10px; */
  width: 420px;
  padding-bottom: 10px;
  border-radius: 10px;
  background-color: rgb(200, 200, 135);
  opacity: 0.95;
  resize: both;
  overflow: scroll;
  max-height: 70vh;
  position: absolute;
  z-index: 999;
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

@media (max-width: 1200px) {
  .selector {
    opacity: 0.9;
    font-size: 7px;
    width: 120px;
    padding-bottom: 5px;
  }

  .item-body {
    gap: 2px;
  }
  .confirmer {
    font-size: 7px;
    height: 20px;
    font-weight: bold;
    width: 110px;
  }

  .itemChoice {
    font-size: 10px;
  }

  .smallCard {
  height: 17px;
  width: 10px;
  rotate: 90deg;
  margin: -5px 5px -6px 5px;
}
}
</style>
