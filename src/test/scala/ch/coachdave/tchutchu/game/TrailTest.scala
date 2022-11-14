package ch.coachdave.tchutchu.game

import Route.Level.*
import Color.*
import ChMap.*
import ch.coachdave.tchutchu.UnitSpec

class TrailTest extends UnitSpec:

  val s1 = Station(1, "Yverdon")
  val s2 = Station(2, "Fribourg")
  val s3 = Station(3, "Neuchâtel")
  val s4 = Station(4, "Berne")
  val s5 = Station(5, "Lucerne")
  val s6 = Station(6, "Soleure")

  val routes = List(Route("A", s3, s1, 2, OVERGROUND, Some(BLACK)), Route("B", s3, s6, 4, OVERGROUND, Some(GREEN)),
    Route("C", s4, s3, 2, OVERGROUND, Some(RED)), Route("D", s4, s6, 2, OVERGROUND, Some(BLACK)), Route("E", s4, s5, 4, OVERGROUND, None),
    Route("F", s4, s2, 1, OVERGROUND, Some(ORANGE)))

  "Trail" should "find longest on given example" in {
    val longest = Trail.longest(routes)
    longest should have length 13
    s2 match
      case s if s == longest.station1.get => longest.station2.get shouldEqual s5
      case s if s == longest.station2.get => longest.station1.get shouldEqual s5
      case _ => fail(s"Unexpected start/end stations with $longest")
  }

  it should "work with empty routes" in {
    val longest = Trail.longest(List())
    longest should have length 0
    longest.station1 shouldEqual None
    longest.station2 shouldEqual None
  }

  it should "work with disconnected routes" in {
    val routes = List(SAR_VAD_1, BER_LUC_1, GEN_YVE_1, IT3_LUG_1)
    val longest = Trail.longest(routes)
    longest should have length 6
  }

  it should "work with a single cycle" in {
    val routes = List(FRI_LAU_1, BER_FRI_1, BER_LUC_1, INT_LUC_1, BRI_INT_1, BRI_SIO_1, MAR_SIO_1, LAU_MAR_1)
    val longest = Trail.longest(routes)
    longest should have length 23
  }

  it should "work with a double cycle" in {
    val routes = List(BER_SOL_1, OLT_SOL_1, LUC_OLT_1, INT_LUC_1, BER_INT_1, BER_LUC_1)
    val longest = Trail.longest(routes)
    longest should have length 17
  }

  it should "work with a triple cycle" in {
    val routes = List(SCE_ZUR_1, SCE_WIN_1, WIN_ZUR_1, STG_ZUR_1, PFA_STG_1, PFA_ZUR_1, ZOU_ZUR_1, LUC_ZOU_1, LUC_OLT_1, OLT_ZUR_1, BAD_ZUR_1)
    val longest = Trail.longest(routes)
    longest should have length 23
  }

  it should "work on known examples" in {
    val routesR = List(FR2_GEN_1, GEN_LAU_1, LAU_MAR_1, MAR_SIO_1, BRI_SIO_1, BRI_IT5_1, LAU_NEU_1, FRI_LAU_1, BER_FRI_1, BER_LUC_1, LUC_ZOU_1, ZOU_ZUR_1, STG_ZUR_1, DE5_STG_1, STG_VAD_1)

    val longestR = Trail.longest(routesR)

    longestR should have length 28
  }

