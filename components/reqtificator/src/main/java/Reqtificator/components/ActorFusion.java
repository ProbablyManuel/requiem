/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Reqtificator.components;

import Reqtificator.exceptions.PatchingException;
import Reqtificator.logging_and_gui.TextManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;
import skyproc.*;
import skyproc.NPC_.NPCFlag;
import skyproc.NPC_.NPCStat;
import skyproc.NPC_.TemplateFlag;
import skyproc.genenums.Skill;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**The ActorFusion component is a helper class to copy data between NPCs.
 * It can either be used to copy an individual data section between NPCs or it
 * can be used to link a given actor to a new template and different inheritance
 * flags without loosing any data from the previous template.
 *
 * @author Ogerboss
 */
class ActorFusion extends Component {

    private final static Logger logger = LogManager.getLogger();

    final private TextManager texts;

    /**Create a new ActorFusion instance.
     *
     */
    protected ActorFusion(TextManager texts) {
        super();
        this.texts = texts;
    }

    /**Fusion two actors.
     * This function creates a clone of the given actor and assigns the given
     * EditorID to it. Afterwards, it assigns a new template to the clone and
     * sets the inheritance flags as specified in the arguments. Any data that
     * was previously inherited from the old template, is copied explicitly to
     * ensure that no data is lost. If the actor inherited any data from a
     * leveled character, an exception will be thrown because this inheritance
     * cannot be resolved at compile time.
     *
     * @param actor the NPC to manipulate
     * @param newtemplate the FormID of the new template to use
     * @param eid the editorID to be assigned to the clone
     * @param toinherit the inheritance flags for the clone
     * @return the cloned actor inheriting the specified data from the new
     * template
     * @throws PatchingException if inheritance from a leveled character was
     * encountered
     */
    protected NPC_ fuse_actors(NPC_ actor, FormID newtemplate, String eid,
            TemplateFlag... toinherit) throws PatchingException {
        int stackDepth = ThreadContext.getDepth();
        ThreadContext.push(formatRecord(actor));
        NPC_ fusion = (NPC_) actor.copy(eid);
        ThreadContext.push(formatRecord(fusion));
        List<TemplateFlag> flaglist = Arrays.asList(toinherit);
        for (TemplateFlag flag : TemplateFlag.values()) {
            if (flaglist.contains(flag)) {
                fusion.set(flag, true);
            } else if (fusion.get(flag)) {
                fusion.set(flag, false);
                NPC_ source = find_ancestor(actor, flag);
                ThreadContext.push(formatRecord(source));
                switch (flag) {
                    case USE_AI_DATA:
                        copy_AIdata(fusion, source);
                        break;
                    case USE_AI_PACKAGES:
                        copy_AIpackages(fusion, source);
                        break;
                    case USE_ATTACK_DATA:
                        copy_attackdata(fusion, source);
                        break;
                    case USE_BASE_DATA:
                        copy_basedata(fusion, source);
                        break;
                    case USE_DEF_PACK_LIST:
                        copy_defaultpackages(fusion, source);
                        break;
                    case USE_FACTIONS:
                        copy_factions(fusion, source);
                        break;
                    case USE_INVENTORY:
                        copy_inventory(fusion, source);
                        break;
                    case USE_KEYWORDS:
                        copy_keywords(fusion, source);
                        break;
                    case USE_SCRIPTS:
                        copy_scripts(fusion, source);
                        break;
                    case USE_SPELL_LIST:
                        copy_spells_perks(fusion, source);
                        break;
                    case USE_STATS:
                        copy_stats(fusion, source);
                        break;
                    case USE_TRAITS:
                        copy_traits(fusion, source);
                        break;
                    default:
                        throw new UnsupportedOperationException(
                                "Inheritance flag " + flag +
                                " not implemented!");
                }
                ThreadContext.pop();
            }
        }
        fusion.setTemplate(newtemplate);
        ThreadContext.trim(stackDepth);
        return fusion;
    }

    /**Find the actor from which the given NPC inherits the specified data.
     * This functions traverses the inheritance chain of the given NPC until
     * it either finds the root template (has no template on its own) or an
     * intermediate actor which does not inherit the specified data from his
     * template. If a LeveledCharacter is found along the way, an exception
     * is raised because determining a unique ancestor is not possible in
     * this case.
     *
     * @param actor the actor to find the ancestor for
     * @param flag the inheritance flag for which the inheritance shall be
     * analyzed
     * @return the template defining the data (or the actor itself if the
     * flag is not checked)
     * @throws PatchingException if the inheritance chain leads to a leveled
     * Character, these cannot be resolved at compile-time
     */
    private NPC_ find_ancestor(NPC_ actor, TemplateFlag flag)
            throws PatchingException {
        NPC_ current = actor;
        ArrayList<MajorRecord> inheritance = new ArrayList<>();
        while (current.get(flag) && !current.getTemplate().isNull()) {
            MajorRecord template = SPDatabase.getMajor(current.getTemplate(),
                    GRUP_TYPE.NPC_, GRUP_TYPE.LVLN);
            inheritance.add(template);
            if (template instanceof LVLN) {
                StringBuilder sb = new StringBuilder();
                for (MajorRecord ancestor : inheritance) {
                    sb.append(texts.format(
                            "patch.actor_fusion.error_leveled_template_entry",
                            formatRecordPretty(ancestor)));
                }
                String message = texts.format(
                        "patch.actor_fusion.error_leveled_template",
                        formatRecordPretty(actor), flag.name(), sb.toString());
                throw new PatchingException(
                        "ambiguous inheritance cannot be resolved", message);
            }
            current = (NPC_) template;
        }
        return current;
    }

    /**Copy faction data from source to target actor.
     *
     * @param target NPC to copy to
     * @param source NPC to copy from
     */
    protected void copy_factions(NPC_ target, NPC_ source) {
        target.set(NPC_.TemplateFlag.USE_FACTIONS, false);
        target.clearFactions();
        for (SubFormInt group : source.getFactions()) {
            target.addFaction(group.getForm(), group.getNum());
        }
        target.setCrimeFaction(source.getCrimeFaction());
    }

    /**Copy script data from source to target actor.
     *
     * @param target NPC to copy to
     * @param source NPC to copy from
     */
    protected void copy_scripts(NPC_ target, NPC_ source) {
        target.set(TemplateFlag.USE_SCRIPTS, false);
        ScriptPackage scriptlist = target.getScriptPackage();
        for (ScriptRef script: scriptlist.getScripts()) {
            scriptlist.removeScript(script);
        }
        for (ScriptRef script: source.getScriptPackage().getScripts()) {
            scriptlist.addScript(script);
        }
    }

    /**Copy keyword data from source to target actor.
     *
     * @param target NPC to copy to
     * @param source NPC to copy from
     */
    protected void copy_keywords(NPC_ target, NPC_ source) {
        target.set(TemplateFlag.USE_KEYWORDS, false);
        target.getKeywordSet().clearKeywordRefs();
        for (FormID keyword : source.getKeywordSet().getKeywordRefs()) {
            target.getKeywordSet().addKeywordRef(keyword);
        }
    }

    /**Copy AI Data data from source to target actor.
     *
     * @param target NPC to copy to
     * @param source NPC to copy from
     */
    protected void copy_AIdata(NPC_ target, NPC_ source) {
        target.set(TemplateFlag.USE_AI_DATA, false);
        target.setAggression(source.getAggression());
        target.setConfidence(source.getConfidence());
        target.setAssistance(source.getAssistance());
        target.setMood(source.getMood());
        target.setEnergy(source.getEnergy());
        target.setMorality(source.getMorality());
        target.set(NPCFlag.AggroRadiusBehavior,
                source.get(NPCFlag.AggroRadiusBehavior));
        target.setAggroWarn(source.getAggroWarn());
        target.setAggroWarnAttack(source.getAggroWarnAttack());
        target.setAggroAttack(source.getAggroAttack());
        target.setCombatStyle(source.getCombatStyle());
        //TODO: copy gift-filter once fixed in SkyProc library
//        target.setGiftFilter(source.getGiftFilter());
    }

    /**Copy AI Package data from source to target actor.
     *
     * @param target NPC to copy to
     * @param source NPC to copy from
     */
    protected void copy_AIpackages(NPC_ target, NPC_ source) {
        target.set(TemplateFlag.USE_AI_PACKAGES, false);
        target.clearAIPackages();
        for (FormID fid_package : source.getAIPackages()) {
            target.addAIPackage(fid_package);
        }
    }

    /**Copy default packages data from source to target actor.
     *
     * @param target NPC to copy to
     * @param source NPC to copy from
     */
    protected void copy_defaultpackages(NPC_ target, NPC_ source) {
        target.set(TemplateFlag.USE_DEF_PACK_LIST, false);
        target.setDefaultPackageList(source.getDefaultPackageList());
        target.setSpectatorOverride(source.getSpectatorOverride());
        target.setObserveDeadOverride(source.getObserveDeadOverride());
        target.setGuardWornOverride(source.getGuardWornOverride());
        target.setCombatOverride(source.getCombatOverride());
    }

    /**Copy stats data from source to target actor.
     *
     * @param target NPC to copy to
     * @param source NPC to copy from
     */
    protected void copy_stats(NPC_ target, NPC_ source) {
        target.set(TemplateFlag.USE_STATS, false);
        target.set(NPCStat.LEVEL, target.get(NPCStat.LEVEL));
        target.set(NPCStat.MIN_CALC_LEVEL, target.get(NPCStat.MIN_CALC_LEVEL));
        target.set(NPCStat.MAX_CALC_LEVEL, target.get(NPCStat.MAX_CALC_LEVEL));
        target.set(NPCFlag.PCLevelMult, source.get(NPCFlag.PCLevelMult));
        target.set(NPCFlag.AutoCalcStats, source.get(NPCFlag.AutoCalcStats));
        target.setHealthOffset(source.getHealthOffset());
        target.setMagickaOffset(source.getMagickaOffset());
        target.setFatigueOffset(source.getFatigueOffset());
        for (Skill stat: Skill.NPC_Skills()) {
            target.set(stat, source.get(stat));
        }
        target.set(NPCStat.SPEED_MULT, source.get(NPCStat.SPEED_MULT));
        target.set(NPCFlag.BleedoutOverride,
                source.get(NPCFlag.BleedoutOverride));
        target.setNPCClass(source.getNPCClass());
    }

    /**Copy spells and perks data from source to target actor.
     *
     * @param target NPC to copy to
     * @param source NPC to copy from
     */
    protected void copy_spells_perks(NPC_ target, NPC_ source) {
        target.set(TemplateFlag.USE_SPELL_LIST, false);
        target.clearSpells();
        for (FormID spell: source.getSpells()) {
            target.addSpell(spell);
        }
        target.clearPerks();
        for (SubFormInt perk: source.getPerks()) {
            target.addPerk(perk.getForm(), perk.getNum());
        }
    }

     /**Copy base data from source to target actor.
     *
     * @param target NPC to copy to
     * @param source NPC to copy from
     */
    protected void copy_basedata(NPC_ target, NPC_ source) {
        target.set(TemplateFlag.USE_BASE_DATA, false);
        target.setName(source.getName());
        //copy short name, once fixed in SkyProc
        //target.setShortName(source.getShortName());
        target.set(NPCFlag.IsCharGenFacePreset,
                source.get(NPCFlag.IsCharGenFacePreset));
        target.set(NPCFlag.Essential, source.get(NPCFlag.Essential));
        target.set(NPCFlag.Protected, source.get(NPCFlag.Protected));
        target.set(NPCFlag.Respawn, source.get(NPCFlag.Respawn));
        target.set(NPCFlag.Unique, source.get(NPCFlag.Unique));
        target.set(NPCFlag.Summonable, source.get(NPCFlag.Summonable));
        target.set(NPCFlag.IsGhost, source.get(NPCFlag.IsGhost));
        target.set(NPCFlag.Invulnerable, source.get(NPCFlag.Invulnerable));
        target.set(NPCFlag.DoesntBleed, source.get(NPCFlag.DoesntBleed));
        target.set(NPCFlag.SimpleActor, source.get(NPCFlag.SimpleActor));
        target.set(NPCFlag.DoesntAffectStealthMeter,
                source.get(NPCFlag.DoesntAffectStealthMeter));
    }

     /**Copy attack data from source to target actor.
     *
     * @param target NPC to copy to
     * @param source NPC to copy from
     */
    protected void copy_attackdata(NPC_ target, NPC_ source) {
        target.set(TemplateFlag.USE_ATTACK_DATA, false);
        target.setAttackDataRace(source.getAttackDataRace());
        target.clearAttackPackages();
        for (NPC_.AttackPackage pack: source.getAttackPackages()) {
            target.addAttackPackage(pack);
        }
    }

    /**Copy traits data from source to target actor.
     *
     * @param target NPC to copy to
     * @param source NPC to copy from
     */
    protected void copy_traits(NPC_ target, NPC_ source) {
        target.set(TemplateFlag.USE_TRAITS, false);
        //Traits tab
        target.setRace(source.getRace());
        target.set(NPCFlag.Female, source.get(NPCFlag.Female));
        target.setSkin(source.getSkin());
        target.setHeight(source.getHeight());
        target.setWeight(source.getWeight());
        target.setFarAwayModelSkin(source.getFarAwayModelSkin());
        target.setFarAwayModelDistance(source.getFarAwayModelDistance());
        target.setVoiceType(source.getVoiceType());
        target.set(NPCStat.DISPOSITION_BASE,
                source.get(NPCStat.DISPOSITION_BASE));
        target.setDeathItem(source.getDeathItem());
        target.set(NPCFlag.OppositeGenderAnims,
                source.get(NPCFlag.OppositeGenderAnims));
        //Sounds tab
        target.setSoundVolume(source.getSoundVolume());
        target.setAudioTemplate(source.getAudioTemplate());
        target.clearSoundPackages();
        for (NPC_.SoundPackage pack: source.getSounds()) {
            target.addSoundPackage(pack);
        }
        //Character Gen Parts tab
        target.setFeatureSet(source.getFeatureSet());
        target.clearHeadParts();
        target.setHairColor(source.getHairColor());
        for (FormID part: source.getHeadParts()) {
            target.addHeadPart(part);
        }
        target.clearTinting();
        for (NPC_.TintLayer tint: source.getTinting()) {
            NPC_.TintLayer clone = new NPC_.TintLayer(tint.getIndex());
            for (RGBA color: RGBA.values()) {
                clone.setColor(color, tint.getColor(color));
            }
            clone.setInterpolation(tint.getInterpolation());
            clone.setPreset(tint.getPreset());
            target.addTinting(clone);
        }
        for (RGB color : RGB.values()) {
            target.setFaceTint(color, source.getFaceTint(color));
        }
        //Character Gen Morphs tab
        target.setEyePreset(source.getEyePreset());
        target.setNosePreset(source.getNosePreset());
        target.setMouthPreset(source.getMouthPreset());
        for (NPC_.FacePart part: NPC_.FacePart.values()) {
            target.setFaceValue(part, source.getFaceValue(part));
        }
    }

    /**Copy inventory data from source to target actor.
     *
     * @param target NPC to copy to
     * @param source NPC to copy from
     */
    protected void copy_inventory(NPC_ target, NPC_ source) {
        target.set(TemplateFlag.USE_INVENTORY, false);
        target.setDefaultOutfit(source.getDefaultOutfit());
        target.setSleepingOutfit(source.getSleepingOutfit());
        target.clearItems();
        for (ItemListing entry: source.getItems()) {
            target.addItem(entry.getForm(), entry.getCount());
        }
    }
}
