<template>
  <img :src="source" :class="color" alt="Card"/>
</template>

<script>

export default {
  props: ['color'],
  computed: {
    source () {
      return this.color === 'LOCOMOTIVE' ? '/img/game/locomotive.png' : '/img/game/train-car.png'
    }
  }
}
</script>

<style scoped>

img {
  margin: 5px;
  border-radius: 5px;
  height: 90px;
}
.BLACK {
  background-color: #444;
}
.VIOLET {
  background-color: violet;
}
.BLUE {
  background-color: blue;
}
.GREEN {
  background-color: green;
}
.YELLOW {
  background-color: yellow;
}
.ORANGE {
  background-color: orange;
}
.RED {
  background-color: red;
}
.WHITE {
  background-color: white;
}
.LOCOMOTIVE {
  background-color: aqua;
}
</style>
