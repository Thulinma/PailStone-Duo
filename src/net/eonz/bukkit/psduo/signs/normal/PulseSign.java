package net.eonz.bukkit.psduo.signs.normal;

/*
 * This code is Copyright (C) 2011 Chris Bode, Some Rights Reserved.
 *
 * Copyright (C) 1999-2002 Technical Pursuit Inc., All Rights Reserved. Patent 
 * Pending, Technical Pursuit Inc.
 *
 * Unless explicitly acquired and licensed from Licensor under the Technical 
 * Pursuit License ("TPL") Version 1.0 or greater, the contents of this file are 
 * subject to the Reciprocal Public License ("RPL") Version 1.1, or subsequent 
 * versions as allowed by the RPL, and You may not copy or use this file in 
 * either source code or executable form, except in compliance with the terms and 
 * conditions of the RPL.
 *
 * You may obtain a copy of both the TPL and the RPL (the "Licenses") from 
 * Technical Pursuit Inc. at http://www.technicalpursuit.com.
 *
 * All software distributed under the Licenses is provided strictly on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESS OR IMPLIED, AND TECHNICAL
 * PURSUIT INC. HEREBY DISCLAIMS ALL SUCH WARRANTIES, INCLUDING WITHOUT 
 * LIMITATION, ANY WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR 
 * PURPOSE, QUIET ENJOYMENT, OR NON-INFRINGEMENT. See the Licenses for specific 
 * language governing rights and limitations under the Licenses. 
 */

import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.event.block.SignChangeEvent;

import net.eonz.bukkit.psduo.signs.PSSign;
import net.eonz.bukkit.psduo.signs.TriggerType;

public class PulseSign extends PSSign {

	private boolean lastState = false;

	private static final int PULSE_TIME = 10;
	
	private boolean ticking = false;
	private int timer;
	
	protected void triggersign(TriggerType type, Object args) {
		InputState is = this.getInput(1, (BlockRedstoneEvent) args);

		if (is == InputState.HIGH && !lastState) {
			if (risingPulse) pulse();
		} else if ((is == InputState.LOW || is == InputState.DISCONNECTED) && lastState) {
			if (fallingPulse) pulse();
		} else {
			return;
		}
	}

	private void pulse() {
		timer = PULSE_TIME;
		if (!ticking) {
			this.startTicking();
			ticking = true;
			this.setOutput(true);
		}
	}
	
	public boolean tick() {
		timer--;
		if (timer <= 0) {
			ticking = false;
			this.setOutput(false);
		}
		return ticking;
	}

	public String getData() {
		// This sign does not use data.
		return "";
	}

	protected void setData(String data) {
		if (data == null) return;
		// This sign does not use data.
	}

	boolean risingPulse = false;
	boolean fallingPulse = false;
	
	protected void declare(boolean reload, SignChangeEvent event) {
		String edgeLine = this.getLines(event)[1].trim();
		
		if (edgeLine.equals("")) {
			edgeLine = "RISING";
		}
		
		if (edgeLine.equalsIgnoreCase("RISING")) {
			risingPulse = true;
		} else if (edgeLine.equalsIgnoreCase("FALLING")) {
			fallingPulse = true;
		} else {
			if (!reload) {
				this.init("There was an error reading the edge you specified.");
				this.init("    Allowed: RISING, FALLING, BOTH");
				event.setCancelled(true);
			}
			return;
		}
		
		if (!reload) {
			this.setLine(1, edgeLine.toUpperCase(), event);
		}
		
		main.sgc.register(this, TriggerType.REDSTONE_CHANGE);
		if (!reload) {
			this.init("Pulse sign accepted.");
		}
	}

}
