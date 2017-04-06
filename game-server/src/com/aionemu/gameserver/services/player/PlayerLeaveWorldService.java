package com.aionemu.gameserver.services.player;

import java.sql.Timestamp;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.commons.database.dao.DAOManager;
import com.aionemu.gameserver.configs.main.AutoGroupConfig;
import com.aionemu.gameserver.custom.BattleService;
import com.aionemu.gameserver.dao.HouseObjectCooldownsDAO;
import com.aionemu.gameserver.dao.ItemCooldownsDAO;
import com.aionemu.gameserver.dao.PlayerCooldownsDAO;
import com.aionemu.gameserver.dao.PlayerDAO;
import com.aionemu.gameserver.dao.PlayerEffectsDAO;
import com.aionemu.gameserver.dao.PlayerLifeStatsDAO;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.dataholders.PlayerInitialData.LocationData;
import com.aionemu.gameserver.model.TaskId;
import com.aionemu.gameserver.model.gameobjects.Summon;
import com.aionemu.gameserver.model.gameobjects.player.BindPointPosition;
import com.aionemu.gameserver.model.gameobjects.player.FriendList;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.summons.SummonMode;
import com.aionemu.gameserver.model.summons.UnsummonType;
import com.aionemu.gameserver.model.team.alliance.PlayerAllianceService;
import com.aionemu.gameserver.model.team.group.PlayerGroupService;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.clientpackets.CM_QUIT;
import com.aionemu.gameserver.network.chatserver.ChatServer;
import com.aionemu.gameserver.questEngine.QuestEngine;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.services.AutoGroupService;
import com.aionemu.gameserver.services.BrokerService;
import com.aionemu.gameserver.services.DuelService;
import com.aionemu.gameserver.services.ExchangeService;
import com.aionemu.gameserver.services.KiskService;
import com.aionemu.gameserver.services.LegionService;
import com.aionemu.gameserver.services.PunishmentService;
import com.aionemu.gameserver.services.RepurchaseService;
import com.aionemu.gameserver.services.SerialKillerService;
import com.aionemu.gameserver.services.drop.DropService;
import com.aionemu.gameserver.services.findgroup.FindGroupService;
import com.aionemu.gameserver.services.instance.InstanceService;
import com.aionemu.gameserver.services.summons.SummonsService;
import com.aionemu.gameserver.services.toypet.PetSpawnService;
import com.aionemu.gameserver.taskmanager.tasks.ExpireTimerTask;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import com.aionemu.gameserver.utils.audit.GMService;
import com.aionemu.gameserver.world.World;
import com.aionemu.gameserver.world.WorldPosition;

/**
 * @author ATracer
 * @modified Neon
 */
public class PlayerLeaveWorldService {

	private static final Logger log = LoggerFactory.getLogger(PlayerLeaveWorldService.class);

	/**
	 * This method is called when a player loses client connection, e.g. when killing the process, or due to bad network connectivity.<br>
	 * <br>
	 * <b><font color='red'>NOTICE:</font> This method must only be called from {@link AionConnection#onDisconnect()} and not from anywhere else</b>
	 * 
	 * @see #leaveWorld(Player)
	 */
	public static void leaveWorldDelayed(Player player, int delayInMillis) {
		Future<?> leaveWorldTask = ThreadPoolManager.getInstance().schedule(() -> leaveWorld(player), delayInMillis);
		player.getController().addTask(TaskId.DESPAWN, leaveWorldTask);
	}

	/**
	 * This method saves a player and removes him from the world. It is called when a player leaves the game, which includes just two cases: either
	 * he goes back to char selection screen or is leaving the game (closing client).<br>
	 * <br>
	 * <b><font color='red'>NOTICE:</font> This method is called only from {@link CM_QUIT} and must not be called from anywhere else</b>
	 */
	public static void leaveWorld(Player player) {
		AionConnection con = player.getClientConnection();
		player.setClientConnection(null); // this sets the player semi-offline, PacketSendUtility will not send packets anymore

		WorldPosition pos = player.getPosition();
		if (pos == null || pos.getMapRegion() == null) { // ensure safe logout
			log.warn(player + " had invalid position: " + pos + " so he was reset to bind point");
			BindPointPosition bp = player.getBindPoint();
			if (bp != null)
				pos = World.getInstance().createPosition(bp.getMapId(), bp.getX(), bp.getY(), bp.getZ(), bp.getHeading(), 1);
			else {
				LocationData ld = DataManager.PLAYER_INITIAL_DATA.getSpawnLocation(player.getRace());
				pos = World.getInstance().createPosition(ld.getMapId(), ld.getX(), ld.getY(), ld.getZ(), ld.getHeading(), 1);
			}
			player.setPosition(pos);
		}

		BattleService.getInstance().onPlayerLogout(player);
		FindGroupService.getInstance().removeFindGroup(player.getRace(), 0x00, player.getObjectId());
		FindGroupService.getInstance().removeFindGroup(player.getRace(), 0x04, player.getObjectId());
		player.getResponseRequester().denyAll();
		player.getFriendList().setStatus(FriendList.Status.OFFLINE, player.getCommonData());
		BrokerService.getInstance().removePlayerCache(player);
		ExchangeService.getInstance().cancelExchange(player);
		RepurchaseService.getInstance().removeRepurchaseItems(player);
		if (AutoGroupConfig.AUTO_GROUP_ENABLE)
			AutoGroupService.getInstance().onPlayerLogOut(player);
		SerialKillerService.getInstance().onLeaveMap(player);
		InstanceService.onLogOut(player);
		GMService.getInstance().onPlayerLogout(player);
		KiskService.getInstance().onLogout(player);

		if (player.isLooting())
			DropService.getInstance().closeDropList(player, player.getLootingNpcOid());

		// Update prison timer
		if (player.isInPrison()) {
			long prisonTimer = System.currentTimeMillis() - player.getStartPrison();
			prisonTimer = player.getPrisonTimer() - prisonTimer;
			player.setPrisonTimer(prisonTimer);
			log.debug("Update prison timer to " + prisonTimer / 1000 + " seconds !");
		}
		if (player.getLifeStats().isAlreadyDead()) {
			if (player.isInInstance() || player.getPanesterraTeam() != null)
				PlayerReviveService.instanceRevive(player);
			else
				PlayerReviveService.bindRevive(player);
		} else if (DuelService.getInstance().isDueling(player.getObjectId())) {
			DuelService.getInstance().loseDuel(player);
		}
		// store current effects
		DAOManager.getDAO(PlayerEffectsDAO.class).storePlayerEffects(player);
		DAOManager.getDAO(PlayerCooldownsDAO.class).storePlayerCooldowns(player);
		DAOManager.getDAO(ItemCooldownsDAO.class).storeItemCooldowns(player);
		DAOManager.getDAO(HouseObjectCooldownsDAO.class).storeHouseObjectCooldowns(player);
		DAOManager.getDAO(PlayerLifeStatsDAO.class).updatePlayerLifeStat(player);

		PlayerGroupService.onPlayerLogout(player);
		PlayerAllianceService.onPlayerLogout(player);
		// fix legion warehouse exploits
		LegionService.getInstance().LegionWhUpdate(player);
		player.getEffectController().removeAllEffects(true);
		player.getLifeStats().cancelAllTasks();

		Summon summon = player.getSummon();
		if (summon != null)
			SummonsService.doMode(SummonMode.RELEASE, summon, UnsummonType.LOGOUT);
		PetSpawnService.dismissPet(player);
		if (player.getPostman() != null)
			player.getPostman().getController().delete();

		PunishmentService.stopPrisonTask(player, true);
		PunishmentService.stopGatherableTask(player, true);
		ExpireTimerTask.getInstance().removePlayer(player);
		if (player.getCraftingTask() != null)
			player.getCraftingTask().stop();

		if (player.isLegionMember())
			LegionService.getInstance().onLogout(player);

		QuestEngine.getInstance().onLogOut(new QuestEnv(null, player, 0));
		Timestamp lastOnline = new Timestamp(System.currentTimeMillis());
		player.getController().delete();
		player.getCommonData().setOnline(false);
		player.getCommonData().setLastOnline(lastOnline);

		ChatServer.getInstance().sendPlayerLogout(player);

		PlayerService.storePlayer(player);

		player.getEquipment().setOwner(null);
		player.getInventory().setOwner(null);
		player.getWarehouse().setOwner(null);
		player.getAccount().getAccountWarehouse().setOwner(null);

		DAOManager.getDAO(PlayerDAO.class).storeOldCharacterLevel(player.getObjectId(), player.getLevel());
		DAOManager.getDAO(PlayerDAO.class).storeLastOnlineTime(player.getObjectId(), lastOnline);
		DAOManager.getDAO(PlayerDAO.class).onlinePlayer(player, false); // marks that player was fully saved and may enter world again

		con.setActivePlayer(null);
	}
}
