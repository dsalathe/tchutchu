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
              <li>
                <strong>Designing an AI</strong>: Enable one-player mode by designing a powerful bot! Reinforcement learning-based ai, heuristically-based on
                spanning trees for connecting cities and max-flow min-cut theorem for blocking efficiently the opponent... Your pick!
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
          <p>
            <img src="img/technical-overview/tchutchu-modelling.drawio.png" />
          </p>
          <p><i>
            This diagram provides a simplified view of Tchutchu's architecture.
            The system is composed of modules, which can be represented as classes or groups of classes.
            Containers are deployable units that run on separate physical machines,
            while caches are utilized to retrieve necessary contexts based on a player's action.
            Additionally, a concurrent queue is employed for the matchmaking process.
          </i></p>
        </section>
        <section id="architecture--techstack">
          <h3>TchuTchu's tech stack</h3>
          <p>
            Tchutchu's game engine is written in <strong>Scala 3.</strong>
            Scala is a very powerful language that brings the best of the oriented object and functional programming worlds together.
            If you're already familiar with scala but not its third version, you should have no problem to understand the source code.
            The main difference is mainly that scala 3 no longer requires bracket syntax like java but can be written in indentation syntax like python.
          </p>
          <p>
            To web-appify TchuTchu, we also need modern frameworks. Three of them were considered:
          </p>
          <ul>
            <li><em>Play:</em> An advanced scala framework to build full stack apps easily. No current support for scala 3 in 2022.</li>
            <li><em>Akka HTTP:</em> A more low-level scala framework for web-based applications. More work to do, but more customizable.</li>
            <li><em>Spring Boot:</em> A multiple purpose state-of-the-art java framework supporting tons of options.</li>
          </ul>
          <p>
            Because Play was not supported with scala 3 (at least yet) and because Akka HTTP seemed too low-level for this demonstration,
            we chose to go with <strong>Spring Boot.</strong>
          </p>
          <p>
            A solid choice for the frontend is <strong>Vue 3</strong>, which was particularly well-suited for this project due to its progressive characteristics.
            Other candidates for the frontend included React, Angular, and ScalaJS.
            While the author was interested in Angular's structure, it was deemed too difficult to use for a total beginner.
            ScalaJS was not considered solely because of its beloved Scala syntax, but also because it appeared less complex and well-suited for this project.
            However, Vue 3 was ultimately chosen due to its popularity at the time and its potential to engage the project's target audience.
          </p>
        </section>
        <section id="architecture--devops">
          <h3>DevOps and Cloud</h3>
          <p>
            If you want to share your new project with friends and family, it can be difficult to ask them to download the source code from a GitHub repository,
            install a JVM, and compile the code just to play on the same local network. A simpler solution is to host your web app on the cloud. <br/>
            In Tchutchu, we consider three main challenges in terms of DevOps:
            <ul>
              <li>
                <em>Setting up Scala 3 with Spring Boot:</em> Setting up Scala 2 or older versions with Play or Spring Boot is relatively straightforward.
                With Scala 3, it's a bit more complicated. Fortunately, <a class="link" href="https://github.com/jecklgamis/spring-boot-scala-example">jecklgamis</a>
                has created a GitHub template to help you get started with a project using Spring Boot and Scala 3 together.
              </li>
              <li>
                <em>Setting up a Vue 3 frontend environment:</em> Using Vue's CLI or <a class="link" href="https://vitejs.dev/">Vite</a>, you can set up a frontend environment.
                Then, you need to adapt your POM to install a plugin that compiles Vue's production code into its JAR. Finally, you might want to set up a proxy to run your JAR backend and create a fast feedback loop to improve your frontend development experience. A tutorial with concrete examples will be provided soon.
              </li>
              <li>
                <em>Deploying your app on the cloud:</em> We are one step away from showing our work to our loved ones. At this point, we can produce a compact and easily deployable JAR. We will containerize our app using Docker, upload the container to the <strong>Google Cloud Platform (GCP)</strong>, set up a VM via Cloud Run, and deploy the app. From the GCP console, we can easily monitor the app's traffic and server logs.
              </li>
            </ul>
          </p>
          <p>
            Optionally, you can set up continuous deployment on GCP. You can also use GitHub Actions to automate multiple tasks.
            For example, in our <a class="link" href="https://github.com/dsalathe/tchutchu/actions">repo</a>, we automated the running of unit tests.
          </p>
        </section>
        <section id="architecture--communication">
          <h3>Communication</h3>
          <p>
            While Ticket to Ride is a turn-based game, it's still important to design an efficient communication system to support instant messaging.
            We want to control the scope of chats and game notifications to only include the relevant players, and we also want to ensure that the system is horizontally scalable.
          </p>
          <p>
            Specifically, we want players to be able to chat with other players before starting a game session, but once the game starts, the scope of the chat should be limited to the players in the same game.
            Because we also want the server to be able to broadcast new game states to the relevant players in real-time, a simple REST-based HTTP protocol is not suitable. Instead, we'll use <strong>WebSockets</strong>, a bi-directional communication protocol that supports queues and topics, similar to popular broker systems like Kafka or ActiveMQ.
          </p>
          <p>
            One of the main concrete challenges of setting up websockets is designing the format of the messages exchanged between the server and the clients.
            It is important to define the types of messages that will be used, as well as their structure and content.
            Additionally, implementing the controllers that will handle the messages on both the backend and frontend sides is a crucial step.
            In the backend, a Scala controller using Spring Boot annotations can be used to handle websocket messages and broadcast new game states,
            while in the frontend, a controller written in Vue can be used to manage the received messages and update the state of the game accordingly.
          </p>
        </section>
        <section id="architecture--gameengine">
          <h3>Game Engine</h3>
          <p>
            It is worth noting that the author, despite being a fan of Scala, actually wrote a Java version of the game first.
            The initial version of the project, called Tchu, was written in Java with a JavaFX frontend. Later, the game engine was re-written in Scala 3,
            and finally, the frontend was re-written in Vue 3. But before doing an honest comparison of Java versus Scala, let's dive into the game engine's structure.
            The source code of the game engine can be found <a class="link" href="https://github.com/dsalathe/tchutchu/tree/main/src/main/scala/ch/coachdave/tchutchu/game">here</a>.
            To design a game engine, two well-known approaches exist: a top-down or a bottom-up strategy.
            The top-down strategy involves having a clear idea of the final product and determining what is needed to execute any kind of actions.
            In contrast, the bottom-up strategy involves having a clear vision of what elements constitute a game and building more sophisticated elements or operations
            based on the basic elements.
            The latter approach may yield unnecessary elements but might lead to a stronger and more consistent data model.
          </p>
          <p>
            The original project from Mr. Schinz uses a bottom-up approach for his students: it allows them to build more concrete and understandable classes and
            complexify the code at later stages. First, we design basic elements such as enums for card colors or routes,
            followed by classes handling different states such as card, player, or game states. Finally, we build the actual game engine upon all these implementations.
          </p>
          <p>
            When designing more complex interactions, it is important to base our thoughts on well-known design patterns to strive for an easily maintainable application.
            A non-exhaustive list of used patterns includes Singleton, Builder, Strategy, and State.
            Additionally, we should carefully use efficient algorithms to solve different problems.
            For instance, in Ticket to Ride, players receive points at the end of the game for each ticket successfully connected.
            This means they win points if there exists a set of routes connecting point A to point B.
            Here is a question for you: how would you determine efficiently the validity of each players ticket,
            given their list of tickets as well as their list of owned routes?
            Details of the algorithm used in our project will be discussed in the algorithm section.
          </p>
        </section>
      </section>
      <section id="implementation">
        <h1>Implementation</h1>
        <p class="tbc">Coming soon!</p>
        <section id="implementation--scala">
          <h3>Why Scala ?</h3>
          <p class="tbc">Coming soon!</p>
        </section>
        <section id="implementation--gameengine">
          <h3>Game Engine's implementation</h3>
          <p class="tbc">Coming soon!</p>
        </section>
        <section id="implementation--algorithm">
          <h3>Notable algorithms</h3>
          <h4>Union-Find</h4>
          <p>
            If you know your way around design patterns, that's great.
            But to truly master the art of programming, you should also have a strong understanding of algorithms.
            Here's an exercise for you: given the current state of the game shown in the image below,
            how can you efficiently determine whether Churchill has validated the first three tickets but not the last two?
          </p>
          <p>
            <img src="img/technical-overview/roadStateTchutchu.png" />
          </p>
          <p>
            That is, given that Churchill has the following 8 routes:
            <br/>
            <i>Bâle - Olten, Olten - Soleure, Soleure - Berne, Berne - Fribourg, Berne - Lucerne, Lucerne -Zoug, Zoug - Zürich, Schaffouse - Kreuzlingen</i>
            <br/>
            and given that Churchill has the following 5 tickets:
            <br/>
            <i>Bâle - Berne, Fribourg - Lucerne, Lucerne - Zürich, Olten - Schaffouse, Schaffouse - Saint-Gall</i>
            <br/>
          </p>
          <p>
            One possible approach is to take each ticket, start with the first city of the ticket,
            and run a Breadth-First Search (BFS) given the set of roads the player has.
            However, this approach tests the connectivity between two cities pairwise independently of the other runs of BFS, but it's not independent at all.
            This approach would take O(T * (C + R)) time complexity, where O() denotes the "Big O" notation for asymptotic analysis,
            T denotes the number of tickets, C denotes the number of cities, and R denotes the number of roads.
          </p>
          <p>
            Fortunately, there is a more efficient approach: the Union-Find data structure.
            The Union-Find data structure is able to group elements into clusters, with each cluster having a representative.
            In this data structure, querying if two elements are in the same cluster is very fast, taking amortized constant time.
            To implement this approach, you can check out the theory on
            the <a class="link" href="https://en.wikipedia.org/wiki/Disjoint-set_data_structure">Wikipedia page</a> or review the
            source code <a class="link" href="https://github.com/dsalathe/tchutchu/blob/main/src/main/scala/ch/coachdave/tchutchu/UnionFind.scala">here</a>.
          </p>
          <p>
            Using the Union-Find data structure, the time complexity of building the data structure is O(C + R),
            where C is the number of cities and R is the number of routes.
            Querying if a ticket is validated, in other words, querying if two cities are connected, takes O(1).
            Therefore, querying for each ticket takes O(T), where T is the number of tickets.
            Since these two phases of the algorithm are one after the other, the total time complexity is O(T + (C+R)), which is linear,
            whereas the previous approach would have been somewhat quadratic.
          </p>
          <h4>Longest trail</h4>
          <p>
            What about computing the longest trail? Well a naive approach would be to try every possible sequence of routes possessed by the player.
            It would yield terrible time complexity but it would work. Given R routes, the number of possible sequence is O(R!):
            it's the number of permutations of unique elements.
          </p>
          <p>
            Can we do better? Well... No. Except if you prove that P = NP, this problem is considered as NP-hard, because
            we can reduce the Hamiltonian path problem to this one, meaning that if we can find a polynomial solution to the longest trail problem,
            we can use this solution to solve the Hamiltonian path problem.
            The Hamiltonian path problem consists of checking if there exists a Hamiltonian path, which is a path that visits each vertex exactly once.
          </p>
          <p>
              So why discuss a very inefficient algorithm?
              Because it is important to understand where scalability issues may arise.
              This means that if we want to scale our game to include many more cities, we may need to either add caches and use dynamic programming,
              or accept using approximations. Additionally, implementing this brute-force solution is far from obvious.
              You can find our attempt at the following
              link: <a href="https://github.com/dsalathe/tchutchu/blob/main/src/main/scala/ch/coachdave/tchutchu/game/Trail.scala" class="link">Trail.scala</a>
          </p>
        </section>
        <section id="implementation--matchmaking">
          <h3>Matchmaking process</h3>
          <p class="tbc">Coming soon!</p>
        </section>
        <section id="implementation--vue">
          <h3>Why Vue ?</h3>
          <p class="tbc">Coming soon!</p>
        </section>

      </section>
      <section id="discussion">
        <h1>Discussion</h1>
        <p class="tbc">Coming soon! Expect to see discussions about signing states with asymetric encriptions, replay attacks and even blockchain...</p>
        <!--p>Todo speak about cache's limitations -> either sign new state, but then speak about replay attacks, then speak about blockchain</p-->
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
        <li>
          <a href="#implementation">Implementation</a>
          <ul>
            <li><a href="#implementation--scala">Scala</a></li>
            <li><a href="#implementation--gameengine">Game Engine</a></li>
            <li><a href="#implementation--algorithm">Algorithms</a></li>
            <li><a href="#implementation--matchmaking">Matchmaking</a></li>
            <li><a href="#implementation--vue">Vue</a></li>
          </ul>
        </li>
        <li>
          <a href="#discussion">Discussion</a>
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

img {
  display: block;
  margin-left: auto;
  margin-right: auto;
  width: 95%;
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
