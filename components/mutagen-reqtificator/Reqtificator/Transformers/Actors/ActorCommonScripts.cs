using System.Collections.Generic;
using Mutagen.Bethesda.Plugins.Cache;
using Mutagen.Bethesda.Skyrim;
using Reqtificator.StaticReferences;
using Serilog;

namespace Reqtificator.Transformers.Actors
{
    internal class ActorCommonScripts : TransformerV2<Npc, INpcGetter>
    {
        private readonly ILinkCache<ISkyrimMod, ISkyrimModGetter> _linkCache;
        private static readonly IReadOnlyList<ScriptEntry> Scripts = new[] { new ScriptEntry { Name = "REQ_NPCData" } };

        public ActorCommonScripts(ILinkCache<ISkyrimMod, ISkyrimModGetter> linkCache)
        {
            _linkCache = linkCache;
        }

        public override TransformationResult<Npc, INpcGetter> Process(TransformationResult<Npc, INpcGetter> input)
        {
            if (input.Record().FormKey == Npcs.Player.FormKey) return input;

            var inheritsScripts =
                input.Record().Configuration.TemplateFlags.HasFlag(NpcConfiguration.TemplateFlag.Script);
            return input.Record().Template.TryResolve(_linkCache) switch
            {
                INpcGetter _ when inheritsScripts => input,
                INpcGetter _ => input.Modify(r =>
                {
                    r.VirtualMachineAdapter ??= new VirtualMachineAdapter();
                    r.VirtualMachineAdapter.Scripts.AddRange(Scripts);
                    Log.Debug("added game mechanics scripts");
                }),
                ILeveledNpcGetter _ or null => input.Modify(r =>
                {
                    r.VirtualMachineAdapter ??= new VirtualMachineAdapter();
                    r.VirtualMachineAdapter.Scripts.AddRange(Scripts);
                    r.Configuration.TemplateFlags &= ~NpcConfiguration.TemplateFlag.Script;
                    Log.Debug("added game mechanics scripts");
                }),
                _ => input
            };
        }
    }
}