using FluentAssertions;
using Mutagen.Bethesda;
using Mutagen.Bethesda.Plugins;
using Mutagen.Bethesda.Skyrim;
using Reqtificator.StaticReferences;
using Reqtificator.Transformers;
using Xunit;

namespace ReqtificatorTest.Transformers
{
    public class CustomLockpickingTest
    {
        [Fact]
        public void Should_add_the_custom_lockpicking_script_binding_to_existing_scripts()
        {
            var transformer = new CustomLockpicking<Door, IDoor, IDoorGetter>();
            var otherScript = new ScriptEntry() { Name = "SomeOtherScript" };
            var door = new Door(FormKey.Factory("123456:Foo.esp"), SkyrimRelease.SkyrimSE)
            {
                VirtualMachineAdapter = new VirtualMachineAdapter { Scripts = { otherScript } }
            };
            var reference = door.DeepCopy();

            var result = transformer.Process(new UnChanged<Door, IDoorGetter>(door));
            result.IsModified().Should().BeTrue();

            var mask = new Door.TranslationMask(defaultOn: true) { VirtualMachineAdapter = false };

            reference.Equals(result.Record(), mask).Should().BeTrue();

            var expectedVirtualMachineAdapter = reference.VirtualMachineAdapter!.DeepCopy();
            expectedVirtualMachineAdapter.Scripts.Add(expectedScript());
            expectedVirtualMachineAdapter.Equals(result.Record().VirtualMachineAdapter).Should().BeTrue();
        }

        [Fact]
        public void Should_create_a_VM_for_custom_lockpicking_script_if_VM_is_null()
        {
            var transformer = new CustomLockpicking<Door, IDoor, IDoorGetter>();
            var door = new Door(FormKey.Factory("123456:Foo.esp"), SkyrimRelease.SkyrimSE)
            {
                VirtualMachineAdapter = null
            };

            var result = transformer.Process(new UnChanged<Door, IDoorGetter>(door));
            result.IsModified().Should().BeTrue();

            var resultScripts = result.Record().VirtualMachineAdapter!.Scripts;
            resultScripts.Count.Should().Be(1);
            resultScripts[0].Equals(expectedScript()).Should().BeTrue();
        }

        [Fact]
        public void Should_modify_records_with_no_scripts_in_VirtualMachineAdapter()
        {
            var transformer = new CustomLockpicking<Door, IDoor, IDoorGetter>();
            var door = new Door(FormKey.Factory("123456:Foo.esp"), SkyrimRelease.SkyrimSE)
            {
                VirtualMachineAdapter = new VirtualMachineAdapter()
            };
            var reference = door.DeepCopy();

            var result = transformer.Process(new UnChanged<Door, IDoorGetter>(door));
            result.IsModified().Should().BeTrue();

            var mask = new Door.TranslationMask(defaultOn: true) { VirtualMachineAdapter = false };

            reference.Equals(result.Record(), mask).Should().BeTrue();

            var expectedVirtualMachineAdapter = reference.VirtualMachineAdapter!.DeepCopy();
            expectedVirtualMachineAdapter.Scripts.Add(expectedScript());
            expectedVirtualMachineAdapter.Equals(result.Record().VirtualMachineAdapter).Should().BeTrue();
        }

        [Fact]
        public void Should_not_modify_records_that_already_have_a_binding_for_the_lockpicking_script()
        {
            var transformer = new CustomLockpicking<Door, IDoor, IDoorGetter>();
            var door = new Door(FormKey.Factory("123456:Foo.esp"), SkyrimRelease.SkyrimSE)
            {
                VirtualMachineAdapter = new VirtualMachineAdapter()
                {
                    Scripts = { new ScriptEntry() { Name = "REQ_LockpickControl" } }
                }
            };

            var result = transformer.Process(new UnChanged<Door, IDoorGetter>(door));
            result.IsModified().Should().BeFalse();
            result.Record().Equals(door).Should().BeTrue();
        }
        private static ScriptEntry expectedScript()
        {
            var lockPickingControScript = new ScriptEntry
            {
                Name = "REQ_LockpickControl"
            };
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
            return lockPickingControScript;
        }
    }
}