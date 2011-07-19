/*******************************************************************************
 * Copyright 2010 J. Craig Venter Institute
 * 
 * 	This file is part of JCVI Java Common
 * 
 *     JCVI Java Common is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 * 
 *     JCVI Java Common is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 * 
 *     You should have received a copy of the GNU General Public License
 *     along with JCVI Java Common.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
/*
 * Created on Oct 15, 2009
 *
 * @author dkatzel
 */
package org.jcvi.command;

import org.apache.commons.cli.Option;
import org.jcvi.common.core.util.Builder;
/**
 * {@code CommandLineOptionBuilder} is an {@link Option}
 * builder which uses a fluent interface to build Apache
 * Commons CLI Option objects.
 * 
 * @author dkatzel
 * @see <a ref="http://commons.apache.org/cli">Apache Commons CLI</a>
 * @see Option
 *
 */
public class CommandLineOptionBuilder implements Builder<Option>{

        private String longName;
        private String shortName;
        private boolean isRequired;
        private String description;
        private boolean isFlag;
        private String displayName;
        
        /**
         * Begin building a new Option.
         * @param shortName the short name for this option to be referenced
         * by {@code -<shortName>}
         * @param description a description of what this option does to be displayed as part
         * of the usage statement.
         */
        public CommandLineOptionBuilder(String shortName, String description){
            this(shortName, shortName,description);
        }
        /**
         * Begin building a new Option.
         * @param shortName the short name for this option to be referenced
         * by {@code -<shortName>}
         * @param displayName the argument display name (used in the usage ?)
         * @param description a description of what this option does to be displayed as part
         * of the usage statement.
         * 
         */
        public CommandLineOptionBuilder(String shortName, String displayName, String description){
            this.shortName = shortName;
            this.description = description;
            this.displayName = displayName;
        }
        /**
         * Sets if this option is a flag.
         * @param isFlag {@code true} if this option is a flag;
         * {@code false} for not a flag.
         * @return this.
         */
        public CommandLineOptionBuilder isFlag(boolean isFlag){
            this.isFlag = isFlag;
            return this;
        }
        /**
         * Sets if this option is a required option.
         * @param isFlag {@code true} if this option is a required;
         * {@code false} if not required.
         * @return this.
         */
        public CommandLineOptionBuilder isRequired(boolean isRequired){
            this.isRequired = isRequired;
            return this;
        }
        /**
         * Sets long option for this name
         * @param longname the long name for this option to be referenced
         * by {@code --<longName>}
         * @return this.
         */
        public CommandLineOptionBuilder longName(String longName){
            this.longName = longName;
            return this;
        }
        /**
         * Use the data collected by this builder to construct a new
         * {@link Option}
         * @return a newly constructed Option with the given settings.
         */
        @Override
        public Option build() {
            Option option = new Option(shortName, description);
            option.setLongOpt(longName);
            option.setRequired(isRequired);
            option.setArgs(isFlag? Option.UNINITIALIZED : 1);
            option.setArgName(displayName);
            return option;
        }

}
