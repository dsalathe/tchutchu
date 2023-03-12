package ch.coachdave.tchutchu.game

import ch.coachdave.tchutchu.game.Route.Level._
import ch.coachdave.tchutchu.game.Color._

object ChMap:

  val BAD: Station = Station(0, "Baden")
  val BAL: Station = Station(1, "Bâle")
  val BEL: Station = Station(2, "Bellinzone")
  val BER: Station = Station(3, "Berne")
  val BRI: Station = Station(4, "Brigue")
  val BRU: Station = Station(5, "Brusio")
  val COI: Station = Station(6, "Coire")
  val DAV: Station = Station(7, "Davos")
  val DEL: Station = Station(8, "Delémont")
  val FRI: Station = Station(9, "Fribourg")
  val GEN: Station = Station(10, "Genève")
  val INT: Station = Station(11, "Interlaken")
  val KRE: Station = Station(12, "Kreuzlingen")
  val LAU: Station = Station(13, "Lausanne")
  val LCF: Station = Station(14, "La Chaux-de-Fonds")
  val LOC: Station = Station(15, "Locarno")
  val LUC: Station = Station(16, "Lucerne")
  val LUG: Station = Station(17, "Lugano")
  val MAR: Station = Station(18, "Martigny")
  val NEU: Station = Station(19, "Neuchâtel")
  val OLT: Station = Station(20, "Olten")
  val PFA: Station = Station(21, "Pfäffikon")
  val SAR: Station = Station(22, "Sargans")
  val SCE: Station = Station(23, "Schaffhouse")
  val SCZ: Station = Station(24, "Schwyz")
  val SIO: Station = Station(25, "Sion")
  val SOL: Station = Station(26, "Soleure")
  val STG: Station = Station(27, "Saint-Gall")
  val VAD: Station = Station(28, "Vaduz")
  val WAS: Station = Station(29, "Wassen")
  val WIN: Station = Station(30, "Winterthour")
  val YVE: Station = Station(31, "Yverdon")
  val ZOU: Station = Station(32, "Zoug")
  val ZUR: Station = Station(33, "Zürich")

  // Stations - countries
  val DE1: Station = Station(34, "Allemagne")
  val DE2: Station = Station(35, "Allemagne")
  val DE3: Station = Station(36, "Allemagne")
  val DE4: Station = Station(37, "Allemagne")
  val DE5: Station = Station(38, "Allemagne")
  val AT1: Station = Station(39, "Autriche")
  val AT2: Station = Station(40, "Autriche")
  val AT3: Station = Station(41, "Autriche")
  val IT1: Station = Station(42, "Italie")
  val IT2: Station = Station(43, "Italie")
  val IT3: Station = Station(44, "Italie")
  val IT4: Station = Station(45, "Italie")
  val IT5: Station = Station(46, "Italie")
  val FR1: Station = Station(47, "France")
  val FR2: Station = Station(48, "France")
  val FR3: Station = Station(49, "France")
  val FR4: Station = Station(50, "France")

  val DE: List[Station] = List(DE1, DE2, DE3, DE4, DE5)
  val AT: List[Station] = List(AT1, AT2, AT3)
  val IT: List[Station] = List(IT1, IT2, IT3, IT4, IT5)
  val FR: List[Station] = List(FR1, FR2, FR3, FR4)

  def ticketToNeighbors(from: List[Station], de: Int, at: Int, it: Int, fr: Int): Ticket =
    def notZero(points: Int, to: List[Station]): List[Trip] =
      if points == 0 then Nil else Trip.all(from, to, points)

    Ticket(notZero(de, DE) ++ notZero(at, AT) ++ notZero(it, IT) ++ notZero(fr, FR))

  def deToNeighbors: Ticket = ticketToNeighbors(DE, 0, 5, 13, 5)
  def atToNeighbors: Ticket = ticketToNeighbors(AT, 5, 0, 6, 14)
  def itToNeighbors: Ticket = ticketToNeighbors(IT, 13, 6, 0, 11)
  def frToNeighbors: Ticket = ticketToNeighbors(FR, 5, 14, 11, 0)


  val AT1_STG_1: Route = Route("AT1_STG_1", AT1, STG, 4, UNDERGROUND, None)
  val AT2_VAD_1: Route = Route("AT2_VAD_1", AT2, VAD, 1, UNDERGROUND, Some(RED))
  val BAD_BAL_1: Route = Route("BAD_BAL_1", BAD, BAL, 3, UNDERGROUND, Some(RED))
  val BAD_OLT_1: Route = Route("BAD_OLT_1", BAD, OLT, 2, OVERGROUND, Some(VIOLET))
  val BAD_ZUR_1: Route = Route("BAD_ZUR_1", BAD, ZUR, 1, OVERGROUND, Some(YELLOW))
  val BAL_DE1_1: Route = Route("BAL_DE1_1", BAL, DE1, 1, UNDERGROUND, Some(BLUE))
  val BAL_DEL_1: Route = Route("BAL_DEL_1", BAL, DEL, 2, UNDERGROUND, Some(YELLOW))
  val BAL_OLT_1: Route = Route("BAL_OLT_1", BAL, OLT, 2, UNDERGROUND, Some(ORANGE))
  val BEL_LOC_1: Route = Route("BEL_LOC_1", BEL, LOC, 1, UNDERGROUND, Some(BLACK))
  val BEL_LUG_1: Route = Route("BEL_LUG_1", BEL, LUG, 1, UNDERGROUND, Some(RED))
  val BEL_LUG_2: Route = Route("BEL_LUG_2", BEL, LUG, 1, UNDERGROUND, Some(YELLOW))
  val BEL_WAS_1: Route = Route("BEL_WAS_1", BEL, WAS, 4, UNDERGROUND, None)
  val BEL_WAS_2: Route = Route("BEL_WAS_2", BEL, WAS, 4, UNDERGROUND, None)
  val BER_FRI_1: Route = Route("BER_FRI_1", BER, FRI, 1, OVERGROUND, Some(ORANGE))
  val BER_FRI_2: Route = Route("BER_FRI_2", BER, FRI, 1, OVERGROUND, Some(YELLOW))
  val BER_INT_1: Route = Route("BER_INT_1", BER, INT, 3, OVERGROUND, Some(BLUE))
  val BER_LUC_1: Route = Route("BER_LUC_1", BER, LUC, 4, OVERGROUND, None)
  val BER_LUC_2: Route = Route("BER_LUC_2", BER, LUC, 4, OVERGROUND, None)
  val BER_NEU_1: Route = Route("BER_NEU_1", BER, NEU, 2, OVERGROUND, Some(RED))
  val BER_SOL_1: Route = Route("BER_SOL_1", BER, SOL, 2, OVERGROUND, Some(BLACK))
  val BRI_INT_1: Route = Route("BRI_INT_1", BRI, INT, 2, UNDERGROUND, Some(WHITE))
  val BRI_IT5_1: Route = Route("BRI_IT5_1", BRI, IT5, 3, UNDERGROUND, Some(GREEN))
  val BRI_LOC_1: Route = Route("BRI_LOC_1", BRI, LOC, 6, UNDERGROUND, None)
  val BRI_SIO_1: Route = Route("BRI_SIO_1", BRI, SIO, 3, UNDERGROUND, Some(BLACK))
  val BRI_WAS_1: Route = Route("BRI_WAS_1", BRI, WAS, 4, UNDERGROUND, Some(RED))
  val BRU_COI_1: Route = Route("BRU_COI_1", BRU, COI, 5, UNDERGROUND, None)
  val BRU_DAV_1: Route = Route("BRU_DAV_1", BRU, DAV, 4, UNDERGROUND, Some(BLUE))
  val BRU_IT2_1: Route = Route("BRU_IT2_1", BRU, IT2, 2, UNDERGROUND, Some(GREEN))
  val COI_DAV_1: Route = Route("COI_DAV_1", COI, DAV, 2, UNDERGROUND, Some(VIOLET))
  val COI_SAR_1: Route = Route("COI_SAR_1", COI, SAR, 1, UNDERGROUND, Some(WHITE))
  val COI_WAS_1: Route = Route("COI_WAS_1", COI, WAS, 5, UNDERGROUND, None)
  val DAV_AT3_1: Route = Route("DAV_AT3_1", DAV, AT3, 3, UNDERGROUND, None)
  val DAV_IT1_1: Route = Route("DAV_IT1_1", DAV, IT1, 3, UNDERGROUND, None)
  val DAV_SAR_1: Route = Route("DAV_SAR_1", DAV, SAR, 3, UNDERGROUND, Some(BLACK))
  val DE2_SCE_1: Route = Route("DE2_SCE_1", DE2, SCE, 1, OVERGROUND, Some(YELLOW))
  val DE3_KRE_1: Route = Route("DE3_KRE_1", DE3, KRE, 1, OVERGROUND, Some(ORANGE))
  val DE4_KRE_1: Route = Route("DE4_KRE_1", DE4, KRE, 1, OVERGROUND, Some(WHITE))
  val DE5_STG_1: Route = Route("DE5_STG_1", DE5, STG, 2, OVERGROUND, None)
  val DEL_FR4_1: Route = Route("DEL_FR4_1", DEL, FR4, 2, UNDERGROUND, Some(BLACK))
  val DEL_LCF_1: Route = Route("DEL_LCF_1", DEL, LCF, 3, UNDERGROUND, Some(WHITE))
  val DEL_SOL_1: Route = Route("DEL_SOL_1", DEL, SOL, 1, UNDERGROUND, Some(VIOLET))
  val FR1_MAR_1: Route = Route("FR1_MAR_1", FR1, MAR, 2, UNDERGROUND, None)
  val FR2_GEN_1: Route = Route("FR2_GEN_1", FR2, GEN, 1, OVERGROUND, Some(YELLOW))
  val FR3_LCF_1: Route = Route("FR3_LCF_1", FR3, LCF, 2, UNDERGROUND, Some(GREEN))
  val FRI_LAU_1: Route = Route("FRI_LAU_1", FRI, LAU, 3, OVERGROUND, Some(RED))
  val FRI_LAU_2: Route = Route("FRI_LAU_2", FRI, LAU, 3, OVERGROUND, Some(VIOLET))
  val GEN_LAU_1: Route = Route("GEN_LAU_1", GEN, LAU, 4, OVERGROUND, Some(BLUE))
  val GEN_LAU_2: Route = Route("GEN_LAU_2", GEN, LAU, 4, OVERGROUND, Some(WHITE))
  val GEN_YVE_1: Route = Route("GEN_YVE_1", GEN, YVE, 6, OVERGROUND, None)
  val INT_LUC_1: Route = Route("INT_LUC_1", INT, LUC, 4, OVERGROUND, Some(VIOLET))
  val IT3_LUG_1: Route = Route("IT3_LUG_1", IT3, LUG, 2, UNDERGROUND, Some(WHITE))
  val IT4_LOC_1: Route = Route("IT4_LOC_1", IT4, LOC, 2, UNDERGROUND, Some(ORANGE))
  val KRE_SCE_1: Route = Route("KRE_SCE_1", KRE, SCE, 3, OVERGROUND, Some(VIOLET))
  val KRE_STG_1: Route = Route("KRE_STG_1", KRE, STG, 1, OVERGROUND, Some(GREEN))
  val KRE_WIN_1: Route = Route("KRE_WIN_1", KRE, WIN, 2, OVERGROUND, Some(YELLOW))
  val LAU_MAR_1: Route = Route("LAU_MAR_1", LAU, MAR, 4, UNDERGROUND, Some(ORANGE))
  val LAU_NEU_1: Route = Route("LAU_NEU_1", LAU, NEU, 4, OVERGROUND, None)
  val LCF_NEU_1: Route = Route("LCF_NEU_1", LCF, NEU, 1, UNDERGROUND, Some(ORANGE))
  val LCF_YVE_1: Route = Route("LCF_YVE_1", LCF, YVE, 3, UNDERGROUND, Some(YELLOW))
  val LOC_LUG_1: Route = Route("LOC_LUG_1", LOC, LUG, 1, UNDERGROUND, Some(VIOLET))
  val LUC_OLT_1: Route = Route("LUC_OLT_1", LUC, OLT, 3, OVERGROUND, Some(GREEN))
  val LUC_SCZ_1: Route = Route("LUC_SCZ_1", LUC, SCZ, 1, OVERGROUND, Some(BLUE))
  val LUC_ZOU_1: Route = Route("LUC_ZOU_1", LUC, ZOU, 1, OVERGROUND, Some(ORANGE))
  val LUC_ZOU_2: Route = Route("LUC_ZOU_2", LUC, ZOU, 1, OVERGROUND, Some(YELLOW))
  val MAR_SIO_1: Route = Route("MAR_SIO_1", MAR, SIO, 2, UNDERGROUND, Some(GREEN))
  val NEU_SOL_1: Route = Route("NEU_SOL_1", NEU, SOL, 4, OVERGROUND, Some(GREEN))
  val NEU_YVE_1: Route = Route("NEU_YVE_1", NEU, YVE, 2, OVERGROUND, Some(BLACK))
  val OLT_SOL_1: Route = Route("OLT_SOL_1", OLT, SOL, 1, OVERGROUND, Some(BLUE))
  val OLT_ZUR_1: Route = Route("OLT_ZUR_1", OLT, ZUR, 3, OVERGROUND, Some(WHITE))
  val PFA_SAR_1: Route = Route("PFA_SAR_1", PFA, SAR, 3, UNDERGROUND, Some(YELLOW))
  val PFA_SCZ_1: Route = Route("PFA_SCZ_1", PFA, SCZ, 1, OVERGROUND, Some(VIOLET))
  val PFA_STG_1: Route = Route("PFA_STG_1", PFA, STG, 3, OVERGROUND, Some(ORANGE))
  val PFA_ZUR_1: Route = Route("PFA_ZUR_1", PFA, ZUR, 2, OVERGROUND, Some(BLUE))
  val SAR_VAD_1: Route = Route("SAR_VAD_1", SAR, VAD, 1, UNDERGROUND, Some(ORANGE))
  val SCE_WIN_1: Route = Route("SCE_WIN_1", SCE, WIN, 1, OVERGROUND, Some(BLACK))
  val SCE_WIN_2: Route = Route("SCE_WIN_2", SCE, WIN, 1, OVERGROUND, Some(WHITE))
  val SCE_ZUR_1: Route = Route("SCE_ZUR_1", SCE, ZUR, 3, OVERGROUND, Some(ORANGE))
  val SCZ_WAS_1: Route = Route("SCZ_WAS_1", SCZ, WAS, 2, UNDERGROUND, Some(GREEN))
  val SCZ_WAS_2: Route = Route("SCZ_WAS_2", SCZ, WAS, 2, UNDERGROUND, Some(YELLOW))
  val SCZ_ZOU_1: Route = Route("SCZ_ZOU_1", SCZ, ZOU, 1, OVERGROUND, Some(BLACK))
  val SCZ_ZOU_2: Route = Route("SCZ_ZOU_2", SCZ, ZOU, 1, OVERGROUND, Some(WHITE))
  val STG_VAD_1: Route = Route("STG_VAD_1", STG, VAD, 2, UNDERGROUND, Some(BLUE))
  val STG_WIN_1: Route = Route("STG_WIN_1", STG, WIN, 3, OVERGROUND, Some(RED))
  val STG_ZUR_1: Route = Route("STG_ZUR_1", STG, ZUR, 4, OVERGROUND, Some(BLACK))
  val WIN_ZUR_1: Route = Route("WIN_ZUR_1", WIN, ZUR, 1, OVERGROUND, Some(BLUE))
  val WIN_ZUR_2: Route = Route("WIN_ZUR_2", WIN, ZUR, 1, OVERGROUND, Some(VIOLET))
  val ZOU_ZUR_1: Route = Route("ZOU_ZUR_1", ZOU, ZUR, 1, OVERGROUND, Some(GREEN))
  val ZOU_ZUR_2: Route = Route("ZOU_ZUR_2", ZOU, ZUR, 1, OVERGROUND, Some(RED))

  val routes: List[Route] = List(AT1_STG_1, AT2_VAD_1, BAD_BAL_1, BAD_OLT_1, BAD_ZUR_1, BAL_DE1_1, BAL_DEL_1, BAL_OLT_1, BEL_LOC_1,
    BEL_LUG_1, BEL_LUG_2, BEL_WAS_1, BEL_WAS_2, BER_FRI_1, BER_FRI_2, BER_INT_1, BER_LUC_1, BER_LUC_2, BER_NEU_1, BER_SOL_1,
    BRI_INT_1, BRI_IT5_1, BRI_LOC_1, BRI_SIO_1, BRI_WAS_1, BRU_COI_1, BRU_DAV_1, BRU_IT2_1, COI_DAV_1, COI_SAR_1, COI_WAS_1,
    DAV_AT3_1, DAV_IT1_1, DAV_SAR_1, DE2_SCE_1, DE3_KRE_1, DE4_KRE_1, DE5_STG_1, DEL_FR4_1, DEL_LCF_1, DEL_SOL_1, FR1_MAR_1,
    FR2_GEN_1, FR3_LCF_1, FRI_LAU_1, FRI_LAU_2, GEN_LAU_1, GEN_LAU_2, GEN_YVE_1, INT_LUC_1, IT3_LUG_1, IT4_LOC_1, KRE_SCE_1,
    KRE_STG_1, KRE_WIN_1, LAU_MAR_1, LAU_NEU_1, LCF_NEU_1, LCF_YVE_1, LOC_LUG_1, LUC_OLT_1, LUC_SCZ_1, LUC_ZOU_1, LUC_ZOU_2,
    MAR_SIO_1, NEU_SOL_1, NEU_YVE_1, OLT_SOL_1, OLT_ZUR_1, PFA_SAR_1, PFA_SCZ_1, PFA_STG_1, PFA_ZUR_1, SAR_VAD_1, SCE_WIN_1,
    SCE_WIN_2, SCE_ZUR_1, SCZ_WAS_1, SCZ_WAS_2, SCZ_ZOU_1, SCZ_ZOU_2, STG_VAD_1, STG_WIN_1, STG_ZUR_1, WIN_ZUR_1, WIN_ZUR_2,
    ZOU_ZUR_1, ZOU_ZUR_2)

  val stations: List[Station] = List(BAD, BAL, BEL, BER, BRI, BRU, COI, DAV, DEL, FRI, GEN, INT, KRE, LAU, LCF, LOC, LUC, LUG, MAR,
    NEU, OLT, PFA, SAR, SCE, SCZ, SIO, SOL, STG, VAD, WAS, WIN, YVE, ZOU, ZUR, DE1, DE2, DE3, DE4, DE5, AT1, AT2, AT3,
    IT1, IT2, IT3, IT4, IT5, FR1, FR2, FR3, FR4)

  val tickets: List[Ticket] = List(
    // City-to-city tickets
    Ticket(BAL, BER, 5),
    Ticket(BAL, BRI, 10),
    Ticket(BAL, STG, 8),
    Ticket(BER, COI, 10),
    Ticket(BER, LUG, 12),
    Ticket(BER, SCZ, 5),
    Ticket(BER, ZUR, 6),
    Ticket(FRI, LUC, 5),
    Ticket(GEN, BAL, 13),
    Ticket(GEN, BER, 8), // 10
    Ticket(GEN, SIO, 10),
    Ticket(GEN, ZUR, 14),
    Ticket(INT, WIN, 7),
    Ticket(KRE, ZUR, 3),
    Ticket(LAU, INT, 7),
    Ticket(LAU, LUC, 8),
    Ticket(LAU, STG, 13),
    Ticket(LCF, BER, 3),
    Ticket(LCF, LUC, 7),
    Ticket(LCF, ZUR, 8), // 20
    Ticket(LUC, VAD, 6),
    Ticket(LUC, ZUR, 2),
    Ticket(LUG, COI, 10),
    Ticket(NEU, WIN, 9),
    Ticket(OLT, SCE, 5),
    Ticket(SCE, MAR, 15),
    Ticket(SCE, STG, 4),
    Ticket(SCE, ZOU, 3),
    Ticket(STG, BRU, 9),
    Ticket(WIN, SCZ, 3), // 30
    Ticket(ZUR, BAL, 4),
    Ticket(ZUR, BRU, 11),
    Ticket(ZUR, LUG, 9),
    Ticket(ZUR, VAD, 6),

    // City to country tickets
    ticketToNeighbors(List(BER), 6, 11, 8, 5),
    ticketToNeighbors(List(COI), 6, 3, 5, 12),
    ticketToNeighbors(List(LUG), 12, 13, 2, 14),
    ticketToNeighbors(List(ZUR), 3, 7, 11, 7),

    // Country to country tickets (two of each)
    deToNeighbors, deToNeighbors,
    atToNeighbors, atToNeighbors,
    itToNeighbors, itToNeighbors,
    frToNeighbors, frToNeighbors)
