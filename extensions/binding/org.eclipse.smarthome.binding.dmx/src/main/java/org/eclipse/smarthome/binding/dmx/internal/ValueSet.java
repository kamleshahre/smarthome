/**
 * Copyright (c) 2014,2017 Contributors to the Eclipse Foundation
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.smarthome.binding.dmx.internal;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.smarthome.core.library.types.PercentType;

/**
 * The {@link ValueSet} holds a set of values and fade times
 *
 * @author Jan N. Klug - Initial contribution
 */

public class ValueSet {
    protected static final Pattern VALUESET_PATTERN = Pattern.compile("^(\\d*):([\\d,]*):([\\d-]*)$");

    private int fadeTime;
    private int holdTime;

    private List<Integer> values = new ArrayList<Integer>();

    /**
     * constructor with fade times only
     *
     * @param fadeTime fade time in ms
     * @param holdTime hold time in ms, -1 is forever
     */
    public ValueSet(int fadeTime, int holdTime) {
        this.fadeTime = fadeTime;
        this.holdTime = holdTime;
    }

    /**
     * constructor with fade times and value
     *
     * @param fadeTime fade time in ms
     * @param holdTime hold time in ms, -1 is forever
     * @param value DMX value (0-255)
     */
    public ValueSet(int fadeTime, int holdTime, int value) {
        this.fadeTime = fadeTime;
        this.holdTime = holdTime;
        addValue(value);
    }

    /**
     * get fade time
     *
     * @return fade time in ms
     */
    public int getFadeTime() {
        return fadeTime;
    }

    /**
     * get hold time
     *
     * @return hold time in ms (-1 = forever)
     */
    public int getHoldTime() {
        return holdTime;
    }

    /**
     * add a value to this list
     *
     * @param value value (0-255)
     */
    public void addValue(int value) {
        values.add(Util.toDmxValue(value));
    }

    /**
     * add a value to this list
     *
     * @param value value (0-100%)
     */
    public void addValue(PercentType value) {
        values.add(Util.toDmxValue(value));
    }

    /**
     * get a value from this value set
     *
     * @param index index of value, if larger than list, re-use from start
     * @return value in the range of 0-255
     */
    public int getValue(int index) {
        return values.get(index % values.size());
    }

    /**
     * parse this value set from a string
     *
     * @param valueSetConfig a string holding a complete value set configuration fadeTime:value,value2,...:holdTime
     * @return a new ValueSet
     */
    public static ValueSet fromString(String valueSetConfig) {
        Matcher valueSetMatch = VALUESET_PATTERN.matcher(valueSetConfig);
        if (valueSetMatch.matches()) {
            ValueSet step = new ValueSet(Integer.valueOf(valueSetMatch.group(1)),
                    Integer.valueOf(valueSetMatch.group(3)));
            for (String value : valueSetMatch.group(2).split(",")) {
                step.addValue(Integer.valueOf(value));
            }
            return step;
        } else {
            return null;
        }
    }

    @Override
    public String toString() {
        String str = "fade/hold:" + String.valueOf(fadeTime) + "/" + String.valueOf(holdTime) + ": ";
        for (Integer value : values) {
            str += String.valueOf(value) + " ";
        }
        return str;
    }
}
