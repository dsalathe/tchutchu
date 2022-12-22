<template>
  <div class="technical">
    <h1>Tech Talk: A Deep Dive into the Game's Structure</h1>
    <p>
      Welcome to the technical overview of TchuTchu!
      This section is intended for <strong>developers</strong> and <strong>recruiters</strong> who are interested in learning about the technical details of this game.
      Here, we will delve into the architectural structure of the game and highlight the key components of the code.
      Whether you are a developer looking to learn more about this game's inner workings or a recruiter evaluating the author's technical skills,
      hopes are that you will find this overview informative and insightful. So sit back, relax, and enjoy a journey through the technical aspects of this game!
      The code is publicly available on <a href="https://github.com/dsalathe/tchutchu">GitHub</a>.
    </p>
    <h3>Tech Trail: Overview of the trip</h3>
    <p>
      In this technical overview, I will first provide an overview of the game and its technical constraints,
      as these factors played a key role in shaping the architecture of the game.
      Next, I will present the chosen architecture, explaining the reasoning behind the design decisions and how they helped to meet the game's requirements.
      Finally, we will delve into the main parts of the code, highlighting the key features and functionality implemented in each component.
      By the end of this technical overview,
      you should have a clear understanding of the game's technical design and how it all comes together to create a seamless and enjoyable gaming experience.
    </p>
    <p class="tbc">To Be Continued...</p>
  </div>
</template>

<script>

</script>

<style scoped>

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

.tbc {
  margin: 100px;
  text-align: center;
  font-size: larger;
  font-style: italic;
}

a {
  position: relative;
  text-decoration: none;
  font-weight: bolder;
}

a::before {
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

a:hover::before {
  transform-origin: left;
  transform: scaleX(1);
}

a:visited {
  color: black;
  text-decoration: none;
}
</style>
