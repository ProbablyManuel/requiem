namespace Reqtificator
{
    public record RequiemVersion(int Major, int Minor, int Patch, string Title)
    {
        public RequiemVersion(int version) : this(version / 10000, version % 10000 / 100, version % 100, "")
        {

        }

        public RequiemVersion(int version, string name) : this(version / 10000, version % 10000 / 100, version % 100, name)
        {

        }

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