package pwcg.product.bos.plane;

import pwcg.campaign.plane.IPlaneAttributeMapping;

public enum BosPlaneAttributeMapping implements IPlaneAttributeMapping
{
    BF109_E7("bf109e7", "static_bf109e7"),
    BF109_F2("bf109f2", "static_bf109","static_bf109_open", "static_bf109_net"),
    BF109_F4("bf109f4", "static_bf109","static_bf109_open", "static_bf109_net"),
    BF109_G2("bf109g2", "static_bf109","static_bf109_open", "static_bf109_net"),
    BF109_G4("bf109g4", "static_bf109","static_bf109_open", "static_bf109_net"),
    BF109_G6("bf109g6", "static_bf109","static_bf109_open", "static_bf109_net"),
    BF109_G14("bf109g14", "static_bf109","static_bf109_open", "static_bf109_net"),
    BF109_K4("bf109k4", "static_bf109k4", "static_bf109_net"),
    BF110_E2("bf110e2", "static_bf110e2", "static_bf110e2_open"),
    BF110_G2("bf110g2", "static_bf110e2", "static_bf110e2_open"),
    FW190_A3("fw190a3", "static_fw190a8"),
    FW190_A5("fw190a5", "static_fw190a8"),
    FW190_A8("fw190a8", "static_fw190a8"),
    FW190_D9("fw190d9", "static_bf109_net"),
    ME262_A("me262a", "static_me262a"),
        
    Ma202_SER8("mc202s8", "static_bf109_net"),

    HE111_H6("he111h6", "static_he111", "static_he111_open"),
    HE111_H16("he111h16", "static_he111", "static_he111_open"),
    HS129_B2S("hs129b2", "static_hs129b2"),
    JU88_A4("ju88a4", "static_ju88", "static_ju88_open", "static_ju87_net"),
    JU87_D3("ju87d3", "static_ju87", "static_ju87_net", "static_ju87_open"),
    JU52("ju523mg4e", "static_ju52"),

    I16_T24("i16t24", "static_i16", "static_i16_net"),
    MIG3_S24("mig3s24", "static_mig3_net"),
    LAGG3_S29("lagg3s29", "static_lagg3", "static_lagg3_net"),
    LA5_S8("la5s8", "static_lagg3", "static_lagg3_net"),
    LA5N_S2("la5fns2", "static_lagg3", "static_lagg3_net"),
    
    YAK1_S69("yak1s69", "static_yak1_open", "static_yak1", "static_yak1_net"),
    YAK1_S127("yak1s127", "static_yak1_open", "static_yak1", "static_yak1_net"),
    YAK7B_S36("yak7bs36", "static_yak1_open", "static_yak1", "static_yak1_net"),
    P38_J25("p38j25", "static_p38j25"),
    P39_L1("p39l1", "static_p39l1"),
    P40_E1("p40e1", "static_p40e1"),
    P47_D28("p47d28", "static_p47d28"),
    P51_D15("p51d15", "static_p51d15"),
    SPITFIRE_MKVB("spitfiremkvb", "static_spitfiremkixe"),
    SPITFIRE_MKIXE("spitfiremkixe", "static_spitfiremkixe"),
    TEMPEST_MKVS2("tempestmkvs2", "static_tempestmkvs2"),

    U2_VS("u2vs", "static_u2vs"),
    IL2_M41("il2m41", "static_il2", "static_il2_net"),
    IL2_M42("il2m42", "static_il2", "static_il2_net"),
    IL2_M43("il2m43", "static_il2", "static_il2_net"),
    PE2_S35("pe2s35", "static_pe2", "static_pe2_open"),
    PE2_S87("pe2s87", "static_pe2", "static_pe2_open"),
    A20B("a20b", "static_a20b"),
    B25("b25draf", "static_a20b");

    
	private String planeType;
	private String[] staticPlaneMatches;
	 
	BosPlaneAttributeMapping(String planeType, String ... staticPlaneMatches)
	{
        this.planeType = planeType;
		this.staticPlaneMatches = staticPlaneMatches;		
	}

	@Override
	public String getPlaneType() 
	{
		return planeType;
	}

	@Override
	public String[] getStaticPlaneMatches() 
	{
		return staticPlaneMatches;
	}
}
