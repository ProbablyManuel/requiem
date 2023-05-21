using Mutagen.Bethesda.Plugins;

namespace Reqtificator.Exceptions
{
    internal interface IDeprecationWarning
    {

    }

    internal record ReqTagPrefixDeprecationWarning(ModKey Origin) : IDeprecationWarning;
}