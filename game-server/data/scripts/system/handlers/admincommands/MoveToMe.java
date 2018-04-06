package admincommands;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.team.TemporaryPlayerTeam;
import com.aionemu.gameserver.services.teleport.TeleportService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.Util;
import com.aionemu.gameserver.utils.chathandlers.AdminCommand;
import com.aionemu.gameserver.world.World;

/**
 * @author Cyrakuse, Estrayl
 */
public class MoveToMe extends AdminCommand {

	public MoveToMe() {
		super("movetome", "Teleports a player (optional his team) to the user.");
		// @formatter:off
		setSyntaxInfo(
			"<name> - Teleports only the player.",
			"<name> <(g)rp|(a)lli> - Teleports either the players group or his alliance including him.");
		// @formatter:on
	}

	@Override
	public void execute(Player admin, String... params) {
		if (params.length < 1) {
			sendInfo(admin);
			return;
		}
		handlePlayerTeleport(admin, params);
	}

	private void handlePlayerTeleport(Player admin, String... params) {
		Player playerToMove = World.getInstance().findPlayer(Util.convertName(params[0]));
		if (playerToMove == null) {
			sendInfo(admin, "The specified player is not online.");
			return;
		}
		if (params.length >= 2) {
			if (!playerToMove.isInTeam()) {
				sendInfo(admin, "The player does not belong to a team.");
				return;
			}
			TemporaryPlayerTeam<?> teamToMove;
			switch (params[2].toLowerCase()) {
				case "g":
				case "grp":
				case "group":
					teamToMove = playerToMove.getPlayerGroup();
					break;
				default:
					teamToMove = playerToMove.getCurrentTeam();
					break;
			}
			teamToMove.getOnlineMembers().stream().forEach(p -> teleportPlayer(p, admin));
		} else {
			teleportPlayer(playerToMove, admin);
		}
	}

	private void teleportPlayer(Player playerToMove, Player admin) {
		TeleportService.teleportTo(playerToMove, admin.getPosition());
		PacketSendUtility.sendMessage(admin, "Teleported player " + playerToMove.getName() + " to your location.");
		PacketSendUtility.sendMessage(playerToMove, "You have been teleported by " + admin.getName() + ".");
	}
}
