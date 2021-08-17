using System.Linq;
using Mutagen.Bethesda;
using Mutagen.Bethesda.Plugins.Records;
using Mutagen.Bethesda.Skyrim;
using Reqtificator.StaticReferences;
using Serilog;

namespace Reqtificator.Transformers
{
    internal class CustomLockpicking<T, TGetter> : TransformerV2<T, TGetter>
        where T : MajorRecord, TGetter, IScripted
        where TGetter : IMajorRecordGetter, IScriptedGetter
    {
        private readonly ScriptEntry _lockPickingScript;

        public CustomLockpicking()
        {
            var lockPickingControScript = new ScriptEntry();
            lockPickingControScript.Name = "REQ_LockpickControl";
            lockPickingControScript.Properties.Add(new ScriptObjectProperty()
            {
                Name = "dataStorage",
                Alias = 18,
                Object = Quests.LockpickingControl.AsSetter(),
                Flags = ScriptProperty.Flag.Edited
            });
            lockPickingControScript.Properties.Add(new ScriptObjectProperty()
            {
                Name = "FollowerControl",
                Object = Quests.FollowerControl.AsSetter(),
                Flags = ScriptProperty.Flag.Edited
            });
            _lockPickingScript = lockPickingControScript;
        }

        public override TransformationResult<T, TGetter> Process(
            TransformationResult<T, TGetter> input)
        {
            var lockpickingScriptBound = input.Record().VirtualMachineAdapter?.Scripts.Any(s => s.Name == _lockPickingScript.Name) ?? false;
            if (lockpickingScriptBound) { return input; }

            var result = input.Modify(record =>
            {
                record.VirtualMachineAdapter ??= new VirtualMachineAdapter();
                record.VirtualMachineAdapter.Scripts.Add(_lockPickingScript);
            });
            Log.Debug("added script for lockpicking implementation");
            return result;
        }
    }
}