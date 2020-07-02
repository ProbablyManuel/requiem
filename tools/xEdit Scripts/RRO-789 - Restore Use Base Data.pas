{
  Restore Use Base Data flag on winning overrides.
}

unit RestoreUseBaseData;

var
  newPlugin: IInterface;


function Initialize: Integer;
var
  pluginName: String;
begin
  InputQuery('Enter', 'Plugin Name (without extenion)', pluginName);
  newPlugin := AddNewFileName(pluginName + '.esp');
  // Screw this atrocious API, let's just add the masters manually.
  AddMasterIfMissing(newPlugin, 'Skyrim.esm');
  AddMasterIfMissing(newPlugin, 'Update.esm');
  AddMasterIfMissing(newPlugin, 'Dawnguard.esm');
  AddMasterIfMissing(newPlugin, 'HearthFires.esm');
  AddMasterIfMissing(newPlugin, 'Dragonborn.esm');
  AddMasterIfMissing(newPlugin, 'Unofficial Skyrim Legendary Edition Patch.esp');
  AddMasterIfMissing(newPlugin, 'Requiem.esp');
end;


function Process(e: IInterface): Integer;
var
  copy: IInterface;
begin
  if not SameText(Signature(e), 'NPC_') or not IsWinningOverride(e) then
    Exit;
  if not GetElementNativeValues(e, 'ACBS\Template Flags\Use Base Data') then begin
    if GetElementNativeValues(MasterOrSelf(e), 'ACBS\Template Flags\Use Base Data') then begin
      copy := wbCopyElementToFile(e, newPlugin, False, True);
      SetElementNativeValues(copy, 'ACBS\Template Flags\Use Base Data', True);
    end;
  end;
end;


function Finalize: Integer;
begin

end;

end.

