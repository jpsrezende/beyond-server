package ai.portals;

import static com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE.STR_SKILL_CAN_NOT_USE_GROUPGATE_NO_RIGHT;

import com.aionemu.gameserver.ai2.AI2Actions;
import com.aionemu.gameserver.ai2.AI2Request;
import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.ai2.NpcAI2;
import com.aionemu.gameserver.model.animations.TeleportAnimation;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_QUESTION_WINDOW;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.services.teleport.TeleportService2;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * @author ATracer, nrg
 */
@AIName("groupgate")
public class GroupGateAI2 extends NpcAI2 {

	private final int CANCEL_DIALOG_METERS = 9;

	@Override
	protected void handleDialogStart(Player player) {
		if (!player.getCommonData().isDaeva()) {
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_SKILL_CAN_NOT_USE_GROUPGATE_BEFORE_CHANGE_CLASS());
			return;
		}

		if (!player.equals(getCreator()) && (!player.isInGroup2() || !player.getPlayerGroup2().hasMember(getCreatorId()))) {
			PacketSendUtility.sendPacket(player, STR_SKILL_CAN_NOT_USE_GROUPGATE_NO_RIGHT());
			return;
		}

		AI2Actions.addRequest(this, player, SM_QUESTION_WINDOW.STR_ASK_GROUP_GATE_DO_YOU_ACCEPT_MOVE, getOwner().getObjectId(), CANCEL_DIALOG_METERS,
			new AI2Request() {

				private boolean decisionTaken = false;

				@Override
				public void acceptRequest(Creature requester, Player responder, int requestId) {
					if (!decisionTaken) {
						switch (getNpcId()) {
							// Group Gates
							case 833208:
							case 749017:
								TeleportService2.teleportTo(responder, 110010000, 1444.9f, 1577.2f, 572.9f, (byte) 0, TeleportAnimation.JUMP_IN);
								break;
							case 833207:
							case 749083:
								TeleportService2.teleportTo(responder, 120010000, 1657.5f, 1398.7f, 194.7f, (byte) 0, TeleportAnimation.JUMP_IN);
								break;
							// Binding Group Gates
							case 749131:
							case 749132:
								TeleportService2.moveToBindLocation(responder);
								break;
						}
						decisionTaken = true;
					}
				}

				@Override
				public void denyRequest(Creature requester, Player responder) {
					decisionTaken = true;
				}

			});
	}
}