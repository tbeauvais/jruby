/*
 * SClassNode.java - No description
 * Created on 05. November 2001, 21:46
 * 
 * Copyright (C) 2001 Jan Arne Petersen, Stefan Matthias Aust, Alan Moore, Benoit Cerrina
 * Jan Arne Petersen <japetersen@web.de>
 * Stefan Matthias Aust <sma@3plus4.de>
 * Alan Moore <alan_moore@gmx.net>
 * Benoit Cerrina <b.cerrina@wanadoo.fr>
 * 
 * JRuby - http://jruby.sourceforge.net
 * 
 * This file is part of JRuby
 * 
 * JRuby is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * 
 * JRuby is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with JRuby; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 * 
 */

package org.jruby.nodes;

import org.jruby.*;
import org.jruby.exceptions.*;
import org.jruby.runtime.*;

/**
 *
 * @author  jpetersen
 * @version
 */
public class SClassNode extends Node {
    public SClassNode(Node recvNode, Node bodyNode) {
        super(Constants.NODE_SCLASS, recvNode, bodyNode, null);
    }
    
 	public String toString()   
	{
		return super.toString() + "recv:" + getRecvNode().toString() + ", "  + "body:" + getBodyNode().toString() + ")";
	}
    public RubyObject eval(Ruby ruby, RubyObject self) {
        RubyClass rubyClass = (RubyClass)getRecvNode().eval(ruby, self);
        if (rubyClass.isSpecialConst()) {
            throw new RubyTypeException(ruby, "no virtual class for " + rubyClass.getRubyClass().toName());
        }
        if (ruby.getSecurityLevel() >= 4 && !rubyClass.isTaint()) {
            throw new RubySecurityException(ruby, "Insecure: can't extend object");
        }
        if (rubyClass.getRubyClass().isSingleton()) {
            // rb_clear_cache();
        }
        ruby.setRubyClass(rubyClass.getSingletonClass());
        
        if (ruby.getWrapper() != null) {
            rubyClass.getSingletonClass().includeModule(ruby.getWrapper());
            rubyClass.includeModule(ruby.getWrapper());
        }
        
        return ((ScopeNode)getBodyNode()).setupModule(ruby, rubyClass);
    }
}
