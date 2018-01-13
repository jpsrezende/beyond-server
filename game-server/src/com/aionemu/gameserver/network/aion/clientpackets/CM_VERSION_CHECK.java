package com.aionemu.gameserver.network.aion.clientpackets;

import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.AionConnection.State;
import com.aionemu.gameserver.network.aion.serverpackets.SM_VERSION_CHECK;
import com.aionemu.gameserver.services.EventService;

/**
 * @author -Nemesiss-
 */
public class CM_VERSION_CHECK extends AionClientPacket {

	/**
	 * Aion Client version
	 */
	private int version;
	@SuppressWarnings("unused")
	private int subversion;
	@SuppressWarnings("unused")
	private int windowsEncoding;
	@SuppressWarnings("unused")
	private int windowsVersion;
	@SuppressWarnings("unused")
	private int windowsSubVersion;

	/**
	 * Constructs new instance of <tt>CM_VERSION_CHECK </tt> packet
	 * 
	 * @param opcode
	 */
	public CM_VERSION_CHECK(int opcode, State state, State... restStates) {
		super(opcode, state, restStates);
	}

	@Override
	protected void readImpl() {
		version = readUH();
		subversion = readUH();
		windowsEncoding = readD();
		windowsVersion = readD();
		windowsSubVersion = readD();
		readC();// always 2?
	}

	@Override
	protected void runImpl() {
		sendPacket(new SM_VERSION_CHECK(version, EventService.getInstance().getEventTheme()));
	}
}
