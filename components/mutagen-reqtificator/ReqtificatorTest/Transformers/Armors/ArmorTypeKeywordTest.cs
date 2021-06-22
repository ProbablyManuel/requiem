using System.Collections;
using System.Linq;
using FluentAssertions;
using Mutagen.Bethesda;
using Mutagen.Bethesda.Plugins;
using Mutagen.Bethesda.Skyrim;
using Noggog;
using Reqtificator.StaticReferences;
using Reqtificator.Transformers;
using Reqtificator.Transformers.Armors;
using Xunit;

namespace ReqtificatorTest.Transformers.Armors
{
    public class ArmorTypeKeywordTest
    {
        [Fact]
        public void Should_add_light_armor_keyword_to_light_armors_if_missing()
        {
            var keyword = FormKey.Factory("ABCDEF:Requiem.esp");
            var transformer = new ArmorTypeKeyword();
            var input = new Armor(FormKey.Factory("123456:Requiem.esp"), SkyrimRelease.SkyrimSE)
            {
                BodyTemplate = new BodyTemplate() { ArmorType = ArmorType.LightArmor },
                Keywords = new ExtendedList<IFormLinkGetter<IKeywordGetter>> { keyword }
            };

            var result = transformer.Process(new UnChanged<Armor, IArmorGetter>(input));
            var mask = new Armor.TranslationMask(defaultOn: true) { Keywords = false };
            result.Should().BeOfType<Modified<Armor, IArmorGetter>>();
            result.Record().Equals(input, mask).Should().BeTrue();
            result.Record().Keywords!.Select(k => k.FormKey).Should().Contain(
                new ArrayList { Keywords.ArmorLight.FormKey, keyword }
            );
        }

        [Fact]
        public void Should_add_heavy_armor_keyword_to_heavy_armors_if_missing()
        {
            var keyword = FormKey.Factory("ABCDEF:Requiem.esp");
            var transformer = new ArmorTypeKeyword();
            var input = new Armor(FormKey.Factory("123456:Requiem.esp"), SkyrimRelease.SkyrimSE)
            {
                BodyTemplate = new BodyTemplate() { ArmorType = ArmorType.HeavyArmor },
                Keywords = new ExtendedList<IFormLinkGetter<IKeywordGetter>> { keyword }
            };

            var result = transformer.Process(new UnChanged<Armor, IArmorGetter>(input));
            var mask = new Armor.TranslationMask(defaultOn: true) { Keywords = false };
            result.Should().BeOfType<Modified<Armor, IArmorGetter>>();
            result.Record().Equals(input, mask).Should().BeTrue();
            result.Record().Keywords!.Select(k => k.FormKey).Should().Contain(
                new ArrayList { Keywords.ArmorHeavy.FormKey, keyword }
            );
        }

        [Fact]
        public void Should_not_change_armors_that_have_a_template()
        {
            var templateArmor = FormKey.Factory("ABCDEF:Requiem.esp");
            var transformer = new ArmorTypeKeyword();
            var input = new Armor(FormKey.Factory("123456:Requiem.esp"), SkyrimRelease.SkyrimSE)
            {
                BodyTemplate = new BodyTemplate() { ArmorType = ArmorType.HeavyArmor },
                TemplateArmor = new FormLinkNullable<Armor>(templateArmor)
            };

            var result = transformer.Process(new UnChanged<Armor, IArmorGetter>(input));
            result.Should().Be(new UnChanged<Armor, IArmorGetter>(input));
        }

        [Fact]
        public void Should_not_change_clothing_records()
        {
            var transformer = new ArmorTypeKeyword();
            var input = new Armor(FormKey.Factory("123456:Requiem.esp"), SkyrimRelease.SkyrimSE)
            {
                BodyTemplate = new BodyTemplate() { ArmorType = ArmorType.Clothing }
            };

            var result = transformer.Process(new UnChanged<Armor, IArmorGetter>(input));
            result.Should().Be(new UnChanged<Armor, IArmorGetter>(input));
        }

        [Fact]
        public void Should_not_change_light_armors_that_have_the_required_keyword()
        {
            var transformer = new ArmorTypeKeyword();
            var input = new Armor(FormKey.Factory("123456:Requiem.esp"), SkyrimRelease.SkyrimSE)
            {
                BodyTemplate = new BodyTemplate() { ArmorType = ArmorType.LightArmor },
                Keywords = new ExtendedList<IFormLinkGetter<IKeywordGetter>> { Keywords.ArmorLight }
            };

            var result = transformer.Process(new UnChanged<Armor, IArmorGetter>(input));
            result.Should().Be(new UnChanged<Armor, IArmorGetter>(input));
        }

        [Fact]
        public void Should_not_change_heavy_armors_that_have_the_required_keyword()
        {
            var transformer = new ArmorTypeKeyword();
            var input = new Armor(FormKey.Factory("123456:Requiem.esp"), SkyrimRelease.SkyrimSE)
            {
                BodyTemplate = new BodyTemplate() { ArmorType = ArmorType.HeavyArmor },
                Keywords = new ExtendedList<IFormLinkGetter<IKeywordGetter>> { Keywords.ArmorHeavy }
            };

            var result = transformer.Process(new UnChanged<Armor, IArmorGetter>(input));
            result.Should().Be(new UnChanged<Armor, IArmorGetter>(input));
        }
    }
}