package de.instinct.eqfleet.net.meta.dto;

import de.instinct.eqfleetshared.gamelogic.model.Rank;
import de.instinct.eqfleetshared.gamelogic.model.UserRank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProfileData {
	
	private String username;
	private Rank rank;
	private UserRank userRank;
	private long currentExp;

}
