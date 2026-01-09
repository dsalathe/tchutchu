<template>
  <div class="main-state-info">
    <div v-for="(player, i) in players" :key="i" class="player-section">
      <ul>
        <li class="player-header">
          <span v-if="shouldShowAvatar(i)" class="avatar-wrapper" :class="getAvatarBorderClass(i)">
            <img :src="getAvatarUrl(i)" :alt="player.name" class="player-avatar" @error="handleImageError($event, i)" />
          </span>
          <span v-else :id="'dotp'+ (i+1)" class="dot"></span>
          <strong>{{ limit(player.name, 12) }}</strong>
        </li>
        <li>- {{ player.tickets }} tickets</li>
        <li>- {{ player.cards }} cards</li>
        <li>- {{ player.wagons }} wagons</li>
        <li>- {{ player.points }} points</li>
      </ul>
    </div>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'

export default {
  name: 'StateInfo',
  props: ['player1', 'player2', 'ownId'],
  computed: {
    ...mapGetters('auth', ['isAuthenticated', 'user', 'userPicture']),
    players () {
      return [this.player1, this.player2]
    }
  },
  methods: {
    limit (text, maxChars) {
      if (text.length <= maxChars) {
        return text
      }
      const firstWord = text.split(' ')[0]
      return firstWord.length > maxChars ? firstWord.substring(0, maxChars - 1) + '.' : firstWord
    },
    shouldShowAvatar (playerIndex) {
      // Show avatar if the player has a picture (received from server)
      // or if this is the current authenticated user
      const player = this.players[playerIndex]
      return player.picture || (this.isAuthenticated && this.ownId === playerIndex)
    },
    getAvatarUrl (playerIndex) {
      const player = this.players[playerIndex]
      // First priority: use player's picture received from server
      if (player.picture) {
        return player.picture
      }
      // Second priority: if this is the authenticated user, use their Keycloak picture
      if (this.isAuthenticated && this.ownId === playerIndex && this.userPicture) {
        return this.userPicture
      }
      // Fallback to Dicebear avatar using player name as seed
      const seed = this.user?.id || player?.name || 'default'
      return `https://api.dicebear.com/7.x/avataaars/svg?seed=${encodeURIComponent(seed)}`
    },
    getAvatarBorderClass (playerIndex) {
      return playerIndex === 0 ? 'avatar-border-red' : 'avatar-border-blue'
    },
    handleImageError (event, playerIndex) {
      // Fallback to Dicebear if image fails to load
      const seed = this.user?.id || this.players[playerIndex]?.name || 'default'
      event.target.src = `https://api.dicebear.com/7.x/avataaars/svg?seed=${encodeURIComponent(seed)}`
    }
  }
}

</script>

<style scoped>

li {
  font-size: min(1vw, 20px);
}

ul {
  list-style-type: none;
  margin: 0;
  padding: 0;
}

.main-state-info {
  display: flex;
  justify-content: space-around;
  background-image: linear-gradient(to right, rgba(255, 182, 193, 0.5), white, rgb(173, 216, 230, 0.5));
  padding: 10px;
  height: 100%;
}

.player-header {
  display: flex;
  align-items: center;
  gap: 6px;
}

.avatar-wrapper {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: max(1.5vw, 24px);
  height: max(1.5vw, 24px);
  border-radius: 50%;
  overflow: hidden;
  border: 3px solid;
}

.avatar-border-red {
  border-color: #DA291C;
  box-shadow: 0 0 4px rgba(218, 41, 28, 0.5);
}

.avatar-border-blue {
  border-color: #0066cc;
  box-shadow: 0 0 4px rgba(0, 102, 204, 0.5);
}

.player-avatar {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 50%;
}

.dot {
  height: 0.75vw;
  width: 0.75vw;
  min-width: 12px;
  min-height: 12px;
  border-radius: 50%;
  border: 1px solid black;
  display: inline-block;
}
#dotp1 {
  background-color:lightpink;
}
#dotp2 {
  background-color: lightblue;
}
</style>
