unit AddPrefixToAllEntriesInLeveledList;

var
  sPrefix: String;


function Initialize: Integer;
begin
  InputQuery('Enter', 'Prefix', sPrefix);
end;


function Process(e: IInterface): Integer;
var
  i: Integer;
  Entries, Entry: IInterface;
begin
  if (Signature(e) = 'LVLI') or (Signature(e) = 'LVLN') or (Signature(e) = 'LVSP') then begin
    Entries := ElementByPath(e, 'Leveled List Entries');
    for i := 0 to ElementCount(Entries) - 1 do begin
        Entry := ElementByPath(ElementByIndex(Entries, i), 'LVLO - Base Data\Reference');
        Entry := WinningOverride(LinksTo(Entry));
        if not Equals(GetFile(Entry), GetFile(e)) then
          Entry := wbCopyElementToFile(Entry, GetFile(e), False, True);
        SetEditorID(Entry, sPrefix + EditorID(Entry));
    end;
  end;
end;


function Finalize: Integer;
begin

end;

end.