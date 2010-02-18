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
package org.jcvi.cli;

import org.apache.commons.cli.Option;
import org.jcvi.Builder;

public class CommandLineOptionBuilder implements Builder<Option>{

        private String longName;
        private String shortName;
        private boolean isRequired;
        private String description;
        private boolean isFlag;
        private String displayName;
        public CommandLineOptionBuilder(String shortName, String description){
            this(shortName, shortName,description);
        }
        public CommandLineOptionBuilder(String shortName, String displayName, String description){
            this.shortName = shortName;
            this.description = description;
            this.displayName = displayName;
        }
        
        public CommandLineOptionBuilder isFlag(boolean isFlag){
            this.isFlag = isFlag;
            return this;
        }
        public CommandLineOptionBuilder isRequired(boolean isRequired){
            this.isRequired = isRequired;
            return this;
        }
        
        public CommandLineOptionBuilder longName(String longName){
            this.longName = longName;
            return this;
        }

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
