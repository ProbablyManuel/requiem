unit NameProjectile;

uses REQ_Util;

var
  re_ignore, re_ammo: TPerlRegEx;


function Initialize: Integer;
begin
  re_ignore := TPerlRegEx.Create;
  re_ignore.RegEx := '^[^_]+_(DEPRECATED|LEGACY|NULL)_(.+)$';

  re_ammo := TPerlRegEx.Create;
  re_ammo.RegEx := '^[^_]+(?:_Ench)?_(Arrow|Bolt)_([^_]+)(?:_([^_]+))?$';
end;

function Process(e: IInterface): Integer;
var
  ammo: IInterface;
  newEditorId, arrowName: String;
begin
  if Signature(e) <> 'PROJ' then Exit;

  if GetElementEditValues(e, 'DATA - Data\Type') <> 'Arrow' then Exit;

  re_ignore.Subject := EditorID(e);
  if re_ignore.Match then Exit;

  ammo := GetSingleReferencedByRegex(e, 'AMMO', re_ammo);
  if not Assigned(ammo) then begin
    AddMessage(Name(e) + ' belongs to zero or multiple ammo records');
    Exit;
  end;

  newEditorId := StringReplace(EditorID(ammo), '_', '_Projectile_', nil);
  SetEditorID(e, newEditorId);
  arrowName := GetElementEditValues(ammo, 'FULL - Name');
  if GetElementNativeValues(e, 'FULL - Name') <> arrowName then
    SetElementNativeValues(e, 'FULL - Name', arrowName);
end;

function Finalize: Integer;
begin
  re_ignore.Free;
  re_ammo.Free;
end;

end.
