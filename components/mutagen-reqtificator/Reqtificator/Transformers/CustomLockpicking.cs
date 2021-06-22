using System.Linq;
using Mutagen.Bethesda;
using Mutagen.Bethesda.Plugins.Records;
using Mutagen.Bethesda.Skyrim;
using Reqtificator.StaticReferences;
using Serilog;

namespace Reqtificator.Transformers
{
    internal class CustomLockpicking<T, TI, TGetter> : Transformer<T, TI, TGetter>
        where T : MajorRecord, TI, IScriptedGetter
        where TI : IMajorRecord, TGetter, IScripted
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

        public override bool ShouldProcess(TGetter record)
        {
            return record.VirtualMachineAdapter?.Scripts.All(s => s.Name != _lockPickingScript.Name) ?? true;
        }

        public override void Process(TI record)
        {
            if (record.VirtualMachineAdapter == null)
            {
                record.VirtualMachineAdapter = new VirtualMachineAdapter();
            }

            record.VirtualMachineAdapter.Scripts.Add(_lockPickingScript);
            Log.Debug("added script for lockpicking implementation");
        }
    }
}