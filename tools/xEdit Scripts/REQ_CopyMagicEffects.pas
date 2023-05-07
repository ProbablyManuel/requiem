unit CopyMagicEffects;

uses REQ_Util;

var
  plugin: IInterface;


function Initialize: Integer;
var
  pluginName: String;
begin
  InputQuery('Enter', 'Filename without extension', pluginName);
  pluginName := pluginName + '.esp';
  plugin := CreateOrGetFile(pluginName);
  AddAllMastersToFile(plugin);
end;


function Process(e: IInterface): Integer;
var
  i: Integer;
  effects, eli, magicEffect: IInterface;
begin
  if Signature(e) <> 'SPEL' then
    Exit;
  effects := ElementByPath(e, 'Effects');
  for i := 0 to Pred(ElementCount(e)) do begin
    eli := ElementByIndex(effects, i);
    magicEffect := WinningOverride(LinksTo(ElementByPath(eli, 'EFID - Base Effect')));
    if not Equals(GetFile(magicEffect), plugin) then
      wbCopyElementToFile(magicEffect, plugin, False, True);
  end;
end;


function Finalize: Integer;
begin
  CleanMasters(plugin);
end;

end.
