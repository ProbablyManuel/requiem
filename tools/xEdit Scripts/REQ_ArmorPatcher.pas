unit ArmorPatcher;

uses REQ_Util;

var
  armors: TStringList;
  re_ignore, re_armor, re_armor_artifact: TPerlRegEx;


function Initialize: Integer;
begin
  armors := TStringList.Create;
  armors.LoadFromFile('Edit Scripts\REQ_ArmorPatcher.txt');

  re_ignore := TPerlRegEx.Create;
  re_ignore.RegEx := '^[^_]+_(DEPRECATED|LEGACY|NULL|(?:(?:Ench|NP|Var)_)?(?:Amulet|Circlet|Cloth|Creature|Ring|Saddle))_(.+)$';

  re_armor := TPerlRegEx.Create;
  re_armor.RegEx := '^[^_]+(?:_(Ench|NP|Var))?_((Heavy|Light)_([^_]+)_([^_]+))(?:_([^_]+))?(?:_(.+))?$';

  re_armor_artifact := TPerlRegEx.Create;
  re_armor_artifact.RegEx := '^[^_]+(?:_NP)?_(Artifact_.+)$';
end;

function Process(e: IInterface): Integer;
var
  key, modifier: String;
  stats: TStringList;
begin
  if Signature(e) <> 'ARMO' then Exit;
  re_ignore.Subject := EditorID(e);
  if re_ignore.Match then Exit;

  re_armor.Subject := EditorID(e);
  re_armor_artifact.Subject := EditorID(e);
  if re_armor.Match then begin
    if (re_armor.Groups[1] = 'Ench') and not Assigned(re_armor.Groups[7]) then
      modifier := ''
    else
      modifier := re_armor.Groups[6];
    key := re_armor.Groups[2] + '_' + modifier;
    if armors.IndexOfName(key) = -1 then
      key := re_armor.Groups[2];
  end
  else if re_armor_artifact.Match then
    key := re_armor_artifact.Groups[1]
  else begin
    AddMessage('EditorID ' + EditorID(e) + ' is invalid');
    Exit;
  end;

  stats := TStringList.Create;
  stats.StrictDelimiter := True;
  stats.DelimitedText := armors.Values[key];
  if stats.Count = 0 then begin
    AddMessage('EditorID ' + EditorID(e) + ' is not recognized');
    Exit;
  end;
  SetElementEditValues(e, 'DNAM - Armor Rating', stats[0]);
  SetElementEditValues(e, 'DATA - Data\Weight', stats[1]);
  SetElementEditValues(e, 'DATA - Data\Value', stats[2]);
  if stats[3] = 'Cold' then begin
    AddKeyword(e, 'Survival_ArmorCold');
    RemoveKeyword(e, 'Survival_ArmorWarm');
  end
  else if stats[3] = 'None' then begin
    RemoveKeyword(e, 'Survival_ArmorCold');
    RemoveKeyword(e, 'Survival_ArmorWarm');
  end
  else if stats[3] = 'Warm' then begin
    AddKeyword(e, 'Survival_ArmorWarm');
    RemoveKeyword(e, 'Survival_ArmorCold');
  end
  else
    AddMessage('Unknown Survival keyword ' + stats[3]);
  stats.Free;
end;

function Finalize: Integer;
begin
  armors.Free;
  re_ignore.Free;
  re_armor.Free;
  re_armor_artifact.Free;
end;

end.
