unit CapLockLevel;

uses REQ_Util;

var
  plugin: IInterface;
  lockLevelCap: String;


function Initialize: Integer;
var
  pluginName, sLockLevelCap: String;
begin
  InputQuery('Enter', 'Filename without extension', pluginName);
  pluginName := pluginName + '.esp';
  plugin := CreateOrGetFile(pluginName);
  AddAllMastersToFile(plugin);

  InputQuery('Enter', 'Lock Level', sLockLevelCap);
  lockLevelCap := StrToInt(sLockLevelCap);
end;


function Process(e: IInterface): Integer;
var
  i: Integer;
  ref: IInterface;
  lockLevel: Integer;
  referencedBy: TList;
begin
  if Signature(e) <> 'CONT' then
    Exit;
  referencedBy := TList.Create;
  referencedBy.Capacity := ReferencedByCount(e);
  for i := 0 to Pred(ReferencedByCount(e)) do
    referencedBy.Add(ReferencedByIndex(e, i));
  for i := 0 to Pred(referencedBy.Count()) do begin
    ref := ObjectToElement(referencedBy.Items[i]);
    if not IsWinningOverride(ref) then
      Continue;
    lockLevel := GetElementNativeValues(ref, 'XLOC - Lock Data\Level');
    if Assigned(lockLevel) and (lockLevel > lockLevelCap) then begin
      if not Equals(GetFile(ref), plugin) then
        ref := wbCopyElementToFile(ref, plugin, False, True);
      SetElementNativeValues(ref, 'XLOC - Lock Data\Level', lockLevelCap);
    end;
  end;
  referencedBy.Free;
end;

function Finalize: Integer;
begin
  CleanMasters(plugin);
end;

end.
