unit WeaponPatcher;

var
  slWeapons, slStats: TStringList;
  re_weapon, re_ignore: TPerlRegEx;


function Initialize: Integer;
begin
  slWeapons := TStringList.Create;
  slWeapons.LoadFromFile('Edit Scripts\REQ_WeaponPatcher.txt');

  slStats := TStringList.Create;

  re_weapon := TPerlRegEx.Create;
  re_weapon.RegEx := '^[^_]+(?:_(?:Ench|NP|Var))?_([^_]+)_([^_]+)(?:_(.+))?$';

  re_ignore := TPerlRegEx.Create;
  re_ignore.RegEx := '^[^_]+_(Creature|DEPRECATED|NULL|Special|Staff)_(.+)$';
end;

function Process(e: IInterface): Integer;
var
  weaponSet, weaponType, weaponModifier: String;
begin
  if Signature(e) <> 'WEAP' then
    Exit;
  re_ignore.Subject := EditorID(e);
  if re_ignore.Match then
    Exit;
  re_weapon.Subject := EditorID(e);
  if not re_weapon.Match then begin
    AddMessage('EditorID ' + EditorID(e) + ' is invalid');
    Exit;
  end;
  weaponSet := re_weapon.Groups[1];
  weaponType := re_weapon.Groups[2];
  weaponModifier := re_weapon.Groups[3];
  if weaponSet = 'Artifact' then
    slStats.DelimitedText := slWeapons.Values[weaponSet + '_' + weaponType + '_' + weaponModifier]
  else
    slStats.DelimitedText := slWeapons.Values[weaponSet + '_' + weaponType];
  if slStats.Count = 0 then begin
    if (weaponType <> 'Bow') and (weaponType <> 'Crossbow') then
      AddMessage('EditorID ' + EditorID(e) + ' is not recognized');
    Exit;
  end;
  SetElementEditValues(e, 'DATA - Game Data\Damage', slStats[0]);
  SetElementEditValues(e, 'DATA - Game Data\Weight', slStats[1]);
  SetElementEditValues(e, 'DATA - Game Data\Value', slStats[2]);
  SetElementEditValues(e, 'DNAM - Data\Speed', slStats[3]);
  SetElementEditValues(e, 'DNAM - Data\Reach', slStats[4]);
  SetElementEditValues(e, 'DNAM - Data\Stagger', slStats[5]);
  SetElementEditValues(e, 'CRDT - Critical Data\Damage', slStats[6]);
end;

function Finalize: Integer;
begin
  slWeapons.Free;
  slStats.Free;
end;

end.