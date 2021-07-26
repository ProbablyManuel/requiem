using Mutagen.Bethesda.Plugins;

namespace Reqtificator.Exceptions
{
    interface IDeprecationWarning
    {

    }

    record ReqTagPrefixDeprecationWarning(ModKey Origin) : IDeprecationWarning;
}