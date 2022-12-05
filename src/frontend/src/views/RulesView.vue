<template>
  <div class="rules">
    <h1>Rules of TchuTchu</h1>
    <p>
      Welcome to TchuTchu! This game is very similar to the game <i>Ticket to ride</i> from <i>Day of wonders</i>. If you are already familiar with this game,
      you might be interested to jump in directly to the chapter <strong><a href="#diff">Main difference with classical <i>Ticket to ride</i></a></strong>.
    </p>
    <h3>Goal</h3>
    <p>
      Players will play one after another and try to connect cities by seizing routes. Each seized route worths points.
      To know which cities a player has to connect, there are tickets that reward the player for some specific connections.
      The hardest the trail, the highest the points. Finally, the player with the longest route has bonus points. The player with the highest score wins.
    </p>
    <h3>Actions</h3>
    <p>At the beginning, you have to select at least 3 tickets among 5.</p>
    <p>During the rest of the game, when it is your turn you will have 3 possible actions:</p>
    <ul>
      <li>Draw 2 cards.</li>
      <li>Draw 3 tickets and keep at least 1.</li>
      <li>Seize a route</li>
    </ul>
    <h4>Cards</h4>
    <p>
      There exists eight colors of cards: <Card color="BLACK" class="smallCard"/><Card color="VIOLET" class="smallCard"/>
      <Card color="BLUE" class="smallCard"/><Card color="GREEN" class="smallCard"/>
      <Card color="YELLOW" class="smallCard"/><Card color="ORANGE" class="smallCard"/>
      <Card color="RED" class="smallCard"/><Card color="WHITE" class="smallCard"/>,
      and one extra special card: <Card color="LOCOMOTIVE" class="smallCard"/>.
      They can be taken by drawing them on the side. 5 cards are up front and one slot is reserved for the deck.
      If a player decides to draw a card directly from the deck, it is not revealed to other players.
    </p>
    <h4>Routes</h4>
    <p>
      Colored wagons can be used to seize routes from the same color. You need one card per segment of the route.
      If the route has a dotted border, it means that this route is a tunnel.
      To seize a tunnel, you might also use <Card color="LOCOMOTIVE" class="smallCard"/>. However, tunnels are risky.
      The game will reveal 3 additional cards and the player has to pay one additional card for each card of the same color and for each <Card color="LOCOMOTIVE" class="smallCard"/>.
      If the player can't or doesn't want to, the operation is canceled: the player gets its cards back and its turn is over.
      Finally, a gray route can be taken with any colors, but they have to be of the same.
    </p>

    <h4>Tickets</h4>
    <p>
      A ticket is a pair of city a player has to connect. If succeeded, the player will be rewarded the points written on the ticket.
      On the contrary, the player might be penalized by the same amount of points. There are three different kind of tickets: city-to-city tickets,
      city-to-country or country-to-country tickets.
    </p>

    <h3>Score computation</h3>
    <h4>Routes</h4>
    Routes are rewarded given their length. The following table gives the exact points:
    <table>
      <tr>
        <td>
          <Card color="BLACK" class="smallCard"/>
        </td>
        <td>
          1
        </td>
      </tr>
      <tr>
        <td>
          <Card color="BLACK" class="smallCard"/><Card color="BLACK" class="smallCard"/>
        </td>
        <td>
          2
        </td>
      </tr>
      <tr>
        <td>
          <Card color="BLACK" class="smallCard"/><Card color="BLACK" class="smallCard"/><Card color="BLACK" class="smallCard"/>
        </td>
        <td>
          4
        </td>
      </tr>
      <tr>
        <td>
          <Card color="BLACK" class="smallCard"/><Card color="BLACK" class="smallCard"/><Card color="BLACK" class="smallCard"/><Card color="BLACK" class="smallCard"/>
        </td>
        <td>
          7
        </td>
      </tr>
      <tr>
        <td>
          <Card color="BLACK" class="smallCard"/><Card color="BLACK" class="smallCard"/><Card color="BLACK" class="smallCard"/><Card color="BLACK" class="smallCard"/><Card color="BLACK" class="smallCard"/>
        </td>
        <td>
          10
        </td>
      </tr>
      <tr>
        <td>
          <Card color="BLACK" class="smallCard"/><Card color="BLACK" class="smallCard"/><Card color="BLACK" class="smallCard"/><Card color="BLACK" class="smallCard"/><Card color="BLACK" class="smallCard"/><Card color="BLACK" class="smallCard"/>
        </td>
        <td>
          15
        </td>
      </tr>
    </table>

    <h4>Tickets</h4>
    <p>
      As explained in the Tickets section, tickets might rewards or penalizes the player. Concerning city-to-country and country-to-country tickets,
      if more than one possibility is fullfiled, the player earns the highest reward amongst them.
      On the contrary, if none of the possibilities is connected, the player only loses the least possible points written on the ticket.
    </p>

    <h4>Longest route</h4>
    <p>
      10 bonus points are attributed to the player with the longest route. It is possible to go through the same city twice or more, but each segment of route
      can only be used once.
    </p>

    <h3>Last turn</h3>
    <p>
      When a player only has 0, 1 or 2 wagons, every other player can only play a last time.
    </p>

    <h3 id="diff">Main difference with classical <i>Ticket to ride</i></h3>
    <ul>
      <li>It is possible to draw face-up <Card color="LOCOMOTIVE" class="smallCard"/> twice.</li>
      <li><Card color="LOCOMOTIVE" class="smallCard"/> can however only be used when seizing a tunnel</li>
      <li>More than 2 <Card color="LOCOMOTIVE" class="smallCard"/> can stay among the 5 face-up cards</li>
      <li>It is allowed to use both routes of double-routes in 2-player mode.</li>
      <li>It is also allowed for a player to seize both of the double-route.</li>
    </ul>

  </div>
</template>

<script>
import Card from '@/components/GameCard.vue'

export default {
  components: { Card }
}

</script>

<style scoped>
h1 {
  color: #AA291C;
}
h3 {
  color: #DA291C;
}
h4 {
  color: #FA392C;
}
.smallCard {
  height: 35px;
  width: 20px;
  rotate: 90deg;
  margin: -7px 10px -14px 10px;
  border: 1px solid black;
}

a {
  text-decoration: none;
}

a:visited {
  color: black;
  text-decoration: none;
}
</style>
