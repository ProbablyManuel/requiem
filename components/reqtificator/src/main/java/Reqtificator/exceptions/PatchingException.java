/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Reqtificator.exceptions;

/**The PatchingException indicates inconsistencies within the imported GameData.
 * This kind of Exception should be raised if the imported data from the user's
 * load order contains records that are in a state that is incompatible to
 * Requiem. Examples would be overwritten races or unpatched playable custom
 * races and Leveled Lists that qualify for special processing, but do not
 * contain the expected data.
 *
 * @author Ogerboss
 */
public class PatchingException extends BaseException{

    public PatchingException(String log, String gui) {
        super(log, gui);
    }

    public PatchingException(String log, String gui, Exception error) {
        super(log, gui, error);
    }
}
