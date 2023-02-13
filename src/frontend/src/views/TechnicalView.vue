<template>
  <div class="main">
    <div class="technical">
      <section id="introduction">
        <h1>Tech Talk: A Deep Dive into the Game's Structure</h1>
        <p>
          Welcome to the technical overview of TchuTchu!
          This section is intended for <strong>developers</strong> who are interested in learning about the technical details of this game.
          Here, we will delve into the architectural structure of the game and highlight the key components of the code.
          Whether you are a developer looking to learn more about this game's inner workings or a recruiter evaluating the author's technical skills,
          it is hoped that you will find this overview informative and insightful. So sit back, relax, and enjoy a journey through the technical aspects of this game!
          The code is publicly available on <a class="link" href="https://github.com/dsalathe/tchutchu">GitHub</a>.
        </p>
        <section id="introduction--overview">
          <h3>Tech Trail: Overview of the Trip</h3>
          <p>
            In this technical overview, I will first provide an overview of the game and its technical constraints,
            as these factors played a key role in shaping the architecture of the game.
            Next, I will present the chosen architecture, explaining the reasoning behind the design decisions and how they helped to meet the game's requirements.
            Finally, we will delve into the main parts of the code, highlighting the key features and functionality implemented in each component.
            By the end of this technical overview,
            you should have a clear understanding of the game's technical design and how it all comes together to create a seamless and enjoyable gaming experience.
          </p>
        </section>
        <section id="introduction--aboutme">
          <h3>About the Author and the Purpose of This Side Project</h3>
          <p>Hi, my name is David and I am a passionate and extremely curious software engineer.
            I love coding, designing solutions, and board games.
            One cool thing with software engineering is that we can often merge coding and almost any passion together: let's digitalize one of my favorite board games!
          </p>
          <p>
            As a side project, I wanted to <em>web-appify</em> "Ticket to Ride: Switzerland" to continue learning and improving my technical skills,
            particularly those that I don't have the opportunity to use in my day-to-day job.
            I saw this project as an opportunity to exercise my skills in software architecture and to learn more about frontend technologies.
            I believe that this project would be of interest to other developers as it tackles complex problems
            and uses a variety of technologies. Overall, I saw it as a great way to grow my skills and to have fun while doing it.

          </p>
        </section>
        <section id="introduction--game">
          <h3>Game Description</h3>
          <p>
            In Ticket to Ride: Switzerland, players are tasked with building the most efficient and successful railway network across the country.
            Players collect and play train cards to claim railway routes on a map of Switzerland, connecting cities and earning points in the process.
            The game also includes destination tickets, which are cards that challenge players to connect specific cities using a certain number of train cards.
            The player with the most points at the end of the game is the winner. In addition to building their own railway network,
            players can also try to block their opponents by claiming routes that they need to complete their destination tickets.
            The game is won by the player who can best balance the need to complete their own destination tickets with the need to block their opponents
          </p>
        </section>
        <section id="introduction--challenges">
          <h3>Main Challenges</h3>
          <p>
            With this short game description in mind and with the web-based constraint, we can highlight the following challenges we have to address:
            <ul>
              <li>
                <strong>Splitting client-server concerns:</strong> One of the main challenges in creating a web-based version of Ticket to Ride: Switzerland
                is separating the client-server concerns. The client-side of the application will be responsible for displaying the game board,
                rendering the game state, and capturing user input. The server-side of the application will be responsible for managing the game state,
                enforcing the game rules, and facilitating communication between players. It is important to carefully design the client-server interface to ensure
                that the game remains responsive and secure.
              </li>
              <li>
                <strong>Modeling the game logic:</strong> We have to think about how we want to structure the game to keep the code as maintainable as possible.
                While having maintainable code is an obvious objective, I personally think it is especially important in the context of games because it is very likely that we will want to add extensions and new features later.
                We will use design patterns to help us with that. Michel Schinz, an EPFL instructor, originally modeled the game in Java.
                If you are interested in this original work, you can find it <a class="link" href="https://cs108.epfl.ch/archive/21/archive.html">here.</a>
                Note that the course is in French.
              </li>
              <li>
                <strong>Game state:</strong> We have to ensure consistency and legitimity of any actions when updating the state of the game given a player's action.
                In a client-server architecture, this means that the server should always double checks the validity of the user's action, otherwise
                a user could cheat by modifying the client code.
              </li>
              <li>
                <strong>Networking:</strong> While this is a turn-based game, we may increase the maximum number of players to 4 or even more,
                which leads us to consider using a better protocol than HTTPS. It is possible to communicate with a browser and server using other protocols.
                One suitable protocol for a web-app game like this one could be websockets.
                This protocol allows the server to send messages directly to clients rather than just responding to client requests. It is a bidirectional channel.
              </li>
              <li>
                <strong>Game Sessions:</strong> If we want players to play against the same opponents until the end of the game
                and if we want the possibility to have multiple games running at the same time, we need to set up a system that can scale horizontally.
                Its responsibility is to ensure that each player plays against the same players. As a bonus, it would be nice to have some resilience against crashes,
                meaning that if a player gets disconnected, they should be able to reconnect to their game.
              </li>
              <li>
                <strong>User interface: </strong> Ticket to Ride is a complex game for beginners. We should help users as best as possible by providing clear
                instructions and guidance throughout the game. This will involve designing intuitive and user-friendly interfaces, as well as implementing helpful
                features such as tutorials and in-game hints.
              </li>
            </ul>
          </p>
        </section>
        <section id="introduction--optionalchallenges">
          <h3>Optional Features</h3>
          <p>
            Solving all these main challenges is already a very interesting technical journey,
            but there are additional features that could enhance the game even further. Some of these have already been implemented,
            while others may be added in the future.
            <ul>
              <li>
                <strong>Implementing multiplayer modes:</strong> While the original game is primarily a turn-based game,
                it may be possible to implement additional multiplayer modes such as real-time or asynchronous play.
                One feature that has already been implemented in this direction is the inclusion of a chat feature.
              </li>
              <li>
                <strong>Supporting different languages:</strong> To make the game more accessible to a wider audience, it may be useful to support different languages.
                This would involve translating the game interface and text, as well as designing a system for selecting the preferred language.
              </li>
              <li>
                <strong>Creating custom game maps:</strong> To add an extra level of customization,
                it may be possible to allow players to create and share their own custom game maps.
                This would involve designing a map editor and implementing a system for uploading and sharing custom maps.
                It would also involve ensuring that the custom maps are balanced and meet the game's requirements.
                Another possibility is to randomly generate a map by cropping actual areas (using, for example,
                the Google Maps API) and using the isomorphic properties of a graph to generate a balanced map.
              </li>
              <li>
                <strong>Leveraging the power of PWA:</strong> Progressive Web Apps (PWAs) allow users to natively install the web app on their phone or even on Windows.
                This could be especially useful with a one-player offline mode.
              </li>
            </ul>
          </p>
        </section>
      </section>
      <section id="architecture">
        <h1>Architecture</h1>
        <p>
          When designing a new application, the first and most important decisions to make are about the system architecture.
        It is important to consider the following meta-constraints in order to make informed decisions:
        </p>
        <ol>
        <li><strong>The skills and size of the team:</strong>
        Knowing the skills and size of the team is crucial in making design decisions.
        The team's skills will impact the choice of technology stack and programming language.
        The size of the team and its social structure, as described by Conway's law, will also impact the overall architecture
        and help determine whether to use a monolithic, event-driven, or microservice architecture.
        </li>
        <li><strong>Characteristics trade-offs:</strong> When designing a system, the first consideration is often how to maximize performance.
        However, sometimes it's necessary to trade-off performance for other factors, such as shorter time-to-market, improved maintainability,
        reduced complexity, or improved testability.
        There are several architecture patterns that are more performant than a monolithic approach, but they often come with a cost of complexity and longer
        time-to-market periods. This trade-off must be considered carefully, taking into account the specific needs of the project. For example, an educational project's
        main goal is often to demonstrate clear concepts with a limited amount of code, allowing curious individuals to experiment with the project in a reasonable amount of time. In this case,
        it might not be wise to spend too much time on designing the most performant system. The author personally spent around 180 hours designing, writing, and testing the project.
        </li>
        <li><strong>Longevity of the app:</strong> Some businesses rewrite their entire app each year, while others need to maintain their app for 20 years or longer.
        If you plan to keep and maintain your app through multiple generations of developers, you may want to consider architecture patterns that have proven to be easier to test and refactor.
        </li>
        </ol>
        <section id="architecture--bigpicture">
          <h3>The Big Picture</h3>
          <p>
            Based on the meta-constraints mentioned earlier, a decision was made to use a <strong>monolithic but event-driven approach</strong> for the project.
            The team is composed of one person, and the primary purpose of the project is demonstration. Performance is not the most important consideration.
            The concrete design of the system is as follows:
          </p>
          <p class="quote">Users will connect to the server through WebSockets.
            Each request is handled by a controller, which dispatches requests to smaller units to handle different possible actions from the user
            (such as playing their turn, writing a message, reconnecting to a game session, etc.).
            By using caches, the game state of a user is retrieved, and combined with the user's action, the game engine is used to compute the next game state.
            Finally, the server broadcasts the new state to each player and saves it in its cache.
          </p>
        </section>
        <section id="architecture--techstack">
          <h3>TchuTchu's tech stack</h3>
        </section>
        <section id="architecture--devops">
          <h3>Devops & Cloud</h3>
        </section>
        <section id="architecture--communication">
          <h3>Communication</h3>
        </section>
        <section id="architecture--gameengine">
          <h3>Game Engine</h3>
        </section>
      </section>
      <p class="tbc">To Be Continued...</p>
    </div>

    <nav class="section-nav">
      <ol>
        <li>
          <a href="#introduction">Introduction</a>
          <ul>
            <li><a href="#introduction--overview">Overview</a></li>
            <li><a href="#introduction--aboutme">About the Author</a></li>
            <li><a href="#introduction--game">Game Description</a></li>
            <li><a href="#introduction--challenges">Main Challenges</a></li>
            <li><a href="#introduction--optionalchallenges">Optional Features</a></li>
          </ul>
        </li>
        <li>
          <a href="#architecture">Architecture</a>
          <ul>
            <li><a href="#architecture--bigpicture">The big picture</a></li>
            <li><a href="#architecture--techstack">The tech stack</a></li>
            <li><a href="#architecture--devops">Devops & Cloud</a></li>
            <li><a href="#architecture--communication">Communication</a></li>
            <li><a href="#architecture--gameengine">Game Engine</a></li>
          </ul>
        </li>
      </ol>
    </nav>
</div>
</template>

<script>
export default {
  mounted () {
    const observer = new IntersectionObserver(entries => {
      entries.forEach(entry => {
        const id = entry.target.getAttribute('id')
        if (entry.intersectionRatio > 0) {
          document.querySelector(`nav li a[href="#${id}"]`).parentElement.classList.add('active')
        } else {
          document.querySelector(`nav li a[href="#${id}"]`).parentElement.classList.remove('active')
        }
      })
    })

    // Track all sections that have an `id` applied
    document.querySelectorAll('section[id]').forEach((section) => {
      observer.observe(section)
    })
  }
}
</script>

<style scoped>

html {
  scroll-behavior: smooth;
}

nav {
  position: sticky;
  top: 2rem;
  align-self: start;
  text-align: left;
}

.section-nav li.active > a {
  color: #333;
  font-weight: bolder;
}

/* Sidebar Navigation */
.section-nav {
  padding-left: 0;
  border-left: 1px solid #efefef;
}

.section-nav a {
  display: block;
  padding: .125rem 0;
  color: #ccc;
  font-weight: lighter;
  text-decoration: none;
  transition: all 50ms ease-in-out; /* 💡 This small transition makes setting of the active state smooth */
}

.section-nav a:hover,
.section-nav a:focus {
  color: #666;
}

/** Poor man's reset **/
* {
  box-sizing: border-box;
}

.section-nav ul, .section-nav  ol {
  list-style: none;
  margin: 0;
  padding: 0;
}
li {
  margin-left: 1rem;
}

/** page layout **/
.main {
  display: grid;
  grid-template-columns: 1fr 15em;
  max-width: 100em;
  width: 90%;
  margin: 0 auto;
}

.technical {
  margin-right: 5%;
}

h1 {
  text-align: center;
  color: #AA291C;
}
h3 {
  text-align: center;
  color: #DA291C;
}
h4 {
  text-align: center;
  color: #FA392C;
}

p {
  text-align: justify;
  margin: 10px 40px;
}

.quote {
  margin: 10px 80px;
  text-align: center;
  font-size: larger;
  font-style: italic;
}

.tbc {
  margin: 100px;
  text-align: center;
  font-size: larger;
  font-style: italic;
}

.link {
  position: relative;
  text-decoration: none;
  font-weight: bolder;
}

.link::before {
    content: '';
    position: absolute;
    width: 100%;
    height: 2px;
    border-radius: 2px;
    background-color: #DA291C;
    bottom: 0;
    left: 0;
    transform-origin: right;
    transform: scaleX(0);
    transition: transform .3s ease-in-out;
  }

.link:hover::before {
  transform-origin: left;
  transform: scaleX(1);
}

.link:visited {
  color: black;
  text-decoration: none;
}
</style>
