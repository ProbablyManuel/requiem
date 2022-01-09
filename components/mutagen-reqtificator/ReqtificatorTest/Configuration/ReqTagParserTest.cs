using System.Collections.Generic;
using System.Collections.Immutable;
using FluentAssertions;
using Moq;
using Mutagen.Bethesda.Plugins;
using Mutagen.Bethesda.Plugins.Order;
using Mutagen.Bethesda.Skyrim;
using Reqtificator.Configuration;
using Reqtificator.Events;
using Reqtificator.Exceptions;
using Xunit;

namespace ReqtificatorTest.Configuration
{
    public class ReqTagParserTest
    {
        [Fact]
        public void Should_extract_the_proper_tags_from_plugin_descriptions()
        {
            var mod1 = new SkyrimMod("Epic.esm", SkyrimRelease.SkyrimSE)
            {
                ModHeader = { Description = "an epic adventure <<REQ:UNROLL>>" }
            };
            var mod2 = new SkyrimMod("Mutations.esm", SkyrimRelease.SkyrimSE)
            {
                ModHeader = { Description = "a mutated adventure <<REQ:MUTATE; REQ:TEMPER>>" }
            };
            var mod3 = new SkyrimMod("Everything.esm", SkyrimRelease.SkyrimSE)
            {
                ModHeader = { Description = "a huge mod <<REQ:MUTATE; REQ:TEMPER;REQ:UNROLL>>" }
            };
            var loadOrder = new LoadOrder<IModListing<ISkyrimModGetter>>
            {
                new ModListing<ISkyrimModGetter>(mod1),
                new ModListing<ISkyrimModGetter>(mod2),
                new ModListing<ISkyrimModGetter>(mod3)
            };
            var internalEvents = new Mock<IInternalEvents>(MockBehavior.Strict);

            var tags = new ReqTagParser(internalEvents.Object).ParseTagsFromModHeaders(loadOrder);
            tags.Should().BeEquivalentTo(new Dictionary<ModKey, IImmutableSet<ReqTags>>
            {
                {mod1.ModKey, ImmutableHashSet.Create(ReqTags.CompactLeveledLists)},
                {mod2.ModKey, ImmutableHashSet.Create(ReqTags.ActorVariations).Add(ReqTags.TemperedItems)},
                {mod3.ModKey, ImmutableHashSet.Create(ReqTags.ActorVariations).Add(ReqTags.TemperedItems).Add(ReqTags.CompactLeveledLists)}
            });
        }

        [Fact]
        public void Should_handle_plugins_without_tags_correctly()
        {
            var mod1 = new SkyrimMod("Epic.esm", SkyrimRelease.SkyrimSE)
            {
                ModHeader = { Description = "an epic adventure with no tags" }
            };
            var loadOrder = new LoadOrder<IModListing<ISkyrimModGetter>> { new ModListing<ISkyrimModGetter>(mod1) };
            var internalEvents = new Mock<IInternalEvents>(MockBehavior.Strict);

            var tags = new ReqTagParser(internalEvents.Object).ParseTagsFromModHeaders(loadOrder);
            tags.Should().BeEquivalentTo(new Dictionary<ModKey, IImmutableSet<ReqTags>>
            {
                {mod1.ModKey, ImmutableHashSet<ReqTags>.Empty}
            });
        }

        [Fact]
        public void Should_parse_plugins_with_legacy_ReqTags_and_emit_a_deprecation_warning()
        {
            var mod1 = new SkyrimMod("Legacy.esm", SkyrimRelease.SkyrimSE)
            {
                ModHeader = { Description = "a legacy adventure <<REQ:\"Legacy\"; REQ:UNROLL>>" }
            };
            var loadOrder = new LoadOrder<IModListing<ISkyrimModGetter>> { new ModListing<ISkyrimModGetter>(mod1) };
            var internalEvents = new Mock<IInternalEvents>(MockBehavior.Strict);

            internalEvents.Setup(e => e.ReportDeprecationWarning(new ReqTagPrefixDeprecationWarning(mod1.ModKey)));

            var tags = new ReqTagParser(internalEvents.Object).ParseTagsFromModHeaders(loadOrder);
            tags.Should().BeEquivalentTo(new Dictionary<ModKey, IImmutableSet<ReqTags>>
            {
                {mod1.ModKey, ImmutableHashSet.Create(ReqTags.CompactLeveledLists)}
            });
            internalEvents.VerifyAll();
        }

        [Fact]
        public void Should_ignore_broken_ReqTag_declarations()
        {
            var mod1 = new SkyrimMod("Legacy.esm", SkyrimRelease.SkyrimSE)
            {
                ModHeader = { Description = "a legacy adventure <<ROQ:MUTATE; REQ:UNROLL>>" }
            };
            var loadOrder = new LoadOrder<IModListing<ISkyrimModGetter>> { new ModListing<ISkyrimModGetter>(mod1) };
            var internalEvents = new Mock<IInternalEvents>(MockBehavior.Strict);

            var tags = new ReqTagParser(internalEvents.Object).ParseTagsFromModHeaders(loadOrder);
            tags.Should().BeEquivalentTo(new Dictionary<ModKey, IImmutableSet<ReqTags>>
            {
                {mod1.ModKey, ImmutableHashSet<ReqTags>.Empty}
            });
        }
    }
}