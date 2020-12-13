package ai.instance.esoterrace;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

import com.aionemu.gameserver.ai.AIName;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.skillengine.SkillEngine;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;

import ai.AggressiveNpcAI;

/**
 * @author xTz
 */
@AIName("wardensurama")
public class WardenSuramaAI extends AggressiveNpcAI {

	private List<Integer> percents = new ArrayList<>();

	public WardenSuramaAI(Npc owner) {
		super(owner);
	}

	@Override
	protected void handleSpawned() {
		super.handleSpawned();
		addPercent();
	}

	@Override
	protected void handleAttack(Creature creature) {
		super.handleAttack(creature);
		checkPercentage(getLifeStats().getHpPercentage());
	}

	private void checkPercentage(int hpPercentage) {

		for (Integer percent : percents) {
			if (hpPercentage <= percent) {
				percents.remove(percent);
				switch (percent) {
					case 50:
					case 25:
					case 5:
						spawnGeysers();
						break;
				}

				break;
			}
		}
	}

	private void addPercent() {
		percents.clear();
		Collections.addAll(percents, new Integer[] { 50, 25, 5 });
	}

	private void spawnGeysers() {
		SkillEngine.getInstance().getSkill(getOwner(), 19332, 50, getOwner()).useNoAnimationSkill();
		spawn(282425, 1305.310059f, 1159.337769f, 53.203529f, (byte) 0, 721);
		spawn(282173, 1316.953979f, 1196.861328f, 53.203529f, (byte) 0, 598);
		spawn(282428, 1305.083130f, 1182.424927f, 53.203529f, (byte) 0, 719);
		spawn(282427, 1328.613770f, 1182.369873f, 53.203529f, (byte) 0, 722);
		spawn(282172, 1343.426147f, 1170.675293f, 53.203529f, (byte) 0, 596);
		spawn(282171, 1317.097656f, 1145.419556f, 53.203529f, (byte) 0, 595);
		spawn(282426, 1328.446289f, 1159.062500f, 53.203529f, (byte) 0, 718);
		spawn(282174, 1290.778442f, 1170.730957f, 53.203529f, (byte) 0, 597);

		getKnownList().forEachPlayer(new Consumer<Player>() {

			@Override
			public void accept(Player player) {
				if (player.isOnline()) {
					PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_IDF4Re_Drana_10());
					PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_IDF4Re_Drana_09());
				}
			}
		});
		doSchedule();
	}

	private void deSpawnGeysers() {
		despawnNpc(282425);
		despawnNpc(282173);
		despawnNpc(282428);
		despawnNpc(282427);
		despawnNpc(282172);
		despawnNpc(282171);
		despawnNpc(282426);
		despawnNpc(282174);
	}

	private void despawnNpc(int npcId) {
		Npc npc = getPosition().getWorldMapInstance().getNpc(npcId);
		if (npc != null) {
			npc.getController().delete();
		}
	}

	private void doSchedule() {
		ThreadPoolManager.getInstance().schedule(new Runnable() {

			@Override
			public void run() {
				deSpawnGeysers();
			}

		}, 13000);
	}

	@Override
	protected void handleBackHome() {
		super.handleBackHome();
		addPercent();
	}

}