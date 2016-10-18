package ai.instance.RukibukiCircusTroupe;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_PLAY_MOVIE;
import com.aionemu.gameserver.utils.PacketSendUtility;

import ai.AggressiveNpcAI2;

/**
 * @author Ritsu
 */
@AIName("solidironchain")
public class SolidIronChainAI2 extends AggressiveNpcAI2 {

	@Override
	public boolean canThink() {
		return false;
	}

	private AtomicBoolean moviePlayed = new AtomicBoolean();

	@Override
	protected void handleDespawned() {
		super.handleDespawned();
		if (moviePlayed.compareAndSet(false, true)) {
			getPosition().getWorldMapInstance().forEachPlayer(new Consumer<Player>() {

				@Override
				public void accept(Player p) {
					PacketSendUtility.sendPacket(p, new SM_PLAY_MOVIE(0, 983));
				}

			});
		}
	}

}
