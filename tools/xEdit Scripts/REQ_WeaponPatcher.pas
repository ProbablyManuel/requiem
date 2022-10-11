unit WeaponPatcher;

var
  weapons: TStringList;
  re_ignore, re_weapon, re_weapon_artifact: TPerlRegEx;


function Initialize: Integer;
begin
  weapons := TStringList.Create;
  weapons.LoadFromFile('Edit Scripts\REQ_WeaponPatcher.txt');

  re_ignore := TPerlRegEx.Create;
  re_ignore.RegEx := '^[^_]+_(Creature|DEPRECATED|NULL|Special|Staff)_(.+)$';

  re_weapon := TPerlRegEx.Create;
  re_weapon.RegEx := '^[^_]+(?:_(?:Ench|NP|Var))?_(Weapon_([^_]+)_([^_]+))(?:_.+)?$';

  re_weapon_artifact := TPerlRegEx.Create;
  re_weapon_artifact.RegEx := '^[^_]+(?:_NP)?_(Artifact_.+)$';
end;

function Process(e: IInterface): Integer;
var
  weaponSet, weaponType, key: String;
  stats: TStringList;
begin
  if Signature(e) <> 'WEAP' then Exit;
  re_ignore.Subject := EditorID(e);
  if re_ignore.Match then Exit;

  re_weapon.Subject := EditorID(e);
  re_weapon_artifact.Subject := EditorID(e);
  if re_weapon.Match then begin
    key := re_weapon.Groups[1];
    weaponSet := re_weapon.Groups[2];
    weaponType := re_weapon.Groups[3];
  end
  else if re_weapon_artifact.Match then
    key := re_weapon_artifact.Groups[1]
  else begin
    AddMessage('EditorID ' + EditorID(e) + ' is invalid');
    Exit;
  end;

  stats := TStringList.Create;
  stats.DelimitedText := weapons.Values[key];
  if stats.Count = 0 then begin
    if (weaponType <> 'Bow') and (weaponType <> 'Crossbow') then
      AddMessage('EditorID ' + EditorID(e) + ' is not recognized');
    Exit;
  end;
  SetElementEditValues(e, 'DATA - Game Data\Damage', stats[0]);
  SetElementEditValues(e, 'DATA - Game Data\Weight', stats[1]);
  SetElementEditValues(e, 'DATA - Game Data\Value', stats[2]);
  SetElementEditValues(e, 'DNAM - Data\Speed', stats[3]);
  SetElementEditValues(e, 'DNAM - Data\Reach', stats[4]);
  SetElementEditValues(e, 'DNAM - Data\Stagger', stats[5]);
  SetElementEditValues(e, 'CRDT - Critical Data\Damage', stats[6]);
  stats.Free;
end;

function Finalize: Integer;
begin
  weapons.Free;
  re_ignore.Free;
  re_weapon.Free;
  re_weapon_artifact.Free;
end;

end.