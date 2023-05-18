unit GravityPullPatcher;

uses REQ_Util;

var
  re_ignore, re_ammo, re_projectile: TPerlRegEx;


function Initialize: Integer;
begin
  re_ignore := TPerlRegEx.Create;
  re_ignore.RegEx := '^[^_]+_(DEPRECATED|LEGACY|NULL)_(.+)$';

  re_ammo := TPerlRegEx.Create;
  re_ammo.RegEx := '^[^_]+(?:_Ench)?_(Arrow|Bolt)_([^_]+)(?:_([^_]+))?$';

  re_projectile := TPerlRegEx.Create;
  re_projectile.RegEx := '^[^_]+_Projectile_(?:_Ench)?_(Arrow|Bolt)_([^_]+)(?:_([^_]+))?$';
end;

function Process(e: IInterface): Integer;
var
  ammo: IInterface;
  gravity: Float;
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

  if HasKeyword(ammo, 'REQ_AmmoWeight_Massive') then
    gravity := 1.0
  else if HasKeyword(ammo, 'REQ_AmmoWeight_Heavy') then
    gravity := 0.65
  else if HasKeyword(ammo, 'REQ_AmmoWeight_Medium') then
    gravity := 0.35
  else if HasKeyword(ammo, 'REQ_AmmoWeight_Light') then
    gravity := 0.15
  else if HasKeyword(ammo, 'REQ_AmmoWeight_None') then
    gravity := 0.0
  else begin
    AddMessage(Name(e) + ' has no ammo weight keyword');
    Exit;
  end;

  if GetElementNativeValues(e, 'DATA - Data\Gravity') <> gravity then
    SetElementNativeValues(e, 'DATA - Data\Gravity', gravity);
end;

function Finalize: Integer;
begin
  re_ignore.Free;
  re_ammo.Free;
end;

end.
