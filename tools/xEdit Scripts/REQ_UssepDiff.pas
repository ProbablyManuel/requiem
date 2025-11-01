{
  Hide all overridden records except the previous winning override.
}
unit UssepDiff;


function Initialize: Integer;
begin
  if not IsFileLoadedAtIndex('Skyrim.esm', 0) then
    Result := 100
  else if not IsFileLoadedAtIndex('SkyrimSE.exe', 1) then
    Result := 101
  else if not IsFileLoadedAtIndex('Update.esm', 2) then
    Result := 102
  else if not IsFileLoadedAtIndex('Dawnguard.esm', 3) then
    Result := 103
  else if not IsFileLoadedAtIndex('HearthFires.esm', 4) then
    Result := 104
  else if not IsFileLoadedAtIndex('Dragonborn.esm', 5) then
    Result := 105
  else if not IsFileLoadedAtIndex('ccbgssse001-fish.esm', 6) then
    Result := 106
  else if not IsFileLoadedAtIndex('ccqdrsse001-survivalmode.esl', 7) then
    Result := 107
  else if not IsFileLoadedAtIndex('ccbgssse037-curios.esl', 8) then
    Result := 108
  else if not IsFileLoadedAtIndex('ccbgssse025-advdsgs.esm', 9) then
    Result := 109
  else if not IsFileLoadedAtIndex('_ResourcePack.esl', 10) then
    Result := 110
  else if not IsFileLoadedAtIndex('unofficial skyrim special edition patch.esp', 11) then
    Result := 110
  else if not IsFileLoadedAtIndex('unofficial skyrim special edition patch.esu', 12) then
    Result := 112
  else if not IsFileLoadedAtIndex('Requiem.esp', 13) then
    Result := 113;
end;


function Process(e: IInterface): Integer;
var
  m, o: IInterface;
  i: Integer;
begin
  if GetFileName(GetFile(e)) <> 'Requiem.esp' then begin
    Result := -1;
    Exit;
  end;
  m := MasterOrSelf(e);
  if OverrideCount(m) < 2 then
    o := nil
  else
    o := OverrideByIndex(m, OverrideCount(m) - 2);
  if not SameText(GetFileName(GetFile(o)), 'unofficial skyrim special edition patch.esu') then begin
    // Not touched by USSEP update -> Hide altogether
    SetElementState(m, esHidden);
    for i := 0 to OverrideCount(m) - 1 do
      SetElementState(OverrideByIndex(m, i), esHidden);
  end
  else if GetIsDeleted(o) then begin
    // Removed by USSEP update -> Show Requiem + new USSEP + old USSEP + prev override
    if OverrideCount(m) > 3 then begin
      SetElementState(m, esHidden);
      for i := 0 to OverrideCount(m) - 5 do
        SetElementState(OverrideByIndex(m, i), esHidden);
    end;
  end
  else begin
    // Added by USSEP update -> Show Requiem + new USSEP + prev override
    // or
    // Modified by new USSEP update -> Requiem + new USSEP + old USSEP
    if OverrideCount(m) > 2 then begin
      SetElementState(m, esHidden);
      for i := 0 to OverrideCount(m) - 4 do
        SetElementState(OverrideByIndex(m, i), esHidden);
    end;
  end;
end;


function Finalize: Integer;
begin
  FilterScripted := True;

  ApplyFilter;
end;

function IsFileLoadedAtIndex(s: String; i: Integer): Boolean;
begin
  Result := SameText(GetFileName(FileByIndex(i)), s);
end;

function Filter(e: IInterface): Boolean;
begin
  Result := not SameText(GetFileName(GetFile(e)), 'Requiem.esp') or not GetElementState(e, esHidden);
end;

end.
