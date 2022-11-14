package ch.coachdave.tchutchu

object TestRandomizer:
  val SEED: Int = 2021
  val RANDOM_ITERATIONS = 1_000
  scala.util.Random.setSeed(SEED)