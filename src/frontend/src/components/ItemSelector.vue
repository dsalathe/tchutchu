<template>
  <div class="selector">
    <h3><slot>Select items</slot></h3>
    <button v-for="(item, i) in items" :key="i" :class="{itemChoice: true, selected: isSelected(item.id)}" @click="triggerItem(item.id)">{{item.display}}</button>
    <button class="confirmer" @click="onConfirm">Confirm choice </button>
  </div>
</template>

<script>

export default {
  props: ['items', 'confirmChoice', 'minItems'],
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
      if (this.selectedItems.size >= parseInt(this.minItems)) {
        this.confirmChoice([...this.selectedItems])
      }
    },
    isSelected (item) {
      return this.selectedItems.has(item)
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
  background-color: rgb(189, 190, 115);
  opacity: 0.25;
}

.selector:hover {
  opacity: 1;
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
</style>
