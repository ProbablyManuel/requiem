/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package skyproc.genenums;

import skyproc.exceptions.BadParameter;

/**
 * Different ways to move
 * @author Justin Swanson
 */
public enum MovementType {
    /**
     *
     */
    Walk,
    /**
     *
     */
    Run,
    /**
     *
     */
    Swim,
    /**
     *
     */
    Fly,
    /**
     *
     */
    Sneak,
    /**
     *
     */
    Sprint;

    static String translate (MovementType type) {
	switch (type) {
	    case Walk:
		return "WALK";
	    case Run:
		return "RUN1";
	    case Sneak:
		return "SNEK";
	    case Swim:
		return "SWIM";
	    case Sprint:
		return "BLDO";
	    default:
		return "UNKN";
	}
    }

    static MovementType translate (String type) throws BadParameter {
	switch (type) {
	    case "WALK":
		return Walk;
	    case "RUN1":
		return Run;
	    case "SNEK":
		return Sneak;
	    case "SWIM":
		return Swim;
	    case "BLDO":
		return Sprint;
	}

	throw new BadParameter ("Movement type " + type + " unrecognized.");

    }
}
