package ai.siege;

import com.aionemu.gameserver.ai.AIName;
import com.aionemu.gameserver.model.gameobjects.siege.SiegeNpc;
import com.aionemu.gameserver.model.templates.npc.AbyssNpcType;
import com.aionemu.gameserver.services.SiegeService;
import com.aionemu.gameserver.services.siege.Siege;

/**
 * @author ATracer, Source
 */
@AIName("siege_protector")
public class SiegeProtectorNpcAI extends SiegeNpcAI {

	@Override
	public void handleBackHome() {
		super.handleBackHome();
		if (getOwner().getAbyssNpcType() != AbyssNpcType.BOSS)
			return;
		Siege<?> siege = SiegeService.getInstance().getSiege(((SiegeNpc) getOwner()).getSiegeId());
		if (siege != null)
			siege.getSiegeCounter().clearDamageCounters();
	}
}