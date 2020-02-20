package pwcg.core.constants;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import pwcg.campaign.context.Country;

public enum Callsign {
	NONE(0),

	STORK(1, Country.GERMANY, Country.RUSSIA),
	GANNET(2, Country.GERMANY, Country.RUSSIA),
	RAVEN(3, Country.GERMANY, Country.RUSSIA),
	ROOK(4, Country.GERMANY, Country.RUSSIA),
	THRUSH(5, Country.GERMANY, Country.RUSSIA),
	CRANE(6, Country.GERMANY, Country.RUSSIA),
	FINCH(7, Country.GERMANY, Country.RUSSIA),
	ORIOLE(8, Country.GERMANY, Country.RUSSIA),
	CANARY(9, Country.GERMANY, Country.RUSSIA),
	SWAN(10, Country.GERMANY, Country.RUSSIA),
	KITTIWAKE(11, Country.GERMANY, Country.RUSSIA),
	EAGLE(12, Country.GERMANY, Country.RUSSIA),
	PELICAN(13, Country.GERMANY, Country.RUSSIA),
	SWIFT(14, Country.GERMANY, Country.RUSSIA),
	REDSHANK(15, Country.GERMANY, Country.RUSSIA),
	DUCK(16, Country.GERMANY, Country.RUSSIA),
	PHEASANT(17, Country.GERMANY, Country.RUSSIA),
	SEAGULL(18, Country.GERMANY, Country.RUSSIA),
	BRAMBLING(19, Country.GERMANY, Country.RUSSIA),
	HAWK(20, Country.GERMANY, Country.RUSSIA),

	ACACIA(21, Country.GERMANY, Country.RUSSIA),
	BEECH(22, Country.GERMANY, Country.RUSSIA),
	ELM(23, Country.GERMANY, Country.RUSSIA),
	OAK(24, Country.GERMANY, Country.RUSSIA),
	HORNBEAM(25, Country.GERMANY, Country.RUSSIA),
	SPRUCE(26, Country.GERMANY, Country.RUSSIA),
	JASMINE(27, Country.GERMANY, Country.RUSSIA),
	WILLOW(28, Country.GERMANY, Country.RUSSIA),
	MAPLE(29, Country.GERMANY, Country.RUSSIA),
	LINDEN(30, Country.GERMANY, Country.RUSSIA),
	MAGNOLIA(31, Country.GERMANY, Country.RUSSIA),
	ALDER(32, Country.GERMANY, Country.RUSSIA),
	FIR(33, Country.GERMANY, Country.RUSSIA),
	ROWAN(34, Country.GERMANY, Country.RUSSIA),
	PINE(35, Country.GERMANY, Country.RUSSIA),
	THUJA(36, Country.GERMANY, Country.RUSSIA),
	PISTACHIO(37, Country.GERMANY, Country.RUSSIA),
	MULBERRY(38, Country.GERMANY, Country.RUSSIA),
	EUCALYPTUS(39, Country.GERMANY, Country.RUSSIA),
	ASH(40, Country.GERMANY, Country.RUSSIA),

	STORM(41, Country.GERMANY, Country.RUSSIA),
	TYPHOON(42, Country.GERMANY, Country.RUSSIA),
	HURRICANE(43, Country.GERMANY, Country.RUSSIA),
	CYCLONE(44, Country.GERMANY, Country.RUSSIA),
	VOLCANO(45, Country.GERMANY, Country.RUSSIA),

    ACORN(46, Country.BRITAIN),
    CHARLIE(47, Country.BRITAIN),
    BEARSKIN(48, Country.BRITAIN),
    LUTON(49, Country.BRITAIN),
    ALERT(50, Country.BRITAIN),
    MITOR(51, Country.BRITAIN),
    ANGEL(52, Country.BRITAIN),
    RABBIT(53, Country.BRITAIN),
    BAFFIN(54, Country.BRITAIN),
    FILMSTAR(55, Country.BRITAIN),
    TURKEY(56, Country.BRITAIN),
    TENNIS(57, Country.BRITAIN),
    VICEROY(58, Country.BRITAIN),
    PANSY(59, Country.BRITAIN),
    HYDRO(60, Country.BRITAIN),
    SUNCUP(61, Country.BRITAIN),
    TALLYHO(62, Country.BRITAIN),
    WAGON(63, Country.BRITAIN),
    DOGROSE(64, Country.BRITAIN),
    GANNIC(65, Country.BRITAIN),

    ROUGHMAN(66, Country.USA),
    BLUE_BIRD(67, Country.USA),
    PLASTIC(68, Country.USA),
    DITTO(69, Country.USA),
    BULLRING(70, Country.USA),
    WOODBINE(71, Country.USA),
    WARCRAFT(72, Country.USA),
    RIPPER(73, Country.USA),
    COBWEB(74, Country.USA),
    ROCKET(75, Country.USA),
    ANGUS(76, Country.USA),
    CLEVELAND(77, Country.USA),
    GRANITE(78, Country.USA),
    SLAPJACK(79, Country.USA),
    PINTAIL(80, Country.USA),
    TURQOISE(81, Country.USA),
    BISON(82, Country.USA),
    NEPTUNE(83, Country.USA),
    CHIEFTAIN(84, Country.USA),
    ELWOOD(85, Country.USA),

    WATERBURY(86, Country.BRITAIN),
    SOUTHBURY(87, Country.BRITAIN),
    ALDERWOOD(88, Country.BRITAIN),
    LOCKERLY(89, Country.BRITAIN),
    NEWBURY(90, Country.BRITAIN),
    DURRINGTON(91, Country.BRITAIN),
    ASHBURY(92, Country.BRITAIN),
    MANCHESTER(93, Country.BRITAIN),
    MORNINGSTAR(94, Country.BRITAIN),
    HOLYBOURNE(95, Country.BRITAIN),
    WALSINGHAM(96, Country.BRITAIN),
    OTTERHOUND(97, Country.BRITAIN),
    WESTMORELAND(98, Country.BRITAIN),
    COVENTRY(99, Country.BRITAIN),
    HARRINGTON(100, Country.BRITAIN),
    CUMBERLAND(101, Country.BRITAIN),
    WELLINGTON(102, Country.BRITAIN),
    BELLINGHAM(103, Country.BRITAIN),
    DUNSTERVILLE(104, Country.BRITAIN),
    SULLINGTON(105, Country.BRITAIN),

    MAYFLOWER(106, Country.USA),
    HIGHTOWER(107, Country.USA),
    ALDERWOOD_(108, Country.USA),
    HALLOWAY(109, Country.USA),
    RAMONA(110, Country.USA),
    MADELINE(111, Country.USA),
    ROSEMARY(112, Country.USA),
    JUNIPER(113, Country.USA),
    DELAWARE(114, Country.USA),
    BURLINGTON(115, Country.USA),
    OVERSTREET(116, Country.USA),
    CLARABELLE(117, Country.USA),
    MARIGOLD(118, Country.USA),
    GOLDENROD(119, Country.USA),
    EDISON(120, Country.USA),
    FARMINGTON(121, Country.USA),
    BEAVERDAM(122, Country.USA),
    ARLINGTON(123, Country.USA),
    EVERGREEN(124, Country.USA),
    SUNFLOWER(125, Country.USA),

    LONGBOW(126, Country.BRITAIN),
    CROSSBOW(127, Country.BRITAIN),
    BROADSWORD(128, Country.BRITAIN),
    JAVELIN(129, Country.BRITAIN),
    KENWAY(130, Country.BRITAIN),

    SWEEPSTAKES(131, Country.USA),
    MARMITE(132, Country.USA),
    MUDGUARD(133, Country.USA),
    GROUNDHOG(134, Country.USA),
    SPOTLIGHT(135, Country.USA),

    BISON_(136, Country.GERMANY, Country.RUSSIA),
    ELK(137, Country.GERMANY, Country.RUSSIA),
    LEOPARD(138, Country.GERMANY, Country.RUSSIA),
    PUMA(139, Country.GERMANY, Country.RUSSIA),
    FERRET(140, Country.GERMANY, Country.RUSSIA),
    WOLF(141, Country.GERMANY, Country.RUSSIA),
    FOX(142, Country.GERMANY, Country.RUSSIA),
    BADGER(143, Country.GERMANY, Country.RUSSIA),
    MONGOOSE(144, Country.GERMANY, Country.RUSSIA),
    CHEETAH(145, Country.GERMANY, Country.RUSSIA),
    RACOON(146, Country.GERMANY, Country.RUSSIA),
    ERMINE(147, Country.GERMANY, Country.RUSSIA),
    LION(148, Country.GERMANY, Country.RUSSIA),
    BOAR(149, Country.GERMANY, Country.RUSSIA),
    BEAVER(150, Country.GERMANY, Country.RUSSIA),
    COYOTE(151, Country.GERMANY, Country.RUSSIA),
    RHINO(152, Country.GERMANY, Country.RUSSIA),
    GROUNDHOG_(153, Country.GERMANY, Country.RUSSIA),
    JAGUAR(154, Country.GERMANY, Country.RUSSIA),
    BEHEMOTH(155, Country.GERMANY, Country.RUSSIA);

	private Integer callsignNum;
	private Set<Country> countries;

	private Callsign(final Integer num, final Country... c)
	{
		callsignNum = num;
		countries = new HashSet<>(Arrays.asList(c));
	}

	public Integer getNum()
	{
		return callsignNum;
	}
	
	public Set<Country> getCountries()
	{
	    return countries;
	}

	public String toString()
	{
		return this.name().substring(0,1) + this.name().substring(1).toLowerCase().replace("_", " ").trim();
	}
}
