package com.aionemu.gameserver.services;

import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.curingzone.CuringObject;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.curingzones.CuringTemplate;
import com.aionemu.gameserver.skillengine.SkillEngine;
import com.aionemu.gameserver.utils.MathUtil;
import com.aionemu.gameserver.utils.ThreadPoolManager;

import javolution.util.FastTable;

/**
 * @author xTz
 */
public class CuringZoneService {

	Logger log = LoggerFactory.getLogger(CuringZoneService.class);
	private FastTable<CuringObject> curingObjects = new FastTable<>();

	private CuringZoneService() {
		for (CuringTemplate t : DataManager.CURING_OBJECTS_DATA.getCuringObject()) {
			CuringObject obj = new CuringObject(t, 0);
			obj.spawn();
			curingObjects.add(obj);
		}
		log.info("spawned Curing Zones");
		startTask();
	}

	private void startTask() {

		ThreadPoolManager.getInstance().scheduleAtFixedRate(new Runnable() {

			@Override
			public void run() {
				for (final CuringObject obj : curingObjects) {
					obj.getKnownList().forEachPlayer(new Consumer<Player>() {

						@Override
						public void accept(Player player) {
							if (MathUtil.isIn3dRange(obj, player, obj.getRange()) && !player.getEffectController().hasAbnormalEffect(8751)) {
								SkillEngine.getInstance().getSkill(player, 8751, 1, player).useNoAnimationSkill();
							}
						}

					});
				}
			}

		}, 1000, 1000);

	}

	private static class SingletonHolder {

		protected static final CuringZoneService instance = new CuringZoneService();
	}

	public static final CuringZoneService getInstance() {
		return SingletonHolder.instance;
	}

}
