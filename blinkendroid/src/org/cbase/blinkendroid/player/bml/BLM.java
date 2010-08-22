/*
 * Copyright 2010 the original author or authors.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.cbase.blinkendroid.player.bml;

import java.io.Serializable;
import java.util.List;

public class BLM implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -3371857600502436099L;
    public BLMHeader header;
    public List<Frame> frames;

    public static class Frame implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -561025050799344726L;
	public int duration;
	public byte matrix[][];
    }
}
