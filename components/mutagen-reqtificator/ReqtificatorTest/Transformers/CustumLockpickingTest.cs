using FluentAssertions;
using Mutagen.Bethesda;
using Mutagen.Bethesda.Plugins;
using Mutagen.Bethesda.Skyrim;
using Reqtificator.StaticReferences;
using Reqtificator.Transformers;
using Xunit;

namespace ReqtificatorTest.Transformers
{
    public static class CustumLockpickingTest
    {
        public class ShouldProcess
        {
            [Fact]
            public void Should_select_records_without_defined_VirtualMachineAdapter()
            {
                var transformer = new CustomLockpicking<Door, IDoor, IDoorGetter>();
                var door = new Door(FormKey.Factory("123456:Foo.esp"), SkyrimRelease.SkyrimSE)
                {
                    VirtualMachineAdapter = null
                };

                transformer.ShouldProcess(door).Should().BeTrue();
            }

            [Fact]
            public void Should_select_tecords_with_rmpty_scripts_list_in_VirtualMachineAdapter()
            {
                var transformer = new CustomLockpicking<Door, IDoor, IDoorGetter>();
                var door = new Door(FormKey.Factory("123456:Foo.esp"), SkyrimRelease.SkyrimSE)
                {
                    VirtualMachineAdapter = new VirtualMachineAdapter()
                };

                transformer.ShouldProcess(door).Should().BeTrue();
            }

            [Fact]
            public void Should_select_records_with_unrelated_scripts_tegistered_in_the_VirtualMachineAdapter()
            {
                var transformer = new CustomLockpicking<Door, IDoor, IDoorGetter>();
                var door = new Door(FormKey.Factory("123456:Foo.esp"), SkyrimRelease.SkyrimSE)
                {
                    VirtualMachineAdapter = new VirtualMachineAdapter()
                    {
                        Scripts = { new ScriptEntry() { Name = "SomeOtherScript" } }
                    }
                };

                transformer.ShouldProcess(door).Should().BeTrue();
            }

            [Fact]
            public void Should_not_select_records_that_already_have_a_binding_for_the_lockpicking_script()
            {
                var transformer = new CustomLockpicking<Door, IDoor, IDoorGetter>();
                var door = new Door(FormKey.Factory("123456:Foo.esp"), SkyrimRelease.SkyrimSE)
                {
                    VirtualMachineAdapter = new VirtualMachineAdapter()
                    {
                        Scripts = { new ScriptEntry() { Name = "REQ_LockpickControl" } }
                    }
                };

                transformer.ShouldProcess(door).Should().BeFalse();
            }
        }

        public class PatchRecord
        {
            private static ScriptEntry expectedScript()
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
                return lockPickingControScript;
            }

            [Fact]
            public void Should_add_the_custom_lockpicking_script_binding()
            {
                var transformer = new CustomLockpicking<Door, IDoor, IDoorGetter>();
                var otherScript = new ScriptEntry() { Name = "SomeOtherScript" };
                var door = new Door(FormKey.Factory("123456:Foo.esp"), SkyrimRelease.SkyrimSE)
                {
                    VirtualMachineAdapter = new VirtualMachineAdapter { Scripts = { otherScript } }
                };
                var reference = door.DeepCopy();

                transformer.Process(door);
                var mask = new Door.TranslationMask(defaultOn: true) { VirtualMachineAdapter = false };
                reference.Equals(door, mask).Should().BeTrue();
                var expectedVirtualMachineAdapter = reference.VirtualMachineAdapter!.DeepCopy();
                expectedVirtualMachineAdapter.Scripts.Add(expectedScript());
                expectedVirtualMachineAdapter.Equals(door.VirtualMachineAdapter).Should().BeTrue();
            }
        }
    }
}