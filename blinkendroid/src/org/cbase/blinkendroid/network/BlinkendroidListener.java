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

package org.cbase.blinkendroid.network;

/**
 * @author Andreas Schildbach
 */
public interface BlinkendroidListener {

    void connectionOpened();

    void connectionClosed();
    
    void connectionFailed(String message);

    void serverTime(long serverTime);

    void play(int resId, long startTime);

    void clip(float startX, float startY, float endX, float endY);

    void arrow(long duration, float angle);
}
