using System;
using System.Collections.Immutable;
using System.Windows.Documents;

namespace Reqtificator
{
    public static class MessageFactory
    {
        private static class Uris
        {
            public static readonly Uri ServiceCentral = new("https://requiem.atlassian.net/servicedesk/customer/portal/1");
        }

        internal static FlowDocument BuildMessage(PatchFinished patchFinished)
        {
            switch (patchFinished.Status)
            {
                case PatchStatus.Success: { return CreateSuccessMessage(); }
                case PatchStatus.GeneralError: { return CreateGeneralErrorMessage(patchFinished.Arguments); }
                case PatchStatus.MissingRequiem: { return CreateMissingRequiemMessage(); }
                default: { throw new NotImplementedException(); }
            }
        }

        private static FlowDocument CreateGeneralErrorMessage(ImmutableList<string> arguments)
        {
            var message = CreateMessage();

            _ = message.P("There was an exception thrown during program execution!");
            _ = message.P("Check the ").Bold("Reqtificator.log").Norm(" file in your ").Bold("data")
                .Norm(" folder. If you cannot make any sense out of it, you can upload it to the ")
                .Link("Requiem Service Central", Uris.ServiceCentral)
                .Norm(" and ask for help there.");

            _ = message.Bold("A note for Mod Organizer users:").Norm(" The log file is created in the special pseudo - mod named \"Overwrite\"");

            if (!arguments.IsEmpty) { _ = message.P("Thrown Exception:\r\n" + arguments[0]); }

            return message;
        }

        private static FlowDocument CreateSuccessMessage()
        {
            var message = CreateMessage();

            _ = message.P("Your ").Bold("Requiem for the Indifferent.esp").Norm(" has been created successfully.");
            _ = message.P("This patch depends heavily on the content of your load order. Please rebuild the patch when you:");
            _ = message.Ul()
                .Li("install a new mod")
                .Li("remove a mod")
                .Li("update a mod to a newer version")
                .Li("change the order of mods");
            _ = message.P("Enjoy your Requiem!");

            return message;
        }

        private static FlowDocument CreateMissingRequiemMessage()
        {
            var message = CreateMessage();
            _ = message.P("We couldn't find Requiem.esp. Please check that you're running the Reqtificator in the ")
                .Bold("Skyrim/Data")
                .Norm(" folder where Mod Organizer or Vortex unpacked Requiem.");
            return message;
        }
        private static FlowDocument CreateMessage()
        {
            return new FlowDocument()
            {
                FontSize = 14
            };
        }
    }
}