using FluentAssertions;
using Mutagen.Bethesda;
using Mutagen.Bethesda.Plugins;
using Mutagen.Bethesda.Skyrim;
using Noggog;
using Reqtificator.Configuration;
using Reqtificator.StaticReferences;
using Reqtificator.Transformers;
using Reqtificator.Transformers.Armors;
using Xunit;

namespace ReqtificatorTest.Transformers.Armors
{
    public class ArmorRatingScalingTest
    {
        public static readonly ArmorRatingThresholds LightArmorThresholds = new(10, 20, 30, 40, 50);
        public static readonly ArmorRatingThresholds HeavyArmorThresholds = new(60, 70, 80, 90, 100);

        public static readonly ArmorPatchingConfiguration Config = new(HeavyArmorThresholds, LightArmorThresholds);

        [Fact]
        public void Should_scale_up_light_body_armor_ratings_if_below_the_corresponding_threshold()
        {
            var otherKeyword = FormKey.Factory("ABCDEF:Requiem.esp");
            var transformer = new ArmorRatingScaling(Config);

            var input = new Armor(FormKey.Factory("123456:Requiem.esp"), SkyrimRelease.SkyrimSE)
            {
                BodyTemplate = new BodyTemplate() { ArmorType = ArmorType.LightArmor },
                Keywords = new ExtendedList<IFormLinkGetter<IKeywordGetter>> { otherKeyword, Keywords.ArmorBody },
                ArmorRating = 5.0f
            };

            var result = transformer.Process(new UnChanged<Armor, IArmorGetter>(input));
            var mask = new Armor.TranslationMask(defaultOn: true) { ArmorRating = false };
            result.Should().BeOfType<Modified<Armor, IArmorGetter>>();
            result.Record().Equals(input, mask).Should().BeTrue();
            var expectedValue = input.ArmorRating * 3.3f + 66f;
            result.Record().ArmorRating.Should().BeInRange(expectedValue * 0.99f, expectedValue * 1.01f);
        }

        [Fact]
        public void Should_scale_up_light_non_body_armor_ratings_if_below_the_corresponding_threshold()
        {
            var otherKeyword = FormKey.Factory("ABCDEF:Requiem.esp");
            var transformer = new ArmorRatingScaling(Config);

            var input = new Armor(FormKey.Factory("123456:Requiem.esp"), SkyrimRelease.SkyrimSE)
            {
                BodyTemplate = new BodyTemplate() { ArmorType = ArmorType.LightArmor },
                Keywords = new ExtendedList<IFormLinkGetter<IKeywordGetter>> { otherKeyword, Keywords.ArmorFeet },
                ArmorRating = 10.0f
            };

            var result = transformer.Process(new UnChanged<Armor, IArmorGetter>(input));
            var mask = new Armor.TranslationMask(defaultOn: true) { ArmorRating = false };
            result.Should().BeOfType<Modified<Armor, IArmorGetter>>();
            result.Record().Equals(input, mask).Should().BeTrue();
            var expectedValue = input.ArmorRating * 3.3f + 18f;
            result.Record().ArmorRating.Should().BeInRange(expectedValue * 0.99f, expectedValue * 1.01f);
        }

        [Fact]
        public void Should_not_change_light_armor_records_if_their_armor_rating_is_above_the_threshold()
        {
            var otherKeyword = FormKey.Factory("ABCDEF:Requiem.esp");
            var transformer = new ArmorRatingScaling(Config);

            var input = new Armor(FormKey.Factory("123456:Requiem.esp"), SkyrimRelease.SkyrimSE)
            {
                BodyTemplate = new BodyTemplate() { ArmorType = ArmorType.LightArmor },
                Keywords = new ExtendedList<IFormLinkGetter<IKeywordGetter>> { otherKeyword, Keywords.ArmorBody },
                ArmorRating = 50.0f
            };

            var result = transformer.Process(new UnChanged<Armor, IArmorGetter>(input));
            result.Should().BeOfType<UnChanged<Armor, IArmorGetter>>();
            result.Record().Equals(input).Should().BeTrue();
        }


        [Fact]
        public void Should_scale_up_heavy_body_armor_ratings_if_below_the_corresponding_threshold()
        {
            var otherKeyword = FormKey.Factory("ABCDEF:Requiem.esp");
            var transformer = new ArmorRatingScaling(Config);

            var input = new Armor(FormKey.Factory("123456:Requiem.esp"), SkyrimRelease.SkyrimSE)
            {
                BodyTemplate = new BodyTemplate() { ArmorType = ArmorType.HeavyArmor },
                Keywords = new ExtendedList<IFormLinkGetter<IKeywordGetter>> { otherKeyword, Keywords.ArmorBody },
                ArmorRating = 45.0f
            };

            var result = transformer.Process(new UnChanged<Armor, IArmorGetter>(input));
            var mask = new Armor.TranslationMask(defaultOn: true) { ArmorRating = false };
            result.Should().BeOfType<Modified<Armor, IArmorGetter>>();
            result.Record().Equals(input, mask).Should().BeTrue();
            var expectedValue = input.ArmorRating * 6.6f + 66f;
            result.Record().ArmorRating.Should().BeInRange(expectedValue * 0.99f, expectedValue * 1.01f);
        }

        [Fact]
        public void Should_scale_up_heavy_non_body_armor_ratings_if_below_the_corresponding_threshold()
        {
            var otherKeyword = FormKey.Factory("ABCDEF:Requiem.esp");
            var transformer = new ArmorRatingScaling(Config);

            var input = new Armor(FormKey.Factory("123456:Requiem.esp"), SkyrimRelease.SkyrimSE)
            {
                BodyTemplate = new BodyTemplate() { ArmorType = ArmorType.HeavyArmor },
                Keywords = new ExtendedList<IFormLinkGetter<IKeywordGetter>> { otherKeyword, Keywords.ArmorFeet },
                ArmorRating = 25.0f
            };

            var result = transformer.Process(new UnChanged<Armor, IArmorGetter>(input));
            var mask = new Armor.TranslationMask(defaultOn: true) { ArmorRating = false };
            result.Should().BeOfType<Modified<Armor, IArmorGetter>>();
            result.Record().Equals(input, mask).Should().BeTrue();
            var expectedValue = input.ArmorRating * 6.6f + 18f;
            result.Record().ArmorRating.Should().BeInRange(expectedValue * 0.99f, expectedValue * 1.01f);
        }

        [Fact]
        public void Should_not_change_heavy_armor_records_if_their_armor_rating_is_above_the_threshold()
        {
            var otherKeyword = FormKey.Factory("ABCDEF:Requiem.esp");
            var transformer = new ArmorRatingScaling(Config);

            var input = new Armor(FormKey.Factory("123456:Requiem.esp"), SkyrimRelease.SkyrimSE)
            {
                BodyTemplate = new BodyTemplate() { ArmorType = ArmorType.HeavyArmor },
                Keywords = new ExtendedList<IFormLinkGetter<IKeywordGetter>> { otherKeyword, Keywords.ArmorBody },
                ArmorRating = 150.0f
            };

            var result = transformer.Process(new UnChanged<Armor, IArmorGetter>(input));
            result.Should().BeOfType<UnChanged<Armor, IArmorGetter>>();
            result.Record().Equals(input).Should().BeTrue();
        }

        [Fact]
        public void Should_not_change_any_clothing_record()
        {
            var otherKeyword = FormKey.Factory("ABCDEF:Requiem.esp");
            var transformer = new ArmorRatingScaling(Config);

            var input = new Armor(FormKey.Factory("123456:Requiem.esp"), SkyrimRelease.SkyrimSE)
            {
                BodyTemplate = new BodyTemplate() { ArmorType = ArmorType.Clothing },
                Keywords = new ExtendedList<IFormLinkGetter<IKeywordGetter>> { otherKeyword, Keywords.ArmorBody },
                ArmorRating = 666.0f
            };

            var result = transformer.Process(new UnChanged<Armor, IArmorGetter>(input));
            result.Should().BeOfType<UnChanged<Armor, IArmorGetter>>();
            result.Record().Equals(input).Should().BeTrue();
        }

        [Fact]
        public void Should_not_change_templated_records()
        {
            var template = FormKey.Factory("ABCDEF:Requiem.esp");
            var transformer = new ArmorRatingScaling(Config);

            var input = new Armor(FormKey.Factory("123456:Requiem.esp"), SkyrimRelease.SkyrimSE)
            {
                BodyTemplate = new BodyTemplate() { ArmorType = ArmorType.HeavyArmor },
                Keywords = new ExtendedList<IFormLinkGetter<IKeywordGetter>> { Keywords.ArmorBody },
                TemplateArmor = new FormLinkNullable<IArmorGetter>(template),
                ArmorRating = 42.0f
            };

            var result = transformer.Process(new UnChanged<Armor, IArmorGetter>(input));
            result.Should().BeOfType<UnChanged<Armor, IArmorGetter>>();
            result.Record().Equals(input).Should().BeTrue();
        }

        [Fact]
        public void Should_not_change_records_marked_as_already_reqtified()
        {
            var template = FormKey.Factory("ABCDEF:Requiem.esp");
            var transformer = new ArmorRatingScaling(Config);

            var input = new Armor(FormKey.Factory("123456:Requiem.esp"), SkyrimRelease.SkyrimSE)
            {
                BodyTemplate = new BodyTemplate { ArmorType = ArmorType.HeavyArmor },
                Keywords = new ExtendedList<IFormLinkGetter<IKeywordGetter>>
                    {Keywords.ArmorBody, Keywords.AlreadyReqtified},
                TemplateArmor = new FormLinkNullable<IArmorGetter>(template),
                ArmorRating = 42.0f
            };

            var result = transformer.Process(new UnChanged<Armor, IArmorGetter>(input));
            result.Should().BeOfType<UnChanged<Armor, IArmorGetter>>();
            result.Record().Equals(input).Should().BeTrue();
        }

        [Fact]
        public void Should_not_change_records_marked_as_excluded_from_armor_rating_scaling()
        {
            var template = FormKey.Factory("ABCDEF:Requiem.esp");
            var transformer = new ArmorRatingScaling(Config);

            var input = new Armor(FormKey.Factory("123456:Requiem.esp"), SkyrimRelease.SkyrimSE)
            {
                BodyTemplate = new BodyTemplate { ArmorType = ArmorType.HeavyArmor },
                Keywords = new ExtendedList<IFormLinkGetter<IKeywordGetter>>
                    {Keywords.ArmorBody, Keywords.NoArmorRatingRescaling},
                TemplateArmor = new FormLinkNullable<IArmorGetter>(template),
                ArmorRating = 42.0f
            };

            var result = transformer.Process(new UnChanged<Armor, IArmorGetter>(input));
            result.Should().BeOfType<UnChanged<Armor, IArmorGetter>>();
            result.Record().Equals(input).Should().BeTrue();
        }

    }
}