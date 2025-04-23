package de.instinct.eqfleetshared.gamelogic.model;

public enum Rank {
	
	RECRUIT("recruit1", "Recruit", 0L),
	RECRUIT2("recruit2", "Recruit II", 50L),
	PRIVATE("private1", "Private", 100L),
	PRIVATE2("private2", "Private II", 200L),
	PRIVATE3("private3", "Private III", 300L),
	PRIVATE4("private4", "Private IV", 400L),
	SPECIALIST1("specialist1", "Specialist", 600L),
	SPECIALIST2("specialist2", "Specialist II", 800L),
	SPECIALIST3("specialist3", "Specialist III", 1000L),
	SPECIALIST4("specialist4", "Specialist IV", 1200L),
	CORPORAL1("corporal1", "Corporal", 1700L),
	CORPORAL2("corporal2", "Corporal II", 2200L),
	CORPORAL3("corporal3", "Corporal III", 2700L),
	CORPORAL4("corporal4", "Corporal III", 3200L),
	CORPORAL5("corporal5", "Senior Corporal", 3700L),
	SEARGENT1("seargent1", "Seargent", 5000L),
	SEARGENT2("seargent2", "Seargent II", 6000L),
	SEARGENT3("seargent3", "Seargent III", 7000L),
	SEARGENT4("seargent4", "Seargent IV", 8000L),
	SEARGENT5("seargent5", "Master Seargent", 9000L),
	OFFICER1("officer1", "Officer", 10000L),
	OFFICER2("officer2", "Officer II", 12000L),
	OFFICER3("officer3", "Officer III", 14000L),
	OFFICER4("officer4", "Officer IV", 16000L),
	OFFICER5("officer5", "Chief Officer", 18000L),
	LIEUTENNANT1("lieutennant1", "Lieutennant", 23000L),
	LIEUTENNANT2("lieutennant2", "Lieutennant II", 27000L),
	LIEUTENNANT3("lieutennant3", "Lieutennant III", 32000L),
	LIEUTENNANT4("lieutennant4", "Lieutennant IV", 37000L),
	LIEUTENNANT5("lieutennant5", "First Lieutennant", 42000L),
	COMMANDER1("commander1", "Commander", 50000L),
	COMMANDER2("commander2", "Commander II", 60000L),
	COMMANDER3("commander3", "Commander III", 70000L),
	COMMANDER4("commander4", "Commander IV", 80000L),
	COMMANDER5("commander5", "Superior Commander", 90000L),
	CAPTAIN1("captain1", "Captain", 100000L),
	CAPTAIN2("captain2", "Captain II", 120000L),
	CAPTAIN3("captain3", "Captain III", 140000L),
	CAPTAIN4("captain4", "Captain IV", 160000L),
	CAPTAIN5("captain5", "Super Captain", 180000L),
	MAJOR1("major1", "Major", 200000L),
	MAJOR2("major2", "Major II", 250000L),
	MAJOR3("major3", "Major III", 300000L),
	MAJOR4("major4", "Major IV", 350000L),
	MAJOR5("major5", "Upper Major", 400000L),
	GENERAL1("general1", "General", 500000L),
	GENERAL2("general2", "General II", 600000L),
	GENERAL3("general3", "General III", 700000L),
	GENERAL4("general4", "General IV", 800000L),
	GENERAL5("general5", "Major General", 900000L),
	GRAND_GENERAL1("grand_general1", "Grand General", 1000000L),
	GRAND_GENERAL2("grand_general2", "Grand General II", 2000000L),
	GRAND_GENERAL3("grand_general3", "Grand General III", 3000000L),
	GRAND_GENERAL4("grand_general4", "Grand General IV", 4000000L),
	DIVINE_GENERAL1("divine_general1", "Divine General", 5000000L),
	DIVINE_GENERAL2("divine_general2", "Divine General II", 6000000L),
	DIVINE_GENERAL3("divine_general3", "Divine General III", 7000000L),
	DIVINE_GENERAL4("divine_general4", "Divine General IV", 8000000L),
	HERO("hero", "Hero", 10000000L),
	LEGEND("legend", "Legend", 20000000L);
	
	private String fileName;
	private String label;
	private long requiredExp;
	
	Rank(String fileName, String label, long requiredExp) {
		this.fileName = fileName;
		this.label = label;
		this.requiredExp = requiredExp;
	}
	
	public static Rank getCurrent(long currentExp) {
		Rank current = RECRUIT;
		for (Rank rank : Rank.values()) {
			if (rank.requiredExp <= currentExp) {
				current = rank;
			} else {
				break;
			}
		}
		return current;
	}
	
	public String getFileName() {
		return fileName;
	}
	
	public String getLabel() {
		return label;
	}
	
	public long getRequiredExp() {
		return requiredExp;
	}
	
	public long getNextRequiredExp() {
		int ordinal = this.ordinal();
		Rank[] values = Rank.values();
		if (ordinal + 1 < values.length) {
			return values[ordinal + 1].requiredExp;
		}
		return -1L;
	}

}
