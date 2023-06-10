unit SetPersistentLocation;


function Process(e: IInterface): Integer;
var
  cell, location, persistentLocation: IInterface;
  locationNativeFormId: Cardinal;
begin
  if Signature(e) <> 'ACHR' then Exit;

  cell := WinningOverride(LinksTo(ElementByPath(e, 'Cell')));
  location := WinningOverride(LinksTo(ElementByPath(cell, 'XLCN - Location')));
  if not Assigned(location) then begin
    AddMessage(Name(e) + ' has no location');
    Exit;
  end;
  locationNativeFormId := LoadOrderFormIDtoFileFormID(GetFile(e), GetLoadOrderFormID(location));

  persistentLocation := ElementByPath(e, 'XLCN - Persistent Location');
  if not Assigned(persistentLocation) then
    persistentLocation := Add(e, 'XLCN', False);
  if GetNativeValue(persistentLocation) <> locationNativeFormId then
    SetNativeValue(persistentLocation, locationNativeFormId);

  if not Equals(GetFile(location), GetFile(e)) then
      wbCopyElementToFile(location, GetFile(e), False, True);
end;

end.
