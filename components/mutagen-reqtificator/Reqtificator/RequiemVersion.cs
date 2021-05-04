namespace Reqtificator
{
    public record RequiemVersion(int Major, int Minor, int Patch, string Title)
    {
        public override string ToString()
        {
            return $"Requiem {Major}.{Minor}.{Patch} - \"{Title}\"";
        }

        public string ShortVersion()
        {
            return $"{Major}.{Minor}.{Patch}";
        }

        public int AsNumeric()
        {
            return Major * 10000 + Minor * 100 + Patch;
        }
    }
}